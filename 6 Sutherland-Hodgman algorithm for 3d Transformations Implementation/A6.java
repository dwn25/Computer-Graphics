import java.io.*;
import java.util.*;
public class A6
{
    static int top = 0;
    static int bottom = 1;
    static int left = 2;
    static int right = 3;
    static int clip_xy;

    static int width = 501;
    static int height = 501;
    static int matrix[][]= new int[height][width];

    static float x_prp = 0.0f;
    static float y_prp = 0.0f;
    static float z_prp = 1.0f;

    static int viewport_x_bottom = 0;
    static int viewport_y_bottom = 0;
    static int viewport_x_top = 500;
    static int viewport_y_top = 500;

    static float x_vup = 0.0f;
    static float y_vup = 1.0f;
    static float z_vup = 0.0f;

    static float x_bottom;
    static float y_bottom;
    static float x_top;
    static float y_top;

    static float x_vpn = 0.0f;
    static float y_vpn = 0.0f;
    static float z_vpn = -1.0f;

    static float x_int;
    static float y_int;
    static float xd;
    static float yd;
    static float through;
    static float x_step;
    static float y_step;

    static float x_vrp = 0.0f;
    static float y_vrp = 0.0f;
    static float z_vrp = 0.0f;

    static boolean parallel = false;
    static float front = 0.6f;
    static float back = -0.6f;

    static float u_min = -0.6f;
    static float u_max = 0.6f;
    static float v_min = -0.6f;
    static float v_max = 0.6f;
    static float t_rand;

    static List<List<List<Float>>> individual_clip = new ArrayList<List<List<Float>>>();
    static List<List<Float>> v_edge = new ArrayList<List<Float>>();
    static List<List<Integer>> xy_perm = new ArrayList<List<Integer>>();

    static List<List<Float>> individual = new ArrayList<List<Float>>();
    static List<List<List<Float>>> all = new ArrayList<List<List<Float>>>();
    static  List<List<List<Float>>> transformed_view = new ArrayList<List<List<Float>>>();

    //static String file = "bound-cow.smf";
    static String file;
    static String output;

    public static void openIn(String file){
        try{
            try{
                //input file from arguments to be opened using Scanner
                Scanner in = new Scanner(new File(file));
                //Loop to traverse the file line by line
                while(in.hasNextLine()){
                    String input = in.nextLine();
                    String line[] = input.split(" ");

                    if(line[0].equals("v")){
                        List<Float> xy = new ArrayList<Float>();
                        float x = Float.parseFloat(line[1]);
                        xy.add(x);

                        float y = Float.parseFloat(line[2]);
                        xy.add(y);

                        float z = Float.parseFloat(line[3]);
                        xy.add(z);

                        xy.add(1.0f);
                        v_edge.add(xy);
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
                    }

                    if(line[0].equals("f")){
                        List<Integer> xy = new ArrayList<Integer>();

                        int x = Integer.parseInt(line[1]);
                        xy.add(x-1);

                        int y = Integer.parseInt(line[2]);
                        xy.add(y-1);

                        int z = Integer.parseInt(line[3]);
                        xy.add(z-1);

                        xy_perm.add(xy);
                    }


                    for (int i = 0; i < matrix.length; i++){
                        for (int j = 0; j < matrix.length; j++){
                            matrix[i][j] = 0;
                        }
                    }
                }

                in.close();
                //System.out.println(v_edge);
                //System.out.println(xy_perm);
                //System.out.println(matrix);
            }
            catch(FileNotFoundException err){
                System.out.println("Unable to load in file");
            }
        }
        catch (NullPointerException err){
            System.out.println("String file is empty");
        }
    }

