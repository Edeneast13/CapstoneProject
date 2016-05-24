package com.brianroper.tattome;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class GetStartedActivity extends AppCompatActivity {

    private EditText mEmailEntry;
    private EditText mPasswordEntry;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started);

        mEmailEntry = (EditText)findViewById(R.id.emailText);
        mPasswordEntry = (EditText)findViewById(R.id.passwordText);

        mAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user != null){

                    Log.i("Status: ", "logged in");
                }
                else{

                    Log.i("Status: ", "not logged in");
                }
            }
        };

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);

        autoLoginWithSharedPref(sharedPreferences);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthStateListener!=null){
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    public void onBackPressed() {

    }

    public void loginAccount(View v){

       String email = mEmailEntry.getText().toString();
       String password = mPasswordEntry.getText().toString();

        User user = new User();
        user.setEmail(email);
        user.setPassword(password);

        authenticateAccountLogin(user.getEmail(), user.getPassword());
    }

    public void signUpIntent(View v){

        Intent i = new Intent(getApplicationContext(), CreateAccountActivity.class);
        startActivity(i);
    }

    public void authenticateAccountLogin(final String username, final String password){

            mAuth.signInWithEmailAndPassword(username, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(Task<AuthResult> task) {

                            if (!task.isSuccessful()) {

                                Toast.makeText(GetStartedActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                            else{

                                Toast.makeText(getApplicationContext(), getString(R.string.login_welcome)
                                    ,Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(getApplicationContext(), ListActivity.class);
                                intent.putExtra("category", "featured");
                                startActivity(intent);
                            }
                        }
                    });
    }

    public void autoLoginWithSharedPref(SharedPreferences sharedPreferences){

        String username = sharedPreferences.getString("username", "");
        String password = sharedPreferences.getString("password", "");

        Log.i("username: ", username);

        if(username.length()>0 && password.length()>0){

            authenticateAccountLogin
                    (username, password);
        }
    }
}
