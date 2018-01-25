package com.example.narasimha.ondevice;

/**
 * Created by narasimha on 7/12/17.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.util.Log;
import android.widget.Toast;

import org.tensorflow.*;
import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.util.Iterator;
import java.util.List;

public class TensorflowEngine {

    private static final int MAX_RESULTS = 3;
    private static final float THRESHOLD = 0.1f;
    Context c;
    TensorFlowInferenceInterface tensorFlowInferenceInterface;
    Session neural_network_session;
    TensorflowEngine(Context c, String model){
        this.c = c;
        tensorFlowInferenceInterface = new
                TensorFlowInferenceInterface(c.getAssets(), model);
    }

    public String graphOperations(String output_tensor, String input_tensor) {
        try {
            Iterator<Operation> names = tensorFlowInferenceInterface.graph().operations();
            StringBuffer labels = new StringBuffer();
            while(names.hasNext()){
                String line = names.next().name();
                if(line.equals("DecodeJpeg")) labels.append(line+"::");
            }
            Operation matrix = tensorFlowInferenceInterface.graphOperation("DecodeJpeg");
            labels.append(matrix.output(0).shape().toString());
            return labels.toString();
        }catch (Exception e){
            Toast.makeText(c, "Unable to load model", Toast.LENGTH_SHORT).show();
            return "";
        }
    }

    public int executeGraphOps(String input_tensor, String out_put, Bitmap image){
        try{
            float[] pixels = preprocessor(image);
            String[] output_names = {out_put};
            long st_time = System.nanoTime();
            tensorFlowInferenceInterface.feed(input_tensor, pixels, 1, 299, 299, 3);
            tensorFlowInferenceInterface.run(output_names);
            final Operation operation = tensorFlowInferenceInterface.graphOperation(out_put);
            final int numClasses = (int) operation.output(0).shape().size(1);
            float[] predictions = new float[numClasses+1];
            tensorFlowInferenceInterface.fetch(out_put, predictions);
            int best = 0;
            for(int i = 1; i<predictions.length; i++){
                if(predictions[i] > predictions[best]) best = i;
            }
            double time = (System.nanoTime() - st_time)/1e6;
            DomainsRepresentation.addTimeOnY(time);
            DomainsRepresentation.currentTime = time;
            return best;
        }catch (Exception e){
            Toast.makeText(c, e.toString(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return -1;
        }
    }

    private Tensor<Float> buildCustomDecoder(byte[] image)throws Exception{
        Graph decoder = new Graph();
        CustomJpegDecoderGraph gDecoder = new CustomJpegDecoderGraph(decoder);
        final Output<String> input = gDecoder.constant("input", image);
        final int H = 229;
        final int W = 229;
        final float mean = 117f;
        final float scale = 1f;
        final Output<Float> output =
                gDecoder.div(
                        gDecoder.sub(
                                gDecoder.resizeBilinear(
                                        gDecoder.expandDims(
                                                gDecoder.cast(gDecoder.decodeJpeg(input, 3), Float.class),
                                                gDecoder.constant("make_batch", 0)),
                                        gDecoder.constant("size", new int[] {H, W})),
                                gDecoder.constant("mean", mean)),
                        gDecoder.constant("scale", scale));
        return new Session(decoder).runner().fetch(output.op().name()).run().get(0).expect(Float.class);
    }

    public float[] preprocessor(Bitmap bm){
        float imageMean = 0.00f;
        float imageStd = 255.00f;
        int[] intvalues = new int[299*299];
        float[] glortUniform = new float[299*299*3];
        bm.getPixels(intvalues, 0,bm.getWidth(), 0,0, bm.getWidth() , bm.getHeight());
        for(int i = 0; i<intvalues.length; i++){
            final int val = intvalues[i];
            glortUniform[i * 3 + 0] = (((val >> 16) & 0xFF) - imageMean) / imageStd;
            glortUniform[i * 3 + 1] = (((val >> 8) & 0xFF) - imageMean) / imageStd;
            glortUniform[i * 3 + 2] = ((val & 0xFF) - imageMean) / imageStd;
        }
        return glortUniform;
    }

}
