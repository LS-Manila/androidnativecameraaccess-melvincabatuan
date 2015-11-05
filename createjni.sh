#!/bin/bash

echo 'copying jni folder to app...'
cp /home/cobalt/AndroidStudioProjects/jni app/  

echo 'creating header file...'
cd app 
javah -d jni -classpath $SDK/platforms/android-21/android.jar:./build/intermediates/classes/debug io.github.melvincabatuan.nativegrayscale.CameraPreview

echo 'done...refresh Android Studio project'
