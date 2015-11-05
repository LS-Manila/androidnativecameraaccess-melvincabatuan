package io.github.melvincabatuan.nativegrayscale;

/**
 * Created by root on 6/3/15.
 */

import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.ImageView;

import java.io.IOException;
import java.util.List;

public class CameraPreview implements SurfaceHolder.Callback, Camera.PreviewCallback {
    private Camera mCamera = null;
    private ImageView MyCameraPreview = null;
    private Bitmap bitmap = null;
    private int[] pixels = null;
    private byte[] FrameData = null;
    private int imageFormat;
    private int PreviewSizeWidth;
    private int PreviewSizeHeight;
    private boolean bProcessing = false;
    public Parameters parameters;

    Handler mHandler = new Handler(Looper.getMainLooper());

    public CameraPreview(int PreviewlayoutWidth, int PreviewlayoutHeight,
                         ImageView CameraPreview) {
        PreviewSizeWidth = PreviewlayoutWidth;
        PreviewSizeHeight = PreviewlayoutHeight;
        MyCameraPreview = CameraPreview;
        bitmap = Bitmap.createBitmap(PreviewSizeWidth, PreviewSizeHeight, Bitmap.Config.ARGB_8888);
        pixels = new int[PreviewSizeWidth * PreviewSizeHeight];
    }



    int i = 0;
    long now, oldnow, count = 0;

    @Override
    public void onPreviewFrame(byte[] arg0, Camera arg1) {

        i++;
        now = System.nanoTime()/1000;
        if (i>3) {
            Log.d("onPreviewFrame: ", "Measured: " + 1000000L / (now - oldnow) + " fps.");
            count++;
        }
        oldnow = now;

        /// NV21 Format ONLY
        if (imageFormat == ImageFormat.NV21) {

            if (!bProcessing) {
                FrameData = arg0;
                mHandler.post(DoImageProcessing);
            }
        }
    }


    public void onPause() {
        mCamera.stopPreview();
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        parameters = mCamera.getParameters();
        List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
        Camera.Size result = getOptimalPreviewSize(sizes, PreviewSizeWidth, PreviewSizeHeight);
        PreviewSizeWidth = result.width;
        PreviewSizeHeight = result.height;
        parameters.setPreviewSize(PreviewSizeWidth,PreviewSizeHeight);

        bitmap = Bitmap.createBitmap(PreviewSizeWidth, PreviewSizeHeight,
                Bitmap.Config.ARGB_8888);
        MyCameraPreview.setImageBitmap(bitmap);
        pixels = new int[PreviewSizeWidth * PreviewSizeHeight];
        imageFormat = parameters.getPreviewFormat();

        try {
            mCamera.setParameters(parameters);
            mCamera.startPreview();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;

        if (sizes == null)
            return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.height / size.width;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;

            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }


    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        mCamera = Camera.open();

        try {
            mCamera.setPreviewDisplay(arg0);
            mCamera.setPreviewCallback(this);
        } catch (IOException e) {
            mCamera.release();
            mCamera = null;
        }
        parameters = mCamera.getParameters();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        mCamera.setPreviewCallback(null);
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }

    public native boolean ImageProcessing(int width, int height,
                                          byte[] NV21FrameData, int[] pixels);

    static {
        System.loadLibrary("ImageProcessing");
    }

    private Runnable DoImageProcessing = new Runnable() {
        public void run() {
            bProcessing = true;
            ImageProcessing(PreviewSizeWidth, PreviewSizeHeight, FrameData, pixels);
            bitmap.setPixels(pixels, 0, PreviewSizeWidth, 0, 0, PreviewSizeWidth, PreviewSizeHeight);
            MyCameraPreview.setImageBitmap(bitmap);
            bProcessing = false;
        }
    };
}