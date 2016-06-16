package com.brianroper.tattome.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.brianroper.tattome.R;
import com.brianroper.tattome.User;
import com.brianroper.tattome.util.NetworkTest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CreateAccountActivity extends AppCompatActivity {

    /*FIELDS */

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private EditText mEmailEntry;
    private EditText mPasswordEntry;
    String mEmail ="";
    String mPass = "";

    /*LIFE CYCLE METHODS*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        mEmailEntry = (EditText)findViewById(R.id.create_email);
        mPasswordEntry = (EditText)findViewById(R.id.create_password);

        mAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {}
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

    /*LISTENER METHODS */

    /* OnClick method for create account button */
    public void createAccount(View v){

        if(NetworkTest.activeNetworkCheck(getApplicationContext())==true){

            setUserCredentials();
        }
        else{

            Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_network),
                    Toast.LENGTH_LONG).show();
        }
    }

    /* ACCOUNT AUTHENTICATION METHODS*/

    /* method for getting user input and sending it to firebase for authentication*/
    public void setUserCredentials(){

        mEmail = mEmailEntry.getText().toString();
        mPass = mPasswordEntry.getText().toString();

        int passLength = mPasswordEntry.getText().length();

        if(passLength < 6){

            Toast.makeText(getApplicationContext(), getString(R.string.pass_length),
                    Toast.LENGTH_LONG).show();
        }

        if(isValidEmail(mEmail) == false){

            Toast.makeText(getApplicationContext(), getString(R.string.invalid_email),
                    Toast.LENGTH_LONG).show();
        }

        if((!mEmail.equals(null)) && (!mPass.equals(null))){

            User user = new User();
            user.setEmail(mEmail);
            user.setPassword(mPass);

            firebaseAccountCreation(user.getEmail(), user.getPassword());
        }else{

            Toast.makeText(getApplicationContext(), getString(R.string.empty_text),
                    Toast.LENGTH_LONG).show();
        }
    }

    /*Email check */
    public boolean isValidEmail(CharSequence email){

        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /* method for create a user account through firebase authentication*/
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
