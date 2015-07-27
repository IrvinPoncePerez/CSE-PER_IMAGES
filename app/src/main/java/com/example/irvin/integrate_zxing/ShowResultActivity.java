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
    String mCropCurrentPhotoPath;

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

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode == RESULT_OK){
            if (requestCode == CAMERA_CAPTURE){
                try {
                    File photoFile = ImageTool.createImageFile(ImageTool.IMAGE_OPTION.CROP_PICTURE);
                    mCropCurrentPhotoPath = "file:" + photoFile.getAbsolutePath();
                    Uri uriData = Uri.parse(mCurrentPhotoPath);
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(uriData, "image/*");
                    intent.putExtra("crop", "true");
                    intent.putExtra("aspectX", 1);
                    intent.putExtra("aspectY", 1);
                    intent.putExtra("outputX", 500);
                    intent.putExtra("outputY", 500);
                    intent.putExtra("return-data", false);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));

                    startActivityForResult(intent, PICTURE_CROP);
                } catch (ActivityNotFoundException ex){
                    String errorMessage = "Whoops - your device doesn't support the crop action!";
                    Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
                    toast.show();
                } catch (IOException e) {
                    Log.d("onAction IOException", e.getMessage());
                }
            } else if (requestCode == PICTURE_CROP){
                Uri uri = data.getData();

                Bitmap bitmap = ImageTool.decodeSampledBitmapFromFile(uri, 100, 100);
                BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
                ImageButton btnCameraPicture = (ImageButton)findViewById(R.id.btnCameraCapture);
                btnCameraPicture.setBackground(bitmapDrawable);

//                BitmapFactory.Options options = new BitmapFactory.Options();
//                options.inJustDecodeBounds = true;
//                BitmapFactory.decodeFile(uri.getPath(), options);
//                int imageHeight = options.outHeight;
//                int imageWidth = options.outWidth;
//                String imageType = options.outMimeType;
            }
        }
    }

    public void btnCameraCapture_onClick(View view){
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getPackageManager()) != null){

                File photoFile = ImageTool.createImageFile(ImageTool.IMAGE_OPTION.TAKE_PICTURE);
                mCurrentPhotoPath = "file:" + photoFile.getAbsolutePath();
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(intent, CAMERA_CAPTURE);

            }
        } catch (ActivityNotFoundException ex) {
            String errorMessage = "Whoops - your device doesn't support capturing images!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        } catch (IOException e) {
            Log.d("Camera IOException", e.getMessage());
        }
    }



}
