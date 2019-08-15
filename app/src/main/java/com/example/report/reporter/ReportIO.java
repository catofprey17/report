package com.example.report.reporter;

import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.hardware.usb.UsbInterface;
import android.icu.util.LocaleData;
import android.icu.util.TaiwanCalendar;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import com.example.report.entities.Draft;
import com.google.android.material.circularreveal.CircularRevealHelper;
import com.googlecode.tesseract.android.TessPdfRenderer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ReportIO {

    private static final String TAG = ReportIO.class.getSimpleName();
    public static final String TESSDATA_SUB = "tessdata";
    public static final String TESSDATA_FILE = TESSDATA_SUB + "/eng.traineddata";
    public static final String TEMPORARY_SUBFOLDER = "/temp";


    public static void copyTessdataToInternalStorage(Context context) throws IOException {

        File directory = new File(context.getFilesDir().getAbsolutePath() + "/" + TESSDATA_SUB);
        if (!directory.exists()) {
            boolean flag = directory.mkdir();
            Log.d(TAG, context.getFilesDir().getAbsolutePath() + "/" + TESSDATA_SUB + " directory created" + flag);
        } else {
            Log.d(TAG, context.getFilesDir().getAbsolutePath() + "/" + TESSDATA_SUB + " directory already exists");
        }

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
        } else {
            Log.d(TAG, "TESSDATA already copied");
        }
    }

    public static List<Uri> getUriList(Intent data) {

        ClipData clipData = data.getClipData();
        List<Uri> uriList = new ArrayList<Uri>();
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
        ContentResolver contentResolver = context.getContentResolver();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, uri));
        } else {
            bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri);
        }

        //Rewrite bitmaps to ARGB_8888 config to accept with tesseract
        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

        return bitmap;
    }

    // TODO Complete method
    public static Boolean createReport(Context context, List<Draft> drafts) throws IOException {
        String hz = fillTemporaryFolder(context, drafts);
        return null;
    }

    private static String fillTemporaryFolder(Context context, List<Draft> drafts) throws IOException {

        // Get count of drafts for each number
        LinkedHashMap<String, Integer> numbers = new LinkedHashMap<>();
        for (int i = 0; i < drafts.size(); i++) {
            String num = drafts.get(i).getNumber();
            if (numbers.get(num) == null) {
                numbers.put(num, 0);
            } else {
                int temp = numbers.get(num);
                numbers.replace(num, ++temp);
            }
        }

        // Create a temporary directory if it's not exist
        String path = context.getFilesDir().getAbsolutePath() + TEMPORARY_SUBFOLDER;
        updateFolder(path);

        // Create a daily directory if it's not exist
        Date currentDate = new Date();
        DateFormat dateFormat = new SimpleDateFormat("ddMMyyyy", Locale.getDefault());
        path = path + "/" + dateFormat.format(currentDate);
        createFolder(path);


        // Create a temporary directory for drafts and daily folder if its not exist


        String numberSubPath;
        for (int i = 0; i < numbers.size(); i++) {
            String key = new ArrayList<>(numbers.keySet()).get(i);
            List<Integer> l = new ArrayList<>(numbers.values());
            String value = String.valueOf(l.get(i));
            numberSubPath = path + "/" + key + "(" + value + ")";
            createFolder(numberSubPath);

            int counter = 1;
            for (int j = 0; j < drafts.size(); j++) {
                if (drafts.get(j).getNumber().equals(key)) {
                    String tempPath = numberSubPath + "/" + key + "(" + counter + ").jpg";
                    writeFileFromUri(context, drafts.get(j).getUri(), tempPath);
                    Log.d(TAG, tempPath + " draft written");
                    counter++;
                }
            }

        }


        return null;
    }


    private static void writeFileFromUri(Context context, Uri uri, String destination) {
        String source = uri.getPath();

        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;

        try {
            bis = new BufferedInputStream(new FileInputStream(context.getContentResolver().openFileDescriptor(uri, "r").getFileDescriptor()));
            bos = new BufferedOutputStream(new FileOutputStream(destination,false));
            byte[] buf = new byte[1024];
            bis.read(buf);
            do {
                bos.write(buf);
            } while (bis.read(buf) != -1);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bis != null) bis.close();
                if (bos != null) bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static boolean createFolder(String path) {
        File directory = new File(path);
        boolean flag = true;
        if (directory.exists()) {
            Log.d(TAG, "Directory " + path + " already exists!");
        } else {
            flag = directory.mkdir();
            if (flag)
                Log.d(TAG, "Directory " + path + " created successful!");
            else
                Log.d(TAG, "Directory " + path + " creation failed!");
        }
        return flag;
    }

    private static boolean updateFolder(String path) {
        File directory = new File(path);
        boolean flag;
        if (directory.exists()) {
            cleanFolder(directory);
            flag = directory.mkdir();
            if (flag)
                Log.d(TAG, "Directory " + path + " updated successful!");
            else
                Log.d(TAG, "Directory " + path + " update failed!");
        } else {
            flag = directory.mkdir();
            if (flag)
                Log.d(TAG, "Directory " + path + " created successful!");
            else
                Log.d(TAG, "Directory " + path + " creation failed!");
        }
        return flag;
    }

    private static void cleanFolder(File directory) {
        if (directory.isDirectory())
            for (File child : directory.listFiles())
                cleanFolder(child);

        directory.delete();
    }
}