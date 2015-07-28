package com.example.irvin.integrate_zxing;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ShowResultActivity extends AppCompatActivity {

    private final static int CAMERA_CAPTURE = 1;
    private final static int PICTURE_CROP = 2;

    private String mCurrentPhotoPath;
    private String mCropCurrentPhotoPath;
    private String mDonwloadPhotoPath;
    private String mEmployeeNumber;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_result);

        mDonwloadPhotoPath = null;
        mCurrentPhotoPath = null;
        mCropCurrentPhotoPath = null;
        mEmployeeNumber = null;
        Intent intent = this.getIntent();

        TextView lblEmployeeName = (TextView)findViewById(R.id.lblEmployeeName);
        TextView lblEmployeeDepto = (TextView)findViewById(R.id.lblEmployeeDepto);
        TextView lblEmployeeJob = (TextView)findViewById(R.id.lblEmployeeJob);
        ImageView imgEmployeePicture = (ImageView)findViewById(R.id.imgEmployeePicture);

        this.setTitle(intent.getStringExtra("EMPLOYEE_NUMBER"));
        this.mEmployeeNumber = intent.getStringExtra("EMPLOYEE_NUMBER");
        lblEmployeeName.setText(intent.getStringExtra("EMPLOYEE_NAME"));
        lblEmployeeDepto.setText(intent.getStringExtra("DEPARTMENT"));
        lblEmployeeJob.setText(intent.getStringExtra("JOB"));
        this.mDonwloadPhotoPath = intent.getStringExtra("PICTURE");

        if (!mDonwloadPhotoPath.equals("")) {
            Bitmap bitmap = ImageTool.decodeSampledBitmapFromFile(Uri.parse(mDonwloadPhotoPath), 100, 100);
            BitmapDrawable bitmapDrawable = new BitmapDrawable(this.getResources(), bitmap);
            imgEmployeePicture.setImageDrawable(bitmapDrawable);
            //btnCameraCapture.setBackground(bitmapDrawable);
        }

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
                    File photoFile = ImageTool.createImageFile();
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
                BitmapDrawable bitmapDrawable = new BitmapDrawable(this.getResources(), bitmap);
                //ImageButton btnCameraPicture = (ImageButton)findViewById(R.id.btnCameraCapture);
                //btnCameraPicture.setBackground(bitmapDrawable);
                ImageView imgEmployeePicture = (ImageView)findViewById(R.id.imgEmployeePicture);
                imgEmployeePicture.setImageDrawable(bitmapDrawable);

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

                File photoFile = ImageTool.createImageFile();
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

    public void btnUpdatePicture_onClick(View view){
        if (mCropCurrentPhotoPath != null){
            new DoUpload(this).execute(mEmployeeNumber, mCropCurrentPhotoPath);
        }
    }

    public void uploadFinish(){

        this.setResult(Activity.RESULT_OK, null);
        this.finish();

    }


    public class DoUpload extends AsyncTask<String, Integer, Boolean>{

        private ProgressDialog progressDialog = null;
        private Context context;
        private DatabaseHandler db;

        public DoUpload(Activity activity){
            progressDialog = new ProgressDialog(activity);
            context = activity;
            db = new DatabaseHandler(context);
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();

            progressDialog.setTitle(getResources().getString(R.string.wait_title));
            progressDialog.setMessage(getString(R.string.upload_message));
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            Boolean result = false;
            HttpURLConnection urlConnection;
            JSONObject jsonObject = new JSONObject();
            OutputStream outputStream;
            int statusCode = 0;
            URL url;

            try {
                jsonObject.put("employee_number", params[0]);
                jsonObject.put("picture", ImageTool.convertImageToString(mCropCurrentPhotoPath));
            } catch (JSONException e) {
                Log.d("JSONException", e.getMessage());
            }

            String stringURL = "http://" + db.getSetting(DatabaseHandler.SETTING.SERVER_ADDRESS) +
                    ":" + db.getSetting(DatabaseHandler.SETTING.PORT_NUMBER) +
                    "/" + db.getSetting(DatabaseHandler.SETTING.SERVER_SERVLET) +
                    "/PostPictureServlet";

            try{
                url = new URL(stringURL);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.connect();

                outputStream = urlConnection.getOutputStream();
                OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");

                writer.write(jsonObject.toString());
                writer.flush();
                writer.close();

                statusCode = urlConnection.getResponseCode();
            } catch (MalformedURLException e) {
                Log.d("MalformedURLException", e.getMessage());
            } catch (IOException e) {
                Log.d("IOException", e.getMessage());
            }


            return statusCode == 200;
        }

        @Override
        protected void onPostExecute(Boolean value){
           if (value){
               ((ShowResultActivity)context).uploadFinish();
           } else {
               Toast.makeText(context, getString(R.string.no_updated), Toast.LENGTH_SHORT);
           }
        }


    }

}
