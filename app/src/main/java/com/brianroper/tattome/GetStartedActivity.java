package com.brianroper.tattome;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.internal.SignInButtonConfig;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.squareup.picasso.OkHttpDownloader;

public class GetStartedActivity extends AppCompatActivity {

    private EditText mEmailEntry;
    private EditText mPasswordEntry;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private GoogleApiClient mGoogleApiClient;
    final int RC_SIGN_IN = 9001;
    private SignInButton mGoogleSignIn;
    private GoogleApiClient.OnConnectionFailedListener mOnConnectionFailedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started);

        mEmailEntry = (EditText)findViewById(R.id.emailText);
        mPasswordEntry = (EditText)findViewById(R.id.passwordText);
        mGoogleSignIn = (SignInButton)findViewById(R.id.google_login_button);

        mAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user != null){

                    Toast.makeText(getApplicationContext(), getString(R.string.login_welcome)
                            , Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(getApplicationContext(), ListActivity.class);
                    intent.putExtra("category", "featured");
                    startActivity(intent);
                }
                else{

                    Log.i("Status: ", "not logged in");
                }
            }
        };

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestServerAuthCode(getResources().getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, mOnConnectionFailedListener)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        googleLogin();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN){

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result){

        Log.d("TAG", "handleSignInResult:" + result.isSuccess());
        if(result.isSuccess()){

            GoogleSignInAccount account = result.getSignInAccount();
            firebaseAuthWithGoogle(account);
        }
        else{

            Toast.makeText(getApplicationContext(), "Authentication failed",
                    Toast.LENGTH_LONG).show();
        }
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
                            } else {

                                Toast.makeText(getApplicationContext(), getString(R.string.login_welcome)
                                        , Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(getApplicationContext(), ListActivity.class);
                                intent.putExtra("category", "featured");
                                startActivity(intent);
                            }
                        }
                    });
    }

    public void googleLogin(){

        mGoogleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account){

        AuthCredential credential = GoogleAuthProvider
                .getCredential(account.getIdToken(), null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {

                        if(!task.isSuccessful()){

                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else{

                            Intent intent = new Intent(getApplicationContext(), ListActivity.class);
                            intent.putExtra("category", "featured");
                            startActivity(intent);
                        }
                    }
                });
    }
}
