package com.forgetmenot.forgetmenot;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
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
    public static final String URL_GET_METEO = "http://api.openweathermap.org/data/2.5/weather?";
    public static final String URL_ICONA_METEO = "http://openweathermap.org/img/w/";

    private ImageView mImmagine;
    private TextView mNomeUtente, mNomeGenerico, mIndirizzo;

    private ProgressBar mLivelloAcqua, mLivelloFertilizzante, mLivelloLuce;

    private TextView mDescrizioneAcqua, mDescrizioneFertilizzante, mDescrizioneLuce;

    private TextView mIntervalloAcqua, mIntervalloFertilizzante;

    private TextView mDataUltimaAcqua, mDataUltimoFertilizzante;

    private TextView mCittà, mTemperaturaMin, mTemperaturaMax, mTemperaturaAttuale;
    private ImageView mIconaMeteo;

    private TextView scopriNegozi;

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

        mIntervalloAcqua = (TextView) findViewById(R.id.intervallo_acqua);
        mIntervalloFertilizzante = (TextView) findViewById(R.id.intervallo_fertilizzante);

        mDataUltimaAcqua = (TextView) findViewById(R.id.data_acqua);
        mDataUltimoFertilizzante = (TextView) findViewById(R.id.data_fertilizzante);


        mCittà = (TextView) findViewById(R.id.città);
        mTemperaturaMin = (TextView) findViewById(R.id.temperatura_min);
        mTemperaturaMax = (TextView) findViewById(R.id.temperatura_max);
        mTemperaturaAttuale = (TextView) findViewById(R.id.temperatura_attuale);
        mIconaMeteo = (ImageView) findViewById(R.id.icona_meteo);

        scopriNegozi=(TextView)findViewById(R.id.scopri);
        scopriNegozi.setOnClickListener(this);

        ((Button) findViewById(R.id.aggiorna_data_acqua)).setOnClickListener(this);
        ((Button) findViewById(R.id.aggiorna_data_concimazione)).setOnClickListener(this);
        ((Button) findViewById(R.id.verifica_luce)).setOnClickListener(this);
        ((Button) findViewById(R.id.info_pianta)).setOnClickListener(this);

        setScrollableDescriptions();

        Picasso.with(this).load("http://forgetmenot.ddns.net/ForgetMeNot/Immagini/narciso.jpg").transform(new CircleTransform()).into(mImmagine);

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

    private void setScrollableDescriptions() {
        mDescrizioneAcqua.setMovementMethod(new ScrollingMovementMethod());
        mDescrizioneAcqua.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                ((ScrollView) findViewById(R.id.scroll_view)).requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        mDescrizioneFertilizzante.setMovementMethod(new ScrollingMovementMethod());
        mDescrizioneFertilizzante.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                ((ScrollView) findViewById(R.id.scroll_view)).requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        mDescrizioneLuce.setMovementMethod(new ScrollingMovementMethod());
        mDescrizioneLuce.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                ((ScrollView) findViewById(R.id.scroll_view)).requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
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
            case R.id.scopri:
                Intent i = new Intent(DettagliPiantaUtente.this, Mappa.class);
                i.putExtra("lat", lat);
                i.putExtra("lon", lon);
                this.startActivity(i);
        }
    }

    private void setResult(String result) {
        JSONObject obj = null;
        try {
            obj = new JSONObject(result);
            mIndirizzo.setText(obj.getString("indirizzo"));
            mLivelloLuce.setProgress(obj.getInt("luce"));
            mDescrizioneAcqua.setText(obj.getString("descrizioneAcqua"));
            mDescrizioneFertilizzante.setText(obj.getString("descrizioneConcimazione"));
            int in = obj.getInt("acqua");
            mIntervalloAcqua.setText(in + ((in == 1)? " giorno" : " giorni"));
            in = obj.getInt("intervalloConcimazione");
            mIntervalloFertilizzante.setText(in + ((in == 1)? " giorno" : " giorni"));
            mDataUltimaAcqua.setText(obj.getString("dataUltimaAcqua"));
            mDataUltimoFertilizzante.setText(obj.getString("dataUltimoFertilizzante"));

            lon = obj.getDouble("gpslong");
            lat = obj.getDouble("gpslat");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        downloadMeteo();
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
            @Override
            public byte[] getBody() throws AuthFailureError {
                return body.getBytes();
            }
        };
        queue.add(stringRequest);
    }


    private void downloadMeteo() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = URL_GET_METEO + "lat=" + lat + "&lon=" + lon;
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v(TAG, "Download meteo riuscito!");
                        visualizzaMeteo(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v(TAG, "Errore nel download del meteo");
            }
        });
        queue.add(jsonRequest);
    }

    private void visualizzaMeteo(JSONObject result) {
        try {
            String città = result.getString("name");
            JSONObject main = result.getJSONObject("main");
            JSONObject weather = result.getJSONArray("weather").getJSONObject(0);
            mCittà.setText(città);
            mTemperaturaMin.setText("Min " + (int) (main.getDouble("temp_min")-273.15) + "°");
            mTemperaturaMax.setText("Max " + (int) (main.getDouble("temp_max")-273.15) + "°");
            mTemperaturaAttuale.setText((int) (main.getDouble("temp")-273.15) + "°");
            Picasso.with(this).load(URL_ICONA_METEO + weather.getString("icon") + ".png").transform(new CircleTransform()).into(mIconaMeteo);
        } catch(JSONException e) {
            e.printStackTrace();
        }
    }
}
