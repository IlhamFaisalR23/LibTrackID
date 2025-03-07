package com.example.hanyarunrun.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.runtime.livedata.observeAsState
import coil.compose.AsyncImage
import com.example.hanyarunrun.data.DataEntity
import com.example.hanyarunrun.viewmodel.DataViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataListScreen(navController: NavHostController, viewModel: DataViewModel) {
    val dataList by viewModel.dataList.observeAsState(emptyList())
    var searchQuery by remember { mutableStateOf("") }
    var selectedYear by remember { mutableStateOf("Semua") }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<DataEntity?>(null) }

    val years = listOf("Semua") + dataList.map { it.tahun }.distinct().sortedDescending()
    val filteredData = dataList.filter {
        (selectedYear == "Semua" || it.tahun.toString() == selectedYear) &&
                (it.namaProvinsi.contains(searchQuery, true) || it.namaKabupatenKota.contains(searchQuery, true))
    }

    fun confirmDelete() {
        itemToDelete?.let { item ->
            viewModel.deleteData(item)
            itemToDelete = null
        }
        showDeleteConfirmation = false
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Data List", style = MaterialTheme.typography.headlineMedium) }
            )
        },
        bottomBar = {
            BottomAppBar(
                modifier = Modifier.height(80.dp),
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurface,
                actions = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { navController.navigate("form") }) {
                            Icon(Icons.Default.Add, contentDescription = "Tambah Data")
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Tambah Data")
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { navController.navigate("home") }) {
                            Icon(Icons.Default.Home, contentDescription = "Beranda")
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Beranda")
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { navController.navigate("graph") }) {
                            Icon(Icons.Default.BarChart, contentDescription = "Lihat Grafik")
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Lihat Grafik")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                label = { Text("Cari Provinsi atau Kota") },
                shape = RoundedCornerShape(8.dp)
            )

            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                OutlinedTextField(
                    value = selectedYear,
                    onValueChange = {},
                    label = { Text("Pilih Tahun") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    years.forEach { year ->
                        DropdownMenuItem(text = { Text(year.toString()) }, onClick = {
                            selectedYear = year.toString()
                            expanded = false
                        })
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (showDeleteConfirmation) {
                AlertDialog(
                    onDismissRequest = { showDeleteConfirmation = false },
                    title = { Text("Konfirmasi Hapus") },
                    text = { Text("Apakah Anda yakin ingin menghapus data ini?") },
                    confirmButton = {
                        TextButton(onClick = { confirmDelete() }) {
                            Text("Ya")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteConfirmation = false }) {
                            Text("Batal")
                        }
                    }
                )
            }

            if (filteredData.isEmpty()) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Tidak ada data tersedia", fontWeight = FontWeight.Bold)
                        Text("Coba tambahkan data baru!")
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredData) { item ->
                        AnimatedVisibility(visible = true, enter = fadeIn(), exit = fadeOut()) {
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    item.imagePath?.let { imagePath ->
                                        AsyncImage(
                                            model = imagePath,
                                            contentDescription = "Gambar",
                                            modifier = Modifier.fillMaxWidth().height(200.dp)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }
                                    Text("Provinsi: ${item.namaProvinsi}", fontWeight = FontWeight.Bold)
                                    Text("Kabupaten/Kota: ${item.namaKabupatenKota}")
                                    Text("Total: ${item.jumlahPerpustakaan} ${item.satuan}")
                                    Text("Tahun: ${item.tahun}")
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                        IconButton(onClick = { navController.navigate("edit/${item.id}") }) {
                                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                                        }
                                        IconButton(onClick = {
                                            itemToDelete = item
                                            showDeleteConfirmation = true
                                        }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
