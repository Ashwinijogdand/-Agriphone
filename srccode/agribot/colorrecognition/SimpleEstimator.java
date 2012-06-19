package com.agribot.colorrecognition;

import android.graphics.Color;
import android.util.Log;

public class SimpleEstimator implements ValueEstimator{

	public int getAverageColor(int[] data, int width, int height) {
		if ( data == null || data.length == 0 ) {
            Log.w(AgribotActivity.TAG, "SimpleEstimator: no data provided");
            return Color.MAGENTA;
    }
    return data[ data.length / 2 ];

	}

}
