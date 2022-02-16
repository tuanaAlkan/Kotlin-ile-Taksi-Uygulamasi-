package com.inanclab.labtaksiproje.istekler

import android.content.Context
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.bigquery.*
import com.inanclab.labtaksiproje.KonumIstekIki
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

object MapsSorguIki {
    lateinit var sqlsorgusu : String
    lateinit var kimlikbilgileri : GoogleCredentials
    lateinit var drop_off_zone : String

    //assets doyasına erişmek için fonksiyon
    fun kimlikBilgileriniAl(context: Context){
        MapsSorguIki.kimlikbilgileri = GoogleCredentials.fromStream(context.assets.open("airy-adapter-311618-cd7d649dde7e.json"))
    }

    //sql sorgusu için fonksiyon(dinamik olması açısından)
    fun sqlsorgusu(sorgu : String){
        sqlsorgusu = sorgu
    }

    suspend fun sqlsorgusu() {
        val bigquery = BigQueryOptions.newBuilder().setCredentials(kimlikbilgileri)
            .build().service
        val queryConfig = QueryJobConfiguration.newBuilder(sqlsorgusu)
            .setUseLegacySql(false).build() // Use standard SQL syntax for queries.
        val jobId = JobId.of(UUID.randomUUID().toString())
        var queryJob = bigquery.create(JobInfo.newBuilder(queryConfig).setJobId(jobId).build())

        queryJob = queryJob!!.waitFor()

        if (queryJob == null) {
            throw RuntimeException("Job no longer exists")
        } else if (queryJob.status.error != null) {
            // You can also look at queryJob.getStatus().getExecutionErrors() for all
            // errors, not just the latest one.
            throw RuntimeException(queryJob.status.error.toString())
        }
        val response = bigquery.getQueryResults(jobId)
        val result = queryJob.getQueryResults()

        for (row: FieldValueList in result.iterateAll()) {
            val bolgesonuc = row["Zone"].stringValue
            drop_off_zone = bolgesonuc
            println(drop_off_zone)
            CoroutineScope(Dispatchers.Default).launch {
                KonumIstekIki.getLocationInfo(drop_off_zone)
                KonumIstekIki.getLatLong(KonumIstekIki.json)
            }
        }

    }
}