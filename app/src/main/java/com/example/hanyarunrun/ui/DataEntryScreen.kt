package com.example.hanyarunrun.ui

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.hanyarunrun.viewmodel.DataViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataEntryScreen(navController: NavHostController, viewModel: DataViewModel = viewModel()) {
    val context = LocalContext.current
    var namaProvinsi by remember { mutableStateOf(TextFieldValue("")) }
    var jumlahPerpustakaan by remember { mutableStateOf(TextFieldValue("")) }
    var tahun by remember { mutableStateOf(TextFieldValue("")) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imagePath by remember { mutableStateOf<String?>(null) }
    var showError by remember { mutableStateOf(false) }
    var selectedKota by remember { mutableStateOf("Pilih Kota") }
    var expanded by remember { mutableStateOf(false) }

    val kotaList by viewModel.kotaList.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchKotaList()
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val savedPath = saveImageToInternalStorage(context, it)
            imagePath = savedPath
            imageUri = uri
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Tambah Data Perpustakaan", style = MaterialTheme.typography.headlineSmall) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("list") }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = namaProvinsi,
                onValueChange = { namaProvinsi = it },
                label = { Text("Nama Provinsi") },
                modifier = Modifier.fillMaxWidth(),
                isError = showError && namaProvinsi.text.isEmpty()
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                OutlinedTextField(
                    value = selectedKota,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Nama Kabupaten/Kota") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    isError = showError && selectedKota == "Pilih Kota"
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    kotaList.forEach { kota ->
                        DropdownMenuItem(
                            text = { Text(kota.bps_kota_nama) },
                            onClick = {
                                selectedKota = kota.bps_kota_nama
                                expanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = jumlahPerpustakaan,
                onValueChange = { jumlahPerpustakaan = it },
                label = { Text("Jumlah Perpustakaan Digital") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                isError = showError && jumlahPerpustakaan.text.isEmpty()
            )

            OutlinedTextField(
                value = tahun,
                onValueChange = { tahun = it },
                label = { Text("Tahun") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                isError = showError && tahun.text.isEmpty()
            )

            if (imageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(imageUri),
                    contentDescription = "Selected Image",
                    modifier = Modifier
                        .size(200.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Belum Memilih Gambar", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            ElevatedButton(onClick = { launcher.launch("image/*") }) {
                Text("Unggah Gambar")
            }

            if (showError) {
                Text(
                    "Semua kolom wajib diisi!",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp
                )
            }

            Button(onClick = {
                if (namaProvinsi.text.isEmpty() || selectedKota == "Pilih Kota" ||
                    jumlahPerpustakaan.text.isEmpty() || tahun.text.isEmpty()) {
                    showError = true
                } else {
                    viewModel.insertData(
                        namaProvinsi.text, selectedKota, jumlahPerpustakaan.text, "UNIT", tahun.text, imagePath
                    )
                    navController.navigate("list")
                }
            }) {
                Text("Simpan Data")
            }
        }
    }
}

fun saveImageToInternalStorage(context: Context, uri: Uri): String? {
    val directory = File(context.filesDir, "images")
    if (!directory.exists()) {
        directory.mkdirs()
    }
    val file = File(directory, "image_${System.currentTimeMillis()}.jpg")
    return try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()
        file.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
