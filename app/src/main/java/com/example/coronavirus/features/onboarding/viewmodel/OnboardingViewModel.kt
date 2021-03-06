package com.example.coronavirus.features.onboarding.viewmodel

import android.app.Activity
import android.app.Application
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.MutableLiveData
import com.example.coronavirus.adapter.BaseViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import javax.inject.Inject

class OnboardingViewModel : BaseViewModel() {
    @Inject
    lateinit var apps : Application

    lateinit var loginLauncher : ActivityResultLauncher<Intent>
    val loginStatus = MutableLiveData<Boolean>()

    fun prepareSignIn(token : String) : GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(token)
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(apps.applicationContext, gso)
    }

    fun firebaseAuthWithGoogle(idToken : String, auth : FirebaseAuth, activity : Activity) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    loginStatus.postValue(true)
                } else {
                    loginStatus.postValue(false)
                }
            }
    }
}