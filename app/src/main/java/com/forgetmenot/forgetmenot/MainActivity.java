package com.forgetmenot.forgetmenot;

import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.MatrixCursor;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;

import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.forgetmenot.forgetmenot.network.GetElencoPianteUtente;
import com.forgetmenot.forgetmenot.network.TaskCallbackElenco;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;


public class MainActivity extends ActionBarActivity implements TaskCallbackElenco, TaskCallbackElencoTipi, View.OnClickListener {
    String prova ;

    //piante utente
    JSONArray elencoPiante=null;
    //piante totali
    JSONArray elencoPianteGenerali=null;
    ImageButton fabUpload;
    SharedPreferences pref;

    
    private Menu menu;

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
        this.menu=menu;

        try {
            String ricerca = "http://forgetmenot.ddns.net/ForgetMeNot/GetElencoTipiPiante";
            GetElencoTipiPiante task = new GetElencoTipiPiante(ricerca, this, this.getApplicationContext());
            task.execute();
        } catch (Exception e) {
        }

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        android.widget.SearchView searchView = (android.widget.SearchView) (menu.findItem(R.id.search)).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String query) {
                //aspetto che il task completi il jsonarray
                while(elencoPianteGenerali==null){
                }

                loadHistory(query);

                return true;

            }

            @Override
            public boolean onQueryTextSubmit(String a) {
                return true;
            }

        });

        return true;
    }
    // History
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void loadHistory(String query) {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

            // Cursor
            String[] columns = new String[] { "_id", "text" };
            Object[] temp = new Object[] { 0, "default" };

            MatrixCursor cursor = new MatrixCursor(columns);
            JSONArray items=new JSONArray();
            for(int i = 0; i < elencoPianteGenerali.length(); i++) {
                try {
                    temp[0] = i;
                    JSONObject o=(JSONObject)elencoPianteGenerali.get(i);
                    temp[1] = o.toString();

                    if (o.getString("nome").toLowerCase().startsWith(query.toLowerCase())){
                        System.out.println(o.toString());
                        items.put(o);
                        cursor.addRow(temp);
                    }

                }
                catch (JSONException e){
                    e.printStackTrace();
                }
            }

            // SearchView
            SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

            final android.widget.SearchView search = (android.widget.SearchView) menu.findItem(R.id.search).getActionView();

            search.setSuggestionsAdapter(new SearchAdapter(this, cursor, items));

        }

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

            //onSearchRequested();
            return true;
        }
        if(id==R.id.foto){
            Intent i = new Intent(MainActivity.this, VisualSearch.class);
            this.startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    //callback from getelencotipipiante
    public void done(String r, boolean inutile){
        try{
            elencoPianteGenerali=new JSONArray(r);
            System.out.println("FATTO");
        }
        catch(JSONException e){
            e.printStackTrace();
        }
    }

    //callback from getelencopianteutente
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
        else if(!bad && warning){
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
    //provamerge
}
