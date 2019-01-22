package com.udacoding.intraojolfirebasekotlin.auth

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.FirebaseDatabase
import com.udacoding.intraojolfirebasekotlin.R
import kotlinx.android.synthetic.main.activity_autentikasi_hp.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.toast

class AutentikasiHpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_autentikasi_hp)

        val key = intent.getStringExtra("key")

        toast(key)
        authentikasisubmit.onClick {

            var database = FirebaseDatabase.getInstance()
            var reference = database.getReference("User")

            reference.child(key).child("hp").setValue(authentikasinomorhp.text.toString())
        }

    }
}
