package com.android.bluetoothmusic.utility;

import android.content.Context;

public class DimensionUtils {

    public static int convertDensityPixel(Context context, int digit) {
        return digit * context.getResources().getDisplayMetrics().densityDpi / 160;
    }

}
