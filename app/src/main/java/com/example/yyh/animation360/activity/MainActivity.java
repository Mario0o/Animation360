package com.example.yyh.animation360.activity;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;

import com.example.yyh.animation360.R;
import com.example.yyh.animation360.service.MyFloatService;



public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startService(View view){
        Intent intent=new Intent(this, MyFloatService.class);
        startService(intent);
        finish();


    }
}
