/**
 *
 * @author DNartey
 */

import java.io.*;
import java.util.*;

public class A2 {

    static String file;
    //static List<List<Integer>> line = new ArrayList<List<Integer>>();
    //String begin_delim = "%%%BEGIN";
    //String end_delim = "%%%END";

    private static final int x_bottom = 0;
    private static final int x_top = 499;
    private static final int y_bottom = 0;
    private static final int y_top = 499;

    static float clip_x = 0.f;
    static float clip_y = 0.f;
    static int code_out;

    static List<List<Integer>> points = new ArrayList<List<Integer>>();
    static List<List<Float>> constraints = new ArrayList<List<Float>>();
    static List<List<Float>> fit_In = new ArrayList<List<Float>>();
    static int matrix[][] = new int [500][500];


    private static final int inside = 0;
    private static final int left = 1;
    private static final int right = 2;
    private static final int bottom = 4;
    private static final int top = 8;



    public static void openIn(String file) throws FileNotFoundException{
        try{

            //input file from arguments to be opened using Scanner
            Scanner in = new Scanner(new File(file));
            //Loop to traverse the file line by line
            while (in.hasNextLine()){
                //if function that looks for the first delimiter
                //if(in.nextLine.equals(begin_delim){
                if (in.nextLine().equals("%%%BEGIN")){
                    //if that first delimiter is found, read in the data line by line into a single string
                    while (in.hasNextLine()) {
                        String input = in.nextLine();

                        //end or break if input is the last delimiter
                        //if (input.equals(end_delim)) {
                        if (input.equals("%%%END")){
                            break;
                        }

                        List<Integer> xy = new ArrayList<Integer>();
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

                        for (int x = 0; x < matrix.length; x++){
                            for (int y = 0; y < matrix.length; y++){
                                matrix[x][y] = 0;
                            }
                        }
                    }
                }
            }

            in.close();
            //rotation 90 degrees counterclockwise
            for(int i=0; i<points.size(); i++){
                List<Float> xy1 = new ArrayList<>();
                for(int z=0; z<4; z+=2){
                    double x0 = (points.get(i).get(z) * Math.cos(Math.toRadians(0))) - (points.get(i).get(z+1) * Math.sin(Math.toRadians(0)));
                    xy1.add((float)x0);

                    double y0 = (points.get(i).get(z) * Math.sin(Math.toRadians(0))) + (points.get(i).get(z+1) * Math.cos(Math.toRadians(0)));
                    xy1.add((float)y0);
                }
                fit_In.add(xy1);
            }
        }
        catch(FileNotFoundException err){
            System.out.println("Unable to load in file");

        }
    }

    public static void bres(){
        List<Integer> xy =  new ArrayList<>();

        //Implementation of Bresenham's Line Drawing Algorithm
        for(int i=0; i< fit_In.size(); i++) {

            int og_x, og_y;

            float x0 = fit_In.get(i).get(0);
            float y0 = fit_In.get(i).get(1);
            float x1 = fit_In.get(i).get(2);
            float y1 = fit_In.get(i).get(3);

            float dx = Math.abs(x1-x0);
            float dy = Math.abs(y1-y0);
            float m = (dx-dy);

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
                int rid2 = (int)(2 * m);
                if(rid2>-dy){
                    m = m-dy;
                    x0 = x0+og_x;
                }
                if(rid2<dx){
                    m = m+dx;
                    y0 = y0+og_y;
                }

                int x_int = (int)x0;
                int y_int = (int)y0;
                xy.add(x_int);
                xy.add(y_int);

                if(!((x_int < 0) || (x_int >= 499) || (y_int < 0) || (y_int >= 499))) {
                    matrix[Math.round(y_int)][x_int] = 5;
                }
                points.add(xy);

            }
        }
    }

    public static void cohen_sutherland(){
        List<Float> temp = new ArrayList<Float>();
        float x1,y1,x2,y2;
        for (int i=0; i<fit_In.size(); i++) {
            x1 = fit_In.get(i).get(0);
            y1 = fit_In.get(i).get(1);

            int c1 = position(x1, y1);

            x2 = fit_In.get(i).get(2);
            y2 = fit_In.get(i).get(3);


            int c2 = position(x2, y2);

            boolean meets = false;

            while(true){
                if((c1 == 0 || c2 == 0)){
                    meets = true;
                    break;
                }

                else if((c1 & c2) != 0){
                    break;
                }

                else{
                    if(c1 >= 1){
                        code_out = c1;
                    }
                    else {
                        code_out = c2;
                    }
                    if((code_out >= 1) && (top >= 1)){
                        clip_x = x1 + (x2 - x1) * (y_top - y1) / (y2 - y1);
                        clip_y = y_top;
                    }
                    if((code_out >= 1) && (right >= 1)){
                        clip_y = y1 + (y2 - y1) * (x_top - x1) / (x2 - x1);
                        clip_x = x_top;
                    }

                    if((code_out >= 1) && (bottom >= 1)){
                        clip_x = x1 + (x2 - x1) * (y_bottom - y1) / (y2 - y1);
                        clip_y = y_bottom;
                    }
                    if((code_out >= 1) && (left >= 1)){
                        clip_y = y1 + (y2 - y1) * (x_bottom - x1) / (x2 - x1);
                        clip_x = x_bottom;
                    }

                    if(code_out == c1){
                        x1 = clip_x;
                        y1 = clip_y;
                        c1 = position(x1, y1);
                    }
                    else{
                        x2 = clip_x;
                        y2 = clip_y;
                        c2 = position(x2, y2);
                    }
                }
            }

            if(meets){
                temp.add(x1);
                temp.add(y1);
                temp.add(x2);
                temp.add(y2);

                constraints.add(temp);
            }
        }
    }

    public static int position(float x, float y){
        int code = inside;

        if (x < x_bottom) {
            code += left;
        }
        if(x > x_top) {
            code += right;
        }
        if(y > y_top) {
            code += top;
        }
        if(y < y_bottom) {
            code += bottom;
        }
        return code;

    }

    public static void cyrus_beck(){

    }

    public static void xpm_out(String file){
        try {
            try {

                PrintWriter xpm_File = new PrintWriter(file.substring(0,5) + ".xpm", "UTF-8");
                xpm_File.println("/*XPM*/");
                xpm_File.println("static char *sco100[]={" + '\n' + "//* width height num_colors chars_per_pixel */");
                xpm_File.println("\"500 500 2 1,\"");
                xpm_File.println("/* colors */");
                xpm_File.println("\"0 c #ffffff,\"" + '\n' + "\"5 c #000000,\"");
                xpm_File.println("/* pixels */");
                for(int x =0; x < 499; x++) {
                    xpm_File.print("\"");
                    //System.out.print("\"");
                    for (int y = 0; y < 500; y++) {
                        xpm_File.print(matrix[x][y]);
                        //System.out.print(matrix[x][y]);

                    }
                    xpm_File.println();
                    //System.out.println();
                    xpm_File.print("\"" + ",");
                    //System.out.print("\"" + ",");

                }
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

    public static void main(String[] args) throws FileNotFoundException{
        //openIn("hw2_5.ps");
        //cohen_sutherland();
        //bres();
        //cyrus_beck();
        //xpm_out("hw2_5.ps");

        for (int i = 0; i < args.length; i += 1) {
            if (args[i].equals("-f")) {
                file = args[i+1];
                openIn(file);
                cohen_sutherland();
                bres();
                xpm_out(file);
            }
            else if(args[i].equals("-x")){
                file =  args[i+1];
                openIn(file);
                cyrus_beck();
                bres();
                xpm_out(file);
            }

        }
    }
}