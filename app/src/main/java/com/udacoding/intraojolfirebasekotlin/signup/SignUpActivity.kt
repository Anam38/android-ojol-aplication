package com.udacoding.intraojolfirebasekotlin.signup

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
//import com.google.android.gms.auth.api.signin.GoogleSignInClient
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.auth.FirebaseUser
//import com.google.firebase.database.FirebaseDatabase
import com.udacoding.intraojolfirebasekotlin.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.udacoding.intraojolfirebasekotlin.login.LoginActivity
import com.udacoding.intraojolfirebasekotlin.signup.model.User
import kotlinx.android.synthetic.main.activity_sign_up.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast


class SignUpActivity : AppCompatActivity() {

    //deklarasi firebase
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        //inisialisasi
        mAuth = FirebaseAuth.getInstance()

        //listener button

        signUpbutton.onClick {

            //chek inputan
            if (signUpEmail.text.isEmpty()){
                signUpEmail.requestFocus()
                signUpEmail.error = "inputan tidak boleh kosong"
            }else if(signUpPassword.text.isEmpty()){
                signUpPassword.requestFocus()
                signUpPassword.error = "password tidak boleh kosong"
            }
            else if (signUpPassword.text.length < 6 ){
                signUpPassword.requestFocus()
                signUpPassword.error = "password minimal 6 karakter"
            }
            else if(signUpPassword.text.toString() != signUpPasswordConfirm.text.toString()){
                signUpPasswordConfirm.requestFocus()
                signUpPasswordConfirm.error = "password tidak cocok"
            }
            else{


                mAuth?.createUserWithEmailAndPassword(signUpEmail.text.toString(),signUpPassword.text.toString()
                    )?.addOnCompleteListener{result ->

                    //check response firebase
                    if (result.isSuccessful){

                        tescobainsert(result.result.user.uid)
                        startActivity<LoginActivity>()
                        toast("Signup Berhasil")
                    }
                    else{
                        toast("Sigup failed")
                    }
                }
            }
        }
    }

    fun tescobainsert(uid: String) {
        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("User")
        var user = User()
        user.name = signUpName.text.toString()
        user.email = signUpEmail.text.toString()
        user.hp = signUpHp.text.toString()
        user.uid = uid
        var key = database.reference.push().key

        key?.let { reference.child(it).setValue(user) }
    }

}
