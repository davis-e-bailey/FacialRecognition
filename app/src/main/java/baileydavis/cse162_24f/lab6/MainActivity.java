package baileydavis.cse162_24f.lab6;


import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity{
    ImageView iw;
    Canvas canvas;
    Bitmap mutableBitmap;
    private FaceDetector detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FaceDetectorOptions highAccuracyOpts = new FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
                .build();

        Bitmap bm = getBitmapFromAssets("faces.png");
        if (bm == null) {
            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            return;
        }

        iw = findViewById(R.id.imageView);
        iw.setImageBitmap(bm);

        mutableBitmap = bm.copy(Bitmap.Config.ARGB_8888, true);
        canvas = new Canvas(mutableBitmap);

        InputImage image = InputImage.fromBitmap(bm, 0);
        Log.d("TAG", "before recognition");

        detector = FaceDetection.getClient(highAccuracyOpts);

        detector.process(image)
                .addOnSuccessListener(
                        faces -> {
                            Log.d("TAG", "on success recognition succeed");
                            for (Face face : faces) {
                                Rect bounds = face.getBoundingBox();
                                Paint paint = new Paint();
                                paint.setAntiAlias(true);
                                paint.setColor(Color.RED);
                                paint.setStyle(Paint.Style.STROKE);
                                paint.setStrokeWidth(8);

                                canvas.drawRect(bounds, paint);

                                runOnUiThread(() -> iw.setImageBitmap(mutableBitmap));
                                Log.d("TAG", "recognition suceed");
                            }
                        })
                .addOnFailureListener(
                        e -> {
                            Log.e("TAG", "Face detection failed: " + e.getMessage());
                            Toast.makeText(getApplicationContext(), "Face detection failed. Please try again.", Toast.LENGTH_SHORT).show();
                        });
    }

    private Bitmap getBitmapFromAssets(String fileName){
        AssetManager am = getAssets();
        InputStream is = null;
        try{
            is = am.open(fileName);
        }catch(IOException e){
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeStream(is);
        return bitmap;
    }
}
