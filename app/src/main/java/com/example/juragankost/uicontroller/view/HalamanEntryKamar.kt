package com.example.juragankost.uicontroller.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.juragankost.R
import com.example.juragankost.uicontroller.viewmodel.EntryKamarViewModel
import com.example.juragankost.uicontroller.viewmodel.SubmitStatus
import com.example.juragankost.uicontroller.viewmodel.provider.PenyediaViewModel

// --- TEMA WARNA LOCAL ---
private val EntrySageDark = Color(0xFF557C55)
private val EntrySoftCream = Color(0xFFFAFAF5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanEntryKamar(
    idKost: String,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EntryKamarViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val uiState = viewModel.uiState
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle Sukses/Gagal
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
        containerColor = EntrySoftCream
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- HEADER ---
            EntryKamarHeader(navigateBack = navigateBack)

            // --- FORM ---
            EntryKamarForm(
                uiState = uiState,
                viewModel = viewModel,
                idKost = idKost
            )
        }
    }
}

// --- KOMPONEN HEADER ---
@Composable
fun EntryKamarHeader(navigateBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .background(EntrySageDark)
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
                text = stringResource(R.string.entry_kamar_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Masukkan detail kamar baru", // Bisa ditambahkan ke strings.xml jika perlu
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

// --- KOMPONEN FORM ---
@Composable
fun EntryKamarForm(
    uiState: com.example.juragankost.uicontroller.viewmodel.EntryKamarUiState, // Mengambil state dari ViewModel
    viewModel: EntryKamarViewModel,
    idKost: String
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

                // Input Nomor Kamar
                EntryTextField(
                    value = uiState.nomorKamar,
                    onValueChange = { viewModel.updateUiState(nomor = it) },
                    label = stringResource(R.string.label_nomor_kamar) + " (Contoh: A01)", // Gabung label + hint
                    icon = Icons.Default.Home,
                    imeAction = ImeAction.Next
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Input Harga (Validasi Angka)
                EntryTextField(
                    value = uiState.harga,
                    onValueChange = { inputBaru ->
                        if (inputBaru.all { it.isDigit() }) {
                            viewModel.updateUiState(harga = inputBaru)
                        }
                    },
                    label = stringResource(R.string.label_harga) + " (Rp)",
                    icon = Icons.Default.Info,
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Tombol Simpan
        Button(
            onClick = { viewModel.saveKamar(idKost) },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = EntrySageDark),
            enabled = uiState.isEntryValid && uiState.submitStatus != SubmitStatus.Loading
        ) {
            if (uiState.submitStatus == SubmitStatus.Loading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text(
                    text = stringResource(R.string.btn_save_kamar),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// --- CUSTOM TEXT FIELD ---
@Composable
fun EntryTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, null, tint = EntrySageDark) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = EntrySageDark,
            unfocusedBorderColor = Color(0xFFE0E0E0),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction
        )
    )
}