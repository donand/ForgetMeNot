package com.forgetmenot.forgetmenot;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
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
public class SearchAdapter extends CursorAdapter {

    private JSONArray items;

    private TextView text;
    private ImageView immagine;

    public SearchAdapter(Context context, Cursor cursor, JSONArray items) {

        super(context, cursor, false);

        this.items = items;

    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        text=(TextView)view.findViewById(R.id.nomePianta);
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

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.search_result, parent, false);

        text = (TextView) view.findViewById(R.id.nomePianta);
        immagine=(ImageView)view.findViewById(R.id.immaginePianta);

        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(context, DettagliGeneraliPianta.class);
                i.putExtra("nome", text.getText());
                context.startActivity(i);
            }
        });
        return view;

    }

}
