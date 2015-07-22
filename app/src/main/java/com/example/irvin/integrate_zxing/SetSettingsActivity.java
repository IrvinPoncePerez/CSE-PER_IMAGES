package com.example.irvin.integrate_zxing;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import com.example.irvin.integrate_zxing.DatabaseHandler.SETTING;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class SetSettingsActivity extends ActionBarActivity {

    DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_settings);

        EditText txtServerAddres = (EditText) findViewById(R.id.txtServerAddres);
        EditText txtPortNumber = (EditText) findViewById(R.id.txtPortNumber);
        EditText txtInstanceName = (EditText) findViewById(R.id.txtInstanceName);
        EditText txtUserName = (EditText) findViewById(R.id.txtUserName);
        EditText txtPassword = (EditText) findViewById(R.id.txtPassword);


        try {
            db = new DatabaseHandler(this);

            txtServerAddres.setText(db.getSetting(SETTING.SERVER_ADDRESS));
            txtPortNumber.setText(db.getSetting(SETTING.PORT_NUMBER));
            txtInstanceName.setText(db.getSetting(SETTING.INSTANCE_NAME));
            txtUserName.setText(db.getSetting(SETTING.USERNAME));
            txtPassword.setText(db.getSetting(SETTING.PASSWORD));

        } catch (Exception ex) {
            Log.d("ERROR ON_CREATE", ex.getMessage());
        }

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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void btnSaveSetting_onClick(View view) {

        Log.d("ON_CLICK", "btnSaveSetting_onClick");

        EditText txtServerAddres = (EditText) findViewById(R.id.txtServerAddres);
        EditText txtPortNumber = (EditText) findViewById(R.id.txtPortNumber);
        EditText txtInstanceName = (EditText) findViewById(R.id.txtInstanceName);
        EditText txtUserName = (EditText) findViewById(R.id.txtUserName);
        EditText txtPassword = (EditText) findViewById(R.id.txtPassword);

        Integer result;

        try {
            result = db.updateSettings(SETTING.SERVER_ADDRESS, txtServerAddres.getText().toString());
            Log.d("UPDATING_SERVER_ADDRESS", result.toString());
            result = db.updateSettings(SETTING.PORT_NUMBER, txtPortNumber.getText().toString());
            Log.d("UPDATING_PORT_NUMBER", result.toString());
            result = db.updateSettings(SETTING.INSTANCE_NAME, txtInstanceName.getText().toString());
            Log.d("UPDATING_INSTANCE_NAME", result.toString());
            result = db.updateSettings(SETTING.USERNAME, txtUserName.getText().toString());
            Log.d("UPDATING_USERNAME", result.toString());
            result = db.updateSettings(SETTING.PASSWORD, txtPassword.getText().toString());
            Log.d("UPDATING_PASSWORD", result.toString());
        } catch (Exception ex) {
            Log.d("ERROR_ON_SAVE", ex.getMessage());
        }

        Connection connection;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            String url = "jdbc:oracle:thin:@" + txtServerAddres.getText().toString() +
                    ":" + txtPortNumber.getText().toString() +
                    ":" + txtInstanceName.getText().toString();

            Log.d("URL", url);

            connection = DriverManager.getConnection(url, txtUserName.getText().toString(), txtPassword.getText().toString());


        } catch (ClassNotFoundException ex) {
            Log.d("CLASSNOTFOUNDEXCEPTION", ex.getMessage());
        } catch (SQLException ex) {
            Log.d("SQLEXCEPTION", ex.getMessage());
        }


    }
}
