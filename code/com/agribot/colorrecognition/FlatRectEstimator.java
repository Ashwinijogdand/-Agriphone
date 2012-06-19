package com.agribot.colorrecognition;

import android.graphics.Color;

public class FlatRectEstimator implements ValueEstimator {

    final int SIZE = 8;

    public int getAverageColor(int[] data, int width, int height) {
            int mx = width / 2;
            int my = height / 2;

            int cnt = 0;
            int r = 0, g = 0, b = 0;

            try {
                    for (int x = mx - SIZE; x < mx + SIZE; x++) {
                            for (int y = my - SIZE; y < my + SIZE; y++) {
                                    r += Color.red(data[y * width + x]);
                                    g += Color.green(data[y * width + x]);
                                    b += Color.blue(data[y * width + x]);
                                    cnt++;
                            }
                    }
            } catch (Exception e) {
                    return Color.MAGENTA;
            }
            return Color.rgb(r / cnt, g / cnt, b / cnt);
    }

}
