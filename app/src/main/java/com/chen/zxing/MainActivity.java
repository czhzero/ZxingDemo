package com.chen.zxing;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.chen.zxing.view.CaptureView;
import com.chen.zxing.view.ToastUtils;
import com.google.zxing.Result;

public class MainActivity extends AppCompatActivity implements CaptureView.DecodeListener {

    private CaptureView vCapture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        vCapture = (CaptureView) findViewById(R.id.v_capture);
        vCapture.setDecodeListener(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        vCapture.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        vCapture.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        vCapture.onDestroy();
    }

    @Override
    public void handleDecode(Result result, Bitmap barcode) {
        ToastUtils.showToast(this, result.getText());
    }
}
