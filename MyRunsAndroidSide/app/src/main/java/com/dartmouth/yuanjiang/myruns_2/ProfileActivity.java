package com.dartmouth.yuanjiang.myruns_2;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.os.Bundle;
import android.os.Environment;

import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ProfileActivity extends Activity {

    public static final int REQUEST_CODE_TAKE_FROM_CAMERA = 0;
    public static final int REQUEST_CODE_CROP_PHOTO = 2;
    public static final int REQUEST_CODE_PICK_FROM_ALBUM = 1;

    private static final String IMAGE_UNSPECIFIED = "image/*";
    private static final String URI_INSTANCE_STATE_KEY = "saved_uri";
    private static final String TAG = "Lab5";

    private ImageView mImageView;
    private Uri mImageCaptureUri;
    private boolean isTakenFromCamera;
    private static final int ID_PHOTO_TAKE_FROM_CAMERA = 0;
    private static final int ID_PHOTO_PICK_FROM_ALBUM = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mImageView = (ImageView) findViewById(R.id.imageProfile);

        if(savedInstanceState != null){
            mImageCaptureUri = savedInstanceState.getParcelable(URI_INSTANCE_STATE_KEY);
        }

        loadUserData();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(URI_INSTANCE_STATE_KEY, mImageCaptureUri);
    }

    //**************************  button click callbacks  **************************//

    public void onChangePhotoClicked(View v){

        displayDialog(MyDialogFragment.PROFILE_DIALOG_ID_PHOTO);
    }

    public void onSaveClicked(View v) {

        saveUserData();

        Toast.makeText(getApplicationContext(),
                getString(R.string.toast_save_text), Toast.LENGTH_SHORT).show();

        finish();
    }

    public void onCancelClicked(View v) {

        Toast.makeText(getApplicationContext(),
                getString(R.string.toast_cancel_text), Toast.LENGTH_SHORT).show();

        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode != RESULT_OK)
            return;

        switch(requestCode){
            case REQUEST_CODE_TAKE_FROM_CAMERA:
                //Send image taken from camera for cropping
                cropImage();
                break;

            case REQUEST_CODE_PICK_FROM_ALBUM:
                //Send image taken from album for cropping
                mImageCaptureUri = data.getData();
                cropImage();
                break;

            case REQUEST_CODE_CROP_PHOTO:
                //Update image view after image crop
                Bundle extras = data.getExtras();
                //Set the picture image in UI
                if(extras != null){
                    mImageView.setImageBitmap((Bitmap) extras.getParcelable("data"));
                }
                if(isTakenFromCamera){
                    File f = new File(mImageCaptureUri.getPath());
                    if(f.exists())
                        f.delete();
                }
                break;
        }
    }

    //**************************  private helper functions  **************************//

    public void displayDialog(int id){
        MyDialogFragment newFragment = MyDialogFragment.newInstance(id);
        newFragment.show(getFragmentManager(), getString(R.string.dialog_fragment_tag));
    }

    private void loadSnap() {

        // Load profile photo from internal storage
        try{
            FileInputStream fis = openFileInput(getString(R.string.profile_photo_file));
            Bitmap bmap = BitmapFactory.decodeStream(fis);
            mImageView.setImageBitmap(bmap);
            fis.close();
        }catch(IOException e){
            // Default profile photo if no photo saved before
            mImageView.setImageResource(R.drawable.default_profile);
        }

    }

    public void onPhotoItemSelected(int item){
        switch (item){
            case ID_PHOTO_TAKE_FROM_CAMERA:

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                //Construct temporary temporary image path and name to save the taken photo
                mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
                        "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg"));
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                intent.putExtra("return-data", true);

                try{
                    // Start a camera capturing activity
                    // REQUEST_CODE_TAKE_FROM_CAMERA is an integer tag you
                    // defined to identify the activity in onActivityResult()
                    // when it returns
                    startActivityForResult(intent, REQUEST_CODE_TAKE_FROM_CAMERA);
                }catch (ActivityNotFoundException e){
                    e.printStackTrace();
                }
                isTakenFromCamera = true;
                break;

            case ID_PHOTO_PICK_FROM_ALBUM:
                Intent intentFromAlbum = new Intent((String)null, null);
                intentFromAlbum.setType("image/*");
                intentFromAlbum.setAction(Intent.ACTION_PICK);
                startActivityForResult(intentFromAlbum,REQUEST_CODE_PICK_FROM_ALBUM);
                break;

            default:
                return;
        }
    }

    private void saveSnap() {

        //commit all the changes into preference file
        //save profile image into internal storage.
        mImageView.buildDrawingCache();
        Bitmap bmap = mImageView.getDrawingCache();
        try{
            FileOutputStream fos = openFileOutput(getString(R.string.profile_photo_file), MODE_PRIVATE);
            bmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch(IOException ioe){
            ioe.printStackTrace();
        }
        Toast.makeText(getApplicationContext(), "saved photo", Toast.LENGTH_SHORT).show();

    }

    private void cropImage(){
        //Use existing crop activity
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(mImageCaptureUri, IMAGE_UNSPECIFIED);

        //specify image size
        intent.putExtra("outputX", 100);
        intent.putExtra("outputY", 100);

        //Specify aspect ratio, 1:1
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", true);

        //identify the activity in onActivityResult() when it returns
        startActivityForResult(intent, REQUEST_CODE_CROP_PHOTO);
    }

    //load the user data from shared preferences if there is no data make sure
    // that we set it to something reasonable
    private void loadUserData(){
        Log.d(TAG,"loadUserData()");

        loadSnap();

        // Get the shared preferences: create or retrieve the activity preference object
        String mKey = getString(R.string.preference_name);
        SharedPreferences mPrefs = getSharedPreferences(mKey, MODE_PRIVATE);

        //Load the name
        mKey = getString(R.string.preference_key_profile_name);
        String mValue = mPrefs.getString(mKey,"");
        ((EditText)findViewById(R.id.name_info)).setText(mValue);

        //Load the User Email
        mKey = getString(R.string.preference_key_profile_email);
        mValue = mPrefs.getString(mKey,"");
        ((EditText)findViewById(R.id.email_info)).setText(mValue);

        //Load phone
        mKey = getString(R.string.preference_key_profile_phone);
        mValue = mPrefs.getString(mKey,"");
        ((EditText)findViewById(R.id.phone_info)).setText(mValue);

        //Load gender info and radio box
        mKey = getString(R.string.preference_key_profile_gender);
        int mIntValue = mPrefs.getInt(mKey, -1);
        // In case there isn't one saved before
        if(mIntValue >= 0){
            //Find the radio button that should be checked
            RadioButton radioBtn = (RadioButton)((RadioGroup)findViewById(R.id.radioGender)).getChildAt(mIntValue);
            //check the button
            radioBtn.setChecked(true);
            Toast.makeText(getApplicationContext(),"number of the radioButton is: " + mIntValue, Toast.LENGTH_SHORT).show();
        }

        //Load the class
        mKey = getString(R.string.preference_key_profile_class);
        mValue = mPrefs.getString(mKey,"");
        ((EditText)findViewById(R.id.class_info)).setText(mValue);

        //Load the Major
        mKey = getString(R.string.preference_key_profile_major);
        mValue = mPrefs.getString(mKey,"");
        ((EditText)findViewById(R.id.major_info)).setText(mValue);
    }

    //save the user data to the shared preferences
    private void saveUserData(){
        Log.d(TAG, "saveUserData()");

        saveSnap();

        //getting the shared preferences editor
        String mKey = getString(R.string.preference_name);
        SharedPreferences mPrefs = getSharedPreferences(mKey, MODE_PRIVATE);

        SharedPreferences.Editor mEditor = mPrefs.edit();
        mEditor.clear();

        //save name info
        mKey = getString(R.string.preference_key_profile_name);
        String mValue = (String) ((EditText)findViewById(R.id.name_info)).getText().toString();
        mEditor.putString(mKey, mValue);

        //save email info
        mKey = getString(R.string.preference_key_profile_email);
        mValue = (String) ((EditText)findViewById(R.id.email_info)).getText().toString();
        mEditor.putString(mKey, mValue);

        //save phone
        mKey = getString(R.string.preference_key_profile_phone);
        mValue = (String) ((EditText)findViewById(R.id.phone_info)).getText().toString();
        mEditor.putString(mKey, mValue);

        // Read which index the radio is checked
        mKey = getString(R.string.preference_key_profile_gender);

        RadioGroup mRadioGroup = (RadioGroup) findViewById(R.id.radioGender);
        int mIntValue = mRadioGroup.indexOfChild(findViewById(mRadioGroup.getCheckedRadioButtonId()));
        mEditor.putInt(mKey, mIntValue);

        // save class
        mKey = getString(R.string.preference_key_profile_class);
        mValue = (String) ((EditText)findViewById(R.id.class_info)).getText().toString();
        mEditor.putString(mKey, mValue);

        //save major
        mKey = getString(R.string.preference_key_profile_major);
        mValue = (String) ((EditText)findViewById(R.id.major_info)).getText().toString();
        mEditor.putString(mKey, mValue);

        // Commit all the changes into the shared preference
        mEditor.commit();

        Toast.makeText(getApplicationContext(), "saved text", Toast.LENGTH_SHORT).show();
    }

}

