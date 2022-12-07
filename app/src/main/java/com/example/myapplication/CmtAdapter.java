package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class CmtAdapter extends RecyclerView.Adapter<CmtAdapter.CmtViewHolder> {
    ArrayList<PostInfo> mDataset;
    FirebaseFirestore firebaseFirestore;

    public static class CmtViewHolder extends RecyclerView.ViewHolder {
        TextView cmtView;
        TextView date;

        public CmtViewHolder(View v) {
            super(v);

            cmtView = v.findViewById(R.id.cmtView);
            date = v.findViewById(R.id.date);
        }
    }

    public CmtAdapter(ArrayList<PostInfo> myDataset) {
        this.mDataset = myDataset;
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    @Override
    public int getItemViewType(int position){
        return position;
    }

    @NonNull
    @Override
    public CmtAdapter.CmtViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_cmt, parent, false);
        return new CmtViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CmtViewHolder holder, int position) {
        // createdAt
        holder.date.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(mDataset.get(position).getCreatedAt()));
        //댓글
        holder.cmtView.setText(mDataset.get(position).getComment());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}