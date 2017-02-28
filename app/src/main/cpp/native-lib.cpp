#include <jni.h>
#include <string>

extern "C"
jstring
Java_io_venuelytics_mobileclient_Dashboard_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
