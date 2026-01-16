package com.example.juragankost.uicontroller.view

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.juragankost.R
import com.example.juragankost.uicontroller.viewmodel.EditPenghuniUiState
import com.example.juragankost.uicontroller.viewmodel.EditPenghuniViewModel
import com.example.juragankost.uicontroller.viewmodel.SubmitStatus
import com.example.juragankost.uicontroller.viewmodel.provider.PenyediaViewModel

// --- TEMA WARNA LOCAL ---
private val EditSageDark = Color(0xFF557C55)
private val EditSoftCream = Color(0xFFFAFAF5)

// URL Server (Pastikan sesuai IP)
private const val BASE_IMAGE_URL = "http://10.0.2.2/JuraganKostAPI/images/"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanEditPenghuni(
    idPenghuni: String,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditPenghuniViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Load data awal saat halaman dibuka
    LaunchedEffect(Unit) {
        viewModel.loadPenghuni(idPenghuni)
    }

    // Handle Image Picker
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.updateUiState(fotoBaruUri = it.toString()) }
    }

    // Handle Sukses/Gagal
    LaunchedEffect(uiState.submitStatus) {
        if (uiState.submitStatus is SubmitStatus.Success) {
            navigateBack() // Kembali otomatis jika sukses
        }
        if (uiState.submitStatus is SubmitStatus.Error) {
            snackbarHostState.showSnackbar((uiState.submitStatus as SubmitStatus.Error).message)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = EditSoftCream
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // --- HEADER ---
            EditPenghuniHeader(navigateBack = navigateBack)

            // --- FORM ---
            EditPenghuniForm(
                uiState = uiState,
                viewModel = viewModel,
                onFotoClick = { launcher.launch("image/*") },
                onSaveClick = {
                    val fotoFile = if (uiState.fotoBaruUri != null) {
                        uriToFile(Uri.parse(uiState.fotoBaruUri), context)
                    } else {
                        null
                    }
                    viewModel.saveChanges(idPenghuni, fotoFile)
                }
            )
        }
    }
}

// --- KOMPONEN HEADER ---
@Composable
fun EditPenghuniHeader(navigateBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .background(EditSageDark)
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
                text = stringResource(R.string.edit_penghuni_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

// --- KOMPONEN FORM ---
@Composable
fun EditPenghuniForm(
    uiState: EditPenghuniUiState,
    viewModel: EditPenghuniViewModel,
    onFotoClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    Column(modifier = Modifier.padding(24.dp)) {

        // AREA FOTO
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.LightGray)
                .border(BorderStroke(1.dp, EditSageDark), RoundedCornerShape(16.dp))
                .clickable { onFotoClick() },
            contentAlignment = Alignment.Center
        ) {
            val fotoTampil = if (uiState.fotoBaruUri != null) {
                uiState.fotoBaruUri // Foto Baru (Lokal)
            } else {
                "$BASE_IMAGE_URL${uiState.fotoLama}" // Foto Lama (Server)
            }

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(fotoTampil)
                    .crossfade(true)
                    .build(),
                contentDescription = stringResource(R.string.label_foto_ktp),
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Overlay Icon Edit
            Surface(
                color = Color.Black.copy(alpha = 0.5f),
                shape = CircleShape,
                modifier = Modifier.align(Alignment.Center)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        Text(
            text = stringResource(R.string.photo_change_hint),
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray,
            modifier = Modifier
                .padding(top = 8.dp)
                .align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 1. Input Nama
        EditTextField(
            value = uiState.nama,
            onValueChange = { baru ->
                if (baru.all { it.isLetter() || it.isWhitespace() || it == '.' || it == '\'' || it == '-' }) {
                    viewModel.updateUiState(nama = baru)
                }
            },
            label = stringResource(R.string.label_nama_penghuni),
            icon = Icons.Default.Person
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Input HP
        EditTextField(
            value = uiState.noHp,
            onValueChange = { baru ->
                if (baru.all { it.isDigit() } && baru.length <= 15) {
                    viewModel.updateUiState(noHp = baru)
                }
            },
            label = stringResource(R.string.label_no_hp),
            icon = Icons.Default.Phone,
            keyboardType = KeyboardType.Phone
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Tombol Simpan
        Button(
            onClick = onSaveClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = EditSageDark),
            enabled = uiState.nama.isNotBlank() &&
                    uiState.noHp.isNotBlank() &&
                    uiState.submitStatus != SubmitStatus.Loading
        ) {
            if (uiState.submitStatus == SubmitStatus.Loading) {
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

// --- CUSTOM TEXT FIELD ---
@Composable
fun EditTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, null, tint = EditSageDark) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = EditSageDark,
            unfocusedBorderColor = Color.LightGray
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
    )
}