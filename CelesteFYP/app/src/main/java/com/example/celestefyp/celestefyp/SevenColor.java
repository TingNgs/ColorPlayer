package com.example.celestefyp.celestefyp;

import android.graphics.Color;

/**
 * Created by NGS on 17/04/2018.
 */

public class SevenColor {
    public int getSevenColor(int pixelColor){
        double tempD = getDistance(pixelColor,255,0,0);
        int tempColor = 1;
        if(getDistance(pixelColor,255,165,0)<tempD){
            tempD = getDistance(pixelColor,255,165,0);
            tempColor=2;
        }
        if(getDistance(pixelColor,255,255,0)<tempD){
            tempD = getDistance(pixelColor,255,255,0);
            tempColor=3;
        }
        if(getDistance(pixelColor,0,255,0)<tempD){
            tempD = getDistance(pixelColor,0,255,0);
            tempColor=4;
        }
        if(getDistance(pixelColor,0,255,255)<tempD){
            tempD = getDistance(pixelColor,0,255,255);
            tempColor=5;
        }
        if(getDistance(pixelColor,0, 0,255)<tempD){
            tempD = getDistance(pixelColor,0, 0,255);
            tempColor=6;
        }
        if(getDistance(pixelColor,43, 0,255)<tempD){
            tempD = getDistance(pixelColor,43, 0,255);
            tempColor=7;
        }
        if(getDistance(pixelColor,87, 0,255)<tempD){
            tempD = getDistance(pixelColor,87, 0,255);
            tempColor=8;
        }
        return tempColor;
    }
    public int getColorValue(int tempColor){
        int color=0;
        if(tempColor==1) color = Color.parseColor("#FF0000");
        if(tempColor==2) color = Color.parseColor("#FFA500");
        if(tempColor==3) color = Color.parseColor("#FFFF00");
        if(tempColor==4) color = Color.parseColor("#008000");
        if(tempColor==5) color = Color.parseColor("#00FFFF");
        if(tempColor==6) color = Color.parseColor("#0000FF");
        if(tempColor==7) color = Color.parseColor("#4B0082");
        if(tempColor==8) color = Color.parseColor("#800880");
        return color;
    }

    public String getColorName(int tempColor){
        if(tempColor==1) return "Red";
        if(tempColor==2) return "Orange";
        if(tempColor==3) return "Yellow";
        if(tempColor==4) return "Green";
        if(tempColor==5) return "Blue";
        if(tempColor==6) return "Six";
        if(tempColor==7) return "Seven";
        if(tempColor==8) return "Eight";
        return "Error";
    }

    private double getDistance(int pixelColor,int r,int g,int b){
        double rd = (Color.red(pixelColor)-r)*(Color.red(pixelColor)-r);
        double gd = (Color.green(pixelColor)-g)*(Color.green(pixelColor)-g);
        double bd = (Color.blue(pixelColor)-b)*(Color.blue(pixelColor)-b);
        return Math.sqrt((rd+gd+bd));
    }
}
