package com.example.report;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Session2Command;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.report.reporter.ReportIO;

import java.io.IOException;

public class EditDraftActivity extends AppCompatActivity {

    public static final String URI_EXTRA = "uri";
    public static final String NUM_EXTRA = "num";


    private ImageView mImageView;
    private EditText mEditText;
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_draft);

        mEditText = findViewById(R.id.edit_draft_num);
        mImageView = findViewById(R.id.image_edit_draft);
        mButton = findViewById(R.id.button_save_changes);

        //TODO Add button handle and EditText text changes

        Intent intent = getIntent();
        try {
            if (intent.hasExtra(NUM_EXTRA) && intent.hasExtra(URI_EXTRA)) {
                mImageView.setImageBitmap(ReportIO.getBitmapFromUri(this,(Uri)intent.getParcelableExtra(URI_EXTRA)));
                mEditText.setText(intent.getStringExtra(NUM_EXTRA));
            } else {
                setResult(RESULT_CANCELED);
                finish();
            }
        } catch (IOException e) {
            e.printStackTrace();
            setResult(RESULT_CANCELED);
            finish();
        }
    }
}
