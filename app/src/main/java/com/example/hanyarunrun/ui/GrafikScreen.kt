package com.example.hanyarunrun.ui

import android.graphics.Color
import android.graphics.Typeface
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.hanyarunrun.data.DataEntity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.navigation.NavHostController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.runtime.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GrafikScreen(navController: NavHostController, dataList: List<DataEntity>) {
    val context = LocalContext.current

    val years = dataList.map { it.tahun }.distinct().sortedDescending()
    var selectedYear by remember { mutableStateOf(years.maxOrNull()?.toString() ?: "") }
    var expanded by remember { mutableStateOf(false) }

    val filteredData = dataList.filter { it.tahun.toString() == selectedYear }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Grafik", style = MaterialTheme.typography.headlineMedium) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
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
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { navController.navigate("list") }) {
                            Icon(Icons.Default.ListAlt, contentDescription = "Lihat Data")
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Lihat Data")
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { navController.navigate("home") }) {
                            Icon(Icons.Default.Home, contentDescription = "Beranda")
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Beranda")
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { navController.navigate("form") }) {
                            Icon(Icons.Default.Add, contentDescription = "Tambah Data")
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Tambah Data")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
            if (years.isNotEmpty()) {
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                    OutlinedTextField(
                        value = selectedYear,
                        onValueChange = {},
                        label = { Text("Pilih Tahun") },
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .clickable { expanded = true }
                    )
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        years.forEach { year ->
                            DropdownMenuItem(text = { Text(year.toString()) }, onClick = {
                                selectedYear = year.toString()
                                expanded = false
                            })
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (filteredData.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Tidak ada data tersedia untuk tahun $selectedYear")
                        Text("Tambahkan data untuk melihat grafik.")
                    }
                }
            } else {
                AndroidView(
                    factory = { ctx ->
                        BarChart(ctx).apply {
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            description.isEnabled = false
                            setPinchZoom(true)
                            setDrawBarShadow(false)
                            setDrawGridBackground(false)

                            xAxis.apply {
                                position = XAxis.XAxisPosition.BOTTOM
                                granularity = 1f
                                setDrawGridLines(false)
                                valueFormatter = IndexAxisValueFormatter(filteredData.map { it.namaKabupatenKota })
                            }

                            axisLeft.apply {
                                setDrawGridLines(true)
                                setDrawZeroLine(true)
                            }

                            axisRight.isEnabled = false
                            legend.isEnabled = true
                        }
                    },
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    update = { chart ->
                        val entries = filteredData.mapIndexed { index, item ->
                            BarEntry(index.toFloat(), item.jumlahPerpustakaan.toFloat())
                        }

                        val dataSet = BarDataSet(entries, "Jumlah Perpustakaan ($selectedYear)").apply {
                            setColors(*ColorTemplate.MATERIAL_COLORS)
                            valueTextColor = Color.BLACK
                            valueTextSize = 12f
                            valueTypeface = Typeface.DEFAULT_BOLD
                        }

                        chart.data = BarData(dataSet)
                        chart.notifyDataSetChanged()
                        chart.invalidate()
                    }
                )
            }
        }
    }
}
