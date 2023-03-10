package com.nd.jetstreamchat.calling;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nd.jetstreamchat.R;
import com.nd.jetstreamchat.user.ChatActivity;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

@SuppressWarnings("ALL")
public class RingingActivity extends AppCompatActivity {

    String his;
    String room;
    String call;
    MediaPlayer mp;

    private void setDay() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            Drawable background = getResources().getDrawable(R.drawable.gradient_bg);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(android.R.color.transparent));
            window.setBackgroundDrawable(background);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setDay();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ringing);

        mp = MediaPlayer.create(this, R.raw.ringtone);
        mp.start();

        //Room
        room = getIntent().getStringExtra("room");
        his = getIntent().getStringExtra("from");

        //GetRoom
        FirebaseDatabase.getInstance().getReference().child("calling").child(room).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //Type
                String type = Objects.requireNonNull(snapshot.child("type").getValue()).toString();
                call = Objects.requireNonNull(snapshot.child("call").getValue()).toString();
                if (type.equals("cancel")){
                    mp.stop();
                    Toast.makeText(RingingActivity.this, "Canceled", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RingingActivity.this, ChatActivity.class);
                    intent.putExtra("id", his);
                    intent.putExtra("type", "create");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //User

        FirebaseFirestore.getInstance().collection("users").document(his).addSnapshotListener((value, error) -> {

            TextView name = findViewById(R.id.name);
            name.setText(Objects.requireNonNull(value.get("name")).toString());
            CircleImageView dp = findViewById(R.id.dp);
            if (!Objects.requireNonNull(value.get("photo")).toString().isEmpty()){
                Picasso.get().load(Objects.requireNonNull(value.get("photo")).toString()).into(dp);
            }

        });


        findViewById(R.id.end).setOnClickListener(v -> {
            mp.stop();
            Toast.makeText(RingingActivity.this, "Declined", Toast.LENGTH_SHORT).show();
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("type", "dec");
            FirebaseDatabase.getInstance().getReference().child("calling").child(room).updateChildren(hashMap);
            Intent intent = new Intent(RingingActivity.this, ChatActivity.class);
            intent.putExtra("id", his);
            intent.putExtra("type", "create");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.pick).setOnClickListener(v -> {
            mp.stop();
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("type", "ans");
            FirebaseDatabase.getInstance().getReference().child("calling").child(room).updateChildren(hashMap);
            //go to call

            if (call.equals("video")){
                //go to call
                Intent intent = new Intent(RingingActivity.this, VideoChatViewActivity.class);
                intent.putExtra("room", room);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }else {
                //go to call
                Intent intent = new Intent(RingingActivity.this, VoiceChatViewActivity.class);
                intent.putExtra("room", room);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();

            }

        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mp.stop();
        Toast.makeText(RingingActivity.this, "Declined", Toast.LENGTH_SHORT).show();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("type", "dec");
        FirebaseDatabase.getInstance().getReference().child("calling").child(room).updateChildren(hashMap);
        Intent intent = new Intent(RingingActivity.this, ChatActivity.class);
        intent.putExtra("id", his);
        intent.putExtra("type", "create");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}