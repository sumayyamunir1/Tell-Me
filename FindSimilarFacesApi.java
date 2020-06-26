package com.microsoft.AzureIntelligentServicesExample.api;

import android.content.Context;
import android.os.AsyncTask;

import com.microsoft.AzureIntelligentServicesExample.R;
import com.microsoft.AzureIntelligentServicesExample.interfaces.SimilarFacesCallback;
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.contract.SimilarFace;


import java.util.Arrays;
import java.util.UUID;

/**
 * Created by Sumayya Munir on 11/4/2016.
 */

public class FindSimilarFacesApi extends AsyncTask<UUID, String, SimilarFace[]> {

    private boolean mSucceed = true;
    private final SimilarFacesCallback<SimilarFace> callback;
    private FaceServiceClient faceServiceClient ;
    private Exception e = null;

    public FindSimilarFacesApi(Context context, SimilarFacesCallback<SimilarFace> callback) {
        //Context context1 = context;
        if (faceServiceClient==null){

            faceServiceClient = new FaceServiceRestClient(context.getString(R.string.subscription_key_faces));
        }
      this.callback =  callback;
    }

    @Override
    protected SimilarFace[] doInBackground(UUID... params) {

        try{
            publishProgress("Finding Similar Faces...");

            UUID[] faceIds = Arrays.copyOfRange(params, 1, params.length);
            // Start find similar faces.
            return faceServiceClient.findSimilar(
                    params[0],
                    faceIds,      /* The first face ID to verify */
                    faceIds.length);     /* The second face ID to verify */
        }  catch (Exception e) {
            mSucceed = false;
            publishProgress(e.getMessage());
          //  addLog(e.getMessage());
            return null;
        }
    }

    @Override
    protected void onPreExecute() {

        //mProgressDialog.show();
    }

    @Override
    protected void onProgressUpdate(String... values) {
        // Show the status of background find similar face task on screen.
        //setUiDuringBackgroundTask(values[0]);
    }

    @Override
    protected void onPostExecute(SimilarFace[] result) {
        super.onPostExecute(result);
        if(result!=null )
            callback.call(result);
        else if(e!=null){
            e.printStackTrace();
        }

        // Show the result on screen when verification is done.

    }
}
