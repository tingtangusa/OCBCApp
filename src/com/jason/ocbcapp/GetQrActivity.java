package com.jason.ocbcapp;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.view.Menu;
import android.widget.ImageView;

public class GetQrActivity extends Activity {

    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_qr);
        QRCodeWriter qrWriter = new QRCodeWriter();
        BitMatrix qrBitMatrix = null;
        try {
            qrBitMatrix = qrWriter.encode("SPAM, HAM, EGGS", BarcodeFormat.QR_CODE, 700, 700);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        Bitmap bitMap = bitMatrixToBitmap(qrBitMatrix);
        ImageView imgView = (ImageView) findViewById(R.id.qrCode);
        imgView.setImageBitmap(bitMap);
    }

    /*
     * Code to convert BitMatrix to BitMap take from zxing's QRCodeEncoder
     */
    private Bitmap bitMatrixToBitmap(BitMatrix bitMatrix) {
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
          int offset = y * width;
          for (int x = 0; x < width; x++) {
            pixels[offset + x] = bitMatrix.get(x, y) ? BLACK : WHITE;
          }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.get_qr, menu);

        return true;
    }

}
