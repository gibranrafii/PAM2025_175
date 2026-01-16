package com.example.juragankost.uicontroller.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.juragankost.R
import com.example.juragankost.modeldata.Kost
import com.example.juragankost.uicontroller.viewmodel.HomeUiState
import com.example.juragankost.uicontroller.viewmodel.HomeViewModel
import com.example.juragankost.uicontroller.viewmodel.provider.PenyediaViewModel

// --- TEMA WARNA LOCAL ---
private val SageDark = Color(0xFF557C55)
private val SageLight = Color(0xFFE8F3E8)
private val SoftTerracotta = Color(0xFFD4A373)
private val SoftCream = Color(0xFFFAFAF5)

@Composable
fun HalamanHome(
    onDetailClick: (String) -> Unit,
    onAddKostClick: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    // Load data setiap kali halaman dibuka
    OnResume { viewModel.getKostList() }

    val uiState = viewModel.homeUiState

    // Hitung total penghuni secara dinamis
    val totalPenghuniSemuaKost = if (uiState is HomeUiState.Success) {
        uiState.kostList.sumOf { it.jumlahPenghuni.toIntOrNull() ?: 0 }
    } else {
        0
    }

    Scaffold(
        containerColor = SoftCream,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddKostClick,
                containerColor = SageDark,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.fab_add_kost))
            }
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // --- HEADER ---
            HomeHeader(
                onLogoutClick = onLogoutClick,
                totalProperti = if (uiState is HomeUiState.Success) uiState.kostList.size.toString() else "0",
                totalPenghuni = totalPenghuniSemuaKost.toString()
            )

            // Judul List
            Text(
                text = stringResource(R.string.home_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4A4A4A),
                modifier = Modifier.padding(start = 24.dp, top = 24.dp, bottom = 12.dp)
            )

            // --- LIST CONTENT ---
            HomeContent(
                uiState = uiState,
                onDetailClick = onDetailClick,
                onDeleteClick = { idKost -> viewModel.deleteKost(idKost) }
            )
        }
    }
}

// --- KOMPONEN HEADER ---
@Composable
fun HomeHeader(
    onLogoutClick: () -> Unit,
    totalProperti: String,
    totalPenghuni: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .background(SageDark)
            .padding(24.dp)
    ) {
        Column {
            // Baris Atas (Sapaan & Logout)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Halo, Juragan", // Bisa masuk strings.xml: greeting_user
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                IconButton(
                    onClick = onLogoutClick,
                    modifier = Modifier.background(Color.White.copy(alpha = 0.2f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = "Logout", // Bisa masuk strings.xml: btn_logout
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // STATISTIK CARDS
            Row(modifier = Modifier.fillMaxWidth()) {
                StatCardSage(
                    title = "Total Properti", // Bisa masuk strings.xml: stat_total_property
                    value = totalProperti,
                    icon = Icons.Outlined.Home,
                    bgColor = Color.White.copy(alpha = 0.2f),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(12.dp))

                StatCardSage(
                    title = "Penghuni Aktif", // Bisa masuk strings.xml: stat_active_tenants
                    value = totalPenghuni,
                    icon = Icons.Outlined.Person,
                    bgColor = SoftTerracotta.copy(alpha = 0.8f),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

// --- KOMPONEN CONTENT (LIST) ---
@Composable
fun HomeContent(
    uiState: HomeUiState,
    onDetailClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit
) {
    when (uiState) {
        is HomeUiState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = SageDark)
            }
        }
        is HomeUiState.Success -> {
            if (uiState.kostList.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.home_empty), color = Color.Gray)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.kostList) { kost ->
                        KostItemAesthetic(
                            kost = kost,
                            onClick = { onDetailClick(kost.idKost) },
                            onDeleteClick = { onDeleteClick(kost.idKost) }
                        )
                    }
                }
            }
        }
        is HomeUiState.Error -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "${stringResource(R.string.error_generic)}: ${uiState.message}",
                    color = SoftTerracotta
                )
            }
        }
    }
}

// --- KOMPONEN KARTU STATISTIK ---
@Composable
fun StatCardSage(title: String, value: String, icon: ImageVector, bgColor: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(110.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(icon, null, tint = Color.White, modifier = Modifier.size(24.dp))
            Column {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}

// --- KOMPONEN ITEM KOST ---
@Composable
fun KostItemAesthetic(kost: Kost, onClick: () -> Unit, onDeleteClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Rumah dalam Kotak Hijau Muda
            Surface(
                modifier = Modifier.size(64.dp),
                shape = RoundedCornerShape(12.dp),
                color = SageLight
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = null,
                        tint = SageDark,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Info Kost
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = kost.namaKost,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D332D)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = kost.alamat,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        maxLines = 1
                    )
                }
            }

            // Tombol Hapus
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.btn_delete),
                    tint = SoftTerracotta
                )
            }
        }
    }
}