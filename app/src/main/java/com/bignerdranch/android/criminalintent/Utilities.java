package com.bignerdranch.android.criminalintent;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.util.SparseArray;
import android.widget.ImageView;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;


/**
 * @author Yang Liu
 * @author Haofan Zhang
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

        if (!profileDir.exists())
            profileDir.mkdirs();

        return profileDir;
    }

    static File createNewProfile(Context context, Crime crime) {
        File profileDir = getProfileDir(context, crime.getId().toString());

        if (profileDir == null) return null;

        File[] profiles = profileDir.listFiles();
        int num_profiles = 0;
        if (profiles != null) num_profiles = profileDir.listFiles().length;

        String profileFilename = String.format(Locale.getDefault(), "%d.jpg", num_profiles);
        File profile = new File(profileDir, profileFilename);
        return profile;
    }

    static List<String> getProfileUris(Context context, String crimeID) {

        File profileDir = getProfileDir(context, crimeID);

        if (profileDir == null) return null;

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

    static Bitmap scaleDown(Bitmap image) {
        Bitmap newImage = scaleDown(image, 500, 500);
        return newImage;
    }

    static Bitmap scaleDown(Bitmap image, int width, int height) {
        int newWidth = Math.min(image.getWidth(), image.getHeight());
        image = Bitmap.createBitmap(image, 0, 0, newWidth, newWidth);
        return Bitmap.createScaledBitmap(image, width, height, false);
    }

    static void drawFaces(Bitmap image, SparseArray<Face> faces) {
        Canvas canvas = new Canvas(image);

        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);

        float left;
        float top;
        float right;
        float bottom;

        for (int i = 0; i < faces.size(); i++) {
            Face face = faces.valueAt(i);

            left = face.getPosition().x;
            top = face.getPosition().y;
            right = left + face.getWidth();
            bottom = top + face.getHeight();

            canvas.drawRect(left, top, right, bottom, paint);
        }
    }

    static Bitmap detectFaces(FaceDetector mFaceDetector, Bitmap image) {
        Frame frame = new Frame.Builder().setBitmap(image).build();
        SparseArray<Face> faces = mFaceDetector.detect(frame);

        Bitmap newImage = image.copy(Bitmap.Config.ARGB_8888, true);
        drawFaces(newImage, faces);
        return newImage;
    }

    static FaceDetector getFaceDetector(Context context) {
        return new FaceDetector.Builder(context)
                .setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.FAST_MODE)
                .setMode(FaceDetector.FAST_MODE)
                .build();
    }

    static void updateEnableFaceDetectionPreference(Context context, boolean faceDetectionEnabled) {
        SharedPreferences preferences = context
                .getSharedPreferences(context.getString(R.string.crime_preferences_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(context.getString(R.string.enable_face_detection_key), faceDetectionEnabled);
        editor.apply();
    }
}
