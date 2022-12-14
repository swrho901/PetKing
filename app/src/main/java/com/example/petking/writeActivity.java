package com.example.petking;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import java.util.Random;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;

public class writeActivity extends AppCompatActivity {

    Uri uri;
    ImageView loaded_image;

    Button btn_add;
    EditText writeTitle;
    EditText writeContents;
    EditText writeMoney;
    CheckBox cb1;
    Spinner spinner;
    public String user_id, user_ad;
    public int total_read_num;

    private FirebaseDatabase database;
    private DatabaseReference mDatabase;
    FirebaseStorage storage = FirebaseStorage.getInstance(); //???????????? ??????????????? ?????????,
    //???????????? ?????????.

    public writeActivity() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        //?????? ???????????? ??????
        ImageButton btnmanager=(ImageButton)findViewById(R.id.btnmanager);
        btnmanager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent outIntent=new Intent(getApplicationContext(), MainActivity.class);
                setResult(RESULT_OK,outIntent);
                finish();
            }
        });

        // ?????? ????????????
        writeTitle=(EditText)findViewById(R.id.writeTitle);

        // ?????? ????????????
        writeContents=(EditText)findViewById(R.id.writeContents);

        // ??? ????????????
        writeMoney = (EditText)findViewById(R.id.writePrice);

        // ???????????? ????????????
        cb1 = (CheckBox)findViewById(R.id.checkBox);

        // ????????? ??? ????????????
        spinner = (Spinner)findViewById(R.id.spinner);

        // ??????
        btn_add= (Button)findViewById(R.id.btn_add);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(writeActivity.this, "??????", Toast.LENGTH_SHORT).show();
                uploadContents();
                //????????? ????????? 9-24 ???????????? ????????????
                Intent outIntent=new Intent(getApplicationContext(), MainActivity.class);
                setResult(RESULT_OK,outIntent);
                finish();

            }
        });

        // ????????? ?????? ?????? ????????? ????????????
        ImageButton addImage = (ImageButton) findViewById(R.id.add_image);
        loaded_image = findViewById(R.id.loaded_image);
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityResult.launch(intent);
            }
        });

    }

    ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if( result.getResultCode() == RESULT_OK && result.getData() != null){

                        uri = result.getData().getData();

                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                            loaded_image.setImageBitmap(bitmap);

                        }catch (FileNotFoundException e){
                            e.printStackTrace();
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                }
            });

    private void uploadContents() {
        Intent secondIntent = getIntent();
        user_ad = secondIntent.getStringExtra("user_address");
        user_id = secondIntent.getStringExtra("user_id");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        String title = writeTitle.getText().toString();
        String cont = writeContents.getText().toString();
        String spinner_text = spinner.getSelectedItem().toString();
        int nego1 = 0;
        if(cb1.isChecked() == true) {
            nego1 = 1;
        }
        int money1 = Integer.parseInt(writeMoney.getText().toString());
        //String user_address;

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance(); // ?????????????????? ?????????????????? ??????

        Random rand = new Random();
        total_read_num = rand.nextInt(10000000);
        total_read_num++;
        Context context = new Context(total_read_num, title, "?????????", user_ad, cont
                , spinner_text, money1, 0, user_id, nego1);
        mDatabase.child("context").child(Integer.toString(total_read_num)).setValue(context);
       // mDatabase.child("total_num_of_contents").child("num").setValue(Integer.toString(total_read_num));
        StorageReference storageRef = storage.getReference();//??????????????? ????????????
        StorageReference riverRef = storageRef.child(Integer.toString(total_read_num));
        UploadTask uploadTask;
        uploadTask = riverRef.putFile(uri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            }
        });
    }


}