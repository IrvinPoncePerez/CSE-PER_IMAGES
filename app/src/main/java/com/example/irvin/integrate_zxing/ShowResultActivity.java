package com.example.irvin.integrate_zxing;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;

public class ShowResultActivity extends AppCompatActivity {

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_result);

        Intent intent = this.getIntent();

        TextView lblEmployeeName = (TextView)findViewById(R.id.lblEmployeeName);
        TextView lblEmployeeDepto = (TextView)findViewById(R.id.lblEmployeeDepto);
        TextView lblEmployeeJob = (TextView)findViewById(R.id.lblEmployeeJob);
        ImageButton btnEmployeePicture = (ImageButton)findViewById(R.id.btnEmployeePicture);

//        byte[] decodeString = Base64.decode(intent.getStringExtra("PICTURE"), Base64.DEFAULT);
//        Bitmap bitmap = BitmapFactory.decodeByteArray(decodeString, 0, decodeString.length);
//        BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);

        this.setTitle(intent.getStringExtra("EMPLOYEE_NUMBER"));
        lblEmployeeName.setText(intent.getStringExtra("EMPLOYEE_NAME"));
        lblEmployeeDepto.setText(intent.getStringExtra("DEPARTMENT"));
        lblEmployeeJob.setText(intent.getStringExtra("JOB"));
//        btnEmployeePicture.setBackground(bitmapDrawable);

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
