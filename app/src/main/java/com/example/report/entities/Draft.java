package com.example.report.entities;

import android.net.Uri;

public class Draft {

    private static final int MIN_NUMBER = 0;
    private static final int MAX_NUMBER = 9999;

    private String number;
    private Uri uri;

    public Draft(Uri uri, String number) {
        this.uri = uri;
        try {
            int temp = Integer.parseInt(number);
            if (temp >=MIN_NUMBER && temp <= MAX_NUMBER);
            this.number = number;
        } catch (NumberFormatException ex) {
            this.number = "";
        }
    }

    public Draft(Uri uri) {
        this.uri = uri;
        this.number = "";
    }

    public Draft(Draft draft) {
        this.uri = draft.uri;
        this.number = draft.number;
    }




    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (obj.getClass() != getClass())
            return false;
        Draft draft = (Draft) obj;
        if (draft.uri.equals(this.uri))
            return true;
        return false;
    }
}
