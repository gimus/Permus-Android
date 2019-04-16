package com.gimus.permus.api.common;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class J {
    public static JSONArray deserializeToArray(String s) {
        try {
            JSONArray j = (JSONArray) new JSONTokener(s).nextValue();
            return j;
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }

    public static JSONObject deserialize(String s) {
        try {
            JSONObject j = (JSONObject) new JSONTokener(s).nextValue();
            return j;
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }

    public static int getInt(JSONObject j, String name) {
        try {
            return j.getInt(name);
        } catch (JSONException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static double getDouble(JSONObject j, String name) {
        try {
            return j.getDouble(name);
        } catch (JSONException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static boolean getBoolean(JSONObject j, String name) {
        try {
            return j.getBoolean(name);
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getString(JSONObject j, String name) {
        try {
            return j.getString(name);
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }

    }

    public static JSONArray getArray(JSONObject j, String name) {
        try {
            return j.getJSONArray(name);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
