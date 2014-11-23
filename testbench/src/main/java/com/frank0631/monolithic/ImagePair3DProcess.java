package com.frank0631.monolithic;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.*;
import org.opencv.features2d.*;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by frank on 10/19/14.
 */
public class ImagePair3DProcess {

    private Mat imgA;
    private Mat imgB;
    private Mat grayImgA;
    private Mat grayImgB;
    private Mat epipoleAB;
    private Mat stitchedAB;
    private Mat matchedImage;
    private int matchLimit;
    private Mat fundamentaAB;
    private Mat homographyAB;

    public ImagePair3DProcess(Mat imgA, Mat imgB, Mat grayImgA, Mat grayImgB, int matchLimit) {
        this.imgA = imgA;
        this.imgB = imgB;
        this.grayImgA = grayImgA;
        this.grayImgB = grayImgB;
        this.matchLimit = matchLimit;
    }

    public Mat getEpipoleAB() {
        return epipoleAB;
    }

    public Mat getStitchedAB() {
        return stitchedAB;
    }

    public Mat getMatchedImage() {
        return matchedImage;
    }

    public Mat getFundamentaAB() {
        return fundamentaAB;
    }

    public Mat getHomographyAB() {
        return homographyAB;
    }

    public static FundHomoMat Quick3DPairProcess(Quick3DPairStruct inputParams) {
        return Quick3DPairProcess(inputParams.imgA, inputParams.imgB, inputParams.showEpipole, inputParams.showMatch,
                inputParams.showStitch,inputParams.displayWindow, inputParams.pairNames, inputParams.outDir);
    }

    public static FundHomoMat Quick3DPairProcess(Mat imgA, Mat imgB, boolean showEpipole, boolean showMatch, boolean showStitch, boolean displayWindow, String pairNames,String outDir) {

        Mat grayImgA=new Mat();
        Mat grayImgB=new Mat();
        Imgproc.cvtColor(imgA, grayImgA, Imgproc.COLOR_RGB2GRAY);
        Imgproc.cvtColor(imgB, grayImgB, Imgproc.COLOR_RGB2GRAY);

        ImagePair3DProcess imagePair3DProcess = new ImagePair3DProcess(imgA, imgB, grayImgA, grayImgB, 1000);
        imagePair3DProcess.ProcessPair();
        Mat epipoleAB = imagePair3DProcess.getEpipoleAB();
        Mat stitchedAB = imagePair3DProcess.getStitchedAB();
        Mat matchedImage = imagePair3DProcess.getMatchedImage();
        FundHomoMat fhMat = new FundHomoMat();
        fhMat.fundamentaAB = imagePair3DProcess.getFundamentaAB();
        fhMat.homographyAB = imagePair3DProcess.getHomographyAB();


        File outFolder = new File(outDir);
        if(!outFolder.exists() || outFolder.isFile())
            outFolder.mkdirs();

        if(showEpipole) {
            if(displayWindow) ImageUtils.showResult(epipoleAB, "epipoleAB " + pairNames);
            Highgui.imwrite(outDir + File.separator + "epipole" + pairNames + ".jpg", epipoleAB);
        }
        if(showStitch){
            if(displayWindow) ImageUtils.showResult(stitchedAB,"stitchedAB "+pairNames);
            Highgui.imwrite(outDir+File.separator+"stitched"+pairNames+".jpg",stitchedAB);
        }
        if(showMatch){
            if(displayWindow) ImageUtils.showResult(matchedImage,"matchedImage "+pairNames);
            Highgui.imwrite(outDir+File.separator+"matched"+pairNames+".jpg",matchedImage);
        }


        return fhMat;
    }

