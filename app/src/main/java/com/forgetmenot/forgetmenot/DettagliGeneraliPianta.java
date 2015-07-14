package com.forgetmenot.forgetmenot;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.forgetmenot.forgetmenot.network.GetDettagliGeneraliPianta;
import com.forgetmenot.forgetmenot.network.TaskCallbackDettagliGeneraliPianta;
import com.forgetmenot.forgetmenot.utils.CircleTransform;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;


public class DettagliGeneraliPianta extends AppCompatActivity implements TaskCallbackDettagliGeneraliPianta {
    public final static String URL_DETTAGLI_GENERALI_PIANTA = "http://forgetmenot.ddns.net/ForgetMeNot/GetDatiGeneraliPianta?nome=";
    public String nomePianta;
    JSONObject JSONpianta;
    ImageView immaginePianta;

    ProgressBar progressBarLuce;

    TextView pianta;
    TextView infoGenerali;
    TextView infoAcqua;
    TextView infoConcimazione;
    TextView fioritura;
    TextView potatura;
    TextView terreno;
    TextView luce;

    int livelloLuce;
    String tipoPianta;
    String url;
    String nomePiantaUrl;

    boolean puoiAggiungere = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dettagli_generali_pianta);

        nomePianta = getIntent().getStringExtra("nome");

        nomePiantaUrl = nomePianta.replaceAll(" ", "%20");
        nomePiantaUrl=nomePianta.replaceAll("ù","%C3%B9");
        url = URL_DETTAGLI_GENERALI_PIANTA + nomePiantaUrl;
        System.out.println("°°°°°°°°°°°°°°"+url);

        immaginePianta = (ImageView) findViewById(R.id.immaginePianta);
        progressBarLuce = (ProgressBar) findViewById(R.id.progress_bar_luce);

        pianta = (TextView) findViewById(R.id.nomePianta);
        infoGenerali = (TextView) findViewById(R.id.infoGeneraliPianta);
        infoAcqua = (TextView) findViewById(R.id.infoAcqua);
        infoConcimazione = (TextView) findViewById(R.id.infoConcimazione);
        fioritura = (TextView) findViewById(R.id.infoFioritura);
        potatura = (TextView) findViewById(R.id.infoPotatura);
        terreno = (TextView) findViewById(R.id.infoTerreno);
        luce = (TextView) findViewById(R.id.livelloLuce);

        System.out.println(" siamo in 1");
        if (checkNetwork())
            new GetDettagliGeneraliPianta(url, this, getApplicationContext()).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dettagli_generali_pianta, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.aggiungi) {
            if (puoiAggiungere) {
                Intent i = new Intent(DettagliGeneraliPianta.this, AggiungiPianta.class);
                i.putExtra("nomePianta", tipoPianta);
                this.startActivity(i);
            } else
                Toast.makeText(getApplicationContext(), "Attenzione! impossibile completare l'operazione", Toast.LENGTH_LONG).show();


            return super.onOptionsItemSelected(item);
        }

        return true;
    }


    public boolean checkNetwork() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        boolean isOnline = (netInfo != null && netInfo.isConnectedOrConnecting());
        if (isOnline) {
            return true;
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Attenzione!")
                    .setMessage("Sembra che tu non sia collegato ad internet! ")
                    .setPositiveButton("Impostazioni", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                            Intent callGPSSettingIntent = new Intent(Settings.ACTION_SETTINGS);
                            startActivityForResult(callGPSSettingIntent, 0);
                        }
                    }).show();
            return false;
        }
    }

    public void done(String result) {
        try {
            System.out.println(" siamo in 2");

            JSONpianta = new JSONObject(result);

            livelloLuce = (JSONpianta.getInt("luce"));

            Picasso.with(getApplicationContext()).load(JSONpianta.getString("immagine")).transform(new CircleTransform()).into(immaginePianta);

            tipoPianta = JSONpianta.getString("nome");

            pianta.setText(tipoPianta);
            infoGenerali.setText(JSONpianta.getString("descrizione"));
            infoAcqua.setText(JSONpianta.getString("descrizioneAcqua"));
            infoConcimazione.setText(JSONpianta.getString("descrizioneConcimazione"));
            fioritura.setText(JSONpianta.getString("fioritura"));
            potatura.setText(JSONpianta.getString("potatura"));
            terreno.setText(JSONpianta.getString("terreno"));
            luce.setText("" + livelloLuce + "/5");
            progressBarLuce.setProgress(20 * livelloLuce);

            puoiAggiungere = true;
            System.out.println(" siamo in 3");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}