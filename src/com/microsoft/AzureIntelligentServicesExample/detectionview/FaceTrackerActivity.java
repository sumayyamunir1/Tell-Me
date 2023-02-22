/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.microsoft.AzureIntelligentServicesExample.detectionview;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.gson.Gson;
import com.google.vrtoolkit.cardboard.CardboardActivity;
import com.google.vrtoolkit.cardboard.CardboardView;
import com.google.vrtoolkit.cardboard.EyeTransform;
import com.google.vrtoolkit.cardboard.HeadTransform;
import com.google.vrtoolkit.cardboard.Viewport;
import com.microsoft.AzureIntelligentServicesExample.R;
import com.microsoft.AzureIntelligentServicesExample.api.DetectionApi;
import com.microsoft.AzureIntelligentServicesExample.api.ImageDescriptionApi;
import com.microsoft.AzureIntelligentServicesExample.api.ImageHelper;
import com.microsoft.AzureIntelligentServicesExample.api.ImageRecognizeApi;
import com.microsoft.AzureIntelligentServicesExample.camera.CameraSourcePreview;
import com.microsoft.AzureIntelligentServicesExample.camera.GraphicOverlay;
import com.microsoft.AzureIntelligentServicesExample.interfaces.Callback;
import com.microsoft.AzureIntelligentServicesExample.interfaces.CallbackDetection;
import com.microsoft.AzureIntelligentServicesExample.location.GPSTracker;
import com.microsoft.AzureIntelligentServicesExample.preferences.Common;
import com.microsoft.AzureIntelligentServicesExample.preferences.SharedPreferenceManager;
import com.microsoft.AzureIntelligentServicesExample.preferences.SqliteDB;
import com.microsoft.AzureIntelligentServicesExample.utils.ConnectivityReceiver;
import com.microsoft.AzureIntelligentServicesExample.utils.GrayScaleImage;
import com.microsoft.AzureIntelligentServicesExample.utils.TellMeConstants;
import com.microsoft.projectoxford.vision.contract.AnalysisResult;
import com.microsoft.projectoxford.vision.contract.Caption;
import com.microsoft.projectoxford.vision.contract.Line;
import com.microsoft.projectoxford.vision.contract.OCR;
import com.microsoft.projectoxford.vision.contract.Region;
import com.microsoft.projectoxford.vision.contract.Word;
import com.microsoft.speech.tts.Synthesizer;
import com.microsoft.speech.tts.Voice;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.microedition.khronos.egl.EGLConfig;

/**
 * Activity for the face tracker app.  This app detects faces with the rear facing camera, and draws
 * overlay graphics to indicate the position, size, and ID of each face.
 */
