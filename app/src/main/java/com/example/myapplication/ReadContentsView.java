package com.example.myapplication;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class ReadContentsView extends LinearLayout {
    private Context context;

    public ReadContentsView(Context context){
        super(context);
        this.context = context;
        initView();
    }
    public ReadContentsView(Context context, @Nullable AttributeSet attributeSet){
        super(context, attributeSet);
        this.context = context;
        initView();
    }

    private void initView(){
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        setOrientation(LinearLayout.VERTICAL);
        LayoutInflater layoutInflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.view_post,this,true);
    }

    public void setPostInfo(PostInfo postInfo){


        LinearLayout contentsLayout = findViewById(R.id.contentsLayout);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        contentsLayout.removeAllViews();

        TextView createdAtTextView = findViewById(R.id.createdAtTextView);
        createdAtTextView.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(postInfo.getCreatedAt()));



        // 기존 방식 이미지어레이 없이 스트링만
        String contents = postInfo.getContents();
        TextView textView = new TextView(context);
        textView.setLayoutParams(layoutParams);
        textView.setText(contents);
        contentsLayout.addView(textView);
    }
}
