package iox.androidtensorflow;

import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import iox.androidtensorflow.TFTextClassification;

public class Dashboard extends AppCompatActivity {

    private static final String TAG = "Dashboard";
    private static final String MODEL_SETTING = "model";
    private TFTextClassification classifier;
    private AssetManager assetMngr;

    private void configApp() {

        //Getting TF settings
        SharedPreferences preferences = getSharedPreferences(MODEL_SETTING, MODE_PRIVATE);
        if (!preferences.contains("model_file")) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("model_file", "file:///android_asset/output_graph.pb");
            editor.putString("vocab_file", "vocab_dict.json");
            editor.putString("input_node", "input_x:0");
            editor.putString("output_node", "output/predictions32:0");
            editor.putString("num_classes", "2");
            editor.apply();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        configApp();
        classifier = new TFTextClassification();
        assetMngr = this.getAssets();

        SharedPreferences preferences = getSharedPreferences(MODEL_SETTING, MODE_PRIVATE);
        String model_file = preferences.getString("model_file", "");
        String vocab_file = preferences.getString("vocab_file", "");
        String input_node = preferences.getString("input_node", "");
        String output_node = preferences.getString("output_node", "");
        int num_classes = Integer.valueOf(preferences.getString("num_classes", ""));

        if (model_file.equalsIgnoreCase("") || vocab_file.equalsIgnoreCase("")) {
            Log.e(TAG, "Cannot read preferences!");
            return;
        }

        classifier.initialize(assetMngr, model_file, vocab_file,input_node, output_node, num_classes);

        // Example of a call to a native method
        //TextView tv = (TextView) findViewById(R.id.sample_text);
        //tv.setText(stringFromJNI());

    }

    public void btnSentimentOnClick(View view) {
        EditText editText = (EditText)findViewById(R.id.input_sentence);
        String text = editText.getText().toString();

        TextView txtSentiment = (TextView)findViewById(R.id.txt_sentiment);

        int result = classifier.classify(text);
        if (result == TFTextClassification.POS_LABEL)
            txtSentiment.setText("POSITIVE");
        else if(result == TFTextClassification.NEG_LABEL)
            txtSentiment.setText("NEGATIVE");
        else
            txtSentiment.setText("ERROR!");
    }
}
