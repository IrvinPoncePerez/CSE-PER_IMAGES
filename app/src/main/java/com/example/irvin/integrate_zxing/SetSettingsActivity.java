package com.example.irvin.integrate_zxing;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.irvin.integrate_zxing.DatabaseHandler.SETTING;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;


public class SetSettingsActivity extends ActionBarActivity {

    DatabaseHandler db;
    String resultConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_settings);
        getSettings();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_set_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    public void btnSaveSetting_onClick(View view) {

        EditText txtServerAddres = (EditText) findViewById(R.id.txtServerAddres);
        EditText txtPortNumber = (EditText) findViewById(R.id.txtPortNumber);
        EditText txtInstanceName = (EditText) findViewById(R.id.txtServerServlet);

        try {
            db.updateSettings(SETTING.SERVER_ADDRESS, txtServerAddres.getText().toString());
            db.updateSettings(SETTING.PORT_NUMBER, txtPortNumber.getText().toString());
            db.updateSettings(SETTING.SERVER_SERVLET, txtInstanceName.getText().toString());

        } catch (Exception ex) {
            Log.d("ERROR_ON_SAVE", ex.getMessage());
        }

        if (testConnection() == true) {

            //Log.d("RESULT", resultConnection);

            Intent intentBack = new Intent();
            intentBack.putExtra("RESULT_CONNECTION", resultConnection);
            setResult(Activity.RESULT_OK,intentBack);

            this.finish();
        }

    }

    private void getSettings(){

        try {
            db = new DatabaseHandler(this);

            ((EditText) findViewById(R.id.txtServerAddres)).setText(db.getSetting(SETTING.SERVER_ADDRESS));
            ((EditText) findViewById(R.id.txtPortNumber)).setText(db.getSetting(SETTING.PORT_NUMBER));
            ((EditText) findViewById(R.id.txtServerServlet)).setText(db.getSetting(SETTING.SERVER_SERVLET));

        } catch (Exception ex) {
            Log.d("ERROR ON_CREATE", ex.getMessage());
        }
    }

    private boolean testConnection(){

        Boolean value;
        String stringURL = "http://" + db.getSetting(SETTING.SERVER_ADDRESS) +
                     ":" + db.getSetting(SETTING.PORT_NUMBER) +
                     "/" + db.getSetting(SETTING.SERVER_SERVLET) +
                     "/TestConnectionServlet";

        Log.d("STRING_URL", stringURL);

        try {
            value = (new DoTestConnection().execute(stringURL).get());
        } catch (InterruptedException e) {
            Log.d("InterruptedException", e.getMessage());
            value = false;
        } catch (ExecutionException e) {
            Log.d("ExecutionException", e.getMessage());
            value = false;
        }
        Log.d("VALUE", value.toString());

        return value;
    }

    private class DoTestConnection extends AsyncTask<String, Integer, Boolean>{

//        ProgressDialog mProgressDialog;

//        @Override
//        protected void onPreExecute(){
//            super.onPreExecute();
//
//            mProgressDialog = new ProgressDialog(SetSettingsActivity.this);
//            mProgressDialog.setTitle("Setting Connection");
//            mProgressDialog.setMessage("Connecting...");
//            mProgressDialog.setIndeterminate(false);
//            mProgressDialog.show();
//        }


        @Override
        protected Boolean doInBackground(String... params) {
            URL url = null;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(params[0]);
            } catch (MalformedURLException e) {
                Log.d("MalformedURLException", e.getMessage());
                return false;
            }

            try {
                urlConnection = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                Log.d("IOException", e.getMessage());
                urlConnection.disconnect();
                return false;
            } catch (Exception ex){
                Log.d("Exception", ex.getMessage());
                urlConnection.disconnect();
                return false;
            }

            InputStream inputStream = null;
            try {
                inputStream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null){
                    Log.d("LINE", line);
                    builder.append(line);
                }

                try {
                    JSONObject jsonObject = new JSONObject(builder.toString());
                    resultConnection = jsonObject.getString("status");
                } catch (JSONException e) {
                    Log.d("JSONException", e.getMessage());
                }


            } catch (IOException e) {
                Log.d("GET_INPUT_STREAM", e.getMessage());
                urlConnection.disconnect();
                return false;
            } finally {
                urlConnection.disconnect();
            }

            return true;
        }
    }

}
