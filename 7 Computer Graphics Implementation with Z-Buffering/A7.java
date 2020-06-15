import java.io.*;
import java.util.*;

/*
Implementation of Sutherland-Hodgman algorithm was gathered from;
        "https://www.longsteve.com/fixmybugs/?page_id=203"

        Homogeneous coordinates implementation
        "https://www.developer.com/java/other/article.php/3714626/Understanding-Transforms-in-Java-2D.htm#Figure_3"
        "https://javaconceptoftheday.com/how-to-perform-matrix-operations-in-java/"
        "https://www.programiz.com/java-programming/multidimensional-array"

        Cross Product Computation
        "http://robotics.stanford.edu/~birch/projective/node4.html"
        "http://www.java2s.com/Code/Android/Game/Computethecrossproductoftwovectors.htm"

        Projections
        "https://www.tutorialspoint.com/computer_graphics/3d_computer_graphics.htm"
        "https://stackoverflow.com/questions/724219/how-to-convert-a-3d-point-into-2d-perspective-projection?rq=1"

        3D Transformations
        "https://www.tutorialspoint.com/computer_graphics/3d_transformation.htm"

        Implementation of Scan Fill Algorithm found here;
        "https://bedeveloper.wordpress.com/a-c-program-to-fill-polygon-using-scan-line-fill-algorithm/"
        "http://code-heaven.blogspot.com/2009/10/simple-c-program-for-scan-line-polygon.html"

        Z-Buffering
        "https://stackoverflow.com/questions/26372092/z-buffering-algorithm-not-drawing-100-correctly"
        "https://www.programcreek.com/java-api-examples/index.php?source_dir=Render-master/src/com/pascucci/render/engine/ZBuffer.java"
        "http://www.mscs.mu.edu/~mikes/174.F2004/demos/dec10/ch11src/src/com/brackeen/javagamebook/graphics3D/ZBuffer.java

        Other sources;
        "https://stackoverflow.com/questions/5393254/java-comparator-class-to-sort-arrays"

*/
public class A7
{
    static float x_prp = 0.0f;
    static float y_prp = 0.0f;
    static float z_prp = 1.0f;

    static String file1 = " ";
    static  String file2 = " ";
    static String file3 = " ";
    static String output;

    static float x_vrp = 0.0f;
    static float y_vrp = 0.0f;
    static float z_vrp = 0.0f;

    static boolean parallel = false;
    static float x_bottom;
    static float y_bottom;
    static float x_top;
    static float y_top;

    static float x_vpn = 0.0f;
    static float y_vpn = 0.0f;
    static float z_vpn = -1.0f;

    static int viewport_x_bottom = 0;
    static int viewport_y_bottom = 0;
    static int viewport_x_top = 500;
    static int viewport_y_top = 500;

    static float x_vup = 0.0f;
    static float y_vup = 1.0f;
    static float z_vup = 0.0f;

    static int width;
    static int height;
    static String matrix[][];
    static float zBuffer[][];

    static float u_min = -0.7f;
    static float u_max = 0.7f;
    static float v_min = -0.7f;
    static float v_max = 0.7f;

    static float front = 0.6f;
    static float back = -0.6f;
    static float t_rand;
    static float z_near;
    static float z_far;

    static List<String> red_color = new ArrayList<String>();
    static List<String> green_color = new ArrayList<String>();
    static List<String> blue_color = new ArrayList<String>();

    static List<List<Float>> v_edge = new ArrayList<List<Float>>();
    static List<List<Integer>> xy_perm = new ArrayList<List<Integer>>();

    static List<List<List<Float>>> individual_clip = new ArrayList<List<List<Float>>>();
    static List<List<List<Float>>> e_all = new ArrayList<List<List<Float>>>();

    static List<List<List<Float>>> all = new ArrayList<List<List<Float>>>();
    static List<List<List<Float>>> transformed_view = new ArrayList<List<List<Float>>>();

