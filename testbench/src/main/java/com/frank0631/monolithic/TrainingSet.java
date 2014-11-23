package com.frank0631.monolithic;

import com.davekoelle.alphanum.AlphanumComparator;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by frank0631 on 10/17/14.
 */

public class TrainingSet {

    //Broken, need training dir folder tree to reorganize

    public ArrayList<Mat> train_list;
    public int train_num;

    public TrainingSet(){
        train_list = new ArrayList<Mat>();
    }

    public TrainingSet(File imgFolder){
        File train_folder = new File(imgFolder,"train");
        this.train_list = loadFromDir(train_folder);
        this.train_num = this.train_list.size();
    }

    public ArrayList<Mat> loadFromDir(String dir) {
        File folder = new File(dir);
        return loadFromDir(folder);
    }

    public ArrayList<Mat> loadFromDir(File folder){

        ArrayList<Mat> imageList = new ArrayList<Mat>();
        if (folder.isFile())return null;

        File[] listedFiles = folder.listFiles();
        String[] listedFileNames = new String[listedFiles.length];
        for (int i = 0; i < listedFiles.length; ++i)
            listedFileNames[i] = listedFiles[i].getAbsolutePath();

        Comparator<String> comp = new AlphanumComparator();
        Arrays.sort(listedFileNames, comp);

        //for (final String filename : listedFileNames)
        //System.out.println(filename);

        for (final String filename : listedFileNames) {
            File fileEntry = new File(filename);
            if (fileEntry.isDirectory()) {
                loadFromDir(fileEntry);
            } else if (fileEntry.isFile()){
                //fileEntry.getAbsoluteFile()
                try {
                    //System.out.println(filename);
                    Mat img = Highgui.imread(filename);
                    if (img!=null)
                        imageList.add(img);
                }
                catch (Exception ex) {
                    //ex.printStackTrace();
                }
            }
        }
        return imageList;
    }

    public static ArrayList<TrainingSet> traverseTrainingDir(String dir){
        File folder = new File(dir);
        return  traverseTrainingDir(folder);
    }

    public static ArrayList<TrainingSet> traverseTrainingDir(File trainingFolder){
        ArrayList<TrainingSet> trainingSetArrayList = new ArrayList<TrainingSet>();

        File[] listedUsernameFolders = trainingFolder.listFiles();

            for (final File subjectFolderName : listedUsernameFolders) {
                System.out.println("Subject: " + subjectFolderName.getName());

                File imgFolder = new File(subjectFolderName,"img");
                if(!imgFolder.isDirectory()) continue;

                TrainingSet tmpTrainingSet = new TrainingSet(imgFolder);
                trainingSetArrayList.add(tmpTrainingSet);
            }
        return trainingSetArrayList;
    }
}
