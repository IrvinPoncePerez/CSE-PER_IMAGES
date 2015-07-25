package com.example.irvin.integrate_zxing;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.content.Intent;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import android.content.Context;
import android.widget.EditText;
import android.widget.Toast;

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

public class HomeActivity extends ActionBarActivity {

    private static final int STATIC_SETTINGS_VALUE = 1;
    private static final int STATIC_SEARCH_VALUE = 2;
    final Context context = this;
    DatabaseHandler db;
    JSONObject jsonObject = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayUseLogoEnabled(true); //Sirve
        getSupportActionBar().setDisplayShowHomeEnabled(true); //Sirve
        getSupportActionBar().setIcon(R.mipmap.calvario_logo); //Sirve
        db = new DatabaseHandler(this);
        setTitle("El Calvario");

        setContentView(R.layout.activity_home);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            Intent intent = new Intent(this, SetSettingsActivity.class);
            startActivityForResult(intent, STATIC_SETTINGS_VALUE);

        } else if (id == R.id.action_scanner) {
//        if (id == R.id.action_scanner){
            IntentIntegrator integrator = new IntentIntegrator(HomeActivity.this);
            integrator.addExtra("SCAN_WIDTH", 600);
            integrator.addExtra("SCAN_HEIGHT", 400);
            integrator.addExtra("SCAN_FORMATS", "CODE_39");
            integrator.initiateScan(IntentIntegrator.ALL_CODE_TYPES);
        }

        return super.onOptionsItemSelected(item);
    }

    public void btnSearch_onClick(View view){

        EditText txtBarcodeResult = (EditText)findViewById(R.id.txtBarcodeResult);
        String text = txtBarcodeResult.getText().toString();

        if (!text.equals("")) {
            if (Integer.parseInt(text) > 0) {
                Intent intent = new Intent(this, ShowResultActivity.class);
                Boolean value = false;
                try {
                    value = new DoDownload().execute(Integer.parseInt(text)).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                if (value == true) {
                    try {
                        intent.putExtra("EMPLOYEE_NUMBER", jsonObject.getString("employee_number"));
                        intent.putExtra("EMPLOYEE_NAME", jsonObject.getString("employee_name"));
                        intent.putExtra("DEPARTMENT", jsonObject.getString("department"));
                        intent.putExtra("JOB", jsonObject.getString("job"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    jsonObject = null; //agregada hoy;
                    startActivityForResult(intent, STATIC_SEARCH_VALUE);
                } else {
                    Toast.makeText(this.getBaseContext(), getResources().getString(R.string.no_connectivity), Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        switch (requestCode){
            case (STATIC_SETTINGS_VALUE):{
                if(resultCode == Activity.RESULT_OK){
                    String str = intent.getStringExtra("RESULT_CONNECTION");

                    Toast.makeText(this.getBaseContext(), str, Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case (STATIC_SEARCH_VALUE):{
                if(resultCode == Activity.RESULT_OK){

                }
                break;
            }
            default: {
                IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setTitle(R.string.app_name);
                String content = "0";

                if (result != null) {
                    String contents = result.getContents();
                    if (contents != null) {

                        content = result.getContents();

                    } else {

                        alertDialogBuilder.setMessage(getString(R.string.no_result));
                        alertDialogBuilder
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();

                    }

                    EditText editText = (EditText) findViewById(R.id.txtBarcodeResult);
                    content = String.valueOf(Integer.parseInt(content));
                    editText.setText(content);
                }
            }
        }

    }

    public class DoDownload extends AsyncTask<Integer, Integer, Boolean> {

        ProgressDialog progressDialog = null;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            Log.d("ON_PRE_EXECUTE", "SI");
            progressDialog = ProgressDialog.show(HomeActivity.this,
                                                 getString(R.string.wait_searching),
                                                 getString(R.string.searching), false, false);
        }

        @Override
        protected Boolean doInBackground(Integer... params) {
            Log.d("DO_IN_BACKGROUND", "SI");
            URL url = null;
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            String stringURL ="http://" + db.getSetting(DatabaseHandler.SETTING.SERVER_ADDRESS) +
                    ":" + db.getSetting(DatabaseHandler.SETTING.PORT_NUMBER) +
                    "/" + db.getSetting(DatabaseHandler.SETTING.SERVER_SERVLET) +
                    "/GetPictureServlet?E=" + String.valueOf(params[0]);

            try{
                url = new URL(stringURL);
                urlConnection = (HttpURLConnection) url.openConnection();
            } catch (MalformedURLException e){
                Log.d("MalformedURLException", e.getMessage());
                return false;
            } catch (IOException e) {
                Log.d("urlConnection EX", e.getMessage());
                return false;
            }

            try{
                inputStream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder builder = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null){
                    builder.append(line);
                }

                jsonObject = new JSONObject(builder.toString());
                urlConnection.disconnect();
            } catch (IOException e) {
                Log.d("inputStream EX", e.getMessage());
                return false;
            } catch (JSONException e) {
                Log.d("jsonObject EX", e.getMessage());
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean){
            progressDialog.dismiss();
            Log.d("ON_POST_EXECUTE", "SI");
        }

    }

}
