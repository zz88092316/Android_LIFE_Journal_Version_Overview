package com.example.lenovo.Android_LIFE_Journal;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import com.example.lenovo.Android_LIFE_Journal.function.SearchNoteAsyncTask;

public class LOGO extends MainActivity{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welecome);
        handler.postDelayed(runnable,1750);
    }
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            startActivity(new Intent(LOGO.this,MainActivity.class));
            finish();
        }
    };

}

