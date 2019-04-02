package com.example.arvind.ihl;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v13.app.FragmentCompat;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import at.markushi.ui.CircleButton;

public class Camera2BasicFragment extends Fragment
        implements FragmentCompat.OnRequestPermissionsResultCallback {

    /** Tag for the {@link Log}. */
    private static final String TAG = "TfLiteCameraDemo";

    private static final String FRAGMENT_DIALOG = "dialog";

    private static final String HANDLE_THREAD_NAME = "CameraBackground";

    boolean flag=false;

    String servings="-1";

    String food_to_db="No food detetcted";

    public String URL_POST_FOOD="https://phseven.herokuapp.com/users/insert_food";

    Button imgcaldb;

    SharedPreferences.Editor editor;

    SharedPreferences pref;


    private static final int PERMISSIONS_REQUEST_CODE = 1;

    private final Object lock = new Object();
    private boolean runClassifier = false;
    private boolean checkedPermissions = false;
    private TextView textView;
    private CircleButton detect;
    private ImageClassifier classifier;

    String food_name="-1";

    ProgressDialog progressDialog;

    /** Max preview width that is guaranteed by Camera2 API */
    private static final int MAX_PREVIEW_WIDTH = 1920;

    /** Max preview height that is guaranteed by Camera2 API */
    private static final int MAX_PREVIEW_HEIGHT = 1080;

    /**
     * {@link TextureView.SurfaceTextureListener} handles several lifecycle events on a {@link
     * TextureView}.
     */
    private final TextureView.SurfaceTextureListener surfaceTextureListener =
            new TextureView.SurfaceTextureListener() {

                @Override
                public void onSurfaceTextureAvailable(SurfaceTexture texture, int width, int height) {
                    openCamera(width, height);
                }

                @Override
                public void onSurfaceTextureSizeChanged(SurfaceTexture texture, int width, int height) {
                    configureTransform(width, height);
                }

                @Override
                public boolean onSurfaceTextureDestroyed(SurfaceTexture texture) {
                    return true;
                }

                @Override
                public void onSurfaceTextureUpdated(SurfaceTexture texture) {
                }
            };

    /** ID of the current {@link CameraDevice}. */
    private String cameraId;

    /** An {@link AutoFitTextureView} for camera preview. */
    private AutoFitTextureView textureView;

    /** A {@link CameraCaptureSession } for camera preview. */
    private CameraCaptureSession captureSession;

    /** A reference to the opened {@link CameraDevice}. */
    private CameraDevice cameraDevice;

    /** The {@link android.util.Size} of camera preview. */
    private Size previewSize;

    /** {@link CameraDevice.StateCallback} is called when {@link CameraDevice} changes its state. */
    private final CameraDevice.StateCallback stateCallback =
            new CameraDevice.StateCallback() {

                @Override
                public void onOpened(@NonNull CameraDevice currentCameraDevice) {
                    // This method is called when the camera is opened.  We start camera preview here.
                    cameraOpenCloseLock.release();
                    cameraDevice = currentCameraDevice;
                    createCameraPreviewSession();
                }

                @Override
                public void onDisconnected(@NonNull CameraDevice currentCameraDevice) {
                    cameraOpenCloseLock.release();
                    currentCameraDevice.close();
                    cameraDevice = null;
                }

                @Override
                public void onError(@NonNull CameraDevice currentCameraDevice, int error) {
                    cameraOpenCloseLock.release();
                    currentCameraDevice.close();
                    cameraDevice = null;
                    Activity activity = getActivity();
                    if (null != activity) {
                        activity.finish();
                    }
                }
            };

    /** An additional thread for running tasks that shouldn't block the UI. */
    private HandlerThread backgroundThread;

    /** A {@link Handler} for running tasks in the background. */
    private Handler backgroundHandler;

    /** An {@link ImageReader} that handles image capture. */
    private ImageReader imageReader;

    /** {@link CaptureRequest.Builder} for the camera preview */
    private CaptureRequest.Builder previewRequestBuilder;

    /** {@link CaptureRequest} generated by {@link #previewRequestBuilder} */
    private CaptureRequest previewRequest;

    /** A {@link Semaphore} to prevent the app from exiting before closing the camera. */
    private Semaphore cameraOpenCloseLock = new Semaphore(1);

    /** A {@link CameraCaptureSession.CaptureCallback} that handles events related to capture. */
    private CameraCaptureSession.CaptureCallback captureCallback =
            new CameraCaptureSession.CaptureCallback() {

                @Override
                public void onCaptureProgressed(
                        @NonNull CameraCaptureSession session,
                        @NonNull CaptureRequest request,
                        @NonNull CaptureResult partialResult) {
                }

                @Override
                public void onCaptureCompleted(
                        @NonNull CameraCaptureSession session,
                        @NonNull CaptureRequest request,
                        @NonNull TotalCaptureResult result) {
                }
            };

    /**
     * Shows a {@link Toast} on the UI thread for the classification results.
     *
     * @param text The message to show
     */
    private void showToast(final String text) {
        final Activity activity = getActivity();
        if (activity != null) {
            activity.runOnUiThread(
                    new Runnable() {
                        @Override
                        public void run() {

                            textView.setText(text);
                        }
                    });
        }
    }

    /**
     * Resizes image.
     *
     * Attempting to use too large a preview size could  exceed the camera bus' bandwidth limitation,
     * resulting in gorgeous previews but the storage of garbage capture data.
     *
     * Given {@code choices} of {@code Size}s supported by a camera, choose the smallest one that is
     * at least as large as the respective texture view size, and that is at most as large as the
     * respective max size, and whose aspect ratio matches with the specified value. If such size
     * doesn't exist, choose the largest one that is at most as large as the respective max size, and
     * whose aspect ratio matches with the specified value.
     *
     * @param choices The list of sizes that the camera supports for the intended output class
     * @param textureViewWidth The width of the texture view relative to sensor coordinate
     * @param textureViewHeight The height of the texture view relative to sensor coordinate
     * @param maxWidth The maximum width that can be chosen
     * @param maxHeight The maximum height that can be chosen
     * @param aspectRatio The aspect ratio
     * @return The optimal {@code Size}, or an arbitrary one if none were big enough
     */
    private static Size chooseOptimalSize(
            Size[] choices,
            int textureViewWidth,
            int textureViewHeight,
            int maxWidth,
            int maxHeight,
            Size aspectRatio) {

        // Collect the supported resolutions that are at least as big as the preview Surface
        List<Size> bigEnough = new ArrayList<>();
        // Collect the supported resolutions that are smaller than the preview Surface
        List<Size> notBigEnough = new ArrayList<>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        for (Size option : choices) {
            if (option.getWidth() <= maxWidth
                    && option.getHeight() <= maxHeight
                    && option.getHeight() == option.getWidth() * h / w) {
                if (option.getWidth() >= textureViewWidth && option.getHeight() >= textureViewHeight) {
                    bigEnough.add(option);
                } else {
                    notBigEnough.add(option);
                }
            }
        }

        // Pick the smallest of those big enough. If there is no one big enough, pick the
        // largest of those not big enough.
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else if (notBigEnough.size() > 0) {
            return Collections.max(notBigEnough, new CompareSizesByArea());
        } else {
            Log.e(TAG, "Couldn't find any suitable preview size");
            return choices[0];
        }
    }

    public static Camera2BasicFragment newInstance() {
        return new Camera2BasicFragment();
    }

    /** Layout the preview and buttons. */
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_camera2_basic, container, false);
    }

    /** Connect the buttons to their event handler. */
    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        textureView = (AutoFitTextureView) view.findViewById(R.id.texture);
        textView = (TextView) view.findViewById(R.id.text);

        imgcaldb=(Button)view.findViewById(R.id.imgcaldb);

        detect = (CircleButton) view.findViewById(R.id.detect);

        Spinner spinner = view.findViewById(R.id.spinner1);

        progressDialog=new ProgressDialog(getActivity());

        String[] items = new String[]{"1", "2", "3","4"};


        ArrayAdapter aa = new ArrayAdapter(getActivity(), R.layout.spinner_item, items);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(aa);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                servings =items[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinner.setAdapter(aa);

        imgcaldb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(servings.equals("-1")){
                  Toast.makeText(getActivity(),"No food selected",Toast.LENGTH_LONG).show();
                }
                else {
                    try {
                        uploadtoDB(servings);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    progressDialog.setMessage("Sending details, please wait.");
                    progressDialog.show();
                }
            }
        });

        detect.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    textView.setVisibility(View.VISIBLE);

                    // start your timer

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    food_to_db=textView.getText().toString();

                    food_to_db=food_to_db.substring(food_to_db.indexOf('\n')+1,food_to_db.length());

                    Log.d("food_name",  food_to_db.substring(food_to_db.indexOf('\n')+1,food_to_db.length()));
                    textView.setVisibility(View.INVISIBLE);

                    if(flag) {
                        Toast.makeText(getActivity(), "Select Servings and Click add", Toast.LENGTH_LONG).show();
                    }
                    // stop your timer.

                }
                return false;
            }
        });
    }

    /** Load the model and labels. */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            classifier = new ImageClassifier(getActivity());
        } catch (IOException e) {
            Log.e(TAG, "Failed to initialize an image classifier.");
        }
        startBackgroundThread();
    }

    @Override
    public void onResume() {
        super.onResume();
        startBackgroundThread();

        // When the screen is turned off and turned back on, the SurfaceTexture is already
        // available, and "onSurfaceTextureAvailable" will not be called. In that case, we can open
        // a camera and start preview from here (otherwise, we wait until the surface is ready in
        // the SurfaceTextureListener).
        if (textureView.isAvailable()) {
            openCamera(textureView.getWidth(), textureView.getHeight());
        } else {
            textureView.setSurfaceTextureListener(surfaceTextureListener);
        }
    }

    @Override
    public void onPause() {
        closeCamera();
        stopBackgroundThread();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        classifier.close();
        super.onDestroy();
    }

    /**
     * Sets up member variables related to camera.
     *
     * @param width The width of available size for camera preview
     * @param height The height of available size for camera preview
     */
    private void setUpCameraOutputs(int width, int height) {
        Activity activity = getActivity();
        CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try {
            for (String cameraId : manager.getCameraIdList()) {
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);

                // We don't use a front facing camera in this sample.
                Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
                    continue;
                }

                StreamConfigurationMap map =
                        characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                if (map == null) {
                    continue;
                }

                // // For still image captures, we use the largest available size.
                Size largest =
                        Collections.max(
                                Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)), new CompareSizesByArea());
                imageReader =
                        ImageReader.newInstance(
                                largest.getWidth(), largest.getHeight(), ImageFormat.JPEG, /*maxImages*/ 2);

                // Find out if we need to swap dimension to get the preview size relative to sensor
                // coordinate.
                int displayRotation = activity.getWindowManager().getDefaultDisplay().getRotation();
                // noinspection ConstantConditions
                /* Orientation of the camera sensor */
                int sensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
                boolean swappedDimensions = false;
                switch (displayRotation) {
                    case Surface.ROTATION_0:
                    case Surface.ROTATION_180:
                        if (sensorOrientation == 90 || sensorOrientation == 270) {
                            swappedDimensions = true;
                        }
                        break;
                    case Surface.ROTATION_90:
                    case Surface.ROTATION_270:
                        if (sensorOrientation == 0 || sensorOrientation == 180) {
                            swappedDimensions = true;
                        }
                        break;
                    default:
                        Log.e(TAG, "Display rotation is invalid: " + displayRotation);
                }

                Point displaySize = new Point();
                activity.getWindowManager().getDefaultDisplay().getSize(displaySize);
                int rotatedPreviewWidth = width;
                int rotatedPreviewHeight = height;
                int maxPreviewWidth = displaySize.x;
                int maxPreviewHeight = displaySize.y;

                if (swappedDimensions) {
                    rotatedPreviewWidth = height;
                    rotatedPreviewHeight = width;
                    maxPreviewWidth = displaySize.y;
                    maxPreviewHeight = displaySize.x;
                }

                if (maxPreviewWidth > MAX_PREVIEW_WIDTH) {
                    maxPreviewWidth = MAX_PREVIEW_WIDTH;
                }

                if (maxPreviewHeight > MAX_PREVIEW_HEIGHT) {
                    maxPreviewHeight = MAX_PREVIEW_HEIGHT;
                }

                previewSize =
                        chooseOptimalSize(
                                map.getOutputSizes(SurfaceTexture.class),
                                rotatedPreviewWidth,
                                rotatedPreviewHeight,
                                maxPreviewWidth,
                                maxPreviewHeight,
                                largest);

                // We fit the aspect ratio of TextureView to the size of preview we picked.
                int orientation = getResources().getConfiguration().orientation;
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    textureView.setAspectRatio(previewSize.getWidth(), previewSize.getHeight());
                } else {
                    textureView.setAspectRatio(previewSize.getHeight(), previewSize.getWidth());
                }

                this.cameraId = cameraId;
                return;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            // Currently an NPE is thrown when the Camera2API is used but not supported on the
            // device this code runs.
            ErrorDialog.newInstance(getString(R.string.camera_error))
                    .show(getChildFragmentManager(), FRAGMENT_DIALOG);
        }
    }

    private String[] getRequiredPermissions() {
        Activity activity = getActivity();
        try {
            PackageInfo info =
                    activity
                            .getPackageManager()
                            .getPackageInfo(activity.getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] ps = info.requestedPermissions;
            if (ps != null && ps.length > 0) {
                return ps;
            } else {
                return new String[0];
            }
        } catch (Exception e) {
            return new String[0];
        }
    }

    /** Opens the camera specified by {@link Camera2BasicFragment#cameraId}. */
    private void openCamera(int width, int height) {
        if (!checkedPermissions && !allPermissionsGranted()) {
            FragmentCompat.requestPermissions(this, getRequiredPermissions(), PERMISSIONS_REQUEST_CODE);
            return;
        } else {
            checkedPermissions = true;
        }
        setUpCameraOutputs(width, height);
        configureTransform(width, height);
        Activity activity = getActivity();
        CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try {
            if (!cameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            manager.openCamera(cameraId, stateCallback, backgroundHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                throw new RuntimeException("Interrupted while trying to lock camera opening.", e);
            }
        }

        private boolean allPermissionsGranted() {
            for (String permission : getRequiredPermissions()) {
                if (ContextCompat.checkSelfPermission(getActivity(), permission)
                        != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public void onRequestPermissionsResult(
                int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

        /** Closes the current {@link CameraDevice}. */
        private void closeCamera() {
            try {
                cameraOpenCloseLock.acquire();
                if (null != captureSession) {
                    captureSession.close();
                    captureSession = null;
                }
                if (null != cameraDevice) {
                    cameraDevice.close();
                    cameraDevice = null;
                }
                if (null != imageReader) {
                    imageReader.close();
                    imageReader = null;
                }
            } catch (InterruptedException e) {
                throw new RuntimeException("Interrupted while trying to lock camera closing.", e);
            } finally {
                cameraOpenCloseLock.release();
            }
        }

        /** Starts a background thread and its {@link Handler}. */
        private void startBackgroundThread() {
            backgroundThread = new HandlerThread(HANDLE_THREAD_NAME);
            backgroundThread.start();
            backgroundHandler = new Handler(backgroundThread.getLooper());
            synchronized (lock) {
                runClassifier = true;
            }
            backgroundHandler.post(periodicClassify);
        }

        /** Stops the background thread and its {@link Handler}. */
        private void stopBackgroundThread() {
            backgroundThread.quitSafely();
            try {
                backgroundThread.join();
                backgroundThread = null;
                backgroundHandler = null;
                synchronized (lock) {
                    runClassifier = false;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        /** Takes photos and classify them periodically. */
        private Runnable periodicClassify =
                new Runnable() {
                    @Override
                    public void run() {
                        synchronized (lock) {
                            if (runClassifier) {
                                classifyFrame();
                            }
                        }
                        backgroundHandler.post(periodicClassify);
                    }
                };

        /** Creates a new {@link CameraCaptureSession} for camera preview. */
        private void createCameraPreviewSession() {
            try {
                SurfaceTexture texture = textureView.getSurfaceTexture();
                assert texture != null;

                // We configure the size of default buffer to be the size of camera preview we want.
                texture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());

                // This is the output Surface we need to start preview.
                Surface surface = new Surface(texture);

                // We set up a CaptureRequest.Builder with the output Surface.
                previewRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                previewRequestBuilder.addTarget(surface);

                // Here, we create a CameraCaptureSession for camera preview.
                cameraDevice.createCaptureSession(
                        Arrays.asList(surface),
                        new CameraCaptureSession.StateCallback() {

                            @Override
                            public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                                // The camera is already closed
                                if (null == cameraDevice) {
                                    return;
                                }

                                // When the session is ready, we start displaying the preview.
                                captureSession = cameraCaptureSession;
                                try {
                                    // Auto focus should be continuous for camera preview.
                                    previewRequestBuilder.set(
                                            CaptureRequest.CONTROL_AF_MODE,
                                            CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);

                                    // Finally, we start displaying the camera preview.
                                    previewRequest = previewRequestBuilder.build();
                                    captureSession.setRepeatingRequest(
                                            previewRequest, captureCallback, backgroundHandler);
                                } catch (CameraAccessException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                                showToast("Failed");
                            }
                        },
                        null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        /**
         * Configures the necessary {@link android.graphics.Matrix} transformation to `textureView`. This
         * method should be called after the camera preview size is determined in setUpCameraOutputs and
         * also the size of `textureView` is fixed.
         *
         * @param viewWidth The width of `textureView`
         * @param viewHeight The height of `textureView`
         */
        private void configureTransform(int viewWidth, int viewHeight) {
            Activity activity = getActivity();
            if (null == textureView || null == previewSize || null == activity) {
                return;
            }
            int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
            Matrix matrix = new Matrix();
            RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
            RectF bufferRect = new RectF(0, 0, previewSize.getHeight(), previewSize.getWidth());
            float centerX = viewRect.centerX();
            float centerY = viewRect.centerY();
            if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
                bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
                matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
                float scale =
                        Math.max(
                                (float) viewHeight / previewSize.getHeight(),
                                (float) viewWidth / previewSize.getWidth());
                matrix.postScale(scale, scale, centerX, centerY);
                matrix.postRotate(90 * (rotation - 2), centerX, centerY);
            } else if (Surface.ROTATION_180 == rotation) {
                matrix.postRotate(180, centerX, centerY);
            }
            textureView.setTransform(matrix);
        }

        /** Classifies a frame from the preview stream. */
        private void classifyFrame() {
            if (classifier == null || getActivity() == null || cameraDevice == null) {
                showToast("Uninitialized Classifier or invalid context.");
                return;
            }
            Bitmap bitmap =
                    textureView.getBitmap(ImageClassifier.DIM_IMG_SIZE_X, ImageClassifier.DIM_IMG_SIZE_Y);
            String textToShow = classifier.classifyFrame(bitmap);
            bitmap.recycle();
            showToast(textToShow);
        }

        /** Compares two {@code Size}s based on their areas. */
        private static class CompareSizesByArea implements Comparator<Size> {

            @Override
            public int compare(Size lhs, Size rhs) {
                // We cast here to ensure the multiplications won't overflow
                return Long.signum(
                        (long) lhs.getWidth() * lhs.getHeight() - (long) rhs.getWidth() * rhs.getHeight());
            }
        }

        /** Shows an error message dialog. */
        public static class ErrorDialog extends DialogFragment {

            private static final String ARG_MESSAGE = "message";

            public static ErrorDialog newInstance(String message) {
                ErrorDialog dialog = new ErrorDialog();
                Bundle args = new Bundle();
                args.putString(ARG_MESSAGE, message);
                dialog.setArguments(args);
                return dialog;
            }

            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                final Activity activity = getActivity();
                return new AlertDialog.Builder(activity)
                        .setMessage(getArguments().getString(ARG_MESSAGE))
                        .setPositiveButton(
                                android.R.string.ok,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        activity.finish();
                                    }
                                })
                        .create();
            }
        }

        void uploadtoDB(String servings) throws IOException {

            if(food_to_db=="No food detetcted"){
                Toast.makeText(getActivity(),"No food selected",Toast.LENGTH_LONG).show();
                return;
            }

            String userid="-1";

            SharedPreferences pref = getActivity().getSharedPreferences("login_pref", 0);

            SharedPreferences.Editor editor = pref.edit();

            userid=pref.getString("uid", null);

            Log.d("Shared",userid);

            String tag_string_req = "req_login";


            String cal=getCalories(food_to_db)+"";

            Log.d("form",userid);
            Log.d("form",servings);
            Log.d("form",cal);
            Log.d("form",food_to_db);



            HashMap<String, String> params = new HashMap<String, String>();
            params.put("userid", userid);
            params.put("quantity",servings);
            params.put("calories",cal);
            params.put("food",food_to_db);

            //Log.d("res",phoneNumber+" "+password);

            JsonObjectRequest strReq = new JsonObjectRequest(URL_POST_FOOD, new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                //create a login session
                                // session.setLogin(true);
                                String res = response.toString();

                                Log.d("food upload",res);
                                if(res.contains("")){

                                    Log.d("res",response.toString());

                                    Toast.makeText(getActivity(),"Success",Toast.LENGTH_LONG).show();

                                    progressDialog.dismiss();

                                    // SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE)


                                    //  MobileNumber.userMobileNumber=phoneNumber;
                                    //  Toast.makeText(getApplicationContext(), "Login Success!", Toast.LENGTH_SHORT).show();



                                    //   scrollView.setVisibility(View.GONE);

                                }
                                else{
//                                Intent intent=new Intent(Login.this,MainActivity.class);
//                                startActivity(intent);
//                                finish();
                                    Toast.makeText(getActivity(), "Invalid details", Toast.LENGTH_SHORT).show();
                                }


                            } catch (Exception e) {
                                // JSON error
                                e.printStackTrace();
                                Toast.makeText(getActivity(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.e("Error: ", error.getMessage());

                }
            });

// add the request object to the queue to be executed
            AppController.getInstance().addToRequestQueue(strReq, tag_string_req);



        }

    public double getCalories(String food_name){

        double value=-1;

        Log.d("tag",food_name);

        HashMap<String, Double> map = new HashMap<>();


        map.put("chocolate cake", 389.0);
        map.put("club sandwich", 220.0);
        map.put("donuts", 361.0);
        map.put("french fries", 312.0);
        map.put("fried rice", 163.0);
        map.put("ice cream", 192.0);
        map.put("omelette", 154.0);
        map.put("pizza", 266.0);
        map.put("samosa", 80.0);
        map.put("spring rolls", 154.0);
        map.put("waffles", 291.0);


        //  Log.d("log",map.get("chocolate cake").toString());


        if(map.containsKey(food_name))
        {
            Log.d("log","inside the loop");
            value = map.get(food_name);
            Camera2BasicFragment obj=new Camera2BasicFragment();
            obj.flag=true;
        }

        return value;

    }
    }