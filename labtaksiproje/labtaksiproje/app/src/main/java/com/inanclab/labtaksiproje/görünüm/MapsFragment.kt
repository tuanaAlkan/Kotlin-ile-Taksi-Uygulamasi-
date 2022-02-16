package com.inanclab.labtaksiproje.görünüm

import android.graphics.Color
import android.os.AsyncTask
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.gson.Gson
import com.inanclab.labtaksiproje.GoogleMapDTO
import com.inanclab.labtaksiproje.KonumIstek
import com.inanclab.labtaksiproje.KonumIstekIki
import com.inanclab.labtaksiproje.R
import okhttp3.OkHttpClient
import okhttp3.Request

class MapsFragment : Fragment() {
    lateinit var googleMapp: GoogleMap
    private lateinit var latitude_1 : String
    private lateinit var longitute_1 : String
    private lateinit var latitude_2 : String
    private lateinit var longitute_2: String

    private val callback = OnMapReadyCallback { googleMap ->
        googleMapp = googleMap
        longitute_1 = KonumIstek.tpep_zone_long
        latitude_1 = KonumIstek.tpep_zone_lat
        longitute_2 = KonumIstekIki.dropoff_zone_long_2
        latitude_2 = KonumIstekIki.dropoff_zone_lat_2

        val alinmayeri = LatLng(latitude_1.toDouble(),longitute_1.toDouble())
        googleMap.addMarker(MarkerOptions().position(alinmayeri).title("Alinma Yeri"))


        val birakilmayeri = LatLng(latitude_2.toDouble(),longitute_2.toDouble())
        googleMap.addMarker(MarkerOptions().position(birakilmayeri).title("Bırakılma Yeri"))
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(birakilmayeri,13f))

        val URL = getDirectionURL(alinmayeri,birakilmayeri)
        Handler().postDelayed({
            GetDirection(URL).execute()
        },3000)

    }
    //Api isteği için fonksiyon
    fun getDirectionURL(origin:LatLng,dest:LatLng) : String{
        return "https://maps.googleapis.com/maps/api/directions/json?origin=${origin.latitude},${origin.longitude}&destination=${dest.latitude} ,${dest.longitude}&sensor=false&mode=driving&key=AIzaSyD7-BqUi1YZvEXqFWEXLRxTzELxZdb5G94"}

    //yolu çizmek için class
    private inner class GetDirection(val url : String) : AsyncTask<Void, Void, List<List<LatLng>>>(){
        override fun doInBackground(vararg params: Void?): List<List<LatLng>> {

            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val data = response.body()!!.string()
            val result =  ArrayList<List<LatLng>>()
            try{
                val respObj = Gson().fromJson(data, GoogleMapDTO::class.java)

                val path =  ArrayList<LatLng>()

                for (i in 0..(respObj.routes[0].legs[0].steps.size-1)){
                    path.addAll(decodePolyline(respObj.routes[0].legs[0].steps[i].polyline.points))
                }
                result.add(path)
            }catch (e:Exception){
                e.printStackTrace()
            }
            return result
        }

        //yol özellikleri
        override fun onPostExecute(result: List<List<LatLng>>) {
            val lineoption = PolylineOptions()
            for (i in result.indices){
                lineoption.addAll(result[i])
                lineoption.width(14f)
                lineoption.color(Color.BLUE)
                lineoption.geodesic(true)
            }
            googleMapp.addPolyline(lineoption)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    fun decodePolyline(encoded: String): List<LatLng> {

        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val latLng = LatLng((lat.toDouble() / 1E5),(lng.toDouble() / 1E5))
            poly.add(latLng)
        }

        return poly
    }

}