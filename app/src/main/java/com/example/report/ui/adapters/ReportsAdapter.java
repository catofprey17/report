package com.example.report.ui.adapters;

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

import com.example.report.R;
import com.example.report.reporter.ReportIO;

import java.io.File;
import java.util.List;

public class ReportsAdapter extends RecyclerView.Adapter<ReportsAdapter.ReportsViewHolder> {

    private List<File> archives;
    private Context context;

    public ReportsAdapter(Context context, List<File> archives) {
        this.archives = archives;
        this.context = context;
    }
    public void updateData(List<File> archives) {
        this.archives = archives;
        notifyDataSetChanged();
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
        holder.mTextView.setText(archives.get(position).getName());
    }

    @Override
    public int getItemCount() {
        if (archives == null) {
            return 0;
        }
        return archives.size();
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
                            "com.example.report.provider",
                            archives.get(getAdapterPosition()));
                    sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
                    sendIntent.setType("application/zip");
                    context.startActivity(sendIntent);
                }
            });

            mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ReportIO.removeReport(archives.get(getAdapterPosition()));
                    archives.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                }
            });
        }

    }
}