    public static void first_trans(){

        //Translating co-ordinate and performing Transformations
        float comp_length = comp_total(x_vpn, y_vpn, z_vpn);
        float translate_vrp [][]= new float[4][4];

        translate_vrp[0][0] = 1;
        translate_vrp[0][1] = 0;
        translate_vrp[0][2] = 0;
        translate_vrp[0][3] = -x_vrp;

        translate_vrp[1][0] = 0;
        translate_vrp[1][1] = 1;
        translate_vrp[1][2] = 0;
        translate_vrp[1][3] = -y_vrp;

        translate_vrp[2][0] = 0;
        translate_vrp[2][1] = 0;
        translate_vrp[2][2] = 1;
        translate_vrp[2][3] = -z_vrp;

        translate_vrp[3][0] = 0;
        translate_vrp[3][1] = 0;
        translate_vrp[3][2] = 0;
        translate_vrp[3][3] = 1;

        /*
        for(int i =0 ;i<translate_vrp.length-1;i++){
            for(int j = 0; j<translate_vrp.length-1;j++){
                System.out.println(translate_vrp[i][j]);
            }
        }
        */


        float r0_z = x_vpn / comp_length;
        float r1_z = y_vpn / comp_length;
        float r2_z = z_vpn / comp_length;

        List<Float> first_cross = compute_cross(x_vup, y_vup, z_vup, r0_z, r1_z, r2_z);
        //System.out.println(first_cross);

        float length_firstcross = comp_total(first_cross.get(0), first_cross.get(1), first_cross.get(2));
        float r1_x = first_cross.get(0)/length_firstcross;
        float r2_x = first_cross.get(1)/length_firstcross;
        float r3_x = first_cross.get(2)/length_firstcross;

        List<Float> second_cross = compute_cross(r0_z, r1_z, r2_z, r1_x, r2_x, r3_x);
        float r1_y = second_cross.get(0);
        float r2_y = second_cross.get(1);
        float r3_y = second_cross.get(2);

        float first_rotate[][] = new float[4][4];
        first_rotate[0][0] = r1_x;
        first_rotate[0][1] = r2_x;
        first_rotate[0][2] = r3_x;
        first_rotate[0][3] = 0;

        first_rotate[1][0] = r1_y;
        first_rotate[1][1] = r2_y;
        first_rotate[1][2] = r3_y;
        first_rotate[1][3] = 0;

        first_rotate[2][0] = r0_z;
        first_rotate[2][1] = r1_z;
        first_rotate[2][2] = r2_z;
        first_rotate[2][3] = 0;

        first_rotate[3][0] = 0;
        first_rotate[3][1] = 0;
        first_rotate[3][2] = 0;
        first_rotate[3][3] = 1;

        /*
        for(int i =0 ;i<translate_vrp.length-1;i++){
            for(int j = 0; j<translate_vrp.length-1;j++){
                System.out.println(first_rotate[i][j]);
            }
        }
        */

        float r1[][] = multiply_float(first_rotate, translate_vrp);

        float s_matrix[][] = new float[4][4];
        s_matrix[0][0] = 1;
        s_matrix[0][1] = 0;
        s_matrix[0][2] = (((0.5f * (u_max + u_min)) - x_prp)/ z_prp);
        s_matrix[0][3] = 0;

        s_matrix[1][0] = 0;
        s_matrix[1][1] = 1;
        s_matrix[1][2] = (((0.5f * (v_max + v_min)) - y_prp)/ z_prp);
        s_matrix[1][3] = 0;

        s_matrix[2][0] = 0;
        s_matrix[2][1] = 0;
        s_matrix[2][2] = 1;
        s_matrix[2][3] = 0;

        s_matrix[3][0] = 0;
        s_matrix[3][1] = 0;
        s_matrix[3][2] = 0;
        s_matrix[3][3] = 1;

        /*
        for(int i =0 ;i<translate_vrp.length-1;i++){
            for(int j = 0; j<translate_vrp.length-1;j++){
                System.out.println(s_matrix[i][j]);
            }
        }
        */

        if(parallel){
            float parallel_trans[][] = new float[4][4];
            parallel_trans[0][0] = 1;
            parallel_trans[0][1] = 0;
            parallel_trans[0][2] = 0;
            parallel_trans[0][3] = -((u_max + u_min)/2);

            parallel_trans[1][0] = 0;
            parallel_trans[1][1] = 1;
            parallel_trans[1][2] = 0;
            parallel_trans[1][3] = -((v_max + v_min)/2);

            parallel_trans[2][0] = 0;
            parallel_trans[2][1] = 0;
            parallel_trans[2][2] = 1;
            parallel_trans[2][3] = -front;

            parallel_trans[3][0] = 0;
            parallel_trans[3][1] = 0;
            parallel_trans[3][2] = 0;
            parallel_trans[3][3] = 1;

            /*
            for(int i =0 ;i<translate_vrp.length-1;i++){
                for(int j = 0; j<translate_vrp.length-1;j++){
                    System.out.println(parallel_trans[i][j]);
                }
            }
            */

            float s_matrix_trans[][] = new float[4][4];
            s_matrix_trans[0][0] = (2/(u_max - u_min));
            s_matrix_trans[0][1] = 0;
            s_matrix_trans[0][2] = 0;
            s_matrix_trans[0][3] = 0;

            s_matrix_trans[1][0] = 0;
            s_matrix_trans[1][1] = (2/(v_max - v_min));
            s_matrix_trans[1][2] = 0;
            s_matrix_trans[1][3] = 0;

            s_matrix_trans[2][0] = 0;
            s_matrix_trans[2][1] = 0;
            s_matrix_trans[2][2] = (1/(front - back));
            s_matrix_trans[2][3] = 0;

            s_matrix_trans[3][0] = 0;
            s_matrix_trans[3][1] = 0;
            s_matrix_trans[3][2] = 0;
            s_matrix_trans[3][3] = 1;

            /*
            for(int i =0 ;i<translate_vrp.length-1;i++){
                for(int j = 0; j<translate_vrp.length-1;j++){
                    System.out.println(s_matrix_trans[i][j]);
                }
            }
            */

            float r2[][] = multiply_float(s_matrix, r1);
            float r3[][] = multiply_float(parallel_trans, r2);
            float N_par[][] = multiply_float(s_matrix_trans, r3);

            for(int i = 0; i< v_edge.size(); i++){
                List<Float> xy = new ArrayList<Float>();
                float compt[][] = new float[4][1];
                compt[0][0] = v_edge.get(i).get(0);
                compt[1][0] = v_edge.get(i).get(1);
                compt[2][0] = v_edge.get(i).get(2);
                compt[3][0] = v_edge.get(i).get(3);

                float [][] r = multiply_float(N_par, compt);
                xy.add(r[0][0]);
                xy.add(r[1][0]);

                v_edge.set(i, xy);
                //System.out.println(v_edge);
            }
        }

        else{
            float trans_prp[][] = new float[4][4];
            trans_prp[0][0] = 1;
            trans_prp[0][1] = 0;
            trans_prp[0][2] = 0;
            trans_prp[0][3] = -x_prp;

            trans_prp[1][0] = 0;
            trans_prp[1][1] = 1;
            trans_prp[1][2] = 0;
            trans_prp[1][3] = -y_prp;

            trans_prp[2][0] = 0;
            trans_prp[2][1] = 0;
            trans_prp[2][2] = 1;
            trans_prp[2][3] = -z_prp;

            trans_prp[3][0] = 0;
            trans_prp[3][1] = 0;
            trans_prp[3][2] = 0;
            trans_prp[3][3] = 1;

            /*
            for(int i =0 ;i<translate_vrp.length-1;i++){
                for(int j = 0; j<translate_vrp.length-1;j++){
                    System.out.println(trans_prp[i][j]);
                }
            }
            */

            float scale[][] = new float [4][4];
            scale[0][0] = (2 * z_prp)/((u_max - u_min) * (z_prp - back));
            scale[0][1] = 0;
            scale[0][2] = 0;
            scale[0][3] = 0;

            scale[1][0] = 0;
            scale[1][1] = (2 * z_prp)/((v_max - v_min) * (z_prp - back));
            scale[1][2] = 0;
            scale[1][3] = 0;

            scale[2][0] = 0;
            scale[2][1] = 0;
            scale[2][2] = (1/(z_prp - back));
            scale[2][3] = 0;

            scale[3][0] = 0;
            scale[3][1] = 0;
            scale[3][2] = 0;
            scale[3][3] = 1;
            /*
            for(int i =0 ;i<translate_vrp.length-1;i++){
                for(int j = 0; j<translate_vrp.length-1;j++){
                    System.out.println(scale[i][j]);
                }
            }
            */
            float r2[][] = multiply_float(trans_prp, r1);
            float r3[][] = multiply_float(s_matrix, r2);
            float N_per[][] = multiply_float(scale, r3);

            for(int i = 0; i< v_edge.size(); i++){
                List<Float> xy = new ArrayList<Float>();
                float compt[][] = new float[4][1];

                compt[0][0] = v_edge.get(i).get(0);
                compt[1][0] = v_edge.get(i).get(1);
                compt[2][0] = v_edge.get(i).get(2);
                compt[3][0] = v_edge.get(i).get(3);

                float final_r[][] = multiply_float(N_per, compt);

                xy.add(final_r[0][0]);
                xy.add(final_r[1][0]);
                xy.add(final_r[2][0]);

                v_edge.set(i, xy);
                //System.out.println(xy);
                //System.out.println(v_edge);
            }
        }
    }

