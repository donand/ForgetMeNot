package com.forgetmenot.forgetmenot;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class VisualSearch extends AppCompatActivity {
    ImageView scegliImmagine;
    Button cerca;
    private static final int CAMERA_REQUEST = 1888;
    boolean immagineScelta = false;
    private String photoPath;
    File f;
    TextView testo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visual_search);
        testo =(TextView)this.findViewById(R.id.text);
        this.scegliImmagine = (ImageView) this.findViewById(R.id.scegli);
        cerca = (Button) this.findViewById(R.id.cerca);

        cerca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (immagineScelta) {
                    Uri uri = Uri.parse("file://"+photoPath);
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("message/rfc822");
                    i.putExtra(Intent.EXTRA_SUBJECT, "Scopri Pianta");
                    i.putExtra(Intent.EXTRA_EMAIL, new String[]{"forgetmenot.team@gmail.com"});
                    i.putExtra(Intent.EXTRA_STREAM, uri);
                    i.putExtra(Intent.EXTRA_TEXT, "Ciao mi piacerebbe conoscere di che pianta si tratta");
                    try {
                        startActivity(Intent.createChooser(i, "Scopri pianta"));
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(VisualSearch.this, "Attenzione non sono stati trovati Email Client su questo telefono", Toast.LENGTH_SHORT).show();
                    }


                } else {
                    Toast.makeText(getApplicationContext(), "Attenzione Selezionare prima una foto", Toast.LENGTH_LONG).show();
                }
            }

        });

        scegliImmagine.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            f = savebitmap(photo);

            photoPath = getRealPathFromURI(getImageUri(getApplicationContext(),photo));
            scegliImmagine.setImageBitmap(photo);
            immagineScelta = true;
            testo.setText("Cliccando su scopri puoi inviare al nostro team un email con la foto della pianta che vuoi cercare. Risponderemo in pochi minuti.");

        }
    }


    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        //inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    private File savebitmap(Bitmap bmp) {
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        OutputStream outStream = null;
        File file = new File(extStorageDirectory + ".png");
        if (file.exists()) {
            file.delete();
            file = new File(extStorageDirectory + ".png");
        }

        try {
            outStream = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 500, outStream);
            outStream.flush();
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return file;
    }
}






