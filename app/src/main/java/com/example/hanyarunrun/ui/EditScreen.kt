package com.example.hanyarunrun.ui

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.hanyarunrun.viewmodel.DataViewModel
import com.example.hanyarunrun.data.DataEntity

@Composable
fun EditScreen(navController: NavHostController, viewModel: DataViewModel, dataId: Int) {
    val context = LocalContext.current
    var namaProvinsi by remember { mutableStateOf("") }
    var namaKabupatenKota by remember { mutableStateOf("") }
    var jumlahPerpustakaan by remember { mutableStateOf("") }
    var satuan by remember { mutableStateOf("") }
    var tahun by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imagePath by remember { mutableStateOf<String?>(null) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageUri = uri
    }

    LaunchedEffect(dataId) {
        try {
            viewModel.getDataById(dataId)?.let { data ->
                namaProvinsi = data.namaProvinsi
                namaKabupatenKota = data.namaKabupatenKota
                jumlahPerpustakaan = data.jumlahPerpustakaan
                satuan = data.satuan
                tahun = data.tahun.toString()
                imagePath = data.imagePath
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Gagal memuat data", Toast.LENGTH_SHORT).show()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Edit Data Perpustakaan",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = namaProvinsi,
                        onValueChange = { namaProvinsi = it },
                        label = { Text("Nama Provinsi") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = namaKabupatenKota,
                        onValueChange = { namaKabupatenKota = it },
                        label = { Text("Nama Kabupaten/Kota") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = jumlahPerpustakaan,
                        onValueChange = { jumlahPerpustakaan = it },
                        label = { Text("Jumlah Perpustakaan Digital") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = satuan,
                        onValueChange = { satuan = it },
                        label = { Text("Satuan") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = tahun,
                        onValueChange = { tahun = it },
                        label = { Text("Tahun") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            if (imageUri != null || imagePath != null) {
                Image(
                    painter = rememberAsyncImagePainter(imageUri ?: imagePath),
                    contentDescription = "Selected Image",
                    modifier = Modifier
                        .size(200.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            ElevatedButton(
                onClick = { launcher.launch("image/*") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Pick Image")
            }

            Button(
                onClick = {
                    try {
                        val newImagePath = imageUri?.toString() ?: imagePath
                        val updatedData = DataEntity(
                            id = dataId,
                            namaProvinsi = namaProvinsi,
                            namaKabupatenKota = namaKabupatenKota,
                            jumlahPerpustakaan = jumlahPerpustakaan,
                            satuan = satuan,
                            tahun = tahun.toIntOrNull() ?: 0,
                            imagePath = newImagePath
                        )
                        viewModel.updateData(updatedData)
                        Toast.makeText(context, "Data berhasil diupdate!", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(context, "Gagal mengupdate data", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Update Data", color = Color.White)
            }
        }
    }
}
