package com.inanclab.labtaksiproje

import org.apache.http.HttpEntity
import org.apache.http.HttpResponse
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.DefaultHttpClient
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream

object KonumIstekIki {
    lateinit var json : JSONObject
    var dropoff_zone_long_2 = ""
    var dropoff_zone_lat_2 = ""


    suspend fun getLocationInfo(address: String): JSONObject? {

        var address = address
        var stringBuilder = StringBuilder()
        try {
            address = address.replace(" ".toRegex(), "%20")
            val httppost =
                HttpPost("https://maps.google.com/maps/api/geocode/json?address=$address&sensor=false&key=AIzaSyC2RVg4scegKxpDKpy_kCczQ8z91fqhgrA")
            val client: HttpClient = DefaultHttpClient()
            val response: HttpResponse
            stringBuilder = StringBuilder()
            response = client.execute(httppost)
            val entity: HttpEntity = response.getEntity()
            val stream: InputStream = entity.content
            var b: Int
            while (stream.read().also { b = it } != -1) {
                stringBuilder.append(b.toChar())
            }
        } catch (e: ClientProtocolException) {
        } catch (e: IOException) {
        }
        var jsonObject = JSONObject()
        try {
            jsonObject = JSONObject(stringBuilder.toString())
            json = jsonObject
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return json
    }

    suspend fun getLatLong(jsonObject: JSONObject): Boolean {
        try {
            val longitute = (jsonObject["results"] as JSONArray).getJSONObject(0)
                .getJSONObject("geometry").getJSONObject("location")
                .getDouble("lng").toString()
            val latitude = (jsonObject["results"] as JSONArray).getJSONObject(0)
                .getJSONObject("geometry").getJSONObject("location")
                .getDouble("lat").toString()

            dropoff_zone_long_2 = longitute
            dropoff_zone_lat_2 = latitude

        } catch (e: JSONException) {
            return false
        }
        return true
    }
}