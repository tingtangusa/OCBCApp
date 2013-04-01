package com.jason.ocbcapp;

import android.content.Context;
import android.widget.Toast;

public class CrossCutting {

    public static void showUnableToConnectMessage(Context context) {
        showToastMessage(context, "Unable to connect to server");
    }

    public static void showToastMessage(Context context, String msg) {
        Toast t = new Toast(context);
        t.setText(msg);
        t.show();
    }
}
