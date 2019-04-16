package com.gimus.permus.api.common;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiTask extends AsyncTask<Void, Void, String> {
    protected ApiCommand ac;
    protected ApiClient c;

    public void executeCommand(ApiClient _c, ApiCommand _ac) {
        c=_c;
        ac= _ac;
        execute();
    }

    protected void onPreExecute() {
        c.onPreExecute(this.ac);
    }

    protected String doInBackground(Void... urls) {

        try {
            URL url = new URL(ac.url);
            ac.stato=1;
            HttpURLConnection urlConnection=null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                ac.inputStream=urlConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader( ac.inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                bufferedReader.close();
                return stringBuilder.toString();
            }
            catch(Exception e) {
                ac.stato=3;
                ac.error=e.getMessage();
                Log.e("ERRORE ApiTask ", e.getMessage(), e);
                return null;
            }
            finally{
                if (urlConnection != null)
                  urlConnection.disconnect();
            }
        }
        catch(Exception e) {
            ac.stato=3;
            ac.error=e.getMessage();
            Log.e("ERRORE ApiTask ", e.getMessage(), e);
            return null;
        }
    }

    protected void onPostExecute(String response) {
        ac.result=response;
        c.onTaskExecuted(this.ac);
    }
}