package com.example.newsapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.UUID;

public class addFile extends AppCompatActivity {

    ImageView imageView;
    private Uri filepath;
    private static final int STORAGE_PERMISSION_CODE = 4665;
    private int PICK_IMAGE_REQUEST = 1;
    private Bitmap bitmap;
    Button uploadBtn;
    SharedPreferences sharedPreferences;
    public static final String filename = "login";
    public static final String Pref_Username = "username";
    public static final String Pref_Password = "password";
    private String username;
    private String password;
    private String mediaPath;
    TextView pathOfImage;
    private String path;
    private String apiUrl = "http://172.20.10.3/newsapp/imageUpload.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_file);

        requestStoragePermission();
        imageView = findViewById(R.id.imageView);
        uploadBtn = findViewById(R.id.uploadbtn);
        pathOfImage = findViewById(R.id.path);
        sharedPreferences = getSharedPreferences(filename, Context.MODE_PRIVATE);
        username = sharedPreferences.getString(Pref_Username,"");
        password = sharedPreferences.getString(Pref_Password,"");

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFiles();
            }
        });

    }


    private void requestStoragePermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == STORAGE_PERMISSION_CODE){
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(getApplicationContext(),"Permission granted",Toast.LENGTH_SHORT).show();

            }else {
                Toast.makeText(getApplicationContext(),"Permisssion Declined",Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void ShowFIleChooser(){
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i,"Select Picture"),PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && data!=null && data.getData() !=null){
            filepath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filepath);
                imageView.setImageBitmap(bitmap);
                pathOfImage.setText(filepath.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public  void selectImage(View view){
        ShowFIleChooser();
    }

//    private void uploadFiles(){
//        mediaPath = getPath(filepath);
//        Handler handler = new Handler(Looper.myLooper());
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                String[] field = new String[2];
//                field[0] = "username";
//                field[1] = "media";
//                String[] data = new String[2];
//                data[0] = username;
//                data[1] =mediaPath;
//
//
//                PutData putData = new PutData("http://172.20.10.3/newsapp/userMedia.php", "POST", field, data);
//                putData.startPut();
//                if (putData.onComplete()) {
//                    String result = putData.getResult();
//                    Toast.makeText(getApplicationContext(),"Image Uploaded",Toast.LENGTH_SHORT).show();
//                    Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();
//                    Toast.makeText(getApplicationContext(),mediaPath,Toast.LENGTH_LONG).show();
//                }
//            }
//        });


//    }
    private void uploadFiles(){
        mediaPath = getPath(filepath);
        try{
            String uploadId = UUID.randomUUID().toString();
            new MultipartUploadRequest(this,uploadId,"http://172.20.10.3/newsapp/userMedia.php")
                    .addFileToUpload(mediaPath,"upload").addParameter("username",username)
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(3)
                    .startUpload();
            Toast.makeText(getApplicationContext(),"Uploaded successfully..",Toast.LENGTH_SHORT);
            uploadFilesToMediaFiles();
//            Intent i = new Intent(getApplicationContext(),MainActivity.class);
//            startActivity(i);
//            finish();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


    }
    private void uploadFilesToMediaFiles(){
//        mediaPath = getPath(filepath);
        try{
            String uploadId = UUID.randomUUID().toString();
            new MultipartUploadRequest(this,uploadId,"http://172.20.10.3/newsapp/uploadInMediaFiles.php")
                    .addFileToUpload(mediaPath,"upload").addParameter("username",username)
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(3)
                    .startUpload();
            Toast.makeText(getApplicationContext(),"Uploaded To Media successfully..",Toast.LENGTH_SHORT);
            Intent i = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(i);
            finish();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


    }

    private String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Images.Media._ID + "=?", new String[]{document_id}, null
        );

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        path = cursor.getString(column_index);
        cursor.close();
        return path;
    }
}