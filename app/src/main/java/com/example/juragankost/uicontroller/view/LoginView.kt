package com.example.juragankost.uicontroller.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.juragankost.R
import com.example.juragankost.uicontroller.viewmodel.LoginStatus
import com.example.juragankost.uicontroller.viewmodel.LoginUiState
import com.example.juragankost.uicontroller.viewmodel.LoginViewModel
import com.example.juragankost.uicontroller.viewmodel.provider.PenyediaViewModel

// --- PALET WARNA "SAGE AESTHETIC" ---
val SagePrimary = Color(0xFF557C55)      // Hijau Sage Gelap
val WarmCream = Color(0xFFFAFAF5)        // Putih Tulang / Krem
val TextDark = Color(0xFF2D332D)         // Hitam Kehijauan (Soft)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginView(
    onLoginSuccess: (String) -> Unit,
    onRegisterClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val uiState = viewModel.uiState
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle Status Login
    LaunchedEffect(uiState.loginStatus) {
        when (uiState.loginStatus) {
            is LoginStatus.Success -> {
                val idUser = uiState.loggedInUser?.idUser ?: ""
                if (idUser.isNotEmpty()) {
                    onLoginSuccess(idUser)
                    viewModel.resetLoginStatus()
                }
            }
            is LoginStatus.Error -> {
                snackbarHostState.showSnackbar((uiState.loginStatus as LoginStatus.Error).message)
                viewModel.resetLoginStatus()
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = WarmCream
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {

            // --- LATAR BELAKANG DEKORATIF ---
            LoginBackgroundDecoration()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // --- LOGO & JUDUL ---
                LoginHeader()

                Spacer(modifier = Modifier.height(40.dp))

                // --- CARD LOGIN ---
                LoginCard(
                    uiState = uiState,
                    viewModel = viewModel,
                    onRegisterClick = onRegisterClick
                )
            }
        }
    }
}

// --- KOMPONEN DEKORASI ---
@Composable
fun LoginBackgroundDecoration() {
    // Hiasan Header (Bentuk Organik)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .clip(RoundedCornerShape(bottomStart = 80.dp, bottomEnd = 0.dp))
            .background(SagePrimary)
    )

    // Lingkaran Hiasan Transparan
    Box(
        modifier = Modifier
            .offset(x = (-50).dp, y = (-50).dp)
            .size(200.dp)
            .clip(RoundedCornerShape(100))
            .background(Color.White.copy(alpha = 0.1f))
    )
}

// --- KOMPONEN HEADER ---
@Composable
fun LoginHeader() {
    Icon(
        imageVector = Icons.Default.Home,
        contentDescription = null,
        tint = WarmCream,
        modifier = Modifier.size(60.dp)
    )
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = stringResource(R.string.app_name).uppercase(), // "JURAGAN KOST"
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold,
        color = WarmCream,
        letterSpacing = 1.sp
    )
    Text(
        text = stringResource(R.string.app_tagline), // "Kelola bisnis kost jadi lebih mudah"
        style = MaterialTheme.typography.bodyMedium,
        color = WarmCream.copy(alpha = 0.9f)
    )
}

// --- KOMPONEN CARD LOGIN ---
@Composable
fun LoginCard(
    uiState: LoginUiState,
    viewModel: LoginViewModel,
    onRegisterClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.login_title), // "Selamat Datang"
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Input Username
            SoftTextField(
                value = uiState.username,
                onValueChange = { viewModel.updateUiState(username = it) },
                label = stringResource(R.string.label_username),
                icon = Icons.Default.Person
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Input Password
            SoftTextField(
                value = uiState.password,
                onValueChange = { viewModel.updateUiState(password = it) },
                label = stringResource(R.string.label_password),
                icon = Icons.Default.Lock,
                isPassword = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Tombol Login
            Button(
                onClick = { viewModel.login() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SagePrimary),
                enabled = uiState.loginStatus != LoginStatus.Loading
            ) {
                if (uiState.loginStatus == LoginStatus.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        text = stringResource(R.string.login_btn).uppercase(), // "MASUK SEKARANG"
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            TextButton(onClick = onRegisterClick) {
                Text(
                    text = stringResource(R.string.register_link), // "Belum punya akun? Daftar"
                    color = SagePrimary
                )
            }
        }
    }
}

// --- KOMPONEN INPUT TEXT FIELD ---
@Composable
fun SoftTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    isPassword: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color.Gray) },
        leadingIcon = { Icon(icon, null, tint = SagePrimary) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = SagePrimary,
            unfocusedBorderColor = Color(0xFFE0E0E0),
            focusedContainerColor = Color(0xFFF9F9F9),
            unfocusedContainerColor = Color(0xFFF9F9F9)
        ),
        singleLine = true,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None
    )
}