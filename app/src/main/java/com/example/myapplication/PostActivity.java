package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;


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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;




import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PostActivity extends BasicActivity {

    private FirebaseFirestore firestore;
    String TAG = "post activity :: ";
    private FirebaseUser user;
    private FirebaseFirestore firebaseFirestore;
    Map<String, Boolean> likey = new HashMap<>();
    Map<String, Boolean> isAlreadyliked = new HashMap<>();
    Object likefromdoc;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        PostInfo postInfo = (PostInfo) getIntent().getSerializableExtra("postInfo");

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
                        postInfo.setLikesCount(Integer.parseInt(document.get("likesCount").toString())); // thisData에 디비데이터 넣기
                        likefromdoc = document.get("likes");
                        if (likefromdoc == null){
                            Log.d(TAG, "is already liked ::: no like ");
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
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.post,menu);
        return super.onCreateOptionsMenu(menu);
    }


}

