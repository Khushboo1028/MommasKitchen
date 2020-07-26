package com.kitchen.mommaskitchen.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;

import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.appupdate.testing.FakeAppUpdateManager;

import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;


import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.kitchen.mommaskitchen.R;
import com.kitchen.mommaskitchen.Fragment.RecipeFragment;
import com.kitchen.mommaskitchen.Fragment.SavedFragment;
import com.kitchen.mommaskitchen.Utility.AnonymousSignIn;
import com.kitchen.mommaskitchen.Utility.DefaultTextConfig;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    public static final String TAG = "MainActivity";
    public static final String CHANNEL_ID = "NOTIF";
    public static final String CHANNEL_NAME = "Notifications";
    public static final String CHANNEL_DESC = "This channel is for all notifications";

    private AppUpdateManager mAppUpdateManager;
    private static final int RC_APP_UPDATE = 11;

    InstallStateUpdatedListener installStateUpdatedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), MainActivity.this);
        setContentView(R.layout.activity_main);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        init();
        //Notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(CHANNEL_DESC);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);

        }

        loadFragment(new RecipeFragment());


    }

    private void init() {
        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(this);

    }

    private void popupSnackbarForCompleteUpdate() {

        Snackbar snackbar =
                Snackbar.make(
                        findViewById(R.id.main_rel),
                        "Momma's Kitchen is updated!",
                        Snackbar.LENGTH_INDEFINITE);

        snackbar.setAction("Install", view -> {
            if (mAppUpdateManager != null) {
                mAppUpdateManager.completeUpdate();
            }
        });


        snackbar.setActionTextColor(getResources().getColor(R.color.white));
        snackbar.show();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;

        switch (item.getItemId()) {
            case R.id.saved:
                fragment = new SavedFragment();
                break;

            case R.id.recipes:
                fragment = new RecipeFragment();
                break;
        }
        return loadFragment(fragment);
    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_APP_UPDATE) {
            if (resultCode != RESULT_OK) {
                Log.e(TAG, "onActivityResult: app download failed");
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        AnonymousSignIn anonymousSignIn = new AnonymousSignIn();
        anonymousSignIn.login(this);


        installStateUpdatedListener = new InstallStateUpdatedListener() {
            @Override
            public void onStateUpdate(InstallState state) {
                if (state.installStatus() == InstallStatus.DOWNLOADED) {
                    //CHECK THIS if AppUpdateType.FLEXIBLE, otherwise you can skip
                    popupSnackbarForCompleteUpdate();
                } else if (state.installStatus() == InstallStatus.INSTALLED) {
                    if (mAppUpdateManager != null) {
                        mAppUpdateManager.unregisterListener(installStateUpdatedListener);
                    }

                } else {
                    Log.i(TAG, "InstallStateUpdatedListener: state: " + state.installStatus());
                }
            }
        };

        mAppUpdateManager = AppUpdateManagerFactory.create(this);

        mAppUpdateManager.registerListener(installStateUpdatedListener);

        mAppUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {

            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE /*AppUpdateType.IMMEDIATE*/)) {

                try {
                    mAppUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo, AppUpdateType.FLEXIBLE /*AppUpdateType.IMMEDIATE*/, MainActivity.this, RC_APP_UPDATE);

                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }

            } else if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                //CHECK THIS if AppUpdateType.FLEXIBLE, otherwise you can skip
                popupSnackbarForCompleteUpdate();
            } else {
                Log.e(TAG, "checkForAppUpdateAvailability: something else");
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAppUpdateManager != null) {
            mAppUpdateManager.unregisterListener(installStateUpdatedListener);
        }
    }
}
