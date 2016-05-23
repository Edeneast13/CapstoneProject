package com.brianroper.tattome;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

        autoLoginWithSharedPref();
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

        Firebase ref = new Firebase(FIREBASE_URL);
        ref.authWithPassword(username, password, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {

                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                sharedPreferences.edit().putString("username", username);
                sharedPreferences.edit().putString("password", password);


                Intent i = new Intent(getApplicationContext(), ListActivity.class);
                i.putExtra("email", mEmailEntry.getText().toString());
                startActivity(i);
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {

                Log.i("LOGIN:", "unsuccessful");
            }
        });
    }

    public void autoLoginWithSharedPref(){

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        String username = sharedPreferences.getString("username", "");
        String password = sharedPreferences.getString("password", "");

        Log.i("username: ", username);

        if(username.length()>0 && password.length()>0){

            mEmailEntry.setText(username);
            mPasswordEntry.setText(password);

            authenticateAccountLogin
                    (mEmailEntry.getText().toString(), mPasswordEntry.getText().toString());
        }
    }
}
