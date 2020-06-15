#!/bin/bash
#runtests.sh
javac A7.java
java A7 –f bound-sprellpsd.smf
java A7 -f bound-cow.smf
java A7 -f bound-cow.smf -F 0.1 -B -0.18
java A7 -f bound-bunny_1k.smf -g bound-cow.smf -i bound-sprtrd.smf
java A7 -f bound-bunny_1k.smf -g bound-cow.smf -i bound-sprtrd.smf -q 0.4 -r 0.2 -w 1.0 -P

