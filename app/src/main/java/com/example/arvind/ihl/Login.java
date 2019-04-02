package com.example.arvind.ihl;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import layout.Home;

public class Login extends AppCompatActivity {


    TextView signup;

    SharedPreferences.Editor editor;

    SharedPreferences pref;

    ProgressDialog progressDialog;

    EditText emailEditText, passwordEditText;
    Button loginButton;
    TextView tiptext,tittle_text;

    RelativeLayout rellay1, rellay2;

    public String URL_LOGIN="https://phseven.herokuapp.com/users/login";

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            tiptext.setVisibility(View.INVISIBLE);
            tittle_text.setVisibility(View.INVISIBLE);
            rellay1.setVisibility(View.VISIBLE);
            rellay2.setVisibility(View.VISIBLE);
        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        pref = getApplicationContext().getSharedPreferences("userid", 0);


        signup=(TextView)findViewById(R.id.signup);
        tiptext=(TextView)findViewById(R.id.tiptext);
        tittle_text=(TextView)findViewById(R.id.tittle_text);
        emailEditText = (EditText)findViewById(R.id.email);
        passwordEditText = (EditText)findViewById(R.id.password);
        loginButton = (Button)findViewById(R.id.login);

        progressDialog=new ProgressDialog(Login.this);


        rellay1 = (RelativeLayout) findViewById(R.id.rellay1);
        rellay2 = (RelativeLayout) findViewById(R.id.rellay2);

        List<String> tips = new ArrayList<String>();

        tips.add("This six-ingredient breakfast is high in fiber and protein.");
        tips.add("When it comes to weight loss, you know just about everything you’re supposed to remove from your life ");
        tips.add("Many people believe that eating healthy foods has to be expensive ");

        Random rand = new Random();

        int n = rand.nextInt(2);

        tiptext.setText(tips.get(n));

        handler.postDelayed(runnable, 3000); //2000 is the timeout for the splash

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(TextUtils.isEmpty(emailEditText.getText().toString())) {
                   emailEditText.setError("number required");
//                    // Toast.makeText(getContext(), "insufficient credentials!", Toast.LENGTH_SHORT).show();
                   return;
               }
//                }
//                if((emailEditText.getText().toString().length()!=10)){
//                    emailEditText.setError("Invalid Mobile number");
//                    //Toast.makeText(getApplicationContext(), "insufficient credentials", Toast.LENGTH_SHORT).show();
//                    return;
//                }
                if( TextUtils.isEmpty(passwordEditText.getText().toString())) {
                    passwordEditText.setError("password required");
                    //Toast.makeText(getContext(), "insufficient credentials!", Toast.LENGTH_SHORT).show();
                    return;
                }

                    progressDialog.setMessage("Logging In...");
                    progressDialog.show();
//                }

                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                checkLogin(email, password);
                //to do code
            }
        });



        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), Signup.class);
                startActivity(intent);
            }
        });

    }

    private void checkLogin(final String phoneNumber, final String password) {

        Log.d("tag","insidemethod");
        // Tag used to cancel the request
        String tag_string_req = "req_login";


        // Post params to be sent to the server

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("emailid", phoneNumber);
        params.put("user_password",password);

        Log.d("res",phoneNumber+" "+password);

        JsonObjectRequest strReq = new JsonObjectRequest(URL_LOGIN, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //create a login session
                            // session.setLogin(true);
                            String res = response.toString();
                            if(res.contains("login")){

                                Log.d("res",response.toString());

                                String uid=response.getString("login");

                                SharedPreferences.Editor editor = getSharedPreferences("login_pref", MODE_PRIVATE).edit();
                                editor.putString("uid", uid);
                                editor.apply();

                                Log.d("login",uid);



                                progressDialog.dismiss();

                         // SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE)


                                //  MobileNumber.userMobileNumber=phoneNumber;
                              //  Toast.makeText(getApplicationContext(), "Login Success!", Toast.LENGTH_SHORT).show();

                                Intent intent=new Intent(Login.this,MainActivity.class);
                                startActivity(intent);
                                finish();

                                //   scrollView.setVisibility(View.GONE);
                                emailEditText.setText("");
                                passwordEditText.setText("");

                                //scrollView.setVisibility(View.GONE);
                                emailEditText.setText("");
                                passwordEditText.setText("");
                            }
                            else{
//                                Intent intent=new Intent(Login.this,MainActivity.class);
//                                startActivity(intent);
//                                finish();
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
                Intent intent=new Intent(Login.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

// add the request object to the queue to be executed
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);





    }
}
