package com.example.report;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class AddReportAdapter extends RecyclerView.Adapter<AddReportAdapter.AddReportViewHolder> {

    private LinkedHashMap<Bitmap, Integer> mData;
    private ItemClickListener mListener;

    public AddReportAdapter(LinkedHashMap<Bitmap, Integer> data, ItemClickListener listener) {
        mData = data;
        mListener = listener;
    }

    @NonNull
    @Override
    public AddReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater =LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.add_report_item, parent, false);

        // TODO Colorize background
        AddReportViewHolder viewHolder = new AddReportViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AddReportViewHolder holder, int position) {
        int num = new ArrayList<Integer>(mData.values()).get(position);
        if (num != 0) {
            holder.mTextView.setText(String.valueOf(num));
        } else {
            // TODO Fix color
            holder.mLayout.setBackgroundColor(Color.parseColor("#FF5555"));
        }
        holder.mImageView.setImageBitmap((Bitmap) mData.keySet().toArray()[position]);
    }

    @Override
    public int getItemCount() {
        if (mData == null) {
            return 0;
        }
        return mData.size();
    }

    class AddReportViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mImageView;
        private TextView mTextView;
        private LinearLayout mLayout;

        public AddReportViewHolder(@NonNull View itemView) {
            super(itemView);

            mLayout = itemView.findViewById(R.id.viewHolder_add_report);
            mImageView = itemView.findViewById(R.id.image_draft);
            mTextView = itemView.findViewById(R.id.text_number);

            // TODO Divide listeners
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Bitmap bitmap = (Bitmap) mData.keySet().toArray()[getAdapterPosition()];
            mListener.onItemClick(bitmap);

        }
    }

    public interface ItemClickListener {
        void onItemClick(Bitmap bitmap);
    }
}
