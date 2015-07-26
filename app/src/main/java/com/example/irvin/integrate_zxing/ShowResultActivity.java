package com.example.irvin.integrate_zxing;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ShowResultActivity extends AppCompatActivity {

    private final static int CAMERA_CAPTURE = 1;
    private final static int PICTURE_CROP = 2;

    String mCurrentPhotoPath;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_result);

//        Intent intent = this.getIntent();
//
//        TextView lblEmployeeName = (TextView)findViewById(R.id.lblEmployeeName);
//        TextView lblEmployeeDepto = (TextView)findViewById(R.id.lblEmployeeDepto);
//        TextView lblEmployeeJob = (TextView)findViewById(R.id.lblEmployeeJob);
//        ImageButton btnEmployeePicture = (ImageButton)findViewById(R.id.btnEmployeePicture);

//        byte[] decodeString = Base64.decode(intent.getStringExtra("PICTURE"), Base64.DEFAULT);
//        Bitmap bitmap = BitmapFactory.decodeByteArray(decodeString, 0, decodeString.length);
//        BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);

//        this.setTitle(intent.getStringExtra("EMPLOYEE_NUMBER"));
//        lblEmployeeName.setText(intent.getStringExtra("EMPLOYEE_NAME"));
//        lblEmployeeDepto.setText(intent.getStringExtra("DEPARTMENT"));
//        lblEmployeeJob.setText(intent.getStringExtra("JOB"));
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode == RESULT_OK){
            if (requestCode == CAMERA_CAPTURE){
                try {
                    Uri uriData = Uri.parse(mCurrentPhotoPath);
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(uriData, "image/*");
                    intent.putExtra("crop", "true");
                    intent.putExtra("aspectX", 1);
                    intent.putExtra("aspectY", 1);
                    intent.putExtra("outputX", 500);
                    intent.putExtra("outputY", 500);
                    intent.putExtra("return-data", true);
                    startActivityForResult(intent, PICTURE_CROP);
                } catch (ActivityNotFoundException ex){
                    String errorMessage = "Whoops - your device doesn't support the crop action!";
                    Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
                    toast.show();
                }
            } else if (requestCode == PICTURE_CROP){

            }
        }
    }

    public void btnCameraCapture_onClick(View view){
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getPackageManager()) != null){

                File photoFile = null;
                try{
                    photoFile = createImageFile();
                } catch (IOException ex){
                    Log.d("IOException", ex.getMessage());
                }

                if (photoFile != null){
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                    startActivityForResult(intent, CAMERA_CAPTURE);
                }

            }
        } catch (ActivityNotFoundException ex) {
            String errorMessage = "Whoops - your device doesn't support capturing images!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

}
