package com.example.newsapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private static final String apiUrl = "http://172.20.10.3/newsapp/imgForHome.php";
    ImageView HomePage,ProfilePage,AddFile,img01;
    private String username,password;
    public static final String filename = "login";
    public static final String Pref_Username = "username";
    public static final String Pref_Password = "password";
    public static Context context;
    ListView lv;
    private static String user_name[];
    private  static String img[];
    private static String img_name[];
    private String likeStatus = null;
    private boolean isliked = false;
    private int cpostion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        HomePage = findViewById(R.id.homePage);
        ProfilePage = findViewById(R.id.ProfilePage);
        AddFile = findViewById(R.id.AddFile);
        lv=  findViewById(R.id.lv);
        SharedPreferences sharedPreferences;

        try {
            sharedPreferences = getSharedPreferences(filename, Context.MODE_PRIVATE);
            username = sharedPreferences.getString(Pref_Username, "");
            password = sharedPreferences.getString(Pref_Password, "");
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        fetch_data_into_array(lv);


        ProfilePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,userProfile.class);
                startActivity(intent);
            }
        });
        AddFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),addFile.class);
                startActivity(i);
            }
        });
//        try {
//            fetchImg();
//        }catch (Exception e){
//            e.printStackTrace();
//        }

    }

    public void fetch_data_into_array(View view)
    {

        class  dbManager extends AsyncTask<String,Void,String>
        {
            protected void onPostExecute(String data)
            {
                try {
                    JSONArray ja = new JSONArray(data);
                    JSONObject jo = null;

                    user_name = new String[ja.length()];
                    img = new String[ja.length()];
                    img_name = new String[ja.length()];


                    for (int i = 0; i < ja.length(); i++) {
                        jo = ja.getJSONObject(i);
                        user_name[i] = jo.getString("username");;
                        img[i] ="http://172.20.10.3/newsapp/images/" + jo.getString("photos");
                        img_name[i] = jo.getString("photos");
                    }

                    myadapter adptr = new myadapter(getApplicationContext(), user_name, img,img_name);
                    lv.setAdapter(adptr);

                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            protected String doInBackground(String... strings)
            {
                try {
                    URL url = new URL(strings[0]);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    StringBuffer data = new StringBuffer();
                    String line;

                    while ((line = br.readLine()) != null) {
                        data.append(line + "\n");
                    }
                    br.close();

                    return data.toString();

                } catch (Exception ex) {
                    return ex.getMessage();
                }

            }

        }
        dbManager obj=new dbManager();
        obj.execute(apiUrl);

    }

    class myadapter extends ArrayAdapter<String>
    {
        Context context;
        String ttl[];
        String dsc[];
        String rimg[];
        String img_name[];

        myadapter(Context c, String ttl[], String rimg[],String img_name[])
        {
            super(c,R.layout.row,R.id.tv1,ttl);
            context=c;
            this.ttl=ttl;
            this.dsc=dsc;
            this.rimg=rimg;
            this.img_name = img_name;
        }
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
        {
            LayoutInflater inflater=(LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row=inflater.inflate(R.layout.row,parent,false);


            ImageView img=row.findViewById(R.id.img1);
            TextView tv1=row.findViewById(R.id.tv1);
            ImageView shareBtn =row.findViewById(R.id.shareBtn);
            ImageView likeBtn = row.findViewById(R.id.likeBtn);





            tv1.setText(ttl[position]);

            String url=rimg[position];
            shareBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    BitmapDrawable bitmapDrawable = (BitmapDrawable) img.getDrawable();
                    Bitmap bitmap = bitmapDrawable.getBitmap();
                    shareImageandText(bitmap);

                }
            });
            likeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    likeBtn.setImageResource(R.drawable.ic_baseline_thumb_up_24);
                    likeStatus = "liked";
                    postLike(img_name[position],likeStatus);
                }

            });
