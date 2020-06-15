Implementation of Sutherland-Hodgman polygon clipping algorithm<br />
Program was written and compiled in java using IntelliJ on MacOS X 10.14.1. A class has been added to with the java file in order to streamline testing.
The bash script has been edited and submitted so that it may be run and will output all the files in the right order with the necessary adjustments to 
the ps file outputted in the xpm file format. Running the bash file can be achieved by typing "./runtests.sh" in terminal. One must ensure that the input
file is within the same enclosing file as the program. The file will be uploaded with the java class file in order to ensure that the program can be run.
A sample run was conducted and the corresponding class file that was created will be left in the enclosing file.  In the chance that the program does not
run with the java class that was uploaded with the java file, please type "javac A5.java" to create the class file. The individual test cases can then be
entered into terminal in the chance that the bash file fails to function as expected. The test cases should be entered in the form of; 
"java A5 -f hw5_1.ps -a 0 -b 0 -c 499 -d 499 -j 0 -k 0 -o 499 -p 499 -s 1.0 -m 0 -n 0 -r 10 > out4.xpm". The commented out code were used for testing. 
Homogenous coordinates were used for the transformations of the x and y coordinates. Wasn't specific as to the extent to which homogeneous coordinates 
should be used so they were used in conjunction with ArrayLists where the homogenous coordinates were taken from stored values in array lists and then 
used to perform the transformation in the order Scale, Rotate and Translate where applicable and then stored into arrayLists for a a better understanding 
or convenient transference between functions. The reference list can be found below.

