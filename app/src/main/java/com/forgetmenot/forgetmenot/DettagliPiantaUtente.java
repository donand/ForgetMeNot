package com.forgetmenot.forgetmenot;

import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.forgetmenot.forgetmenot.utils.CircleTransform;

import org.json.JSONException;
import org.json.JSONObject;


public class DettagliPiantaUtente extends ActionBarActivity implements View.OnClickListener {
    public static final String TAG = "DettagliPianteUtente";
    public static final String URL_AGGIORNA_DATA_ACQUA = "http://forgetmenot.ddns.net/ForgetMeNot/AggiornaDataUltimaAcqua";
    public static final String URL_AGGIORNA_DATA_CONCIMAZIONE = "http://forgetmenot.ddns.net/ForgetMeNot/AggiornaDataUltimaConcimazione";
    public static final String URL_GET_DETTAGLI = "http://forgetmenot.ddns.net/ForgetMeNot/GetDettagliPiantaUser?id=";

    private ImageView mImmagine;
    private TextView mNomeUtente;
    private TextView mNomeGenerico;
    private TextView mIndirizzo;

    private ProgressBar mLivelloAcqua;
    private ProgressBar mLivelloFertilizzante;
    private ProgressBar mLivelloLuce;

    private TextView mDescrizioneAcqua;
    private TextView mDescrizioneFertilizzante;
    private TextView mDescrizioneLuce;

    private TextView mIntervalloAcqua;
    private TextView mIntervalloFertilizzante;

    private TextView mDataUltimaAcqua;
    private TextView mDataUltimoFertilizzante;

    private double lat, lon;
    private int idPossesso;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dettagli_pianta_utente);

        idPossesso = 1;

        mImmagine = (ImageView) findViewById(R.id.immagine);
        mNomeUtente = (TextView) findViewById(R.id.nome_utente);
        mNomeGenerico = (TextView) findViewById(R.id.nome_generico);
        mIndirizzo = (TextView) findViewById(R.id.indirizzo);

        mLivelloAcqua = (ProgressBar) findViewById(R.id.progress_bar_acqua);
        mLivelloFertilizzante = (ProgressBar) findViewById(R.id.progress_bar_fertilizzante);
        mLivelloLuce = (ProgressBar) findViewById(R.id.progress_bar_luce);

        mDescrizioneAcqua = (TextView) findViewById(R.id.descrizione_acqua);
        mDescrizioneFertilizzante = (TextView) findViewById(R.id.descrizione_fertilizzante);
        mDescrizioneLuce = (TextView) findViewById(R.id.descrizione_luce);

        //TODO: fare intervallo acqua
        //TODO: fare intervallo fertilizzante

        //TODO: fare data ultima acqua
        //TODO: fare data ultimo fertilizzante

        ((Button) findViewById(R.id.aggiorna_data_acqua)).setOnClickListener(this);
        ((Button) findViewById(R.id.aggiorna_data_concimazione)).setOnClickListener(this);
        ((Button) findViewById(R.id.verifica_luce)).setOnClickListener(this);
        ((Button) findViewById(R.id.info_pianta)).setOnClickListener(this);

        Picasso.with(this).load("http://forgetmenot.ddns.net/ForgetMeNot/Immagini/narciso.jpg").into(mImmagine);

        downloadDettagli(URL_GET_DETTAGLI + idPossesso);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dettagli_pianta_utente, menu);
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

        return super.onOptionsItemSelected(item);
    }

    private void downloadDettagli(String url) {
        RequestQueue queue = Volley.newRequestQueue(this);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v(TAG, "Download riuscito!");
                        setResult(response);
                    }
                }, new Response.ErrorListener() {
            @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.v(TAG, "Errore nel download della pianta");
                    }
            });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.aggiorna_data_acqua:
                aggiornaData(URL_AGGIORNA_DATA_ACQUA);
                break;
            case R.id.aggiorna_data_concimazione:
                aggiornaData(URL_AGGIORNA_DATA_CONCIMAZIONE);
                break;
            case R.id.verifica_luce:
                //fa partire l'activity per la verifica della luce
                break;
            case R.id.info_pianta:
                //fa partire l'activity dei dettagli generali di una pianta
                break;
        }
    }

    private void setResult(String result) {
        JSONObject obj = null;
        try {
            obj = new JSONObject(result);
            //mNomeUtente.setText(obj.getString("nomeAssegnatoDallUser"));
            //mNomeGenerico.setText(obj.getString("nomeGenerale"));
            //Picasso.with(this).load(obj.getString("immagine")).into(mImmagine);
            mIndirizzo.setText(obj.getString("indirizzo"));

            //mLivelloAcqua.setProgress(obj.getInt("livelloAcqua"));
            //mLivelloFertilizzante.setProgress(obj.getInt("livelloConcimazione"));
            mLivelloLuce.setProgress(obj.getInt("luce"));

            mDescrizioneAcqua.setText(obj.getString("descrizioneAcqua"));
            mDescrizioneFertilizzante.setText(obj.getString("descrizioneConcimazione"));
            //mDescrizioneLuce.setText(obj.getString("descrizioneLuce"));

            //TODO: fare intervallo acqua
            //mIntervalloAcqua.setText(obj.getInt("intervalloAcqua"));
            //TODO: fare intervallo fertilizzante
            //mIntervalloFertilizzante.setText(obj.getInt("intervalloFertilizzante"));

            //TODO: fare data ultima acqua
            //mDataUltimaAcqua.setText(obj.getString("dataUltimaAcqua"));
            //TODO: fare data ultimo fertilizzante
            //mDataUltimoFertilizzante.setText(obj.getString("dataUltimaConcimazione"));

            lon = obj.getDouble("gpslong");
            lat = obj.getDouble("gpslat");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void aggiornaData(String url) {
        JSONObject obj = null;
        try {
            obj = new JSONObject();
            obj.put("idPossesso", idPossesso);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String body = obj.toString();
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v(TAG, "Aggiornamento data riuscito!");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.v(TAG, "Errore nell'aggiornamento della data");
            }
        }){
            // this is the relevant method
            @Override
            public byte[] getBody() throws AuthFailureError {

                return body.getBytes();
            }
        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}
