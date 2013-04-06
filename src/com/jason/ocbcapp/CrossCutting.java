package com.jason.ocbcapp;

import java.io.InputStream;
import java.util.Scanner;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class CrossCutting {

    private static final String APP_TAG = MainActivity.APP_TAG;

    public static void showUnableToConnectMessage(Context context) {
        showToastMessage(context, "Unable to connect to server");
    }

    public static void showToastMessage(Context context, String msg) {
        Toast t = new Toast(context);
        t.setText(msg);
        t.show();
    }

    public static String readStream(InputStream inputStream) {
        // TODO Auto-generated method stub
        StringBuilder buf = new StringBuilder();
        Scanner sc = null;
        try {

            sc = new Scanner(inputStream);
            while (sc.hasNext()) {
                buf.append(sc.next());
            }

        } catch (Exception e) {
            Log.e(APP_TAG, e.getMessage());
        }
        return buf.toString();
    }
}