    public void ProcessPair() {
        FeatureDetector detector = FeatureDetector.create(FeatureDetector.FAST);
        DescriptorExtractor extractor = DescriptorExtractor.create(DescriptorExtractor.ORB);
        MatOfKeyPoint keypointsA = new MatOfKeyPoint();
        MatOfKeyPoint keypointsB = new MatOfKeyPoint();
        detector.detect(grayImgA, keypointsA);
        detector.detect(grayImgB, keypointsB);
        Mat descriptersA = new Mat(grayImgA.rows(), grayImgA.cols(), grayImgA.type());
        Mat descriptersB = new Mat(grayImgB.rows(), grayImgB.cols(), grayImgB.type());
        extractor.compute(grayImgA, keypointsA, descriptersA);
        extractor.compute(grayImgB, keypointsB, descriptersB);

        MatOfDMatch matchs = new MatOfDMatch();
        DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
        matcher.match(descriptersA, descriptersB, matchs);
        List<DMatch> matchesList = matchs.toList();

        Collections.sort(matchesList, new MatchDistanceComparator());

        if (matchLimit > 0)
            matchesList = matchesList.subList(0, Math.min(matchLimit, matchesList.size()));

        MatOfDMatch matchsSorted = new MatOfDMatch();
        matchsSorted.fromList(matchesList);

        ArrayList<Point> AimgPoints = new ArrayList<Point>();
        ArrayList<Point> BimgPoints = new ArrayList<Point>();
        for (final DMatch matchpoint : matchesList) {
            AimgPoints.add(keypointsA.toList().get(matchpoint.queryIdx).pt);
            BimgPoints.add(keypointsB.toList().get(matchpoint.trainIdx).pt);
        }
        MatOfPoint2f AmatofPoints = new MatOfPoint2f();
        MatOfPoint2f BmatofPoints = new MatOfPoint2f();
        AmatofPoints.fromList(AimgPoints);
        BmatofPoints.fromList(BimgPoints);

        fundamentaAB = Calib3d.findFundamentalMat(AmatofPoints, BmatofPoints, Calib3d.RANSAC, 5, 0.99);
        homographyAB = Calib3d.findHomography(BmatofPoints, AmatofPoints, Calib3d.RANSAC, 5);

        Mat epilinesA = new Mat();
        Mat epilinesB = new Mat();
        Mat linesImgA = imgA.clone();
        Mat linesImgB = imgB.clone();

        Calib3d.computeCorrespondEpilines(BmatofPoints, 2, getFundamentaAB(), epilinesA);
        Calib3d.computeCorrespondEpilines(AmatofPoints, 1, getFundamentaAB(), epilinesB);

        for (int r = 0; r < epilinesA.rows(); r++) {
            double ax = epilinesA.get(r, 0)[0];
            double by = epilinesA.get(r, 0)[1];
            double c = epilinesA.get(r, 0)[2];
            //System.out.println("ax: "+ax+" by: "+by+" c: "+c);
            ImageUtils.vector(linesImgA, ax, by, c, new Scalar(255, 100, 100));
        }
        for (int r = 0; r < epilinesB.rows(); r++) {
            double ax = -epilinesB.get(r, 0)[0];
            double by = -epilinesB.get(r, 0)[1];
            double c = -epilinesB.get(r, 0)[2];
            //System.out.println("ax: "+ax+" by: "+by+" c: "+c);
            ImageUtils.vector(linesImgB, ax, by, c, new Scalar(100, 255, 100));
        }

        //System.out.println(linesAB.get(0,0)[0]+","+linesAB.get(0,0)[1]+","+linesAB.get(0,0)[2]);
        //System.out.println("linesA: "+epilinesA.toString());
        //System.out.println("linesB: "+epilinesB.toString());
        //System.out.println("fundamentaAB: " + getFundamentaAB().toString());

        int biggestHeight = Math.max(grayImgA.height(), grayImgB.height());
        int concatWidth = grayImgA.width() + grayImgB.width();
        Size warpedSize = new Size(concatWidth, biggestHeight);

        epipoleAB = new Mat(warpedSize, imgA.type());
        Mat epipoleHalfA = new Mat(epipoleAB, new Rect(0, 0, imgA.width(), imgA.height()));
        Mat epipoleHalfB = new Mat(epipoleAB, new Rect(imgA.width(), 0, imgB.width(), imgB.height()));
        linesImgA.copyTo(epipoleHalfA);
        linesImgB.copyTo(epipoleHalfB);

        Mat warpedB = new Mat();
        Imgproc.warpPerspective(imgB, warpedB, getHomographyAB(), warpedSize);

        stitchedAB = new Mat(warpedSize, imgA.type());
        Mat stitchedHalfA = new Mat(stitchedAB, new Rect(0, 0, imgA.width(), imgA.height()));
        Mat stitchedHalfB = new Mat(stitchedAB, new Rect(0, 0, warpedB.width(), warpedB.height()));
        warpedB.copyTo(stitchedHalfB);
        imgA.copyTo(stitchedHalfA);

        matchedImage = new Mat(concatWidth, biggestHeight, imgA.type());
        Features2d.drawMatches(imgA, keypointsA, imgB, keypointsB, matchsSorted, matchedImage);
        return;
    }

}
