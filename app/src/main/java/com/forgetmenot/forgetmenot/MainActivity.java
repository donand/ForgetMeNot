package com.forgetmenot.forgetmenot;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.MatrixCursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;

import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.forgetmenot.forgetmenot.network.GetElencoPianteUtente;
import com.forgetmenot.forgetmenot.network.GetElencoTipiPiante;
import com.forgetmenot.forgetmenot.network.TaskCallbackElenco;
import com.forgetmenot.forgetmenot.network.TaskCallbackElencoTipi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class MainActivity extends ActionBarActivity implements TaskCallbackElenco, TaskCallbackElencoTipi, View.OnClickListener {
    String prova ;

    //piante utente
    JSONArray elencoPiante=null;
    //piante totali
    JSONArray elencoPianteGenerali=null;
    ImageButton fabUpload;
    SharedPreferences pref;

    SwipeRefreshLayout swipeView;
    
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pref=getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        String idUtente=pref.getString("idUtente", null);
        final String ricerca = "http://forgetmenot.ddns.net/ForgetMeNot/ElencoPianteUtente?utente="+idUtente;

        //floating button
        fabUpload = (ImageButton)findViewById(R.id.fab);
        fabUpload.setVisibility(View.VISIBLE);
        fabUpload.bringToFront();
        fabUpload.setOnClickListener(this);

        swipeView = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        //swipeView.setEnabled(false);
        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeView.setRefreshing(true);

                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeView.setRefreshing(false);
                        System.out.println("REFRESH");
                        /* Devo chiamare l'API */
                        chiamaApi(ricerca);
                    }
                }, 2000);

            }
        });
        //chiamo il task che prende l'elenco di piante dell'utente dal server. ora provo con un utente a caso
        try {
            GetElencoPianteUtente task = new GetElencoPianteUtente(ricerca, this, this.getApplicationContext());
            if(checkNetwork()) task.execute();
        } catch (Exception e) {
        }
    }
    private void chiamaApi(String ricerca){
        GetElencoPianteUtente task = new GetElencoPianteUtente(ricerca, this, this.getApplicationContext());
        if(checkNetwork()) task.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu=menu;

        try {
            String ricerca = "http://forgetmenot.ddns.net/ForgetMeNot/GetElencoTipiPiante";
            GetElencoTipiPiante task = new GetElencoTipiPiante(ricerca, this, this.getApplicationContext());
            if(checkNetwork()) task.execute();
        } catch (Exception e) {
        }

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final android.widget.SearchView searchView = (android.widget.SearchView) (menu.findItem(R.id.search)).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String query) {
                //aspetto che il task completi il jsonarray
                while (elencoPianteGenerali == null) {
                }

                loadHistory(query);

                return true;

            }

            @Override
            public boolean onQueryTextSubmit(String a) {
                return false;
            }


        });

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus)
                    return;
                String query = searchView.getQuery().toString();
                searchView.setQuery(query, false);
            }
        });

        return true;
    }

    /*@Override
    public boolean onSearchRequested(){
        System.out.println("CERCA");
        loadHistory("");
        return true;
    }*/
    // History
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void loadHistory(String query) {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Cursor
            String[] columns = new String[] { "_id", "text" };
            Object[] temp = new Object[] { 0, "default" };

            MatrixCursor cursor = new MatrixCursor(columns);
            JSONArray items=new JSONArray();
            for(int i = 0; i < elencoPianteGenerali.length(); i++) {
                try {
                    temp[0] = i;
                    JSONObject o=(JSONObject)elencoPianteGenerali.get(i);
                    temp[1] = o.toString();

                    if (o.getString("nome").toLowerCase().startsWith(query.toLowerCase()) || query.equals("")){
                        //System.out.println(o.toString());
                        items.put(o);
                        cursor.addRow(temp);
                    }

                }
                catch (JSONException e){
                    e.printStackTrace();
                }
            }
            // SearchView
            SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

            final android.widget.SearchView search = (android.widget.SearchView) menu.findItem(R.id.search).getActionView();
            //search.setSuggestionsAdapter(new SearchAdapter(this, cursor, items));
            search.setSuggestionsAdapter(new SearchAdapter(this, cursor, items));
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.luce){
            Intent i = new Intent(MainActivity.this, VerificaLuce.class);
            this.startActivity(i);
        }
        if(id==R.id.search){
            //onSearchRequested();
            return true;
        }
        /*if(id==R.id.foto){
            Intent i = new Intent(MainActivity.this, VisualSearch.class);
            this.startActivity(i);
        }*/

        return super.onOptionsItemSelected(item);
    }

    //callback from getelencotipipiante
    public void done(String r, boolean inutile){
        try{
            elencoPianteGenerali=new JSONArray(r);
        }
        catch(JSONException e){
            e.printStackTrace();
        }
    }

    //callback from getelencopianteutente
    public void done(String result){
        try{
            TextView messaggioIniziale=(TextView)findViewById(R.id.messaggio);
            System.out.println("RESULT "+result);
            if(result==null || result.equals("[]")){
                messaggioIniziale.setText("Non hai nessuna pianta. Aggiungi le tue piante cliccando sul bottone in basso!");
                return;
            }
            elencoPiante=new JSONArray(result);

            impostaMessaggioIniziale(elencoPiante);

            //listview di activity_main
            final ListView listaPiante=(ListView)findViewById(R.id.piante);
            CustomListPiante adapter = new CustomListPiante(MainActivity.this, elencoPiante);
            listaPiante.setAdapter(adapter);
            listaPiante.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    int topRowVerticalPosition =
                            (listaPiante == null || listaPiante.getChildCount() == 0) ?
                                    0 : listaPiante.getChildAt(0).getTop();
                    swipeView.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
                }
            });
            /*String nomeR="Nome";
            String immagineR="http://www.drogbaster.it/immagini-3d/album/slides/immagini-tridimensionali%20(5).jpg";*/
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private void impostaMessaggioIniziale(JSONArray result){
        boolean bad=false;
        boolean warning=false;
        for(int i=0; i<result.length(); i++){
            try{
                JSONObject o=result.getJSONObject(i);
                int livelloAcqua=o.getInt("livelloAcqua");
                int livelloFertilizzante=o.getInt("livelloConcimazione");
                if(livelloAcqua==0 || livelloFertilizzante==0){
                    bad=true;
                }
                else if(!bad && (livelloAcqua<=2 || livelloFertilizzante<=2)){
                    warning=true;
                }
            }
            catch (JSONException e){
                e.printStackTrace();
            }
        }
        TextView messaggioIniziale=(TextView)findViewById(R.id.messaggio);
        CardView cardMessaggioIniziale = (CardView) findViewById(R.id.cardview_messaggio);

        if(bad){
            messaggioIniziale.setText("Attenzione! Qualcuna delle tue piante ha bisogno di cure!");
            cardMessaggioIniziale.setCardBackgroundColor(getResources().getColor(R.color.material_red));

        }
        else if(!bad && warning){
            messaggioIniziale.setText("Attenzione! Qualcuna delle tue piante avrà presto bisogno di cure!");
            cardMessaggioIniziale.setCardBackgroundColor(getResources().getColor(R.color.material_yellow));
        }
        else {
            messaggioIniziale.setText("Le tue piante stanno bene.");
            cardMessaggioIniziale.setCardBackgroundColor(getResources().getColor(R.color.material_green));
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                Intent i = new Intent(this, AggiungiPianta.class);
                startActivity(i);
                break;
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
