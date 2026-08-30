#!/bin/bash

source /opt/intel/openvino/bin/setupvars.sh

python3 image_enhancement/img_enhancement.py

python3 detection_classification/detection_model.py
