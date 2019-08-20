package com.example.report.reporter;

import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import com.example.report.entities.Draft;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ReportIO {

    private static final String TAG = ReportIO.class.getSimpleName();
    private static final String TESSDATA_SUB = "tessdata";
    private static final String TESSDATA_FILE = TESSDATA_SUB + "/eng.traineddata";
    private static final String TEMPORARY_SUBFOLDER = "/temp";
    private static final int BUFFER_SIZE = 2048;



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
    public static void createReport(Context context, List<Draft> drafts) {
        fillTemporaryFolder(context, drafts);
    }

    private static void fillTemporaryFolder(Context context, List<Draft> drafts) {

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


        // TODO create const
        createFolder(path +"/zips");

        // TODO create const
        String draftsPath = path + "/drafts";
        createFolder(path +"/drafts");


        // Create a temporary directory for drafts and daily folder if its not exist


        String numberSubPath;
        for (int i = 0; i < numbers.size(); i++) {
            String key = new ArrayList<>(numbers.keySet()).get(i);
            List<Integer> l = new ArrayList<>(numbers.values());
            String value = String.valueOf(l.get(i));
            numberSubPath = draftsPath + "/" + key + "(" + value + ")";
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


        // Create zip archive with drafts

        createSubZips(path + "/drafts", path + "/zips");

        String finalZipPath = context.getFilesDir().getAbsolutePath() + "/zips";

        createFolder(finalZipPath);
        createFinalZip(context, path + "/zips", finalZipPath + "/" +dateFormat.format(currentDate) + ".zip");
    }


    private static void writeFileFromUri(Context context, Uri uri, String destination) {
        String source = uri.getPath();

        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;

        try {
            bis = new BufferedInputStream(new FileInputStream(context.getContentResolver().openFileDescriptor(uri, "r").getFileDescriptor()));
            bos = new BufferedOutputStream(new FileOutputStream(destination,false));
            byte[] buf = new byte[BUFFER_SIZE];
            bis.read(buf);
            do {
                bos.write(buf);
            } while (bis.read(buf) != -1);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
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

    private static void createFolder(String path) {
        File directory = new File(path);
        if (directory.exists()) {
            Log.d(TAG, "Directory " + path + " already exists!");
        } else {
            if (directory.mkdir())
                Log.d(TAG, "Directory " + path + " created successful!");
            else
                Log.d(TAG, "Directory " + path + " creation failed!");
        }
    }

    private static void updateFolder(String path) {
        File directory = new File(path);
        if (directory.exists()) {
            cleanFolder(directory);
            if (directory.mkdir())
                Log.d(TAG, "Directory " + path + " updated successful!");
            else
                Log.d(TAG, "Directory " + path + " update failed!");
        } else {
            if (directory.mkdir())
                Log.d(TAG, "Directory " + path + " created successful!");
            else
                Log.d(TAG, "Directory " + path + " creation failed!");
        }
    }

    private static void cleanFolder(File directory) {
        if (directory.isDirectory())
            for (File child : directory.listFiles())
                cleanFolder(child);

        directory.delete();
    }

    private static void createSubZips(String sourcePath, String destPath) {

        File[] folder = new File(sourcePath).listFiles();

        for (File subfolder: folder) {
            try {
                String zipPath = destPath + "/" + subfolder.getName() + ".zip";
                BufferedInputStream sourceBIS;
                ZipOutputStream destZOS = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipPath)));
                destZOS.setLevel(0);
                byte data[] = new byte[BUFFER_SIZE];

                File[] draftFolder = subfolder.listFiles();
                for (File draft: draftFolder) {
                    FileInputStream sourceFIS = new FileInputStream(draft);
                    sourceBIS = new BufferedInputStream(sourceFIS,BUFFER_SIZE);

                    ZipEntry entry = new ZipEntry(draft.getName());
                    destZOS.putNextEntry(entry);

                    int count;
                    while ((count = sourceBIS.read(data, 0, BUFFER_SIZE)) != -1) {
                        destZOS.write(data, 0, count);
                    }

                }
                destZOS.close();
                Log.d(TAG, zipPath + " ZIP created");





            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



    }

    private static void createFinalZip(Context context, String sourcePath, String destPath) {
        File[] zips = new File(sourcePath).listFiles();

        try {
            BufferedInputStream sourceBIS;
            ZipOutputStream destZOS = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(destPath)));
            destZOS.setLevel(0);
            byte data[] = new byte[BUFFER_SIZE];
            for (File zip: zips) {
                FileInputStream sourceFIS = new FileInputStream(zip);
                sourceBIS = new BufferedInputStream(sourceFIS,BUFFER_SIZE);

                ZipEntry entry = new ZipEntry(zip.getName());
                destZOS.putNextEntry(entry);

                int count;
                while ((count = sourceBIS.read(data, 0, BUFFER_SIZE)) != -1) {
                    destZOS.write(data, 0, count);
                }
            }
            destZOS.close();
            Log.d(TAG, destPath + " ZIP created");

            cleanFolder(new File(context.getFilesDir().getAbsolutePath() + TEMPORARY_SUBFOLDER));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<File> getReportsPaths(Context context) {
         File file = new File(context.getFilesDir().getAbsolutePath() + "/zips");
         if (!file.exists()) {
             createFolder(file.getPath());
         }
         List<File> result = new ArrayList<>(Arrays.asList(file.listFiles()));
         return result;
    }

    public static void removeReport(File file) {
        if (file.delete()) {
            Log.d(TAG, file.getName() + " removed");
        } else {
            Log.d(TAG, file.getName() + " removing failed");
        }

    }

    public static void removeAllReports(Context context) {
         File file = new File(context.getFilesDir().getAbsolutePath() + "/zips");
         cleanFolder(file);
    }
}