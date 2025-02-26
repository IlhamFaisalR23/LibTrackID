package com.example.hanyarunrun.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "perpus_jabar")
data class DataEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val namaProvinsi: String,
    val namaKabupatenKota: String,
    val jumlahPerpustakaan: String,
    val satuan: String,
    val tahun: Int,
    val imagePath: String? = null
)