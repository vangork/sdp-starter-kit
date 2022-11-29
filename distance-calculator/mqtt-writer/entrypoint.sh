#!/bin/bash
currDate=$(date +'%m/%d/%Y')
sed -i 's|$currDate|'"$currDate"'|' Distance.csv
java -jar mqtt-writer-1.0.0.jar
