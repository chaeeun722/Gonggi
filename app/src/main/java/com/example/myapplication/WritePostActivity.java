package com.example.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;


public class WritePostActivity extends BasicActivity {
    private static final String TAG = "WritePostActivity";
    private FirebaseUser user;
    Uri uri;
    ImageView imageView;
    //    private StorageReference storageRef;
//    private ArrayList<String> pathList = new ArrayList<>();
    private LinearLayout parent;
    private RelativeLayout buttonsBackgroundLayout;
    //    private RelativeLayout loaderLayout; view loader 적용 안함
//    private ImageView selectedImageVIew;
//    private EditText selectedEditText;
    private EditText contentsEditText;
    private EditText titleEditText;
    private PostInfo postInfo;
    private int pathCount, successCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_post);

        parent = findViewById(R.id.contentsLayout);
        buttonsBackgroundLayout = findViewById(R.id.buttonsBackgroundLayout);

//        findViewById(R.id.check).setOnClickListener(onClickListener);
//        findViewById(R.id.image).setOnClickListener(onClickListener);
//        buttonsBackgroundLayout.setOnClickListener(onClickListener);
        buttonsBackgroundLayout.setOnClickListener(onClickListener);
        findViewById(R.id.check).setOnClickListener(onClickListener);
        findViewById(R.id.delete).setOnClickListener(onClickListener);

        setToolbarTitle("게시글 작성");


//        postInfo = (PostInfo) getIntent().getSerializableExtra("postInfo");

//        findViewById(R.id.contentsEditText).setOnFocusChangeListener(onFocusChangeListner); 여러개선택
//        findViewById(R.id.titleEditText).setOnFocusChangeListener(new View.OnFocusChangeListener(){
//            @Override
//            public  void onFocusChange(View v,boolean hasFocus){
//                selected
//            }
//        });


//        Button image = findViewById(R.id.image);
//        imageView = findViewById(R.id.imageView);
//
//
//        image.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(Intent.ACTION_PICK);
//                intent.setType("image/*");
//                startActivityResult.launch(intent);
//            }
//        });


    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (resultCode == Activity.RESULT_OK) {
//                    String profilePath = data.getStringExtra("profilePath"); 유저프로필설정
//                    LinearLayout parent = findViewById(R.id.contentsLayout);
                    ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                    LinearLayout linearLayout = new LinearLayout(WritePostActivity.this);
                    linearLayout.setLayoutParams(layoutParams);
                    linearLayout.setOrientation(LinearLayout.VERTICAL);
                    parent.addView(linearLayout);

//                    if (selectedEditText == null) { 여러개 선택시 수정코드인데 우리한테는 적용안시켜서 노필요
//                         parent.addView(linearLayout);
//                    } else {
//                        for (int i = 0; i< parent.getChildCount(); i++) {
//                            if(parent.getChildAt(i) == selectedEdittext)
//                        }
//                    }
//                    ImageView imageView = new ImageView(WritePostActivity.this);
//                    imageView.setLayoutParams(layoutParams);
//                    Glide.with(this).load(profilePath).override(1000).into(imageView);
//                    linearlayout.addView(imageView); 이미지 파트

                    EditText editText = new EditText(WritePostActivity.this);
                    editText.setLayoutParams(layoutParams);
                    editText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_CLASS_TEXT);
                    editText.setHint("dscryption"); // don't needed
                    linearLayout.addView(editText);
                }
                break;
        }
    }

/* 혜민코드
    ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult
            (new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                                uri = result.getData().getData();
                                try {
                                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                                    imageView.setImageBitmap(bitmap);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    );
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.check:profileUpdate();
                break;
            }
        }
    }; */


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.check:
                    storageUpload();
                    break;
                case R.id.buttonsBackgroundLayout:
                    if (buttonsBackgroundLayout.getVisibility() == View.VISIBLE){
                        buttonsBackgroundLayout.setVisibility(View.GONE);
                    }
                    break;
//                case R.id.delete:
//                    parent.removeView((View)selectedImageVIew.getParent());
//                    buttonsBackgroundLayout.setVisibility(View.GONE);
//                    break;
            }
        }

    };


    /* // 사진 여러개놓고 수정하는 코드
     View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener(){
        @Override
        public void onFocusChange
    };
     */

