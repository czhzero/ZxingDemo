package com.chen.zxing.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.RelativeLayout;


import com.chen.zxing.R;
import com.chen.zxing.camera.CameraManager;
import com.chen.zxing.decoding.DecodeThread;
import com.chen.zxing.sound.SoundVibratingPlayer;
import com.google.zxing.Result;


/**
 * Created by chenzhaohua on 16/3/11.
 */
public class CaptureView extends RelativeLayout implements SurfaceHolder.Callback {

    private static final String TAG = "CaptureView";

    private SurfaceView surfaceView;
    private ViewfinderView viewfinderView;

    private boolean hasSurface;

    private CaptureHandler handler;
    private SoundVibratingPlayer player;
    private ActivityFinishTimer inactivityTimer;
    private DecodeThread decodeThread;
    private DecodeListener decodeListener;
    private Context mContext;

    /**
     * scan frame size
     */
    private int mFrameTop;
    private int mFrameLeft;
    private int mFrameHeight;



    public interface DecodeListener {
        void handleDecode(Result result, Bitmap barcode);
    }

    public CaptureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.layout_capture_view, this);
        mContext = context;
        surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CaptureView);
        mFrameHeight = (int) typedArray.getDimension(R.styleable.CaptureView_frame_height, 0);
        mFrameLeft = (int) typedArray.getDimension(R.styleable.CaptureView_frame_left, 0);
        mFrameTop = (int) typedArray.getDimension(R.styleable.CaptureView_frame_top, 0);

        LogUtils.d(TAG, "mFrameHeight = " + mFrameHeight);
        LogUtils.d(TAG, "mFrameLeft = " + mFrameLeft);
        LogUtils.d(TAG, "mFrameTop = " + mFrameTop);

        hasSurface = false;
        player = new SoundVibratingPlayer(context);
        inactivityTimer = new ActivityFinishTimer((Activity) context);
        CameraManager.init(context.getApplicationContext());
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        hasSurface = true;
        startCamera(surfaceHolder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    public void setDecodeListener(DecodeListener listener) {
        decodeListener = listener;
    }


    public void onResume() {
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            startCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
    }


    public void onPause() {
        stopCamera();
    }


    public void onDestroy() {
        inactivityTimer.shutdown();
    }


    public void restartCamera() {
        viewfinderView.showLine(true);
        startCamera(surfaceView.getHolder());
    }


    private void startCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            ToastUtils.showToast(mContext, "无法打开相机，请检查是否授予摄像头权限");
            return;
        }

        if (handler == null) {
            handler = new CaptureHandler(this);
            decodeThread = new DecodeThread(handler, null, null, new ViewfinderViewCallback(viewfinderView));
            decodeThread.start();
        }

        Message quit = Message.obtain(handler, R.id.restart_preview);
        quit.sendToTarget();
        CameraManager.get().initFrameRect(mFrameLeft, mFrameTop, mFrameHeight);
        CameraManager.get().startPreview();
        CameraManager.get().requestPreviewFrame(decodeThread.getDecodeHandler(), R.id.decode);
        CameraManager.get().requestAutoFocus(handler, R.id.auto_focus);
        viewfinderView.drawViewfinder();

    }


    private void stopCamera() {

        CameraManager.get().stopPreview();

        try {
            CameraManager.get().closeDriver();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }


        if (decodeThread == null || handler == null) {
            return;
        }

        Message quit = Message.obtain(decodeThread.getDecodeHandler(), R.id.quit);
        quit.sendToTarget();
        try {
            decodeThread.join();
        } catch (InterruptedException e) {
            // continue
        }
        handler.removeMessages(R.id.decode_succeeded);
        handler.removeMessages(R.id.decode_failed);
        handler = null;
        decodeThread = null;

    }


    public void requestPreviewFrame() {
        CameraManager.get().requestPreviewFrame(decodeThread.getDecodeHandler(), R.id.decode);
    }


    public void requestAutoFocus() {
        CameraManager.get().requestAutoFocus(handler, R.id.auto_focus);
    }


    private void handleDecode(Result result, Bitmap barcode) {
        inactivityTimer.onActivity();
        player.playBeepSoundAndVibrate();
        if (decodeListener != null) {
            decodeListener.handleDecode(result, barcode);
        }
    }


    public static final class CaptureHandler extends Handler {

        private CaptureView view;
        private State state;

        private enum State {
            PREVIEW,
            DONE
        }

        public CaptureHandler(CaptureView view) {
            this.view = view;
            state = State.PREVIEW;
        }

        @Override
        public void handleMessage(Message message) {
            int id = message.what;

            if (id == R.id.auto_focus) {
                if (state == State.PREVIEW) {
                    view.requestAutoFocus();
                }
            } else if (id == R.id.restart_preview) {
                state = State.PREVIEW;
            } else if (id == R.id.decode_succeeded) {
                state = State.DONE;
                Bundle bundle = message.getData();
                Bitmap barcode = bundle == null ? null : (Bitmap) bundle.getParcelable(DecodeThread.BARCODE_BITMAP);
                view.handleDecode((Result) message.obj, barcode);
            } else if (id == R.id.decode_failed) {
                state = State.PREVIEW;
                view.requestPreviewFrame();
            }

        }


    }


}
