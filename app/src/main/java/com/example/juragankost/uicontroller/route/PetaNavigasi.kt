package com.example.juragankost.uicontroller.route

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.juragankost.uicontroller.view.LoginView
import com.example.juragankost.uicontroller.view.HalamanHome
import androidx.compose.material3.Text
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.juragankost.uicontroller.view.HalamanCheckIn
import com.example.juragankost.uicontroller.view.HalamanDetailKost
import com.example.juragankost.uicontroller.view.HalamanDetailPenghuni
import com.example.juragankost.uicontroller.view.HalamanEditKamar
import com.example.juragankost.uicontroller.view.HalamanEditKost
import com.example.juragankost.uicontroller.view.HalamanEditPenghuni
import com.example.juragankost.uicontroller.view.HalamanEntryKamar
import com.example.juragankost.uicontroller.view.HalamanEntryKost
import com.example.juragankost.uicontroller.view.HalamanPayment
import com.example.juragankost.uicontroller.view.HalamanRegister
import com.example.juragankost.uicontroller.view.HalamanRiwayatKost

@Composable
fun PengelolaHalaman(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = DestinasiLogin.route,
        modifier = modifier
    ) {
        // 1. Rute LOGIN
        composable(route = DestinasiLogin.route) {
            LoginView(
                // Tugas 1: Kalau Login Sukses (Kode Lama)
                onLoginSuccess = { idUser ->
                    navController.navigate("${DestinasiHome.route}/$idUser") {
                        popUpTo(DestinasiLogin.route) { inclusive = true }
                    }
                },

                // Tugas 2: Kalau Tombol Daftar Diklik (TAMBAHAN BARU)
                onRegisterClick = {
                    navController.navigate(DestinasiRegister.route)
                }
            )
        }
        // Register Jika tidak ada akun
        composable(route = DestinasiRegister.route) {
            HalamanRegister(
                navigateBack = { navController.popBackStack() }
            )
        }

        // 2. Rute HOME (Update bagian onAddKostClick)
        composable(
            route = DestinasiHome.routeWithArg,
            arguments = listOf(
                navArgument(DestinasiHome.idUserArg) { type = NavType.StringType }
            )
        ) { backStackEntry -> // Ambil backStackEntry untuk dapat argumen

            // Ambil ID User dari argumen navigasi
            val idUser = backStackEntry.arguments?.getString(DestinasiHome.idUserArg) ?: ""

            HalamanHome(
                onDetailClick = { idKost ->
                    // UPDATE INI: Pindah ke Detail Kost membawa ID
                    navController.navigate("${DestinasiDetailKost.route}/$idKost")
                },
                onAddKostClick = {
                    // PINDAH KE HALAMAN TAMBAH KOST
                    // Kita kirim juga ID User biar halaman tambah tahu siapa pemiliknya
                    navController.navigate("${DestinasiEntryKost.route}/$idUser")
                },
                onLogoutClick = {
                    navController.navigate(DestinasiLogin.route) {
                        // popUpTo(0) artinya hapus SEMUA halaman dari tumpukan (backstack)
                        // Jadi user tidak bisa tekan 'Back' untuk kembali ke Home
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // 3. Rute ENTRY KOST (TAMBAHAN BARU)
        composable(
            route = "${DestinasiEntryKost.route}/{${DestinasiHome.idUserArg}}", // Terima ID User
            arguments = listOf(navArgument(DestinasiHome.idUserArg) { type = NavType.StringType })
        ) { backStackEntry ->

            val idUser = backStackEntry.arguments?.getString(DestinasiHome.idUserArg) ?: ""

            HalamanEntryKost(
                idUser = idUser,
                navigateBack = {
                    // Saat kembali, popUp agar halaman entry hilang dari stack
                    navController.popBackStack()
                }
            )
        }
        // 4. Rute DASHBOARD KAMAR (DETAIL KOST)
        composable(
            route = DestinasiDetailKost.routeWithArg,
            arguments = listOf(navArgument(DestinasiDetailKost.idKostArg) { type = NavType.StringType })
        ) { backStackEntry ->
            val idKost = backStackEntry.arguments?.getString(DestinasiDetailKost.idKostArg) ?: ""

            HalamanDetailKost(
                idKost = idKost,
                navigateBack = { navController.popBackStack() },
                onAddKamarClick = {
                    navController.navigate("${DestinasiEntryKamar.route}/$idKost")
                },
                onKamarClick = { kamar ->
                    if (kamar.isOccupied == "0") {
                        // KOSONG -> Check In
                        navController.navigate("${DestinasiCheckIn.route}/${kamar.idRoom}")
                    } else {
                        // TERISI (1) -> Detail Penghuni & Check Out
                        navController.navigate("${DestinasiDetailPenghuni.route}/${kamar.idRoom}")
                    }
                },
                onHistoryClick = {
                    navController.navigate("${DestinasiRiwayatKost.route}/$idKost")
                },
                onEditKostClick = { id ->
                    // SEKARANG LEBIH JELAS: Arahkan ke Edit Kost bawa ID
                    navController.navigate("${DestinasiEditKost.route}/$id")
                },
                onEditKamarClick = { idRoom ->
                    navController.navigate("${DestinasiEditKamar.route}/$idRoom")
                }
            )
        }

        // 5. Rute ENTRY KAMAR
        composable(
            route = DestinasiEntryKamar.routeWithArg,
            arguments = listOf(navArgument(DestinasiEntryKamar.idKostArg) { type = NavType.StringType })
        ) { backStackEntry ->

            val idKost = backStackEntry.arguments?.getString(DestinasiEntryKamar.idKostArg) ?: ""

            HalamanEntryKamar(
                idKost = idKost,
                navigateBack = { navController.popBackStack() }
            )
        }

        // 6. Rute CHECK-IN
        composable(
            route = DestinasiCheckIn.routeWithArg,
            arguments = listOf(navArgument(DestinasiCheckIn.idRoomArg) { type = NavType.StringType })
        ) { backStackEntry ->
            val idRoom = backStackEntry.arguments?.getString(DestinasiCheckIn.idRoomArg) ?: ""

            HalamanCheckIn(
                idRoom = idRoom,
                navigateBack = { navController.popBackStack() }
            )
        }

        // 7. Rute DETAIL PENGHUNI
        composable(
            route = DestinasiDetailPenghuni.routeWithArg,
            arguments = listOf(navArgument(DestinasiDetailPenghuni.idRoomArg) { type = NavType.StringType })
        ) { backStackEntry ->


            HalamanDetailPenghuni(
                navigateBack = { navController.popBackStack() },
                onPaymentClick = { idPenghuni ->
                    // Nah sekarang kita punya ID-nya!
                    navController.navigate("${DestinasiPayment.route}/$idPenghuni")
                },
                onEditClick = { idPenghuni ->
                    navController.navigate("${DestinasiEditPenghuni.route}/$idPenghuni")
                }
            )
        }
        // 8. Rute PAYMENT
        composable(
            route = DestinasiPayment.routeWithArg,
            arguments = listOf(navArgument(DestinasiPayment.idPenghuniArg) { type = NavType.StringType })
        ) {
            HalamanPayment(
                navigateBack = { navController.popBackStack() }
            )
        }

        // 9. Riwayat
        composable(
            route = DestinasiRiwayatKost.routeWithArgs,
            arguments = listOf(navArgument(DestinasiRiwayatKost.idKostArg) {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val idKost = backStackEntry.arguments?.getString(DestinasiRiwayatKost.idKostArg) ?: ""

            // Panggil Halaman UI Baru
            HalamanRiwayatKost(
                idKost = idKost,
                navigateBack = { navController.popBackStack() }
            )
        }

        // 10. edit penghuni
        composable(
            route = DestinasiEditPenghuni.routeWithArgs,
            arguments = listOf(navArgument(DestinasiEditPenghuni.idPenghuniArg) {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val idPenghuni = backStackEntry.arguments?.getString(DestinasiEditPenghuni.idPenghuniArg) ?: ""

            HalamanEditPenghuni(
                idPenghuni = idPenghuni,
                navigateBack = { navController.popBackStack() }
            )
        }
        // 11. Edit Kost
        composable(
            route = DestinasiEditKost.routeWithArgs,
            arguments = listOf(navArgument(DestinasiEditKost.idKostArg) {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val idKost = backStackEntry.arguments?.getString(DestinasiEditKost.idKostArg) ?: ""

            HalamanEditKost(
                idKost = idKost,
                navigateBack = { navController.popBackStack() }
            )
        }
        // 12. Edit Kamar
        composable(
            route = DestinasiEditKamar.routeWithArgs,
            arguments = listOf(navArgument(DestinasiEditKamar.idRoomArg) { type = NavType.StringType })
        ) { backStackEntry ->
            val idRoom = backStackEntry.arguments?.getString(DestinasiEditKamar.idRoomArg) ?: ""
            HalamanEditKamar(idRoom = idRoom, navigateBack = { navController.popBackStack() })
        }
    }
}