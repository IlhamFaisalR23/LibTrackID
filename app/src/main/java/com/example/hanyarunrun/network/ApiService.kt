package com.example.hanyarunrun.network

import com.example.hanyarunrun.data.KotaResponse
import retrofit2.http.GET

interface ApiService {
    @GET("od_kode_wilayah_dan_nama_wilayah_kota_kabupaten")
    suspend fun getKotaList(): KotaResponse
}
