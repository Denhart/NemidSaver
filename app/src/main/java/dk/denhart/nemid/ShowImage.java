package dk.denhart.nemid;

/**
 * Created by Denhart on 09-08-2014.
 */
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import java.io.File;
import uk.co.senab.photoview.PhotoViewAttacher;
import static org.apache.commons.io.FileUtils.readFileToByteArray;

public class ShowImage extends Activity {
    PhotoViewAttacher mAttacher;
    ProgressDialog PD;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        File myImage = (File) getIntent().getExtras().getSerializable("myImage");
        new decryptTask().execute(myImage);
        SharedPreferences pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = pref.edit();
        ed.putBoolean("activity_executed", true); //Assume user is logged in at this point
        ed.commit();
    }
    protected void onRestart() {
        super.onRestart();
        Intent myIntent = new Intent(ShowImage.this, pinActivity.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        ShowImage.this.startActivity(myIntent);
    }

    protected void onPause(){
        super.onPause();
        SharedPreferences pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = pref.edit();
        ed.putBoolean("activity_executed", true); //Assume user is logged in at this point
        ed.commit();
    }

    protected void onDestroy(){
        super.onDestroy();
        Log.i("Hey", "IM HERE");

    }

    protected void onStop(){
        super.onStop();
    }
    private class decryptTask extends AsyncTask<File, Bitmap, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            PD = new ProgressDialog(ShowImage.this);
            PD.setMessage(getString(R.string.decrypting));
            PD.setCancelable(false);
            PD.show();
        }
        protected Bitmap doInBackground(File... myImage) {
            try {
                String b64IV = (String) getIntent().getExtras().getString("IV");
                byte[] data = readFileToByteArray(myImage[0]);
                //Encryption
                String pincode = Singleton.getInstance().getString();
                CryptoHandler pbe = new CryptoHandler(pincode.toCharArray());
                byte[] iv = Base64.decode(b64IV,Base64.DEFAULT);
                Crypto storage = new Crypto(iv,data);
                data = pbe.decrypt(storage);
                Bitmap bmp = BitmapFactory.decodeByteArray(data , 0, data.length);
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
                return bmp;
            } catch (Exception e){ }
            return null;
        }

        protected void onPostExecute(Bitmap bmp) {
            PD.dismiss();
            try {
                ImageView imageView = (ImageView) findViewById(R.id.imageView);
                imageView.setImageBitmap(bmp);
                mAttacher = new PhotoViewAttacher(imageView);
            } catch (Exception e){}
        }
    }
}
