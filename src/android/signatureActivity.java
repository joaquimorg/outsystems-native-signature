package org.joaquim.signature;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Window;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.github.gcacace.signaturepad.views.SignaturePad;

import java.io.*;
import java.nio.ByteBuffer;

import org.json.JSONArray;
import org.json.JSONException;

public class signatureActivity extends Activity
{

    private SignaturePad mSignaturePad;
    private ImageButton mClearButton;
    private ImageButton mSaveButton;
    private ImageButton mCancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(getApplication().getResources().getIdentifier(
                "activity_signature", "layout",
                getApplication().getPackageName()));

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenWidth = (int) (metrics.widthPixels * 0.90);
        int screenHeight = (int) (metrics.heightPixels * 0.40);

        getWindow().setLayout(screenWidth, screenHeight);

        this.setFinishOnTouchOutside(false);

        mSignaturePad = (SignaturePad) findViewById(getApplication()
                .getResources().getIdentifier("signature_pad", "id",
                        getApplication().getPackageName()));

        /*mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onSigned()
            {
                mSaveButton.setEnabled(true);
                mClearButton.setEnabled(true);
            }

            @Override
            public void onClear()
            {
                mSaveButton.setEnabled(false);
                mClearButton.setEnabled(false);
            }
        });*/

        mCancelButton = (ImageButton) findViewById(getApplication().getResources()
                .getIdentifier("cancel_button", "id",
                        getApplication().getPackageName()));
        mClearButton = (ImageButton) findViewById(getApplication().getResources()
                .getIdentifier("clear_button", "id",
                        getApplication().getPackageName()));
        mSaveButton = (ImageButton) findViewById(getApplication().getResources()
                .getIdentifier("save_button", "id",
                        getApplication().getPackageName()));

        /*mSaveButton.setEnabled(true);
        mClearButton.setEnabled(true);*/

        String btnNames = getIntent().getExtras().getString("buttonNames",null);
        /*TextView title = (TextView) findViewById(getApplication()
                .getResources().getIdentifier("signature_pad_title", "id",
                        getApplication().getPackageName()));
        title.setText(getIntent().getExtras().getString("title",""));*/

        /*TextView description = (TextView) findViewById(getApplication()
                .getResources().getIdentifier("signature_pad_description", "id",
                        getApplication().getPackageName()));
        description.setText(getIntent().getExtras().getString("description",""));
        */
        /*JSONArray buttonNames;
        try {
            if (btnNames != null) {
                buttonNames = new JSONArray(btnNames);
                if(buttonNames != null &&  buttonNames.length() == 3){
                    mCancelButton.setText(buttonNames.get(0).toString());
                    mClearButton.setText(buttonNames.get(1).toString());
                    mSaveButton.setText(buttonNames.get(2).toString());
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                finishWithResult(null, Activity.RESULT_CANCELED);
            }
        });

        mClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                mSignaturePad.clear();
            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                boolean isTransparent =  getIntent().getExtras().getBoolean("isTransparent");
                Bitmap signatureBitmap;
                if(isTransparent){
                    signatureBitmap = mSignaturePad.getTransparentSignatureBitmap();
                } else {
                    signatureBitmap = mSignaturePad.getSignatureBitmap();
                }

                String base64Image = convertBitmapToBase64(signatureBitmap);
                finishWithResult(base64Image, Activity.RESULT_OK);
                
            }
        });
    }

    public String convertBitmapToBase64(Bitmap bitmap)
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String encoded = Base64.encodeToString(byteArray, Base64.NO_WRAP);
        //return "data:image/png;base64," + encoded;
        return encoded;
    }

    private void finishWithResult(String result, int status)
    {
        Bundle conData = new Bundle();
        conData.putString("results", result);
        Intent intent = new Intent();
        intent.putExtras(conData);
        setResult(status, intent);
        finish();
    }
}
