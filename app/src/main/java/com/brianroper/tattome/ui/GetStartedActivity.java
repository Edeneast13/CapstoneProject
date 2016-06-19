package com.brianroper.tattome.ui;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.transition.Slide;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.brianroper.tattome.R;
import com.brianroper.tattome.User;
import com.brianroper.tattome.util.NetworkTest;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.facebook.FacebookSdk;

public class GetStartedActivity extends AppCompatActivity {

    /*FIELDS */

    private EditText mEmailEntry;
    private EditText mPasswordEntry;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private GoogleApiClient mGoogleApiClient;
    final int RC_SIGN_IN = 9001;
    private SignInButton mGoogleSignIn;
    private GoogleApiClient.OnConnectionFailedListener mOnConnectionFailedListener;
    private Bundle mBundle;
    private LoginButton mFacebookSignIn;
    private CallbackManager mCallbackManager;

    /*LIFE CYCLE METHODS */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_get_started);

        mEmailEntry = (EditText)findViewById(R.id.emailText);
        mPasswordEntry = (EditText)findViewById(R.id.passwordText);
        mGoogleSignIn = (SignInButton)findViewById(R.id.google_login_button);
        mFacebookSignIn = (LoginButton)findViewById(R.id.facebook_login_button);

        mCallbackManager = CallbackManager.Factory.create();

        mFacebookSignIn.setReadPermissions("email");

        mAuth = FirebaseAuth.getInstance();

        //test to prevent SDKs older than Lollipop from crashing because of transition
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){

            mBundle = ActivityOptions.makeSceneTransitionAnimation(this)
                    .toBundle();
        }

        /*firebase checks here for a currently logged in user if there is one they are
        * redirected passed the login activity of the app */
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
        firebaseAuthWithFacebook();
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
        //disables activity back action
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }

        mCallbackManager.onActivityResult(requestCode, resultCode, data);
}
    /*Login user using google login and authentication*/
    private void handleSignInResult(GoogleSignInResult result){

        Log.d("TAG", "handleSignInResult:" + result.isSuccess());
        if(result.isSuccess()){

            /*if result returns successful the user will be logged into the app with
             * their google account  */
            GoogleSignInAccount account = result.getSignInAccount();
            firebaseAuthWithGoogle(account);
        }
        else{

            Toast.makeText(getApplicationContext(), "Authentication failed",
                    Toast.LENGTH_LONG).show();
        }
    }

    /*BUTTON LISTENERS */

    /*OnClick method for login account button*/
    public void loginAccount(View v){

        if(NetworkTest.activeNetworkCheck(getApplicationContext()) == true){

            String email = mEmailEntry.getText().toString();
            String password = mPasswordEntry.getText().toString();

            User user = new User();
            user.setEmail(email);
            user.setPassword(password);

            authenticateAccountLogin(user.getEmail(), user.getPassword());
        }else{

            Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_network),
                    Toast.LENGTH_LONG).show();
        }
    }

    /*OnClick method for create a new account clickable textview */
    public void signUpIntent(View v){
        Intent i = new Intent(getApplicationContext(), CreateAccountActivity.class);
        startActivity(i);
    }

    /*AUTHENTICATION METHODS */

    /*Account login authentication using firebase */
    public void authenticateAccountLogin(final String username, final String password){

            mAuth.signInWithEmailAndPassword(username, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(Task<AuthResult> task) {

                            if (!task.isSuccessful()) {

                                Toast.makeText(GetStartedActivity.this, "Authentication failed",
                                        Toast.LENGTH_SHORT).show();
                            } else {

                                Toast.makeText(getApplicationContext(), getString(R.string.login_welcome)
                                        , Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(getApplicationContext(), ListActivity.class);
                                intent.putExtra("category", "featured");
                                startActivity(intent, mBundle);
                            }
                        }
                    });
    }

    /*method for logging in using google*/
    public void googleLogin(){

        if(NetworkTest.activeNetworkCheck(getApplicationContext())==true){

            mGoogleSignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                }
            });
        }else{

            Toast.makeText(GetStartedActivity.this, "Authentication failed.",
                    Toast.LENGTH_SHORT).show();
        }

    }
    /*firebase authentication for google login*/
    private void firebaseAuthWithGoogle(GoogleSignInAccount account){

        AuthCredential credential = GoogleAuthProvider
                .getCredential(account.getIdToken(), null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {

                        if (!task.isSuccessful()) {

                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {

                            Intent intent = new Intent(getApplicationContext(), ListActivity.class);
                            intent.putExtra("category", "featured");
                            startActivity(intent, mBundle);
                        }
                    }
                });
    }

    /*firebase authentication for facebook*/
    private void firebaseAuthWithFacebook(){

        mFacebookSignIn.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                handleFacebookAccessToken(loginResult.getAccessToken());

                Intent intent = new Intent(getApplicationContext(), ListActivity.class);
                intent.putExtra("category", "featured");
                startActivity(intent, mBundle);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    /*method for handling returned token from facebook SDK for facebook login */
    private void handleFacebookAccessToken(AccessToken token){

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!task.isSuccessful()) {

                            Toast.makeText(getApplicationContext(),
                                    "Authentication Failed",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
