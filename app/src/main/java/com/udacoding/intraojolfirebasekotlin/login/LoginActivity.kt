package com.udacoding.intraojolfirebasekotlin.login

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Log.d
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.udacoding.intraojolfirebasekotlin.R
import com.udacoding.intraojolfirebasekotlin.auth.AutentikasiHpActivity
import com.udacoding.intraojolfirebasekotlin.signup.SignUpActivity
import com.udacoding.intraojolfirebasekotlin.signup.model.User
import com.udacoding.intraojolfirebasekotlin.utama.HomeActivity
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast


class LoginActivity : AppCompatActivity() {

    var googleSignInClient: GoogleSignInClient? = null

    //deklarasi firebase
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //inisialisasi
        mAuth = FirebaseAuth.getInstance()

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        //button sigupgmail
        signUpbuttonGmail.onClick {
            signIn()
        }
        //button login
        loginSignIn.onClick {

            //chek inputan
            if (loginUsername.text.isEmpty()) {
                loginUsername.requestFocus()
                loginUsername.error = "Inputan tidak boleh kosong"
            } else if (loginPassword.text.isEmpty()) {
                loginPassword.requestFocus()
                loginPassword.error = "Password Tidak boleh Kosong"
            } else {
                mAuth?.signInWithEmailAndPassword(loginUsername.text.toString(), loginPassword.text.toString())
                    ?.addOnCompleteListener { result ->

                        if (result.isSuccessful) {
                            toast("login berhasil")
                            startActivity<HomeActivity>()
                        } else {
                            toast("Login Failed")
                        }
                    }
            }
        }

    }

    private fun signIn() {
        val signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 1) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                //Log.w(TAG, "Google sign in failed", e)
                // ...
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {

        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        mAuth?.signInWithCredential(credential)?.
            addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information

                    mAuth?.currentUser?.uid?.let { chekinsert(it,account)}
                    toast("sigin Gmail Berhasil")

                } else {
                    // If sign in fails, display a message to the user.
                    toast("Sigin Gmail Failed")
                }
            }
    }

    private fun chekinsert(uid: String,account: GoogleSignInAccount ) {
        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("User")

        var query = reference.orderByChild("uid").equalTo(uid)
        query.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                toast("error")

            }
            override fun onDataChange(p0: DataSnapshot) {

                if(p0.value == null){

                    //insert data base
                    var user = User()
                    user.uid = uid
                    user.name = account.displayName
                    user.email = account.email
                    user.hp = ""

                    var key = database.reference.push().key
                    key?.let { reference.child(it).setValue(user) }

                    //toast(key.toString())
                    startActivity<AutentikasiHpActivity>("key" to key.toString())
                }
                else startActivity<HomeActivity>()

            }

        })
    }

}
