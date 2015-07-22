package com.example.irvin.integrate_zxing;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import com.example.irvin.integrate_zxing.DatabaseHandler.SETTING;

//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;

public class SetSettingsActivity extends ActionBarActivity {

    DatabaseHandler db;

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
        EditText txtInstanceName = (EditText) findViewById(R.id.txtInstanceName);
        EditText txtUserName = (EditText) findViewById(R.id.txtUserName);
        EditText txtPassword = (EditText) findViewById(R.id.txtPassword);

        try {
            db.updateSettings(SETTING.SERVER_ADDRESS, txtServerAddres.getText().toString());
            db.updateSettings(SETTING.PORT_NUMBER, txtPortNumber.getText().toString());
            db.updateSettings(SETTING.INSTANCE_NAME, txtInstanceName.getText().toString());
            db.updateSettings(SETTING.USERNAME, txtUserName.getText().toString());
            db.updateSettings(SETTING.PASSWORD, txtPassword.getText().toString());
        } catch (Exception ex) {
            Log.d("ERROR_ON_SAVE", ex.getMessage());
        }



        this.fileList();
    }

    private void getSettings(){
        EditText txtServerAddres = ((EditText) findViewById(R.id.txtServerAddres));
        EditText txtPortNumber = (EditText) findViewById(R.id.txtPortNumber);
        EditText txtInstanceName = (EditText) findViewById(R.id.txtInstanceName);
        EditText txtUserName = (EditText) findViewById(R.id.txtUserName);
        EditText txtPassword = (EditText) findViewById(R.id.txtPassword);

        try {
            db = new DatabaseHandler(this);

            ((EditText) findViewById(R.id.txtServerAddres)).setText(db.getSetting(SETTING.SERVER_ADDRESS));
            ((EditText) findViewById(R.id.txtPortNumber)).setText(db.getSetting(SETTING.PORT_NUMBER));
            ((EditText) findViewById(R.id.txtInstanceName)).setText(db.getSetting(SETTING.INSTANCE_NAME));
            ((EditText) findViewById(R.id.txtUserName)).setText(db.getSetting(SETTING.USERNAME));
            ((EditText) findViewById(R.id.txtPassword)).setText(db.getSetting(SETTING.PASSWORD));

        } catch (Exception ex) {
            Log.d("ERROR ON_CREATE", ex.getMessage());
        }
    }

//    private Boolean testConnection(){

//        Connection connection = null;
//        Statement statement = null;
//        ResultSet resultSet = null;
//
//        try {
//            Class.forName("oracle.jdbc.driver.OracleDrive");
//            String url = "jdbc:oracle:thin:@" + db.getSetting(SETTING.SERVER_ADDRESS) +
//                    ":" + db.getSetting(SETTING.PORT_NUMBER) +
//                    ":" + db.getSetting(SETTING.INSTANCE_NAME);
//
//            Log.d("URL", url);
//
//            connection = DriverManager.getConnection(url,
//                    db.getSetting(SETTING.USERNAME),
//                    db.getSetting(SETTING.PASSWORD));
//
//            connection = DriverManager.getConnection(url);
//
//            statement = connection.createStatement();
//            resultSet = statement.executeQuery("SELECT DESC_CONNECTION FROM TEST_CONNECTION");
//
//            Log.d("ON_TEST_CONNECTION", "on test connection");
//            while (resultSet.next()){
//                Log.d("RESULT_TEST_CONNECTION", resultSet.getString(0));
//            }
//        } catch (ClassNotFoundException ex) {
//            Log.d("CLASSNOTFOUNDEXCEPTION", ex.getMessage());
//            return false;
//        } catch (SQLException ex) {
//            Log.d("SQLEXCEPTION", ex.getMessage());
//            return false;
//        }
//        return true;
//    }
}
