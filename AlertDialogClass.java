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

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.kairos.Kairos;
import com.kairos.KairosListener;
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

/**
 * Created by Sumayya Munir on 11/9/2016.
 */

public class AlertDialogClass extends Activity {
    AlertDialog dialog;
    EditText name;
    EditText relation;
    Button save ;
    Button clear ;
    Button cancel ;
    SqliteDB myDatabase;
    String path;
    Bitmap  ownerImage;
    Cloudinary cloudinary;
    Kairos myKairos;
    KairosListener listener;
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
        Map config = new HashMap();
        config.put("cloud_name", "fjwuantitheft");
        config.put("api_key", "794699647772398");
        config.put("api_secret", "aBQfmOOLq4nnpvplbsY-7FrA8CA");
        cloudinary = new Cloudinary(config);
        // listener
        listener = new KairosListener() {

            @Override
            public void onSuccess(String response) {
                Log.d("KAIROS DEMO", response);
               /* try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("images");
                    JSONObject completeObject = jsonArray.getJSONObject(0);
                    JSONObject transactionJsonObject = completeObject.getJSONObject("transaction");
                    Toast.makeText(getApplicationContext(), transactionJsonObject.toString() + "", Toast.LENGTH_LONG).show();

                    String status = transactionJsonObject.getString("status");
                    //String message = transactionJsonObject.getString("message");
                    String galleryName = transactionJsonObject.getString("gallery_name");

                    Toast.makeText(getApplicationContext(), status + " Face Matched" , Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/

                //Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFail(String response) {
                Log.d("KAIROS DEMO", response);

                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_LONG).show();
            }

        };
 /* * * instantiate a new kairos instance * * */
        myKairos = new Kairos();

        /* * * set authentication * * */
        String app_id = "c7974d8c";
        String api_key = "01e8f06a3c51bddeb155cbb9044c5cc2";
        myKairos.setAuthentication(this, app_id, api_key);
        save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                String name_entered= name.getText().toString();
                String relation_entered= relation.getText().toString();
                if(!(name_entered.trim().length()==0)&&!(relation_entered.trim().length()==0)){
                    if(ownerImage!=null){
                        myDatabase.addProfile(name_entered, relation_entered);
                        uploadImageAndRegisterOwner(name_entered);
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
    public void uploadImageAndRegisterOwner(final String person_name) {


        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                String imageName = "fjwu_" + person_name;
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ownerImage.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);

                byte[] bitmapdata = bos.toByteArray();
                ByteArrayInputStream bs = new ByteArrayInputStream(bitmapdata);

                //Uploading image to the cloudinary to get the publically available image
                try {
                    Log.e("name",person_name);
                    Log.e("imageName",imageName);
                    cloudinary.uploader().upload(bs, ObjectUtils.asMap("public_id", imageName));
                    Log.e("image uploaded","image uploaded to server");
                } catch (IOException e) {
                    e.printStackTrace();
                }


                ownerImageUrl = "http://res.cloudinary.com/fjwuantitheft/image/upload/v1463650737/fjwu_" + person_name + ".jpg";

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                    }
                });

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Log.e("feed", "done");

               try {

                    //  List galleries
                   //Log.e("listener",listener+"");
                    myKairos.listGalleries(listener);

                    //Enrolling the owners image to the kairos
                    String subjectId = person_name;
                    String galleryId = "Tell Me";
                    String selector = "FULL";
                    String multipleFaces = "false";
                    String minHeadScale = "0.125";
                    myKairos.enroll(ownerImageUrl,
                            subjectId,
                            galleryId,
                            selector,
                            multipleFaces,
                            minHeadScale,
                            listener);
//                            myKairos.recognize(imageUrl, galleryId, null, null, null, null, listener);
                } catch (JSONException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        String person_name = "";
                    }
                });

            }
        }.execute();

    }

}
