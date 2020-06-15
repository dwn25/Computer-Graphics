#!/bin/bash
#runtests.sh
javac A6.java
java A6 -f bound-lo-sphere.smf
java A6 -f bound-lo-sphere.smf -P
java A6 -f bound-lo-sphere.smf -q 1.0
java A6 -f bound-lo-sphere.smf -z 5.0 -q 1.0 -w -0.5
java A6 -f bound-lo-sphere.smf -k 125 -p 375 -q 1.0 -u -1.4
java A6 -f bound-cow.smf
java A6 -f bound-cow.smf -P
java A6 -f bound-cow.smf -j 0 -k 30 -o 275 -p 305 -P
java A6 -f bound-cow.smf -X 0.25 -Y -0.15 -Z 0.3
java A6 -f bound-cow.smf -X 0.25 -Y -0.15 -Z 0.3 -j 103 -k 143 -o 421 -p 379
java A6 -f bound-cow.smf -q -1 -r 1.5 -w -2.0
java A6 -f bound-cow.smf -Q 1.5 -R 1 -W 0.4
