package com.forgetmenot.forgetmenot;

import android.app.Activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CustomListPianteConCertaLuce extends BaseAdapter {
    private final Activity context;
    LayoutInflater inflater;
    private final JSONArray elencoPiante;


    public CustomListPianteConCertaLuce(Activity context, JSONArray elenco) {
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
            return  getItem(position).getInt("nome");
        }
        catch(JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = this.inflater.inflate(R.layout.list_piante_con_certa_luce_item,
                    parent, false);
            holder.nomePianta = (TextView) convertView.findViewById(R.id.nomePianta);
            holder.fotoPianta=(ImageView)convertView.findViewById(R.id.fotoPianta);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        try {
            JSONObject o = elencoPiante.getJSONObject(position);
            System.out.println(o.toString());
            holder.nomePianta.setText(o.getString("nome"));


            Picasso.with(context)
                    .load(o.getString("immagine")).into(holder.fotoPianta);
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }

    private class ViewHolder {
        TextView nomePianta;
        ImageView fotoPianta;

    }

}