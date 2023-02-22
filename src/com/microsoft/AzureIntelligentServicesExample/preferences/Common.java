package com.microsoft.AzureIntelligentServicesExample.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Common 
{

static SharedPreferences pref;
private static String PREF_NAME="tell_me";
static Editor editor;
public static void SavePreferences(String key, String value,Context context)
{
	 
   pref = context.getSharedPreferences(PREF_NAME, 0);
   editor = pref.edit();

   editor.putString(key, value);
   editor.commit();
}
 
public static String GetPreferences(String key,Context context)
{
           pref = context.getSharedPreferences(PREF_NAME, 0);
           String savedPreferences = pref.getString(key, "");
 
           return savedPreferences;
     }


}
