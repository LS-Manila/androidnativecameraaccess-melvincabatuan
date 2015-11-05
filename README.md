# Android Native Camera Access through JNI 

androidnativecameraaccess-melvincabatuan created by Classroom for GitHub

This project illustrates the simple access of Android's Native Camera utilizing the Java Native Interface (JNI).

## Prerequisite:

1. Native Development Kit (NDK): http://developer.android.com/ndk/index.html 
2. OpenCV Library: http://opencv.org/

## Problem

Access the Native Camera and display the Grayscale image/video.

## Keypoints (JNI)

### Android.mk
```make
LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

OPENCV_LIB_TYPE:=STATIC
OPENCV_INSTALL_MODULES:=on

include /home/cobalt/Android/OpenCV-android-sdk/sdk/native/jni/OpenCV.mk

LOCAL_MODULE    := ImageProcessing
LOCAL_SRC_FILES := ImageProcessing.cpp
LOCAL_LDLIBS +=  -llog -ldl

include $(BUILD_SHARED_LIBRARY)
```

### Application.mk
```make
APP_STL := gnustl_static
APP_CPPFLAGS := -frtti -fexceptions
APP_ABI := armeabi-v7a
APP_PLATFORM := android-21
APP_MODULES := ImageProcessing
```

### Imageprocessing.cpp
```cpp
#include "io_github_melvincabatuan_nativegrayscale_CameraPreview.h"
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>

using namespace std;
using namespace cv;

Mat *temp = NULL;

/*
 * Class:     io_github_melvincabatuan_nativegrayscale_CameraPreview
 * Method:    ImageProcessing
 * Signature: (II[B[I)Z
 */
JNIEXPORT jboolean JNICALL Java_io_github_melvincabatuan_nativegrayscale_CameraPreview_ImageProcessing(JNIEnv *env, jobject thiz, jint width, jint height, jbyteArray NV21FrameData, jintArray outPixels){

	jbyte *pNV21FrameData = env->GetByteArrayElements(NV21FrameData, 0);
	jint *poutPixels = env->GetIntArrayElements(outPixels, 0);

	if ( temp == NULL )
    	{
    		temp = new Mat(height, width, CV_8UC1);
    	}

	Mat mGray(height, width, CV_8UC1, (unsigned char *)pNV21FrameData);
	Mat mResult(height, width, CV_8UC4, (unsigned char *)poutPixels);
	//Mat OtsuImg = *temp;
     	//int thresh = 0;

	//threshold( mGray, OtsuImg, thresh, 255, THRESH_BINARY | THRESH_OTSU );
	/// thresh = 0 is ignored

	///cvtColor(OtsuImg, mResult, CV_GRAY2BGRA);

        cvtColor(mGray, mResult, CV_GRAY2BGRA);

	env->ReleaseByteArrayElements(NV21FrameData, pNV21FrameData, 0);
	env->ReleaseIntArrayElements(outPixels, poutPixels, 0);
	return true;
}
```

## Accept

To accept the assignment, click the following URL:

https://classroom.github.com/assignment-invitations/d45adc7a750fb7ae839c5aaf97934600

## Sample Solution:

https://github.com/DeLaSalleUniversity-Manila/androidnativecameraaccess-melvincabatuan

## Submission Procedure with Git: 

```shell
$ cd /path/to/your/android/app/
$ git init
$ git add –all
$ git commit -m "your message, e.x. Assignment 1 submission"
$ git remote add origin <Assignment link copied from assignment github, e.x. https://github.com/DeLaSalleUniversity-Manila/secondactivityassignment-melvincabatuan.git>
$ git push -u origin master
<then Enter Username and Password>
```


## Screenshots:


![alt tag](https://github.com/DeLaSalleUniversity-Manila/androidnativecameraaccess-melvincabatuan/blob/master/device-2015-11-06-072111.png)

![alt tag](https://github.com/DeLaSalleUniversity-Manila/androidnativecameraaccess-melvincabatuan/blob/master/device-2015-11-06-072455.png)

"*To be yourself in a world that is constantly trying to make you something else is the greatest accomplishment.*" - Ralph Waldo Emerson
