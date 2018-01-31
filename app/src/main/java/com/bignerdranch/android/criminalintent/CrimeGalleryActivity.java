package com.bignerdranch.android.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.GridView;

import com.google.android.gms.vision.face.FaceDetector;

import java.util.LinkedList;
import java.util.List;

import static com.bignerdranch.android.criminalintent.Utilities.detectFaces;
import static com.bignerdranch.android.criminalintent.Utilities.getFaceDetector;
import static com.bignerdranch.android.criminalintent.Utilities.getProfileUris;
import static com.bignerdranch.android.criminalintent.Utilities.scaleDown;

/**
 * @author Harry Liu
 * @author Haofan Zhang
 * @version Jan 29, 2018
 */
public class CrimeGalleryActivity extends AppCompatActivity {
    private GridView mGalleryGrid;

    private Context mContext;
    private ImageAdapter imageAdapter;
    private FaceDetector mFaceDetector;

    private class UpdateProfileTask extends AsyncTask<Object, Void, List<Bitmap>> {
        @Override
        protected List<Bitmap> doInBackground(Object... values) {

            List<String> profileUris = (List<String> ) values[0];
            boolean faceDetectionEnabled = (boolean) values[1];
            FaceDetector faceDetector = (FaceDetector) values[2];
            List<Bitmap> profiles = (List<Bitmap>) values[3];

            for (String profileUri: profileUris) {

                Bitmap image = BitmapFactory.decodeFile(profileUri);

                Bitmap newImage = scaleDown(image);

                if (faceDetectionEnabled)
                    newImage = detectFaces(faceDetector, newImage);

                newImage = scaleDown(newImage);
                profiles.add(newImage);

                publishProgress();
            }

            return profiles;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            imageAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_gallery);

        Intent intent = getIntent();
        String crimeID = intent.getStringExtra(CrimeFragment.EXTRA_CRIME_ID);
        mGalleryGrid = (GridView) findViewById(R.id.gallery_grid);

        final boolean faceDetectionEnabled = intent.getBooleanExtra(CrimeFragment.EXTRA_FACE_DETECTION_ENABLED, false);

        final List<String> profileUris = getProfileUris(this, crimeID);

        mFaceDetector = getFaceDetector(this);
        mContext = this;

        final List<Bitmap> profiles = new LinkedList<>();

        imageAdapter = new ImageAdapter(mContext, profiles);
        mGalleryGrid.setAdapter(imageAdapter);

        new UpdateProfileTask().execute(profileUris, faceDetectionEnabled, mFaceDetector, profiles);
    }

    @Override
    public void onStop(){
        super.onStop();
        mFaceDetector.release();
    }

    @Override
    public void onStart(){
        super.onStart();
        if (!mFaceDetector.isOperational()) {mFaceDetector = getFaceDetector(this);}
    }
}
