package com.calmatrixoffline;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;

public class FoodClassifier {

    private static final String TAG = "FoodClassifier";
    private Interpreter interpreter;
    private static final int INPUT_SIZE = 224;
    private boolean isQuantized = false;

    // ============================================================
    // FOOD-11 CLASSES (11 classes)
    // ============================================================
    /*
    private static final String[] FOOD_CLASSES = {
            "bread", "dairy", "dessert", "egg", "fried_food",
            "meat", "noodles_pasta", "rice", "seafood", "soup", "vegetables"
    };

     */

    private static final String[] FOOD_CLASSES = {
            "baked_potato",
            "burger",
            "crispy_chicken",
            "donut",
            "fries",
            "hot_dog",
            "pizza",
            "sandwich",
            "taco",
            "taquito"
    };

    // ============================================================
    // NAME MAP: Map model output to database names
    // ============================================================
    /*
    private static final Map<String, String> NAME_MAP = new HashMap<>();
    static {
        NAME_MAP.put("bread", "bread");
        NAME_MAP.put("dairy", "dairy");
        NAME_MAP.put("dessert", "dessert");
        NAME_MAP.put("egg", "egg");
        NAME_MAP.put("fried_food", "fried food");
        NAME_MAP.put("meat", "meat");
        NAME_MAP.put("noodles_pasta", "noodles");
        NAME_MAP.put("rice", "rice");
        NAME_MAP.put("seafood", "seafood");
        NAME_MAP.put("soup", "soup");
        NAME_MAP.put("vegetables", "vegetables");
    }

    */
    private static final Map<String, String> NAME_MAP = new HashMap<>();
    static {
        NAME_MAP.put("baked_potato", "baked potato");
        NAME_MAP.put("burger", "burger");
        NAME_MAP.put("crispy_chicken", "crispy chicken");
        NAME_MAP.put("donut", "donut");
        NAME_MAP.put("fries", "fries");
        NAME_MAP.put("hot_dog", "hot dog");
        NAME_MAP.put("pizza", "pizza");
        NAME_MAP.put("sandwich", "sandwich");
        NAME_MAP.put("taco", "taco");
        NAME_MAP.put("taquito", "taquito");
    }

    // ============================================================
    // CONSTRUCTOR
    // ============================================================
    public FoodClassifier(Context context) {
        try {
            Log.d(TAG, "Loading model from assets...");
            InputStream modelStream = context.getAssets().open("food_model.tflite");
            byte[] modelBytes = new byte[modelStream.available()];
            modelStream.read(modelBytes);
            modelStream.close();

            ByteBuffer modelBuffer = ByteBuffer.allocateDirect(modelBytes.length);
            modelBuffer.put(modelBytes);
            modelBuffer.rewind();

            interpreter = new Interpreter(modelBuffer);

            DataType inputType = interpreter.getInputTensor(0).dataType();
            isQuantized = (inputType == DataType.UINT8);
            Log.d(TAG, "Model loaded. Input type: " + (isQuantized ? "UINT8" : "FLOAT32"));

        } catch (Exception e) {
            Log.e(TAG, "Failed to load model: " + e.getMessage());
            interpreter = null;
        }
    }

    // ============================================================
    // CLASSIFY METHOD (with dbHelper for corrections)
    // ============================================================

