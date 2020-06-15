import java.util.*;
import java.io.*;
//Inspiration for implementation of Sutherland-Hodgman polygon clipping algorithm gotten from "https://www.longsteve.com/fixmybugs/?page_id=203"
public class A3 {


    private static final int inside = 0;
    private static final int left = 1;
    private static final int right = 2;
    private static final int bottom = 4;
    private static final int top = 8;
    private static final int x_bottom = 0;
    private static final int x_top = 499;
    private static final int y_bottom = 0;
    private static final int y_top = 499;

    static float clip_x = 0.f;
    static float clip_y = 0.f;
    static int clip_xy;
    static float x_int;
    static float y_int;
    static float xd;
    static float yd;
    static float through;
    static float x_step;
    static float y_step;

    static List<List<Integer>> lines = new ArrayList<List<Integer>>();
    static List<List<Float>> fit_In = new ArrayList<List<Float>>();
    static List<List<Float>> new_lines = new ArrayList<List<Float>>();

    static int matrix[][] = new int [500][500];
    static String file;

    public static void openIn(String file) throws FileNotFoundException {
        try{

            int x0,y0;
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

                        if(input.equals("%%%END")){
                            break;
                        }

                        if(input.trim().length() == 0){
                            continue;
                        }

                        if(input.equals("stroke")){
                            /*
                                                    String []line = input.split(" ");
                        if(line[0].equals("")){
                            x0 = Integer.parseInt(line[1]);
                            if(line[2].equals("")){
                                y0 = Integer.parseInt(line[3]);
                            }
                            else{
                                y0 = Integer.parseInt(line[2]);
                            }
                        }

                        else{
                            x0 = Integer.parseInt(line[0]);

                            if(line[1].equals("")){
                                y0 = Integer.parseInt(line[2]);
                            }
                            else{
                                y0 = Integer.parseInt(line[1]);
                            }
                        }
                        break;
                        */
                            continue;
                        }

                        String []line = input.split(" ");
                        if(line[0].equals("")){
                            x0 = Integer.parseInt(line[1]);
                            if(line[2].equals("")){
                                y0 = Integer.parseInt(line[3]);
                            }
                            else{
                                y0 = Integer.parseInt(line[2]);
                            }
                        }

                        else{
                            x0 = Integer.parseInt(line[0]);

                            if(line[1].equals("")){
                                y0 = Integer.parseInt(line[2]);
                            }
                            else{
                                y0 = Integer.parseInt(line[1]);
                            }
                        }

                        List<Integer> xy = new ArrayList<>();
                        xy.add(x0);
                        xy.add(y0);
                        lines.add(xy);

                        for (int i = 0; i < matrix.length; i++){
                            for (int j = 0; j < matrix.length; j++){
                                matrix[i][j] = 0;
                            }
                        }
                    }
                }
            }

            in.close();
            //System.out.println(polygon);
        }
        catch(FileNotFoundException err){
            System.out.println("Unable to load in file");

        }
    }

    public static void rotate() {
        //rotation 90 degrees counterclockwise
        for (int i = 0; i < lines.size(); i++){
            List<Float> xy1 = new ArrayList<>();
            double x0 = (lines.get(i).get(0) * Math.cos(Math.toRadians(0))) - (lines.get(i).get(1) * Math.sin(Math.toRadians(0)));
            xy1.add((float) x0);

            double y0 = (lines.get(i).get(1) * Math.sin(Math.toRadians(0))) + (lines.get(i).get(1) * Math.cos(Math.toRadians(0)));
            xy1.add((float) y0);

            fit_In.add(xy1);
        }
    }

    public static void dda(){
        float x,y;
        //Implementation of Digital Differential Analyzer Line Drawing Algorithm
        for(int i=0; i< new_lines.size()-1; i++) {

            float x0 = new_lines.get(i).get(0);
            float x1 = new_lines.get(i + 1).get(0);
            xd = x1 - x0;

            float y0 = new_lines.get(i).get(1);
            float y1 = new_lines.get(i + 1).get(1);
            yd = y1 - y0;

            if((x0 == x1) && (yd < 0)){
                through = Math.abs(yd);
            }

            if(Math.abs(xd) > Math.abs(yd)){
                through = Math.abs(xd);
            }
            else{
                through = Math.abs(yd);
            }


            x_step = xd/through;
            x = (int)x0;

            y_step = yd/through;
            y = (int)y0;


            for (int j=0; j<through; j++){
                x = x + x_step;
                y = y + y_step;

                if(!((x < 0) || (x >= 499) || (y < 0) || (y >= 499))){
                    matrix[Math.round(y)][Math.round(x)] = 5;
                }
            }
        }
    }

    public static void sutherland_hodg() {

        {
            clip_xy = top;
            line_clipping();
            fit_In.clear();
            for (int i = 0; i < new_lines.size(); i++) {
                List<Float> new_xy = new ArrayList<>();
                float x0 = new_lines.get(i).get(0);
                new_xy.add(x0);
                float y0 = new_lines.get(i).get(1);
                new_xy.add(y0);
                fit_In.add(new_xy);
            }
            if (clip_xy != left) {
                new_lines.clear();
            }
        }


        {
        clip_xy = right;
        line_clipping();
        fit_In.clear();
        for (int i = 0; i < new_lines.size(); i++) {
            List<Float> new_xy = new ArrayList<>();
            float x0 = new_lines.get(i).get(0);
            new_xy.add(x0);
            float y0 = new_lines.get(i).get(1);
            new_xy.add(y0);
            fit_In.add(new_xy);
        }
        if (clip_xy != left) {
            new_lines.clear();
        }
        }

        {
            clip_xy = bottom;
        line_clipping();
        fit_In.clear();
        for (int i = 0; i < new_lines.size(); i++) {
            List<Float> new_xy = new ArrayList<>();
            float x0 = new_lines.get(i).get(0);
            new_xy.add(x0);
            float y0 = new_lines.get(i).get(1);
            new_xy.add(y0);
            fit_In.add(new_xy);
        }
        if (clip_xy != left){
            new_lines.clear();
        }
    }

        {
            clip_xy = left;
            line_clipping();
            fit_In.clear();
            for (int i = 0; i < new_lines.size(); i++) {
                List<Float> new_xy = new ArrayList<>();
                float x0 = new_lines.get(i).get(0);
                new_xy.add(x0);
                float y0 = new_lines.get(i).get(1);
                new_xy.add(y0);
                fit_In.add(new_xy);
            }
            if (clip_xy != left) {
                new_lines.clear();
            }
        }
    }

    public static void add_Vertex(float x0, float y0){
        List<Float> new_Vertex =  new ArrayList<>();
        new_Vertex.add(x0);
        new_Vertex.add(y0);

        new_lines.add(new_Vertex);

    }

    public static void line_clipping(){
        for(int i=0;i<fit_In.size()-1; i++){
            float x0 = fit_In.get(i).get(0);
            float y0 = fit_In.get(i).get(1);
            float x1 = fit_In.get(i+1).get(0);
            float y1 = fit_In.get(i+1).get(1);

            if(position(x0,y0)){
                if(i == 0){
                    add_Vertex(x0,y0);
                }
                if(position(x1,y1)){
                    add_Vertex(x1,y1);
                }
                else{
                    intersect(x0,y0,x1,y1,clip_xy);
                    add_Vertex(x_int,y_int);
                }
            }
            else{
                if(position(x1,y1)){
                    intersect(x1,y1,x0,y0,clip_xy);
                    add_Vertex(x_int,y_int);
                    add_Vertex(x1,y1);
                }
            }

        }
    }

    public static void intersect(float x0, float y0, float x1, float y1, int clip_xy){

        float c_x = x1-x0;
        float c_y = y1-y0;
        float m= c_y/c_x;

        if((c_y == 0)|| (c_x == 0)){
            if(clip_xy == top){
                y_int = y_top;
                x_int = x0;
            }
            else if(clip_xy == bottom){
                y_int = y_bottom;
                x_int = x0;
            }
            else if(clip_xy == left){
                y_int = y0;
                x_int = x_bottom;
            }
            else if(clip_xy == right){
                y_int = y0;
                x_int = x_top;
            }
            return;
        }

        if (clip_xy  == top){
            y_int = y_top;
            x_int = (x_top - y0)/m + x0;
        }
        if (clip_xy  == bottom){
            y_int = y_bottom;
            x_int = (y_bottom - y0)/m + x1;

        }
        if (clip_xy  == left){
            y_int = m * (x_bottom - x0) + y0;
            x_int = x_bottom;
        }
        if (clip_xy  == right){
            y_int = m * (x_top - x0) + y0;
            x_int = x_top;
        }

    }

    public static boolean position(float x, float y){

        if ((clip_xy == left) && (x > x_bottom)){
            return true;
        }
        if((clip_xy == right) && (x < x_top)){
            return true;
        }
        if((clip_xy == top) && (y < y_top)){
            return true;
        }
        if((clip_xy == bottom) && (y > y_bottom)){
            return true;
        }

        return false;

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
                    for (int y = 0; y < 499; y++) {
                        xpm_File.print(matrix[499-x-1][y]);
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

    public static void main(String[] args) throws FileNotFoundException {
        //openIn("hw3_5.ps");
        //rotate();
        //sutherland_hodg();
        //dda();
        //xpm_out("hw3_5.ps");

        for (int i = 0; i < args.length; i += 1) {
                if(args[i].equals("-f")){
                    file = args[i+1];
                    openIn(file);
                    rotate();
                    sutherland_hodg();
                    dda();
                    xpm_out(file);
                }
                else if(args[i].length() == 0){
                    openIn("hw3_1.ps");
                    rotate();
                    sutherland_hodg();
                    dda();
                    xpm_out("hw3_1.ps");
            }

        }
    }
}
