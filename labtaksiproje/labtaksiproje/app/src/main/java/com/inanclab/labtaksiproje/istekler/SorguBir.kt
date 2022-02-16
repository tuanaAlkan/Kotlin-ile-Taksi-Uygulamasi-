package com.inanclab.labtaksiproje.istekler

import android.content.Context
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.bigquery.*
import java.util.*

object SorguBir {
    lateinit var sqlsorgusu : String
    lateinit var kimlikbilgileri : GoogleCredentials
    var alinmatarihleriliste = mutableListOf<String>()
    var bırakılmatarihleri = mutableListOf<String>()
    var yolcusayileri = mutableListOf<String>()
    var mesafelerliste = mutableListOf<String>()


    //assets doyasına erişmek için fonksiyon
    fun kimlikBilgileriniAl(context: Context){
        kimlikbilgileri = GoogleCredentials.fromStream(context.assets.open("airy-adapter-311618-cd7d649dde7e.json"))
    }

    //sql sorgusu için fonksiyon(dinamik olması açısından)
    fun sqlsorgusu(sorgu : String){
        sqlsorgusu = sorgu
    }

    //Veritabanına istek atmak için fonksiyon

    suspend fun bigQuerySorgusu(){
        //kimlik bilgilerini ayarlıyoruz
        val bigquery = BigQueryOptions.newBuilder().setCredentials(kimlikbilgileri).build().service

        //sorgu yapısını kuruyoruz
        val queryConfig = QueryJobConfiguration.newBuilder(sqlsorgusu).setUseLegacySql(false).build()

        //job için random private id ayarlıyoruz
        val jobId = JobId.of(UUID.randomUUID().toString())

        //queryjob yapısını kuruyoruz // apiye istek atıyoruz
        var queryJob = bigquery.create(JobInfo.newBuilder(queryConfig).setJobId(jobId).build())

        //api isteğinin cevabının dönmesini bekliyoruz
        queryJob = queryJob!!.waitFor()

        //null kontrolü
        if (queryJob == null) {
            throw RuntimeException("Job no longer exists")
        } else if (queryJob.status.error != null) {
            // You can also look at queryJob.getStatus().getExecutionErrors() for all
            // errors, not just the latest one.
            throw RuntimeException(queryJob.status.error.toString())
        }

        //dönen cevabu alıyoruz
        val response = bigquery.getQueryResults(jobId)

        //ve sonuçları alıyoruz
        val result = queryJob.getQueryResults()

        // dönen sonuçları tek tek almak için for döngüsü kullanıyoruz
        for (row: FieldValueList in result.iterateAll()){
            val alinmatarihisonuc = row["alinma_tarihi"].stringValue
            val birakilmatarihisonuc = row["birakilma_tarihi"].stringValue
            val yolcusayilarisonuc = row["passenger_count"].stringValue
            val mesafelersonuc = row["trip_distance"].stringValue
            // ve dönen değerleri tek tek en yukarda yazdığımız boş listelere ekliyoruz
            alinmatarihleriliste.add(alinmatarihisonuc)
            bırakılmatarihleri.add(birakilmatarihisonuc)
            yolcusayileri.add(yolcusayilarisonuc)
            mesafelerliste.add(mesafelersonuc)
        }
    }


}