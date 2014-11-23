package com.frank0631.monolithic;

import org.opencv.features2d.DMatch;
import java.util.Comparator;

/**
 * Created by frank on 10/18/14.
 */
public class MatchDistanceComparator implements Comparator<DMatch>{

    public int compare(DMatch o1, DMatch o2) {
        if(o1.distance<o2.distance)
            return -1;
        if(o1.distance>o2.distance)
            return 1;
        return 0;
    }
}
