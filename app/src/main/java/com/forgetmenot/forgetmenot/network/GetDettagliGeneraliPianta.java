package com.forgetmenot.forgetmenot.network;

/**
 * Created by Davide on 02/07/15.
 */

import android.os.AsyncTask;

import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Sara on 27/06/2015.
 */
public class GetDettagliGeneraliPianta extends AsyncTask<String, Void, String> {

    //nome della pianta da cercare.
    private String ricerca;
    private TaskCallbackDettagliGeneraliPianta dati;
    private Context context;

    public GetDettagliGeneraliPianta(String q, TaskCallbackDettagliGeneraliPianta a, Context c){
        ricerca=q;
        dati=a;
        context=c;
    }

    @Override
    protected String doInBackground(String... url){
        try{

	/*String nomePianta=getIntent.getExtras().getString("nomePianta");
	String ricerca="http://forgetmenot.ddns.net/ForgetMeNot/GetDatiGeneraliPianta?nome="+nomePianta;*/
            URL u = new URL(ricerca);
            HttpURLConnection urlConnection = (HttpURLConnection) u.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.connect();

            //read response
            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String result = "";
            String line="";
            while ((line = in.readLine()) != null)
                result += line;
            in.close();
            return result;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    @Override
    protected void onPostExecute(String result) {
        //il booleano serve solo a distinguere i metodi done
        dati.done(result);
    }
}