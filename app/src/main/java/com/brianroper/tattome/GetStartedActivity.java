package com.brianroper.tattome;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GetStartedActivity extends AppCompatActivity {

    private EditText mEmailEntry;
    private EditText mPasswordEntry;
    final String FIREBASE_URL = "https://tattoome.firebaseio.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started);

        Firebase.setAndroidContext(this);

        mEmailEntry = (EditText)findViewById(R.id.emailText);
        mPasswordEntry = (EditText)findViewById(R.id.passwordText);

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

    public void authenticateAccountLogin(String username, String password){

        Firebase ref = new Firebase(FIREBASE_URL);
        ref.authWithPassword(username, password, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {

                Intent i = new Intent(getApplicationContext(), ListActivity.class);
                i.putExtra("email", mEmailEntry.getText().toString());
                startActivity(i);

                System.out.println("User ID: " + authData.getUid() + ", Provider: " + authData.getProvider());
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {

                Log.i("LOGIN:", "unsuccessful");
            }
        });
    }

}
