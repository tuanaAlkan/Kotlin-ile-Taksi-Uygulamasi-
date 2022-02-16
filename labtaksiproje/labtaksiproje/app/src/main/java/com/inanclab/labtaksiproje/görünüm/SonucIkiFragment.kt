package com.inanclab.labtaksiproje.görünüm

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.inanclab.labtaksiproje.R
import com.inanclab.labtaksiproje.adaptorler.SorguIkiRecyclerViewAdapter
import com.inanclab.labtaksiproje.istekler.SorguBir
import kotlinx.android.synthetic.main.fragment_sonuc_iki.*


class SonucIkiFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sonuc_iki, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sonucikirecyclerview.layoutManager = LinearLayoutManager(view.context)
        sonucikirecyclerview.adapter = SorguIkiRecyclerViewAdapter(
            SorguBir.alinmatarihleriliste,
            SorguBir.yolcusayileri,
            SorguBir.mesafelerliste)
    }


}