package com.boroscsaba.commonlibrary.cloudsync

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.boroscsaba.commonlibrary.ApplicationBase
import com.boroscsaba.commonlibrary.AsyncTask
import com.boroscsaba.commonlibrary.LoggingHelper

import com.boroscsaba.commonlibrary.R
import com.boroscsaba.commonlibrary.settings.SettingsHelper
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.fragment_cloud_sync_login.view.*


class CloudSyncLoginFragment : androidx.fragment.app.Fragment() {

    private var googleSignInClient: GoogleSignInClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken((activity?.application as ApplicationBase).getDefaultWebClientId())
                .requestEmail()
                .build()
        googleSignInClient = GoogleSignIn.getClient(activity as Activity, gso)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_cloud_sync_login, container, false)
        view.sign_in_button.setOnClickListener {
            startActivityForResult(googleSignInClient?.signInIntent, 175)
        }
        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 175) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    firebaseAuthWithGoogle(account)
                }
                else {
                    Toast.makeText(activity?.application, "Login error!", Toast.LENGTH_LONG).show()
                    LoggingHelper.logEvent(context!!, "CloudSync_login_null_error")
                }
            } catch (e: ApiException) {
                Toast.makeText(activity?.application, "Login error!", Toast.LENGTH_LONG).show()
                LoggingHelper.logException(e, context!!)
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Toast.makeText(activity?.application, "Login error!", Toast.LENGTH_LONG).show()
                        LoggingHelper.logEvent(context!!,"FirebaseAuth_error")
                    }
                    else {
                        val application = activity?.application
                        if (application != null) {
                            val app = application as ApplicationBase
                            SettingsHelper(app).setCloudSyncState(true)
                            Toast.makeText(app, "Hi ${account.displayName}!", Toast.LENGTH_LONG).show()
                            AsyncTask().execute({
                                app.getCloudSyncService()?.synchronizeData()
                            })
                        }
                    }
                }
    }
}
