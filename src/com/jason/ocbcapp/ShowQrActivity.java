package com.jason.ocbcapp;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.widget.ImageView;

public class ShowQrActivity extends Activity {

    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;
    private static final int QR_WIDTH = 500;
    private static final int QR_HEIGHT = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_qr);

        // Show the up button at the icon
        setupActionBar();

        // Get QRWriter and write the QR code to the image view
        BitMatrix qrBitMatrix = createQrCodeBitMatrix();
        Bitmap bitMap = bitMatrixToBitmap(qrBitMatrix);
        setupImageViewWithBitmap(bitMap);
    }

    /**
     * Creates the qr code that contains two information:
     * 1) User's token.
     * 2) Services that the user selected.
     * @return {@link com.google.zxing.common.BitMatrix} qrBitMatrix
     */
    private BitMatrix createQrCodeBitMatrix() {

        QRCodeWriter qrWriter = new QRCodeWriter();
        BitMatrix qrBitMatrix = null;
        String userToken = CrossCutting.getUserTokenFromPreferences(getApplicationContext());
        ArrayList<String> servicesSelected = getServicesSelected();
        // make json object from user token and the services the user
        // selected.
        JSONObject walkinRequest = createWalkinRequestJson(userToken,
                servicesSelected);
        try {
            qrBitMatrix = qrWriter.encode(walkinRequest.toString(),
                    BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return qrBitMatrix;
    }

    /**
     * @param bitMap
     */
    private void setupImageViewWithBitmap(Bitmap bitMap) {
        ImageView imgView = (ImageView) findViewById(R.id.qrCode);
        imgView.setImageBitmap(bitMap);
    }

    /**
     * Creates the json object that will be encoded in the qr code.
     * @param userToken
     * @param servicesSelected
     * @return {@link org.json.JSONObject} walkinRequest
     * 
     * @throws JSONException
     */
    private JSONObject createWalkinRequestJson(String userToken,
            ArrayList<String> servicesSelected) {
        JSONObject walkinRequest = new JSONObject();
        try {
            walkinRequest.accumulate("userToken", userToken);
            JSONArray servicesJArray = new JSONArray(servicesSelected);
            walkinRequest.accumulate("services", servicesJArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return walkinRequest;
    }

    private ArrayList<String> getServicesSelected() {
        ArrayList<String> servicesSelected = getIntent()
                .getStringArrayListExtra("servicesSelected");
        return servicesSelected;
    }

    /**
     * Set up the {@link android.app.ActionBar}.
     */
    private void setupActionBar() {
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /*
     * Code to convert BitMatrix to BitMap.
     * Taken from zxing's QRCodeEncoder
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
        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
