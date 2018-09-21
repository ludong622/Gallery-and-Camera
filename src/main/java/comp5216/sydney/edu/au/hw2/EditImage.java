package comp5216.sydney.edu.au.hw2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class EditImage extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    private Bitmap bitmap;
    private ImageView imageview;
    private SeekBar hueBar,saturationBar,brightnessBar;

    private float mHue,mSaturation ,mBrightness;
    private static int MAXVALUE=255,MIDVALUE=127;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_image);

        Intent i = getIntent();
        String f = i.getStringExtra("img");

        bitmap= BitmapFactory.decodeFile(f);
        imageview=(ImageView) findViewById(R.id.imageview);
        hueBar=(SeekBar) findViewById(R.id.hueBar);
        saturationBar=(SeekBar) findViewById(R.id.saturationBar);
        brightnessBar=(SeekBar) findViewById(R.id.brightnessBar);

        hueBar.setOnSeekBarChangeListener(this);
        saturationBar.setOnSeekBarChangeListener(this);
        brightnessBar.setOnSeekBarChangeListener(this);

        hueBar.setMax(MAXVALUE);
        hueBar.setProgress(MIDVALUE);
        saturationBar.setMax(MAXVALUE);
        saturationBar.setProgress(MIDVALUE);
        brightnessBar.setMax(MAXVALUE);
        brightnessBar.setProgress(MIDVALUE);

        imageview.setImageBitmap(bitmap);
    }

    public static Bitmap handleImageLikePS(Bitmap bp, float hue, float saturation, float lum){

        Bitmap bitmap=Bitmap.createBitmap(bp.getWidth(), bp.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas=new Canvas(bitmap);
        Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);

        ColorMatrix hueMatrix=new ColorMatrix();
        hueMatrix.setRotate(0, hue);
        hueMatrix.setRotate(1, hue);
        hueMatrix.setRotate(2, hue);

        ColorMatrix saturationMatrix=new ColorMatrix();
        saturationMatrix.setSaturation(saturation);

        ColorMatrix lumMatrix=new ColorMatrix();
        lumMatrix.setScale(lum,lum,lum,1);

        ColorMatrix imageMatrix=new ColorMatrix();
        imageMatrix.postConcat(hueMatrix);
        imageMatrix.postConcat(saturationMatrix);
        imageMatrix.postConcat(lumMatrix);

        paint.setColorFilter(new ColorMatrixColorFilter(imageMatrix));
        canvas.drawBitmap(bp, 0, 0, paint);//此处如果换成bitmap就会仅仅调用一次，图像将不能被编辑

        return bitmap;
    }

    public void onProgressChanged(SeekBar seekbar, int progress, boolean arg2) {
        switch(seekbar.getId()){
            case R.id.hueBar:
                mHue=(progress-MIDVALUE)*1.0F/MIDVALUE*180;
                break;
            case R.id.saturationBar:
                mSaturation=progress*1.0F/MIDVALUE;
                break;
            case R.id.brightnessBar:
                mBrightness=progress*1.0F/MIDVALUE;
                break;
        }
        imageview.setImageBitmap(handleImageLikePS(bitmap, mHue, mSaturation, mBrightness));
    }


    public void onStartTrackingTouch(SeekBar arg0) {
        // TODO Auto-generated method stub

    }


    public void onStopTrackingTouch(SeekBar arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public void onEditSave(View v) {

        Intent i = getIntent();
        String f = i.getStringExtra("img");
        saveBmpToPath(bitmap, f + ".");
        saveBmpToPath(handleImageLikePS(bitmap, mHue, mSaturation, mBrightness), f);
        Intent data = new Intent();
        data.putExtra("img", f);
        setResult(RESULT_OK, data);
        finish();
    }

    public void onEditUndo(View v) {
        hueBar.setProgress(MIDVALUE);
        saturationBar.setProgress(MIDVALUE);
        brightnessBar.setProgress(MIDVALUE);
    }

    public void onEditBack(View v) {
        finish();
    }

    public boolean saveBmpToPath(final Bitmap bitmap, final String filePath) {
        if (bitmap == null || filePath == null) {
            return false;
        }
        boolean result = false; //Default result
        File file = new File(filePath);
        OutputStream outputStream = null; //File output stream
        try {
            outputStream = new FileOutputStream(file);
            result = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream); //Compress image to JPEG format with maximum quality 100
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close(); //Close stream
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
}

