package com.example.report.reporter;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

public class Recognizer {

    private Context mContext;
    private ContentResolver mContentResolver;

    public Recognizer(Context context) throws IOException {
        mContext = context;
        mContentResolver = context.getContentResolver();
        ReportIO.copyTessdataToInternalStorage(context);
    }

    public LinkedHashMap<Bitmap, Integer> getDraftsHashMap (List<Bitmap> bitmaps) throws IOException {

        LinkedHashMap<Bitmap, Integer> result = new LinkedHashMap<>();
        for (int i = 0; i < bitmaps.size(); i++) {
            int num;
            try {
                num = Integer.parseInt(getDraftNum(bitmaps.get(i)));
            } catch (NumberFormatException ex) {
                num = 0;
            }
            result.put(bitmaps.get(i),num);
        }
        return result;
    }


    private String getDraftNum(Bitmap bitmap) throws IOException {

        ReportIO.copyTessdataToInternalStorage(mContext);

        bitmap = cropDraft(bitmap);

        TessBaseAPI tessBaseAPI = new TessBaseAPI();
        tessBaseAPI.init(mContext.getFilesDir().getAbsolutePath(), "eng");
        tessBaseAPI.setImage(bitmap);
        String extractedText = tessBaseAPI.getUTF8Text();
        tessBaseAPI.end();
        return extractedText;
    }

    private static Bitmap cropDraft(Bitmap bitmap) {
        // TODO Switch draft orientation
        int xStart = (int) Math.round(bitmap.getWidth() * 0.42);
        int xEnd = (int) Math.round(bitmap.getWidth() * 0.72);
        int yStart = (int) Math.round(bitmap.getHeight() * 0.84);
        int yEnd = (int) Math.round(bitmap.getHeight() * 0.91);

        return Bitmap.createBitmap(bitmap,xStart, yStart, xEnd-xStart, yEnd-yStart);
    }
}
