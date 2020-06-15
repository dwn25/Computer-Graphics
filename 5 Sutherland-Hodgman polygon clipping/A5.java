import java.io.*;
import java.util.*;
public class A5
{

    static int x_bottom;
    static int y_bottom;
    static int x_top;
    static int y_top;

    static int clip_xy;
    static float x_int;
    static float y_int;

    static int viewport_x_bottom;
    static int viewport_y_bottom;
    static int viewport_x_top;
    static int viewport_y_top;

    static float rotation;
    static int trans_x;
    static int trans_y;
    static float scal_f;

    static int width = 501;
    static int height = 501;
    static int matrix[][] = new int [height][width];

    static int top = 8;
    static int bottom = 4;
    static int left = 1;
    static int right = 2;

    static String file;

    static List<List<List<Float>>> individual_clip = new ArrayList<List<List<Float>>>();
    static List<List<List<Integer>>> print = new ArrayList<List<List<Integer>>>();
    static List<List<List<Integer>>> delete = new ArrayList<List<List<Integer>>>();
    static List<List<List<Integer>>> full = new ArrayList<List<List<Integer>>>();

    static List<List<Float>> individual = new ArrayList<List<Float>>();
    static List<List<List<Integer>>> all = new ArrayList<List<List<Integer>>>();
    static List<List<List<Float>>> fit_In = new ArrayList<List<List<Float>>>();

    static List<List<List<Float>>> transformed_view = new ArrayList<List<List<Float>>>();


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

    public static void first_trans(){
        /*
        Scaling
            |x2|   |scal_f 0 0| |x|
            |y2| = |0 scal_f 0|*|y|
            |1 |   |0  0     1| |1|
        */
        List<List<List<Float>>> scaling = new ArrayList<List<List<Float>>>();
        for (int i = 0; i< all.size(); i++) {
            List<List<Float>> individual = new ArrayList<List<Float>>();
            for(int j = 0; j< all.get(i).size(); j++) {
                List<Float> xy = new ArrayList<Float>();

                float x = all.get(i).get(j).get(0);
                float y = all.get(i).get(j).get(1);

                float scaled_matrix[][] = new float [3][3];
                float xy_values1[][] = new float [3][1];

                scaled_matrix[0][0] = scal_f;
                scaled_matrix[1][0] = 0;
                scaled_matrix[2][0] = 0;

                scaled_matrix[0][1] = 0;
                scaled_matrix[1][1] = scal_f;
                scaled_matrix[2][1] = 0;

                scaled_matrix[0][2] = 0;
                scaled_matrix[1][2] = 0;
                scaled_matrix[2][2] = 1;

                xy_values1[0][0] = x;
                xy_values1[1][0] = y;
                xy_values1[2][0] = 1;

                float scaled[][] = multiply_float(scaled_matrix,xy_values1);

                float x1 = scaled[0][0];
                float y1 = scaled[1][0];

                xy.add(x1);
                xy.add(y1);

                individual.add(xy);
            }
            scaling.add(individual);
        }

        /*
        Rotation
            |x2|   |cos(rotation) -sin(rotation) 0| |x|
            |y2| = |sin(rotation) cos(rotation)  0|*|y|
            |1 |   |0                   0        1| |1|
        */
        List<List<List<Float>>> rotating = new ArrayList<List<List<Float>>>();
        for(int i=0; i<scaling.size(); i++){
            List<List<Float>> individual = new ArrayList<List<Float>>();
            for(int j = 0; j< all.get(i).size(); j++){
                List<Float> xy = new ArrayList<Float>();

                float x = scaling.get(i).get(j).get(0);
                float y = scaling.get(i).get(j).get(1);

                double rotated_matrix[][] = new double [3][3];
                double xy_values2[][] = new double [3][1];

                rotated_matrix[0][0] = Math.cos(Math.toRadians(rotation));
                rotated_matrix[1][0] = Math.sin(Math.toRadians(rotation));
                rotated_matrix[2][0] = 0;

                rotated_matrix[0][1] = -Math.sin(Math.toRadians(rotation));
                rotated_matrix[1][1] = Math.cos(Math.toRadians(rotation));
                rotated_matrix[2][1] = 0;

                rotated_matrix[0][2] = 0;
                rotated_matrix[1][2] = 0;
                rotated_matrix[2][2] = 1;

                xy_values2[0][0] = x;
                xy_values2[1][0] = y;
                xy_values2[2][0] = 1;

                double rotate[][] = multiply_double(rotated_matrix,xy_values2);

                double x1 = rotate[0][0];
                double y1 = rotate[1][0];

                xy.add((float)x1);
                xy.add((float)y1);
                individual.add(xy);
            }
            rotating.add(individual);
        }

        /*Translation
            |x2|   |1 0 trans_x| |x|
            |y2| = |0 1 trans_y|*|y|
            |1 |   |0 0  1     | |1|
            */
        for(int i=0; i<rotating.size(); i++){
            List<List<Float>> individual = new ArrayList<List<Float>>();
            for(int j = 0; j< all.get(i).size(); j++){

                List<Float> xy = new ArrayList<Float>();

                float x = rotating.get(i).get(j).get(0);
                float y = rotating.get(i).get(j).get(1);

                float translate_matrix[][] = new float [3][3];
                float xy_values3[][] = new float [3][1];

                translate_matrix[0][0] = 1;
                translate_matrix[1][0] = 0;
                translate_matrix[2][0] = 0;

                translate_matrix[0][1] = 0;
                translate_matrix[1][1] = 1;
                translate_matrix[2][1] = 0;

                translate_matrix[0][2] = trans_x;
                translate_matrix[1][2] = trans_y;
                translate_matrix[2][2] = 1;

                xy_values3[0][0] = x;
                xy_values3[1][0] = y;
                xy_values3[2][0] = 1;

                float translate[][] = multiply_float(translate_matrix,xy_values3);

                float x1 = translate[0][0];
                float y1 = translate[1][0];

                xy.add(x1);
                xy.add(y1);
                individual.add(xy);
            }
            fit_In.add(individual);
        }
    }

