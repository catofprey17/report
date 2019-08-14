package com.example.report.entities;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

// TODO Coplete if it's necessary
public class DraftList {

    private Draft[]drafts;

    public DraftList() {

    }

    public Draft getByBitmap (Bitmap bitmap) {
        for (Draft draft: drafts) {
            if (draft.getBitmap() == bitmap) {
                return draft;
            }
        }
        return null;
    }

    public DraftList getByNumber (String number) {
        return null;
    }
}
