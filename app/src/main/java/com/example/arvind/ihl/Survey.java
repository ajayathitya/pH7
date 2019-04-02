package com.example.arvind.ihl;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Survey extends AppCompatActivity {

    Button opa,opb,opc,opd,nextbtn;
    TextView question;

    static int i=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

        question=(TextView)findViewById(R.id.question);
        opa=(Button)findViewById(R.id.opa);
        opb=(Button)findViewById(R.id.opb);
        opc=(Button)findViewById(R.id.opc);
        opd=(Button)findViewById(R.id.opd);

        nextbtn=(Button)findViewById(R.id.nextbtn);

        loop();

        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loop();
            }
        });

        opa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                opa.setBackgroundColor(Color.GREEN);

            }
        });
        opb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                opb.setBackgroundColor(Color.GREEN);

            }
        });
        opc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                opc.setBackgroundColor(Color.GREEN);

            }
        });
        opd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                opd.setBackgroundColor(Color.GREEN);

            }
        });


    }
    void getQues(){

            question.setText("question "+i);

            opa.setText("option 1");
            opb.setText("option 2");
            opc.setText("option 3");
            opd.setText("option 4");

        }

        void loop(){
            while (i<119){
                getQues();
                i++;
                break;
            }
        }
}
