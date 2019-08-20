package com.example.report;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.report.entities.Draft;
import com.example.report.reporter.Recognizer;
import com.example.report.reporter.ReportIO;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class AddReportActivity extends AppCompatActivity implements AddReportAdapter.ItemClickListener{

    private static final int IMAGES_REQUEST_CODE = 304;
    private static final int PERMISSION_REQUEST_CODE = 103;
    private static final int EDIT_DRAFT_REQUEST_CODE = 666;

    private List<Draft> mData;

    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private AddReportAdapter mAdapter;
    private MaterialButton mCreateButton;
    private TextView mProgressText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_report);

        mProgressBar = findViewById(R.id.progress_bar);
        mRecyclerView = findViewById(R.id.rv_add_report);
        mCreateButton = findViewById(R.id.button_create_report);
        mProgressText = findViewById(R.id.text_progress_loading_images);




        mData = new ArrayList<>();
        mAdapter = new AddReportAdapter(this, mData, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(dividerItemDecoration);



        launchDraftsInput();


        mCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReportCreatingAsyncTask task = new ReportCreatingAsyncTask(AddReportActivity.this);
                task.execute(mData);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case IMAGES_REQUEST_CODE:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        BitmapAsyncTask task = new BitmapAsyncTask(this);
                        task.execute(data);
                        break;

                    case Activity.RESULT_CANCELED:
                        setResult(RESULT_CANCELED);
                        finish();
                }
                break;

                // TODO cache exceptions
            case EDIT_DRAFT_REQUEST_CODE:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Integer index = data.getIntExtra(EditDraftActivity.LIST_INDEX_EXTRA, -1);
                        String number = data.getStringExtra(EditDraftActivity.NUM_EXTRA);
                        if (index == -1) {
                            return;
                        }
                        mData.get(index).setNumber(number);
                        mAdapter.notifyDataSetChanged();
                        checkForCreationAvailability();
                }
                break;

        }
    }

    private void launchDraftsInput() {
        checkForPermissions();
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType("image/jpeg");
        startActivityForResult(intent, IMAGES_REQUEST_CODE);
    }

    private void checkForPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (!(grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                setResult(RESULT_CANCELED);
                finish();
            }
        }
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(this, EditDraftActivity.class);
        Draft draft = mData.get(position);
        intent.putExtra(EditDraftActivity.URI_EXTRA, draft.getUri());
        intent.putExtra(EditDraftActivity.NUM_EXTRA, draft.getNumber());
        intent.putExtra(EditDraftActivity.LIST_INDEX_EXTRA, position);
        startActivityForResult(intent, EDIT_DRAFT_REQUEST_CODE);
    }


    // TODO Move to another thread
    @Override
    public void checkForCreationAvailability() {
        int wrongNums = 0;
        for (int i = 0; i< mData.size(); i++) {
            if (mData.get(i).getNumber().equals(""))
                wrongNums++;

        }
        if (wrongNums > 0) {
            String message = getString(
                    R.string.snack_message_add_report_prefix) +
                    " " +
                    wrongNums +
                    " " +
                    getString(R.string.snack_mesage_add_report_postfix);
            View view = findViewById(R.id.root_activity_add_report);
            Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
        } else {
            mCreateButton.setEnabled(true);
        }

    }

    class BitmapAsyncTask extends AsyncTask<Intent, Integer, List<Draft>> {

        private WeakReference<Context> contextRef;

        public BitmapAsyncTask(Context context) {
            contextRef = new WeakReference<>(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
            mProgressText.setVisibility(View.VISIBLE);
            // TODO create string res
            mProgressText.setText("Подготовка...");
        }

        @Override
        protected List<Draft> doInBackground(Intent... intents) {
            try {
                List<Draft> drafts= new ArrayList<>();
                List<Uri> uris = ReportIO.getUriList(intents[0]);
                Recognizer recognizer = new Recognizer(contextRef.get());

                int size = uris.size();
                for (int i = 0; i < size; i++) {
                    publishProgress(size - i);
                    Draft draft = new Draft(uris.get(i));
                    draft.setNumber(recognizer.getDraftNum(ReportIO.getBitmapFromUri(contextRef.get(), uris.get(i))));
                    drafts.add(draft);
                }
                recognizer.close();
                return drafts;

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            mProgressText.setText(getResources().getString(R.string.progress_loading_prefix) + " " + values[0]);
        }

        @Override
        protected void onPostExecute(List<Draft> drafts) {
            super.onPostExecute(drafts);
            mData.addAll(drafts);
            mAdapter.notifyDataSetChanged();
            mProgressBar.setVisibility(View.INVISIBLE);
            mProgressText.setVisibility(View.INVISIBLE);
            mCreateButton.setVisibility(View.VISIBLE);
            checkForCreationAvailability();
        }
    }

    class ReportCreatingAsyncTask extends AsyncTask<List<Draft>,Integer,Void> {

        private WeakReference<Context> contextRef;

        public ReportCreatingAsyncTask(Context context) {contextRef = new WeakReference<>(context);}


        @Override
        protected Void doInBackground(List<Draft>... lists) {
            ReportIO.createReport(contextRef.get(), lists[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            setResult(RESULT_OK);
            finish();
        }

    }

}
