package com.agribot.colorrecognition;

import android.graphics.Color;

public class NamedColor {

	 private int color;
     private String name;

     public String getName() {
             return name;
     }

     public int getInteger() {
             return color;
     }

     public NamedColor(int color, Classifier mClassifier) {
             this.color = color;
             if ( mClassifier != null )
                     this.name = mClassifier.classify(color);
             else
                     this.name = "UNKNOWN";
     }

     public String toString() {
             return color + " " + asRGB() + " " + asHSV() + " " + name;
     }

     public String asRGB() {
             return "R\t" + Color.red(color) + "\nG\t" + Color.green(color)
                             + "\nB\t" + Color.blue(color);
     }

     /*------------------------------ Code Added here*/
     
     
     public int asRed() {
         return Color.red(color);
 }
     
     public int asGreen() {
    	 return Color.green(color);
 }
     
     public int asBlue() {
    	 return Color.blue(color);
 }
     
     
     
     /*-------------------------------- Code completed here*/
     
     public String asHSV() {
             float[] hsv = new float[3];
             Color.colorToHSV(color, hsv);
             return "HUE\t" + hsv[0] + "\nSAT\t" + hsv[1] + "\nVAL\t" + hsv[2];
     }
     
     private String int2xx(int a){
             String s = Integer.toHexString(a);
             if ( s.length() < 2 ) s = "0" + s;
             return s;
     }
     
     public String asHTML() {
             return "#" + int2xx( Color.red(color) ) + int2xx( Color.green(color) ) + int2xx( Color.blue(color) );
     }

}
