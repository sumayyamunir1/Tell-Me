package com.microsoft.AzureIntelligentServicesExample.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.microsoft.AzureIntelligentServicesExample.R;
import com.microsoft.AzureIntelligentServicesExample.detectionview.FaceTrackerActivity;


public class SplashScreen extends Activity implements
		GestureDetector.OnGestureListener,
		GestureDetector.OnDoubleTapListener,View.OnClickListener{

	//private Synthesizer m_syn;
	/*private boolean isTouchEnabled = false;
	private GestureDetectorCompat mDetector;
	private ProgressDialog mProgressDialog;*/


	// called when the activity is first created
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.splash);
		//new InitTextToSpeech().execute();

		/*mDetector = new GestureDetectorCompat(this,this);
		mDetector.setOnDoubleTapListener(this);*/

		Button btnProceed = (Button)findViewById(R.id.btn_proceed);
		Button btnSettings = (Button) findViewById(R.id.btn_settings);

		btnProceed.setOnClickListener(this);
		btnSettings.setOnClickListener(this);

	}

	/*private void initTextToSpeech() {

		if (getString(R.string.t_to_s_subscription_key).startsWith("Please")) {
			new android.support.v7.app.AlertDialog.Builder(this)
					.setTitle(getString(R.string.add_subscription_key_tip_title))
					.setMessage(getString(R.string.add_subscription_key_tip))
					.setCancelable(false)
					.show();
		} else {

			if (m_syn == null) {
				m_syn = new Synthesizer("clientid", getString(R.string.t_to_s_subscription_key));
			}
			m_syn.SetServiceStrategy(Synthesizer.ServiceStrategy.AlwaysService);

			Voice v = new Voice("en-US", "Microsoft Server Speech Text to Speech Voice (en-US, ZiraRUS)", Voice.Gender.Female, true);
			m_syn.SetVoice(v, null);
			m_syn.SpeakToAudio("To listen to the instruction tap on screen once.");
			m_syn.SpeakToAudio("To skip the instructions tap on screen twice");
		}

	}*/

	@Override
	public boolean onTouchEvent(MotionEvent event){
		//this.mDetector.onTouchEvent(event);
		// Be sure to call the superclass implementation
		return super.onTouchEvent(event);
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
		/*if(isTouchEnabled){
			m_syn.SpeakToAudio("This mobile app along with the assisting headset will help you know of following");
			delay(300);
			m_syn.SpeakToAudio("Where are you");
			delay(300);
			m_syn.SpeakToAudio("Who is around you");
			delay(300);
			m_syn.SpeakToAudio("What is around you");
			delay(300);
			m_syn.SpeakToAudio("And will also read out the text for you");
		}*/
		return true;
	}

	/*private void delay(long milliseconds){
		try {
			Thread.sleep(milliseconds);
		} catch(InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
	}*/

	@Override
	public boolean onDoubleTap(MotionEvent motionEvent) {
		/*m_syn.SpeakToAudio("Now put your mobile in the headset and operate using the two buttons on the side");
		Intent intent = new Intent(this,FaceTrackerActivity.class);
		startActivity(intent);*/
		return true;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent motionEvent) {
		return false;
	}

	@Override
	public boolean onDown(MotionEvent motionEvent) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent motionEvent) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent motionEvent) {
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent motionEvent) {

	}

	@Override
	public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
		return false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.btn_proceed:
				Intent proceed = new Intent(this,FaceTrackerActivity.class);
				startActivity(proceed);
				break;

			case R.id.btn_settings:
				Intent settings = new Intent(this,SettingsActivity.class);
				startActivity(settings);
				break;
		}
	}

	/*public class InitTextToSpeech extends AsyncTask<String, String, String> {

		public InitTextToSpeech() {
			mProgressDialog = new ProgressDialog(SplashScreen.this);
			mProgressDialog.setMessage("Initializing");
		}

		@Override
		protected String doInBackground(String... params) {
			// Get an instance of face service client to detect faces in image.

			//initTextToSpeech();
			return null;

		}

		@Override
		protected void onPreExecute() {
			mProgressDialog.show();
			//addLog("Request: Detecting in image " + mImageUri);*//*
		}

		@Override
		protected void onProgressUpdate(String... progress) {
			//mProgressDialog.setMessage(progress[0]);
			//setInfo(progress[0]);*//*
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			mProgressDialog.dismiss();
			isTouchEnabled = true;
		}
	}*/
}
