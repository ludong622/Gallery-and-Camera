package comp5216.sydney.edu.au.hw2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ViewImage extends Activity {

    ImageView iv2;
    public final int EDIT_IMAGE_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        Intent i = getIntent();
        String f = i.getStringExtra("img");
        iv2 = (ImageView)findViewById(R.id.imageView2);
        iv2.setImageURI(Uri.parse(f));
    }

    public void onBack(View v) {
        setResult(RESULT_CANCELED);
        finish();
    }

    public void onEdit(View v) {

        Intent i = getIntent();
        String f = i.getStringExtra("img");
        startActivityForResult(new Intent(getApplicationContext(), EditImage.class).putExtra("img", f), EDIT_IMAGE_REQUEST_CODE);

    }

    public void onUndo(View v) {
        Intent i = getIntent();
        String f = i.getStringExtra("img");
        Bitmap bitmap= BitmapFactory.decodeFile(f + ".");
        saveBmpToPath(bitmap, f);
        iv2.setImageBitmap(bitmap);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent i) {
        if (requestCode == EDIT_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Extract name value from result extras
                String st = i.getStringExtra("img");
                Bitmap bitmap= BitmapFactory.decodeFile(st);
                iv2.setImageBitmap(bitmap);
            }
        }
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
