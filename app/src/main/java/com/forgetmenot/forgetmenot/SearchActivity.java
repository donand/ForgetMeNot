package com.forgetmenot.forgetmenot;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import com.forgetmenot.forgetmenot.network.GetRisultatiRicerca;
import com.forgetmenot.forgetmenot.network.TaskCallbackGetRisultatiRicerca;
import com.forgetmenot.forgetmenot.utils.CircleTransform;
import com.squareup.picasso.*;


public class SearchActivity extends Activity implements TaskCallbackGetRisultatiRicerca {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            showResults(query);
        }
    }

    private void showResults(String query) {
        // Query your data set and show results
        try {
            String ricerca = "http://forgetmenot.ddns.net/ForgetMeNot/GetRisultatiRicerca?nome=" + query;
            GetRisultatiRicerca task = new GetRisultatiRicerca(ricerca, this, this.getApplicationContext());
            task.execute();
        } catch (Exception e) {
        }

    }
    public void done(String result){
        try{
            TextView ris=(TextView)findViewById(R.id.risultato);
            if(result.equals(null) || result.equals("")){
                ris.setText("Nessuna pianta corrisponde alla tua ricerca.");
            }
            else{
                ris.setText("Pianta trovata: ");
            }
            JSONObject o=new JSONObject(result);
            String nomePianta=o.getString("nome");
            int id=Integer.parseInt(o.getString("id"));
            String foto=o.getString("immagine");

            /*String nomeR="Nome";
            String immagineR="http://www.drogbaster.it/immagini-3d/album/slides/immagini-tridimensionali%20(5).jpg";*/
            ImageView immagine=(ImageView)findViewById(R.id.immaginePianta);

            TextView nome=(TextView)findViewById(R.id.nomePianta);
            nome.setText(nomePianta);
            Picasso.with(getApplicationContext())
                    .load(foto).transform(new CircleTransform()).into(immagine);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

}
