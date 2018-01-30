package com.bignerdranch.android.criminalintent;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.Locale;

/**
 * @author Yang Liu
 * @version Jan 29, 2018
 */

public class Utilities {
    static File createNewProfile(Context context, Crime crime) {
        File externalFilesDir = context
                .getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if (externalFilesDir == null) {
            return null;
        }

        File crimeDir = new File(externalFilesDir, crime.getId().toString());

        Log.d(Utilities.class.getSimpleName(), crimeDir.exists() + "------");

        if(!crimeDir.exists())
            crimeDir.mkdirs();

        File[] profiles = crimeDir.listFiles();
        int num_files = 0;
        if (profiles != null) num_files = crimeDir.listFiles().length;
        String filename = String.format(Locale.getDefault(), "%d.jpg", num_files);
        Log.d(Utilities.class.getSimpleName(), new File(crimeDir, filename).getAbsolutePath());
        return new File(crimeDir, filename);
    }
}
