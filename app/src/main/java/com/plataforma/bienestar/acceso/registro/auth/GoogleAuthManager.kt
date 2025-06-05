package com.plataforma.bienestar.acceso.registro.auth

import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.ClearCredentialException
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.Firebase
import com.plataforma.bienestar.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class GoogleAuthManager(private val context: Context, private val coroutineScope: CoroutineScope) {

    private val auth: FirebaseAuth = Firebase.auth
    private val credentialManager: CredentialManager = CredentialManager.create(context)

    fun checkCurrentUser(): FirebaseUser? = auth.currentUser

    fun launchSignIn(
        onSuccess: (FirebaseUser) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(context.getString(R.string.default_web_client_id))
            .setFilterByAuthorizedAccounts(true)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        coroutineScope.launch {
            try {
                val result = credentialManager.getCredential(
                    context = context,
                    request = request
                )
                handleSignIn(result.credential, onSuccess, onFailure)
            } catch (e: GetCredentialException) {
                Log.e(TAG, "Couldn't retrieve user's credentials: ${e.localizedMessage}")
                onFailure("Couldn't retrieve credentials: ${e.localizedMessage}")
            }
        }
    }

    private fun handleSignIn(
        credential: Credential,
        onSuccess: (FirebaseUser) -> Unit,
        onFailure: (String) -> Unit
    ) {
        if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            firebaseAuthWithGoogle(googleIdTokenCredential.idToken, onSuccess, onFailure)
        } else {
            Log.w(TAG, "Credential is not of type Google ID!")
            onFailure("Credential is not of type Google ID!")
        }
    }

    private fun firebaseAuthWithGoogle(
        idToken: String,
        onSuccess: (FirebaseUser) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    if (user != null) {
                        onSuccess(user)
                    } else {
                        onFailure("User is null after successful sign in")
                    }
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    onFailure(task.exception?.localizedMessage ?: "Unknown error")
                }
            }
    }

    fun signOut(onComplete: () -> Unit) {
        auth.signOut()
        coroutineScope.launch {
            try {
                val clearRequest = ClearCredentialStateRequest()
                credentialManager.clearCredentialState(clearRequest)
                onComplete()
            } catch (e: ClearCredentialException) {
                Log.e(TAG, "Couldn't clear user credentials: ${e.localizedMessage}")
                onComplete()
            }
        }
    }

    companion object {
        private const val TAG = "GoogleAuthManager"
    }
}