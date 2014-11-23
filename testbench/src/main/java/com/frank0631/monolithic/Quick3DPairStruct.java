package com.frank0631.monolithic;

import org.opencv.core.Mat;

/**
 * Created by frank0631 on 10/21/14.
 */
public class Quick3DPairStruct {

    Mat imgA;
    Mat imgB;
    boolean showEpipole;
    boolean showMatch;
    boolean showStitch;
    boolean displayWindow;
    String pairNames;
    String outDir;

    public Quick3DPairStruct(){
        imgA = new Mat();
        imgB = new Mat();
        showEpipole=false;
        showMatch=false;

        showStitch=false;
        displayWindow=false;
        pairNames="";
        outDir="";
    }
}
