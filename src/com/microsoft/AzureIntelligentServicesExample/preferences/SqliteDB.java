package com.microsoft.AzureIntelligentServicesExample.preferences;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Sumayya Munir on 11/9/2016.
 */

public class SqliteDB extends SQLiteOpenHelper{
    //SQLiteDatabase myDatabase;
    public static final String DATABASE_NAME = "Tell_Me.db";
    public static final String TABLE_NAME = "PersonInfo";
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String RELATIONSHIP = "relationship";
    public static final String PATH = "path";

    public SqliteDB(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase myDatabase) {
        myDatabase.execSQL(
                "create table PersonInfo " +
                        "(id integer primary key, name text,relationship text,path text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase myDatabase, int i, int i1) {
        myDatabase.execSQL("DROP TABLE IF EXISTS books");
        this.onCreate(myDatabase);
    }

    public void addProfile (String name, String relationship) {
        SQLiteDatabase myDatabase=this.getWritableDatabase();
       // SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("relationship", relationship);
        myDatabase.insert("PersonInfo", null, contentValues);

    }
    public ArrayList<String> getProfile(){
        SQLiteDatabase myDatabase=this.getReadableDatabase();
        int i=0;
        Cursor cursor = myDatabase.rawQuery("select * from PersonInfo;",null);
        String data = "ID:\tName";
        ArrayList<String> path_List=new ArrayList<String>();



        if(cursor.moveToFirst()){

            do{

                int index1 = cursor.getColumnIndex("id");
                int index2 = cursor.getColumnIndex("name");
                int index3=cursor.getColumnIndex("relationship");
                int index4=cursor.getColumnIndex("path");



                path_List.add(i,cursor.getString(index4));
                i++;
            }
            while(cursor.moveToNext());
            Log.e("all_paths",path_List+"");


        }
       cursor.close();
        return path_List;
    }
    public boolean checkDB(){
        SQLiteDatabase myDatabase=this.getReadableDatabase();
        String string = "SELECT count(*) FROM PersonInfo";
        Cursor mcursor = myDatabase.rawQuery(string, null);
if(mcursor!=null){
    mcursor.moveToFirst();

    int count = mcursor.getInt(0);

    if(count > 0){
        return true;
    }

}
        return false;
    }

}
