package com.forgetmenot.forgetmenot;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Switch;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class DettagliPiantaUtente extends ActionBarActivity implements View.OnClickListener {
    public static final String TAG = "DettagliPianteUtente";
    public static final String URL_AGGIORNA_DATA_ACQUA = "http://forgetmenot.ddns.net/ForgetMeNot/AggiornaDataUltimaAcqua";
    public static final String URL_AGGIORNA_DATA_CONCIMAZIONE = "http://forgetmenot.ddns.net/ForgetMeNot/AggiornaDataUltimaConcimazione";
    public static final String URL_GET_DETTAGLI = "http://forgetmenot.ddns.net/ForgetMeNot/GetDettagliPiantaUser?id=";
    public static final String URL_GET_METEO = "http://api.openweathermap.org/data/2.5/weather?";
    public static final String URL_ICONA_METEO = "http://openweathermap.org/img/w/";
    public static final String URL_SET_NOTIFICHE_ACQUA = "http://forgetmenot.ddns.net/ForgetMeNot/SetNotificheAcqua";
    public static final String URL_SET_NOTIFICHE_FERTILIZZANTE = "http://forgetmenot.ddns.net/ForgetMeNot/SetNotificheFertilizzante";

    public static final String NOME_ASSEGNATO = "com.forgetmenot.forgetmenot.nomeAssegnato";
    public static final String NOME_GENERALE = "com.forgetmenot.forgetmenot.nomeGenerale";
    public static final String LIVELLO_ACQUA = "com.forgetmenot.forgetmenot.livelloAcqua";
    public static final String LIVELLO_CONCIMAZIONE = "com.forgetmenot.forgetmenot.livelloConcimazione";
    public static final String IMMAGINE = "com.forgetmenot.forgetmenot.immagine";
    public static final String ID = "com.forgetmenot.forgetmenot.id";

    public static final int LIVELLO_WARNING = 2;
    private static final int MILLISECONDS_IN_A_DAY = 1000*60*60*24;

    private ImageView mImmagine;
    private TextView mNomeUtente, mNomeGenerico, mIndirizzo;
    private ProgressBar mLivelloAcqua, mLivelloFertilizzante, mLivelloLuce;
    private TextView mDescrizioneAcqua, mDescrizioneFertilizzante, mDescrizioneLuce;
    private TextView mIntervalloAcqua, mIntervalloFertilizzante;
    private TextView mDataUltimaAcqua, mDataUltimoFertilizzante, mDataProssimaAcqua, mDataProssimoFertilizzante;
    private TextView mCittà, mTemperaturaMin, mTemperaturaMax, mTemperaturaAttuale;
    private ImageView mIconaMeteo, mIconaStatoPianta;
    private Button scopriNegozi;
    private Switch mSwitchAcqua, mSwitchFertilizzante;
    private CardView mCardViewMessaggioStato;
    private TextView mMessaggioStatoPianta;

    private double lat, lon;
    private int idPossesso, intervalloAcqua, intervalloFertilizzante;
    private boolean notificheAcqua, notificheFertilizzante;
    private Date dataUltimaAcqua, dataUltimoFertilizzante, dataProssimaAcqua, dataProssimoFertilizzante;
    private DateFormat inputDateFormat = new SimpleDateFormat("EEE MMM d k:m:s zzz yyyy", Locale.ENGLISH);
    private DateFormat outputDateFormat = new SimpleDateFormat("EEE d MMMM yyyy", Locale.getDefault());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dettagli_pianta_utente);

        Intent intent = getIntent();

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
        mDataProssimaAcqua = (TextView) findViewById(R.id.data_prossima_acqua);
        mDataProssimoFertilizzante = (TextView) findViewById(R.id.data_prossimo_fertilizzante);

        mCittà = (TextView) findViewById(R.id.città);
        mTemperaturaMin = (TextView) findViewById(R.id.temperatura_min);
        mTemperaturaMax = (TextView) findViewById(R.id.temperatura_max);
        mTemperaturaAttuale = (TextView) findViewById(R.id.temperatura_attuale);
        mIconaMeteo = (ImageView) findViewById(R.id.icona_meteo);

        scopriNegozi = (Button) findViewById(R.id.scopri_negozi);
        scopriNegozi.setOnClickListener(this);

        mSwitchAcqua = (Switch) findViewById(R.id.switch_notifiche_acqua);
        mSwitchFertilizzante = (Switch) findViewById(R.id.switch_notifiche_fertilizzante);

        mCardViewMessaggioStato = (CardView) findViewById(R.id.cardview_messaggio);
        mMessaggioStatoPianta = (TextView) findViewById(R.id.messaggio_stato_pianta);
        mIconaStatoPianta = (ImageView) findViewById(R.id.icona_stato_pianta);

        ((Button) findViewById(R.id.aggiorna_data_acqua)).setOnClickListener(this);
        ((Button) findViewById(R.id.aggiorna_data_concimazione)).setOnClickListener(this);
        ((Button) findViewById(R.id.verifica_luce)).setOnClickListener(this);
        ((Button) findViewById(R.id.info_pianta)).setOnClickListener(this);
        idPossesso = intent.getIntExtra(ID, -1);
        mNomeUtente.setText(intent.getStringExtra(NOME_ASSEGNATO));
        mNomeGenerico.setText(intent.getStringExtra(NOME_GENERALE));
        Picasso.with(this).load(intent.getStringExtra(IMMAGINE)).transform(new CircleTransform()).into(mImmagine);

        setSwitchListeners();
        setScrollableDescriptions();
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
        int id = item.getItemId();
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

    private void setSwitchListeners() {
        mSwitchAcqua.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                notificheAcqua = b;
                JSONObject obj = null;
                try {
                    obj = new JSONObject();
                    obj.put("idPossesso", idPossesso);
                    obj.put("notificheAcqua", b);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setNotificheToServer(URL_SET_NOTIFICHE_ACQUA, obj);
                if (b)
                    impostaNotificaInnaffiamento();
                else
                    cancellaNotificaInnaffiamento();
                Log.v(TAG, "switch acqua cambiato a " + b);
            }
        });
        mSwitchFertilizzante.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                notificheFertilizzante = b;
                JSONObject obj = null;
                try {
                    obj = new JSONObject();
                    obj.put("idPossesso", idPossesso);
                    obj.put("notificheFertilizzante", b);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setNotificheToServer(URL_SET_NOTIFICHE_FERTILIZZANTE, obj);
                if (b)
                    impostaNotificaFertilizzante();
                else
                    cancellaNotificaFertilizzante();
                Log.v(TAG, "switch fertilizzante cambiato a " + b);
            }
        });
    }

    private void setMessaggioStato() {
        int livelloAcqua = mLivelloAcqua.getProgress();
        int livelloFertilizzante = mLivelloFertilizzante.getProgress();

        if (livelloAcqua == 0 || livelloFertilizzante == 0) {
            mCardViewMessaggioStato.setCardBackgroundColor(getResources().getColor(R.color.material_red));
            mMessaggioStatoPianta.setText("Attenzione! La pianta ha bisogno di cure!");
            mIconaStatoPianta.setImageResource(R.drawable.bad_white);
        } else if (livelloAcqua <= LIVELLO_WARNING || livelloFertilizzante <= LIVELLO_WARNING) {
            mCardViewMessaggioStato.setCardBackgroundColor(getResources().getColor(R.color.material_yellow));
            mMessaggioStatoPianta.setText("Attenzione! La pianta avrà presto bisogno di cure!");
            mIconaStatoPianta.setImageResource(R.drawable.neutral_white);
        }
        else {
            mCardViewMessaggioStato.setCardBackgroundColor(getResources().getColor(R.color.material_green));
            mMessaggioStatoPianta.setText("La pianta sta bene!");
            mIconaStatoPianta.setImageResource(R.drawable.happy_white);
        }
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
                if(notificheAcqua)
                    impostaNotificaInnaffiamento();
                break;
            case R.id.aggiorna_data_concimazione:
                aggiornaData(URL_AGGIORNA_DATA_CONCIMAZIONE);
                if (notificheFertilizzante)
                    impostaNotificaFertilizzante();
                break;
            case R.id.verifica_luce:
                //fa partire l'activity per la verifica della luce
                break;
            case R.id.info_pianta:
                //fa partire l'activity dei dettagli generali di una pianta
                break;
            case R.id.scopri_negozi:
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

            intervalloAcqua = obj.getInt("acqua");
            mIntervalloAcqua.setText(intervalloAcqua + ((intervalloAcqua == 1)? " giorno" : " giorni"));
            intervalloFertilizzante = obj.getInt("intervalloConcimazione");
            mIntervalloFertilizzante.setText(intervalloFertilizzante + ((intervalloFertilizzante == 1)? " giorno" : " giorni"));

            dataUltimaAcqua = inputDateFormat.parse(obj.getString("dataUltimaAcqua"));
            dataUltimoFertilizzante = inputDateFormat.parse(obj.getString("dataUltimoFertilizzante"));
            dataProssimaAcqua = new Date(dataUltimaAcqua.getTime() + intervalloAcqua*(MILLISECONDS_IN_A_DAY));
            dataProssimoFertilizzante = new Date(dataUltimoFertilizzante.getTime() + intervalloFertilizzante*(MILLISECONDS_IN_A_DAY));
            mDataUltimaAcqua.setText(outputDateFormat.format(dataUltimaAcqua));
            mDataUltimoFertilizzante.setText(outputDateFormat.format(dataUltimoFertilizzante));
            mDataProssimaAcqua.setText(outputDateFormat.format(dataProssimaAcqua));
            mDataProssimoFertilizzante.setText(outputDateFormat.format(dataProssimoFertilizzante));

            mLivelloAcqua.setProgress(obj.getInt("livelloAcqua") * 10);
            mLivelloFertilizzante.setProgress(obj.getInt("livelloConcimazione") * 10);
            lon = obj.getDouble("gpslong");
            lat = obj.getDouble("gpslat");

            notificheAcqua = obj.getBoolean("notificheAcqua");
            mSwitchAcqua.setChecked(notificheAcqua);
            notificheFertilizzante = obj.getBoolean("notificheFertilizzante");
            mSwitchFertilizzante.setChecked(notificheFertilizzante);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        downloadMeteo();
        setMessaggioStato();
    }


    private void aggiornaData(final String url) {
        JSONObject obj = null;
        try {
            obj = new JSONObject();
            obj.put("idPossesso", idPossesso);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v(TAG, "Aggiornamento data riuscito!");
                        if (url.equals(URL_AGGIORNA_DATA_ACQUA))
                            mLivelloAcqua.setProgress(100);
                        else
                            mLivelloFertilizzante.setProgress(100);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.v(TAG, "Errore nell'aggiornamento della data");
            }
        });
        queue.add(jsonRequest);
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
            Picasso.with(this).load(URL_ICONA_METEO + weather.getString("icon") + ".png").into(mIconaMeteo);
        } catch(JSONException e) {
            e.printStackTrace();
        }
    }


    private void impostaNotificaInnaffiamento() {
        Intent intent = new Intent(this, NotificationService.class).setAction(NotificationService.ACTION_NOTIFICA_ACQUA);
        impostaNotifica(intent, dataProssimaAcqua.getTime());
    }

    private void impostaNotificaFertilizzante() {
        Intent intent = new Intent(this, NotificationService.class).setAction(NotificationService.ACTION_NOTIFICA_FERTILIZZANTE);
        impostaNotifica(intent, dataProssimoFertilizzante.getTime());
    }

    private void impostaNotifica(Intent intent, long date) {
        Intent i = getIntent();
        intent.putExtras(i);
        PendingIntent pendingIntent = PendingIntent.getService(this, idPossesso, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
        am.cancel(pendingIntent);
        am.set(AlarmManager.RTC_WAKEUP, date, pendingIntent);
    }



    private void cancellaNotificaInnaffiamento() {
        Intent intent = new Intent(this, NotificationService.class).setAction(NotificationService.ACTION_NOTIFICA_ACQUA);
        cancellaNotifica(intent);
    }

    private void cancellaNotificaFertilizzante() {
        Intent intent = new Intent(this, NotificationService.class).setAction(NotificationService.ACTION_NOTIFICA_FERTILIZZANTE);
        cancellaNotifica(intent);
    }

    private void cancellaNotifica(Intent intent) {
        PendingIntent pendingIntent = PendingIntent.getService(this, idPossesso, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
        am.cancel(pendingIntent);
    }



    private void setNotificheToServer(String url, JSONObject obj) {
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v(TAG, "Notifiche settate correttamente!");
                    }
                }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Log.v(TAG, "Errore nell'aggiornamento delle notifiche");
                }
        });
        queue.add(jsonRequest);
    }
}