package comp5216.sydney.edu.au.hw2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyCamera extends AppCompatActivity {
    private Button btn_camera_capture = null;
    private Button btn_camera_cancel = null;
    private Button btn_camera_ok = null;
    private Button btn_camera_cancel2 = null;

    private Camera camera = null;
    private MySurfaceView mySurfaceView = null;

    private byte[] buffer = null;

    private final int TYPE_FILE_IMAGE = 1;


    private Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            camera.setDisplayOrientation(90);
            if (data == null){
                Log.i("MyPicture", "picture taken data: null");
            }else{
                Log.i("MyPicture", "picture taken data: " + data.length);
            }

            buffer = new byte[data.length];
            buffer = data.clone();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_camera);

        btn_camera_capture = (Button) findViewById(R.id.camera_capture);
        btn_camera_ok = (Button) findViewById(R.id.camera_ok);
        btn_camera_cancel = (Button) findViewById(R.id.camera_cancel);
        btn_camera_cancel2 = (Button) findViewById(R.id.camera_cancel2);


        btn_camera_capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Capture image
                camera.takePicture(null, null, pictureCallback);

                btn_camera_capture.setVisibility(View.INVISIBLE);
                btn_camera_ok.setVisibility(View.VISIBLE);
                btn_camera_cancel.setVisibility(View.VISIBLE);
                btn_camera_cancel2.setVisibility(View.INVISIBLE);
            }
        });

        btn_camera_ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Save image
                saveImageToFile();
                setResult(RESULT_OK);
                finish();
            }
        });

        btn_camera_cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Re-capture image
                camera.startPreview();
                btn_camera_capture.setVisibility(View.VISIBLE);
                btn_camera_ok.setVisibility(View.INVISIBLE);
                btn_camera_cancel.setVisibility(View.INVISIBLE);
                btn_camera_cancel2.setVisibility(View.VISIBLE);
            }
        });

        btn_camera_cancel2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Quit camera
                finish();
            }
        });
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();

        camera.release();
        camera = null;
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (camera == null){
            camera = getCameraInstance();
        }

        mySurfaceView = new MySurfaceView(getApplicationContext(), camera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mySurfaceView);
    }

    private Camera getCameraInstance(){
        Camera camera = null;
        try{
            camera = camera.open();
        }catch(Exception e){
            e.printStackTrace();
        }
        return camera;
    }


    //Save image
    private void saveImageToFile(){
        File file = getOutFile(TYPE_FILE_IMAGE);


        if (file == null){
            Toast.makeText(getApplicationContext(), "Failed to create the file", Toast.LENGTH_SHORT).show();
            return ;
        }
        Log.i("MyPicture", "customize image path" + file.getPath());
        Toast.makeText(getApplicationContext(), "image path" + file.getPath(), Toast.LENGTH_SHORT).show();
        if (buffer == null){
            Log.i("MyPicture", "customize camera Buffer: null");
        }else{
            try{
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(buffer);
                fos.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    //Generate Uri
    //Get URI of output file
    private Uri getOutFileUri(int fileType) {
        return Uri.fromFile(getOutFile(fileType));
    }

    //Generate output file
    private File getOutFile(int fileType) {

        String storageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_REMOVED.equals(storageState)){
            Toast.makeText(getApplicationContext(), "No SD card", Toast.LENGTH_SHORT).show();
            return null;
        }

        File mediaStorageDir = new File (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"MyPictures");
        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                Log.i("MyPictures", "Failed to create the file path");
                Log.i("MyPictures", "mediaStorageDir : " + mediaStorageDir.getPath());
                return null;
            }
        }
        File file = new File(getFilePath(mediaStorageDir,fileType));

        return file;
    }
    //Generate output file path
    private String getFilePath(File mediaStorageDir, int fileType){
        String timeStamp =new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String filePath = mediaStorageDir.getPath() + File.separator;
        if (fileType == TYPE_FILE_IMAGE){
            filePath += ("IMG_" + timeStamp + ".jpg");
        }else{
            return null;
        }
        return filePath;
    }


}


