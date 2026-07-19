#!/bin/sh
export JAVA_HOME=/home/marvis/Marvis/User/oAN1i2eUlf_ndWWpP4W5Z2flQhbg/workspace/conv_19f61cc5e3c_804576e1b7a9/temp/jdk-17.0.19+10
export ANDROID_HOME=/home/marvis/Marvis/User/oAN1i2eUlf_ndWWpP4W5Z2flQhbg/workspace/conv_19f61cc5e3c_804576e1b7a9/temp/android_sdk
cd /home/marvis/Marvis/User/oAN1i2eUlf_ndWWpP4W5Z2flQhbg/workspace/conv_19f61cc5e3c_804576e1b7a9/temp/build_native/MusicPro-Native
exec "$JAVA_HOME/bin/java" -jar gradle/wrapper/gradle-wrapper.jar "$@"