    public static List<Float> compute_cross(float x0, float y0, float z0, float x1, float y1, float z1){
        List<Float> cross = new ArrayList<>();
        float vector1[][] = new float[3][1];
        float vector2[][] = new float[3][1];

        vector1[0][0] = x0;
        vector1[1][0] = y0;
        vector1[2][0] = z0;

        vector2[0][0] = x1;
        vector2[1][0] = y1;
        vector2[2][0] = z1;

        float x = (vector1[1][0]*vector2[2][0])-(vector1[2][0]*vector2[1][0]);
        float y = (vector1[2][0]*vector2[0][0])-(vector1[0][0]*vector2[2][0]);
        float z = (vector1[0][0]*vector2[1][0])-(vector1[1][0]*vector2[0][0]);

        cross.add(x);
        cross.add(y);
        cross.add(z);

        return cross;
    }

    public static float comp_total(float x, float y, float z){
        return (float) Math.sqrt(x*x + y*y + z*z);
    }

    public static void wireframe(){
        if(!parallel){
            for(int i = 0; i< v_edge.size(); i++){
                List<Float> xy = new ArrayList<Float>();

                float x = v_edge.get(i).get(0);
                float y = v_edge.get(i).get(1);
                float z = v_edge.get(i).get(2);

                float b = z/ t_rand;

                x = x/b;
                y = y/b;

                xy.add(x);
                xy.add(y);

                v_edge.set(i, xy);
            }
        }
    }

