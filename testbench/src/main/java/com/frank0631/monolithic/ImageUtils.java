package com.frank0631.monolithic;

import org.opencv.core.*;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.opencv.core.Core.line;

/**
 * Created by frank0631 on 10/17/14.
 */
public class ImageUtils {

    public static void showResult(Mat img, String windowName) {
        Imgproc.resize(img, img, new Size(640, 480));
        MatOfByte matOfByte = new MatOfByte();
        Highgui.imencode(".jpg", img, matOfByte);
        byte[] byteArray = matOfByte.toArray();
        BufferedImage bufImage = null;
        try {
            InputStream in = new ByteArrayInputStream(byteArray);
            bufImage = ImageIO.read(in);
            JFrame frame = new JFrame(windowName);
            frame.getContentPane().add(new JLabel(new ImageIcon(bufImage)));
            frame.pack();
            frame.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void vector(Mat img, double a, double b, double c, Scalar color)
    {
        Point pt1 = new Point(0,c);
        Point pt2 = new Point(img.width(), a*img.width()+c);
        line(img, pt1, pt2, color);
        return;
    }

    public static Mat unitCamMatrix(){
        double[][] camDouble = {{1, 0, 1}, {0, 1, 1}, {0, 0, 1}};
        Mat camMat = Mat.zeros(3,3, CvType.CV_64F);
        for(int i=0;i<3;i++)
            for(int j=0;j<3;j++)
                camMat.put(i,j,camDouble[i][j]);
        return camMat;
    }




}
