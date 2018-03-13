package com.kevindom.skeight.activity

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.View
import bind
import com.crashlytics.android.Crashlytics
import com.github.salomonbrys.kodein.android.KodeinAppCompatActivity
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.instance
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.kevindom.skeight.R
import com.kevindom.skeight.auth.AuthManager
import com.kevindom.skeight.databinding.ActivityLoginBinding
import com.kevindom.skeight.firebase.AnalyticsManager
import com.kevindom.skeight.firebase.UserManager
import com.kevindom.skeight.model.User
import com.kevindom.skeight.storage.Preferences
import startAnimation

class LoginActivity : KodeinAppCompatActivity() {

    companion object {
        const val TAG = "LoginActivity"
        const val RC_SIGN_IN = 123
    }

    private val userManager: UserManager by lazy { appKodein().instance<UserManager>() }
    private val preferences: Preferences by lazy { appKodein().instance<Preferences>() }
    private val analyticsManager: AnalyticsManager by lazy { appKodein().instance<AnalyticsManager>() }

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = R.layout.activity_login.bind(this)
        FirebaseApp.initializeApp(this)
    }

    override fun onStart() {
        super.onStart()
        binding.loginBackground.startAnimation(R.drawable.anim_login)
        binding.btnSignIn.startAnimation(R.drawable.anim_google)
        binding.btnSignIn.setOnClickListener {
            AuthManager.signIn(this, RC_SIGN_IN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser
                user?.let {
                    binding.loginLoader.startAnimation(R.drawable.anim_loading, loop = true)
                    userManager.checkIfExists(it.uid) { userExists ->
                        if (userExists) {
                            addRegistrationToken(it.uid)
                        } else {
                            createUser(User(
                                    id = it.uid,
                                    name = it.displayName ?: "Anonymous",
                                    email = it.email ?: "",
                                    phoneNumber = it.phoneNumber,
                                    photoUrl = if (it.photoUrl == null) null else it.photoUrl.toString(),
                                    registrationTokens = mapOf(preferences.getRegistrationToken() to true)
                            ))
                        }
                    }
                }
            } else {
                binding.loginLoader.visibility = View.GONE
                Snackbar.make(binding.root, R.string.general_error_login, Snackbar.LENGTH_SHORT)
            }
        }
    }

    private fun addRegistrationToken(userId: String) {
        userManager.addRegistrationToken(
                userId,
                preferences.getRegistrationToken(),
                completeListener = { onCompleteLogin(userId) },
                failureListener = { onFailedLogin(it) }
        )
    }

    private fun createUser(user: User) {
        userManager.addUser(
                user,
                completeListener = { onCompleteLogin(user.id) },
                failureListener = { onFailedLogin(it) }
        )
    }

    private fun onFailedLogin(exception: Exception) {
        Log.e(TAG, exception.message, exception.cause)
        binding.loginLoader.visibility = View.GONE
        Snackbar.make(binding.root, R.string.general_error_login, Snackbar.LENGTH_SHORT)
    }

    private fun onCompleteLogin(userId: String) {
        analyticsManager.logSignIn(userId)
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        finish()
        startActivity(intent)
    }
}