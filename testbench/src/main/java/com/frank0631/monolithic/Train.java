package com.frank0631.monolithic;

import java.util.ArrayList;

public class Train {

    public static void main(String[] args){

        nu.pattern.OpenCV.loadShared();
        System.loadLibrary("opencv_java249");

        ArrayList<TrainingSet> trainingSet=null;

        //TODO Make training set serializable
        trainingSet = TrainingSet.traverseTrainingDir("training_subset");

       /* for (final TrainingSet currentCar : trainingSet)
            for(int i=1;i<currentCar.train_num;i++) {

                Quick3DPairStruct inputParams = new Quick3DPairStruct();

                inputParams.imgA = currentCar.train_list.get(i);
                inputParams.imgB = currentCar.train_list.get(i-1);
                inputParams.showEpipole=true;
                inputParams.showMatch=false;
                inputParams.showStitch=false;
                inputParams.displayWindow=false;
                inputParams.pairNames = new String("" + i + " vs " + (i - 1));
                inputParams.outDir="results/trainingExterior/"+currentCar.username+"_"+currentCar.subdir;

                FundHomoMat fundHomoMat = ImagePair3DProcess.Quick3DPairProcess(inputParams);
            }*/

    }

}

