package com.alphabet.chitchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegistrationActivity extends AppCompatActivity {
    private EditText username, email, password, conpassword;
    private Button register;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private TextView login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        //toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //initiatization
        username = findViewById(R.id.etusername);
        email = findViewById(R.id.etemail);
        password = findViewById(R.id.etpass_reg);
        conpassword = findViewById(R.id.etconpass_reg);
        firebaseAuth = FirebaseAuth.getInstance();
        login = findViewById(R.id.tvlogin);
        register = findViewById(R.id.btnregis);
        //onclicks
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_username = username.getText().toString();
                String txt_email = email.getText().toString();
                String txt_password = password.getText().toString();
                String txt_conpassword = conpassword.getText().toString();
                if (TextUtils.isEmpty(txt_username) || TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password) | TextUtils.isEmpty(txt_conpassword)) {
                    Toast.makeText(RegistrationActivity.this, "All fields are required", Toast.LENGTH_LONG).show();
                } else if (txt_password.length() < 8) {
                    Toast.makeText(RegistrationActivity.this, "password must be atleast 8 characters", Toast.LENGTH_LONG).show();
                } else if (!txt_password.equals(txt_conpassword)) {
                    Toast.makeText(RegistrationActivity.this, "password mismatch", Toast.LENGTH_LONG).show();
                } else
                    Register(txt_username, txt_email, txt_password);
            }
        });
        //here to code


    }

    //functions in activity
    private void Register(final String username, String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    String userid = firebaseUser.getUid();

                    databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("id", userid);
                    hashMap.put("username", username);
                    hashMap.put("imageURL", "default");

                    databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                                Toast.makeText(RegistrationActivity.this, "Registration success", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(RegistrationActivity.this, "Registration failed", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
