package com.jati.dev.androidalarmmanager.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by jati on 01/06/18
 */

public class CommonUtils {
    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }
}
