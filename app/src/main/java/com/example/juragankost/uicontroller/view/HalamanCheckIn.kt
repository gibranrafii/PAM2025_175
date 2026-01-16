package com.example.juragankost.uicontroller.view

import android.content.Context
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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.outlined.AccountBox
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.juragankost.R
import com.example.juragankost.uicontroller.viewmodel.CheckInViewModel
import com.example.juragankost.uicontroller.viewmodel.SubmitStatus
import com.example.juragankost.uicontroller.viewmodel.provider.PenyediaViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

// --- TEMA WARNA LOCAL ---
private val CheckInSageDark = Color(0xFF557C55)
private val CheckInSoftCream = Color(0xFFFAFAF5)
private val CheckInTextDark = Color(0xFF2D332D)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanCheckIn(
    idRoom: String,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CheckInViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Setup Image Picker
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.updateUiState(fotoUri = it.toString()) }
    }

    // Handle Status Submit
    LaunchedEffect(uiState.submitStatus) {
        if (uiState.submitStatus is SubmitStatus.Success) {
            navigateBack()
        }
        if (uiState.submitStatus is SubmitStatus.Error) {
            snackbarHostState.showSnackbar((uiState.submitStatus as SubmitStatus.Error).message)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = CheckInSoftCream
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- HEADER ---
            HeaderSection(navigateBack)

            // --- FORM CONTENT ---
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

                        // 1. Input Nama (Validasi Huruf)
                        CheckInTextField(
                            value = uiState.nama,
                            onValueChange = { baru ->
                                if (baru.all { it.isLetter() || it.isWhitespace() || it == '.' || it == '\'' || it == '-' }) {
                                    viewModel.updateUiState(nama = baru)
                                }
                            },
                            label = stringResource(R.string.label_nama_penghuni),
                            icon = Icons.Default.Person,
                            keyboardType = KeyboardType.Text
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // 2. Input No HP (Validasi Angka)
                        CheckInTextField(
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

                        Spacer(modifier = Modifier.height(24.dp))

                        // 3. Area Upload Foto
                        Text(
                            text = stringResource(R.string.label_foto_ktp),
                            style = MaterialTheme.typography.labelLarge,
                            color = CheckInTextDark,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        FotoUploadArea(
                            fotoUri = uiState.fotoUri,
                            onUploadClick = { launcher.launch("image/*") }
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // TOMBOL SUBMIT
                Button(
                    onClick = {
                        val file = uriToFile(Uri.parse(uiState.fotoUri), context)
                        viewModel.checkIn(idRoom, file)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CheckInSageDark),
                    enabled = uiState.nama.isNotBlank() && uiState.noHp.isNotBlank() && uiState.fotoUri != null && uiState.submitStatus != SubmitStatus.Loading
                ) {
                    if (uiState.submitStatus == SubmitStatus.Loading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(
                            text = stringResource(R.string.btn_submit_checkin),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}

// --- KOMPONEN TERPISAH (Agar Rapi) ---

@Composable
fun HeaderSection(onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .background(CheckInSageDark)
            .padding(24.dp)
    ) {
        Column {
            IconButton(
                onClick = onBackClick,
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
                text = stringResource(R.string.checkin_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Isi data lengkap untuk check-in", // Bisa dipindah ke strings.xml: checkin_subtitle
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun FotoUploadArea(fotoUri: String?, onUploadClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF5F5F5))
            .border(
                BorderStroke(1.dp, if (fotoUri == null) Color.LightGray else CheckInSageDark),
                RoundedCornerShape(12.dp)
            )
            .clickable { onUploadClick() },
        contentAlignment = Alignment.Center
    ) {
        if (fotoUri != null) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(Uri.parse(fotoUri))
                    .crossfade(true)
                    .build(),
                contentDescription = "Preview KTP",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Surface(
                color = Color.Black.copy(alpha = 0.6f),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.btn_change_photo),
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(8.dp),
                    fontWeight = FontWeight.Bold,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Outlined.AccountBox,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(48.dp)
                )
                Text(
                    text = stringResource(R.string.btn_choose_photo),
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun CheckInTextField(
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
        leadingIcon = { Icon(icon, null, tint = CheckInSageDark) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = CheckInSageDark,
            unfocusedBorderColor = Color(0xFFE0E0E0),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
    )
}

// FUNGSI UTILITY
fun uriToFile(imageUri: Uri, context: Context): File {
    val myFile = File(context.cacheDir, "temp_ktp_${System.currentTimeMillis()}.jpg")
    val inputStream = context.contentResolver.openInputStream(imageUri) as InputStream
    val outputStream = FileOutputStream(myFile)
    val buffer = ByteArray(1024)
    var length: Int
    while (inputStream.read(buffer).also { length = it } > 0) {
        outputStream.write(buffer, 0, length)
    }
    outputStream.close()
    inputStream.close()
    return myFile
}