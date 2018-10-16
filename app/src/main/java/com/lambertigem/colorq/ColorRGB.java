package com.lambertigem.colorq;

public class ColorRGB {

        public double R, G, B, relativePercentage;
        int x,y;

        public ColorRGB(double inRed, double inGreen, double inBlue, int x1, int y1, double relativePercent)
        {
            R = inRed;
            G = inGreen;
            B = inBlue;
            x = x1;
            y = y1;
            relativePercentage = relativePercent;
        }

}
