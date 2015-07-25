package com.example.irvin.integrate_zxing;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class ShowResultActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_result);

        Intent intent = this.getIntent();

        TextView lblEmployeeName = (TextView)findViewById(R.id.lblEmployeeName);
        TextView lblEmployeeDepto = (TextView)findViewById(R.id.lblEmployeeDepto);
        TextView lblEmployeeJob = (TextView)findViewById(R.id.lblEmployeeJob);

        this.setTitle(intent.getStringExtra("EMPLOYEE_NUMBER"));
        lblEmployeeName.setText(intent.getStringExtra("EMPLOYEE_NAME"));
        lblEmployeeDepto.setText(intent.getStringExtra("DEPARTMENT"));
        lblEmployeeJob.setText(intent.getStringExtra("JOB"));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_result, menu);
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

}
