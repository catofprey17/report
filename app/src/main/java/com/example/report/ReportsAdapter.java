package com.example.report;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;

public class ReportsAdapter extends RecyclerView.Adapter<ReportsAdapter.ReportsViewHolder> {

    private File[] archives;
    private Context context;

    public ReportsAdapter(Context context, File[] archives) {
        this.archives = archives;
        this.context = context;
    }

    @NonNull
    @Override
    public ReportsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.reports_item, parent, false);

        ReportsViewHolder viewHolder = new ReportsViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ReportsViewHolder holder, int position) {
        holder.mTextView.setText(archives[position].getName());
    }

    @Override
    public int getItemCount() {
        if (archives == null) {
            return 0;
        }
        return archives.length;
    }

    class ReportsViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout mLayout;
        TextView mTextView;
        ImageButton mButton;

        public ReportsViewHolder(@NonNull View itemView) {
            super(itemView);

            mLayout = itemView.findViewById(R.id.item_report_constraint_layout);
            mTextView = itemView.findViewById(R.id.text_report_name);
            mButton = itemView.findViewById(R.id.button_remove_report);

            mLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    Uri uri = FileProvider.getUriForFile(
                            context,
                            "com.example.report.provider", //(use your app signature + ".provider" )
                            archives[getAdapterPosition()]);
                    sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
                    sendIntent.setType("application/zip");
                    context.startActivity(sendIntent);
                }
            });
        }

    }
}