//            checkLike(img_name[position],username,position);

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    checkLike(img_name[position],username,position);

                        if(position==cpostion && isliked) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    likeBtn.setImageResource(R.drawable.ic_baseline_thumb_up_24);
                                    isliked = false;
                                }
                            });
                        }

                    else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                likeBtn.setImageResource(R.drawable.ic_outline_thumb_up_24);
                            }
                        });

                    }
                }
            };
            Thread thread = new Thread(runnable);
            thread.start();


            class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {
                private String url;
                private ImageView imageView;

                public ImageLoadTask(String url, ImageView imageView) {
                    this.url = url;
                    this.imageView = imageView;
                }

                @Override
                protected Bitmap doInBackground(Void... params) {
                    try {
                        URL connection = new URL(url);
                        InputStream input = connection.openStream();
                        Bitmap myBitmap = BitmapFactory.decodeStream(input);
                        Bitmap resized = Bitmap.createScaledBitmap(myBitmap, 400, 400, true);
                        return resized;
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                    return null;
                }
                @Override
                protected void onPostExecute(Bitmap result) {
                    super.onPostExecute(result);
                    imageView.setImageBitmap(result);
                }
            }
            ImageLoadTask obj=new ImageLoadTask(url,img);
            obj.execute();

            return row;
        }

    }
    private void postLike(String img_name,String likeStatus){

        try {

            Handler handler = new Handler(Looper.myLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    String[] field = new String[3];
                    field[0] = "username";
                    field[1] = "img_name";
                    field[2] = "likeStatus";
                    String[] data = new String[3];
                    data[0] = username;
                    data[1] = img_name;
                    data[2] = likeStatus;


                    PutData putData = new PutData("http://172.20.10.3/newsapp/likePost.php", "POST", field, data);
                    putData.startPut();
                    if (putData.onComplete()) {
                        String result = putData.getResult();
                        Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }private void checkLike(String img_name, String username ,int postion){



        try {
            cpostion = postion;
            HandlerThread handlerThread = new HandlerThread("MyHandlerThread");
            handlerThread.start();
            Looper looper = handlerThread.getLooper();
            Handler handler = new Handler(looper);
            handler.post(new Runnable() {
                @Override
                public void run() {

                            String[] field = new String[2];
                            field[0] = "username";
                            field[1] = "img_name";
                            String[] data = new String[2];
                            data[0] = username;
                            data[1] = img_name;


                            PutData putData = new PutData("http://172.20.10.3/newsapp/checkLike.php", "POST", field, data);
                            putData.startPut();
                            if (putData.onComplete()) {
                                String result = putData.getResult();
                                Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();
                                if(result.equals("liked")){
                                    isliked = true;


                                }
                            }

                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
//        return isliked;
    }
    private void shareImageandText(Bitmap bitmap) {
        Uri uri = getmageToShare(bitmap);
        Intent intent = new Intent(Intent.ACTION_SEND);

        // putting uri of image to be shared
        intent.putExtra(Intent.EXTRA_STREAM, uri);

        // adding text to share
        intent.putExtra(Intent.EXTRA_TEXT, "Sharing Image");

        // Add subject Here
        intent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");

        // setting type to image
        intent.setType("image/jpg");

        // calling startactivity() to share
        startActivity(Intent.createChooser(intent, "Share Via"));
    }
    private Uri getmageToShare(Bitmap bitmap) {
        File imagefolder = new File(getCacheDir(), "images");
        Uri uri = null;
        try {
            imagefolder.mkdirs();
            File file = new File(imagefolder, "shared_image.jpg");
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, outputStream);
            outputStream.flush();
            outputStream.close();
                uri = FileProvider.getUriForFile(this, "com.limxtop.research.fileprovider", file);
        } catch (Exception e) {
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return uri;
    }


//    private void fetchImg() {
//        Handler handler = new Handler(Looper.myLooper());
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                String[] field = new String[2];
//                field[0] = "username";
//                field[1] = "password";
//                String[] data = new String[2];
//                data[0] = username;
//                data[1] = password;
//
//
//                PutData putData = new PutData("http://172.20.10.3/newsapp/fetchImg.php", "POST", field, data);
//                putData.startPut();
//                if (putData.onComplete()) {
//                    String result = putData.getResult();
//                    Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();
//                    uploadImg(result);
//
//                }
//            }
//        });
//
//    }
//
//    private  void uploadImg(String result) {
//        String img = "http://172.20.10.3/newsapp/images/"+result;
//        context = getApplicationContext();
//        Glide.with(MainActivity.context).load(img).into(img01);
//
//    }
}