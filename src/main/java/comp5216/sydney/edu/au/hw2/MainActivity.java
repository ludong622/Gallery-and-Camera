package comp5216.sydney.edu.au.hw2;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    GridView gv;
    ArrayList<File> list;

    public final int VIEW_IMAGE_REQUEST_CODE = 2;
    public final int TAKE_IMAGE_REQUEST_CODE = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            list = imageReader(Environment.getExternalStorageDirectory());
        } catch (IOException e) {
            e.printStackTrace();
        }

        gv = (GridView)findViewById(R.id.gridView);
        gv.setAdapter(new GridAdapter());
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startActivityForResult(new Intent(getApplicationContext(), ViewImage.class).putExtra("img", list.get(i).toString()), VIEW_IMAGE_REQUEST_CODE);
            }
        });

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (checkCameraHardWare(getApplicationContext())){
                    Intent intent = new Intent(getApplicationContext(), MyCamera.class);
                    startActivityForResult(intent, TAKE_IMAGE_REQUEST_CODE);
                }else{
                    Toast.makeText(getApplicationContext(), "No camera", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent i) {
        if (requestCode == VIEW_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_CANCELED) {
                try {
                    list = imageReader(Environment.getExternalStorageDirectory());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                gv.setAdapter(new GridAdapter());
            }
        }
        if (requestCode == TAKE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    list = imageReader(Environment.getExternalStorageDirectory());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                gv.setAdapter(new GridAdapter());
            }
        }
    }

    class GridAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            view = getLayoutInflater().inflate(R.layout.single_grid, viewGroup, false);
            ImageView iv = (ImageView) view.findViewById(R.id.imageView);

            iv.setImageURI(Uri.parse(getItem(i).toString()));

            return view;
        }
    }

    ArrayList<File> imageReader(File root) throws IOException {
        ArrayList<File> a = new ArrayList<>();
        File[] files = root.listFiles();
        for (File f : files){
            if (f.isDirectory()){
                a.addAll(imageReader(f));

            }
            else {
                if (f.getName().endsWith(".jpg")){
                    a.add(f);
                }
            }
        }
        return a;
    }

    //Check the existence of the camera
    private boolean checkCameraHardWare(Context context){
        PackageManager packageManager = context.getPackageManager();
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            return true;
        }
        return false;
    }


}
