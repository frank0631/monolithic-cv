package com.frank0631.monolithic;

import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import remon_pcl.pcl.*;
import remon_pcl.pcl.search.KdTree_PointXYZ;

/**
 * Created by frank on 10/18/14.
 */
public class Testbench {

    public static void main(String[] args){
        System.out.println("Hello,OpenCV");
        nu.pattern.OpenCV.loadShared();
        System.loadLibrary("opencv_java249");

        //display Params
        boolean showEpipole=true;
        boolean showMatch=true;
        boolean showStitch=true;
        boolean displayWindow=false;


        //Quick 3D Image Pair processing for books and rooms
        Mat roomA = Highgui.imread("resources/room/roomA.jpg");
        System.out.println("width:"+roomA.width());
        Mat roomB = Highgui.imread("resources/room/roomB.jpg");
        ImagePair3DProcess.Quick3DPairProcess(roomA, roomB, showEpipole, showMatch, showStitch, displayWindow,"roomA vs roomB","results/room");
        ImagePair3DProcess.Quick3DPairProcess(roomB, roomA, showEpipole, showMatch, showStitch, displayWindow,"roomB vs roomA","results/room");//Broken left to right

        Mat Book1 = Highgui.imread("resources/books/book1.jpg");
        Mat Book2 = Highgui.imread("resources/books/book2.jpg");
        Mat Book3 = Highgui.imread("resources/books/book3.jpg");
        //ImagePair3DProcess.Quick3DPairProcess(Book2, Book1, showEpipole, showMatch, showStitch, displayWindow,"book2v1","results/book");//Broken 1 is blurry
        //ImagePair3DProcess.Quick3DPairProcess(Book3, Book2, showEpipole, showMatch, showStitch, displayWindow,"book3v2","results/book");
        //ImagePair3DProcess.Quick3DPairProcess(Book3, Book1, showEpipole, showMatch, showStitch, displayWindow,"book3v1","results/book");//Broken 1 is blurry

        //Point Cloud Wrapper test
        System.loadLibrary("PCL_Wrapper");
        program1_test();

        /*//Quick 3D Image Pair processing for joe the bmw
        File joeFolder = new File("training_subset/bmwbayside/4jgda7db8da163624/img");
        TrainingSet joe = new TrainingSet("bmwbayside", "4jgda7db8da163624", joeFolder);
        for(int i=1;i<joe.train_num;i++) {

            Quick3DPairStruct inputParams = new Quick3DPairStruct();

            inputParams.imgA = joe.train_list.get(i);
            inputParams.imgB = joe.train_list.get(i-1);
            inputParams.showEpipole=true;
            inputParams.showMatch=false;
            inputParams.showStitch=false;
            inputParams.displayWindow=true;
            inputParams.pairNames = new String("" + i + " vs " + (i - 1));
            inputParams.outDir="results/joe";

            FundHomoMat fundHomoMat = ImagePair3DProcess.Quick3DPairProcess(inputParams);
            System.out.println("fundamental " + inputParams.pairNames + "\n" + fundHomoMat.fundamentaAB.dump());
            System.out.println();
        }*/
    }

    static void program1_test()
    {
        PointCloud_PointXYZ cloud =new PointCloud_PointXYZ();
        //passing points
        cloud.push_back(new PointXYZ((float)0.025, (float)0.060, (float)0.070));
        cloud.push_back(new PointXYZ((float)0.026, (float)0.060, (float)0.070));
        cloud.push_back(new PointXYZ((float)0.027, (float)0.060, (float)0.070));
        cloud.push_back(new PointXYZ((float)0.028, (float)0.060, (float)0.070));
        // Create the normal estimation class, and pass the input dataset to it
        NormalEstimation_PointXYZ_Normal ne=new NormalEstimation_PointXYZ_Normal();
        ne.setInputCloud(cloud);
        KdTree_PointXYZ tree=new KdTree_PointXYZ(0, true);
        ne.setSearchMethod(tree);
        PointCloud_Normal cloud_normals=new PointCloud_Normal();
        ne.setRadiusSearch(0.03);
        ne.compute(cloud_normals);
        System.out.println("Normal Cloud Size:"+cloud_normals.size());
        for (int i = 0; i < cloud_normals.size(); i++) {
            Normal N=cloud_normals.at(i);
            System.out.print("Point ");
            System.out.println(i);
            System.out.println(N.getNormal_x());
            System.out.println(N.getNormal_y());
            System.out.println(N.getNormal_z());
        }
    }

}
