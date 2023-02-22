/*
package com.microsoft.AzureIntelligentServicesExample.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.microsoft.AzureIntelligentServicesExample.R;
import com.microsoft.AzureIntelligentServicesExample.detectionview.FaceTrackerActivity;
import com.microsoft.AzureIntelligentServicesExample.preferences.SqliteDB;

import org.json.JSONException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

*/
/**
 * Created by Sumayya Munir on 11/9/2016.
 *//*


public class AlertDialogClass extends Activity {
    AlertDialog dialog;
    EditText name;
    EditText relation;
    SqliteDB myDatabase;
    String path;
    Bitmap  ownerImage;
    String ownerImageUrl = "";
    ImageView image;


    public static int OWNER_IMAGE = 101;
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.dialog_box);
        myDatabase=new SqliteDB(this);
        final Button save = (Button) findViewById(R .id.button1);
        final Button cancel = (Button) findViewById(R.id.button3);
        final Button button1 = (Button) findViewById(R.id.button2);
        final Button button2 = (Button) findViewById(R.id.button4);
        image=(ImageView) findViewById(R.id.imageView3);
        name=(EditText) findViewById(R.id.editText);
        relation=(EditText) findViewById(R.id.editText2);
            save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                String name_entered= name.getText().toString();
                String relation_entered= relation.getText().toString();
                if(!(name_entered.trim().length()==0)&&!(relation_entered.trim().length()==0)){
                    if(ownerImage!=null){
                        myDatabase.addProfile(name_entered, relation_entered);

                        Intent i = new Intent(AlertDialogClass.this, FaceTrackerActivity.class);
                        startActivity(i);
                        save.setPressed(true);
                        image.setImageBitmap(null);
                    }
                    else{
                        Toast.makeText(AlertDialogClass.this,"take picture to add your profile",Toast.LENGTH_SHORT).show();
                    }


                }
                else {
                    Toast.makeText(AlertDialogClass.this,"Enter your name or relation",Toast.LENGTH_SHORT).show();
                }

            }
        });
        button2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                name.setText("");



            }
        });
        button1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                relation.setText("");


            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                Intent i=new Intent(AlertDialogClass.this,FaceTrackerActivity.class);
                startActivity(i);
                cancel.setPressed(true);

            }
        });
    }

    public void capture(View v){
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, OWNER_IMAGE);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OWNER_IMAGE) {

            Bundle extras = data.getExtras();
            ownerImage = (Bitmap) extras.get("data");
            image.setImageBitmap(ownerImage);

        }

    }

}
*/