    public ClassificationResult classifyFood(Bitmap bitmap, DatabaseHelper dbHelper) {
        if (interpreter == null) {
            return new ClassificationResult("food", 0.0f);
        }

        // Resize to 224x224
        Bitmap resized = Bitmap.createScaledBitmap(bitmap, 224, 224, true);

        // Create float32 buffer with normalized values [0, 1]
        ByteBuffer inputBuffer = ByteBuffer.allocateDirect(224 * 224 * 3 * 4);
        inputBuffer.order(ByteOrder.nativeOrder());

        int[] pixels = new int[224 * 224];
        resized.getPixels(pixels, 0, 224, 0, 0, 224, 224);

        for (int pixel : pixels) {
            // Extract RGB and normalize to [0, 1]
            float r = ((pixel >> 16) & 0xFF) / 255.0f;
            float g = ((pixel >> 8) & 0xFF) / 255.0f;
            float b = (pixel & 0xFF) / 255.0f;
            inputBuffer.putFloat(r);
            inputBuffer.putFloat(g);
            inputBuffer.putFloat(b);
        }

        // Run inference
        float[][] output = new float[1][10];  // 10 classes
        interpreter.run(inputBuffer, output);

        // Find best match
        int bestIndex = 0;
        float bestScore = 0;
        for (int i = 0; i < 10; i++) {
            if (output[0][i] > bestScore) {
                bestScore = output[0][i];
                bestIndex = i;
            }
        }

        Log.d(TAG, "Best score: " + bestScore + ", index: " + bestIndex);

        if (bestScore < 0.5f) {
            return new ClassificationResult("food", bestScore);
        }

        String modelOutput = FOOD_CLASSES[bestIndex];
        // ... rest of your code (corrections, mapping) ...
        // Check correction
        if (dbHelper != null) {
            String correction = dbHelper.getCorrection(modelOutput);
            if (correction != null && !correction.isEmpty()) {
                return new ClassificationResult(correction, bestScore);
            }
        }

        // Map to friendly name
        if (NAME_MAP.containsKey(modelOutput)) {
            return new ClassificationResult(NAME_MAP.get(modelOutput), bestScore);
        }

        return new ClassificationResult(modelOutput.replace("_", " "), bestScore);
    }
    public ClassificationResult classifyFood_old2(Bitmap bitmap, DatabaseHelper dbHelper) {
        if (interpreter == null) {
            return new ClassificationResult("food", 0.0f);
        }

        // Preprocess: resize to 224x224
        Bitmap resized = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, true);

        // Convert to float32 buffer in [0,1] range
        ByteBuffer inputBuffer = ByteBuffer.allocateDirect(INPUT_SIZE * INPUT_SIZE * 3 * 4);
        inputBuffer.order(ByteOrder.nativeOrder());

        int[] pixels = new int[INPUT_SIZE * INPUT_SIZE];
        resized.getPixels(pixels, 0, INPUT_SIZE, 0, 0, INPUT_SIZE, INPUT_SIZE);

        for (int pixel : pixels) {
            float r = ((pixel >> 16) & 0xFF) / 255.0f;
            float g = ((pixel >> 8) & 0xFF) / 255.0f;
            float b = (pixel & 0xFF) / 255.0f;
            inputBuffer.putFloat(r);
            inputBuffer.putFloat(g);
            inputBuffer.putFloat(b);
        }

        // Run inference
        float[][] output = new float[1][FOOD_CLASSES.length];
        try {
            interpreter.run(inputBuffer, output);
        } catch (Exception e) {
            Log.e(TAG, "Inference failed: " + e.getMessage());
            return new ClassificationResult("food", 0.0f);
        }

        // Find best match
        int bestIndex = 0;
        float bestScore = 0;
        for (int i = 0; i < FOOD_CLASSES.length; i++) {
            if (output[0][i] > bestScore) {
                bestScore = output[0][i];
                bestIndex = i;
            }
        }

        Log.d(TAG, "Best index: " + bestIndex + ", score: " + bestScore);

        if (bestScore < 0.5f) {
            return new ClassificationResult("food", bestScore);
        }

        String modelOutput = FOOD_CLASSES[bestIndex];

        // Check correction
        if (dbHelper != null) {
            String correction = dbHelper.getCorrection(modelOutput);
            if (correction != null && !correction.isEmpty()) {
                return new ClassificationResult(correction, bestScore);
            }
        }

        // Map to friendly name
        if (NAME_MAP.containsKey(modelOutput)) {
            return new ClassificationResult(NAME_MAP.get(modelOutput), bestScore);
        }

