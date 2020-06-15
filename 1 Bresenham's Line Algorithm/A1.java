/**
 *
 * @author DNartey
 */
import java.io.*;
import java.util.*;

public class A1 {

    static String file;
    public static int  x1 ,y1 ,x0, y0, dx, dy, og_x, og_y, rid2, error, margin_error;
    static List<List<Integer>> points = new ArrayList<List<Integer>>();
    static List<List<Integer>> line = new ArrayList<List<Integer>>();
    static List<Integer> xy = new ArrayList<>();
    static int matrix[][] = new int [500][500];
    String begin_delim = "%%%BEGIN";
    String end_delim = "%%%END";



    public static void bres(){
        //Implementation of Bresenham's Line Drawing Algorithm
        for(int i=0; i< points.size(); i++) {

            x0 = points.get(i).get(0);
            y0 = points.get(i).get(1);
            x1 = points.get(i).get(2);
            y1 = points.get(i).get(3);

            dx = Math.abs(x1-x0);
            dy = Math.abs(y1-y0);
            int m = dx-dy;

            //m = (dy-dx);
            error = 0;
            if(x0<x1){
                og_x = 1;
            }
            else{
                og_x = -1;
            }

            if(y0<y1){
                og_y = 1;
            }
            else{
                og_y = -1;
            }

            while(!(x0 == x1) || !(y0 == y1)){
                int rid2 = 2 * m;
                if(rid2>-dy){
                    m = m-dy;
                    x0 = x0+og_x;
                }
                if(rid2<dx){
                    m = m+dx;
                    y0 = y0+og_y;
                }

                List<Integer> xy = new ArrayList<>();
                xy.add(x0);
                xy.add(y0);
                matrix[x0][y0]=1;
                line.add(xy);


        }

        }
        //System.out.println(line);
    }

        /*public static void format(int[] format){

        for(int x : format){
            System.out.println(x);
            //System.out.println("\t");
        }
        System.out.println();

    }
    */


    public static void openIn(String file){
        try{

            //input file from arguments to be opened using Scanner
            Scanner in = new Scanner(new File(file));
            //Loop to traverse the file line by line
            while (in.hasNextLine()) {
                //if function that looks for the first delimiter
                //if(in.nextLine.equals(begin_delim){
                if (in.nextLine().equals("%%%BEGIN")) {
                    //if that first delimiter is found, read in the data line by line into a single string
                    while (in.hasNextLine()) {
                        String input = in.nextLine();

                        //end or break if input is the last delimiter
                        //if (input.equals(end_delim)) {
                        if (input.equals("%%%END")) {
                            break;
                        }

                        String Line[] = input.split(" ");
                        int x0 = Integer.parseInt(Line[0]);
                        xy.add(x0);

                        int y0 = Integer.parseInt(Line[1]);
                        xy.add(y0);

                        int x1 = Integer.parseInt(Line[2]);
                        xy.add(x1);

                        int y1 = Integer.parseInt(Line[3]);
                        xy.add(y1);


                        //List<Integer> x1y1 = new ArrayList<>();
                        //x1y1.add(x2);
                        //x1y1.add(y2);

                        points.add(xy);
                        //System.out.println(points);
                        //points.add(x1y1);

                        for (int x = 0; x < matrix.length; x++) {
                            for (int y = 0; y < matrix.length; y++) {
                                matrix[x][y] = 0;
                            }
                        }
                    }
                }
            }

        in.close();
        }
        catch(FileNotFoundException err){
            System.out.println("Unable to load in file");

        }
    }

    public static void xpm_out(String file){
        //int canvas[][];
        try {
            try {

                PrintWriter xpm_File = new PrintWriter(file.substring(0,5) + ".xpm", "UTF-8");
                xpm_File.println("/*XPM*/");
                xpm_File.println("static char *sco100[]={" + '\n' + "//* width height num_colors chars_per_pixel */");
                xpm_File.println("\"500 500 2 1,\"");
                xpm_File.println("/* colors */");
                xpm_File.println("\"0 c #ffffff,\"" + '\n' + "\"1 c #000000,\"");
                xpm_File.println("/* pixels */");
                //int m=0;
                //int arrange[] =  new int [1000];
                    for(int x =0; x < 499; x++) {
                        xpm_File.print("\"");
                        //System.out.print("\"");
                        for (int y = 0; y < 499; y++) {
                            xpm_File.print(matrix[x][y]);
                            //System.out.print(matrix[x][y]);

                        }
                        xpm_File.println();
                        //System.out.println();
                        xpm_File.print("\"" + ",");
                        //System.out.print("\"" + ",");

                    }
                    //m++;


                /*
                for (int matrix[] : arrange) {
                    //format(matrix);
                }

                    if(x == 499){
                        xpm_File.print("\"");
                    }
                    else
                    {
                        if(matrix[x][y]==1){
                            //xpm_File.print("2");
                        }
                        else{
                           // xpm_File.print("3");
                        }
                        //xpm_File.print("\'"+ "," + "\n" );

                    }*/


                xpm_File.println("};");
                xpm_File.close();
            } catch (UnsupportedEncodingException err) {
                System.out.println("No file was created");
            }
        }
        catch(FileNotFoundException err){
            System.out.println("Unable to find file");
        }


    }

    public static void main(String[] args) {
        //openIn("hw1_1.ps");
        //bres();
        //xpm_out("hw1_1.ps");
        for (int i = 0; i < args.length; i += 1) {
            if (args[i].equals("-f")) {
                file = args[i+1];
                openIn(file);
                bres();
                xpm_out(file);
                //System.out.println(points);
                //System.out.println(line);
            }

        }
    }
    }