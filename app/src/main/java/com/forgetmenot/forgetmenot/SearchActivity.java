package com.forgetmenot.forgetmenot;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.net.Uri;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import com.squareup.picasso.*;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;


public class SearchActivity extends Activity implements TaskCallbackGetRisultatiRicerca{

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
            TextView nome=(TextView)findViewById(R.id.nomePianta);
            if(result.equals(null) || result.equals("")){
                nome.setVisibility(View.VISIBLE);
                nome.setText("Nessuna pianta corrisponde alla tua ricerca.");
            }
            JSONObject o=new JSONObject(result);
            String nomePianta=o.getString("nome");
            int id=Integer.parseInt(o.getString("id"));
            String foto=o.getString("immagine");

            /*String nomeR="Nome";
            String immagineR="http://www.drogbaster.it/immagini-3d/album/slides/immagini-tridimensionali%20(5).jpg";*/
            ImageView immagine=(ImageView)findViewById(R.id.immaginePianta);
            immagine.setVisibility(View.VISIBLE);

            nome.setVisibility(View.VISIBLE);
            nome.setText(nomePianta);
            Picasso.with(getApplicationContext())
                    .load(foto).into(immagine);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

}
