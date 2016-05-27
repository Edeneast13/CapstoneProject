package com.brianroper.tattome.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.brianroper.tattome.R;
import com.brianroper.tattome.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CreateAccountActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private EditText mEmailEntry;
    private EditText mPasswordEntry;
    String mEmail ="";
    String mPass = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        mEmailEntry = (EditText)findViewById(R.id.create_email);
        mPasswordEntry = (EditText)findViewById(R.id.create_password);

        mAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user!=null){


                }
                else{


                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthStateListener != null){

            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    public void createAccount(View v){

        setUserCredentials();
    }

    public void setUserCredentials(){

        mEmail = mEmailEntry.getText().toString();
        mPass = mPasswordEntry.getText().toString();

        if((!mEmail.equals(null)) && (!mPass.equals(null))){

            User user = new User();
            user.setEmail(mEmail);
            user.setPassword(mPass);

            firebaseAccountCreation(user.getEmail(), user.getPassword());
        }
    }

    public void firebaseAccountCreation(String username, String password){

        mAuth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {

                        if (!task.isSuccessful()) {
                            Toast.makeText(CreateAccountActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else{

                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.creation_success_toast),
                                    Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(getApplicationContext(), ListActivity.class);
                            intent.putExtra("category", "featured");
                            startActivity(intent);
                        }
                    }
                });
    }
}
