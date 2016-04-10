package com.codepath.apps.tumblrsnap.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.codepath.apps.tumblrsnap.R;

import java.util.ArrayList;

/**
 * Created by Kanet on 4/9/2016.
 */
public class PreviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public final ArrayList<Bitmap> mBitmaps;
    public ImageView ivPhotoParent;
    public PreviewAdapter(ArrayList<Bitmap> bitmaps,ImageView imageView) {
        super();
        mBitmaps=bitmaps;
        ivPhotoParent=imageView;
    }
    @Override
    public int getItemCount() {
        return mBitmaps.size();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Bitmap bitmap=mBitmaps.get(position);
        if (bitmap!=null){
            ViewHolderBitmap vhBitmap=(ViewHolderBitmap)holder;
            vhBitmap.ivPhoto.setImageBitmap(bitmap);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v1 = inflater.inflate(R.layout.preview_photo_item, parent, false);
        viewHolder = new ViewHolderBitmap(v1);
        return viewHolder;
    }

    public class ViewHolderBitmap extends RecyclerView.ViewHolder implements View.OnClickListener{
        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ImageView ivPhoto;
        public ViewHolderBitmap(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            ivPhoto = (ImageView) itemView.findViewById(R.id.ivPhoto);
            ivPhoto.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            ivPhotoParent.setImageBitmap(mBitmaps.get(getAdapterPosition()));
            Log.d("DEBUG","ok");
        }

    }
}
