package com.forgetmenot.forgetmenot;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import android.content.pm.PackageManager;

public class VerificaLuce extends AppCompatActivity implements SensorEventListener {


    private TextView valoreLuce;
    private SensorManager mSensorManager;
    private Sensor luce;
    private String prova2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifica_luce);
        valoreLuce= (TextView)findViewById(R.id.valoreLuce);
        // Get an instance of the sensor service
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        luce=mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        PackageManager PM= this.getPackageManager();
        boolean gyro = PM.hasSystemFeature(PackageManager.FEATURE_SENSOR_GYROSCOPE);
        boolean light = PM.hasSystemFeature(PackageManager.FEATURE_SENSOR_LIGHT);

        if(gyro){

            if(light){
                Toast.makeText(getApplicationContext(),"In questo telefono sono presenti sia il sensore di luminosità che il giroscopio", Toast.LENGTH_LONG).show();
            }
            else
                Toast.makeText(getApplicationContext(),"In questo telefono è presente solo il giroscopio", Toast.LENGTH_LONG).show();

        }
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something if sensor accuracy changes.
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        float max = luce.getMaximumRange();
        float livelloLuce = (event.values[0]);
        if(livelloLuce<300)
            valoreLuce.setText("1");
        else if(livelloLuce<800)
            valoreLuce.setText("2");
        else if(livelloLuce<1400)
            valoreLuce.setText("3");
        else if(livelloLuce<2000)
            valoreLuce.setText("4");
        else
            valoreLuce.setText("5");
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

}
