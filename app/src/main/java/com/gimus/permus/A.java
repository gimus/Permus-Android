package com.gimus.permus;

import android.app.Activity;
import android.app.Application;
        import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;

import com.gimus.permus.api.client.Client;
import com.gimus.permus.api.model.Lega;
import com.gimus.permus.api.model.Subject;
import com.gimus.permus.api.model.SystemInfo;

public class A extends Application {
    private static Application app;
    public static A a;
    public static Client client;
    public static Subject subject;
    public static Lega lega;
    public static SystemInfo systemInfo;
    public Activity currentActivity;
    public MainActivity mainActivity;

    public static Application getApplication() {
        return app;
    }

    public static Context getContext() {
        return getApplication().getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app= this;
        a=this;
        handler.sendMessageDelayed(handler.obtainMessage(), 2*R.integer.tick);
//        String deviceId = String.valueOf( Math.abs((getAndroidDeviceId()+ "OF_COURSE_HASH_CODE_IS_MODIFIED_JUST_FOR_APP_GP-FANTA").hashCode()));
    }


    public static double getUserBalance() {
        if (systemInfo != null)
            return ((double) Math.round( A.systemInfo.requesterInfo.coinBalance*100))/100;
         else
            return 0;
    }


    public static void setSubject(Subject s){
        A.subject=s;
    }
/*
    protected String getAndroidDeviceId() {
        return  Settings.Secure.getString( getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
    }
*/
    public static String getResourceString(int id) {
        return getContext().getResources().getText(id).toString();
    }

    public static void savePreferenceString(String preferenceName, String value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(preferenceName, value); // value to store
        editor.commit();
    }

    public static String getPreferenceString(String preferenceName){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        return preferences.getString(preferenceName, "");
    }

    public Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (mainActivity !=null ) mainActivity.tick();
            handler.sendMessageDelayed( handler.obtainMessage(), 3000);
             return false;
        }
    });




}