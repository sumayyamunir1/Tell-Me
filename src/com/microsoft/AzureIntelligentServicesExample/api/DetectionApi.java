package com.microsoft.AzureIntelligentServicesExample.api;


import android.content.Context;
import android.os.AsyncTask;

import com.microsoft.AzureIntelligentServicesExample.R;
import com.microsoft.AzureIntelligentServicesExample.interfaces.CallbackDetection;
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.contract.Face;


import java.io.InputStream;

public class DetectionApi extends AsyncTask<InputStream, String, Face[]> {
    private boolean mSucceed = true;
    private final CallbackDetection<Face> callback;
   private FaceServiceClient faceServiceClient ;
    private Exception e = null;
    public DetectionApi(Context context, CallbackDetection<Face> callback) {
        //Context context1 = context;
        if (faceServiceClient==null){

            faceServiceClient = new FaceServiceRestClient(context.getString(R.string.subscription_key_faces));
        }
        this.callback = callback;
    }
    @Override
    protected Face[] doInBackground(InputStream... params) {
        // Get an instance of face service client to detect faces in image.

        try {
            publishProgress("Detecting...");

            // Start detection.
            return faceServiceClient.detect(
                    params[0],  /* Input stream of image to detect */
                    true,       /* Whether to return face ID */
                    true,       /* Whether to return face landmarks */
                        /* Which face attributes to analyze, currently we support:
                           age,gender,headPose,smile,facialHair */
                    new FaceServiceClient.FaceAttributeType[] {
                            FaceServiceClient.FaceAttributeType.Age,
                            FaceServiceClient.FaceAttributeType.Gender,
                            FaceServiceClient.FaceAttributeType.Glasses,
                            FaceServiceClient.FaceAttributeType.Smile,
                            FaceServiceClient.FaceAttributeType.HeadPose
                    });
        } catch (Exception e) {
            mSucceed = false;
            publishProgress(e.getMessage());
           // addLog(e.getMessage());
            return null;
        }

    }

    @Override
    protected void onPreExecute() {
       /* mProgressDialog.show();
        addLog("Request: Detecting in image " + mImageUri);*/
    }

    @Override
    protected void onProgressUpdate(String... progress) {
      /*  mProgressDialog.setMessage(progress[0]);
        setInfo(progress[0]);*/
    }

    @Override
    protected void onPostExecute(Face[] result) {
        super.onPostExecute(result);
        if(result!=null )
            callback.call(result);
        else if(e!=null){
            e.printStackTrace();
        }

    }



}