public final class FaceTrackerActivity extends CardboardActivity implements CardboardView.StereoRenderer {
    List<com.microsoft.projectoxford.face.contract.Face> faces;
    private static final String TAG = "FaceTracker";
    private CameraSource mCameraSource = null;
    boolean detectedResults = false;
    private CameraSourcePreview mPreview;
    private GraphicOverlay mGraphicOverlay;
    ByteArrayInputStream inputStream;
    private static final int RC_HANDLE_GMS = 9001;
    private static final int RC_HANDLE_CAMERA_PERM = 2;
    static int x = 0;
    private Synthesizer m_syn;
    String description_Results;
    String v3 ;
    String text_result = "";
    List<com.microsoft.projectoxford.face.contract.Face> detectedFaces;
    SqliteDB myDatabase;
    String path;
    Bitmap bitmapImage;
    ArrayList<String> retured_paths;
    GPSTracker gps;
    //private GoogleApiClient client;
    String previous_address;
    Bitmap text_bitmap;
    int xDim, yDim;
    Bitmap new_bitmap;
    GrayScaleImage grayScale_obj;
    private TextToSpeech texttospeech;
    SparseArray<Face> offlineFaces;
    boolean isConnected;
    SharedPreferenceManager manager;
    /**
     * Initializes the UI and initiates the creation of a face detector.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.main);
        myDatabase = new SqliteDB(this);
        gps = new GPSTracker(FaceTrackerActivity.this);

        retured_paths = new ArrayList<String>();
        faces = new ArrayList<com.microsoft.projectoxford.face.contract.Face>();
        mPreview = (CameraSourcePreview) findViewById(R.id.preview);
        mGraphicOverlay = (GraphicOverlay) findViewById(R.id.faceOverlay);
        grayScale_obj = new GrayScaleImage();
        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource();
        } else {
            requestCameraPermission();
        }
        //Text to Speech Recognition
        TextToSpeech();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
       // client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        /////////Text to Speech Recognition offline mode///////////
        texttospeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    texttospeech.setLanguage(Locale.US);
                }
            }
        });


    }

    private void TextToSpeech() {
        if (getString(R.string.t_to_s_subscription_key).startsWith("Please")) {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.add_subscription_key_tip_title))
                    .setMessage(getString(R.string.add_subscription_key_tip))
                    .setCancelable(false)
                    .show();
        } else {

            if (m_syn == null) {
                // Create Text To Speech Synthesizer.
                m_syn = new Synthesizer("clientid", getString(R.string.t_to_s_subscription_key));
            }
            m_syn.SetServiceStrategy(Synthesizer.ServiceStrategy.AlwaysService);
            Voice v = new Voice("en-US", "Microsoft Server Speech Text to Speech Voice (en-US, ZiraRUS)", Voice.Gender.Female, true);
            m_syn.SetVoice(v, null);
        }
    }
    private void checkConnection() {
        isConnected = ConnectivityReceiver.isConnected(this);
        Log.e("isConnected", isConnected + "");

    }
    /**
     * Handles the requesting of the camera permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */
    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }
        final Activity thisActivity = this;
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };
        Snackbar.make(mGraphicOverlay, R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();
    }

    /**
     * Creates and starts the camera.  Note that this uses a higher resolution in comparison
     * to other detection examples to enable the barcode detector to detect small barcodes
     * at long distances.
     */
    private void createCameraSource() {

        Context context = getApplicationContext();
        FaceDetector detector = new FaceDetector.Builder(context)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();
        detector.setProcessor(
                new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory())
                        .build());
        if (!detector.isOperational()) {
            // Note: The first time that an app using face API is installed on a device, GMS will
            // download a native library to the device in order to do detection.  Usually this
            // completes before the app is run for the first time.  But if that download has not yet
            // completed, then the above call will not detect any faces.
            //
            // isOperational() can be used to check if the required native library is currently
            // available.  The detector will automatically become operational once the library
            // download completes on device.
            Log.w(TAG, "Face detector dependencies are not yet available.");
        }
        mCameraSource = new CameraSource.Builder(context, detector)
                .setRequestedPreviewSize(640, 480)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedFps(30.0f)
                .setAutoFocusEnabled(true)
                .build();

    }

    /**
     * Restarts the camera.
     */
    @Override
    protected void onResume() {
        super.onResume();

        startCameraSource();
    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        mPreview.stop();
    }

    /**
     * Releases the resources associated with the camera source, the associated detector, and the
     * rest of the processing pipeline.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraSource != null) {
            mCameraSource.release();
        }
    }

    /**
     * CallbackDetection for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }
        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            // we have permission, so create the camerasource
            createCameraSource();
            return;
        }
        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Face Tracker sample")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show();
    }

    //==============================================================================================
    // Camera Source Preview
    //==============================================================================================

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() {

        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }
        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    CameraSource.PictureCallback mCall = new CameraSource.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes) {
            FileOutputStream outStream = null;
            try {
                faces = new ArrayList<com.microsoft.projectoxford.face.contract.Face>();
                v3="";
                text_result = "";
                x++;
                path = Environment.getExternalStorageDirectory() + "/Images" + x + ".jpg";
                outStream = new FileOutputStream(path);
                outStream.write(bytes);
                outStream.close();
                Uri myUri = Uri.parse("file://" + path);
                bitmapImage = ImageHelper.loadSizeLimitedBitmapFromUri(
                        myUri, getContentResolver());
                // Put the image into an input stream for detection.
                final ByteArrayOutputStream output = new ByteArrayOutputStream();

                bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, output);
                inputStream = new ByteArrayInputStream(output.toByteArray());

                //// for text recognition convert captured image in grayscale
                text_bitmap = grayScale_obj.convertColorIntoBlackAndWhiteImage(bitmapImage);

                if (isConnected) {
                    ///////////####################Online Mode####################//////////
                    manager = SharedPreferenceManager.getSharedPreferenceInstance(FaceTrackerActivity.this);
                    ///////////////////// GPS LOCATION/////////////////////
                    if (manager.read(TellMeConstants.KEY_PREF_LOCATION, true)) {
                        gps.getLocation();
                        double latitude = gps.getLatitude();
                        double longitude = gps.getLongitude();
                        String address = gps.findLocation(latitude, longitude);
                        previous_address = Common.GetPreferences("address", FaceTrackerActivity.this);
                        if (!previous_address.equalsIgnoreCase(address)) {
                            Log.e("address", address);
                            Log.e("previous address ", previous_address);
                            //   Toast.makeText(getApplicationContext(), "Your Location is " + address, Toast.LENGTH_LONG).show();
                            m_syn.SpeakToAudio(address);
                        }
                        Common.SavePreferences("address", address, FaceTrackerActivity.this);
                    }
                    ///////////////Read Text from image using OCR/////////////
                    if (manager.read(TellMeConstants.KEY_PREF_TEXT, true)) {
                        doRecognize(text_bitmap);
                    }
                    ///////////////detect faces ///////////

                    if (manager.read(TellMeConstants.KEY_PREF_FACE, true)) {
                        doDetect(inputStream);
                    }
                    //////////////Describe my surroundings////////////
                    if (manager.read(TellMeConstants.KEY_PREF_DESCRIBE, true)) {
                        doDescribe(bitmapImage);
                    }
                    // ****************************************************************************//
                }
                if(!isConnected){
                    ////////////////////////********Offline mode**************///////////////////////

                    manager = SharedPreferenceManager.getSharedPreferenceInstance(FaceTrackerActivity.this);

                   // Detect faces offline mode
                    if (manager.read(TellMeConstants.KEY_PREF_FACE, true)) {
                        FaceDetector detector = new FaceDetector.Builder(getApplicationContext())
                                .setTrackingEnabled(false)
                                .build();
                        // Create a frame from the bitmap and run face detection on the frame.
                        Frame frame = new Frame.Builder().setBitmap(bitmapImage).build();
                        offlineFaces = detector.detect(frame);
                        if(offlineFaces.size()>2){
                            texttospeech.speak(" I see a group of" + " " + offlineFaces.size() + " " + "people ",TextToSpeech.QUEUE_ADD,null);
                            detector.release();
                        }
                        else if(offlineFaces.size()<2&&offlineFaces.size()>0
                                ){
                            texttospeech.speak(" I see " + " " + offlineFaces.size() + " " + "person in front of me ",TextToSpeech.QUEUE_ADD,null);
                            detector.release();
                        }
                        else
                            texttospeech.speak(" no face detected ",TextToSpeech.QUEUE_ADD,null);

                    }
                      /*if (manager.read(TellMeConstants.KEY_PREF_TEXT, true)) {

                    }*/
                    ////////////////////////**********************///////////////////////
                }

            } catch (FileNotFoundException e) {
                Log.d("CAMERA", e.getMessage());
               // Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Log.d("CAMERA", e.getMessage());
               // Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
            }
        }
    };



    public void doRecognize(Bitmap bitmapImage) {
        try {
            new ImageRecognizeApi(this, new Callback<String>() {
                @Override
                public void call(String data) {

                    Gson gson = new Gson();
                    OCR r = gson.fromJson(data, OCR.class);


                    for (Region reg : r.regions) {
                        for (Line line : reg.lines) {
                            for (Word word : line.words) {
                                text_result += word.text + " ";
                            }
                            text_result += "\n";
                        }
                        text_result += "\n\n";
                    }
                    if (text_result != "") {
                        Log.e("doRecognize", "doRecognize is working");

                        v3 = text_result;
                        texttospeech.speak(v3, TextToSpeech.QUEUE_ADD, null);
                       // m_syn.SpeakToAudio(v3);

                    }

                }
            }).execute(bitmapImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doDetect(final ByteArrayInputStream inputStream) {
        try {
            new DetectionApi(this, new CallbackDetection<com.microsoft.projectoxford.face.contract.Face>() {
                @Override
                public void call(com.microsoft.projectoxford.face.contract.Face[] result) {

                    String sentence = "";

                    String detectionResult;
                    Log.e("detection", "detection");


                    if (result != null) {
                        // Show the detailed list of original faces.
                        detectionResult = result.length + " face"
                                + (result.length != 1 ? "s" : "") + " detected";
                        detectedFaces = Arrays.asList(result);
                        for (com.microsoft.projectoxford.face.contract.Face face : detectedFaces) {

                            faces.add(face);
                        }
                        Log.e("faces_list", faces.size() + "");
                        Log.e("detectedFaces", detectedFaces.size() + "");
                        if (detectedFaces.size() <= 2 && detectedFaces.size() != 0) {
                            for (int i = 0; i < detectedFaces.size(); i++) {
                                String mood = "";

                                if ((detectedFaces.get(i).faceAttributes.smile >= 0.7) && (detectedFaces.get(i).faceAttributes.glasses.toString().equalsIgnoreCase("ReadingGlasses"))) {
                                    mood = "happy";
                                    sentence = "I see a " + mood + " " + detectedFaces.get(i).faceAttributes.gender + " " + detectedFaces.get(i).faceAttributes.age + " years old with glasses ";
                                    double age=detectedFaces.get(i).faceAttributes.age;

                                   // m_syn.SpeakToAudio(sentence);
                                    texttospeech.speak(sentence, TextToSpeech.QUEUE_ADD, null);
                                    Log.e("sentence", sentence);

                                } else if ((detectedFaces.get(i).faceAttributes.smile >= 0.7) && (detectedFaces.get(i).faceAttributes.glasses.toString().equalsIgnoreCase("NoGlasses"))) {
                                    mood = "happy";
                                    sentence = " I see a " + mood + " " + detectedFaces.get(i).faceAttributes.gender + " " + detectedFaces.get(i).faceAttributes.age + " years old with no glasses";
                                   // m_syn.SpeakToAudio(sentence);
                                    texttospeech.speak(sentence, TextToSpeech.QUEUE_ADD, null);
                                    Log.e("sentence", sentence);
                                } else if ((detectedFaces.get(i).faceAttributes.smile < 0.2) && (detectedFaces.get(i).faceAttributes.glasses.toString().equalsIgnoreCase("ReadingGlasses"))) {

                                    sentence = "I see a " + " " + detectedFaces.get(i).faceAttributes.gender + " " + detectedFaces.get(i).faceAttributes.age + " years old with glasses ";
                                   // m_syn.SpeakToAudio(sentence);
                                    texttospeech.speak(sentence, TextToSpeech.QUEUE_ADD, null);
                                    Log.e("sentence", sentence);
                                } else {
                                    sentence = " I see a " + detectedFaces.get(i).faceAttributes.gender + " " + detectedFaces.get(i).faceAttributes.age + " years old";
                                    //m_syn.SpeakToAudio(sentence);
                                    texttospeech.speak(sentence, TextToSpeech.QUEUE_ADD, null);
                                    Log.e("sentence", sentence);
                                }

                            }
                            detectedResults = true;
                        } else if (detectedFaces.size() > 2) {
                            sentence = " I see a group of" + " " + detectedFaces.size() + " " + "people ";
                           // m_syn.SpeakToAudio(sentence);
                            texttospeech.speak(sentence, TextToSpeech.QUEUE_ADD, null);
                            Log.e("sentence group", sentence);

                        } else if (detectedFaces.size() == 0) {
                            detectedResults = true;
                        }

                    }
                }


            }).execute(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void doDescribe(Bitmap bitmapImage) {
        try {
            new ImageDescriptionApi(this, new Callback<String>() {
                @Override
                public void call(String data) {
                    if (data != null && !data.isEmpty()) {
                        if (v3.equals("")) {
                            if (detectedResults == true) {
                                Gson gson = new Gson();
                                AnalysisResult descriptionResults = gson.fromJson(data, AnalysisResult.class);


                                for (Caption caption : descriptionResults.description.captions) {

                                    description_Results = caption.text + "\n";
                                    Log.e("description_Results", description_Results + "");
                                   // Toast.makeText(FaceTrackerActivity.this, "description_Results value" + description_Results, Toast.LENGTH_LONG).show();
                                    if (description_Results.contains("alcohol")) {
                                        description_Results = description_Results.replace("of alcohol", " ");
                                        Log.e("description_Results", description_Results + "");
                                        texttospeech.speak(description_Results, TextToSpeech.QUEUE_ADD, null);
                                       // m_syn.SpeakToAudio(description_Results);
                                    } else if (description_Results.contains("blender")) {
                                        description_Results = description_Results.replace("blender", "bottle");
                                        Log.e("description_Results", description_Results + "");
                                        texttospeech.speak(description_Results, TextToSpeech.QUEUE_ADD, null);
                                      //  m_syn.SpeakToAudio(description_Results);
                                    } else if (description_Results.contains("bathroom")) {
                                        description_Results = "I see a door in front of me";
                                        texttospeech.speak(description_Results, TextToSpeech.QUEUE_ADD, null);
                                      //  m_syn.SpeakToAudio(description_Results);

                                    } else if (description_Results.contains("tie")) {
                                        description_Results = description_Results.replace("a man wearing a shirt and tie", "a man wearing a shirt");
                                        texttospeech.speak(description_Results, TextToSpeech.QUEUE_ADD, null);
                                        //m_syn.SpeakToAudio(description_Results);
                                    } else if (description_Results.contains("white toilet") || description_Results.contains("coffee mug")) {
                                        description_Results = "I see a coffee cup ";
                                        texttospeech.speak(description_Results, TextToSpeech.QUEUE_ADD, null);
                                        //m_syn.SpeakToAudio(description_Results);
                                    } else {
                                        texttospeech.speak(description_Results, TextToSpeech.QUEUE_ADD, null);
                                        //m_syn.SpeakToAudio(description_Results);
                                    }
                                }
                            }
                        }


                    }


                }
            }).execute(bitmapImage);
        } catch (Exception e)

        {
           // Toast.makeText(FaceTrackerActivity.this, "Exception", Toast.LENGTH_SHORT).show();
        }
    }

    // ==============================================================================================
    // CardBoard method
    //==============================================================================================

    @Override
    public void onNewFrame(HeadTransform headTransform) {

    }

    @Override
    public void onDrawEye(EyeTransform eyeTransform) {

    }

    @Override
    public void onFinishFrame(Viewport viewport) {

    }

    @Override
    public void onSurfaceChanged(int i, int i1) {

    }

    @Override
    public void onSurfaceCreated(EGLConfig eglConfig) {

    }

    @Override
    public void onRendererShutdown() {

    }

    @Override
    public void onCardboardTrigger() {
        Log.e("Trigger", " cardboard trigger");
        checkConnection();
        mCameraSource.takePicture(null, mCall);
        texttospeech.speak("wait", TextToSpeech.QUEUE_ADD, null);
        Log.i(TAG, "onRendererShutdown1");

    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("FaceTracker Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
       // client.connect();
        //AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
       // AppIndex.AppIndexApi.end(client, getIndexApiAction());
       // client.disconnect();
    }

    //==============================================================================================
    // Graphic Face Tracker
    //==============================================================================================

    /**
     * Factory for creating a face tracker to be associated with a new face.  The multiprocessor
     * uses this factory to create face trackers as needed -- one for each individual.
     */
    private class GraphicFaceTrackerFactory implements MultiProcessor.Factory<Face> {
        @Override
        public Tracker<Face> create(Face face) {
            return new GraphicFaceTracker(mGraphicOverlay);
        }
    }

    /**
     * Face tracker for each detected individual. This maintains a face graphic within the app's
     * associated face overlay.
     */
    private class GraphicFaceTracker extends Tracker<Face> {
        private GraphicOverlay mOverlay;
        private FaceGraphic mFaceGraphic;

        GraphicFaceTracker(GraphicOverlay overlay) {
            mOverlay = overlay;
            mFaceGraphic = new FaceGraphic(overlay);
            // mCameraSource.takePicture(null,mCall);
        }

        /**
         * Start tracking the detected face instance within the face overlay.
         */
        @Override
        public void onNewItem(int faceId, Face item) {
            // mCameraSource.takePicture(null,mCall);
            mFaceGraphic.setId(faceId);

        }

        /**
         * Update the position/characteristics of the face within the overlay.
         */
        @Override
        public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
            mOverlay.add(mFaceGraphic);
            mFaceGraphic.updateFace(face);
            //if(face!=null)
            //mCameraSource.takePicture(null,mCall);
                /*startService(new Intent(FaceTrackerActivity.this, CameraService.class));*/

        }

        /**
         * Hide the graphic when the corresponding face was not detected.  This can happen for
         * intermediate frames temporarily (e.g., if the face was momentarily blocked from
         * view).
         */
        @Override
        public void onMissing(FaceDetector.Detections<Face> detectionResults) {
            mOverlay.remove(mFaceGraphic);
        }

        /**
         * Called when the face is assumed to be gone for good. Remove the graphic annotation from
         * the overlay.
         */
        @Override
        public void onDone() {
            mOverlay.remove(mFaceGraphic);
        }
    }

}
