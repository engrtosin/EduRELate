package com.codepath.edurelate.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;

import com.codepath.edurelate.BitmapScaler;
import com.codepath.edurelate.databinding.FragmentNewGroupBinding;
import com.codepath.edurelate.databinding.FragmentNewPicBinding;
import com.codepath.edurelate.models.Group;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class NewPicDialogFragment extends DialogFragment {

    public static final String TAG = "NewGroupDialogFragment";
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    private static final int TAKEN_PHOTO_WIDTH = 100;
    private static final int PICK_PHOTO_CODE = 100;
    public static final String PHOTO_FILE_NAME = "new_photo.jpg";

    FragmentNewPicBinding binding;
    protected File photoFile;
    Uri fileProvider;
    public ParseFile parsePhotoFile;
    public byte[] byteArray;
    NewPicInterface mListener;

    /* ---------------------- interface ---------------------- */

    public interface NewPicInterface {
        void picSaved(ParseFile parseFile);
    }

    public NewPicDialogFragment() {
    }

    public static NewPicDialogFragment newInstance(String title) {
        NewPicDialogFragment frag = new NewPicDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNewPicBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        photoFile = getPhotoFileUri(PHOTO_FILE_NAME);
        fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider.Edurelate", photoFile);
        mListener = (NewPicInterface) getActivity();
        setOnClickListeners();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    protected void setOnClickListeners() {
        binding.ivCameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCamera();
            }
        });
        binding.ivDocBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPickPhoto();
            }
        });
        binding.ivDoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (photoFile == null || binding.ivNewPic.getDrawable() == null) {
                    Toast.makeText(getContext(), "Image cannot be empty.", Toast.LENGTH_SHORT).show();
                    return;
                }
                parsePhotoFile = saveImageAsParseFile();
                Log.i(TAG,"pic saved");
                mListener.picSaved(parsePhotoFile);
                dismiss();
            }
        });
    }

    public void onPickPhoto() {
        Log.i(TAG,"on pick photo");
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
        startActivityForResult(intent, PICK_PHOTO_CODE);
        Log.i(TAG,"Intent to media gallery");
//        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
    }

    protected void launchCamera() {
        Log.i(TAG,"Camera about to be launched in launchCamera");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    public File getPhotoFileUri(String fileName) {
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }

    public Bitmap loadFromUri(Uri photoUri) {
        Bitmap image = null;
        try {
            // check version of Android on device
            if(Build.VERSION.SDK_INT > 27){
                // on newer versions of Android, use the new decodeBitmap method
                ImageDecoder.Source source = ImageDecoder.createSource(getActivity().getContentResolver(), photoUri);
                image = ImageDecoder.decodeBitmap(source);
            } else {
                // support older versions of Android by using getBitmap
                image = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), photoUri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                try {
                    resizeBitmap();
                } catch (IOException e) {
                    Log.e(TAG,"Resizing taken photo failed: + " + e, e);
                }
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                takenImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byteArray = stream.toByteArray();
                binding.ivNewPic.setImageBitmap(takenImage);
            } else {
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
        if ((data != null) && requestCode == PICK_PHOTO_CODE) {
            Uri photoUri = data.getData();

            String realPath = getRealPathFromUri(getContext(),photoUri);
            File localFile = new File(realPath);
            boolean canRead = localFile.canRead();
            Bitmap selectedImage = loadFromUri(photoUri);
            String uriToString = photoUri.toString();
            File urllocalFile = new File(uriToString);
            canRead = urllocalFile.canRead();

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            selectedImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byteArray = stream.toByteArray();

            binding.ivNewPic.setImageBitmap(selectedImage);
        }
    }

    public static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    protected void resizeBitmap() throws IOException {
        Uri takenPhotoUri = Uri.fromFile(getPhotoFileUri(PHOTO_FILE_NAME));
        Bitmap rawTakenImage = BitmapFactory.decodeFile(takenPhotoUri.getPath());
        Bitmap resizedBitmap = BitmapScaler.scaleToFitWidth(rawTakenImage, TAKEN_PHOTO_WIDTH);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
        File resizedFile = getPhotoFileUri(PHOTO_FILE_NAME + "_resized");
        resizedFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(resizedFile);
        fos.write(bytes.toByteArray());
        fos.close();
    }

    private ParseFile saveImageAsParseFile() {
        return new ParseFile(byteArray);
    }
}