package dk.denhart.nemid;

/**
 * Created by Denhart on 08-08-2014.
 */
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import java.io.File;
import java.io.FileOutputStream;


import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.ErrorCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.text.format.Time;
import android.widget.TextView;
import android.widget.Toast;

public class CameraActivity extends Activity implements OnClickListener {
    EditText etName;
    Button btnSave, btnCancel;
    Dialog dialog;

    Activity context;
    Preview preview;
    Camera camera;

    String date;
    ImageView fotoButton;
    LinearLayout progressLayout;
    private String path;
    private String pIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        path = Environment.getExternalStorageDirectory().getAbsolutePath() + getString(R.string.imagePath);
        context=this;
        fotoButton = (ImageView) findViewById(R.id.imageView_foto);
        progressLayout = (LinearLayout) findViewById(R.id.progress_layout);
        preview = new Preview(this,
                (SurfaceView) findViewById(R.id.KutCameraFragment));
        FrameLayout frame = (FrameLayout) findViewById(R.id.preview);
        frame.addView(preview);
        preview.setKeepScreenOn(true);
        fotoButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                try {
                    takeFocusedPicture();
                } catch (Exception e) {}
                fotoButton.setClickable(false);
                progressLayout.setVisibility(View.VISIBLE);
            }
        });
        Time now = new Time();
        now.setToNow();
        date = now.format("%m%d%y%h%M%s");
                if(camera==null){
            camera = Camera.open();
            camera.startPreview();
            camera.setErrorCallback(new ErrorCallback() {
                public void onError(int error, Camera mcamera) {
                    camera.release();
                    camera = Camera.open();
                    Log.d("Camera died", "error camera");
                }
            });
        }
        if (camera != null) {
            if (Build.VERSION.SDK_INT >= 14)
                setCameraDisplayOrientation(context,
                        CameraInfo.CAMERA_FACING_BACK, camera);
            preview.setCamera(camera);
        }
    }

    protected void onRestart() {
        super.onRestart();
        Intent myIntent = new Intent(CameraActivity.this, pinActivity.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        CameraActivity.this.startActivity(myIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
/*        if(camera==null){
            camera = Camera.open();
            camera.startPreview();
            camera.setErrorCallback(new ErrorCallback() {
                public void onError(int error, Camera mcamera) {
                    camera.release();
                    camera = Camera.open();
                    Log.d("Camera died", "error camera");
                }
            });
        }
        if (camera != null) {
            if (Build.VERSION.SDK_INT >= 14)
                setCameraDisplayOrientation(context,
                        CameraInfo.CAMERA_FACING_BACK, camera);
            preview.setCamera(camera);
        }*/
    }

    private void setCameraDisplayOrientation(Activity activity, int cameraId,
                                             android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate the mirror
        } else { // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    Camera.AutoFocusCallback mAutoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            try{
                camera.takePicture(mShutterCallback, null, jpegCallback);
            }catch(Exception e){
            }
        }
    };

    Camera.ShutterCallback mShutterCallback = new ShutterCallback() {

        @Override
        public void onShutter() {


        }
    };
    public void takeFocusedPicture() {
        camera.autoFocus(mAutoFocusCallback);

    }

    PictureCallback jpegCallback = new PictureCallback() {
        @SuppressWarnings("deprecation")
        public void onPictureTaken(byte[] data, Camera camera) {
            new encryptTask().execute(data);
            showCustomDialog();
            fotoButton.setClickable(true);
            camera.startPreview();
            progressLayout.setVisibility(View.GONE);
        }
    };

    protected void onPause(){
        super.onPause();
        SharedPreferences pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = pref.edit();
        ed.putBoolean("activity_executed", true); //Assume user is logged in at this point
        ed.commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnsave:

                String search = etName.getText().toString().trim();
                if (TextUtils.isEmpty(search)) {
                    Toast.makeText(CameraActivity.this,
                            getString(R.string.noinput),
                            Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(CameraActivity.this,
                            getString(R.string.saved),
                            Toast.LENGTH_SHORT).show();
                            DatabaseHandler db = new DatabaseHandler(this);
                            db.addImage(new ImageDB(date + ".jpg", search, pIV));
                }
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                dialog.dismiss();
                Log.i("Path",path);
                super.finish();
                break;
            case R.id.btncancel:
                File file = new File(path + date + ".jpg");
                file.delete();
                InputMethodManager immm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                immm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                dialog.dismiss();
                Toast.makeText(CameraActivity.this,
                        getString(R.string.canceled),
                        Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    protected void showCustomDialog() {
        dialog = new Dialog(CameraActivity.this,
                R.style.Theme_D1NoTitleDim);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_name);
        etName = (EditText) dialog.findViewById(R.id.etname);
        etName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    btnSave.performClick();
                    return true;
                }
                return false;
            }
        });
        btnSave = (Button) dialog.findViewById(R.id.btnsave);
        btnCancel = (Button) dialog.findViewById(R.id.btncancel);
        btnSave.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        dialog.show();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
    }
    private class encryptTask extends AsyncTask<byte[], byte[], byte[]> {
        protected byte[] doInBackground(byte[]... data) {
            try {
                Log.i("INFO","STARTING ENCRYPTION NOW!");
                String pincode = Singleton.getInstance().getString();
                CryptoHandler pbe = new CryptoHandler(pincode.toCharArray());
                Crypto storage = pbe.encrypt(data[0]);
                data[0] = storage.getCiphertext();
                pIV = Base64.encodeToString(storage.getIv(),Base64.DEFAULT);
                Log.i("INFO","DONE ENCRYPTION NOW!");
            } catch (Exception e){}
            return data[0];
        }

        protected void onPostExecute(byte[] data) {
            try {
                FileOutputStream outStream;
                outStream = new FileOutputStream(path + date + ".jpg");
                outStream.write(data);
                outStream.close();
            } catch (Exception e){}
        }
    }
}