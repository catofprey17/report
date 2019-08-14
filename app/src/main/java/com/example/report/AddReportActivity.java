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
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.example.report.reporter.Recognizer;
import com.example.report.reporter.ReportIO;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AddReportActivity extends AppCompatActivity implements AddReportAdapter.ItemClickListener{

    private static final int IMAGES_REQUEST_CODE = 304;
    private static final int PERMISSION_REQUEST_CODE = 103;
    private static final int EDIT_DRAFT_REQUEST_CODE = 666;

    private LinkedHashMap<Uri, Integer> mData;

    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private AddReportAdapter mAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_report);

        mProgressBar = findViewById(R.id.progress_bar);
        mRecyclerView = findViewById(R.id.rv_add_report);


        mData = new LinkedHashMap<>();
        mAdapter = new AddReportAdapter(mData, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(dividerItemDecoration);


        launchDraftsInput();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case IMAGES_REQUEST_CODE: {
                if (resultCode == Activity.RESULT_OK) {
                    BitmapAsyncTask task = new BitmapAsyncTask(this);
                    task.execute(data);

                }
                break;
            }

            case EDIT_DRAFT_REQUEST_CODE: {
                if (resultCode == Activity.RESULT_OK) {

                }
                break;
            }
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
    public void onItemClick(Bitmap bitmap) {
        Intent intent = new Intent(this, EditDraftActivity.class);
        intent.putExtra(EditDraftActivity.BITMAP_EXTRA, bitmap);
        intent.putExtra(EditDraftActivity.NUM_EXTRA, mData.get(bitmap));
        EditDraftActivity.sBitmap = bitmap;
        startActivityForResult(intent, EDIT_DRAFT_REQUEST_CODE);
    }







    class BitmapAsyncTask extends AsyncTask<Intent, Boolean, LinkedHashMap<Uri, Integer>> {

        private WeakReference<Context> contextRef;

        public BitmapAsyncTask(Context context) {
            contextRef = new WeakReference<Context>(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected LinkedHashMap<Uri, Integer> doInBackground(Intent... intents) {
            try {
                LinkedHashMap<Uri, Integer> result;
                Recognizer recognizer = new Recognizer(contextRef.get());
                List<Bitmap> bitmaps = ReportIO.getBitmapList(contextRef.get(), intents[0]);
                result = recognizer.getDraftsHashMap(bitmaps);
                return result;

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onProgressUpdate(Boolean... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(LinkedHashMap<Uri, Integer> bitmapUriHashMap) {
            super.onPostExecute(bitmapUriHashMap);
            mData.putAll(bitmapUriHashMap);
            mAdapter.notifyDataSetChanged();
            mProgressBar.setVisibility(View.INVISIBLE);

        }
    }

}
