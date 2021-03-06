/* Copyright 2013 Foxdog Studios Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.foxdogstudios.peepers;

import java.net.InetAddress;
import java.net.Inet4Address;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import uk.org.potentialdifference.darknet.R;
import uk.org.potentialdifference.darknet.StreamCameraDelegate;

public final class StreamCameraActivity extends Activity
    implements SurfaceHolder.Callback, StreamCameraDelegate
{
    private static final String TAG = StreamCameraActivity.class.getSimpleName();

    private static final String WAKE_LOCK_TAG = "peepers";

    private static final String PREF_CAMERA = "camera";
    private static final int PREF_CAMERA_INDEX_DEF = 0;
    private static final String PREF_PORT = "port";
    private static final int PREF_PORT_DEF = 8080;
    private static final String PREF_JPEG_SIZE = "size";
    private static final String PREF_JPEG_QUALITY = "jpeg_quality";
    private static final int PREF_JPEG_QUALITY_DEF = 40;
    // preview sizes will always have at least one element, so this is safe
    private static final int PREF_PREVIEW_SIZE_INDEX_DEF = 0;

    private boolean mRunning = false;
    private boolean mPreviewDisplayCreated = false;
    private SurfaceView mSurfaceView = null;
    private SurfaceHolder mPreviewDisplay = null;
    private CameraStreamer mCameraStreamer = null;

    private String mIpAddress = "";
    private int mCameraIndex = PREF_CAMERA_INDEX_DEF;
    private int mPort = PREF_PORT_DEF;
    private int mJpegQuality = PREF_JPEG_QUALITY_DEF;
    private int mPrevieSizeIndex = PREF_PREVIEW_SIZE_INDEX_DEF;
    private TextView mIpAddressView = null;
    private WakeLock mWakeLock = null;

    private int mDesiredCameraWidth = 800;
    private int mDesiredCameraHeight = 480;

    public StreamCameraActivity()
    {
        super();
    } // constructor()

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.stream_camera_activity);

        Bundle extras = getIntent().getExtras();
        mDesiredCameraWidth = extras.getInt("width");
        mDesiredCameraHeight = extras.getInt("height");

        mSurfaceView = (SurfaceView) findViewById(R.id.camera);
        mPreviewDisplay = mSurfaceView.getHolder();
        mPreviewDisplay.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mPreviewDisplay.addCallback(this);

        mIpAddress = tryGetIpV4Address();
        mIpAddressView = (TextView) findViewById(R.id.ip_address);

        final PowerManager powerManager =
                (PowerManager) getSystemService(POWER_SERVICE);
        mWakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK,
                WAKE_LOCK_TAG);

    } // onCreate(Bundle)

    @Override
    protected void onResume()
    {
        super.onResume();
        mRunning = true;
        tryStartCameraStreamer();
        mWakeLock.acquire();
    } // onResume()

    @Override
    protected void onPause()
    {
        mWakeLock.release();
        super.onPause();
        mRunning = false;
        ensureCameraStreamerStopped();
    } // onPause()

    @Override
    public void surfaceChanged(final SurfaceHolder holder, final int format,
            final int width, final int height)
    {
        CharSequence text = String.format("surfaceChanged %d x %d", width, height);
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(getApplicationContext(), text, duration);
        toast.show();
    } // surfaceChanged(SurfaceHolder, int, int, int)

    @Override
    public void surfaceCreated(final SurfaceHolder holder)
    {
        mPreviewDisplayCreated = true;
        tryStartCameraStreamer();
    } // surfaceCreated(SurfaceHolder)

    @Override
    public void surfaceDestroyed(final SurfaceHolder holder)
    {
        mPreviewDisplayCreated = false;
        ensureCameraStreamerStopped();
    } // surfaceDestroyed(SurfaceHolder)

    private void tryStartCameraStreamer()
    {
        if (mRunning && mPreviewDisplayCreated)
        {
            mCameraStreamer = new CameraStreamer(this, mCameraIndex, false, mPort,
                                                 mPrevieSizeIndex, mJpegQuality, mPreviewDisplay,
                                                 mDesiredCameraWidth, mDesiredCameraHeight);
            mCameraStreamer.start();
        } // if
    } // tryStartCameraStreamer()

    private void ensureCameraStreamerStopped()
    {
        if (mCameraStreamer != null)
        {
            mCameraStreamer.stop();
            mCameraStreamer = null;
        } // if
    } // stopCameraStreamer()

    private static String tryGetIpV4Address()
    {
        try
        {
            final Enumeration<NetworkInterface> en =
                    NetworkInterface.getNetworkInterfaces();
            while (en.hasMoreElements())
            {
                final NetworkInterface intf = en.nextElement();
                final Enumeration<InetAddress> enumIpAddr =
                        intf.getInetAddresses();
                while (enumIpAddr.hasMoreElements())
                {
                    final  InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress())
                    {
                        final String addr = inetAddress.getHostAddress().toUpperCase();
                        if (inetAddress instanceof Inet4Address)
                        {
                            return addr;
                        }
                    } // if
                } // while
            } // for
        } // try
        catch (final Exception e)
        {
            // Ignore
        } // catch
        return null;
    } // tryGetIpV4Address()

    private int[] scaleToFit(int[] fit, int[] within) {
        float aspectRatio = ((float) fit[0]) / ((float) fit[1]);
        float width = (float) within[0];
        float height = (float) within[1];
        float withinRatio = width / height;
        if (aspectRatio > withinRatio) {
            return new int[]{ within[0], Math.round(width / aspectRatio)};
        } else {
            return new int[]{ Math.round(height * aspectRatio), within[1]};
        }
    }

    @Override
    public void cameraStreamDidStart(int width, int height) {
        DisplayMetrics screen = getResources().getDisplayMetrics();
        final ViewGroup.LayoutParams params = mSurfaceView.getLayoutParams();
        int[] scaledToFit = scaleToFit(new int[] {width, height},
                                       new int[] {screen.widthPixels,
                                                  screen.heightPixels});
        params.width = scaledToFit[0];
        params.height = scaledToFit[1];
        runOnUiThread(new Runnable() {
                public void run() {
                    mSurfaceView.setLayoutParams(params);
                    mSurfaceView.requestLayout();
                }
            });
    }

} // class StreamCameraActivity
