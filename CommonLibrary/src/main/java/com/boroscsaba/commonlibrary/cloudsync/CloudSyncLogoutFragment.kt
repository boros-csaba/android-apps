package com.boroscsaba.commonlibrary.cloudsync

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.boroscsaba.commonlibrary.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_cloud_sync_logout.view.*
import com.boroscsaba.commonlibrary.ApplicationBase
import com.boroscsaba.commonlibrary.settings.SettingsHelper
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions


class CloudSyncLogoutFragment : Fragment() {

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
        val view = inflater.inflate(R.layout.fragment_cloud_sync_logout, container, false)
        view.sign_out_button.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            googleSignInClient?.signOut()
        }
        view.welcomeMessage.text = resources.getString(R.string.cloud_sync_logged_in_as, FirebaseAuth.getInstance().currentUser?.email)
        val app = activity?.application ?: return view
        view.syncSwitch.isChecked = SettingsHelper(app).isCloudSyncEnabled()
        view.syncSwitch.setOnCheckedChangeListener { _, isChecked ->
            SettingsHelper(app).setCloudSyncState(isChecked)
        }
        return view
    }
}