//    private void storageUpload(){ // 이미지 부분 수정필요 현재 디비연결파트만 사용중
//        final String title = ((EditText) findViewById(R.id.titleEditText)).getText().toString();
//        final String contents = ((EditText) findViewById(R.id.contentsEditText)).getText().toString();
//
//        if(title.length() > 0 && contents.length() > 0){
//            // if view loader needed, add here
////            loaderLayout.setVisibility(View.VISIBLE);
//
//            String contentsList = "";
//            user = FirebaseAuth.getInstance().getCurrentUser();
//            //firebase storage
//            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
////            final DocumentReference documentReference = firebaseFirestore.collection("posts").document();
//
//            String id = getIntent().getStringExtra("id");
//            DocumentReference dr;
//            if(id == null){
//                dr = firebaseFirestore.collection("posts").document();
//            }else{
//                dr = firebaseFirestore.collection("posts").document(id);
//            }
//            final DocumentReference documentReference = dr;
//
//
//            // 이하 코드 추후 수정필요
//            for(int i = 0; i< parent.getChildCount(); i++){
//                LinearLayout linearLayout = (LinearLayout)parent.getChildAt(i);
//                for (int ii = 0; ii < linearLayout.getChildCount(); ii++){
//                    View view = linearLayout.getChildAt(ii);
//                    if (view instanceof EditText){
//                        String text = ((EditText)view).getText().toString();
//                        if (text.length()>0){
//                            contentsList = text;
//                        }
//                    } else { // image upload and storage linking needed
//                        //contentsList.add(pathList).get(pathCount);
//                    }
//                }
//            }
//            // if pathList 가 없기때문에 그냥 써둠 현재 단일 포스트 동작완료
//            PostInfo postInfo = new PostInfo(title, contentsList, user.getUid(), new Date());
//            storeUpload(documentReference, postInfo);
//        }
//        else {startToast("다시입력해주세요.");
//        }
//    }

    private void storageUpload() {
        final String title = ((EditText) findViewById(R.id.titleEditText)).getText().toString();

        if (title.length() > 0) {
            //loaderLayout.setVisibility(View.VISIBLE);
            String contentsList = "";
            user = FirebaseAuth.getInstance().getCurrentUser();

            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
            String id = getIntent().getStringExtra("id");
            DocumentReference dr;
            if(id == null){
                dr = firebaseFirestore.collection("posts").document(title);
            }else{
                dr = firebaseFirestore.collection("posts").document(id);
            }
            final DocumentReference documentReference = dr;

            for(int i = 0; i < parent.getChildCount(); i++){
                LinearLayout linearLayout = (LinearLayout)parent.getChildAt(i);
                for(int ii = 0; ii < linearLayout.getChildCount(); ii++){
                    View view = linearLayout.getChildAt(ii);
                    if(view instanceof EditText){
                        String text = ((EditText)view).getText().toString();
                        if(text.length() > 0){
                            contentsList = text;
                        }
                    } else {
                        //image list
                    }
                }
            }
            postInfo = new PostInfo(title, contentsList, user.getUid(), new Date());
            storeUpload(documentReference, postInfo);
        } else {
            startToast("제목을 입력해주세요.");
        }
    }

    private void storeUpload(DocumentReference documentReference, PostInfo postInfo){
        documentReference.set(postInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!" + documentReference.getId());
//                loaderLayout.setVisibility(View.GONE);
//                Intent resultIntent = new Intent();
//                resultIntent.putExtra("postinfo", postInfo);
//                setResult(Activity.RESULT_OK, resultIntent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
//                        loaderLayout.setVisibility(View.GONE);
                    }
                });
    }
    private void startToast(String msg){Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    private void myStartActivity(Class c, String media, int requestCode) {
        Intent intent = new Intent(this, c);
        intent.putExtra("media", media);
        startActivityForResult(intent, requestCode);
    }
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("글 작성을 취소하시겠습니까?");
        builder.setCancelable(false);
        builder.setPositiveButton("예", new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                exit();
            }
        });

        builder.setNegativeButton("아니요", new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setNeutralButton("취소", new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }
    public void exit() {
        super.onBackPressed();
    }
}