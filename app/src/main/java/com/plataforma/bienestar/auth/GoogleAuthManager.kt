package com.plataforma.bienestar.auth

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class GoogleAuthManager(private val activity: Activity) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    companion object {
        const val RC_SIGN_IN = 9001
    }

    private val googleSignInClient: GoogleSignInClient by lazy {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("93379839080-rsvuh26tjtfsngvnbi1ua22v1h7vevas.apps.googleusercontent.com") // Reemplaza con tu Web Client ID de Firebase
            .requestEmail()
            .build()
        GoogleSignIn.getClient(activity, gso)
    }

    fun signInWithGoogle(onSuccess: (String) -> Unit, onFailure: (String) -> Unit) {
        val signInIntent = googleSignInClient.signInIntent
        activity.startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    fun handleSignInResult(requestCode: Int, data: Intent?, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) {
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!, onSuccess, onFailure)
            } catch (e: ApiException) {
                onFailure("Error al iniciar sesión: ${e.statusCode}")
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String, onSuccess: (String) -> Unit, onFailure: (String) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    onSuccess("Inicio de sesión exitoso: ${user?.displayName}")
                } else {
                    onFailure("Error autenticando en Firebase")
                }
            }
    }
}
