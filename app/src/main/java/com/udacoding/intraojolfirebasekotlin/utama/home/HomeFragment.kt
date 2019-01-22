package com.udacoding.intraojolfirebasekotlin.utama.home


import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

import com.udacoding.intraojolfirebasekotlin.R
import com.udacoding.intraojolfirebasekotlin.utils.GPSTracker
import kotlinx.android.synthetic.main.fragment_home.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import java.util.*
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import android.content.Intent
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import com.google.android.gms.maps.model.LatLngBounds
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.nandohusni.baggit.network.NetworkModule
import com.udacoding.intraojolfirebasekotlin.utama.home.model.Booking
import com.udacoding.intraojolfirebasekotlin.utama.home.model.ResultRoute
import com.udacoding.intraojolfirebasekotlin.utils.ChangeFormat
import com.udacoding.intraojolfirebasekotlin.utils.Constan
import com.udacoding.intraojolfirebasekotlin.utils.DirectionMapsV2
import com.udacoding.intraojolfirebasekotlin.waiting.WaitingDriverActivity
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
import retrofit2.Call
import retrofit2.Response


class HomeFragment : Fragment(), OnMapReadyCallback {

    var latawal : Double? = null
    var lonawal : Double? = null
    var latakhir : Double? = null
    var lonakhir : Double? = null
    var textjarak : String? = null
    var map: GoogleMap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        homeAwal.onClick {
            placeAutocomplite(1)
        }

        homeTujuan.onClick {
            placeAutocomplite(2)
        }

        homebuttonnext.onClick {
            if(homeAwal.text.isNotEmpty() && homeTujuan.text.isNotEmpty()){
                insertserver()
            }else{
                toast("silahkan pilih tujuan Anda")
            }
        }
    }

    private fun insertserver() {
        val currentTime = Calendar.getInstance().time
        val dateNow = currentTime.toString()

        val uid = FirebaseAuth.getInstance().currentUser?.uid

        val booking = Booking()
        booking.uid = uid
        booking.tanggal = dateNow
        booking.lokasiAwal = homeAwal.text.toString()
        booking.lokasiTujuan = homeTujuan.text.toString()
        booking.latAwal = latawal
        booking.latTujuan = latakhir
        booking.lonAwal = lonawal
        booking.lonTujuan = lonakhir
        booking.driver = ""
        booking.status = 1
        booking.harga = homeprice.text.toString()
        booking.jarak = textjarak

        val getdatabase = FirebaseDatabase.getInstance()
        val referenseTable = getdatabase.getReference("Order")
        val key = referenseTable.push().key

        key?.let { referenseTable.child(it).setValue(booking) }

        startActivity<WaitingDriverActivity>(Constan.Key to key.toString())
    }

    //req tujuanawal or tujuan akhir
    fun placeAutocomplite(req : Int){
        try {
            val intent = PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                .build(activity)
            startActivityForResult(intent, req)
        } catch (e: GooglePlayServicesRepairableException) {
            // TODO: Handle the error.
        } catch (e: GooglePlayServicesNotAvailableException) {
            // TODO: Handle the error.
        }
    }

    override fun onResume() {
        super.onResume()

        mapView.onResume()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

//    override fun onDestroy() {
//        super.onDestroy()
//
//        mapView.onDestroy()
//    }
    override fun onMapReady(p0: GoogleMap?) {

        map = p0

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                arrayOf(
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ), 12
            )
        }else{
            showgps()
        }
    }

    private fun showgps() {
        val gps = context?.let { GPSTracker(it) }

        if (gps?.canGetLocation?: true){

            latawal = gps?.latitude
            lonawal = gps?.longitude

            var name = showNameLocation(latawal,lonawal)

            homeAwal.text = name
            showmarker(latawal,lonawal,name)
        }
    }

    //tentuin name location berdasarkan gps
    private fun showNameLocation(lan: Double?, lon: Double?):String {

        //fonversi koordinat menjadi nama tempat
        val geo = Geocoder(context, Locale.getDefault())

        //get  arrray hasil berdasarkan koordinat
        val name = geo.getFromLocation(lan?:0.0,lon?:0.0,1)

        var resultName = name[0].getAddressLine(0)
        var countryName = name[0].countryName
        var cityName = name[0].locale

        return resultName
    }


    private fun showmarker(lan: Double?, lon: Double?, name:String) {

        var latlang = LatLng(lan ?: 0.0, lon?: 0.0)

        //create market
        map?.addMarker(MarkerOptions().position(latlang).title(name))
        //show button zoom
        map?.uiSettings?.isZoomControlsEnabled = true
        //setting camera zoom
        map?.animateCamera(CameraUpdateFactory.newLatLngZoom(latlang,16f))

    }

    //jika permision berhasil
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == 12){
            showgps()
        }
    }

    //place auto complite
     override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //tujuan awal
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                val place = PlaceAutocomplete.getPlace(activity, data)

                latawal = place.latLng.latitude
                lonawal = place.latLng.longitude

                val namelocation = place.address.toString()

                homeAwal.text = namelocation
                if (homeTujuan.text.length > 0){
                    map?.clear()

                    val name = showNameLocation(latawal,lonawal)
                    showmarker(latakhir,lonakhir,name)
                }
                showmarker(latawal,lonawal,namelocation)


            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                val status = PlaceAutocomplete.getStatus(activity, data)
                // TODO: Handle the error.

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }

        //tujuan akhir
        }else if (requestCode == 2){
            val place = PlaceAutocomplete.getPlace(activity, data)

            latakhir = place.latLng.latitude
            lonakhir = place.latLng.longitude

            val namelocation = place.address.toString()
            homeTujuan.text = namelocation

            //clear location
            if (homeAwal.text.length > 0){

                map?.clear()
                val name = showNameLocation(latakhir,lonakhir)
                showmarker(latawal,lonawal,name)
            }
            showmarker(latakhir,lonakhir,namelocation)
            route()
            setBound()
        }
    }

    private fun setBound() {
        val coordinat1 = LatLng(latawal?:0.0,lonawal?:0.0)
        val coordinat2 = LatLng(latakhir?:0.0,lonakhir?:0.0)

        map?.addMarker(MarkerOptions().position(coordinat1).title(("lokasi Awal")))
        map?.addMarker(MarkerOptions().position(coordinat2).title(("Tujuan")))

        var builder = LatLngBounds.builder()
        builder.include(coordinat1)
        builder.include(coordinat2)

        val width = resources.displayMetrics.widthPixels
        val height = resources.displayMetrics.heightPixels
        val padding = (width * 0.12).toInt()

        map?.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinat1,14f))
        map?.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinat2,14f))
        map?.uiSettings?.isZoomControlsEnabled = true
        map?.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(),width,height,padding))


