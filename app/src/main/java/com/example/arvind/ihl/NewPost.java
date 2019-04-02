package com.example.arvind.ihl;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;

import layout.Home;

public class NewPost extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1888;
    private ImageView imageView;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    public String URL_POST="https://phseven.herokuapp.com/register_posts";

    String uid;

    SharedPreferences pref;

    SharedPreferences.Editor editor;


    EditText post_content;
    Button submit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        this.imageView = (ImageView) this.findViewById(R.id.imageView1);
        Button photoButton = (Button) this.findViewById(R.id.button1);
        submit=(Button)this.findViewById(R.id.addpost);
        post_content=(EditText)findViewById(R.id.post_content);

        pref = getApplication().getSharedPreferences("userid", Context.MODE_PRIVATE);
        uid=pref.getString("userid", null);

        String postvalue=post_content.getText().toString().trim();

        String img_url="image";

        post_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPost(postvalue,img_url,uid);
            }
        });

        photoButton.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA},
                            MY_CAMERA_PERMISSION_CODE);
                } else {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new
                        Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }

        }
    }

        protected void onActivityResult( int requestCode, int resultCode, Intent data){
            if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                imageView.setImageBitmap(photo);
            }
        }


    private void addPost(final String postval, final String imgurl,final String uid) {

        Log.d("tag","insidemethod");
        // Tag used to cancel the request
        String tag_string_req = "req_login";


// Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("file_text", postval);
        params.put("url",imgurl);
        params.put("userid",uid);

        JsonObjectRequest strReq = new JsonObjectRequest(URL_POST, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //create a login session
                            // session.setLogin(true);
                            String res = response.toString();
                            if(res.contains("post_id")){

                                //     SharedPreferences pref = getActivity().getSharedPreferences("MyPref", Context.MODE_PRIVATE)

                                //  MobileNumber.userMobileNumber=phoneNumber;
                                Toast.makeText(getApplicationContext(), "Post Success!", Toast.LENGTH_SHORT).show();

                                Intent intent=new Intent(NewPost.this, Home.class);
                                startActivity(intent);
                                finish();

                                //   scrollView.setVisibility(View.GONE);

                            }
                            else{

                                Toast.makeText(getApplicationContext(), "Invalid details", Toast.LENGTH_SHORT).show();
                            }


                        } catch (Exception e) {
                            // JSON error
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());

            }
        });

// add the request object to the queue to be executed
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);





    }


}