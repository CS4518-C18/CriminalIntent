package com.bignerdranch.android.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

public class CrimeGalleryActivity extends AppCompatActivity {
    private String crimeID;
    private GridView mGalleryGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_gallery);
        Intent intent = getIntent();
        crimeID = intent.getStringExtra(CrimeFragment.EXTRA_CRIME_ID);
        mGalleryGrid = (GridView) findViewById(R.id.gallery_grid);
        updateGallery();
    }

    private void updateGallery() {
        /*
        mGalleryGrid.setAdapter(new ImageAdapter(this));
        mGalleryGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(getApplicationContext(), "" + position,
                        Toast.LENGTH_SHORT).show();
            }
        });
        */
        System.out.println(crimeID);
    }
}
