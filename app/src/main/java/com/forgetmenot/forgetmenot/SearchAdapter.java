package com.forgetmenot.forgetmenot;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.forgetmenot.forgetmenot.utils.CircleTransform;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Sara on 03/07/2015.
 */
public class SearchAdapter extends CursorAdapter{

    private JSONArray items;

    private TextView text;
    private ImageView immagine;
    LayoutInflater inflater;
    Cursor mCursor;
    Context context;
    public SearchAdapter(Context cont, Cursor cursor, JSONArray items) {

        super(cont, cursor, false);
        this.items = items;
        inflater = (LayoutInflater) cont.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mCursor=cursor;
        context=cont;
    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        text=(TextView)view.findViewById(R.id.nomePianta);
        immagine=(ImageView)view.findViewById(R.id.immaginePianta);
        final Context c=context;
        try{
            JSONObject o=(JSONObject)items.get(cursor.getPosition());
            text.setText(o.getString("nome"));
            Picasso.with(context)
                    .load(o.getString("immagine")).transform(new CircleTransform()).into(immagine);

        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    public View newView(final Context context, Cursor cursor, ViewGroup parent) {

        View view = inflater.inflate(R.layout.search_result, parent, false);
        text = (TextView) view.findViewById(R.id.nomePianta);
        immagine=(ImageView)view.findViewById(R.id.immaginePianta);


        /*text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, DettagliGeneraliPianta.class);
                i.putExtra("nome", text.getText());
                context.startActivity(i);
            }
        });*/
        return view;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (mCursor.moveToPosition(position)) {
            final ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = this.inflater.inflate(R.layout.search_result,
                        parent, false);
                holder.nomePianta = (TextView) convertView.findViewById(R.id.nomePianta);
                holder.immaginePianta = (ImageView) convertView.findViewById(R.id.immaginePianta);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            try {
                final JSONObject o = items.getJSONObject(position);
                holder.nomePianta.setText(o.getString("nome"));
                Picasso.with(context)
                        .load(o.getString("immagine")).transform(new CircleTransform()).into(holder.immaginePianta);
                holder.nomePianta.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(context, DettagliGeneraliPianta.class);
                        i.putExtra("nome", holder.nomePianta.getText());
                        context.startActivity(i);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return convertView;
    }

    private class ViewHolder {
        TextView nomePianta;
        ImageView immaginePianta;

    }

}