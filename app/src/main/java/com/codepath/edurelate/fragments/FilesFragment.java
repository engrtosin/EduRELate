package com.codepath.edurelate.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.edurelate.R;
import com.codepath.edurelate.activities.GroupDetailsActivity;
import com.codepath.edurelate.databinding.FragmentFilesBinding;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FilesFragment extends Fragment {

    public static final String TAG = "FilesFragment";

    FragmentFilesBinding binding;
    View rootView;
    FilesListener mListener;
    GroupDetailsActivity pActivity;
    File newPdf;

    public interface FilesListener {
        void intentToFiles();
        void pdfResult(Uri pdfUri);
    }

    public void setListener(FilesListener listener) {
        mListener = listener;
    }

    public FilesFragment() {
        // Required empty public constructor
    }

    public static FilesFragment newInstance() {
        FilesFragment fragment = new FilesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pActivity = (GroupDetailsActivity) getActivity();
        setPDFTransferListener();
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFilesBinding.inflate(inflater,container,false);
        rootView = binding.getRoot();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setClickListeners();
    }

    private void setClickListeners() {
        binding.tvNewFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.intentToFiles();
            }
        });
    }

    private void setPDFTransferListener() {
        pActivity.setPDFTransferInterface(new FilesListener() {
            @Override
            public void intentToFiles() {

            }

            @Override
            public void pdfResult(Uri pdfUri) {
                Log.i(TAG,"Receiving file from activity");
                String uriString = pdfUri.toString();
                newPdf = new File(uriString);
            }
        });
    }

    private ParseObject uploadPDFToParse(File PDFFile, ParseObject po, String columnName){
        if (PDFFile != null){
            Log.d("EB", "PDFFile is not NULL: " + PDFFile.toString());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            BufferedInputStream in = null;
            try {
                in = new BufferedInputStream(new FileInputStream(PDFFile));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            int read;
            byte[] buff = new byte[1024];
            try {
                while ((read = in.read(buff)) > 0)
                {
                    out.write(buff, 0, read);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            byte[] pdfBytes = out.toByteArray();

            // Create the ParseFile
            ParseFile file = new ParseFile(PDFFile.getName() , pdfBytes);
            po.put(columnName, file);

            // Upload the file into Parse Cloud
            file.saveInBackground();
            po.saveInBackground();
        }
        return po;
    }

    void generateImageFromPdf(Uri pdfUri) {
        int pageNumber = 0;
        PdfiumCore pdfiumCore = new PdfiumCore(getContext());
        try {
            ParcelFileDescriptor fd = getActivity().getContentResolver().openFileDescriptor(pdfUri, "r");
            PdfDocument pdfDocument = pdfiumCore.newDocument(fd);
            pdfiumCore.openPage(pdfDocument, pageNumber);
            int width = pdfiumCore.getPageWidthPoint(pdfDocument, pageNumber);
            int height = pdfiumCore.getPageHeightPoint(pdfDocument, pageNumber);
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            pdfiumCore.renderPageBitmap(pdfDocument, bmp, pageNumber, 0, 0, width, height);
            saveImage(bmp);
            pdfiumCore.closeDocument(pdfDocument); // important!
        } catch(Exception e) {
            Log.e(TAG,"Error while getting image");
        }
    }

    public final static String FOLDER = Environment.getExternalStorageDirectory() + "/PDF";
    private void saveImage(Bitmap bmp) {
        FileOutputStream out = null;
        try {
            File folder = new File(FOLDER);
            if(!folder.exists())
                folder.mkdirs();
            File file = new File(folder, "PDF.png");
            out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
        } catch (Exception e) {
            //todo with exception
        } finally {
            try {
                if (out != null)
                    out.close();
            } catch (Exception e) {
                //todo with exception
            }
        }
    }
}