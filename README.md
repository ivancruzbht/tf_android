# Text Classification with TensorFlow in Android

This is an Android Studio project of an Android app for english text classification (sentiment analysis) using TensorFlow

The CNN model and the vocab dictionary used by this app were generated with https://github.com/ivancruzbht/TextCNN

This was the way how I integrated it

Install tensorflow and do as described in:
https://github.com/tensorflow/tensorflow/tree/master/tensorflow/examples/android


### GRADLE:
-  I had to modify the gradle script to support the architecture of my test device and to integrate the tf native lib in the project (compile tf libs, build tf lite for android and then copy the lib to my project, then build the APK). 
-  I also had to modify the gradle script to include the java interfaces for the tf android lib. These are located in "/TF_LOCATION/tensorflow/contrib/android/java/" and "/TF_LOCATION/'tensorflow/java/src/main/java/org/tensorflow/"
-  I also have to add "aaptOptions "{ noCompress 'pb' }" to the gradle file because this indicates to not to compress the TF pb file (issue with TF on android. Details here: https://github.com/tensorflow/tensorflow/issues/5553)
-  If the project is built with Gradle,  the TF libs are built and integrated AFTER the APK is built. Therefore you should have to build the project twice in order to get the TF libs into the APK. (Any fix of this in the gradle build script is very welcome :))

### Dir Structure

-  You have to copy the model file (output.pb file) in the app app/src/main/assets/ along with the vocab json file.

Code:

The code explains it by itself :). The TFTextClassification java class is a good template for TF Classification in Android. The Dashboard Activity gets all the config that is used for TFTextClassification in order to classify something.