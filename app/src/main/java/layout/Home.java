package layout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.internal.NavigationMenu;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.arvind.ihl.AppController;
import com.example.arvind.ihl.CameraActivity;
import com.example.arvind.ihl.Comment_Data;
import com.example.arvind.ihl.HeartRate;
import com.example.arvind.ihl.List_Data;
import com.example.arvind.ihl.Login;
import com.example.arvind.ihl.MainActivity;
import com.example.arvind.ihl.MyAdapter;
import com.example.arvind.ihl.MyInterface;
import com.example.arvind.ihl.NewPost;
import com.example.arvind.ihl.R;
import com.example.arvind.ihl.StepCounter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.markushi.ui.CircleButton;
import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;

import static android.content.Context.MODE_PRIVATE;

public class Home extends Fragment implements MyInterface {


    private static final String URL_POST = "https://phseven.herokuapp.com/users/display_allposts";
    private RecyclerView rv;
    private List<List_Data>list_data;
    private MyAdapter adapter;




    private List<Comment_Data>comment_data;



    public Home() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.fragment_home, container, false);





        //newpost=(CircleButton)v.findViewById(R.id.newpost);

        /*newpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(),NewPost.class);
                startActivity(intent);
            }
        });*/


        FabSpeedDial fab = (FabSpeedDial) v.findViewById(R.id.fabutton);
        fab.setMenuListener(new SimpleMenuListenerAdapter() {
            @Override
            public boolean onPrepareMenu(NavigationMenu navigationMenu) {
                // TODO: Do something with yout menu items, or return false if you don't want to show them
                return true;
            }
            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {


                //Toast.makeText(getContext(),menuItem.getTitle(),Toast.LENGTH_LONG).show();

                if(menuItem.getTitle().equals("new post")){

                    Intent intent=new Intent(getContext(),NewPost.class);
                    startActivity(intent);

                }

                else if(menuItem.getTitle().equals("new activty")){

                }

                else if(menuItem.getTitle().equals("calculate calories")){

                    Intent intent=new Intent(getContext(),CameraActivity.class);
                    startActivity(intent);

                }
                else if(menuItem.getTitle().equals("Step counter")){

                    Intent intent=new Intent(getContext(),StepCounter.class);
                    startActivity(intent);

                }
                else if(menuItem.getTitle().equals("Calculate Heart beat")){

                    Intent intent=new Intent(getContext(),HeartRate.class);
                    startActivity(intent);

                }
                else if(menuItem.getTitle().equals("VR Game")){

                    Intent launchIntent = getActivity().getPackageManager().getLaunchIntentForPackage("com.zeroG.run7");
                    if (launchIntent != null) {
                        startActivity(launchIntent);//null pointer check in case package name was not found
                    }
                }



                return true;
            }
        });



        rv=(RecyclerView)v.findViewById(R.id.recycler_view);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        list_data=new ArrayList<>();
        adapter=new MyAdapter(list_data,getContext());

        comment_data=new ArrayList<>();



        getData();



        return v;
    }

    @Override
    public void fragmentNowVisible() {

    }

    private void getData() {

        String userid="-1";

        SharedPreferences pref = getContext().getSharedPreferences("login_pref", 0);

        SharedPreferences.Editor editor = pref.edit();

        userid=pref.getString("uid", null);

        Log.d("Shared",userid);


        String tag_string_req = "req_login";

        // int userid=12;



        HashMap<String, String> params = new HashMap<String, String>();
        params.put("userid",userid);


        JsonObjectRequest strReq = new JsonObjectRequest(URL_POST, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //create a login session
                            JSONArray  jsonArray=response.getJSONArray("result");

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
                                    String uid = msg.getString(1);
                                    Log.d("uid",uid);
                                    String imglink = msg.getString(2);
                                    Log.d("imglink",imglink);
                                    String posttext = msg.getString(3);
                                    Log.d("posttext",posttext);
                                    String timestamp = msg.getString(4);
                                    Log.d("posttext",posttext);
                                    String likecount = msg.getString(5);
                                    Log.d("likecount",likecount);



                                    JSONArray cmtArray=msg.getJSONArray(6);


                                    for(int k=0;k<cmtArray.length();k++){

                                        JSONArray commentArray=cmtArray.getJSONArray(k);


                                        String name=commentArray.getString(0);
                                        Log.d("cmtANme",name);
                                        String comment=commentArray.getString(1);
                                        Log.d("cmt",comment);

                                        Comment_Data cd=new Comment_Data(name,comment);
                                        comment_data.add(cd);
                                    }

                                    List_Data ld=new List_Data(uid,imglink,posttext,likecount,username,timestamp);
                                    list_data.add(ld);


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            rv.setAdapter(adapter);


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