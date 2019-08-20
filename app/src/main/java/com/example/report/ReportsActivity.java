package com.example.report;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.report.reporter.ReportIO;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class ReportsActivity extends AppCompatActivity {

    private FloatingActionButton mFAB;
    private RecyclerView mRecyclerView;
    private ReportsAdapter mAdapter;

    private static final int NEW_REPORT_REQUEST_CODE = 595;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        mFAB = findViewById(R.id.fab);

        mRecyclerView = findViewById(R.id.rv_reports);
        mAdapter = new ReportsAdapter(this, ReportIO.getReportsPaths(this));
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(dividerItemDecoration);






        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ReportsActivity.this, AddReportActivity.class);
                startActivityForResult(intent, NEW_REPORT_REQUEST_CODE);
            }
        });




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_button_clear_archives:
                ReportIO.removeAllReports(this);
                mAdapter.updateData(ReportIO.getReportsPaths(this));
                mAdapter.notifyDataSetChanged();
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NEW_REPORT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            mAdapter.updateData(ReportIO.getReportsPaths(this));
            mAdapter.notifyDataSetChanged();

        }
    }
}
