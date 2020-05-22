package com.kitchen.mommaskitchen.Utility;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kitchen.mommaskitchen.R;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AnonymousSignIn {

    public static final String TAG = "AnonymousSignIn";
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    public void login(final Activity mActivity){
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null) {
            mAuth.signInAnonymously()
                    .addOnCompleteListener(mActivity, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInAnonymously:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                addData(mActivity, user.getUid());

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInAnonymously:failure", task.getException());


                            }


                        }
                    });
        }else{
            Log.i(TAG,"User is signed in anonymously with UID " + currentUser.getUid());

        }
    }

    private void addData(Activity mActivity, String uid){
        db = FirebaseFirestore.getInstance();

        final Map<String, Object> docData = new HashMap<>();
        docData.put(mActivity.getString(R.string.date_created), new Timestamp(new Date()));

        final DocumentReference docIdRef = db.collection(mActivity.getString(R.string.users)).document(uid);

        docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        Log.d(TAG, "User exists!");
                    } else {
                        Log.d(TAG, "User does not exist!");

                        docIdRef.set(docData).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.i(TAG, "User ID Successfully set in database");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.i(TAG, "An error occurred in setting uid to database" + e.getMessage());
                            }
                        });
                    }
                }else{
                    Log.d(TAG, "Failed attempt to search user doc ", task.getException());
                }
            }
        });

    }
}