    public static void openIn(String file, int color){
        try{
            try{
                Scanner in = new Scanner(new File(file));

                while(in.hasNextLine()){
                    String input = in.nextLine();
                    String line[] = input.split(" ");

                    if(input.trim().length() != 0 && line[0].equals("v")) {
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

                    if(input.trim().length() != 0 && line[0].equals("f")){
                        List<Integer> xy = new ArrayList<Integer>();

                        int x = Integer.parseInt(line[1]);
                        xy.add(x-1);

                        int y = Integer.parseInt(line[2]);
                        xy.add(y-1);

                        int z = Integer.parseInt(line[3]);
                        xy.add(z-1);

                        xy.add(color);
                        xy_perm.add(xy);
                    }
                }
                in.close();
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
                xy.add(z);

                v_edge.set(i, xy);
            }
        }
    }
    public static void convert(){
        for(int i = 0; i< xy_perm.size(); i++){
            List<List<Float>> individual = new ArrayList<List<Float>>();

            int post1 = xy_perm.get(i).get(0);
            List<Float> row1 = v_edge.get(post1);
            individual.add(row1);

            int index_2 = xy_perm.get(i).get(1);
            List<Float> row2 = v_edge.get(index_2);
            individual.add(row2);

            int index_3 = xy_perm.get(i).get(2);
            List<Float> row3 = v_edge.get(index_3);
            individual.add(row3);

            int color =  xy_perm.get(i).get(3);

            List<Float> row4 = new ArrayList<Float>();
            row4.add((float) color);

            individual.add(row1);
            individual.add(row4);

            all.add(individual);
        }
    }

    public static float comp_valz(List<List<Float>> individual, float ix, float iy, float jx, float jy, float kx, float ky){
        float x1 = individual.get(0).get(0);
        float y1 = individual.get(0).get(1);
        float z1 = individual.get(0).get(2);

        float x2 = individual.get(1).get(0);
        float y2 = individual.get(1).get(1);
        float z2 = individual.get(1).get(2);

        float x3 = individual.get(2).get(0);
        float y3 = individual.get(2).get(1);
        float z3 = individual.get(2).get(2);

        float l1 = comp_total(ix - x1, iy - y1, 0);
        float l2 = comp_total(x2 - x1, y2 - y1, 0);

        float izz = z1 + (l1/l2) * (z2 - z1);

        float l3 = comp_total(jx - x1, jy - y1, 0);
        float l4 = comp_total(x3 - x1, y3 - y1, 0);

        float jzz = z1 + (l3/l4) * (z3 - z1);

        float l5 = comp_total(kx - ix, ky - iy, 0);
        float l6 = comp_total(jx - ix, jy - iy, 0);

        float tot_z = izz + (l5/l6) * (jzz - izz);

        return tot_z;
    }
    public static float list_Max(List<List<Float>> in_list){
        float val = -1;
        int i = 0;
        while(i<in_list.size()){
            float y = in_list.get(i).get(1);
            if(Math.round(y) > Math.round(val)){
                val = y;
            }
            i++;
        }
        return val;
    }
    public static float list_Min(List<List<Float>> in_list){
        float val = Float.MAX_VALUE;
        int i = 0;
        while(i<in_list.size()){
            float y = in_list.get(i).get(1);
            if(Math.round(y) < Math.round(val)){
                val = y;
            }
            i++;
        }
        return val;
    }

    public static void viewport(){
        if(parallel){
            x_bottom = -1.0f;
            y_bottom = -1.0f;

            x_top = 1.0f;
            y_top = 1.0f;

            z_near = 0;
            z_far = -1;
        }

        else{
            x_bottom = -Math.abs(t_rand);
            y_bottom = -Math.abs(t_rand);

            x_top = Math.abs(t_rand);
            y_top = Math.abs(t_rand);

            z_near = (z_prp - front)/(back - z_prp);
            z_far = -1;
        }

        /*Translation to world window origin
            |x2|   |1 0 -x_bottom| |x|
            |y2| = |0 1 -y_bottom|*|y|
            |1 |   |0 0  1       | |1|
            */
        List<List<List<Float>>> ww_translate_poly = new ArrayList<List<List<Float>>>();

        for(int i = 0; i< all.size(); i++){
            List<List<Float>> individual = new ArrayList<List<Float>>();
            for(int j = 0; j< all.get(i).size(); j++){

                List<Float> xy = new ArrayList<Float>();
                float color = all.get(i).get(4).get(0);

                if(j<4){
                    float x = all.get(i).get(j).get(0);
                    float y = all.get(i).get(j).get(1);
                    float z1 = all.get(i).get(j).get(2);

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
                    xy.add(z1);
                    xy.add(color);

                    individual.add(xy);
                }
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
            List<List<Float>> indivudual = new ArrayList<List<Float>>();
            for(int j=0; j<ww_translate_poly.get(i).size(); j++){
                List<Float> xy = new ArrayList<Float>();

                float x = ww_translate_poly.get(i).get(j).get(0);
                float y = ww_translate_poly.get(i).get(j).get(1);
                float z = ww_translate_poly.get(i).get(j).get(2);
                float color = ww_translate_poly.get(i).get(j).get(3);

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
                xy.add(z);
                xy.add(color);

                indivudual.add(xy);
            }
            viewport_scale.add(indivudual);
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
                float z = viewport_scale.get(i).get(j).get(2);
                float color = viewport_scale.get(i).get(j).get(3);

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
                xy.add(z);
                xy.add(color);

                individual.add(xy);
            }

            transformed_view.add(individual);
        }
    }

    public static void scan_fill(List<List<Float>> polygon){
        int color = Math.round(polygon.get(0).get(3));

        for(int i = 0; i< individual_clip.size(); i++){
            for(int j = 0; j< individual_clip.get(i).size(); j+=2){
                float x0 = individual_clip.get(i).get(j).get(0);
                float y0 = individual_clip.get(i).get(j).get(1);
                float x1 = individual_clip.get(i).get(j+1).get(0);
                float y1 = individual_clip.get(i).get(j+1).get(1);
                float temx = x0;

                while(temx <= x1){

                    float zvar = comp_valz(polygon, x0, y0, x1, y1, temx, y0);

                    if(temx < 0) temx = 0;
                    if(y0 < 0) y0 = 0;
                    if(y0 > 500) y0 = 500;
                    if(temx > 500) temx = 500;

                    if((zvar < front) && (zvar > zBuffer[Math.round(y0)][Math.round(temx)])){
                        zBuffer[Math.round(y0)][Math.round(temx)] = zvar;

                        int decision = (int)(20 * (zvar - z_far)/(z_near - z_far));

                        if(decision >= 20){
                            decision = 19;
                        }
                        if(decision < 0){
                            decision = 0;
                        }

                        if(color == 0){
                            matrix[Math.round(y0)][Math.round(temx)] = red_color.get(decision);
                            }

                        else if(color == 1){
                                matrix[Math.round(y0)][Math.round(temx)] = green_color.get(decision);
                        }

                        else{
                            matrix[Math.round(y0)][Math.round(temx)] = blue_color.get(decision);
                        }
                    }

                    temx++;
                }
            }
        }
    }

    public static void intersect(float x0, float y0, float x1, float y1, float y, float clip_xy){
        List<Float> xy = new ArrayList<Float>();

        float dx = x1 - x0;
        float dy = y1 - y0;

        float x = x0 + (dx/dy)*(y - y0);

        xy.add(x);
        xy.add(y);

        individual_clip.get((int)(y-clip_xy)).add(xy);
    }

    public static void start_fill(){

        red_color.add("A");
        red_color.add("B");
        red_color.add("C");
        red_color.add("D");
        red_color.add("E");
        red_color.add("F");
        red_color.add("G");
        red_color.add("H");
        red_color.add("I");
        red_color.add("J");
        red_color.add("K");
        red_color.add("L");
        red_color.add("M");
        red_color.add("N");
        red_color.add("O");
        red_color.add("P");
        red_color.add("Q");
        red_color.add("R");
        red_color.add("S");
        red_color.add("T");

        green_color.add("U");
        green_color.add("V");
        green_color.add("W");
        green_color.add("X");
        green_color.add("Y");
        green_color.add("Z");
        green_color.add("1");
        green_color.add("2");
        green_color.add("3");
        green_color.add("4");
        green_color.add("5");
        green_color.add("6");
        green_color.add("7");
        green_color.add("8");
        green_color.add("9");
        green_color.add("*");
        green_color.add("a");
        green_color.add("b");
        green_color.add("c");
        green_color.add("d");

        blue_color.add("e");
        blue_color.add("f");
        blue_color.add("g");
        blue_color.add("h");
        blue_color.add("i");
        blue_color.add("j");
        blue_color.add("k");
        blue_color.add("l");
        blue_color.add("m");
        blue_color.add("n");
        blue_color.add("o");
        blue_color.add("p");
        blue_color.add("q");
        blue_color.add("r");
        blue_color.add("s");
        blue_color.add("t");
        blue_color.add("u");
        blue_color.add("v");
        blue_color.add("w");
        blue_color.add("x");
        blue_color.add("y");
        blue_color.add("z");

        for(int i = 0; i< transformed_view.size(); i++){
            float ymax = list_Max(transformed_view.get(i));
            float ymin = list_Min(transformed_view.get(i));

            for(int s = (int)(ymin); s<= (int)(ymax); s++){
                e_all.add(new ArrayList<List<Float>>());
                individual_clip.add(new ArrayList<List<Float>>());
            }

            for(int j = 0; j< transformed_view.get(i).size() - 1; j++){

                float ymin_edge = transformed_view.get(i).get(j).get(1);
                float ymax_edge = transformed_view.get(i).get(j+1).get(1);

                for(int y = (int)(ymin); y<=(int)(ymax); y++){

                    if(((Math.round(y) >= Math.round(ymin_edge) && Math.round(y) < Math.round(ymax_edge)) ||
                            (Math.round(y) >= Math.round(ymax_edge) && Math.round(y) < Math.round(ymin_edge)))
                            && Math.round(ymin_edge) != Math.round(ymax_edge)){
                        List<Float> xy = new ArrayList<Float>();
                        float x1 = transformed_view.get(i).get(j).get(0);
                        float x2 = transformed_view.get(i).get(j+1).get(0);

                        xy.add(x1);
                        xy.add(ymin_edge);
                        xy.add(x2);
                        xy.add(ymax_edge);

                        e_all.get((int)(y-ymin)).add(xy);
                    }
                }
            }

            for(int y = (int)(ymin); y <= (int)(ymax); y++){
                individual_clip.clear();
                for(int s = (int)(ymin); s<=(int)(ymax); s++){
                    individual_clip.add(new ArrayList<List<Float>>());
                }

                for(int j = 0; j< e_all.get((int)(y-ymin)).size(); j++){
                    float x1 = e_all.get((int)(y-ymin)).get(j).get(0);
                    float y1 = e_all.get((int)(y-ymin)).get(j).get(1);
                    float x2 = e_all.get((int)(y-ymin)).get(j).get(2);
                    float y2 = e_all.get((int)(y-ymin)).get(j).get(3);

                    intersect(x1, y1, x2, y2, y, ymin);
                }

                for(int a = 0; a< individual_clip.size(); a++){
                    Collections.sort(individual_clip.get(a), new Comparator<List<Float>>(){
                        public int compare(List<Float> list1, List<Float> list2){
                            return list1.get(0).compareTo(list2.get(0));
                        }
                    });
                }
                scan_fill(transformed_view.get(i));
            }
            e_all.clear();
            individual_clip.clear();
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

    public static void xpm_out(String file){
        try{
            try{
                PrintWriter xpm_File = new PrintWriter(file + ".xpm", "UTF-8");
                xpm_File.println("/*XPM*/");
                xpm_File.println("static char *sco100[]={" + '\n' + "//* width height num_colors chars_per_pixel */");
                xpm_File.println("\""+ width + " " + height + " " + "61 1,\"");
                xpm_File.println("/* colors */");

                //Black
                xpm_File.println("\"0 c #000000,\"");

                //Red
                xpm_File.println("\"A c #FA8072,\"");
                xpm_File.println("\"B c #FF2400,\"");
                xpm_File.println("\"C c #ED2939,\"");
                xpm_File.println("\"D c #7C0A02,\"");
                xpm_File.println("\"E c #C21807,\"");
                xpm_File.println("\"F c #CD5C5C,\"");
                xpm_File.println("\"G c #E0115F,\"");
                xpm_File.println("\"H c #B22222,\"");
                xpm_File.println("\"I c #960018,\"");
                xpm_File.println("\"J c #EA3C53,\"");
                xpm_File.println("\"K c #800000,\"");
                xpm_File.println("\"L c #FF0800,\"");
                xpm_File.println("\"M c #7E191B,\"");
                xpm_File.println("\"N c #D21F3C,\"");
                xpm_File.println("\"O c #BF0A30,\"");
                xpm_File.println("\"P c #CA3433,\"");
                xpm_File.println("\"Q c #dd0000,\"");
                xpm_File.println("\"R c #Dff280,\"");
                xpm_File.println("\"S c #5e1914,\"");
                xpm_File.println("\"T c #8d021f,\"");

                //Green
                xpm_File.println("\"U c #5A6351,\"");
                xpm_File.println("\"V c #636F57,\"");
                xpm_File.println("\"W c #526F35,\"");
                xpm_File.println("\"X c #3B5323,\"");
                xpm_File.println("\"Y c #78AB46,\"");
                xpm_File.println("\"Z c #4A7023,\"");
                xpm_File.println("\"1 c #66CD00,\"");
                xpm_File.println("\"2 c #458B00,\"");
                xpm_File.println("\"3 c #76EE00,\"");
                xpm_File.println("\"4 c #8BA870,\"");
                xpm_File.println("\"5 c #9CBA7F,\"");
                xpm_File.println("\"6 c #3D5229,\"");
                xpm_File.println("\"7 c #488214,\"");
                xpm_File.println("\"8 c #46523C,\"");
                xpm_File.println("\"9 c #61B329,\"");
                xpm_File.println("\"* c #687E5A,\"");
                xpm_File.println("\"a c #8FA880,\"");
                xpm_File.println("\"b c #5DFC0A,\"");
                xpm_File.println("\"c c #476A34,\"");
                xpm_File.println("\"d c #84BE6A,\"");

                //Blue
                xpm_File.println("\"e c #838B8B,\"");
                xpm_File.println("\"f c #7A8B8B,\"");
                xpm_File.println("\"g c #66CCCC,\"");
                xpm_File.println("\"h c #00FFFF,\"");
                xpm_File.println("\"i c #E0FFFF,\"");
                xpm_File.println("\"j c #00868B,\"");
                xpm_File.println("\"m c #00CED1,\"");
                xpm_File.println("\"n c #00C5CD,\"");
                xpm_File.println("\"o c #67E6EC,\"");
                xpm_File.println("\"p c #65909A,\"");
                xpm_File.println("\"q c #39B7CD,\"");
                xpm_File.println("\"r c #68838B,\"");
                xpm_File.println("\"s c #B2DFEE,\"");
                xpm_File.println("\"t c #0099CC,\"");
                xpm_File.println("\"u c #0000c3,\"");
                xpm_File.println("\"v c #0000d0,\"");
                xpm_File.println("\"w c #0000dd,\"");
                xpm_File.println("\"x c #0000ea,\"");
                xpm_File.println("\"y c #0000f7,\"");
                xpm_File.println("\"z c #0000ff,\"");

                xpm_File.println("/* matrix */");
                for(int x =0; x < height; x++) {
                    xpm_File.print("\"");
                    //System.out.print("\"");
                    for (int y = 0; y < width; y++) {
                        xpm_File.print(matrix[height-x-1][y]);
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
            catch(UnsupportedEncodingException err){
                System.out.println("No file was created");
            }
        }
        catch(FileNotFoundException err){
            System.out.println("Unable to find file");

        }
    }



    public static void main(String[] args) throws NullPointerException{
        for (int i=0; i<args.length; i+=2){
            if (args[i].equals("-f")) {
                file1 = args[i + 1];
                output = file1.substring(0, file1.lastIndexOf('.'));
            }
            else if (args[i].equals("-g")) {
                file2 = args[i + 1];
                output += file2.substring(0, file2.lastIndexOf('.'));
            }
            else if (args[i].equals("-i")) {
                file3 = args[i + 1];
                output += file3.substring(0, file3.lastIndexOf('.'));
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
            else if(args[i].equals("-F")){
                front = Float.parseFloat(args[i + 1]);
                output += "_F_" + front;
            }
            else if(args[i].equals("-B")){
                back = Float.parseFloat(args[i + 1]);
                output += "_B_" + back;
            }
        }

        t_rand = z_prp /(back - z_prp);

        openIn(file1,0);

        width = 501;
        height = 501;
        matrix = new String[height][width];
        zBuffer = new float[height][width];

        //Initialing pixel and zbuffer
        for (int i=0; i<height; i++)
        {
            for (int j=0; j<width; j++)
            {
                matrix[i][j] = "0";
                zBuffer[i][j] = -1;
            }
        }
        first_trans();
        wireframe();
        convert();
        viewport();
        start_fill();

        if(!file2.isEmpty()){
            red_color = new ArrayList<String>();
            green_color = new ArrayList<String>();
            blue_color = new ArrayList<String>();
            v_edge = new ArrayList<List<Float>>();
            xy_perm = new ArrayList<List<Integer>>();
            all = new ArrayList<List<List<Float>>>();
            transformed_view = new ArrayList<List<List<Float>>>();
            individual_clip = new ArrayList<List<List<Float>>>();
            e_all = new ArrayList<List<List<Float>>>();
            openIn(file2,1);
            first_trans();
            wireframe();
            convert();
            viewport();
            start_fill();

        }

        if(!file3.isEmpty()){
            red_color = new ArrayList<String>();
            green_color = new ArrayList<String>();
            blue_color = new ArrayList<String>();
            v_edge = new ArrayList<List<Float>>();
            xy_perm = new ArrayList<List<Integer>>();
            all = new ArrayList<List<List<Float>>>();
            transformed_view = new ArrayList<List<List<Float>>>();
            individual_clip = new ArrayList<List<List<Float>>>();
            e_all = new ArrayList<List<List<Float>>>();
            openIn(file3,2);
            first_trans();
            wireframe();
            convert();
            viewport();
            start_fill();
        }
        xpm_out(output);
    }
}