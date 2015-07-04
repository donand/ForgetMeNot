package com.forgetmenot.forgetmenot;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.forgetmenot.forgetmenot.network.GetElencoPianteUtente;
import com.forgetmenot.forgetmenot.network.TaskCallbackElenco;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends ActionBarActivity implements TaskCallbackElenco, View.OnClickListener {
    String prova ;
    JSONArray elencoPiante=null;
    ImageButton fabUpload;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pref=getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        //floating button
        fabUpload = (ImageButton)findViewById(R.id.fab);
        fabUpload.setVisibility(View.VISIBLE);
        fabUpload.bringToFront();
        fabUpload.setOnClickListener(this);

        String idUtente=pref.getString("idUtente", null);

        //chiamo il task che prende l'elenco di piante dell'utente dal server. ora provo con un utente a caso
        try {
            String ricerca = "http://forgetmenot.ddns.net/ForgetMeNot/ElencoPianteUtente?utente="+idUtente;
            GetElencoPianteUtente task = new GetElencoPianteUtente(ricerca, this, this.getApplicationContext());
            task.execute();
        } catch (Exception e) {
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) (menu.findItem(R.id.search)).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if(id == R.id.luce){
            Intent i = new Intent(MainActivity.this, VerificaLuce.class);
            this.startActivity(i);
        }

        if(id==R.id.search){
            onSearchRequested();
            return true;
        }
        if(id==R.id.foto){
            Intent i = new Intent(MainActivity.this, VisualSearch.class);
            this.startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }
    public void done(String result){
        try{
            TextView messaggioIniziale=(TextView)findViewById(R.id.messaggio);
            if(result==null || result.equals("")){
                messaggioIniziale.setVisibility(View.VISIBLE);
                messaggioIniziale.setText("Non hai nessuna pianta.");
            }
            elencoPiante=new JSONArray(result);

            impostaMessaggioIniziale(elencoPiante);

            //listview di activity_main
            ListView listaPiante=(ListView)findViewById(R.id.piante);
            CustomListPiante adapter = new CustomListPiante(MainActivity.this, elencoPiante);
            listaPiante.setAdapter(adapter);

            /*String nomeR="Nome";
            String immagineR="http://www.drogbaster.it/immagini-3d/album/slides/immagini-tridimensionali%20(5).jpg";*/
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private void impostaMessaggioIniziale(JSONArray result){
        boolean bad=false;
        boolean warning=false;
        for(int i=0; i<result.length(); i++){
            try{
                JSONObject o=result.getJSONObject(i);
                int livelloAcqua=o.getInt("livelloAcqua");
                int livelloFertilizzante=o.getInt("livelloConcimazione");
                if(livelloAcqua==0 || livelloFertilizzante==0){
                    bad=true;
                }
                else if(!bad && (livelloAcqua<=2 || livelloFertilizzante<=2)){
                    warning=true;
                }
            }
            catch (JSONException e){
                e.printStackTrace();
            }
        }
        TextView messaggioIniziale=(TextView)findViewById(R.id.messaggio);
        CardView cardMessaggioIniziale = (CardView) findViewById(R.id.cardview_messaggio);

        if(bad){
            messaggioIniziale.setText("Attenzione! Qualcuna delle tue piante ha bisogno di cure!");
            cardMessaggioIniziale.setCardBackgroundColor(getResources().getColor(R.color.material_red));

        }
        else if(warning){
            messaggioIniziale.setText("Attenzione! Qualcuna delle tue piante avrà presto bisogno di cure!");
            cardMessaggioIniziale.setCardBackgroundColor(getResources().getColor(R.color.material_yellow));
        }
        else {
            messaggioIniziale.setText("Le tue piante stanno bene.");
            cardMessaggioIniziale.setCardBackgroundColor(getResources().getColor(R.color.material_green));
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                Intent i = new Intent(this, AggiungiPianta.class);
                startActivity(i);
                break;
        }
    }
}
