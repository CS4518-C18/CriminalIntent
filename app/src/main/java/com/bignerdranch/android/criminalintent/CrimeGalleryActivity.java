package com.bignerdranch.android.criminalintent;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridView;

import java.util.List;

import static com.bignerdranch.android.criminalintent.Utilities.getProfileUris;

public class CrimeGalleryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_gallery);

        Intent intent = getIntent();
        String crimeID = intent.getStringExtra(CrimeFragment.EXTRA_CRIME_ID);
        GridView mGalleryGrid = (GridView) findViewById(R.id.gallery_grid);

        List<String> profileUris = getProfileUris(this, crimeID);

        ImageAdapter imageAdapter = new ImageAdapter(this, profileUris);
        mGalleryGrid.setAdapter(imageAdapter);
    }

}
