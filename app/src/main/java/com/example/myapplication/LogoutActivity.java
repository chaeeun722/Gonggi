package com.example.myapplication;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class LogoutActivity extends BasicActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);
        findViewById(R.id.LogoutButton).setOnClickListener(onClickListener);
        findViewById(R.id.revokeButton).setOnClickListener(onClickListener);


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


