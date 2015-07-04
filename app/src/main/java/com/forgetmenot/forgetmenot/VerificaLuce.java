package com.forgetmenot.forgetmenot;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import android.content.pm.PackageManager;

import com.forgetmenot.forgetmenot.network.GetPianteConCertaLuce;
import com.forgetmenot.forgetmenot.network.TaskCallbackElencoPianteConCertaLuce;

import org.json.JSONArray;

public class VerificaLuce extends AppCompatActivity implements SensorEventListener, TaskCallbackElencoPianteConCertaLuce {


    private TextView testo;
    private TextView comincia;
    private TextView messaggio;
    private TextView nuovaRicerca;
    private TextView trova;

    private ListView listaPiante;

    private ProgressBar barraLuce;

    private ImageView valoreLuce;
    private ImageButton info;

    private int livelloLuceFinale;

    private SensorManager mSensorManager;
    private Sensor luce;

    private boolean sensoreLuce = true;
    private boolean cambia = false;
    private boolean interruttore = true;


    public static final String URL_GET_PIANTE_CON_UNA_CERTA_LUCE = "http://forgetmenot.ddns.net/ForgetMeNot//GetPianteFromLuce?luce=";
    JSONArray elencoPiante = null;
    private String url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifica_luce);

        testo = (TextView) findViewById(R.id.attenzione);
        //infoLivelloLuce = (TextView) findViewById(R.id.livello_luce);
        comincia = (TextView) findViewById(R.id.text_comincia);
        trova = (TextView) findViewById(R.id.trovaPiante);
        messaggio = (TextView) findViewById(R.id.messaggio);
        nuovaRicerca = (TextView) findViewById(R.id.nuovaRicerca);
        listaPiante = (ListView) findViewById(R.id.piante);
        listaPiante.setVisibility(View.INVISIBLE);
        valoreLuce = (ImageView) findViewById(R.id.checkluce);
        info = (ImageButton) findViewById(R.id.info);

        barraLuce = (ProgressBar)findViewById(R.id.progress_bar_luce);
        // Get an instance of the sensor service
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        luce = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        PackageManager PM = this.getPackageManager();

        boolean gyro = PM.hasSystemFeature(PackageManager.FEATURE_SENSOR_GYROSCOPE);
        boolean light = PM.hasSystemFeature(PackageManager.FEATURE_SENSOR_LIGHT);
        sensoreLuce = light;


        comincia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sensoreLuce) {
                    comincia.setVisibility(View.GONE);
                    testo.setText("Il livello di luminosità in questo punto della casa è:");
                    testo.setTextColor(Color.BLACK);
                    //testo.setTextAppearance(getApplicationContext(), android.R.style.TextAppearance_Large);
                    trova.setVisibility(View.VISIBLE);
                    cambia = true;
                } else {
                    Toast.makeText(getApplicationContext(), "Attenzione questo dispositivo non dispone del sensore di luminosità. Impossibile completare l'operazione", Toast.LENGTH_LONG).show();
                }
            }

        });

        trova.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trova.setVisibility(View.GONE);
                cercaPiante();
                nuovaRicerca.setVisibility(View.VISIBLE);
                barraLuce.setVisibility(View.VISIBLE);
                interruttore = false;

            }

        });

        nuovaRicerca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nuovaRicerca.setVisibility(View.INVISIBLE);
                //infoLivelloLuce.setVisibility(View.VISIBLE);
                cambia = true;
                trova.setVisibility(View.VISIBLE);
                messaggio.setVisibility(View.INVISIBLE);
                listaPiante.setVisibility(View.INVISIBLE);
                barraLuce.setVisibility(View.INVISIBLE);
                interruttore = true;



            }

        });

    }
    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something if sensor accuracy changes.
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        if(interruttore) {
            float livelloLuce = (event.values[0]);
            if (cambia) {
                if (livelloLuce < 1000) {
                    livelloLuceFinale = 1;
                    valoreLuce.setImageResource(R.drawable.uno);
                    barraLuce.setProgress(livelloLuceFinale * 20);
                    messaggio.setText("Ecco le piante che hanno bisogno di luce " + livelloLuceFinale + "/5");
                } else if (livelloLuce < 2500) {
                    livelloLuceFinale = 2;
                    valoreLuce.setImageResource(R.drawable.due);
                    barraLuce.setProgress(livelloLuceFinale * 20);
                    messaggio.setText("Ecco le piante che hanno bisogno di luce " + livelloLuceFinale + "/5");
                } else if (livelloLuce < 4000) {
                    livelloLuceFinale = 3;
                    valoreLuce.setImageResource(R.drawable.tre);
                    barraLuce.setProgress(livelloLuceFinale * 20);
                    messaggio.setText("Ecco le piante che hanno bisogno di luce " + livelloLuceFinale + "/5");
                } else if (livelloLuce < 6000) {
                    livelloLuceFinale = 4;
                    valoreLuce.setImageResource(R.drawable.quattro);
                    barraLuce.setProgress(livelloLuceFinale * 20);
                    messaggio.setText("Ecco le piante che hanno bisogno di luce " + livelloLuceFinale + "/5");
                } else {
                    livelloLuceFinale = 5;
                    valoreLuce.setImageResource(R.drawable.cinque);
                    barraLuce.setProgress(livelloLuceFinale * 20);
                    messaggio.setText("Ecco le piante che hanno bisogno di luce " + livelloLuceFinale + "/5");
                }

            }
        }
    }


    @Override
    protected void onResume() {
        // Register a listener for the sensor.
        super.onResume();
        mSensorManager.registerListener(this, luce, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        // important to unregister the sensor when the activity pauses.
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    private void cercaPiante() {
        url = URL_GET_PIANTE_CON_UNA_CERTA_LUCE;
        url = url + livelloLuceFinale;
        System.out.println(url);
        GetPianteConCertaLuce task = new GetPianteConCertaLuce(url, this, getApplicationContext());
        if (checkNetwork()) task.execute();
    }

    public void done(String result){
        try{
            messaggio.setVisibility(View.VISIBLE);
            System.out.println("------------------------------------ "+result);
            elencoPiante = new JSONArray(result);
            if(elencoPiante.length()==0){
                messaggio.setText("Non è stata trovata nessuna pianta con questa luminosità!");
            }

            //listview di activity_main
            listaPiante.setVisibility(View.VISIBLE);
            setListViewHeightBasedOnItems(listaPiante);
            CustomListPianteConCertaLuce adapter = new CustomListPianteConCertaLuce(VerificaLuce.this, elencoPiante);
            listaPiante.setAdapter(adapter);

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    public static boolean setListViewHeightBasedOnItems(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                item.measure(0, 0);
                totalItemsHeight += item.getMeasuredHeight();
            }

            // Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight;
            listView.setLayoutParams(params);
            listView.requestLayout();

            return true;

        } else {
            return false;
        }

    }


    public boolean checkNetwork() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        boolean isOnline = (netInfo != null && netInfo.isConnectedOrConnecting());
        if(isOnline) {
            return true;
        }else{
            new AlertDialog.Builder(this)
                    .setTitle("Attenzione!")
                    .setMessage("Sembra che tu non sia collegato ad internet! ")
                    .setPositiveButton("Impostazioni", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                            Intent callGPSSettingIntent = new Intent(Settings.ACTION_SETTINGS);
                            startActivityForResult(callGPSSettingIntent,0);
                        }
                    }).show();
            return false;
        }
    }
}
