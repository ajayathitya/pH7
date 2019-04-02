package com.example.arvind.ihl;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;

public class ActivityDashboard extends AppCompatActivity {

    int ste,tim;
    float fste,ftim;
    double spe,dis,cal;

    String URL_ACT="";

    TextView st,se,ca,di,sp;
    Button addStepstoDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Intent intent = getIntent();
        String steps = intent.getStringExtra("steps");
        String sec = intent.getStringExtra("sec");

        st = findViewById(R.id.times);
        se = findViewById(R.id.count);
        ca = findViewById(R.id.calo);
        di = findViewById(R.id.dist);
        sp = findViewById(R.id.spee);

        addStepstoDB=(Button)findViewById(R.id.addStepstoDB);

        ste = Integer.parseInt(steps);
        tim = Integer.parseInt(sec);

        fste = Float.parseFloat(steps);
        ftim = (float)tim;
        ftim/=3600;
        cal = fste * 0.04;
        dis = fste/1312;
        spe = dis/ftim;

        se.setText(ste+" Steps");
        st.setText(tim+" Seconds");
        ca.setText(cal+" Cal.");
        di.setText(String.format("%.2f",dis)+" KM");
        sp.setText(String.format("%.2f",spe)+" KM/h");

        addStepstoDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              //  sendToDB(ste+"",tim+"",cal+"",spe+"",di+"");

            }
        });
    }
    void sendToDB(String steps,String time,String cal,String speed,String dist){

        Log.d("tag","insidemethod");
        // Tag used to cancel the request
        String tag_string_req = "req_login";


        // Post params to be sent to the server

       HashMap<String, String> params = new HashMap<String, String>();
//        params.put("user_id", steps);
//        params.put("step_cal",Float.parseFloat(cal));
//        params.put("time",time);
//        params.put("",speed);
//        params.put("",dist);



        JsonObjectRequest strReq = new JsonObjectRequest(URL_ACT, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //create a login session
                            // session.setLogin(true);
                            String res = response.toString();
                            if(res.contains("")){

                                Log.d("res",response.toString());



                                 Toast.makeText(getApplicationContext(), "Update Success!", Toast.LENGTH_SHORT).show();
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

            }
        });

// add the request object to the queue to be executed
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);



    }
}
