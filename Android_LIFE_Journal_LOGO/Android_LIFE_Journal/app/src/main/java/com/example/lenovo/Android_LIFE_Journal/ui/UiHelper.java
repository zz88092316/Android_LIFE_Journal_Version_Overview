package com.example.lenovo.Android_LIFE_Journal.ui;

import android.content.Context;
import android.widget.Toast;

public class UiHelper {
    public static void toastShowMessageShort(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
