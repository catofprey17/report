package com.example.report;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.report.entities.Draft;

import java.io.IOException;
import java.util.List;

public class AddReportAdapter extends RecyclerView.Adapter<AddReportAdapter.AddReportViewHolder> {

    private List<Draft> mData;
    private ItemClickListener mListener;
    private Context mContext;

    public AddReportAdapter(Context context, List<Draft> data, ItemClickListener listener) {
        mContext = context;
        mData = data;
        mListener = listener;
    }

    @NonNull
    @Override
    public AddReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.add_report_item, parent, false);

        // TODO Colorize background
        AddReportViewHolder viewHolder = new AddReportViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull AddReportViewHolder holder, int position) {
        Draft draft = mData.get(position);
        try {
            if (draft.getNumber().isEmpty()) {
                holder.mRoot.setBackgroundColor(ContextCompat.getColor(mContext, R.color.recyclerItemIncorrect));
            } else {
                holder.mRoot.setBackgroundColor(ContextCompat.getColor(mContext, R.color.recyclerItemCorrect));
            }
            holder.mTextView.setText(draft.getNumber());
            holder.mImageView.setImageBitmap(draft.getBitmap(mContext));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @Override
    public int getItemCount() {
        if (mData == null) {
            return 0;
        }
        return mData.size();
    }



    class AddReportViewHolder extends RecyclerView.ViewHolder {

        private ImageView mImageView;
        private TextView mTextView;
        private LinearLayout mDescription;
        private LinearLayout mRoot;
        private ImageButton mButton;

        public AddReportViewHolder(@NonNull View itemView) {
            super(itemView);

            mDescription = itemView.findViewById(R.id.viewHolder_item_description);
            mImageView = itemView.findViewById(R.id.image_draft);
            mTextView = itemView.findViewById(R.id.text_number);
            mButton = itemView.findViewById(R.id.button_remove_draft);
            mRoot = itemView.findViewById(R.id.viewHolder_root);

            mDescription.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onItemClick(getAdapterPosition());
                }
            });

            mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mData.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    mListener.checkForCreationAvailability();
                }
            });

        }

    }

    public interface ItemClickListener {
        void onItemClick(int position);
        void checkForCreationAvailability();
    }
}
