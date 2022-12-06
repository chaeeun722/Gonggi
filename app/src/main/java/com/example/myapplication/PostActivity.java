package com.example.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PostActivity extends BasicActivity {

    String TAG = "post activity :: ";
    private FirebaseUser user;
    private FirebaseFirestore firebaseFirestore;
    Map<String, Boolean> likey = new HashMap<>();

    private CollectionReference collectionReference;
    private HomeAdapter homeAdapter;
    public  CmtAdapter cmtAdapter;
    private ArrayList<PostInfo> postList;
    private Util util;

    //댓글
    ImageButton btn_r_write;
    EditText input_r_content;
    RecyclerView recyclerView;
    private LinearLayout parent;
    private PostInfo postInfo;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        PostInfo postInfo = (PostInfo) getIntent().getSerializableExtra("postInfo");
//        String thispostID = postInfo.getID().toString();
//
//        Log.d(TAG, " this document: " + thispostID);

        //댓글
        input_r_content = (EditText) findViewById(R.id.input_r_content);
        btn_r_write = this.<ImageButton>findViewById(R.id.btn_r_write);
        recyclerView = (RecyclerView) findViewById(R.id.recycler);



        postList = new ArrayList<>();
        cmtAdapter = new CmtAdapter(PostActivity.this, postList);

        RecyclerView recyclerView = findViewById(R.id.recycler);

        recyclerView.setHasFixedSize(true);
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
        String  TAG = "like debug  :: ";
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
                if (postInfo.getLikes().containsKey(currentUserUID)){ //이미 눌렀던 상태
                    Log.d(TAG, "3  ::  ");
                    likebtn.setText("like"); // 다시 라이크로 돌리고
                    postInfo.setLikesCount(postInfo.getLikesCount() - 1); // 좋아요 수 하나빼기
                    postInfo.getLikes().remove(currentUserUID); // 눌럿던거 지우기
                    postDoc.update("likes", null);
                    likeCounter.setText(Integer.toString(postInfo.getLikesCount()));
                    postDoc.update("likesCount", postInfo.getLikesCount() );
                } else { // 좋아요 누르기
                    Log.d(TAG, "4  ::  ");
                    likebtn.setText("liked"); // 좋아요
                    postInfo.setLikesCount(postInfo.getLikesCount() + 1); // 좋아요 수 +
                    postInfo.getLikes().put(currentUserUID,true);
                    postDoc.update("likes", likey);
                    likeCounter.setText(Integer.toString(postInfo.getLikesCount())); // 카운터글씨를 다시 바꿔주기
                    postDoc.update("likesCount", postInfo.getLikesCount() );
                }
            }
        });


        setToolbarTitle(postInfo.getTitle());

    }

    @Override
    protected void onResume(){
        super.onResume();
        postsUpdate();
    }

    private void postsUpdate(){
        if (user != null) {
            CollectionReference collectionReference = firebaseFirestore.collection("comments");
            collectionReference.orderBy("createdAt", Query.Direction.DESCENDING).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                postList.clear();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    postList.add(new PostInfo(
                                            document.getData().get("comment").toString(),
                                            new Date(document.getDate("createdAt").getTime())));

                                }
                                homeAdapter.notifyDataSetChanged();
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }





    //댓글 파이어베이스 연결
    private void post() {
        final String comment = ((EditText) findViewById(R.id.input_r_content)).getText().toString();
        input_r_content.setText(""); //글쓰고 나서 텍스트 창 초기화
        if (comment.length() > 0) {
            //loaderLayout.setVisibility(View.VISIBLE);
            user = FirebaseAuth.getInstance().getCurrentUser();

            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
            String id = getIntent().getStringExtra("id");
            DocumentReference dr;
            if(id == null){
                dr = firebaseFirestore.collection("comments").document();
            }else{
                dr = firebaseFirestore.collection("comments").document(id);
            }
            final DocumentReference documentReference = dr;

            postInfo = new PostInfo(comment, user.getUid(), new Date());
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.post,menu);
        return super.onCreateOptionsMenu(menu);
    }


}
















