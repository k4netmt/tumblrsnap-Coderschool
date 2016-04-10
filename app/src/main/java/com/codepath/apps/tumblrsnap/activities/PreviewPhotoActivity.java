package com.codepath.apps.tumblrsnap.activities;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.codepath.apps.tumblrsnap.ImageFilterProcessor;
import com.codepath.apps.tumblrsnap.R;
import com.codepath.apps.tumblrsnap.TumblrClient;
import com.codepath.apps.tumblrsnap.adapters.PreviewAdapter;
import com.codepath.apps.tumblrsnap.interfaces.SpacesItemDecoration;
import com.codepath.apps.tumblrsnap.models.User;
import com.codepath.libraries.androidviewhelpers.SimpleProgressDialog;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PreviewPhotoActivity extends FragmentActivity {
	private Bitmap photoBitmap;
	private Bitmap processedBitmap;
	private SimpleProgressDialog dialog;
	private ImageFilterProcessor filterProcessor;
	private PreviewAdapter previewAdapter;
	private ArrayList<Bitmap> bitmaps;
	/////////
	@Bind(R.id.ivPreview)ImageView ivPreview;
	@Bind(R.id.lvPhotos)RecyclerView rvPhotos;
	/////////
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preview_photo);
		ButterKnife.bind(this);
		ivPreview = (ImageView) findViewById(R.id.ivPreview);
		Uri photoUri = (Uri)getIntent().getParcelableExtra("photo_bitmap");
		try {
			photoBitmap= MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(photoBitmap!=null){
			filterProcessor = new ImageFilterProcessor(photoBitmap);
			bitmaps=new ArrayList<Bitmap>();
			bitmaps.add(getPreview(ImageFilterProcessor.NONE));
			bitmaps.add(getPreview(ImageFilterProcessor.GRAYSCALE));
			bitmaps.add(getPreview(ImageFilterProcessor.BLUR));
			bitmaps.add(getPreview(ImageFilterProcessor.CRYSTALLIZE));
			bitmaps.add(getPreview(ImageFilterProcessor.CONTRAST));
			bitmaps.add(getPreview(ImageFilterProcessor.FRACTAL));
			bitmaps.add(getPreview(ImageFilterProcessor.GLOW));
			bitmaps.add(getPreview(ImageFilterProcessor.MARBLE));
			bitmaps.add(getPreview(ImageFilterProcessor.PINCH));
			bitmaps.add(getPreview(ImageFilterProcessor.SOLARIZE));
			bitmaps.add(getPreview(ImageFilterProcessor.TRITONE));
			bitmaps.add(getPreview(ImageFilterProcessor.WARP));
			previewAdapter=new PreviewAdapter(bitmaps,ivPreview);
			rvPhotos.setHasFixedSize(true);
			rvPhotos.setAdapter(previewAdapter);
			LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
			rvPhotos.setLayoutManager(layoutManager);
			SpacesItemDecoration decoration = new SpacesItemDecoration(4);
			rvPhotos.addItemDecoration(decoration);

			redisplayPreview(ImageFilterProcessor.NONE);
		}
	}

	private Bitmap getPreview(int effectId) {
		Bitmap bitmap = filterProcessor.applyFilter(effectId);
		return bitmap;
	}

	private void redisplayPreview(int effectId) {
        processedBitmap = filterProcessor.applyFilter(effectId);
        ivPreview.setImageBitmap(processedBitmap);
	}


/*	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.preview_photo, menu);
		return true;
	}*/

/*	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == R.id.more || itemId == R.id.action_save)
			return true;
		
		int effectId = 0;
		
		switch (itemId) {
		case R.id.filter_none:
			effectId = ImageFilterProcessor.NONE;
			break;
		case R.id.filter_blur:
			effectId = ImageFilterProcessor.BLUR;
			break;
		case R.id.filter_grayscale:
			effectId = ImageFilterProcessor.GRAYSCALE;
			break;
		case R.id.filter_crystallize:
			effectId = ImageFilterProcessor.CRYSTALLIZE;
			break;
		case R.id.filter_solarize:
			effectId = ImageFilterProcessor.SOLARIZE;
			break;
		case R.id.filter_glow:
			effectId = ImageFilterProcessor.GLOW;
			break;
		default:
			effectId = ImageFilterProcessor.NONE;
			break;
		}
		redisplayPreview(effectId);
		return true;
	}*/

	public void onSaveButton(MenuItem menuItem) {
		dialog = SimpleProgressDialog.build(this);
		dialog.show();
		
		TumblrClient client = ((TumblrClient) TumblrClient.getInstance(TumblrClient.class, this));
		client.createPhotoPost(User.currentUser().getBlogHostname(), processedBitmap, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int arg0, String arg1) {
				dialog.dismiss();
				PreviewPhotoActivity.this.finish();
			}

			@Override
			public void onFailure(Throwable arg0, String arg1) {
				dialog.dismiss();
			}
		});
	}
}
