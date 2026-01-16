package com.example.juragankost.uicontroller.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.juragankost.R
import com.example.juragankost.uicontroller.viewmodel.EntryKostViewModel
import com.example.juragankost.uicontroller.viewmodel.EntryKostUiState
import com.example.juragankost.uicontroller.viewmodel.SubmitStatus
import com.example.juragankost.uicontroller.viewmodel.provider.PenyediaViewModel

// --- TEMA WARNA LOCAL ---
private val EntryKostSageDark = Color(0xFF557C55)
private val EntryKostSoftCream = Color(0xFFFAFAF5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanEntryKost(
    idUser: String,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EntryKostViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val uiState = viewModel.uiState
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle Sukses/Gagal Submit
    LaunchedEffect(uiState.submitStatus) {
        if (uiState.submitStatus is SubmitStatus.Success) {
            navigateBack()
            viewModel.resetForm()
        }
        if (uiState.submitStatus is SubmitStatus.Error) {
            snackbarHostState.showSnackbar((uiState.submitStatus as SubmitStatus.Error).message)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = EntryKostSoftCream
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- HEADER ---
            EntryKostHeader(navigateBack = navigateBack)

            // --- FORM ---
            EntryKostForm(
                uiState = uiState,
                viewModel = viewModel,
                idUser = idUser
            )
        }
    }
}

// --- KOMPONEN HEADER ---
@Composable
fun EntryKostHeader(navigateBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .background(EntryKostSageDark)
            .padding(24.dp)
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
                text = stringResource(R.string.entry_kost_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Daftarkan lokasi kost baru Anda", // Bisa dipindah ke strings.xml
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

// --- KOMPONEN FORM ---
@Composable
fun EntryKostForm(
    uiState: EntryKostUiState,
    viewModel: EntryKostViewModel,
    idUser: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Card Pembungkus Form
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {

                // Input Nama Kost
                EntryKostTextField(
                    value = uiState.namaKost,
                    onValueChange = { viewModel.updateUiState(nama = it) },
                    label = stringResource(R.string.label_nama_kost) + " (" + stringResource(R.string.hint_nama_kost) + ")",
                    icon = Icons.Default.Home,
                    imeAction = ImeAction.Next
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Input Alamat (Multi-line support)
                EntryKostTextField(
                    value = uiState.alamat,
                    onValueChange = { viewModel.updateUiState(alamat = it) },
                    label = stringResource(R.string.label_alamat),
                    icon = Icons.Default.LocationOn,
                    imeAction = ImeAction.Done,
                    minLines = 3 // Agar kotak lebih tinggi
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Tombol Simpan
        Button(
            onClick = { viewModel.saveKost(idUser) },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = EntryKostSageDark),
            enabled = uiState.isEntryValid && uiState.submitStatus != SubmitStatus.Loading
        ) {
            if (uiState.submitStatus == SubmitStatus.Loading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text(
                    text = stringResource(R.string.btn_save_kost),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// --- CUSTOM TEXT FIELD ---
@Composable
fun EntryKostTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    imeAction: ImeAction = ImeAction.Default,
    minLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = {
            // Icon diletakkan di atas jika multiline agar rapi
            if (minLines > 1) {
                Column(modifier = Modifier.height(IntrinsicSize.Max)) {
                    Icon(icon, null, tint = EntryKostSageDark)
                    Spacer(modifier = Modifier.weight(1f))
                }
            } else {
                Icon(icon, null, tint = EntryKostSageDark)
            }
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = EntryKostSageDark,
            unfocusedBorderColor = Color(0xFFE0E0E0),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        ),
        singleLine = minLines == 1,
        minLines = minLines,
        keyboardOptions = KeyboardOptions(imeAction = imeAction)
    )
}