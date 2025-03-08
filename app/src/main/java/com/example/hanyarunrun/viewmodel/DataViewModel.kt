package com.example.hanyarunrun.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.hanyarunrun.data.AppDatabase
import com.example.hanyarunrun.data.DataEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

import com.example.hanyarunrun.data.KotaItem
import com.example.hanyarunrun.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DataViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).dataDao()
    val dataList: LiveData<List<DataEntity>> = dao.getAll()
    private val _kotaList = MutableStateFlow<List<KotaItem>>(emptyList())
    val kotaList: StateFlow<List<KotaItem>> = _kotaList

    fun insertData(
        namaProvinsi: String,
        namaKabupatenKota: String,
        jumlahPerpustakaan: String,
        satuan: String,
        tahun: String,
        imagePath: String? = null
    ) {
        viewModelScope.launch {
            val tahunValue = tahun.toIntOrNull() ?: 0
            dao.insert(
                DataEntity(
                    namaProvinsi = namaProvinsi,
                    namaKabupatenKota = namaKabupatenKota,
                    jumlahPerpustakaan = jumlahPerpustakaan,
                    satuan = satuan,
                    tahun = tahunValue,
                    imagePath = imagePath
                )
            )
        }
    }

    suspend fun uploadImage(imageFile: File): String {
        return "path/to/uploaded/image.jpg"
    }

    fun updateData(data: DataEntity) {
        viewModelScope.launch {
            dao.update(data)
        }
    }

    fun deleteData(data: DataEntity) {
        viewModelScope.launch {
            dao.delete(data)
        }
    }

    suspend fun getDataById(id: Int): DataEntity? {
        return withContext(Dispatchers.IO) {
            dao.getById(id)
        }
    }

    fun checkDatabase() {
        viewModelScope.launch {
            val allData = dao.getAll().value ?: emptyList()
            println("Database contains: ${allData.size} items")
        }
    }

    fun fetchKotaList() {
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.getKotaList()
                _kotaList.value = response.data
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}