package com.example.arvind.ihl;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;

public class Signup extends AppCompatActivity {

    TextView signup;

    EditText emailEditText, passEditText,nameEditText;
    Button registerButton;

    public String URL_REG="https://phseven.herokuapp.com/users/register";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        signup=(TextView)findViewById(R.id.signup);
        emailEditText = (EditText)findViewById(R.id.emailEditText);
        passEditText = (EditText)findViewById(R.id.passEditText);
        nameEditText = (EditText)findViewById(R.id.nameEditText);
        registerButton = (Button)findViewById(R.id.registerButton);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if(TextUtils.isEmpty(emailEditText.getText().toString())){
//                    emailEditText.setError("number required");
//                    // Toast.makeText(getContext(), "insufficient credentials!", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if((emailEditText.getText().toString().length()!=10)){
//                    emailEditText.setError("Invalid Mobile number");
//                    //Toast.makeText(getApplicationContext(), "insufficient credentials", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if( TextUtils.isEmpty(passwordEditText.getText().toString())){
//                    passwordEditText.setError("password required");
//                    //Toast.makeText(getContext(), "insufficient credentials!", Toast.LENGTH_SHORT).show();
//                    return;
//                }
                String email = emailEditText.getText().toString();
                String password = passEditText.getText().toString();
                String username=nameEditText.getText().toString();
                checkLogin(email, password,username);
                //to do code
            }
        });


    }

    private void checkLogin(final String phoneNumber, final String password,final  String username) {

        Log.d("tag","insidemethod");
        // Tag used to cancel the request
        String tag_string_req = "req_login";


// Post params to be sent to the server
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("username",username);
        params.put("emailid", phoneNumber);
        params.put("user_password",password);

        JsonObjectRequest strReq = new JsonObjectRequest(URL_REG, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("TAG", "reg Response: " + response.toString());
                        try {
                            //create a login session
                            // session.setLogin(true);
                          //  String res = response.toString();

                            Log.d("TAG", "obj val: " + response.get("ID"));

                            if(response.get("ID").equals(null)){

                                Toast.makeText(getApplicationContext(), "Signup Error", Toast.LENGTH_SHORT).show();

                                //     SharedPreferences pref = getActivity().getSharedPreferences("MyPref", Context.MODE_PRIVATE)


                                //  MobileNumber.userMobileNumber=phoneNumber;

                            }
                            else{

                                Toast.makeText(getApplicationContext(), "Signup Success!", Toast.LENGTH_SHORT).show();

                                Intent intent=new Intent(Signup.this,Login.class);
                                startActivity(intent);

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
