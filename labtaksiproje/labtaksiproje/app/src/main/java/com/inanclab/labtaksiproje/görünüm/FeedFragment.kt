package com.inanclab.labtaksiproje.görünüm

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.Navigation
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.inanclab.labtaksiproje.R
import com.inanclab.labtaksiproje.istekler.MapSorguBir
import com.inanclab.labtaksiproje.istekler.MapsSorguIki
import com.inanclab.labtaksiproje.istekler.SorguBir
import kotlinx.android.synthetic.main.fragment_feed.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class FeedFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        SorguBir.kimlikBilgileriniAl(view.context)
        MapSorguBir.kimlikBilgileriniAl(view.context)
        MapsSorguIki.kimlikBilgileriniAl(view.context)

        super.onViewCreated(view, savedInstanceState)
        progressBar.visibility = View.GONE
        cardView1.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            SorguBir.mesafelerliste.clear()
            SorguBir.yolcusayileri.clear()
            SorguBir.alinmatarihleriliste.clear()
            SorguBir.bırakılmatarihleri.clear()
            SorguBir.sqlsorgusu("SELECT tpep_dropoff_datetime, DATE(tpep_dropoff_datetime, \"UTC\") as birakilma_tarihi, passenger_count, trip_distance," +
                    "tpep_pickup_datetime," +
                    " DATE(tpep_pickup_datetime, \"UTC\") as alinma_tarihi" +
                    " FROM `airy-adapter-311618.datasettwo.yellow_trip_tada`" +
                    " ORDER BY passenger_count DESC LIMIT 5")
            CoroutineScope(Dispatchers.Default).launch {
                SorguBir.bigQuerySorgusu()
                val action = FeedFragmentDirections.actionFeedFragmentToSonucBirFragment()
                Navigation.findNavController(it).navigate(action)
            }
        }
        cardview22.setOnClickListener {
            SorguBir.mesafelerliste.clear()
            SorguBir.yolcusayileri.clear()
            SorguBir.alinmatarihleriliste.clear()
            SorguBir.bırakılmatarihleri.clear()
            lateinit var gunbir : String
            lateinit var guniki : String
            val popup = MaterialDialog(it.context).noAutoDismiss().customView(R.layout.tarih_secici)
            popup.findViewById<Button>(R.id.pop_up_button_tamam).visibility = View.GONE
            popup.show()
            popup.findViewById<Button>(R.id.tarih_sec_button_ileri).setOnClickListener { view->
                val Gunbir = popup.findViewById<EditText>(R.id.tarih_sec_edit_text)
                if (Gunbir.text.isEmpty() || Gunbir.text.toString().toLong() > 30 || Gunbir.text.toString().toLong() < 0){
                    Toast.makeText(it.context,"Lütfen Geçerli Bir Tarih Girin ",Toast.LENGTH_LONG).show()
                }else{
                    gunbir = Gunbir.text.toString()
                    popup.dismiss()
                    val popupiki = MaterialDialog(it.context).noAutoDismiss().customView(R.layout.tarih_secici)
                    popup.findViewById<Button>(R.id.tarih_sec_button_ileri).visibility = View.GONE
                    popup.findViewById<Button>(R.id.pop_up_button_tamam).visibility = View.VISIBLE
                    popupiki.show()
                    popupiki.findViewById<Button>(R.id.pop_up_button_tamam).setOnClickListener { gorunum ->
                        progressBar.visibility = View.VISIBLE
                        val Guniki = popupiki.findViewById<EditText>(R.id.tarih_sec_edit_text)
                        if (Guniki.text.isEmpty() || Guniki.text.toString().toLong() > 30 || Guniki.text.toString().toLong() < 0 ){
                            Toast.makeText(it.context,"Lütfen Geçerli Bir Tarih Girin ",Toast.LENGTH_LONG).show()
                        }else{
                            guniki = Guniki.text.toString()
                            popupiki.dismiss()
                            SorguBir.sqlsorgusu("SELECT DATE(tpep_pickup_datetime, \"UTC\") as alinma_tarihi," +
                                    " DATE(tpep_dropoff_datetime, \"UTC\") as birakilma_tarihi," +
                                    "trip_distance," +
                                    "passenger_count " +
                                    "FROM `airy-adapter-311618.datasettwo.yellow_trip_tada` " +
                                    "WHERE tpep_pickup_datetime BETWEEN '2020-12-${gunbir}' AND '2020-12-${guniki}' AND trip_distance != 0 ORDER BY trip_distance LIMIT 5 ")
                            CoroutineScope(Dispatchers.Default).launch {
                                SorguBir.bigQuerySorgusu()
                                val action =
                                    FeedFragmentDirections.actionFeedFragmentToSonucIkiFragment()
                                Navigation.findNavController(it).navigate(action)
                            }
                        }
                    }

                }
            }

        }

        cardview3.setOnClickListener {
            val popup = MaterialDialog(it.context).noAutoDismiss().customView(R.layout.tarih_secici)
            popup.findViewById<Button>(R.id.tarih_sec_button_ileri).visibility = View.GONE
            popup.show()
            popup.findViewById<Button>(R.id.pop_up_button_tamam).setOnClickListener { gorunum ->
                val bolgetext = popup.findViewById<EditText>(R.id.tarih_sec_edit_text)
                if (bolgetext.text.isEmpty() || bolgetext.text.toString().toLong() > 30 || bolgetext.text.toString().toLong() < 0){
                    Toast.makeText(it.context,"Lütfen Geçerli Bir Tarih Girin ",Toast.LENGTH_LONG).show()
                }else{
                    progressBar.visibility = View.VISIBLE
                    MapSorguBir.bolge = bolgetext.text.toString()
                    MapSorguBir.sqlsorgusu("SELECT\n" +
                            "  Zone, trip_distance, PULocationID, DOLocationID,\n" +
                            "  tpep_pickup_datetime, DATE(tpep_pickup_datetime, \"UTC\") AS date_converted\n" +
                            "FROM\n" +
                            "  `airy-adapter-311618.datasettwo.yellow_trip_tada` as a\n" +
                            " \n" +
                            "LEFT JOIN\n" +
                            "  `airy-adapter-311618.datasetlabproje.zones` as b\n" +
                            "  ON (\n" +
                            "      a.DOLocationID = b.LocationID \n" +
                            "  )\n" +
                            "WHERE tpep_pickup_datetime = '2020-12-${MapSorguBir.bolge}'\n" +
                            "\n" +
                            "ORDER BY trip_distance DESC\n" +
                            "LIMIT\n" +
                            "  1")
                    MapsSorguIki.drop_off_zone = bolgetext.text.toString()
                    MapsSorguIki.sqlsorgusu("SELECT\n" +
                            "  Zone, trip_distance, PULocationID, DOLocationID,\n" +
                            "  tpep_pickup_datetime, DATE(tpep_pickup_datetime, \"UTC\") AS date_converted\n" +
                            "FROM\n" +
                            "  `airy-adapter-311618.datasettwo.yellow_trip_tada` as a\n" +
                            " \n" +
                            "LEFT JOIN\n" +
                            "  `airy-adapter-311618.datasetlabproje.zones` as b\n" +
                            "  ON (\n" +
                            "      a.PULocationID = b.LocationID \n" +
                            "  )\n" +
                            "WHERE tpep_pickup_datetime = '2020-12-${MapsSorguIki.drop_off_zone}'\n" +
                            "\n" +
                            "ORDER BY trip_distance DESC\n" +
                            "LIMIT\n" +
                            "  1")
                    popup.dismiss()
                    try {
                        CoroutineScope(Dispatchers.Default).launch {
                            MapSorguBir.sqlsorgusu()
                            delay(2000)
                            MapsSorguIki.sqlsorgusu()
                            delay(2000)
                            CoroutineScope(Dispatchers.IO).launch {
                                val action = FeedFragmentDirections.actionFeedFragmentToMapsFragment()
                                Navigation.findNavController(it).navigate(action)
                            }
                        }
                    }catch (e : Exception){
                        e.printStackTrace()
                    }
                }
            }
        }

    }

}