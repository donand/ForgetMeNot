package com.forgetmenot.forgetmenot.network;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Sara on 05/07/2015.
 */
public class GetElencoTipiPiante extends AsyncTask<String, Void, String> {

    //nome della pianta da cercare.
    private String ricerca;
    private TaskCallbackElencoTipi searchactivity;
    private Context context;

    public GetElencoTipiPiante(String q, TaskCallbackElencoTipi a, Context c){
        ricerca=q;
        searchactivity=a;
        context=c;
    }

    @Override
    protected String doInBackground(String... url){
        try{
            URL u = new URL(ricerca);

            HttpURLConnection urlConnection = (HttpURLConnection) u.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            System.out.println(ricerca);
            urlConnection.connect();
            System.out.println(urlConnection.getResponseCode());
            //read response
            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String result = "";
            String line="";
            while ((line = in.readLine()) != null)
                result += line;
            in.close();
            System.out.println(result);
            return result;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    @Override
    protected void onPostExecute(String result) {
        searchactivity.done(result, true);
    }
}

