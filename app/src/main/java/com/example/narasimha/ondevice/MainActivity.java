package com.example.narasimha.ondevice;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.hardware.camera2.*;

import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraUtils;
import com.otaliastudios.cameraview.CameraView;

import java.io.File;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    CameraView cameraView;
    TextView classlabels;
    ImageView preview;
    TensorflowEngine inference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toast.makeText(this, "Loaded Activity", Toast.LENGTH_SHORT);
        Button button = findViewById(R.id.button2);
        button.setOnClickListener(this);
        cameraView = findViewById(R.id.cam_stream);
        preview = findViewById(R.id.preview_test);
        cameraView.addCameraListener(cameraListener);
        cameraView.start();
        //cache all labels
        InferenceCache.classLabels = initMaps();
        classlabels = findViewById(R.id.class_test);
        inference = new TensorflowEngine(this, "inception.pb");
    }

    CameraListener cameraListener = new CameraListener() {
        @Override
        public void onOrientationChanged(int orientation) {
            super.onOrientationChanged(orientation);
            Toast.makeText(MainActivity.this, "Orientation changed", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPictureTaken(byte[] jpeg) {
            super.onPictureTaken(jpeg);
            CameraUtils.decodeBitmap(jpeg, bitmapCallback);
        }
    };
    CameraUtils.BitmapCallback bitmapCallback = new CameraUtils.BitmapCallback() {
        @Override
        public void onBitmapReady(Bitmap bitmap) {

            int prediction = inference.executeGraphOps("Mul", "softmax", Bitmap.createScaledBitmap(bitmap,
                    299,299, true));
            classlabels.setText(getLabel(prediction)+"::time: "+DomainsRepresentation.currentTime+"ms");
        }
    };

    String getLabel(int prediction){
        return InferenceCache.classLabels.get(prediction);
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraView.stop();
    }

    @Override
    protected void onStop() {
        super.onStop();
        cameraView.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraView.destroy();
    }

    @Override
    protected void onResume(){
        super.onResume();
        cameraView.start();
    }


    public void tensorflow(){
        Uri url = Uri.parse("https://www.tensorflow.org/");
        Intent browser = new Intent(Intent.ACTION_VIEW, url);
        startActivity(browser);
    }

    public void settings(View v){
        startActivity(new Intent(this, ModelConfiguration.class));
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button2:
                tensorflow();
                break;
        }
    }

    private Map<Integer, String> initMaps(){
        try{
            ClassMapper reducer = new ClassMapper();
            MapLabels mapper = new MapLabels();
            Map<String, String> labels = mapper.loadLabels(
                    getAssets().open("labels.txt")
            );
            Map<Integer, String> nodes = mapper.loadNodes(
                    getAssets().open("labelmap.pbtxt")
            );
            return reducer.reduceMaps(nodes, labels);
        }catch (Exception e){
            return null;
        }
    }

    private void testLabels(int softmax0_maxIndex){
        if(InferenceCache.classLabels.containsKey(softmax0_maxIndex))
            classlabels.setText(InferenceCache.classLabels.get(softmax0_maxIndex));
        else classlabels.setText(InferenceCache.classLabels.keySet().toString());
    }

    public void capture_frame(View v){
        cameraView.capturePicture();
    }

    public void prepareGraphAndDisplay(View v){
        startActivity(new Intent(this, Statistics.class));
    }
}
