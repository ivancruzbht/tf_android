package iox.androidtensorflow;

import android.content.res.AssetManager;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

public class TFTextClassification {
    private static final String TAG = "TFTextClassification";
    private Map<String,Integer> vocabMap = null;
    private int maxLenght = 0;

    private TensorFlowInferenceInterface TFInference;
    private String inputNode;
    private String outputNode;
    private int numClasses;
    private final int batchSize = 1;

    public static final int TFTC_ERROR = 1;
    public static final int POS_LABEL = 1;
    public static final int NEG_LABEL = 0;


    //Get an inverted index input for TFInference Lib
    private int[] buildInput(List<String> words){
        //TODO Gen arbitrary sentences input
        int[] input = new int[batchSize * maxLenght]; // 1 sentence by maxLenWords

        int i = 0;
        for(String word : words){
            int index = vocabMap.get(word);
            //TODO instead of mapping the word to 0, generate a _RARE_ vocab word
            input[i] = index;
            i++;

            if (i == maxLenght)
                break;
        }

        return input;
    }


    public int initialize(
            AssetManager assetManager,
            String modelFilename,
            String vocabFilename,
            String inputNode,
            String outputNode,
            int numClasses
    ){
        //Loading Vocab
        String vocabJson = null;
        try {
            this.inputNode = inputNode;
            this.outputNode = outputNode;
            this.numClasses = numClasses;

            BufferedReader br = new BufferedReader(new InputStreamReader(assetManager.open(vocabFilename)));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            vocabJson = sb.toString();
            br.close();

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Type type = new TypeToken<Map<String, Integer>>(){}.getType();
            vocabMap = gson.fromJson(vocabJson, type);
            maxLenght = vocabMap.get("__MAX_DOC_LEN__");
        }
        catch(Exception e){
            Log.e(TAG,e.toString());
            return TFTC_ERROR;
        }

        //Load TensorFlow Model
        TFInference = new TensorFlowInferenceInterface();
        return TFInference.initializeTensorFlow(assetManager, modelFilename);
    }

    public int classify(String text){
        int [] input = buildInput(TextPreprocess.preprocess(text));

        // TF Input
        TFInference.fillNodeInt(
                inputNode, new int[] {1, maxLenght}, input);

        TFInference.fillNodeFloat(
                "dropout_keep_prob", new int[] {1}, new float[] {(float)1.0});

        //TF Inference
        String [] outputNames = { this.outputNode };
        TFInference.runInference(outputNames);

        //TF output
        int [] outputs = new int[1];

        TFInference.readNodeInt(outputNode, outputs);

        //Find the best classification

        Log.d(TAG, "TF output: " + String.valueOf(outputs[0]));
        if (outputs[0] == 1)
            return POS_LABEL;
        if (outputs[0] == 0)
            return NEG_LABEL;

        return TFTC_ERROR;
    }
}
