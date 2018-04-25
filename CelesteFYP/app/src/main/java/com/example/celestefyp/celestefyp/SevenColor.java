package com.example.celestefyp.celestefyp;

import android.graphics.Color;

/**
 * Created by NGS on 17/04/2018.
 */

public class SevenColor {
    public int getSevenColor(int pixelColor){
        int tempColor = 2;
        double tempD = getDistance(pixelColor,255,243,140);
        if(getDistance(pixelColor,254,196,195)<tempD){
            if(Color.red(pixelColor)>Color.blue(pixelColor) && Color.red(pixelColor)>Color.blue(pixelColor) && Math.abs(Color.red(pixelColor)-Color.green(pixelColor))>15){
                tempD = getDistance(pixelColor,254,196,195);
                tempColor=1;
            }
        }

        if(getDistance(pixelColor,172,225,240)<tempD){
            if(Color.red(pixelColor)<Color.green(pixelColor) && Color.red(pixelColor)<Color.blue(pixelColor)) {
                tempD = getDistance(pixelColor, 172, 225, 240);
                tempColor = 3;
            }
        }
        if(getDistance(pixelColor,205,176,254)<tempD){
            if(Color.red(pixelColor)>Color.green(pixelColor) && Color.blue(pixelColor)>Color.green(pixelColor)) {
                tempD = getDistance(pixelColor, 205, 176, 254);
                tempColor = 4;
            }
        }
        if(getDistance(pixelColor,255,181,155)<tempD){
            if(Color.red(pixelColor)>Color.green(pixelColor) && Color.red(pixelColor)>Color.blue(pixelColor) &&  Math.abs(Color.red(pixelColor)-Color.green(pixelColor))>15&&  Math.abs(Color.blue(pixelColor)-Color.green(pixelColor))<30) {
                tempD = getDistance(pixelColor, 255, 181, 155);
                tempColor = 5;
            }
        }
        if(getDistance(pixelColor,108, 222,163)<tempD){
            if(Color.green(pixelColor)>Color.red(pixelColor) && Color.green(pixelColor)>Color.blue(pixelColor)) {
                tempD = getDistance(pixelColor, 108, 222, 163);
                tempColor = 6;
            }
        }
        if(getDistance(pixelColor,177, 177,177)<tempD){
            if( Math.abs(Color.red(pixelColor)-Color.blue(pixelColor))<10 && Math.abs(Color.red(pixelColor)-Color.green(pixelColor))<10&& Math.abs(Color.blue(pixelColor)-Color.green(pixelColor))<10){
                //tempD = getDistance(pixelColor,177, 177,177);
                tempColor=7;
            }
        }
        return tempColor;
    }
    public int getColorValue(int tempColor){
        int color=0;
        if(tempColor==1) color = Color.parseColor("#fec4c3");
        if(tempColor==2) color = Color.parseColor("#fff38c");
        if(tempColor==3) color = Color.parseColor("#ace1f0");
        if(tempColor==4) color = Color.parseColor("#cdb0fe");
        if(tempColor==5) color = Color.parseColor("#ffb59b");
        if(tempColor==6) color = Color.parseColor("#6cdea3");
        if(tempColor==7) color = Color.parseColor("#b1b1b1");
        return color;
    }

    public String getColorName(int tempColor){
        if(tempColor==1) return "R";
        if(tempColor==2) return "Y";
        if(tempColor==3) return "B";
        if(tempColor==4) return "P";
        if(tempColor==5) return "O";
        if(tempColor==6) return "G";
        if(tempColor==7) return "S";
        return "Error";
    }
    public int getColorFre(int tempColor){
        if(tempColor==1) return 1750;
        if(tempColor==2) return 2980;
        if(tempColor==3) return 3140;
        if(tempColor==4) return 1170;
        if(tempColor==5) return 2640;
        if(tempColor==6) return 1400;
        if(tempColor==7) return 1560;
        return 0;
    }
    private double getDistance(int pixelColor,int r,int g,int b){
        double rd = (Color.red(pixelColor)-r)*(Color.red(pixelColor)-r);
        double gd = (Color.green(pixelColor)-g)*(Color.green(pixelColor)-g);
        double bd = (Color.blue(pixelColor)-b)*(Color.blue(pixelColor)-b);
        return (rd+gd+bd);
    }

    private boolean inRangeOf(int pixelColor,double or,double ob,double og){
        double r = Color.red(pixelColor);
        double g = Color.red(pixelColor);
        double b = Color.red(pixelColor);
        if(inpresOf((r-g)/g,(or-og)/og)){
            if(inpresOf((r-b)/b,(or-ob)/ob)){
                if(inpresOf((g-b)/b,(og-ob)/ob)){
                    return true;
                }
            }
        }
        return false;
    }
    private boolean inpresOf(double x,double y){
        if(Math.abs((Math.abs(x) -Math.abs(y)))*100 < 30) return true;
        else return false;
    }
}
