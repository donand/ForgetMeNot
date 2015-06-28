package com.forgetmenot.forgetmenot;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import android.content.pm.PackageManager;

import org.json.JSONArray;

import java.net.URLClassLoader;

public class VerificaLuce extends AppCompatActivity implements SensorEventListener, TaskCallbackElencoPianteConCertaLuce {


    private TextView testo;
    private TextView infoLivelloLuce;
    private TextView comincia;
    private TextView trova;
    private TextView messaggio;
    private TextView nuovaRicerca;


    private ImageView valoreLuce;

    private int livelloLuceFinale;

    private SensorManager mSensorManager;
    private Sensor luce;

    private boolean sensoreLuce = true;
    private boolean cambia = false;


    public static final String URL_GET_PIANTE_CON_UNA_CERTA_LUCE = "http://forgetmenot.ddns.net/ForgetMeNot//GetPianteFromLuce?luce=";
    JSONArray elencoPiante = null;
    private String url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifica_luce);

        testo = (TextView) findViewById(R.id.attenzione);
        infoLivelloLuce = (TextView) findViewById(R.id.livello_luce);
        comincia = (TextView) findViewById(R.id.text_comincia);
        trova = (TextView) findViewById(R.id.trovaPiante);
        messaggio = (TextView) findViewById(R.id.messaggio);
        nuovaRicerca = (TextView) findViewById(R.id.nuovaRicerca);


        valoreLuce = (ImageView) findViewById(R.id.checkluce);


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
                    comincia.setVisibility(View.INVISIBLE);
                    testo.setVisibility((View.INVISIBLE));
                    infoLivelloLuce.setVisibility(View.VISIBLE);
                    cambia = true;
                    trova.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(getApplicationContext(), "Attenzione questo dispositivo non dispone del sensore di luminosità. Impossibile completare l'operazione", Toast.LENGTH_LONG).show();
                }
            }

        });

        trova.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trova.setVisibility(View.INVISIBLE);
                cercaPiante();
                nuovaRicerca.setVisibility(View.VISIBLE);
            }

        });

        nuovaRicerca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nuovaRicerca.setVisibility(View.INVISIBLE);
                infoLivelloLuce.setVisibility(View.VISIBLE);
                cambia = true;
                trova.setVisibility(View.VISIBLE);
                messaggio.setVisibility(View.INVISIBLE);
            }


        });


    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something if sensor accuracy changes.
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        float livelloLuce = (event.values[0]);
        if (cambia) {
            if (livelloLuce < 1000) {
                livelloLuceFinale = 1;
                valoreLuce.setImageResource(R.drawable.uno);
            } else if (livelloLuce < 2500) {
                livelloLuceFinale = 2;
                valoreLuce.setImageResource(R.drawable.due);
            } else if (livelloLuce < 3500) {
                livelloLuceFinale = 3;
                valoreLuce.setImageResource(R.drawable.tre);
            } else if (livelloLuce < 5000) {
                livelloLuceFinale = 4;
                valoreLuce.setImageResource(R.drawable.quattro);
            } else {
                livelloLuceFinale = 5;
                valoreLuce.setImageResource(R.drawable.cinque);
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
        task.execute();
    }

    public void done(String result){
        try{
            messaggio.setVisibility(View.VISIBLE);
            System.out.println("------------------------------------ "+result);
            elencoPiante = new JSONArray(result);
            if(elencoPiante.length()==0){
                messaggio.setText("Non è stata trovata nessuna pianta con questa luminosità");
            }
            else {
                messaggio.setText("Piante trovate con luminosità " + livelloLuceFinale + ":");
            }
            //listview di activity_main
            ListView listaPiante=(ListView)findViewById(R.id.piante);
            CustomListPianteConCertaLuce adapter = new CustomListPianteConCertaLuce(VerificaLuce.this, elencoPiante);
            listaPiante.setAdapter(adapter);

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