//        var latlangBound = LatLngBounds.builder()
//        latlangBound.include(coordinat1)
//        latlangBound.include(coordinat2)
//
//        map?.animateCamera(CameraUpdateFactory.newLatLngBounds(latlangBound.build(),16))
    }

    fun route(){
        val locationawal = "$latawal,$lonawal"
        val locationakhir = "$latakhir,$lonakhir"

        NetworkModule.getService().route(locationawal,locationakhir,
            activity?.getString(R.string.google_maps_key)?: "")
            .enqueue(object : retrofit2.Callback<ResultRoute>{
                override fun onFailure(call: Call<ResultRoute>, t: Throwable) {

                }

                override fun onResponse(call: Call<ResultRoute>, response: Response<ResultRoute>) {
                    //get Route
                    val route = response.body()?.routes
                    //get object 0
                    val object0 = route?.get(0)

                    //get object overview polyline
                    val overview = object0?.overviewPolyline

                    val legs = object0?.legs

                    textjarak = legs?.get(0)?.distance?.text
                    val jarak = legs?.get(0)?.distance?.value

                    val duration = legs?.get(0)?.duration?.text

                    //get poin
                    val points = overview?.points

                    var jarakKm = Math.ceil(jarak?.toDouble() ?: 0.0)

                    var price = jarakKm / 1000 * 4000

                    homeprice.text = "Rp. "+ChangeFormat.toRupiahFormat2(price.toString())
                    homeWaktudistance.text = duration + "($textjarak)"

                    //buat tampilan jarak
                    points?.let { map?.let { it1 -> DirectionMapsV2.gambarRoute(it1, it) } }
                }
            })
    }
}
