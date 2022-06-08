package com.example.newsapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;

public class userProfile extends AppCompatActivity {
    TextView Fullname,Username,emailID;

    private String strJson;
    private static final String apiUrl = "http://172.20.10.3/newsapp/fetchImg.php";


    private OkHttpClient client;
    private Response response;
    private RequestBody requestBody;
    private Request request;
    private String username;
    private String password;
//    private ProgressDialog progressDialog;
    public boolean userLoggedin = false;
    private ImageView HomePage,ProfilePage,AddFile,profilePic;
    private Button LogoutBtn;
    private ListView lv;


    SharedPreferences sharedPreferences;
    public static final String filename = "login";
    public static final String Pref_Username = "username";
    public static final String Pref_Password = "password";
    public static Context context;
    private static String user_name[];
    private  static String img[];


    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

//        setUserTypeOnButtonClick();
//        progressDialog = new ProgressDialog(this);
//        progressDialog.setTitle("Please wait...");
//        progressDialog.setCanceledOnTouchOutside(false);
        Fullname = findViewById(R.id.fullname);
        Username = findViewById(R.id.username);
        emailID = findViewById(R.id.emailID);
        HomePage = findViewById(R.id.homePage1);
        ProfilePage = findViewById(R.id.ProfilePage1);
        LogoutBtn = findViewById(R.id.logoutbtn);
        AddFile = findViewById(R.id.AddFile);
        profilePic = findViewById(R.id.profilePic);
        lv = findViewById(R.id.lv01);




            sharedPreferences = getSharedPreferences(filename, Context.MODE_PRIVATE);
            username = sharedPreferences.getString(Pref_Username,"");
            password = sharedPreferences.getString(Pref_Password,"");

                if (!sharedPreferences.contains(Pref_Username)) {
                    Intent i = new Intent(userProfile.this, login.class);
                    startActivity(i);
                } else {

//                    Intent i = new Intent(userProfile.this, addFile.class);
//                    startActivity(i);
                }






//        progressDialog.show();
        client = new OkHttpClient();
        try {

//            Bundle extra = getIntent().getExtras();
//            if (extra != null) {
//                username = extra.getString("username");
//                password = extra.getString("password");


                  Handler handler = new Handler(Looper.myLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            String[] field = new String[2];
                            field[0] = "username";
                            field[1] = "password";
                            String[] data = new String[2];
                            data[0] = username;
                            data[1] = password;


                            PutData putData = new PutData("http://172.20.10.3/newsapp/profileData.php", "POST", field, data);
                            putData.startPut();
                            if (putData.onComplete()) {
                                String result = putData.getResult();
                                updateUserData(result);
                            }

                        }
                    });
//                }

        }catch (Exception e){
            e.printStackTrace();
        }


        HomePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(userProfile.this,MainActivity.class);
                startActivity(intent);
            }
        });
        LogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();
                Intent i = new Intent(getApplicationContext(),login.class);
                startActivity(i);
                finish();

            }
        });
        AddFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),addFile.class);
                startActivity(i);
            }
        });
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),updateProfilepic.class);
                startActivity(i);
            }
        });
        try {

            Handler handler = new Handler(Looper.myLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    String[] field = new String[2];
                    field[0] = "username";
                    field[1] = "password";
                    String[] data = new String[2];
                    data[0] = username;
                    data[1] = password;


                    PutData putData = new PutData("http://172.20.10.3/newsapp/fetchImg.php", "POST", field, data);
                    putData.startPut();
                    if (putData.onComplete()) {
                        String result = putData.getResult();
                        Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();
//                        fetch_data_into_array(lv);
                        fetchData(result);

                    }

                }
            });
//                }

        }catch (Exception e){
            e.printStackTrace();
        }



    }
    public void fetchData(String result){
        try {
            JSONArray ja = new JSONArray(result);
            JSONObject jo = null;

            user_name = new String[ja.length()];
            img = new String[ja.length()];

            for (int i = 0; i < ja.length(); i++) {
                jo = ja.getJSONObject(i);
                user_name[i] = jo.getString("username");;
                img[i] ="http://172.20.10.3/newsapp/images/" + jo.getString("photos");;
            }

            userProfile.myadapter adptr = new userProfile.myadapter(getApplicationContext(), user_name, img);
            lv.setAdapter(adptr);

        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(), "here....", Toast.LENGTH_LONG).show();

        }
    }


    class myadapter extends ArrayAdapter<String>
    {
        Context context;
        String ttl[];
        String rimg[];

        myadapter(Context c, String ttl[], String rimg[])
        {
            super(c,R.layout.row,R.id.tv1,ttl);
            context=c;
            this.ttl=ttl;
            this.rimg=rimg;
        }
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
        {
            LayoutInflater inflater=(LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row=inflater.inflate(R.layout.row,parent,false);

            ImageView img=row.findViewById(R.id.img1);
            TextView tv1=row.findViewById(R.id.tv1);



            tv1.setText(ttl[position]);

            String url=rimg[position];


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



    private  void updateUserData(String strJson){
        try {
                JSONArray parent = new JSONArray(strJson);
                JSONObject child = parent.getJSONObject(0);

                String full_name = child.getString("fullname");
                String user_name = child.getString("username");
                String email_Id = child.getString("email");
                String profile_Pic = child.getString("profilePicture");

                Fullname.setText(full_name);
                Username.setText(user_name);
                emailID.setText(email_Id);
                uploadImg(profile_Pic);

//                progressDialog.hide();

        }catch (JSONException  e){
            e.printStackTrace();
        }
    }
    private  void uploadImg(String result) {
        String img = "http://172.20.10.3/newsapp/images/"+result;
        context = getApplicationContext();
        Glide.with(userProfile.context).load(img).into(profilePic);

    }


}