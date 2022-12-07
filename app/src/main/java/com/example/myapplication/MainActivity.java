package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends BasicActivity {
    private static final String TAG = "MainActiviy";
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference collectionReference;
    private HomeAdapter2 homeAdapter;
    private ArrayList<PostInfo> postList;
    private Util util;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        //설정 버튼 클릭시 액티비티 전환
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

        setToolbarTitle(getResources().getString(R.string.app_name));


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser == null) {
            myStartActivity(JoinActivity.class);
        } else {
            firebaseFirestore = FirebaseFirestore.getInstance();
            DocumentReference documentReference = firebaseFirestore.collection("users").document(firebaseUser.getUid());
//            documentReference.get().addOnCompleteListener((task) -> {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//                    if (document != null) {
//                        if (document.exists()) {
//                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
//                        } else {
//                            Log.d(TAG, "No such document");
//                        }
//                    }
//                } else {
//                    Log.d(TAG, "get failed with ", task.getException());
//                }
//            });
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {
                            if (document.exists()) {
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        }

        util = new Util(this);
        postList = new ArrayList<>();
        homeAdapter = new HomeAdapter2(MainActivity.this, postList);
        homeAdapter.setOnPostListener(onPostListener);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        findViewById(R.id.floatingActionButton).setOnClickListener(onClickListener);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerView.setAdapter(homeAdapter);
    }

    @Override
    protected void onResume(){
        super.onResume();
        postsUpdate();
    }

//    protected void onResume(){
//        super.onResume();
//
//        if (firebaseUser != null) {
//            collectionReference = firebaseFirestore.collection("posts");
//            collectionReference.orderBy("createdAt", Query.Direction.DESCENDING).get()
//                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                            if (task.isSuccessful()) {
//                                postList = new ArrayList<>();
//                                for (QueryDocumentSnapshot document : task.getResult()) {
//                                    Log.d(TAG, document.getId() + " => " + document.getData());
//                                    postList.add(new PostInfo(
//                                            document.getData().get("title").toString(),
//                                            document.getData().get("contents").toString(),
//                                            document.getData().get("publisher").toString(),
//                                            new Date(document.getDate("createdAt").getTime()),
//                                            document.getId()));
//                                }
//                                mainAdapter.notifyDataSetChanged();
//
//
//                            } else {
//                                Log.d(TAG, "Error getting documents: ", task.getException());
//                            }
//                        }
//                    });
//        }
//    }


    OnPostListener onPostListener = new OnPostListener() {
        @Override
        public void onDelete(String id) {
            postsUpdate();
        }

        @Override
        public void onModify(String id) {
            myStartActivity(WritePostActivity.class, id);
        }
    };


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
//                case R.id.logoutButton: 추후 사용
//                    FirebaseAuth.getInstance().signOut();
//                    startJoinActivity();
//                    break;
                case R.id.floatingActionButton:
                    myStartActivity(WritePostActivity.class);
                    break;
            }
        }
    };

    private void postsUpdate(){
        if (firebaseUser != null) {
            CollectionReference collectionReference = firebaseFirestore.collection("posts");
            collectionReference.orderBy("createdAt", Query.Direction.DESCENDING).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                postList.clear();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    postList.add(new PostInfo(
                                            document.getData().get("title").toString(),
                                            document.getData().get("contents").toString(),
                                            document.getData().get("publisher").toString(),
                                            new Date(document.getDate("createdAt").getTime()),
                                            document.getId()));
                                }
                                homeAdapter.notifyDataSetChanged();
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }

//    private void postsUpdate(){
//        if (firebaseUser != null) {
//            CollectionReference collectionReference = firebaseFirestore.collection("posts");
//            collectionReference.orderBy("createdAt", Query.Direction.DESCENDING).get()
//                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                            if (task.isSuccessful()) {
//                                postList.clear();
//                                for (QueryDocumentSnapshot document : task.getResult()) {
//                                    Log.d(TAG, document.getId() + " => " + document.getData());
//                                    postList.add(new PostInfo(
//                                            document.getData().get("title").toString(),
//                                            (ArrayList<String>) document.getData().get("contents"),
//                                            document.getData().get("publisher").toString(),
//                                            new Date(document.getDate("createdAt").getTime()),
//                                            document.getId()));
//                                }
//                                mainAdapter.notifyDataSetChanged();
//                            } else {
//                                Log.d(TAG, "Error getting documents: ", task.getException());
//                            }
//                        }
//                    });
//        }
//    }



    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }

    private void myStartActivity(Class c, String id) {
        Intent intent = new Intent(this, c);
        intent.putExtra("id",id);
        startActivity(intent);
    }

//    private void myStartActivity(Class c, PostInfo postInfo) {
//        Intent intent = new Intent(this, c);
//        intent.putExtra("postInfo", postInfo);
//        startActivity(intent);
//    }



}



