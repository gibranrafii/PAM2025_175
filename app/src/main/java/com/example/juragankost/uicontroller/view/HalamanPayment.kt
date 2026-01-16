package com.example.juragankost.uicontroller.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.juragankost.R
import com.example.juragankost.modeldata.Payment
import com.example.juragankost.uicontroller.viewmodel.PaymentViewModel
import com.example.juragankost.uicontroller.viewmodel.SubmitStatus
import com.example.juragankost.uicontroller.viewmodel.provider.PenyediaViewModel
import java.text.NumberFormat
import java.util.Locale

// --- TEMA WARNA ---
private val PaymentSageDark = Color(0xFF557C55)
private val PaymentSoftCream = Color(0xFFFAFAF5)
private val MoneyGreen = Color(0xFF2E7D32)
private val SummaryGold = Color(0xFFD4A373)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HalamanPayment(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PaymentViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val uiState = viewModel.uiState

    // HITUNG TOTAL PEMASUKAN
    val totalMasuk = remember(uiState.paymentList) {
        uiState.paymentList.sumOf {
            it.nominal.replace(".", "").replace(",", "").toDoubleOrNull() ?: 0.0
        }
    }
    val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))
    val totalFormatted = formatter.format(totalMasuk)

    Scaffold(
        containerColor = PaymentSoftCream
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // --- HEADER ---
            PaymentHeader(navigateBack = navigateBack)

            // --- KONTEN UTAMA ---
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
            ) {
                // CARD TOTAL (RINGKASAN)
                PaymentSummaryCard(totalFormatted = totalFormatted)

                // FORM INPUT
                PaymentForm(
                    uiState = uiState,
                    viewModel = viewModel
                )

                // LIST RIWAYAT
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.payment_history_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4A4A4A)
                )
                Spacer(modifier = Modifier.height(8.dp))

                PaymentList(uiState = uiState)
            }
        }
    }
}

// --- KOMPONEN HEADER ---
@Composable
fun PaymentHeader(navigateBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .background(PaymentSageDark)
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
                text = stringResource(R.string.payment_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Rekap pembayaran penghuni", // Bisa masuk strings.xml
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(60.dp)) // Spacer untuk tempat floating card
        }
    }
}

// --- KOMPONEN RINGKASAN ---
@Composable
fun PaymentSummaryCard(totalFormatted: String) {
    Card(
        modifier = Modifier
            .offset(y = (-45).dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Total Terbayar", // Bisa masuk strings.xml
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )
                Text(
                    text = "Rp $totalFormatted",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MoneyGreen
                )
            }
            Icon(
                imageVector = Icons.Outlined.ShoppingCart,
                contentDescription = null,
                tint = SummaryGold,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

// --- KOMPONEN FORM ---
@Composable
fun PaymentForm(
    uiState: com.example.juragankost.uicontroller.viewmodel.PaymentUiState,
    viewModel: PaymentViewModel
) {
    // JUDUL SEKSI INPUT
    Text(
        text = stringResource(R.string.payment_input_title),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = PaymentSageDark,
        modifier = Modifier.offset(y = (-25).dp)
    )

    // FORM INPUT (Versi Compact)
    Column(
        modifier = Modifier
            .offset(y = (-15).dp)
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        // VALIDASI NOMINAL (Hanya Angka)
        PaymentTextField(
            value = uiState.nominalInput,
            onValueChange = { baru ->
                // VALIDASI: Hanya terima angka 0-9
                if (baru.all { it.isDigit() }) {
                    viewModel.updateInput(nominal = baru)
                }
            },
            label = stringResource(R.string.label_nominal),
            keyboardType = KeyboardType.Number
        )

        Spacer(modifier = Modifier.height(8.dp))

        PaymentTextField(
            value = uiState.keteranganInput,
            onValueChange = { viewModel.updateInput(keterangan = it) },
            label = stringResource(R.string.label_keterangan) + " (" + stringResource(R.string.hint_keterangan) + ")"
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { viewModel.addPayment() },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PaymentSageDark),
            enabled = uiState.nominalInput.isNotEmpty() && uiState.submitStatus != SubmitStatus.Loading
        ) {
            if (uiState.submitStatus == SubmitStatus.Loading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
            } else {
                Text(
                    text = stringResource(R.string.btn_save_payment),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// --- KOMPONEN LIST ---
@Composable
fun PaymentList(uiState: com.example.juragankost.uicontroller.viewmodel.PaymentUiState) {
    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = PaymentSageDark)
        }
    } else if (uiState.paymentList.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.payment_empty), color = Color.Gray)
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(uiState.paymentList) { payment ->
                ItemPaymentStyled(payment)
            }
        }
    }
}

@Composable
fun ItemPaymentStyled(payment: Payment) {
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
                        tint = MoneyGreen,
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
                    color = MoneyGreen
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

@Composable
fun PaymentTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, style = MaterialTheme.typography.bodySmall) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PaymentSageDark,
            unfocusedBorderColor = Color(0xFFE0E0E0),
            focusedContainerColor = Color(0xFFF9F9F9),
            unfocusedContainerColor = Color(0xFFF9F9F9)
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
    )
}