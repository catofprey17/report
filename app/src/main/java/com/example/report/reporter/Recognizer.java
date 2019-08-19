package com.example.report.reporter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import com.example.report.entities.Draft;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Recognizer {

    private Context context;
    private TessBaseAPI tessBaseAPI;

    public Recognizer(Context context) throws IOException {
        this.context = context;
        ReportIO.copyTessdataToInternalStorage(context);
        tessBaseAPI = new TessBaseAPI();
        tessBaseAPI.init(this.context.getFilesDir().getAbsolutePath(), "eng");
    }

    public String getDraftNum(Bitmap bitmap) {

        bitmap = cropDraft(bitmap);

//        TessBaseAPI tessBaseAPI = new TessBaseAPI();
//        tessBaseAPI.init(context.getFilesDir().getAbsolutePath(), "eng");
        tessBaseAPI.setImage(bitmap);
        String extractedText = tessBaseAPI.getUTF8Text();
//        tessBaseAPI.end();
        return extractedText;
    }

    public List<Draft> getDraftsNums(List<Uri> uris) throws IOException {



        List<Draft> drafts = new ArrayList<>();

        // TODO Move to

        for (int i = 0; i < uris.size() ; i++) {
            Uri uri = uris.get(i);
            Bitmap bitmap = cropDraft(ReportIO.getBitmapFromUri(context, uri));
            tessBaseAPI.setImage(bitmap);
            String extractedText = tessBaseAPI.getUTF8Text();
            Draft draft = new Draft(uri, extractedText);
            drafts.add(draft);
        }

        tessBaseAPI.end();

        return drafts;

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
