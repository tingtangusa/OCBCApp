package com.jason.ocbcapp;

import android.content.Context;
import android.widget.Toast;

public class CrossCutting {

    public static void showToastMessage(Context context, String msg) {
        Toast t = new Toast(context);
        t.setText(msg);
        t.show();
    }
}
