package com.example.juragankost.uicontroller.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.juragankost.R
import com.example.juragankost.modeldata.Penghuni
import com.example.juragankost.uicontroller.viewmodel.DetailPenghuniUiState
import com.example.juragankost.uicontroller.viewmodel.DetailPenghuniViewModel
import com.example.juragankost.uicontroller.viewmodel.SubmitStatus
import com.example.juragankost.uicontroller.viewmodel.provider.PenyediaViewModel

// --- TEMA WARNA LOCAL ---
private val DetailSageDark = Color(0xFF557C55)
private val DetailSoftCream = Color(0xFFFAFAF5)
private val DetailTerracotta = Color(0xFFD4A373)
private val DetailTextDark = Color(0xFF2D332D)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanDetailPenghuni(
    navigateBack: () -> Unit,
    onPaymentClick: (String) -> Unit,
    onEditClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DetailPenghuniViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val uiState = viewModel.uiState
    val checkoutStatus = viewModel.checkoutStatus

    // Refresh detail penghuni
    OnResume {
        viewModel.getDetailPenghuni()
    }

    Scaffold(
        containerColor = DetailSoftCream
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- HEADER ---
            DetailPenghuniHeader(
                navigateBack = navigateBack,
                onEditClick = {
                    // Hanya izinkan edit jika data sukses dimuat
                    if (uiState is DetailPenghuniUiState.Success) {
                        onEditClick(uiState.penghuni.idPenghuni)
                    }
                },
                showEditButton = uiState is DetailPenghuniUiState.Success
            )

            // --- KONTEN ---
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                when (uiState) {
                    is DetailPenghuniUiState.Loading -> CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = DetailSageDark
                    )
                    is DetailPenghuniUiState.Error -> Text(
                        text = uiState.message,
                        color = DetailTerracotta,
                        modifier = Modifier.align(Alignment.Center)
                    )
                    is DetailPenghuniUiState.Success -> {
                        PenghuniContent(
                            penghuni = uiState.penghuni,
                            checkoutStatus = checkoutStatus,
                            onPaymentClick = { onPaymentClick(uiState.penghuni.idPenghuni) },
                            onCheckoutClick = { viewModel.checkOut() }
                        )
                    }
                }
            }
        }
    }
}

// --- KOMPONEN HEADER ---
@Composable
fun DetailPenghuniHeader(
    navigateBack: () -> Unit,
    onEditClick: () -> Unit,
    showEditButton: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .background(DetailSageDark)
            .padding(24.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tombol Kembali
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

                // Tombol Edit (Kondisional)
                if (showEditButton) {
                    IconButton(
                        onClick = onEditClick,
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.2f), CircleShape)
                            .size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = stringResource(R.string.edit_penghuni_title),
                            tint = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.detail_penghuni_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Data diri & status hunian", // Bisa dipindah ke strings.xml
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

// --- KOMPONEN UTAMA (CONTENT) ---
@Composable
fun PenghuniContent(
    penghuni: Penghuni,
    checkoutStatus: SubmitStatus,
    onPaymentClick: () -> Unit,
    onCheckoutClick: () -> Unit
) {
    val imageUrl = "http://10.0.2.2/JuraganKostAPI/images/${penghuni.fotoKtpPath}"

    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        // 1. FOTO KTP
        Card(
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = stringResource(R.string.label_foto_ktp),
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 2. DATA DIRI
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                InfoRowSage(
                    Icons.Default.Person,
                    stringResource(R.string.label_nama_penghuni),
                    penghuni.namaLengkap
                )
                Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF0F0F0))
                InfoRowSage(
                    Icons.Default.Phone,
                    stringResource(R.string.label_no_hp),
                    penghuni.noHp
                )
                Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF0F0F0))
                InfoRowSage(
                    Icons.Default.DateRange,
                    stringResource(R.string.label_tgl_masuk),
                    penghuni.tanggalMasuk ?: "-"
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // 3. TOMBOL AKSI
        Button(
            onClick = onPaymentClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DetailSageDark)
        ) {
            Text(stringResource(R.string.btn_manage_payment), fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onCheckoutClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DetailTerracotta),
            enabled = checkoutStatus != SubmitStatus.Loading
        ) {
            if (checkoutStatus == SubmitStatus.Loading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
            } else {
                Text(stringResource(R.string.btn_checkout), fontWeight = FontWeight.Bold)
            }
        }
    }
}

// --- KOMPONEN BARIS INFO ---
@Composable
fun InfoRowSage(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFFF5F9F5),
            modifier = Modifier.size(40.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = DetailSageDark, modifier = Modifier.size(20.dp))
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = DetailTextDark
            )
        }
    }
}