    public static void sutherland_hodg(){
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
                        add_vertex(x1, y1);
                    }
                    if(position(x2, y2)){
                        add_vertex(x2, y2);
                    }
                    else{
                        intersect(x1, y1, x2, y2, clip_xy);
                        add_vertex(x_int, y_int);
                    }
                }
                else{
                    if(position(x2, y2)){
                        intersect(x2, y2, x1, y1, clip_xy);
                        add_vertex(x_int, y_int);

                        add_vertex(x2, y2);
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

    public static void add_vertex(float x0, float y0){
        List<Float> new_Vertex =  new ArrayList<>();
        new_Vertex.add(x0);
        new_Vertex.add(y0);

        individual.add(new_Vertex);
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

    public static void viewport(){
            /*Translation to world window origin
            |x2|   |1 0 -x_bottom| |x|
            |y2| = |0 1 -y_bottom|*|y|
            |1 |   |0 0  1       | |1|
            */
        List<List<List<Float>>> ww_translate_poly = new ArrayList<List<List<Float>>>();
        for(int i = 0; i< individual_clip.size(); i++){
            List<List<Float>> individual = new ArrayList<List<Float>>();
            for(int j = 0; j< individual_clip.get(i).size(); j++){

                List<Float> xy = new ArrayList<Float>();

                float x = individual_clip.get(i).get(j).get(0);
                float y = individual_clip.get(i).get(j).get(1);

                float vw_translate[][] = new float [3][3];
                float xy_value4[][] = new float [3][1];

                vw_translate[0][0] = 1;
                vw_translate[1][0] = 0;
                vw_translate[2][0] = 0;

                vw_translate[0][1] = 0;
                vw_translate[1][1] = 1;
                vw_translate[2][1] = 0;

                vw_translate[0][2] = -x_bottom;
                vw_translate[1][2] = -y_bottom;
                vw_translate[2][2] = 1;

                xy_value4[0][0] = x;
                xy_value4[1][0] = y;
                xy_value4[2][0] = 1;

                float translate_vw[][] = multiply_float(vw_translate,xy_value4);

                float x1 = translate_vw[0][0];
                float y1 = translate_vw[1][0];
                xy.add(x1);
                xy.add(y1);
                individual.add(xy);
            }

            ww_translate_poly.add(individual);
        }
        /*
        Scale to view port
            |x2|   |scal_f 0 0| |x|
            |y2| = |0 scal_f 0|*|y|
            |1 |   |0  0     1| |1|
        */
        List<List<List<Float>>> viewport_scale = new ArrayList<List<List<Float>>>();
        for (int i=0; i<ww_translate_poly.size(); i++){
            List<List<Float>> individual = new ArrayList<List<Float>>();
            for(int j=0; j<ww_translate_poly.get(i).size(); j++){
                List<Float> xy = new ArrayList<Float>();

                float x = ww_translate_poly.get(i).get(j).get(0);
                float y = ww_translate_poly.get(i).get(j).get(1);
                float num_x = viewport_x_top -viewport_x_bottom;
                float num_y = viewport_y_top - viewport_y_bottom;
                float den_x = x_top - x_bottom;
                float den_y = y_top - y_bottom;
                float scal_x =  (num_x/den_x);
                float scal_y =  (num_y/den_y);

                float scaling_vw[][] = new float [3][3];
                float xy_values5[][] = new float [3][1];

                scaling_vw[0][0] = scal_x;
                scaling_vw[1][0] = 0;
                scaling_vw[2][0] = 0;

                scaling_vw[0][1] = 0;
                scaling_vw[1][1] = scal_y;
                scaling_vw[2][1] = 0;

                scaling_vw[0][2] = 0;
                scaling_vw[1][2] = 0;
                scaling_vw[2][2] = 1;

                xy_values5[0][0] = x;
                xy_values5[1][0] = y;
                xy_values5[2][0] = 1;

                float scaled[][] = multiply_float(scaling_vw,xy_values5);

                float x1 = scaled[0][0];
                float y1 = scaled[1][0];

                xy.add(x1);
                xy.add(y1);
                individual.add(xy);
            }
            viewport_scale.add(individual);
        }

        /*Translation to view port origin
            |x2|   |1 0 -x_bottom| |x|
            |y2| = |0 1 -y_bottom|*|y|
            |1 |   |0 0  1       | |1|
            */
        for(int i=0; i<viewport_scale.size(); i++){
            List<List<Float>> individual = new ArrayList<List<Float>>();

            for(int j=0; j<viewport_scale.get(i).size(); j++){

                List<Float> xy = new ArrayList<Float>();

                float x = viewport_scale.get(i).get(j).get(0);
                float y = viewport_scale.get(i).get(j).get(1);

                float vp_origin[][] = new float [3][3];
                float xy_value6[][] = new float [3][1];

                vp_origin[0][0] = 1;
                vp_origin[1][0] = 0;
                vp_origin[2][0] = 0;

                vp_origin[0][1] = 0;
                vp_origin[1][1] = 1;
                vp_origin[2][1] = 0;

                vp_origin[0][2] = viewport_x_bottom;
                vp_origin[1][2] = viewport_y_bottom;
                vp_origin[2][2] = 1;

                xy_value6[0][0] = x;
                xy_value6[1][0] = y;
                xy_value6[2][0] = 1;

                float translate_vw[][] = multiply_float(vp_origin,xy_value6);

                float x1 = translate_vw[0][0];
                float y1 = translate_vw[1][0];
                x = x + viewport_x_bottom;
                y = y + viewport_y_bottom;
                xy.add(x1);
                xy.add(y1);
                individual.add(xy);
            }

            transformed_view.add(individual);

        }

    }

    public static void start_fill(){
        for(int i = 0; i< transformed_view.size(); i++){
            List<List<Integer>> individual = new ArrayList<List<Integer>>();
            for(int j = 0; j< transformed_view.get(i).size(); j++){
                List<Integer> xy = new ArrayList<Integer>();

                int x = Math.round(transformed_view.get(i).get(j).get(0));
                xy.add(x);

                int y = Math.round(transformed_view.get(i).get(j).get(1));
                xy.add(y);
                individual.add(xy);
            }
            print.add(individual);
        }

        for(int i = 0; i< print.size(); i++){
            int y_top1 = list_Max(print.get(i));
            int y_bottom1 = list_Min(print.get(i));

            for(int a=y_bottom1; a<=y_top1; a++){
                full.add(new ArrayList<List<Integer>>());
                delete.add(new ArrayList<List<Integer>>());
            }

            for(int b = 0; b< print.get(i).size() - 1; b++){

                int y_bottom2 = print.get(i).get(b).get(1);
                int y_top2 = print.get(i).get(b+1).get(1);

                for(int c = y_bottom1; c<=y_top1; c++){
                    if(((c >= y_bottom2 && c < y_top2) || (c >= y_top2 && c < y_bottom2)) && y_bottom2 != y_top2){
                        List<Integer> xy = new ArrayList<Integer>();
                        int x1 = print.get(i).get(b).get(0);
                        xy.add(x1);

                        int x2 = print.get(i).get(b+1).get(0);
                        xy.add(y_bottom2);
                        xy.add(x2);
                        xy.add(y_top2);

                        full.get(c-y_bottom1).add(xy);
                    }
                }
            }

            for(int d = y_bottom1; d <= y_top1; d++){
                delete.clear();
                for(int e=y_bottom1; e<=y_top1; e++){
                    delete.add(new ArrayList<List<Integer>>());
                }

                for(int f = 0; f< full.get(d-y_bottom1).size(); f++){
                    int x0 = full.get(d-y_bottom1).get(f).get(0);
                    int y0 = full.get(d-y_bottom1).get(f).get(1);
                    int x1 = full.get(d-y_bottom1).get(f).get(2);
                    int y1 = full.get(d-y_bottom1).get(f).get(3);

                    poly_intersect(x0, y0, x1, y1, d, y_bottom1);
                }

                int g = 0;
                while( g< delete.size()){
                    Collections.sort(delete.get(g), new Comparator<List<Integer>>(){
                        public int compare(List<Integer> list1, List<Integer> list2)
                        {
                            return list1.get(0).compareTo(list2.get(0));
                        }
                    });
                    g++;
                }
                fill_inside();
            }
            full.clear();
            delete.clear();
        }
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

    public static void xpm_out(){
        //PrintWriter xpm_File = new PrintWriter(file.substring(0,4) + ".xpm", "UTF-8");
        System.out.println("/*XPM*/");
        System.out.println("static char *sco100[]={" + '\n' + "//* width height num_colors chars_per_pixel */");
        System.out.println("\"" + height +" " + width+" " +" 2 1,\"");
        System.out.println("/* colors */");
        System.out.println("\"0 c #ffffff,\"" + '\n' + "\"5 c #000000,\"");
        System.out.println("/* pixels */");
        for(int x =0; x < height; x++) {
            System.out.print("\"");
            //System.out.print("\"");
            for (int y = 0; y < width; y++) {
                System.out.print(matrix[height-x-1][y]);

            }
            System.out.println();
            //System.out.println();
            System.out.print("\"" + ",");
            //System.out.print("\"" + ",");

        }
        System.out.println("};");
        System.out.close();


    }

    public static double[][] multiply_double(double[][] a, double[][] b){
        int row_length = a.length;
        int acol_length = a[0].length;
        int bcol_length = b[0].length;

        double [][] mult = new double[row_length][bcol_length];

        for(int x=0; x<row_length; x++) {
            for(int y=0; y<bcol_length; y++) {
                for(int z=0; z<acol_length; z++) {
                    mult[x][y] += a[x][z] * b[z][y];
                }
            }
        }

        return mult;
    }

    public static float[][] multiply_float(float[][] a, float[][] b){
        int row_length = a.length;
        int acol_length = a[0].length;
        int bcol_length = b[0].length;

        float [][] mult = new float[row_length][bcol_length];

        for(int x=0; x<row_length; x++) {
            for(int y=0; y<bcol_length; y++) {
                for(int z=0; z<acol_length; z++) {
                    mult[x][y] += a[x][z] * b[z][y];
                }
            }
        }

        return mult;
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

    public static void main(String[] args) throws NullPointerException{
        //open_In("hw5_1.ps");
        //first_trans();
        //sutherland_hodg();
        //viewport();
        //start_fill();
        //xpm_out();

        for (int i=0; i<args.length; i+=2) {
            if (args[i].equals("-f")){
                file = args[i + 1];
            }
            else if (args[i].equals("-s")){
                scal_f = Float.parseFloat(args[i + 1]);
            }

            else if(args[i].equals("-r")){
                rotation = Integer.parseInt(args[i+1]);
            }

            else if(args[i].equals("-m")){
                trans_x = Integer.parseInt(args[i+1]);
            }

            else if(args[i].equals("-n")){
                trans_y = Integer.parseInt(args[i+1]);
            }

            else if(args[i].equals("-a")){
                x_bottom = Integer.parseInt(args[i+1]);
            }

            else if(args[i].equals("-b")){
                y_bottom = Integer.parseInt(args[i+1]);
            }

            else if(args[i].equals("-c")){
                x_top = Integer.parseInt(args[i+1]);
            }

            else if(args[i].equals("-d")){
                y_top = Integer.parseInt(args[i+1]);
            }

            else if(args[i].equals("-j")){
                viewport_x_bottom = Integer.parseInt(args[i+1]);
            }

            else if(args[i].equals("-k")){
                viewport_y_bottom = Integer.parseInt(args[i+1]);
            }

            else if(args[i].equals("-o")){
                viewport_x_top = Integer.parseInt(args[i+1]);
            }

            else if(args[i].equals("-p")){
                viewport_y_top = Integer.parseInt(args[i+1]);
            }

        }

        open_In(file);
        first_trans();
        sutherland_hodg();
        viewport();
        start_fill();
        xpm_out();

        //System.out.print("-s: "+ scal_f + "\n" + "-r: "+ rotation +"\n"+ "-m: "+trans_x +"\n");
        //System.out.print("-n: "+ trans_y + "\n"+  "-a: " +x_bottom+ "\n"+ "-b: "+y_bottom+"\n"+"-c: "+ x_top+"\n");
        //System.out.print("-d: "+ y_top+"\n"+ "-j: "+viewport_x_bottom+"\n"+"-o: "+viewport_x_top+"\n" +"-k: "+viewport_y_bottom+"\n"+ "-p: "+viewport_y_top);

    }

}