import java.util.*;
import java.io.*;
//Inspiration for implementation of Sutherland-Hodgman polygon clipping algorithm gotten from "https://www.longsteve.com/fixmybugs/?page_id=203"
//Implementation of Scan Fill Algorithm found here; "https://bedeveloper.wordpress.com/a-c-program-to-fill-polygon-using-scan-line-fill-algorithm/"
//and "http://code-heaven.blogspot.com/2009/10/simple-c-program-for-scan-line-polygon.html"
//Other sources;"//https://stackoverflow.com/questions/5393254/java-comparator-class-to-sort-arrays"

public class A4 {

    private static final int left = 1;
    private static final int right = 2;
    private static final int bottom = 4;
    private static final int top = 8;
    private static final int x_bottom = 0;
    private static final int y_bottom = 0;
    private static final int x_top = 500;
    private static final int y_top = 500;

    static List<List<List<Float>>> individual_clip = new ArrayList<List<List<Float>>>();
    static List<List<List<Integer>>> print = new ArrayList<List<List<Integer>>>();
    static List<List<List<Integer>>> delete = new ArrayList<List<List<Integer>>>();
    static List<List<List<Integer>>> full = new ArrayList<List<List<Integer>>>();

    static int clip_xy;
    static float x_int;
    static float y_int;
    static String file;
    static int matrix[][] = new int [500][500];

    static List<List<Float>> individual = new ArrayList<List<Float>>();
    static List<List<List<Integer>>> all = new ArrayList<List<List<Integer>>>();
    static List<List<List<Float>>> fit_In = new ArrayList<List<List<Float>>>();



