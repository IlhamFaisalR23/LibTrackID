package com.example.hanyarunrun.ui

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.hanyarunrun.viewmodel.DataViewModel
import java.io.File

@Composable
fun DataEntryScreen(navController: NavHostController, viewModel: DataViewModel) {
    val context = LocalContext.current
    var namaProvinsi by remember { mutableStateOf("") }
    var namaKabupatenKota by remember { mutableStateOf("") }
    var jumlahPerpustakaan by remember { mutableStateOf("") }
    var satuan by remember { mutableStateOf("") }
    var tahun by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageUri = uri
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Input Data Perpustakaan",
                style = MaterialTheme.typography.headlineMedium
            )
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

            Button(
                onClick = { launcher.launch("image/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Pick Image")
            }

            Button(
                onClick = {
                    val imagePath = imageUri?.toString() ?: "path/to/uploaded/image.jpg"

                    viewModel.insertData(
                        namaProvinsi = namaProvinsi,
                        namaKabupatenKota = namaKabupatenKota,
                        jumlahPerpustakaan = jumlahPerpustakaan,
                        satuan = satuan,
                        tahun = tahun,
                        imagePath = imagePath
                    )
                    Toast.makeText(context, "Data berhasil ditambahkan!", Toast.LENGTH_SHORT).show()
                    navController.navigate("list")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit Data")
            }

            Button(
                onClick = { navController.navigate("list") },
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = "List Data")
            }
        }
    }
}