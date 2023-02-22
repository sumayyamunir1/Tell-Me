package com.microsoft.AzureIntelligentServicesExample.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.microsoft.AzureIntelligentServicesExample.utils.TellMeConstants;

public class SharedPreferenceManager {

    static SharedPreferenceManager sessionManager = null;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    public SharedPreferenceManager(Context context) {
        mPreferences = context.getSharedPreferences(TellMeConstants.APP_PREFERENCE_NAME, PreferenceUtils.PRIVATE_MODE);
        mEditor = mPreferences.edit();
        mEditor.apply();
    }

    public static SharedPreferenceManager getSharedPreferenceInstance(Context context) {
        if (sessionManager == null)
            sessionManager = new SharedPreferenceManager(context);
        return sessionManager;
    }

    public SharedPreferences getSharedPreferences() {
        return mPreferences;
    }

    public void clearPrefsData() {
        mEditor.clear();
        mEditor.commit();
    }

    public String read(String valueKey, String valueDefault) {
        return mPreferences.getString(valueKey, valueDefault);
    }

    public void save(String valueKey, String value) {
        mEditor.putString(valueKey, value);
        mEditor.commit();
    }

    public int read(String valueKey, int valueDefault) {
        return mPreferences.getInt(valueKey, valueDefault);
    }

    public void save(String valueKey, int value) {
        mEditor.putInt(valueKey, value);
        mEditor.commit();
    }

    public boolean read(String valueKey, boolean valueDefault) {
        return mPreferences.getBoolean(valueKey, valueDefault);
    }

    public void save(String valueKey, boolean value) {
        mEditor.putBoolean(valueKey, value);
        mEditor.commit();
    }

    public long read(String valueKey, long valueDefault) {
        return mPreferences.getLong(valueKey, valueDefault);
    }

    public void save(String valueKey, long value) {
        mEditor.putLong(valueKey, value);
        mEditor.commit();
    }

    public float read(String valueKey, float valueDefault) {
        return mPreferences.getFloat(valueKey, valueDefault);
    }

    public void save(String valueKey, float value) {
        mEditor.putFloat(valueKey, value);
        mEditor.commit();
    }
}
