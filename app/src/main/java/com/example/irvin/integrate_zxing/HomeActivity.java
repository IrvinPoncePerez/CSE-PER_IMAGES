package com.example.irvin.integrate_zxing;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.content.Intent;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import android.content.Context;
import android.widget.TextView;


public class HomeActivity extends ActionBarActivity {

    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayUseLogoEnabled(true); //Sirve
        getSupportActionBar().setDisplayShowHomeEnabled(true); //Sirve
        getSupportActionBar().setIcon(R.mipmap.calvario_logo); //Sirve

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
        //if (id == R.id.action_settings) {
          //  return true;
        //}

        return false;//super.onOptionsItemSelected(item);
    }

    public void btnScan_onClick(View view){
        IntentIntegrator integrator = new IntentIntegrator(HomeActivity.this);
        integrator.addExtra("SCAN_WIDTH", 640);
        integrator.addExtra("SCAN_HEIGHT", 480);
        //tegrator.addExtra("SCAN_MODE", "QR_CODE_MODE,PRODUCT_MODE,");
        integrator.addExtra("SCAN_FORMATS", "CODE_39");

        integrator.addExtra("PROMPT_MESSAGE", "Scanner Start!");
        integrator.initiateScan(IntentIntegrator.ALL_CODE_TYPES);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(R.string.app_name);
        String content = "";

        if (result != null) {
            String contents = result.getContents();
            if (contents != null) {
                alertDialogBuilder.setMessage(result.toString());
                content = result.getContents();
            } else {
                alertDialogBuilder.setMessage("Don't result scanner");
            }

            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

            TextView textView = (TextView)findViewById(R.id.txvBarcode);
            textView.setText(content);


//            TextView textView = new TextView(this);
//            textView.setTextSize(40);
//            textView.setText(content);
//
//            this.setContentView(textView);

        }

    }


}
