package com.example.report.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import com.example.report.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.settings_fragment_container, new SettingsFragment())
                .commit();
    }
}
