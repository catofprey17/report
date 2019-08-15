package com.example.report.reporter;

import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.hardware.usb.UsbInterface;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import com.example.report.entities.Draft;
import com.googlecode.tesseract.android.TessPdfRenderer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class ReportIO {

    private static final String TAG = ReportIO.class.getSimpleName();
    public static final String TESSDATA_SUB = "tessdata";
    public static final String TESSDATA_FILE = TESSDATA_SUB + "/eng.traineddata";




    public static void copyTessdataToInternalStorage(Context context) throws IOException {

        File directory = new File(context.getFilesDir().getAbsolutePath() + "/" + TESSDATA_SUB);
        if (!directory.exists()) {
            directory.mkdir();
            Log.d(TAG, TESSDATA_SUB + " directory created");
        } else {Log.d(TAG, TESSDATA_SUB + " directory already exists");}

        directory = new File(context.getFilesDir().getAbsolutePath() + "/" + TESSDATA_FILE);


        if (!directory.exists()) {
            InputStream inputStream = context.getAssets().open(TESSDATA_FILE);
            OutputStream outputStream = new FileOutputStream(context.getFilesDir().getAbsolutePath() + "/" + TESSDATA_FILE);

            byte[] buf = new byte[1024];
            int len;

            while ((len = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, len);
            }
            inputStream.close();
            outputStream.close();
            Log.d(TAG, "TESSDATA files copied");
        } else {Log.d(TAG, "TESSDATA already copied");}
    }

    // TODO Remove method
    public static List<Bitmap> getBitmapList(Context context, Intent data) throws IOException {

        // Get Bitmaps from Intent data
        ClipData clipData = data.getClipData();
        ContentResolver contentResolver = context.getContentResolver();
        List<Uri> uriList= new ArrayList<Uri>();
        List<Bitmap> bitmapList = new ArrayList<>();

        if (clipData == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                bitmapList.add(ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, data.getData())));
            } else {
                bitmapList.add(MediaStore.Images.Media.getBitmap(contentResolver, data.getData()));
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    ClipData.Item item = clipData.getItemAt(i);
                    bitmapList.add(ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, item.getUri())));
                }

            } else {
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    ClipData.Item item = clipData.getItemAt(i);
                    bitmapList.add(MediaStore.Images.Media.getBitmap(contentResolver, item.getUri()));
                }
            }
        }

        //Rewrite bitmaps to ARGB_8888 config to accept with tesseract
        List<Bitmap> result = new ArrayList<>();
        for (int i = 0; i< bitmapList.size(); i++) {
            result.add(bitmapList.get(i).copy(Bitmap.Config.ARGB_8888,true));
        }

        return result;
    }

    public static List<Uri> getUriList(Intent data) {

        ClipData clipData = data.getClipData();
        List<Uri> uriList= new ArrayList<Uri>();
        if (clipData == null) {
            uriList.add(data.getData());
        } else {
            for (int i = 0; i < clipData.getItemCount(); i++) {
                ClipData.Item item = clipData.getItemAt(i);
                uriList.add(item.getUri());
            }
        }
        return uriList;
    }

    public static Bitmap getBitmapFromUri(Context context, Uri uri) throws IOException {

        Bitmap bitmap;
        ContentResolver contentResolver= context.getContentResolver();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, uri));
        } else {
            bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri);
        }

        //Rewrite bitmaps to ARGB_8888 config to accept with tesseract
        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888,true);

        return bitmap;
    }

    // TODO Complete method
    public static Boolean createReport(List<Draft> drafts) {
        return null;
    }
}
