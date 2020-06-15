#!/bin/bash
#runtests.sh
java A5 -f hw5_1.ps -a 0 -b 0 -c 499 -d 499 -j 0 -k 0 -o 499 -p 499 -s 1.0 -m 0 -n 0 -r 0 > out1.xpm
java A5 -f hw5_1.ps -a 0 -b 0 -c 499 -d 499 -j 0 -k 0 -o 499 -p 499 -s 0.5 -m 0 -n 0 -r 0 > out2.xpm
java A5 -f hw5_1.ps -a 0 -b 0 -c 499 -d 499 -j 0 -k 0 -o 499 -p 499 -s 1.0 -m 30 -n 50 -r 0 > out3.xpm
java A5 -f hw5_1.ps -a 0 -b 0 -c 499 -d 499 -j 0 -k 0 -o 499 -p 499 -s 1.0 -m 0 -n 0 -r 10 > out4.xpm
java A5 -f hw5_1.ps -a 0 -b 0 -c 499 -d 499 -j 0 -k 0 -o 499 -p 499 -s 0.5 -m 10 -n 20 -r 10 > out5.xpm
java A5 -f hw5_1.ps -a 50 -b 0 -c 325 -d 499 -j 0 -k 110 -o 480 -p 410 -s 1 -m 0 -n 0 -r 0 > out6.xpm
java A5 -f hw5_1.ps -a 10 -b 10 -c 550 -d 499 -j 10 -k 10 -o 500 -p 400 -s 1.2 -m 6 -n 25 -r 8 > out7.xpm
java A5 -f hw5_1.ps -b 62 -c 500 -d 479 -r 75 -j 139 -o 404 -p 461 -s .85 -m 300 > out8.xpm
java A5 -f hw5_1.ps -a 275 -b 81 -c 550 -d 502 -r -37 -j 123 -k 373 -p 467 > out9.xpm
java A5 -f hw5_1.ps -a -135 -b -53 -c 633 -d 842 -m -23 -j 101 -p 415 -s 3.6 > out10.xpm



