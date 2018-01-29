package com.bignerdranch.android.criminalintent;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.io.File;

/**
 * Created by haofanzhang on 1/26/18.
 */

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private File[] images = null;

    public ImageAdapter(Context c, String s) {
        mContext = c;
        Uri uri = Uri.parse(s);
        try {
            images = new File(uri.getPath()).listFiles();
        } catch (NullPointerException e) {
            images = null;
        }
    }

    public int getCount() {
        if (images != null) {
            return images.length;
        } else {
            return 0;
        }
    }

    public Object getItem(int position) {
        if (images != null) {
            return images[position];
        } else {
            return 0;
        }
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
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setPadding(1, 1, 1, 1);

        } else {
            imageView = (ImageView) convertView;
        }

        if(images != null && images[position].exists()){
            imageView.setImageBitmap(ImageLab.getThumbnail(images[position]));
        }

        return imageView;
    }
}
