package com.example.juragankost.uicontroller.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.juragankost.R
import com.example.juragankost.uicontroller.viewmodel.EditKamarViewModel
import com.example.juragankost.uicontroller.viewmodel.SubmitStatus
import com.example.juragankost.uicontroller.viewmodel.provider.PenyediaViewModel

// --- TEMA WARNA LOCAL ---
private val EditKamarSage = Color(0xFF557C55)
private val EditKamarSoftCream = Color(0xFFFAFAF5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanEditKamar(
    idRoom: String,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditKamarViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val snackbarHostState = remember { SnackbarHostState() }

    // Load Data saat pertama kali dibuka
    LaunchedEffect(Unit) { viewModel.loadKamar(idRoom) }

    // Cek Status Submit (Sukses/Gagal)
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
        containerColor = EditKamarSoftCream
    ) { innerPadding ->
        Column(modifier = modifier.padding(innerPadding).fillMaxSize()) {

            // --- HEADER ---
            EditKamarHeader(navigateBack = navigateBack)

            // --- FORM ---
            if (viewModel.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = EditKamarSage)
                }
            } else {
                EditKamarForm(
                    viewModel = viewModel,
                    idRoom = idRoom
                )
            }
        }
    }
}

// --- KOMPONEN HEADER ---
@Composable
fun EditKamarHeader(navigateBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .background(EditKamarSage)
            .padding(24.dp)
    ) {
        Column {
            IconButton(
                onClick = navigateBack,
                modifier = Modifier.background(Color.White.copy(0.2f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.btn_back),
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.edit_kamar_title),
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// --- KOMPONEN FORM ---
@Composable
fun EditKamarForm(
    viewModel: EditKamarViewModel,
    idRoom: String
) {
    Column(Modifier.padding(24.dp)) {
        // Input Nomor Kamar
        OutlinedTextField(
            value = viewModel.nomorKamar,
            onValueChange = { viewModel.updateState(nomor = it) },
            label = { Text(stringResource(R.string.label_nomor_kamar)) },
            leadingIcon = { Icon(Icons.Default.Home, null, tint = EditKamarSage) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = EditKamarSage,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Input Harga (Validasi Angka)
        OutlinedTextField(
            value = viewModel.harga,
            onValueChange = {
                if (it.all { char -> char.isDigit() }) {
                    viewModel.updateState(hargaBaru = it)
                }
            },
            label = { Text(stringResource(R.string.label_harga)) },
            leadingIcon = { Icon(Icons.Default.ShoppingCart, null, tint = EditKamarSage) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = EditKamarSage,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Tombol Simpan
        Button(
            onClick = { viewModel.updateKamar(idRoom) },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = EditKamarSage),
            enabled = viewModel.nomorKamar.isNotBlank() &&
                    viewModel.harga.isNotBlank() &&
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