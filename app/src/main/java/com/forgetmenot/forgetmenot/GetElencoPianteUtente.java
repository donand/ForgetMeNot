package com.forgetmenot.forgetmenot;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Sara on 27/06/2015.
 */
public class GetElencoPianteUtente extends AsyncTask<String, Void, String> {

    //nome della pianta da cercare.
    private String ricerca;
    private TaskCallbackElenco main;
    private Context context;

    public GetElencoPianteUtente(String q, TaskCallbackElenco a, Context c){
        ricerca=q;
        main=a;
        context=c;
    }

    @Override
    protected String doInBackground(String... url){
        try{
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
        main.done(result);
    }
}
