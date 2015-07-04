package com.forgetmenot.forgetmenot.network;

import android.os.AsyncTask;

import com.forgetmenot.forgetmenot.network.TaskCallbackUtente;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Sara on 28/06/2015.
 */
public class RegistraUtente extends AsyncTask<String, Void, String> {
    private String email;
    private TaskCallbackUtente splash;

    public RegistraUtente(TaskCallbackUtente u, String e){
        email=e;
        splash=u;
    }

    @Override
    protected String doInBackground(String... url){
        try{
            URL u = new URL("http://forgetmenot.ddns.net/ForgetMeNot/RegistraUtente");
            HttpURLConnection urlConnection = (HttpURLConnection) u.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.connect();

            JSONObject input=new JSONObject();
            input.put("email", email);

            OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
            out.write(input.toString());
            out.close();

            String response="";

            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            String returnString="";
            while ((returnString = in.readLine()) != null){
                response +=returnString;
            }
            in.close();
            System.out.println("RESPONSE (ID) "+response);
            String id=Integer.toString(new JSONObject(response).getInt("id"));
            return id;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    @Override
    protected void onPostExecute(String response) {
        //il booleano serve solo a distinguere i metodi done
        splash.done(response);
    }
}
