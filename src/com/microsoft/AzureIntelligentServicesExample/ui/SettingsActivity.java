package com.microsoft.AzureIntelligentServicesExample.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;


import com.microsoft.AzureIntelligentServicesExample.R;
import com.microsoft.AzureIntelligentServicesExample.adapter.CustomAdapter;
import com.microsoft.AzureIntelligentServicesExample.model.Setting;
import com.microsoft.AzureIntelligentServicesExample.preferences.SharedPreferenceManager;
import com.microsoft.AzureIntelligentServicesExample.utils.TellMeConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SettingsActivity extends Activity {
    private RecyclerView recyclerView;
    private CustomAdapter adapter;
    private TextToSpeech texttospeech;
    private List<Setting> settings = new ArrayList<>();
    private List<String> settingsData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final SharedPreferenceManager manager = SharedPreferenceManager.getSharedPreferenceInstance(this);
        initSettings();

        recyclerView=(RecyclerView)findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));



        texttospeech=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    texttospeech.setLanguage(Locale.US);
                }
            }
        });

        texttospeech.speak("To check options, tap once on them", TextToSpeech.QUEUE_ADD, null);
        texttospeech.speak("To select or un select them long press on option", TextToSpeech.QUEUE_ADD, null);


        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                Toast.makeText(view.getContext(), "Single Click on position :"+position,
                        Toast.LENGTH_SHORT).show();
                texttospeech.speak(settings.get(position).getName(), TextToSpeech.QUEUE_ADD, null);
            }

            @Override
            public void onLongClick(View view, int position) {
                Toast.makeText(view.getContext(), "Long press on position :"+position,
                        Toast.LENGTH_LONG).show();

                if(manager.read(settings.get(position).getName(),false)){
                    view.setBackgroundResource(R.color.white);
                    speakDisabled();
                    manager.save(settings.get(position).getName(),false);
                }else{
                    view.setBackgroundResource(R.color.colorPrimaryLight);
                    speakEnabled();
                    manager.save(settings.get(position).getName(),true);
                }
            }
        }));


        adapter=new CustomAdapter(this);

        boolean isFirstRun = manager.read("isFirstRun", false);
        if(!isFirstRun) {
            populateSettings();
            manager.save("isFirstRun",true);
        }else{
            populateSettingsFromPreferences();
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        if(texttospeech !=null){
            texttospeech.stop();
            texttospeech.shutdown();
        }
    }

    private void speakEnabled(){
        texttospeech.speak("Enabled", TextToSpeech.QUEUE_ADD, null);
    }

    private void speakDisabled(){
        texttospeech.speak("Disabled", TextToSpeech.QUEUE_ADD, null);
    }

    private void initSettings(){
        settingsData = new ArrayList<>();
        settingsData.add(TellMeConstants.KEY_PREF_LOCATION);
        settingsData.add(TellMeConstants.KEY_PREF_TEXT);
        settingsData.add(TellMeConstants.KEY_PREF_FACE);
        settingsData.add(TellMeConstants.KEY_PREF_DESCRIBE);
    }

    /* call this function only first time app gets installed, else the preference values will be used */
    private void populateSettings() {

        initSettings();
        SharedPreferenceManager manager = SharedPreferenceManager.getSharedPreferenceInstance(this);

        for(int i = 0;i<settingsData.size();i++){
            Setting setting = new Setting();
            setting.setName(settingsData.get(i));
            setting.setState(true);
            settings.add(setting);
            manager.save(settingsData.get(i),true);
        }

        adapter.setListContent(settings);
        recyclerView.setAdapter(adapter);
    }

    private void populateSettingsFromPreferences(){

        SharedPreferenceManager manager = SharedPreferenceManager.getSharedPreferenceInstance(this);

        for(int i = 0;i<settingsData.size();i++){
            Setting setting = new Setting();
            setting.setName(settingsData.get(i));
            setting.setState(manager.read(settingsData.get(i),false));
            settings.add(setting);
        }

        adapter.setListContent(settings);
        recyclerView.setAdapter(adapter);

    }

    public interface ClickListener{
        void onClick(View view, int position);
        void onLongClick(View view, int position);
    }

    /**
     * RecyclerView: Implementing single item click and long press (Part-II)
     *
     * - creating an innerclass implementing RevyvlerView.OnItemTouchListener
     * - Pass clickListener interface as parameter
     * */

    class RecyclerTouchListener implements RecyclerView.OnItemTouchListener{

        private ClickListener clicklistener;
        private GestureDetector gestureDetector;

        RecyclerTouchListener(Context context, final RecyclerView recycleView, final ClickListener clicklistener){

            this.clicklistener=clicklistener;
            gestureDetector=new GestureDetector(context,new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child=recycleView.findChildViewUnder(e.getX(),e.getY());
                    if(child!=null && clicklistener!=null){
                        clicklistener.onLongClick(child,recycleView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child=rv.findChildViewUnder(e.getX(),e.getY());
            if(child!=null && clicklistener!=null && gestureDetector.onTouchEvent(e)){
                clicklistener.onClick(child,rv.getChildAdapterPosition(child));
            }

            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }
}
