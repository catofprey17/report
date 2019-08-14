package com.example.report;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Session2Command;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

public class EditDraftActivity extends AppCompatActivity {

    public static final String BITMAP_EXTRA = "bitmap";
    public static final String NUM_EXTRA = "num";

    public static Bitmap sBitmap;

    private ImageView mImageView;
    private EditText mEditText;
    private ImageButton mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_draft);

        mEditText = findViewById(R.id.edit_draft_num);
        mImageView = findViewById(R.id.image_edit_draft);
        mButton = findViewById(R.id.button_save_changes);

        Intent intent = getIntent();
        if (intent.hasExtra(NUM_EXTRA)) {
            mImageView.setImageBitmap(sBitmap);
            mEditText.setText(intent.getStringExtra(NUM_EXTRA));
        } else {
            setResult(RESULT_CANCELED);
            finish();
        }
    }
}
