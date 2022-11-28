package com.example.myapplication;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class LogoutActivity extends BasicActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);
        findViewById(R.id.LogoutButton).setOnClickListener(onClickListener);
        findViewById(R.id.revokeButton).setOnClickListener(onClickListener);

        //내글 모아보기 버튼 클릭시 액태비티 전환
        Button mypost_btn = (Button) findViewById(R.id.mypost);
        mypost_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getApplicationContext(),MyPostListActivity.class);
                startActivity(intent);
                overridePendingTransition(0,0); //애니메이션 없애는거
                finish(); //원래 액티비티 종료
            }
        });
        //홈 버튼 클릭시 액티비티 전환
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

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.LogoutButton:
                    FirebaseAuth.getInstance().signOut();
                    myStartActivity(JoinActivity.class);
                    break;
                case R.id.revokeButton:
                    FirebaseAuth.getInstance().getCurrentUser().delete();
                    finishAffinity();
                    break;
            }
        }
    };

    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }
}


