package com.example.juragankost.uicontroller.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
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
import com.example.juragankost.uicontroller.viewmodel.EditKostViewModel
import com.example.juragankost.uicontroller.viewmodel.SubmitStatus
import com.example.juragankost.uicontroller.viewmodel.provider.PenyediaViewModel

// --- TEMA WARNA LOCAL ---
private val EditKostSage = Color(0xFF557C55)
private val EditKostSoftCream = Color(0xFFFAFAF5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanEditKost(
    idKost: String,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditKostViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val snackbarHostState = remember { SnackbarHostState() }

    // Load data kost saat halaman dibuka pertama kali
    LaunchedEffect(Unit) {
        viewModel.loadKost(idKost)
    }

    // Cek status submit (Sukses/Gagal)
    LaunchedEffect(viewModel.submitStatus) {
        if (viewModel.submitStatus is SubmitStatus.Success) {
            navigateBack()
        }
        if (viewModel.submitStatus is SubmitStatus.Error) {
            snackbarHostState.showSnackbar((viewModel.submitStatus as SubmitStatus.Error).message)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = EditKostSoftCream
    ) { innerPadding ->
        Column(
            modifier = modifier.padding(innerPadding).fillMaxSize()
        ) {
            // --- HEADER ---
            EditKostHeader(navigateBack = navigateBack)

            // --- FORM CONTENT ---
            if (viewModel.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = EditKostSage)
                }
            } else {
                EditKostForm(
                    viewModel = viewModel,
                    idKost = idKost
                )
            }
        }
    }
}

// --- KOMPONEN HEADER ---
@Composable
fun EditKostHeader(navigateBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .background(EditKostSage)
            .padding(24.dp)
    ) {
        Column {
            IconButton(
                onClick = navigateBack,
                modifier = Modifier.background(Color.White.copy(alpha = 0.2f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.btn_back),
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.edit_kost_title),
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(R.string.edit_kost_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

// --- KOMPONEN FORM ---
@Composable
fun EditKostForm(
    viewModel: EditKostViewModel,
    idKost: String
) {
    Column(modifier = Modifier.padding(24.dp)) {
        // Input Nama Kost
        OutlinedTextField(
            value = viewModel.namaKost,
            onValueChange = { viewModel.updateState(nama = it) },
            label = { Text(stringResource(R.string.label_nama_kost)) },
            leadingIcon = { Icon(Icons.Default.Home, null, tint = EditKostSage) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = EditKostSage,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Input Alamat
        OutlinedTextField(
            value = viewModel.alamat,
            onValueChange = { viewModel.updateState(alamatBaru = it) },
            label = { Text(stringResource(R.string.label_alamat)) },
            leadingIcon = { Icon(Icons.Default.LocationOn, null, tint = EditKostSage) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = EditKostSage,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            minLines = 3
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Tombol Update
        Button(
            onClick = { viewModel.updateKost(idKost) },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = EditKostSage),
            enabled = viewModel.namaKost.isNotBlank() &&
                    viewModel.alamat.isNotBlank() &&
                    viewModel.submitStatus != SubmitStatus.Loading
        ) {
            if (viewModel.submitStatus == SubmitStatus.Loading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text(
                    text = stringResource(R.string.btn_save_changes),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}