package com.bignerdranch.android.criminalintent;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import java.util.List;

/**
 * @author Harry Liu
 * @author Haofan Zhang
 * @version Jan 28, 2018
 */

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private List<Bitmap> images;

    public ImageAdapter(Context c, List<Bitmap> images) {
        mContext = c;
        this.images = images;
    }

    public int getCount() {
        return images.size();
    }

    public Bitmap getItem(int position) {
        return images.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new SquareImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageBitmap(images.get(position));
        return imageView;
    }
}
