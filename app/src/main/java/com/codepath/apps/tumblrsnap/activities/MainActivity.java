package com.codepath.apps.tumblrsnap.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.codepath.apps.tumblrsnap.R;
import com.codepath.apps.tumblrsnap.TumblrSnapApp;
import com.codepath.apps.tumblrsnap.fragments.LoginFragment;
import com.codepath.apps.tumblrsnap.fragments.LoginFragment.OnLoginHandler;
import com.codepath.apps.tumblrsnap.fragments.PhotosFragment;
import com.codepath.apps.tumblrsnap.models.User;

import java.io.File;
import java.io.IOException;

public class MainActivity extends FragmentActivity implements OnLoginHandler {

    // PICK_PHOTO_CODE is a constant integer
    public final static int PICK_PHOTO_CODE = 1046;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateFragments();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

  /*      MenuItem settings = menu.findItem(R.id.action_settings);
        if (User.currentUser() == null) {
            settings.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        } else {
            settings.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }*/

        return true;
    }

    public void onSettingsButton(MenuItem menuItem) {
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
    }
    public void onLogoutButton(View view) {
        TumblrSnapApp.getClient().clearAccessToken();
        User.setCurrentUser(null);
        onBackPressed();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_use_existing:{
                onPickPhoto(item.getActionView());
                break;
            }
            case R.id.action_take_photo:
            {
                onLaunchCamera(item.getActionView());
                break;
            }
            case R.id.action_signout:
            {
                onLogoutButton(item.getActionView());
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLogin() {
        updateFragments();
    }

    private void updateFragments() {
        if (User.currentUser() == null) {
            showLoginFragment();
        } else {
            showPhotosFragment();
        }
        invalidateOptionsMenu();
    }

    private void showPhotosFragment() {
        showFragment(PhotosFragment.class);
    }

    private void showLoginFragment() {
        showFragment(LoginFragment.class);
    }

    @SuppressWarnings("rawtypes")
    private void showFragment(Class activeFragmentClass) {
        Class[] fragmentClasses = new Class[] { LoginFragment.class,
                PhotosFragment.class };
        FragmentManager mgr = getSupportFragmentManager();
        FragmentTransaction transaction = mgr.beginTransaction();
        try {
            for (Class klass : fragmentClasses) {
                Fragment fragment = mgr.findFragmentByTag(klass.getName());
                if (klass == activeFragmentClass) {
                    if (fragment != null) {
                        transaction.show(fragment);
                    } else {
                        transaction.add(R.id.frmContent, (Fragment) klass.newInstance(), klass.getName());
                    }
                } else {
                    if (fragment != null) {
                        transaction.hide(fragment);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        transaction.commit();
    }

    // Trigger gallery selection for a photo
    public void onPickPhoto(View view) {
        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(intent, PICK_PHOTO_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Uri takenPhotoUri = getPhotoFileUri(photoFileName);
                // by this point we have the camera photo on disk
                Intent intent=new Intent(MainActivity.this,PreviewPhotoActivity.class);
                intent.putExtra("photo_bitmap", takenPhotoUri);
                startActivity(intent);
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == PICK_PHOTO_CODE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    try{
                        Uri photoUri = data.getData();
                        // Do something with the photo based on Uri
                        Bitmap selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
                        if (selectedImage!=null){
                            Intent intent=new Intent(MainActivity.this,PreviewPhotoActivity.class);
                            intent.putExtra("photo_bitmap", photoUri);
                            startActivity(intent);
                        }
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }

    }

    /////////////////////////////////////
    public final String APP_TAG = "MyCustomApp";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public String photoFileName = "photo.jpg";
    //////////////////////////////////////////
    public void onLaunchCamera(View view) {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getPhotoFileUri(photoFileName)); // set the image file name

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }
    // Returns the Uri for a photo stored on disk given the fileName
    public Uri getPhotoFileUri(String fileName) {
        // Only continue if the SD Card is mounted
        if (isExternalStorageAvailable()) {
            // Get safe storage directory for photos
            // Use `getExternalFilesDir` on Context to access package-specific directories.
            // This way, we don't need to request external read/write runtime permissions.
            File mediaStorageDir = new File(
                    getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
                Log.d(APP_TAG, "failed to create directory");
            }

            // Return the file target for the photo based on filename
            return Uri.fromFile(new File(mediaStorageDir.getPath() + File.separator + fileName));
        }
        return null;
    }
    private boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }
}