    public static void open_In(String file){
        try{
            int x0,y0;
            //input file from arguments to be opened using Scanner
            Scanner in = new Scanner(new File(file));
            //Loop to traverse the file line by line
            while (in.hasNextLine()){
                //if function that looks for the first delimiter
                //if(in.nextLine.equals(begin_delim){
                if (in.nextLine().equals("%%%BEGIN")){
                    List<List<Integer>> individual = new ArrayList<List<Integer>>();
                    String input = in.nextLine();

                    //if that first delimiter is found, read in the data line by line into a single string
                    while (in.hasNextLine()){
                        input = in.nextLine();

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
                            all.add(individual);
                            individual = new ArrayList<List<Integer>>();
                            continue;
                        }

                        String line[] = input.split(" ");
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

                        individual.add(xy);

                        for (int i = 0; i < matrix.length; i++){
                            for (int j = 0; j < matrix.length; j++){
                                matrix[i][j] = 0;
                            }
                        }
                    }
                }
            }

            in.close();
            //System.out.println(all);
            //System.out.println(individual);
            //System.out.println(matrix);
        }
        catch(FileNotFoundException err){
            System.out.println("Unable to load in file");

        }
    }

    public static void sutherland_hodg(){
        for (int i = 0; i < all.size(); i++){
            List<List<Float>> temp = new ArrayList<List<Float>>();
            for (int j = 0; j < all.get(i).size(); j++){

                List<Float> xy = new ArrayList<Float>();
                float x = all.get(i).get(j).get(0);
                xy.add(x);

                float y = all.get(i).get(j).get(1);
                xy.add(y);

                temp.add(xy);
            }
            fit_In.add(temp);
        }

        top();
        bottom();
        right();
        left();

    }

    public static void top(){
        clip_xy = top;
        line_clipping();
        fit_In.clear();
        for (int i = 0; i < individual_clip.size(); i++){
            List<List<Float>> temp = new ArrayList<List<Float>>();
            for (int j = 0; j < individual_clip.get(i).size(); j++){

                List<Float> xy = new ArrayList<Float>();
                float x = individual_clip.get(i).get(j).get(0);
                xy.add(x);

                float y = individual_clip.get(i).get(j).get(1);
                xy.add(y);

                temp.add(xy);
            }
            fit_In.add(temp);
        }
        individual_clip.clear();
    }

    public static void bottom(){
        clip_xy = bottom;
        line_clipping();
        fit_In.clear();
        for (int i = 0; i < individual_clip.size(); i++){
            List<List<Float>> temp = new ArrayList<List<Float>>();
            for (int j = 0; j < individual_clip.get(i).size(); j++){

                List<Float> xy = new ArrayList<Float>();
                float x = individual_clip.get(i).get(j).get(0);
                xy.add(x);
                float y = individual_clip.get(i).get(j).get(1);
                xy.add(y);

                temp.add(xy);
            }
            fit_In.add(temp);
        }
        individual_clip.clear();
    }

    public static void right(){
        clip_xy = right;
        line_clipping();
        fit_In.clear();
        for(int i = 0; i < individual_clip.size(); i++){
            List<List<Float>> temp = new ArrayList<List<Float>>();

            for(int j = 0; j< individual_clip.get(i).size(); j++){
                List<Float> xy = new ArrayList<Float>();

                float x = individual_clip.get(i).get(j).get(0);
                xy.add(x);
                float y = individual_clip.get(i).get(j).get(1);
                xy.add(y);

                temp.add(xy);
            }
            fit_In.add(temp);
        }
        individual_clip.clear();
    }

    public static void left(){
            clip_xy = left;
            line_clipping();
    }

    public static void line_clipping(){
        for(int i = 0; i< fit_In.size(); i++){
            for(int j = 0; j< fit_In.get(i).size()-1; j++){

                float x1 = fit_In.get(i).get(j).get(0);
                float y1 = fit_In.get(i).get(j).get(1);
                float x2 = fit_In.get(i).get(j+1).get(0);
                float y2 = fit_In.get(i).get(j+1).get(1);

                if(position(x1, y1)){
                    if(j == 0){
                        add_Vertex(x1, y1);
                    }
                    if(position(x2, y2)){
                        add_Vertex(x2, y2);
                    }
                    else{
                        intersect(x1, y1, x2, y2, clip_xy);
                        add_Vertex(x_int, y_int);
                    }
                }
                else{
                    if(position(x2, y2)){
                        intersect(x2, y2, x1, y1, clip_xy);
                        add_Vertex(x_int, y_int);

                        add_Vertex(x2, y2);
                    }
                }
            }
            if(!individual.isEmpty())
                individual_clip.add(individual);
            individual = new ArrayList<List<Float>>();
        }

        for(int i = 0; i< individual_clip.size(); i++){
            List<Float> xy = new ArrayList<Float>();

            float x = individual_clip.get(i).get(0).get(0);
            xy.add(x);

            float y = individual_clip.get(i).get(0).get(1);
            xy.add(y);

            individual_clip.get(i).add(xy);
        }
    }

    public static void add_Vertex(float x0, float y0){
        List<Float> new_Vertex =  new ArrayList<>();
        new_Vertex.add(x0);
        new_Vertex.add(y0);

        individual.add(new_Vertex);
    }

    public static void intersect(float x0, float y0, float x1, float y1, int clip_xy){

        float c_x = x1-x0;
        float c_y = y1-y0;
        float m = c_y/c_x;

        if((c_y == 0) || (c_x == 0)){
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

    public static void start_fill(){
        for(int i = 0; i< individual_clip.size(); i++){
            List<List<Integer>> temp = new ArrayList<List<Integer>>();

            for(int l = 0; l< individual_clip.get(i).size(); l++){
                List<Integer> row = new ArrayList<Integer>();

                int x0 = Math.round(individual_clip.get(i).get(l).get(0));
                row.add(x0);

                int y0 = Math.round(individual_clip.get(i).get(l).get(1));
                row.add(y0);
                temp.add(row);
            }

            print.add(temp);
            //System.out.println(individual_clip);
            //System.out.println(temp);
            //System.out.println(row);
            //System.out.println(print);

        }
        for(int i = 0; i < print.size(); i++){
            int y_top1 = list_Max(print.get(i));
            int y_bottom1 = list_Min(print.get(i));
            //System.out.println(y_top1 + y_bottom1);

            for(int s = y_bottom1; s <= y_top1; s++){
                full.add(new ArrayList<List<Integer>>());
                delete.add(new ArrayList<List<Integer>>());
                //System.out.println(full);
                //System.out.println(delete);
            }

            for(int j = 0; j < print.get(i).size() - 1; j++){

                int ye = print.get(i).get(j).get(1);
                int ye2 = print.get(i).get(j+1).get(1);

                for(int y = y_bottom1; y <= y_top1; y++){
                    if(ye != ye2 && ((y >= ye2 && y < ye) || (y >= ye && y < ye2)) ){
                        List<Integer> row = new ArrayList<Integer>();

                        int x0 = print.get(i).get(j).get(0);
                        row.add(x0);
                        row.add(ye);

                        int x1 = print.get(i).get(j+1).get(0);
                        row.add(x1);
                        row.add(ye2);

                        //full.get(y).add(row);
                        //full.get(j-y).add(row)
                        full.get(y-y_bottom1).add(row);
                    }
                }
                //System.out.println(row);
                //System.out.println(print);
            }

            for(int y_1 = y_bottom1; y_1 <= y_top1; y_1++){
                delete.clear();
                //System.out.println(delete);
                for(int s=y_bottom1; s<=y_top1; s++){
                    delete.add(new ArrayList<List<Integer>>());
                }

                for(int j = 0; j< full.get(y_1-y_bottom1).size(); j++){
                    //int x0 = full.get(y_1).get(j).get(0);
                    int x0 = full.get(y_1-y_bottom1).get(j).get(0);
                    int y0 = full.get(y_1-y_bottom1).get(j).get(1);
                    int x1 = full.get(y_1-y_bottom1).get(j).get(2);
                    int y1 = full.get(y_1-y_bottom1).get(j).get(3);

                    //System.out.println(full);
                    poly_intersect(x0, y0, x1, y1, y_1, y_bottom1);
                }
                int x = 0;
                while( x< delete.size()){
                    Collections.sort(delete.get(x), new Comparator<List<Integer>>(){
                        public int compare(List<Integer> list1, List<Integer> list2)
                        {
                            return list1.get(0).compareTo(list2.get(0));
                        }
                    });
                    x++;
                }
                //fill_inside(x0,y0,x1);
                //inside();
                fill_inside();
            }
            full.clear();
            delete.clear();
        }
    }


    public static void fill_inside(){
        int i = 0;
        int j = 0;
        while(i < delete.size()){
            while(j< delete.get(i).size()){

                int x0 = delete.get(i).get(j).get(0);
                int y0 = delete.get(i).get(j).get(1);
                int x1 = delete.get(i).get(j+1).get(0);

                while(x0 != x1){
                    matrix[y0][x0] = 5;
                    x0++;
                }
                j+=2;
            }
            i++;
        }
    }

    public static int list_Max(List<List<Integer>> in_list){
        int val = Integer.MIN_VALUE;
        int i = 0;
        while(i<in_list.size()){
            int y = in_list.get(i).get(1);
            if(y > val){
                val = y;
            }
            i++;
        }
        return val;
    }

    public static void poly_intersect(int x0, int y0, int x1, int y1, int y_inherit, int y_bottom2){
        List<Integer> new_intersect = new ArrayList<Integer>();

        float m_x = x1 - x0;
        float m_y = y1 - y0;

        int x_inherit = Math.round(x0 + (m_x/m_y)*(y_inherit - y0));

        new_intersect.add(x_inherit);
        new_intersect.add(y_inherit);

        delete.get(y_inherit-y_bottom2).add(new_intersect);
    }

    public static int list_Min(List<List<Integer>> in_list){
        int val = Integer.MAX_VALUE;
        int i = 0;
        while(i<in_list.size()){
            int y = in_list.get(i).get(1);
            if(y < val){
                val = y;
            }
            i++;
        }
        return val;
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

    public static void main(String[] args){
        //open_In("hw4_9.ps");
        //sutherland_hodg();
        //start_fill();
        //xpm_out("hw4_9.ps");

        for (int i = 0; i < args.length; i += 1) {
            if(args[i].equals("-f")){
                file = args[i+1];
                open_In(file);
                sutherland_hodg();
                start_fill();
                xpm_out(file);
            }
            else if(args[i].length() == 0){
                open_In("hw4_1.ps");
                sutherland_hodg();
                start_fill();
                xpm_out("hw4_1.ps");
            }
        }
    }
}