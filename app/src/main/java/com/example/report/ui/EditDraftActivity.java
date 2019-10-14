package com.example.report.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Session2Command;
import android.net.ParseException;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.report.R;
import com.example.report.entities.Draft;
import com.example.report.reporter.ReportIO;

import java.io.IOException;

public class EditDraftActivity extends AppCompatActivity {

    public static final String URI_EXTRA = "uri";
    public static final String NUM_EXTRA = "num";
    public static final String LIST_INDEX_EXTRA = "list-extra";


    private ImageView mImageView;
    private EditText mEditText;
    private Button mButton;

    private Integer mListNum;
    private Draft mDraft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_draft);

        mEditText = findViewById(R.id.edit_draft_num);
        mImageView = findViewById(R.id.image_edit_draft);
        mButton = findViewById(R.id.button_save_changes);

        //TODO Add button handle and EditText text changes

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Integer temp = Integer.parseInt(mEditText.getText().toString());
                    Intent result = new Intent();
                    result.putExtra(NUM_EXTRA, temp.toString());
                    result.putExtra(LIST_INDEX_EXTRA, mListNum);
                    setResult(RESULT_OK, result);
                } catch (ParseException ex) {
                    setResult(RESULT_CANCELED);
                }
                finish();
            }
        });

        Intent intent = getIntent();
        try {
            if (intent.hasExtra(NUM_EXTRA) && intent.hasExtra(URI_EXTRA) && intent.hasExtra(LIST_INDEX_EXTRA)) {
                mDraft = new Draft((Uri)intent.getParcelableExtra(URI_EXTRA),
                        intent.getStringExtra(NUM_EXTRA));
                mListNum = intent.getIntExtra(LIST_INDEX_EXTRA, -1);
                mImageView.setImageBitmap(ReportIO.getBitmapFromUri(this,mDraft.getUri()));
                mEditText.setText(mDraft.getNumber());
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
