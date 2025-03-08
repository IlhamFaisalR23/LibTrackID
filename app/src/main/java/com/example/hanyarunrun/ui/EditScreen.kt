package com.example.hanyarunrun.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.hanyarunrun.viewmodel.DataViewModel
import com.example.hanyarunrun.data.DataEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreen(navController: NavHostController, viewModel: DataViewModel, dataId: Int) {
    val context = LocalContext.current
    var namaProvinsi by remember { mutableStateOf("") }
    var namaKabupatenKota by remember { mutableStateOf("") }
    var jumlahPerpustakaan by remember { mutableStateOf("") }
    var tahun by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imagePath by remember { mutableStateOf<String?>(null) }
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }

    var satuan by remember { mutableStateOf("UNIT") }
    var isSatuanExpanded by remember { mutableStateOf(false) }
    val satuanOptions = listOf("UNIT")

    var isKotaExpanded by remember { mutableStateOf(false) }
    val kotaList by viewModel.kotaList.collectAsState()

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
            snackbarMessage = "Gagal memuat data"
            showSnackbar = true
        }
    }

    LaunchedEffect(Unit) {
        viewModel.fetchKotaList()
        println("Fetching Kota List...")
    }

    LaunchedEffect(kotaList) {
        println("Kota List Updated: $kotaList")
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Edit Data Perpustakaan", style = MaterialTheme.typography.headlineSmall) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
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
            // Input Fields
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = namaProvinsi,
                        onValueChange = { namaProvinsi = it },
                        label = { Text("Nama Provinsi") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    ExposedDropdownMenuBox(
                        expanded = isKotaExpanded,
                        onExpandedChange = { isKotaExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = namaKabupatenKota,
                            onValueChange = {},
                            label = { Text("Nama Kabupaten/Kota") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isKotaExpanded) }
                        )
                        ExposedDropdownMenu(
                            expanded = isKotaExpanded,
                            onDismissRequest = { isKotaExpanded = false }
                        ) {
                            kotaList.forEach { kota ->
                                DropdownMenuItem(
                                    text = { Text(kota.bps_kota_nama) },
                                    onClick = {
                                        namaKabupatenKota = kota.bps_kota_nama
                                        isKotaExpanded = false
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
                        modifier = Modifier.fillMaxWidth()
                    )
                    ExposedDropdownMenuBox(
                        expanded = isSatuanExpanded,
                        onExpandedChange = { isSatuanExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = satuan,
                            onValueChange = {},
                            label = { Text("Satuan") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            readOnly = true
                        )
                        ExposedDropdownMenu(
                            expanded = isSatuanExpanded,
                            onDismissRequest = { isSatuanExpanded = false }
                        ) {
                            satuanOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        satuan = option
                                        isSatuanExpanded = false
                                    }
                                )
                            }
                        }
                    }
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

            ElevatedButton(
                onClick = { launcher.launch("image/*") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Unggah Gambar")
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
                        snackbarMessage = "Data berhasil diupdate!"
                        showSnackbar = true
                        navController.popBackStack()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        snackbarMessage = "Gagal mengupdate data"
                        showSnackbar = true
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Update Data", color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }

    if (showSnackbar) {
        LaunchedEffect(showSnackbar) {
            showSnackbar = false
        }
        Snackbar(
            modifier = Modifier.padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            action = {
                TextButton(onClick = { showSnackbar = false }) {
                    Text("OK", color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        ) {
            Text(snackbarMessage, color = MaterialTheme.colorScheme.onPrimary)
        }
    }
}