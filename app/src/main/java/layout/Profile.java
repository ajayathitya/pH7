package layout;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.arvind.ihl.AppController;
import com.example.arvind.ihl.Comment_Data;
import com.example.arvind.ihl.List_Data;
import com.example.arvind.ihl.Login;
import com.example.arvind.ihl.MainActivity;
import com.example.arvind.ihl.MyInterface;
import com.example.arvind.ihl.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Profile extends Fragment implements MyInterface {


    Button share;

    String URL_PROFILE = "https://phseven.herokuapp.com/users/getprofile";

    TextView name, bmi, height, weight;


    String spval;

    public Profile() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);


        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        share = (Button) view.findViewById(R.id.share);

        name = (TextView) view.findViewById(R.id.name);
        bmi = (TextView) view.findViewById(R.id.bmi);
        height = (TextView) view.findViewById(R.id.height);
        weight = (TextView) view.findViewById(R.id.weight);


        getData();

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myintent = new Intent(Intent.ACTION_SEND);
                myintent.setType("text/plain");
                String body = "Check out pH7 app ";
                String sub = "SIH 2019";
                myintent.putExtra(Intent.EXTRA_SUBJECT, sub);
                myintent.putExtra(Intent.EXTRA_TEXT, body);
                startActivity(Intent.createChooser(myintent, "Share using"));
            }
        });


        return view;

    }


    @Override
    public void fragmentNowVisible() {
        Log.d("Debug", "Profile visible");
    }


    private void getData() {

        String userid = "-1";

        Log.d("Shared","inside");

        SharedPreferences pref = getContext().getSharedPreferences("login_pref", 0);

        SharedPreferences.Editor editor = pref.edit();

        userid = pref.getString("uid", null);

        Log.d("Shared", userid);


        Log.d("tag", "insidemethod");
        // Tag used to cancel the request
        String tag_string_req = "req_login";


        // Post params to be sent to the server

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("userid", userid);

        JsonObjectRequest strReq = new JsonObjectRequest(URL_PROFILE, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //create a login session
                            JSONArray  jsonArray=response.getJSONArray("result");

                            Log.d("resp",jsonArray.toString());

//
//                            JSONArray jsonArray= null;
//                            try {
//                                jsonArray = new JSONArray(array);
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }

                            // JSONObject jsonObject=new JSONObject(response);
                            for (int i=0; i<jsonArray.length(); i++) {
                                JSONArray msg= null;
                                try {
                                    msg = jsonArray.getJSONArray(i);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Log.d("tag",msg.toString());

                                try {
                                    String username=msg.getString(0);
                                    name.setText(username);

                                    String height1=msg.getString(1);
                                    height.setText(height1);

                                    String weight1=msg.getString(2);
                                    weight.setText(weight1);


                                    String bmi1=msg.getString(3);
                                    bmi.setText(bmi1);





                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                        } catch (Exception e) {
                            // JSON error
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
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