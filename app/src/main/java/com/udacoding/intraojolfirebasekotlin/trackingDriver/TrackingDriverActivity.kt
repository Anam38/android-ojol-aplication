package com.udacoding.intraojolfirebasekotlin.trackingDriver

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.udacoding.intraojolfirebasekotlin.R
import com.udacoding.intraojolfirebasekotlin.trackingDriver.model.driver
import com.udacoding.intraojolfirebasekotlin.utama.home.model.Booking
import kotlinx.android.synthetic.main.fragment_home.*

class TrackingDriverActivity : AppCompatActivity(), OnMapReadyCallback {

    var mMap : GoogleMap? = null
    var booking : Booking? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracking_driver)

        homebuttonnext.text = "home"
        booking = intent.getSerializableExtra("booking") as Booking

        homeAwal.text = booking?.lokasiAwal.toString()
        homeTujuan.text = booking?.lokasiTujuan.toString()
        homeprice.text = booking?.harga
        homeWaktudistance.text = booking?.jarak

        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(p0: GoogleMap?) {

        mMap = p0
        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("Driver")
        val query = reference.orderByChild("uid").equalTo(booking?.driver)
        query.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                for (issue in p0.children){
                    val driver = issue.getValue(driver::class.java)

                    showmakerDriver(driver)
                }
            }
        })
    }

    private fun showmakerDriver(driver: driver?) {
        val posisi = LatLng(driver?.lat ?: 0.0, driver?.lon ?: 0.0 )
        mMap?.clear()
        mMap?.addMarker(MarkerOptions()
            .position(posisi)
            .title("Your Driver")
            .snippet(driver?.name))
        mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(posisi,14f))
    }
}