        return new ClassificationResult(modelOutput.replace("_", " "), bestScore);
    }





    //
    public ClassificationResult classifyFood_old1(Bitmap bitmap, DatabaseHelper dbHelper) {//string->classificationresult 26082517
        if (interpreter == null) {
            Log.e(TAG, "Interpreter is null");
            //return "food";
            return new ClassificationResult("food", 0.0f);
        }

        // Preprocess image
        Bitmap resized = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, true);
        Object inputBuffer;

        if (isQuantized) {
            // UINT8 input: 0-255
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(INPUT_SIZE * INPUT_SIZE * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            int[] pixels = new int[INPUT_SIZE * INPUT_SIZE];
            resized.getPixels(pixels, 0, INPUT_SIZE, 0, 0, INPUT_SIZE, INPUT_SIZE);

            for (int pixel : pixels) {
                byte r = (byte) ((pixel >> 16) & 0xFF);
                byte g = (byte) ((pixel >> 8) & 0xFF);
                byte b = (byte) (pixel & 0xFF);
                byteBuffer.put(r);
                byteBuffer.put(g);
                byteBuffer.put(b);
            }
            inputBuffer = byteBuffer;
        } else {
            // FLOAT32 input: 0.0-1.0
            ByteBuffer floatBuffer = ByteBuffer.allocateDirect(INPUT_SIZE * INPUT_SIZE * 3 * 4);
            floatBuffer.order(ByteOrder.nativeOrder());

            int[] pixels = new int[INPUT_SIZE * INPUT_SIZE];
            resized.getPixels(pixels, 0, INPUT_SIZE, 0, 0, INPUT_SIZE, INPUT_SIZE);

            for (int pixel : pixels) {
                float r = ((pixel >> 16) & 0xFF) / 255.0f;
                float g = ((pixel >> 8) & 0xFF) / 255.0f;
                float b = (pixel & 0xFF) / 255.0f;
                floatBuffer.putFloat(r);
                floatBuffer.putFloat(g);
                floatBuffer.putFloat(b);
            }
            inputBuffer = floatBuffer;
        }

        // Run inference
        float[][] output = new float[1][FOOD_CLASSES.length];
        try {
            interpreter.run(inputBuffer, output);
        } catch (Exception e) {
            Log.e(TAG, "Inference failed: " + e.getMessage());
            //return "food";
            return new ClassificationResult("food", 0.0f);  //26082518
        }

        // ============================================================
        // FIND BEST MATCH — THIS IS WHERE THE VALUES ARE SET
        // ============================================================  26082518
        // Find best match
        int bestIndex = 0;
        float bestScore = 0;

        // Get model output
        //String modelOutput = FOOD_CLASSES[bestIndex];nonsense 26082518

        for (int i = 0; i < FOOD_CLASSES.length; i++) {
            if (output[0][i] > bestScore) {
                bestScore = output[0][i];
                bestIndex = i;
            }
        }

        Log.d(TAG, "Best index: " + bestIndex + ", score: " + bestScore);

        ///============================================================
        // CHECK CONFIDENCE FIRST
        // ============================================================
        // If confidence is too low, return "food"  0.2->0.5 2600082513
        if (bestScore < 0.5f) {
            //return "food"; 26082518
            return new ClassificationResult("food", bestScore);
        }


        //2608251724
        // ============================================================
        // GET MODEL OUTPUT
        // ============================================================


        // Get the model's raw output
        String modelOutput = FOOD_CLASSES[bestIndex];
        Log.d(TAG, "Model output: " + modelOutput);





        // ============================================================
        // PRIORITY 1: Check user correction
        // ============================================================
        if (dbHelper != null) {
            String correction = dbHelper.getCorrection(modelOutput);
            if (correction != null && !correction.isEmpty()) {
                Log.d(TAG, "Using user correction: " + correction);
                //return correction;
                return new ClassificationResult(correction, bestScore);
            }
        }

        // ============================================================
        // PRIORITY 2: Check NAME_MAP
        // ============================================================
        if (NAME_MAP.containsKey(modelOutput)) {
            String mapped = NAME_MAP.get(modelOutput);
            Log.d(TAG, "Mapped to: " + mapped);
            //return mapped;
            return new ClassificationResult(mapped, bestScore);
        }

        // ============================================================
        // PRIORITY 3: Fallback — clean the name
        // ============================================================
        String cleaned = modelOutput.replace("_", " ");
        Log.d(TAG, "Fallback: " + cleaned);
        //return cleaned;
        return new ClassificationResult(cleaned, bestScore);
    }



    // Add this inside FoodClassifier.java 2608151724
    public static class ClassificationResult {
        public String foodName;
        public float confidence;

        public ClassificationResult(String foodName, float confidence) {
            this.foodName = foodName;
            this.confidence = confidence;
        }
    }
}