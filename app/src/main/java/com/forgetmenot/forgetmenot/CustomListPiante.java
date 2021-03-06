package com.forgetmenot.forgetmenot;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.forgetmenot.forgetmenot.utils.CircleTransform;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Sara on 27/06/2015.
 */
public class CustomListPiante extends BaseAdapter {
    private final Activity context;
    LayoutInflater inflater;
    private final JSONArray elencoPiante;


    public CustomListPiante(Activity context, JSONArray elenco) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.elencoPiante=elenco;
    }
    public int getCount() {
        return elencoPiante.length();
    }

    public JSONObject getItem(int position) {
        try {
            return elencoPiante.getJSONObject(position);
        }
        catch(JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public long getItemId(int position) {

        try {
            return elencoPiante.getJSONObject(position).getInt("ID");
        }
        catch(JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        CardView pianta;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = this.inflater.inflate(R.layout.list_piante_utente_item,
                    parent, false);
            holder.nomePianta = (TextView) convertView.findViewById(R.id.nomePianta);
            holder.la=(ProgressBar)convertView.findViewById(R.id.acquaProgress);
            holder.lf=(ProgressBar)convertView.findViewById(R.id.fertilizzanteProgress);
            holder.stato=(ImageView)convertView.findViewById(R.id.statoPianta);
            holder.immaginePianta=(ImageView)convertView.findViewById(R.id.immaginePianta);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        try {
            final JSONObject o = elencoPiante.getJSONObject(position);
            pianta=(CardView)convertView.findViewById(R.id.card_view);
            pianta.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent((MainActivity)context, DettagliPiantaUtente.class);
                    try {
                        i.putExtra(DettagliPiantaUtente.NOME_GENERALE, o.getString("nomePianta"));
                        i.putExtra(DettagliPiantaUtente.NOME_ASSEGNATO, o.getString("nomeAssegnato"));
                        i.putExtra(DettagliPiantaUtente.LIVELLO_CONCIMAZIONE, o.getInt("livelloConcimazione"));
                        i.putExtra(DettagliPiantaUtente.LIVELLO_ACQUA, o.getInt("livelloAcqua"));
                        i.putExtra(DettagliPiantaUtente.IMMAGINE, o.getString("immagine"));
                        i.putExtra(DettagliPiantaUtente.ID, o.getInt("ID"));
                    }
                    catch (JSONException e){
                        e.printStackTrace();
                    }
                    ((MainActivity)context).startActivity(i);
                }
            });

            holder.nomePianta.setText(o.getString("nomeAssegnato"));
            holder.livelloAcqua=o.getInt("livelloAcqua");
            holder.livelloFertilizzante=o.getInt("livelloConcimazione");

            holder.la.setProgress(holder.livelloAcqua * 10);

            holder.lf.setProgress(holder.livelloFertilizzante * 10);

            //se si verifica vuol dire che la pianta sta male.
            if(holder.livelloFertilizzante==0 || holder.livelloAcqua==0){
                holder.stato.setImageResource(R.drawable.sad_red_48dp);
            }
            else if(holder.livelloFertilizzante<=2 || holder.livelloAcqua<=2){
                holder.stato.setImageResource(R.drawable.neutral_yellow_48dp);
            }
            else holder.stato.setImageResource(R.drawable.happy_green_48dp);

            Picasso.with(context)
                    .load(o.getString("immagine")).transform(new CircleTransform()).into(holder.immaginePianta);
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }

    private class ViewHolder {
        ImageView stato;
        ProgressBar la;
        ProgressBar lf;
        TextView nomePianta;
        ImageView immaginePianta;
        int livelloAcqua;
        int livelloFertilizzante;
    }

}
    /*
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_piante_utente_item, null, true);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.immaginePianta);
        TextView nome = (TextView) rowView.findViewById(R.id.nomePianta);

        try{
            nome.setText(elencoPiante.getJSONObject(position).getString("nome"));
            Picasso.with(context)
                    .load(elencoPiante.getJSONObject(position).getString("immagine")).into(imageView);
            int livelloAcqua=elencoPiante.getJSONObject(position).getInt("livelloAcqua");
            int livelloFertilizzante=elencoPiante.getJSONObject(position).getInt("livelloFertilizzante");
            ProgressBar la=(ProgressBar)rowView.findViewById(R.id.acquaProgress);
            la.setProgress(livelloAcqua);
            ProgressBar lf=(ProgressBar)rowView.findViewById(R.id.fertilizzanteProgress);
            lf.setProgress(livelloFertilizzante);

             //controlli sul livello acqua e impostazione stato. ora imposto sempre felice
            ImageView stato=(ImageView)rowView.findViewById(R.id.statoPianta);
            stato.setImageResource(R.drawable.happy);
            System.out.println(nome.getText());
        }
        catch(JSONException e){
            e.printStackTrace();
        }
        return rowView;
    }
}*/
