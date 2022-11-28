package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.audiofx.LoudnessEnhancer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MyPostListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_post_list);

        //게시판 클릭시 액태비티 전환
        Button home_btn = (Button) findViewById(R.id.home);
        home_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                overridePendingTransition(0,0); //애니메이션 없애는거
                finish(); //원래 액티비티 종료
            }
        });
        //설정버튼 클릭시 액티비티 전환
        Button setting_btn = (Button) findViewById(R.id.setting);
        setting_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getApplicationContext(),LogoutActivity.class);
                startActivity(intent);
                overridePendingTransition(0,0); //애니메이션 없애는거
                finish(); //원래 액티비티 종료
            }
        });
    }
}