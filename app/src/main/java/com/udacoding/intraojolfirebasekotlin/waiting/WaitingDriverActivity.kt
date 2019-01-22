package com.udacoding.intraojolfirebasekotlin.waiting

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import com.udacoding.intraojolfirebasekotlin.R
import com.udacoding.intraojolfirebasekotlin.trackingDriver.TrackingDriverActivity
import com.udacoding.intraojolfirebasekotlin.utama.HomeActivity
import com.udacoding.intraojolfirebasekotlin.utama.home.model.Booking
import com.udacoding.intraojolfirebasekotlin.utils.Constan
import kotlinx.android.synthetic.main.activity_waiting_driver.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class WaitingDriverActivity : AppCompatActivity() {

    var key: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_waiting_driver)

        pulsator.start()

        key = intent.getStringExtra(Constan.Key)

        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("Order")

        reference.child(key ?: "")
            .addValueEventListener(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    val booking = p0.getValue(Booking::class.java)

                    if(booking?.status == 2){
                        pulsator.stop()
                        startActivity<TrackingDriverActivity>("booking" to booking)
                        toast("terambil")
                    }
                }

            })

        btnCancelOrder.onClick {
            reference.child(key?: "").child("status").setValue(3)
            pulsator.stop()
            startActivity<HomeActivity>()
            toast("Cancel Berhasil")
        }
    }
}
