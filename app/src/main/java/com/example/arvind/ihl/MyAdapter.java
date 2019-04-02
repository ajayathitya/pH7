package com.example.arvind.ihl;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static java.security.AccessController.getContext;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private List<List_Data>list_data;
    private Context context;

    public MyAdapter(List<List_Data> list_data, Context context) {
        this.list_data = list_data;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.data_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        List_Data listData=list_data.get(position);

        if (listData
                .getImage_url().isEmpty()) {
            holder.img.setImageResource(R.drawable.logo2);
        } else{
            Picasso.with(context)
                    .load(listData
                            .getImage_url()).centerCrop().fit()
                    .into(holder.img);
        }



        holder.txtname.setText(listData.getName());
        holder.username.setText(listData.getUsername());
        holder.timestamp.setText(listData.getTimestamp());

        Log.d("holder",listData.getName());

    }

    @Override
    public int getItemCount() {
        return list_data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView img;
        private TextView txtname,username,timestamp;
        private Button cmtbtn,likebtn;
        private CardView card;
        public ViewHolder(View itemView) {
            super(itemView);
            img=(ImageView)itemView.findViewById(R.id.image_view);
            txtname=(TextView)itemView.findViewById(R.id.text_name);
            username=(TextView)itemView.findViewById(R.id.username);
            timestamp=(TextView)itemView.findViewById(R.id.timestamp);
            cmtbtn=(Button)itemView.findViewById(R.id.cmtbtn);
            likebtn=(Button)itemView.findViewById(R.id.likebtn);
            card=(CardView)itemView.findViewById(R.id.card);
            cmtbtn.setOnClickListener(this);
            likebtn.setOnClickListener(this);
            card.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if (v.getId() == cmtbtn.getId()){

                int pos = getAdapterPosition();

                // Comment_Data commentData=comment_data.get(pos);

                //    Toast.makeText(context,commentData.getComment(),Toast.LENGTH_LONG).show();

                onShowPopup(v);

            }
            else if (v.getId() == likebtn.getId()){

                likebtn.setBackground(ContextCompat.getDrawable(context, R.drawable.ic_thumb_up_blue_24dp));

                int pos = getAdapterPosition();

                Toast.makeText(context,"like"+pos,Toast.LENGTH_LONG).show();

            }
            else if (v.getId() == card.getId()){


                int pos = getAdapterPosition();

                // check if item still exists
                Toast.makeText(context,"card clicked"+pos,Toast.LENGTH_LONG).show();

            }
        }

    }

    public void onShowPopup(View v){

        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // inflate the custom popup layout
        View inflatedView = layoutInflater.inflate(R.layout.popup_layout, null, false);
        // find the ListView in the popup layout
        ListView listView = (ListView)inflatedView.findViewById(R.id.commentsListView);
        LinearLayout headerView = (LinearLayout)inflatedView.findViewById(R.id.headerLayout);
        // get device size
        Display display =  ((Activity)context).getWindowManager().getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);
//        mDeviceHeight = size.y;
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;


        // fill the data to the list items
        setSimpleList(listView);


        // set height depends on the device size
        PopupWindow popWindow = new PopupWindow(inflatedView, width, height - 50, true);
        // set a background drawable with rounders corners
        // popWindow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.popup_bg));

        popWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);


        // show the popup at bottom of the screen and set some margin at bottom ie,
        popWindow.showAtLocation(v, Gravity.BOTTOM, 0,100);
    }
    void setSimpleList(ListView listView){

        ArrayList<String> contactsList = new ArrayList<String>();

        for (int index = 0; index < 10; index++) {
            contactsList.add("I am @ index " + index + " today " + Calendar.getInstance().getTime().toString());
        }

//        listView.setAdapter(new ArrayAdapter<String>(getContext(),
//                R.layout.popup_list_item, android.R.id.text1, contactsList));


    }

}