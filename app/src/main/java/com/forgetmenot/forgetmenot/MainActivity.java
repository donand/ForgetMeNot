package com.forgetmenot.forgetmenot;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends ActionBarActivity implements TaskCallbackElenco {
    String prova ;
    JSONArray elencoPiante=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //chiamo il task che prende l'elenco di piante dell'utente dal server. ora provo con un utente a caso
        try {
            String ricerca = "http://forgetmenot.ddns.net/ForgetMeNot/ElencoPianteUtente?utente=1";
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

        return super.onOptionsItemSelected(item);
    }
    public void done(String result){
        try{
            TextView nome=(TextView)findViewById(R.id.nomePianta);
            if(result.equals(null) || result.equals("")){
                nome.setVisibility(View.VISIBLE);
                nome.setText("Non hai nessuna pianta.");
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
        for(int i=0; i<result.length(); i++){
            try{
                JSONObject o=result.getJSONObject(i);
                int livelloAcqua=o.getInt("livelloAcqua");
                int livelloFertilizzante=o.getInt("livelloConcimazione");
                if(livelloAcqua<=5 || livelloFertilizzante<=5){
                    bad=true;
                }
            }
            catch (JSONException e){
                e.printStackTrace();
            }
        }
        TextView messaggioIniziale=(TextView)findViewById(R.id.messaggio);

        if(bad){
            messaggioIniziale.setText("Attenzione. Qualcuna delle tue piante ha bisogno di cure.");
            messaggioIniziale.setTextColor(Color.RED);
            messaggioIniziale.setGravity(Gravity.CENTER);
        }
        else{
            messaggioIniziale.setText("Le tue piante stanno bene.");
            messaggioIniziale.setTextColor(Color.GREEN);
            messaggioIniziale.setGravity(Gravity.CENTER);
        }
    }

}
