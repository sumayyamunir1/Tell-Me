package com.microsoft.AzureIntelligentServicesExample.ui;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;

import com.microsoft.AzureIntelligentServicesExample.R;
import com.microsoft.AzureIntelligentServicesExample.detectionview.FaceTrackerActivity;
import com.microsoft.speech.tts.Synthesizer;
import com.microsoft.speech.tts.Voice;

public class SplashScreen extends Activity {

	private final int SPLASH_DISPLAY_LIGHT = 5000;
	private Synthesizer m_syn;
	// called when the activity is first created
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.splash);
		if (m_syn == null) {
			// Create Text To Speech Synthesizer.
			m_syn = new Synthesizer("clientid", getString(R.string.t_to_s_subscription_key));
		}
		m_syn.SetServiceStrategy(Synthesizer.ServiceStrategy.AlwaysService);
		Voice v = new Voice("en-US", "Microsoft Server Speech Text to Speech Voice (en-US, ZiraRUS)", Voice.Gender.Female, true);
		m_syn.SetVoice(v, null);
		m_syn.SpeakToAudio("Put your cell phone in a V R set and trigger the button outside V R");
		m_syn.SpeakToAudio("On button trigger this app will tell you  the current location");
		m_syn.SpeakToAudio("It will also tell People around you and what they are doing");
		/*
		 * New Handler to start the Splash screen and close this Splash-Screen
		 * after some seconds.
		 */
		new Handler().postDelayed(new Runnable() {
			
			public void run() {
				// Create an Intent that will start the Menu-Activity.
				Intent splash_screen = new Intent(SplashScreen.this,
						FaceTrackerActivity.class);
				SplashScreen.this.startActivity(splash_screen);
				SplashScreen.this.finish();

			}
		}, SPLASH_DISPLAY_LIGHT);
	}

}
