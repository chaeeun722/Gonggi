package com.example.myapplication;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.PopupMenu;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PostActivity extends BasicActivity {
    private ArrayList<PostInfo> mDataset;
    private Activity activity;
    private OnPostListener onPostListener;
    private FirebaseFirestore firebaseFirestore;
    private Util util;
    private FirebaseUser user;

    String TAG = "post activity :: ";
    Map<String, Boolean> likey = new HashMap<>();
    Map<String, Boolean> likeyCancel = new HashMap<>();
    Map<String, Boolean> isAlreadyliked = new HashMap<>();
    Object likefromdoc;

    private CollectionReference collectionReference;
    public  CmtAdapter cmtAdapter;
    ArrayList<PostInfo> postList = new ArrayList<>();

    //댓글
    Button btn_r_write;
    EditText input_r_content;
    RecyclerView recyclerView;
    private LinearLayout parent;
    PostInfo postInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        postInfo = (PostInfo) getIntent().getSerializableExtra("postInfo");
//        String thispostID = postInfo.getID().toString();
//
//        Log.d(TAG, " this document: " + thispostID);

        //댓글
        input_r_content = (EditText) findViewById(R.id.input_r_content);
        btn_r_write = (Button) findViewById(R.id.btn_r_write);
        recyclerView = (RecyclerView) findViewById(R.id.recycler);

        cmtAdapter = new CmtAdapter(postList);

        RecyclerView recyclerView = findViewById(R.id.recycler);

        recyclerView.setLayoutManager(new LinearLayoutManager(PostActivity.this));
        recyclerView.setAdapter(cmtAdapter);

        //댓글버튼 클릭시! alert창
        btn_r_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert_confirm = new AlertDialog.Builder(PostActivity.this);
                alert_confirm.setMessage("댓글을 게시하겠습니까?").setCancelable(false).setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                post();
                                postsUpdate();
                            }
                        }).setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        });
                AlertDialog alert = alert_confirm.create();
                alert.show();
            }
        });

        ReadContentsView readContentsView = findViewById(R.id.readContentsView);
        readContentsView.setPostInfo(postInfo);


        Button likebtn = findViewById(R.id.likeBtn);
        TextView likeCounter = findViewById(R.id.likeCounterTextView);
        String  TAG = "like debug  :: post :: ";
        Log.d(TAG, (Integer.toString(postInfo.getLikesCount())));

        user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserUID = user.getUid();
        Log.d(TAG, "current user : " + currentUserUID);

        String id = postInfo.getID();
        Log.d(TAG, "this post id :: " + id);
        firebaseFirestore = FirebaseFirestore.getInstance();
        DocumentReference postDoc = firebaseFirestore.collection("posts").document(id);
        Log.d(TAG, "postDoc : "+postDoc.get().toString());

        postDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        likeCounter.setText(document.get("likesCount").toString()); // 디비에 있는 좋아요 수로 설정.
                        postInfo.setLikesCount(Integer.parseInt(document.get("likesCount").toString())); // thisData에 디비데이터 넣기
                        likefromdoc = document.get("likes");
                        Object likefromdoclikecnt = document.get("likesCount");
                        int likefromdoclikecntToint = Integer.parseInt(likefromdoclikecnt.toString());
                        Log.d(TAG, "likefromdoc count :::  " + likefromdoclikecnt);
                        if (likefromdoc == null || (likefromdoclikecntToint == 0)){
                            Log.d(TAG, "is already liked ::: no like ");
                            likebtn.setText("like");
                        } else { // 좋아요 했을때
//                            Log.d(TAG, "is already liked ::: "+ likefromdoc.toString());
                            String[] liked = likefromdoc.toString().split("=");
//                            Log.d(TAG, "is already liked to string ::: "+  liked[0] + liked[1]);
                            isAlreadyliked.put(liked[0], true); // likes map 객체 넣기
                            Log.d(TAG, "is already liked ::: "+ isAlreadyliked);
                            postInfo.setLikes(isAlreadyliked);
                            Log.d(TAG, "onComplete: " + postInfo.getLikes());
                            if (liked[0].contains(currentUserUID)){
                                Log.d(TAG, "is already liked :: make btn liked");
                                likebtn.setText("liked");
                            }
                        }
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });


        likebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                likey.clear();
                likey.put(currentUserUID,true);
                Log.d(TAG, "1  ::  "+ likey.toString());
                DocumentReference postDoc = firebaseFirestore.collection("posts").document(id);
                Log.d(TAG, "2  ::  "+ postDoc);
