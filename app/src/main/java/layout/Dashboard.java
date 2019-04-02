package layout;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.internal.NavigationMenu;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.arvind.ihl.CameraActivity;
import com.example.arvind.ihl.HeartRate;
import com.example.arvind.ihl.MyInterface;
import com.example.arvind.ihl.NewPost;
import com.example.arvind.ihl.R;
import com.example.arvind.ihl.StepCounter;
import com.example.arvind.ihl.Survey;

import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;

public class Dashboard extends Fragment implements MyInterface {


    String spval;



    public Dashboard() {
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
        View view =  inflater.inflate(R.layout.fragment_dashboard, container, false);

        FabSpeedDial fab = (FabSpeedDial) view.findViewById(R.id.fabutton);





        fab.setMenuListener(new SimpleMenuListenerAdapter() {
            @Override
            public boolean onPrepareMenu(NavigationMenu navigationMenu) {
                // TODO: Do something with yout menu items, or return false if you don't want to show them
                return true;
            }
            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {
                //  Toast.makeText(getContext(),menuItem.getTitle(),Toast.LENGTH_LONG).show();

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



        return view;

    }




    @Override
    public void fragmentNowVisible() {
        Log.d("Debug", "Dashboard visible");
    }
}