    public static void convert(){
        for(int i = 0; i< xy_perm.size(); i++){
            List<List<Float>> individual = new ArrayList<List<Float>>();

            int post1 = xy_perm.get(i).get(0);
            List<Float> xy1 = v_edge.get(post1);
            individual.add(xy1);

            int post2 = xy_perm.get(i).get(1);
            List<Float> xy2 = v_edge.get(post2);
            individual.add(xy2);

            int post3 = xy_perm.get(i).get(2);
            List<Float> xy3 = v_edge.get(post3);
            individual.add(xy3);

            all.add(individual);
        }
    }

    public static void sutherland_hodg(){
        if(parallel){
            x_bottom = -1.0f;
            x_top = 1.0f;

            y_bottom = -1.0f;
            y_top = 1.0f;
        }
        else{
            x_bottom = -Math.abs(t_rand);
            x_top = Math.abs(t_rand);

            y_bottom = -Math.abs(t_rand);
            y_top = Math.abs(t_rand);
        }

        top();
        bottom();
        right();
        left();
    }
    public static void top(){
        clip_xy = top;
        line_clipping();
        all.clear();
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
            all.add(temp);
        }
        individual_clip.clear();
    }
    public static void right(){
        clip_xy = right;
        line_clipping();
        all.clear();
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
            all.add(temp);
        }
        individual_clip.clear();
    }
    public static void bottom(){
        clip_xy = bottom;
        line_clipping();
        all.clear();
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
            all.add(temp);
        }
        individual_clip.clear();
    }
    public static void left(){
        clip_xy = left;
        line_clipping();
    }
    public static void line_clipping(){
        for(int i = 0; i< all.size(); i++){
            for(int j = 0; j< all.get(i).size()-1; j++)
            {
                float x0 = all.get(i).get(j).get(0);
                float y0 = all.get(i).get(j).get(1);
                float x1 = all.get(i).get(j+1).get(0);
                float y1 = all.get(i).get(j+1).get(1);

                if(position(x0, y0)) {
                    if(j == 0){
                        add_vertex(x0, y0);
                    }
                    if(position(x1, y1)){
                        add_vertex(x1, y1);

                    }
                    else{
                        intersect(x0, y0, x1, y1, clip_xy);
                        add_vertex(x_int, y_int);
                    }
                }
                else{
                    if(position(x1, y1)){
                        intersect(x1, y1, x0, y0, clip_xy);
                        add_vertex(x_int, y_int);

                        add_vertex(x1, y1);
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
    public static void add_vertex(float x, float y){
        List<Float> xy = new ArrayList<Float>();
        xy.add(x);
        xy.add(y);

        individual.add(xy);
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

        if((clip_xy == left) && (x > x_bottom)){
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
                float num_x = viewport_x_top - viewport_x_bottom;
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
        for(int i = 0; i< viewport_scale.size(); i++){
            List<List<Float>> individual = new ArrayList<List<Float>>();
            for(int j = 0; j< viewport_scale.get(i).size(); j++){

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

                xy.add(x1);
                xy.add(y1);
                individual.add(xy);
            }

            transformed_view.add(individual);

        }

    }
    public static float[][] multiply_float(float[][] a, float[][] b){
        int row_length = a.length;
        int acol_length = a[0].length;
        int bcol_length = b[0].length;

        float [][] mult = new float[row_length][bcol_length];

        for(int x=0; x<row_length; x++){
            for(int y=0; y<bcol_length; y++){
                for(int z=0; z<acol_length; z++){
                    mult[x][y] += a[x][z] * b[z][y];
                }
            }
        }

        return mult;
    }

    public static void dda(){
        float x, y;
        //Implementation of Digital Differential Analyzer Line Drawing Algorithm
        for (int i = 0; i< transformed_view.size(); i++){
            for(int j = 0; j< transformed_view.get(i).size() - 1; j++) {
                float x0 = transformed_view.get(i).get(j).get(0);
                float x1 = transformed_view.get(i).get(j + 1).get(0);
                xd = x1 - x0;

                float y0 = transformed_view.get(i).get(j).get(1);
                float y1 = transformed_view.get(i).get(j + 1).get(1);
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
                x = x0;

                y_step = yd/through;
                y = y0;


                for (int k=0; k<through; k++){
                    if(!(x < viewport_x_bottom || y < viewport_y_bottom || x > viewport_x_top || y > viewport_y_top))
                        matrix[Math.round(y)][Math.round(x)] = 5;

                    x = x + x_step;
                    y = y + y_step;
                }
            }
        }
    }

    public static void xpm_out(String file){
        try {
            try {
                PrintWriter xpm_File = new PrintWriter(file + ".xpm", "UTF-8");
                xpm_File.println("/*XPM*/");
                xpm_File.println("static char *sco100[]={" + '\n' + "//* width height num_colors chars_per_pixel */");
                xpm_File.println("\"500 500 2 1,\"");
                xpm_File.println("/* colors */");
                xpm_File.println("\"0 c #ffffff,\"" + '\n' + "\"5 c #000000,\"");
                xpm_File.println("/* matrix */");
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
                }
                catch (UnsupportedEncodingException err) {
                    System.out.println("No file was created");
                }
        }
        catch(FileNotFoundException err){
            System.out.println("Unable to find file");
        }
    }


    public static void main(String[] args) throws NullPointerException {
        for (int i = 0; i < args.length; i+=2) {
            if(args[i].equals("-f")) {
                file = args[i + 1];
                output = file.substring(0, file.lastIndexOf('.'));
            }
            else if(args[i].equals("-j")){
                viewport_x_bottom = Integer.parseInt(args[i+1]);
                output += "_j_"+viewport_x_bottom;
            }
            else if(args[i].equals("-k")){
                viewport_y_bottom = Integer.parseInt(args[i+1]);
                output += "_k_"+viewport_y_bottom;
            }
            else if(args[i].equals("-o")){
                viewport_x_top = Integer.parseInt(args[i + 1]);
                output += "_o_" + viewport_x_top;
            }
            else if(args[i].equals("-p")){
                viewport_y_top = Integer.parseInt(args[i+1]);
                output += "_p_"+viewport_y_top;
            }
            else if(args[i].equals("-x")){
                x_prp = Float.parseFloat(args[i+1]);
                output += "_x_"+ x_prp;
            }
            else if(args[i].equals("-y")){
                y_prp = Float.parseFloat(args[i+1]);
                output += "_y_"+ y_prp;
            }
            else if(args[i].equals("-z")){
                z_prp = Float.parseFloat(args[i+1]);
                output += "_z_"+ z_prp;
            }
            else if(args[i].equals("-X")){
                x_vrp = Float.parseFloat(args[i+1]);
                output += "_X_"+ x_vrp;
            }
            else if(args[i].equals("-Y")){
                y_vrp = Float.parseFloat(args[i+1]);
                output += "_Y_"+ y_vrp;
            }
            else if(args[i].equals("-Z")){
                z_vrp = Float.parseFloat(args[i+1]);
                output += "_Z_"+ z_vrp;
            }
            else if(args[i].equals("-q")){
                x_vpn = Float.parseFloat(args[i+1]);
                output += "_q_"+ x_vpn;
            }
            else if(args[i].equals("-r")){
                y_vpn = Float.parseFloat(args[i+1]);
                output += "_r_"+ y_vpn;
             }
            else if(args[i].equals("-w")){
                z_vpn = Float.parseFloat(args[i+1]);
                output += "_w_"+ z_vpn;
            }
            else if(args[i].equals("-Q")){
                x_vup = Float.parseFloat(args[i+1]);
                output += "_Q_"+ x_vup;
            }
            else if(args[i].equals("-R")){
                y_vup = Float.parseFloat(args[i+1]);
                output += "_R_"+ y_vup;
            }
            else if(args[i].equals("-W")){
                z_vup = Float.parseFloat(args[i+1]);
                output += "_W_"+ z_vup;
            }
            else if(args[i].equals("-u")){
                u_min = Float.parseFloat(args[i+1]);
                output += "_u_"+ u_min;
            }
            else if(args[i].equals("-v")){
                v_min = Float.parseFloat(args[i+1]);
                output += "_v_"+ v_min;
            }
            else if(args[i].equals("-U")){
                u_max = Float.parseFloat(args[i+1]);
                output += "_U_"+ u_max;
            }
            else if(args[i].equals("-V")){
                v_max = Float.parseFloat(args[i+1]);
                output += "_V_"+ v_max;
            }
            else if(args[i].equals("-P")){
                parallel = true;
                output += "_P";
            }
        }
        t_rand = z_prp / (back - z_prp);


        openIn(file);
        first_trans();
        wireframe();
        convert();
        sutherland_hodg();
        viewport();
        dda();
        xpm_out(output);
    }
}