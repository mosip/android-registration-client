package io.mosip.registration_client.ocr.engine;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.label.TensorLabel;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.util.List;
import java.util.Map;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ops.TransformToGrayscaleOp;
import android.graphics.Matrix;

public class DocumentClassifier {
    private static final String TAG = "DocumentClassifier";
    private static final String MODEL_PATH = "Id_Classifier.tflite";
    private static final String LABELS_PATH = "labels.txt";
    private static final int INPUT_SIZE = 224;
    private static final float CONFIDENCE_THRESHOLD = 0.7f;

    /**
     * Plain classification outcome — label + confidence, nothing more.
     * There is deliberately no "known document types" allowlist here: the
     * .tflite model and labels.txt are swappable per deployment, so this
     * class has no business knowing what a valid label looks like. Below
     * {@link #CONFIDENCE_THRESHOLD}, documentType is "UNKNOWN" and is
     * passed through as-is rather than treated as a failure — what to do
     * with an unrecognized type (e.g. fall back to manual entry) is a
     * decision for later, not baked in here.
     */
    public static class ClassificationResult {
        @NonNull public final String documentType;
        public final float confidence;

        public ClassificationResult(@NonNull String documentType, float confidence) {
            this.documentType = documentType;
            this.confidence = confidence;
        }
    }

    private Interpreter interpreter;
    private List<String> labels;
    private ImageProcessor imageProcessor;

    public DocumentClassifier(@NonNull Context context) throws IOException {
        MappedByteBuffer modelBuffer = FileUtil.loadMappedFile(context, MODEL_PATH);
        Interpreter.Options options = new Interpreter.Options();
        options.setNumThreads(2);
        
        this.interpreter = new Interpreter(modelBuffer, options);
        this.labels = FileUtil.loadLabels(context, LABELS_PATH);
        this.imageProcessor = new ImageProcessor.Builder()
                .add(new ResizeOp(INPUT_SIZE, INPUT_SIZE, ResizeOp.ResizeMethod.BILINEAR))
                .add(new NormalizeOp(0.0f, 255.0f))
                .build();
    }

    @Nullable
    public ClassificationResult classify(@Nullable Bitmap bitmap) {
        if (interpreter == null || bitmap == null || bitmap.isRecycled()) return null;

        try {
            TensorImage tensorImage = new TensorImage(interpreter.getInputTensor(0).dataType());
            tensorImage.load(bitmap);
            tensorImage = imageProcessor.process(tensorImage);

            TensorBuffer probabilityBuffer = TensorBuffer.createFixedSize(
                    interpreter.getOutputTensor(0).shape(),
                    interpreter.getOutputTensor(0).dataType()
            );

            interpreter.run(tensorImage.getBuffer(), probabilityBuffer.getBuffer());

            Map<String, Float> labeledProbability =
                    new TensorLabel(labels, probabilityBuffer).getMapWithFloatValue();

            if (Log.isLoggable(TAG, Log.DEBUG)) {
                for (Map.Entry<String, Float> entry : labeledProbability.entrySet()) {
                    Log.d(TAG, "Class: " + entry.getKey() + " -> Confidence: " + entry.getValue());
                }
            }

            String bestLabel = null;
            float highestConfidence = 0.0f;

            for (Map.Entry<String, Float> entry : labeledProbability.entrySet()) {
                if (entry.getValue() > highestConfidence) {
                    highestConfidence = entry.getValue();
                    bestLabel = entry.getKey();
                }
            }

            if (bestLabel == null || highestConfidence < CONFIDENCE_THRESHOLD) {
                Log.w(TAG, "Top match [" + bestLabel + "] score (" + highestConfidence + ") below limit.");
                return new ClassificationResult("UNKNOWN", highestConfidence);
            }

            return new ClassificationResult(bestLabel, highestConfidence);

        } catch (Exception e) {
            Log.e(TAG, "Classification pipeline error", e);
            return null;
        }
    }
    public void close() {
        if (interpreter != null) {
            interpreter.close();
            interpreter = null;
        }
    }
    private Bitmap rotateBitmap(Bitmap source, float angle) {
    if (angle == 0) return source;
    Matrix matrix = new Matrix();
    matrix.postRotate(angle);
    Bitmap rotated = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    if (source != rotated && !source.isRecycled()) {
        source.recycle(); // Prevent memory leaks by freeing the old unrotated bitmap
    }
    return rotated;
}
}