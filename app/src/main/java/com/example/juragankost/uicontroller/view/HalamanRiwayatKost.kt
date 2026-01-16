package com.example.juragankost.uicontroller.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.juragankost.R
import com.example.juragankost.modeldata.Payment
import com.example.juragankost.uicontroller.viewmodel.RiwayatKostViewModel
import com.example.juragankost.uicontroller.viewmodel.provider.PenyediaViewModel
import java.text.NumberFormat
import java.util.Locale

// --- TEMA WARNA LOCAL ---
private val ReportSageDark = Color(0xFF557C55)
private val ReportSoftCream = Color(0xFFFAFAF5)
private val ReportMoneyGreen = Color(0xFF2E7D32)
private val ReportGold = Color(0xFFD4A373)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanRiwayatKost(
    idKost: String,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RiwayatKostViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    // Ambil data saat halaman dibuka
    OnResume { viewModel.getRiwayatByKost(idKost) }

    val uiState = viewModel.uiState

    // Hitung Total Pemasukan Otomatis
    val totalPemasukan = remember(uiState.paymentList) {
        uiState.paymentList.sumOf {
            it.nominal.replace(".", "").replace(",", "").toDoubleOrNull() ?: 0.0
        }
    }
    val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))
    val totalFormatted = formatter.format(totalPemasukan)

    Scaffold(
        containerColor = ReportSoftCream
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // --- HEADER ---
            RiwayatHeader(navigateBack = navigateBack)

            // --- KONTEN UTAMA ---
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
            ) {
                // CARD TOTAL (RINGKASAN)
                RiwayatSummaryCard(totalFormatted = totalFormatted)

                // JUDUL LIST
                Text(
                    text = stringResource(R.string.payment_history_title), // "Riwayat Transaksi"
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4A4A4A),
                    modifier = Modifier.offset(y = (-20).dp)
                )

                // LIST DATA
                RiwayatList(uiState = uiState)
            }
        }
    }
}

// --- KOMPONEN HEADER ---
@Composable
fun RiwayatHeader(navigateBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .background(ReportSageDark)
            .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 0.dp)
    ) {
        Column {
            IconButton(
                onClick = navigateBack,
                modifier = Modifier
                    .background(Color.White.copy(alpha = 0.2f), CircleShape)
                    .size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.btn_back),
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Laporan Keuangan", // Teks spesifik halaman ini
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Rekap transaksi seluruh penghuni",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}

// --- KOMPONEN RINGKASAN ---
@Composable
fun RiwayatSummaryCard(totalFormatted: String) {
    Card(
        modifier = Modifier
            .offset(y = (-40).dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Total Pemasukan",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )
                Text(
                    text = "Rp $totalFormatted",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = ReportMoneyGreen
                )
            }
            Icon(
                imageVector = Icons.Outlined.ShoppingCart,
                contentDescription = null,
                tint = ReportGold,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}

// --- KOMPONEN LIST ---
@Composable
fun RiwayatList(uiState: com.example.juragankost.uicontroller.viewmodel.RiwayatKostUiState) {
    if (uiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxWidth().height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = ReportSageDark)
        }
    } else if (uiState.paymentList.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxWidth().height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.payment_empty), // "Belum ada data pembayaran"
                color = Color.Gray
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.offset(y = (-10).dp),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(uiState.paymentList) { payment ->
                ItemRiwayatKost(payment)
            }
        }
    }
}

// --- KOMPONEN ITEM ---
@Composable
fun ItemRiwayatKost(payment: Payment) {
    val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))
    val nominalFormatted = try {
        formatter.format(payment.nominal.toDouble())
    } catch (e: Exception) {
        payment.nominal
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = Color(0xFFE8F5E9),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.CheckCircle,
                        contentDescription = null,
                        tint = ReportMoneyGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Rp $nominalFormatted",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = ReportMoneyGreen
                )
                Text(
                    text = payment.keterangan,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Black.copy(alpha = 0.7f)
                )
            }
            Text(
                text = payment.tanggalBayar,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
        }
    }
}