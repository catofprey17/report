package com.example.report.reporter;

import android.content.Context;
import android.graphics.Bitmap;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.IOException;

public class Recognizer {

    private TessBaseAPI tessBaseAPI;

    public Recognizer(Context context) throws IOException {
        ReportIO.copyTessdataToInternalStorage(context);
        tessBaseAPI = new TessBaseAPI();
        tessBaseAPI.init(context.getFilesDir().getAbsolutePath(), "eng");
    }

    public String getDraftNum(Bitmap bitmap) {
        bitmap = cropDraft(bitmap);
        tessBaseAPI.setImage(bitmap);
        return tessBaseAPI.getUTF8Text();
    }

    public void close() {
        tessBaseAPI.end();
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
