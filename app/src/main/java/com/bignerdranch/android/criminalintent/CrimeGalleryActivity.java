package com.bignerdranch.android.criminalintent;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridView;

public class CrimeGalleryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_gallery);
        Intent intent = getIntent();
        String suri = intent.getStringExtra(CrimeFragment.EXTRA_CRIME_ID);
        GridView mGalleryGrid = (GridView) findViewById(R.id.gallery_grid);
        mGalleryGrid.setAdapter(new ImageAdapter(this, suri));
    }
}
