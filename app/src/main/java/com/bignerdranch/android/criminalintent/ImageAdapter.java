package com.bignerdranch.android.criminalintent;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.bignerdranch.android.criminalintent.Utilities.setImage;

/**
 * @author Harry Liu
 * @version Jan 28, 2018
 */

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private List<String> imagePaths;

    public ImageAdapter(Context c, List<String> paths) {
        mContext = c;
        imagePaths = paths;
    }

    public int getCount() {
        return imagePaths.size();
    }

    public String getItem(int position) {
        return imagePaths.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new SquareImageView (mContext);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        } else {
            imageView = (ImageView) convertView;
        }

        File imageFile = new File(getItem(position));
        setImage(mContext, Uri.fromFile(imageFile), imageView);
        return imageView;
    }
}
