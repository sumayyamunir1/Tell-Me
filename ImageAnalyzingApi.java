package com.microsoft.AzureIntelligentServicesExample.api;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.microsoft.AzureIntelligentServicesExample.R;
import com.microsoft.AzureIntelligentServicesExample.interfaces.Callback;
import com.microsoft.projectoxford.vision.VisionServiceClient;
import com.microsoft.projectoxford.vision.VisionServiceRestClient;
import com.microsoft.projectoxford.vision.contract.AnalysisResult;
import com.microsoft.projectoxford.vision.rest.VisionServiceException;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageAnalyzingApi extends AsyncTask<Bitmap, String, String> {

    private final Callback<String> callback;
    private VisionServiceClient client ;
    private Exception e = null;

    public ImageAnalyzingApi(Context context, Callback<String> callback) {
        //Context context1 = context;
        if (client==null){
            client = new VisionServiceRestClient(context.getString(R.string.subscription_key));
        }
        this.callback = callback;
    }

    /*public void getApiClient(){
        if (client==null){
            client = new VisionServiceRestClient(context.getString(R.string.subscription_key));
        }
    }*/

    @Override
    protected String doInBackground(Bitmap... args) {
        try {
            return process(args[0]);
        } catch (Exception e) {
            this.e = e;    // Store error
        }

        return null;
    }

    @Override
    protected void onPostExecute(String data) {
        super.onPostExecute(data);
        if(data!=null && !data.isEmpty())
            callback.call(data);
        else if(e!=null){
            callback.call(e.toString());
        }
        // Display based on error existence

    }

    private String process(Bitmap bitmap) throws VisionServiceException, IOException {
        Gson gson = new Gson();
        String[] features = {"ImageType", "Color", "Faces", "Adult", "Categories"};
        String[] details = {};

        // Put the image into an input stream for detection.
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());
        Log.e("inputStream",inputStream+"");
        Log.e("features",features+"");
        Log.e("details",details+"");

        AnalysisResult v = this.client.analyzeImage(inputStream, features, details);

        String result = gson.toJson(v);
        Log.d("result", result);

        return result;
    }

}
