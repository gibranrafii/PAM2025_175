package com.example.juragankost.uicontroller.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.juragankost.R
import com.example.juragankost.modeldata.Kamar
import com.example.juragankost.uicontroller.viewmodel.DetailKostViewModel
import com.example.juragankost.uicontroller.viewmodel.DetailUiState
import com.example.juragankost.uicontroller.viewmodel.provider.PenyediaViewModel
import java.text.NumberFormat
import java.util.Locale

// --- TEMA WARNA LOCAL ---
private val DetailKostSageDark = Color(0xFF557C55)
private val DetailKostSoftCream = Color(0xFFFAFAF5)
private val DetailKostSoftTerracotta = Color(0xFFD4A373)
private val DetailKostMutedGreen = Color(0xFF6A9C6A)
private val DetailKostTextDark = Color(0xFF2D332D)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanDetailKost(
    navigateBack: () -> Unit,
    onAddKamarClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onEditKostClick: (String) -> Unit,
    onKamarClick: (Kamar) -> Unit,
    onEditKamarClick: (String) -> Unit,
    idKost: String,
    modifier: Modifier = Modifier,
    viewModel: DetailKostViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    // Load Data Kamar saat halaman dibuka
    OnResume { viewModel.getKamarList() }

    Scaffold(
        containerColor = DetailKostSoftCream,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddKamarClick,
                containerColor = DetailKostSageDark,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.fab_add_kamar))
            }
        }
    ) { innerPadding ->
        Column(
            modifier = modifier.padding(innerPadding).fillMaxSize()
        ) {
            // --- HEADER ---
            DetailKostHeader(
                navigateBack = navigateBack,
                onEditKostClick = { onEditKostClick(idKost) },
                onHistoryClick = onHistoryClick
            )

            // --- GRID KAMAR ---
            val uiState = viewModel.detailUiState
            Box(modifier = Modifier.fillMaxSize()) {
                when (uiState) {
                    is DetailUiState.Loading -> CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = DetailKostSageDark
                    )
                    is DetailUiState.Success -> {
                        if (uiState.kamarList.isEmpty()) {
                            Column(
                                modifier = Modifier.align(Alignment.Center),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(stringResource(R.string.dashboard_empty), color = Color.Gray)
                            }
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                contentPadding = PaddingValues(24.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(uiState.kamarList) { kamar ->
                                    ItemKamarSage(
                                        kamar = kamar,
                                        onClick = { onKamarClick(kamar) },
                                        onDeleteClick = { viewModel.deleteKamar(kamar.idRoom) },
                                        onEditClick = { idRoom -> onEditKamarClick(idRoom) }
                                    )
                                }
                            }
                        }
                    }
                    is DetailUiState.Error -> Text(
                        text = uiState.message,
                        modifier = Modifier.align(Alignment.Center),
                        color = DetailKostSoftTerracotta
                    )
                }
            }
        }
    }
}

// --- KOMPONEN HEADER ---
@Composable
fun DetailKostHeader(
    navigateBack: () -> Unit,
    onEditKostClick: () -> Unit,
    onHistoryClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .background(DetailKostSageDark)
            .padding(24.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tombol Back
                IconButton(
                    onClick = navigateBack,
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                        .size(40.dp)
                ) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = stringResource(R.string.btn_back),
                        tint = Color.White
                    )
                }

                // Tombol Kanan (Edit & History)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = onEditKostClick,
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.2f), CircleShape)
                            .size(40.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Kost", tint = Color.White)
                    }
                    IconButton(
                        onClick = onHistoryClick,
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.2f), CircleShape)
                            .size(40.dp)
                    ) {
                        Icon(Icons.Default.DateRange, contentDescription = "Riwayat", tint = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.dashboard_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Kelola ketersediaan & penghuni", // Bisa dipindah ke strings.xml: dashboard_subtitle
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

// --- KOMPONEN KARTU KAMAR (ITEM GRID) ---
@Composable
fun ItemKamarSage(
    kamar: Kamar,
    onClick: () -> Unit,
    onDeleteClick: (String) -> Unit,
    onEditClick: (String) -> Unit
) {
    val isTerisi = kamar.isOccupied == "1"
    val statusColor = if (isTerisi) DetailKostSoftTerracotta else DetailKostMutedGreen
    val statusText = if (isTerisi) stringResource(R.string.status_occupied) else stringResource(R.string.status_available)
    val statusBg = if (isTerisi) DetailKostSoftTerracotta.copy(alpha = 0.15f) else DetailKostMutedGreen.copy(alpha = 0.15f)

    val hargaFormatted = try {
        val number = kamar.harga.toDouble()
        NumberFormat.getNumberInstance(Locale("id", "ID")).format(number)
    } catch (e: Exception) { kamar.harga }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Baris Judul: Nomor Kamar (Kiri) & Tombol (Kanan)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = kamar.nomorKamar,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = DetailKostTextDark
                    )

                    Row {
                        // Tombol Edit
                        IconButton(
                            onClick = { onEditClick(kamar.idRoom) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                null,
                                tint = DetailKostSageDark,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        // Tombol Hapus
                        IconButton(
                            onClick = { onDeleteClick(kamar.idRoom) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Outlined.Delete,
                                null,
                                tint = Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Harga
                Text(
                    text = "Rp $hargaFormatted",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Label Status
                Surface(
                    color = statusBg,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier.padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = statusText,
                            color = statusColor,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}