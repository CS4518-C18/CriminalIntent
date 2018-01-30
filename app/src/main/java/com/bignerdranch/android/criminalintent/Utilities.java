package com.bignerdranch.android.criminalintent;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.util.Pair;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import jp.wasabeef.picasso.transformations.CropSquareTransformation;

/**
 * @author Yang Liu
 * @version Jan 29, 2018
 */

public class Utilities {
    private static File getProfileDir(Context context, String crimeID) {
        File externalFilesDir = context
                .getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if (externalFilesDir == null) {
            return null;
        }

        File profileDir = new File(externalFilesDir, crimeID);

        if(!profileDir.exists())
            profileDir.mkdirs();

        return profileDir;
    }

    static File createNewProfile(Context context, Crime crime) {
        File profileDir = getProfileDir(context, crime.getId().toString());

        if(profileDir == null) return null;

        File[] profiles = profileDir.listFiles();
        int num_profiles = 0;
        if (profiles != null) num_profiles = profileDir.listFiles().length;

        String profileFilename = String.format(Locale.getDefault(), "%d.jpg", num_profiles);
        File profile = new File(profileDir, profileFilename);
        return profile;
    }

    static List<String> getProfileUris(Context context, String crimeID) {

        File profileDir = getProfileDir(context, crimeID);

        if(profileDir == null) return null;

        File[] profiles = profileDir.listFiles();

        int num_profiles = 0;
        if (profiles != null) num_profiles = profileDir.listFiles().length;

        String profileDirName = profileDir.getAbsolutePath();
        LinkedList<String> profileNames = new LinkedList<>();
        for (int i = 0; i < num_profiles; i++) {
            String filename = String.format(Locale.getDefault(), "%s/%d.jpg", profileDirName, i);
            Log.d("getProfileUris", filename);
            profileNames.add(filename);
        }
       return profileNames;
    }

    static void setImage(Context context, Uri imageUri, ImageView imageView) {
        Log.d("setImage", imageUri.toString());
        Picasso.with(context)
                .load(imageUri)
                .transform(new CropSquareTransformation())
                .fit()
                .into(imageView);
    }
}
