package iox.androidtensorflow;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TextPreprocess {

    //Just split the text into words by space. More complex preprocessing (steming, lemmatization, rare word etc)
    //should be handled in a preprocessing stage like this
    public static List<String> preprocess(String text){
        ArrayList<String> tokens = new ArrayList<>(Arrays.asList(text.split("\\s+")));
        return tokens;
    }
}
