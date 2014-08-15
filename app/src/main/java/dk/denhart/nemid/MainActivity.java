package dk.denhart.nemid;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import java.io.File;
import java.util.List;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.view.View.OnClickListener;



/*
* TODO:
* Handle Exceptions properly!
* Add a plus in actionbar!
* Spotify like dialogs!0
* */

public class MainActivity extends Activity implements OnClickListener {
    Button btnSearch, btnCancel;
    Dialog dialog;
    private String filename;
    private String filepath;
    DatabaseHandler db = new DatabaseHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        filepath = Environment.getExternalStorageDirectory().getAbsolutePath() + getString(R.string.imagePath);
    }

    @Override
    protected void onStart(){
        super.onStart();
        SharedPreferences pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        if(!pref.getBoolean("activity_executed", false)) {
            Intent myIntent = new Intent(MainActivity.this, pinActivity.class);
            myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            MainActivity.this.startActivity(myIntent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.activity_main);
        List<String> fileNicks = db.getAllImagesString();
        final ListView fileListView = (ListView) findViewById(R.id.listView2);
 //       ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
 //               android.R.layout.simple_list_item_1, fileNicks); //
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
        R.layout.rowlayout, R.id.label, fileNicks);
        fileListView.setAdapter(adapter);
        fileListView.setLongClickable(true);
        fileListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageDB image = db.getFilenameOnDisk((String) fileListView.getAdapter().getItem(position));
                File f = new File(filepath + image.getFileName());
                String IV = image.getIV();
                Bundle bundle = new Bundle();
                bundle.putSerializable("myImage",f);
                bundle.putString("IV",IV);
                Intent myIntent = new Intent(MainActivity.this, ShowImage.class);
                myIntent.putExtras(bundle);
                MainActivity.this.startActivity(myIntent);
            }
        });
        fileListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View view,
                                           int pos, long id) {
                showCustomDialog((String) fileListView.getAdapter().getItem(pos));
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings || id == R.id.action_refresh) {
            Intent myIntent = new Intent(MainActivity.this, CameraActivity.class);
            MainActivity.this.startActivity(myIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onStop(){
        super.onStop();
        SharedPreferences pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = pref.edit();
        ed.putBoolean("activity_executed", false);
        ed.commit();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.del_yes:
                    Toast.makeText(MainActivity.this,
                            getString(R.string.deleted),
                            Toast.LENGTH_SHORT).show();
                deleteEntry(filename);
                redrawList();
                dialog.dismiss();
                break;
            case R.id.del_no:
                Toast.makeText(MainActivity.this,
                        getString(R.string.canceled),
                        Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                break;
            default:
                break;
        }
    }

    protected void showCustomDialog(String name) {
        filename = name;
        dialog = new Dialog(MainActivity.this,
                 R.style.Theme_D1NoTitleDim);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_delete);
        btnSearch = (Button) dialog.findViewById(R.id.del_yes);
        btnCancel = (Button) dialog.findViewById(R.id.del_no);
        btnSearch.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        dialog.show();
    }

    protected void deleteEntry(String fileEntry){
        ImageDB delImg = db.getFilenameOnDisk(fileEntry);
        db.deleteImageEntry(delImg);
        File file = new File(filepath + delImg.getFileName());
        file.delete();
    }

    protected void redrawList(){
        List<String> fileNicks = db.getAllImagesString();
        ListView fileListView = (ListView) findViewById(R.id.listView2);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.rowlayout, R.id.label, fileNicks);
        fileListView.setAdapter(adapter);
    }
}
