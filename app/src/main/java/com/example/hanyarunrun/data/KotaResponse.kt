package com.example.hanyarunrun.data

data class KotaResponse(
    val data: List<KotaItem>
)

data class KotaItem(
    val bps_kota_nama: String
)
