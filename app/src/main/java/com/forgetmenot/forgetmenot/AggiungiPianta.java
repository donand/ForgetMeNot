package com.forgetmenot.forgetmenot;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ImageButton;
import android.app.AlertDialog;
import android.content.DialogInterface;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class AggiungiPianta extends ActionBarActivity {


    private static final long           MINIMUM_DISTANCE = 1; //metres
    private static final long           MINIMUM_TIME = 1000; //msecs
    private static final String         URL_GET_TYPES ="http://forgetmenot.ddns.net/ForgetMeNot/GetElencoTipiPiante";
    private static final String         URL_ADD_PLANT ="http://forgetmenot.ddns.net/ForgetMeNot/AggiuntaNuovaPianta";
    private static final String         TAG = "TipiImmaginiPiante";
    private static final String         ADD_FAIL = "Impossibile aggiungere la pianta,";
    private static final String         STRING_SELEZIONA = "Seleziona una pianta fra quelle presenti";

    protected LocationManager           locationManager;
    protected ImageButton               gps;
    protected EditText                  editIndirizzo;
    protected EditText                  editNomePianta;
    protected EditText                  eIndirizzo;
    protected Spinner                   spinner;
    protected ArrayAdapter<String>      adapter;
    protected String[]                  typeArray;
    protected String[]                  imageArray;
    protected HashMap<String, String>   typeImageMap;
    protected ImageView                 imageView;

    protected double                    latitude;
    protected double                    longitude;
    protected String                    tipoPianta;
    protected String                    nomePianta;
    protected String                    indirizzo;
    protected int                       utenteID; // da prendere poi con il merge

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aggiungi_pianta);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        utenteID = Integer.parseInt(pref.getString("idUtente", null));

        //setting gps button
        gps = (ImageButton) findViewById(R.id.gps);
        gps.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                fillAddress();
            }
        });
        //database request for plant types
        requestTypes(URL_GET_TYPES);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_aggiungi_pianta, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id){
            case R.id.checked:
                addPlant();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*** My methods ***/

    //method for filling Address edit text from pressing gps button
    protected void fillAddress() {
        //getting coordinates
        Location location = null;
        latitude = 0;
        longitude = 0;
        boolean  isGPSEnabled;
        boolean  isNetworkEnabled;

        locationManager = (LocationManager) AggiungiPianta.this.getSystemService(LOCATION_SERVICE);
        // getting GPS status
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!isGPSEnabled) alertNoGps();
        // if GPS Enabled get lat/long using GPS Services
        if (isGPSEnabled) {
            if (location == null) {
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        MINIMUM_TIME,
                        MINIMUM_DISTANCE, new MyLocationListener());
                Log.d("GPS", "GPS Enabled");
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        Log.d("GPS", "prendo le coordinate dal gps");
                    }
                }
            }
        }
        // getting network status
        /*isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (isNetworkEnabled) {
            if (location == null) {
                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        MINIMUM_TIME,
                        MINIMUM_DISTANCE, new MyLocationListener());
                        Log.d("Network", "Network Enabled");
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }
                }
            }
        }*/



        //converting coordinates into address
        Geocoder geocoder = new Geocoder(AggiungiPianta.this, Locale.getDefault());
        Address address;
        String result;
        List<Address> list = null;
        try {
            list = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (list.size() >0){
            address = list.get(0);
            result = address.getAddressLine(0) + ", " + address.getLocality();
            //filling EditText with result
            editIndirizzo = (EditText) findViewById(R.id.eIndirizzo);
            editIndirizzo.setText(result);
            String message = "Indirizzo registrato\nSe l'indirizzo non coincide\nmodificarlo manualmente";
            Toast.makeText(AggiungiPianta.this, message, Toast.LENGTH_LONG).show();
        }
    }

    //private alert No GPS
    private void alertNoGps(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Il GPS sembra essere disattivo, attivarlo ora?")
                .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    //private Listener
    private class MyLocationListener implements LocationListener {

        public void onLocationChanged(Location location){
            //This method is not used
        }

        public void onStatusChanged(String s, int i, Bundle b){
            //This method is not used
        }

        public void onProviderEnabled(String s){
            //This method is not used
        }

        public void onProviderDisabled(String s){
            //This method is not used
        }

    }

    private void requestTypes(String url){
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray array) {
                        typeArray = new String[array.length()+1];
                        imageArray = new String[array.length()+1];
                        typeImageMap = new HashMap<>();
                        try{
                            //setting first array value to a Default to be displayed in the spinner
                            typeArray[0] = STRING_SELEZIONA;
                            imageArray[0] = null;
                            for(int i=1; i<=array.length(); i++){
                                JSONObject pianta = (JSONObject) array.get(i-1);
                                String tipo = pianta.getString("nome");
                                String immagine = pianta.getString("immagine");
                                typeArray[i] = tipo;
                                imageArray[i] = immagine;
                                typeImageMap.put(tipo, immagine);
                            }
                            setSpinner();
                        } catch(JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //not used
            }
        });
        queue.add(jsonArrayRequest);
    }

    private void setSpinner(){
        spinner = (Spinner) findViewById(R.id.typeSpin);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, typeArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = adapter.getItem(position).toString();
                tipoPianta = selectedItem;
                String associateImage = typeImageMap.get(selectedItem);
                imageView = (ImageView) findViewById(R.id.imageView);
                Picasso.with(AggiungiPianta.this).load(associateImage).into(imageView);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //not used
            }
        });
    }

    private void addPlant(){
        editNomePianta = (EditText) findViewById(R.id.eNome);
        nomePianta = editNomePianta.getText().toString();
        eIndirizzo = (EditText) findViewById(R.id.eIndirizzo);
        indirizzo = eIndirizzo.getText().toString();

        if(tipoPianta == STRING_SELEZIONA){
            Toast.makeText(AggiungiPianta.this, ADD_FAIL+" devi scegliere un tipo!", Toast.LENGTH_LONG).show();
            return;
        }
        if(nomePianta.equals("")){
            Toast.makeText(AggiungiPianta.this, ADD_FAIL +" devi darle un nome!", Toast.LENGTH_LONG).show();
            return;
        }
        if(latitude == 0 || longitude == 0){
            Toast.makeText(AggiungiPianta.this, ADD_FAIL+" coordinate non impostate, usa il tasto GPS per impostarle!", Toast.LENGTH_LONG).show();
            return;
        }


        JSONObject obj = null;
        try{
            obj = new JSONObject();
            obj.put("utenteID", utenteID);
            obj.put("nomeGenerale", tipoPianta);
            obj.put("nomeAssegnato", nomePianta);
            obj.put("lat", latitude);
            obj.put("lon", longitude);
            obj.put("indirizzo", indirizzo);

        } catch(JSONException e){
            e.printStackTrace();
        }
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_ADD_PLANT, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        finish();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        queue.add(jsonObjectRequest);
    }
}