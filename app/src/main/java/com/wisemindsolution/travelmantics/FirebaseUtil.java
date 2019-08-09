package com.wisemindsolution.travelmantics;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FirebaseUtil {
    final static String TAG = "FirebaseUtil";
    private final static int RC_SIGN_IN = 1;
    public static FirebaseDatabase firebaseDatabase;
    public static DatabaseReference databaseReference;
    public static FirebaseUtil firebaseUtil;
    public static FirebaseStorage mStorage;
    public static StorageReference mStorageRef;
    public static FirebaseAuth mAuth;
    public static FirebaseAuth.AuthStateListener mAuthStateListener;
    public static ArrayList<TravelDealModel> mDeals;

    private FirebaseUtil(){ }

    public static void openFbReference(String ref, final Activity callerActivity) {
        Log.i(TAG, "firebase util...................");

        if(firebaseUtil == null) {
            firebaseUtil = new FirebaseUtil();
            firebaseDatabase = FirebaseDatabase.getInstance();
            mAuth = FirebaseAuth.getInstance();
            mAuthStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                   if(mAuth.getCurrentUser() == null ){
                       signIn(callerActivity);
                       Toast.makeText(callerActivity, "Welcome", Toast.LENGTH_LONG).show();
                   }

                }
            };
        }
        mDeals = new ArrayList<>();
        databaseReference = firebaseDatabase.getReference().child(ref);
        connectStorage();
    }

    public static void attachListener(){
        mAuth.addAuthStateListener(mAuthStateListener);
    }
    public static void detatchListener(){
        mAuth.removeAuthStateListener(mAuthStateListener);
    }
    private static void signIn(Activity activity){
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

// Create and launch sign-in intent
      activity.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);

    }
    private static void connectStorage(){
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference().child("deals_pictures");
    }
}