//                if (postInfo.getLikes().containsKey(currentUserUID)){ //이미 눌렀던 상태
                if (postInfo.getLikes().containsValue(true)){
                    Log.d(TAG, "3  ::  ");
                    likebtn.setText("like"); // 다시 라이크로 돌리고
                    postInfo.setLikesCount(postInfo.getLikesCount() - 1); // 좋아요 수 하나빼기
                    postInfo.getLikes().remove(currentUserUID); // 눌럿던거 지우기
                    likeyCancel.put(null,null);
                    postInfo.setLikes(likeyCancel);
                    postDoc.update("likes", null);
                    likeCounter.setText(Integer.toString(postInfo.getLikesCount()));
                    postDoc.update("likesCount", postInfo.getLikesCount() );
                } else { // 좋아요 누르기
                    Log.d(TAG, "4  ::  ");
                    likebtn.setText("liked"); // 좋아요
                    postInfo.setLikes(likey);
                    postInfo.setLikesCount(postInfo.getLikesCount() + 1); // 좋아요 수 +
                    postInfo.getLikes().put(currentUserUID,true);
                    postDoc.update("likes", likey);
                    likeCounter.setText(Integer.toString(postInfo.getLikesCount())); // 카운터글씨를 다시 바꿔주기
                    postDoc.update("likesCount", postInfo.getLikesCount() );
                }
            }
        });

        CollectionReference collectionReference = firebaseFirestore.collection("comments")
                .document(postInfo.getTitle())
                .collection(postInfo.getTitle());

        collectionReference
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            postList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                postList.add(new PostInfo(
                                        document.getData().get("comment").toString(),
                                        new Date(document.getDate("createdAt").getTime())));

                            }
                            cmtAdapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });


        setToolbarTitle(postInfo.getTitle());

    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    private void postsUpdate(){
        //이 함수를 통해서 firestore에서 값을 불러오는 것 같은데
        //이걸 collection("comments").document("문제 제목").collection("문제 제목").get()으로 해주면 될 듯
        //field 값은 comment와 createdAt 그대로 쓰는 것 추천
        //그리고 이걸 onResume에 넣지 말고 post()가 있는 다음에 넣어서 댓글을 달면 update하게 하면 될 듯
        //만약 제대로 결과가 나오지 않는다면 post() 안에 넣어서 post()가 다 실행된 다음 작동하게 만들어주기
        CollectionReference collectionReference = firebaseFirestore.collection("comments")
                .document(postInfo.getTitle())
                .collection(postInfo.getTitle());

        collectionReference
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            postList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                postList.add(new PostInfo(
                                        document.getData().get("comment").toString(),
                                        new Date(document.getDate("createdAt").getTime())));

                            }
                            cmtAdapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    //댓글 파이어베이스 연결
    private void post() {
        String comment = ((EditText) findViewById(R.id.input_r_content)).getText().toString();
        Date date = new Date();

        Map<String, Object> cmtBase = new HashMap<>();
        cmtBase.put("comment", comment);
        cmtBase.put("createdAt", date);

        if (comment.length() > 0) {
            user = FirebaseAuth.getInstance().getCurrentUser();

            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

            firebaseFirestore.collection("comments")
                    .document(postInfo.getTitle())
                    .collection(postInfo.getTitle())
                    .document()
                    .set(cmtBase);
        }
        input_r_content.setText(""); //글쓰고 나서 텍스트 창 초기화
    }

/*    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.post,menu);
        return super.onCreateOptionsMenu(menu);
    }*/



}


