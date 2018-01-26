package com.bignerdranch.android.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class CrimeGalleryActivity extends AppCompatActivity {
    private String uri;
    private GridView mGalleryGrid;
    private ImageView im;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_gallery);
        Intent intent = getIntent();
        uri = intent.getStringExtra(CrimeFragment.EXTRA_CRIME_ID);
        mGalleryGrid = (GridView) findViewById(R.id.gallery_grid);
        im = (ImageView) findViewById(R.id.img_1);
        updateGallery();
    }

    private void updateGallery() {

        mGalleryGrid.setAdapter(new ImageAdapter(this));
        mGalleryGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(getApplicationContext(), "" + position,
                        Toast.LENGTH_SHORT).show();
            }
        });
        Uri.parse(uri);
        File img = new File(Uri.parse(uri).getPath());
        if(img.exists()){

            Bitmap myBitmap = BitmapFactory.decodeFile(img.getAbsolutePath());
            im.setImageBitmap(myBitmap);

        }
    }
}
