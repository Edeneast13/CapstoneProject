package com.brianroper.tattome;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CreateAccountActivity extends AppCompatActivity {

    private EditText mEmailEntry;
    private EditText mPasswordEntry;
    final String FIREBASE_URL = "https://tattoome.firebaseio.com/";
    String mEmail ="";
    String mPass = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        Firebase.setAndroidContext(this);

        mEmailEntry = (EditText)findViewById(R.id.create_email);
        mPasswordEntry = (EditText)findViewById(R.id.create_password);
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

        Firebase ref = new Firebase(FIREBASE_URL);
        ref.createUser(username, password, new Firebase.ValueResultHandler<Map<String,Object>>() {

            @Override
            public void onSuccess(Map<String, Object> stringObjectMap) {

                Toast.makeText(getApplicationContext(), getString(R.string.creation_success_toast),
                        Toast.LENGTH_LONG).show();

                Intent i = new Intent(getApplicationContext(), ListActivity.class);
                i.putExtra("email", mEmailEntry.getText().toString());
                startActivity(i);
            }

            @Override
            public void onError(FirebaseError firebaseError) {

                Log.i("Creation Failed: ", firebaseError.toString());
            }
        });
    }
}
