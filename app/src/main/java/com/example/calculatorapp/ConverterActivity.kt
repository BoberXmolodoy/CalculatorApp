package com.example.calculatorapp

import android.os.Bundle
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.zIndex
import com.example.calculatorapp.ui.theme.CalculatorAppTheme

class ConverterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalculatorAppTheme {
                val result = intent.getStringExtra("calculationResult") ?: "0"
                ConverterScreen(result)
            }
        }
    }
}

// 🔹 Функція для конвертації валют
fun convertCurrency(from: String, to: String, amount: String): String {
    val amountDouble = amount.toDoubleOrNull() ?: 0.0
    val exchangeRate = when (from to to) {
        "USD" to "EUR" -> 0.85
        "USD" to "GBP" -> 0.75
        "USD" to "JPY" -> 110.0
        "USD" to "UAH" -> 42.3
        "EUR" to "USD" -> 1.18
        "EUR" to "GBP" -> 0.88
        "EUR" to "JPY" -> 129.53
        "EUR" to "UAH" -> 49.8
        "GBP" to "USD" -> 1.33
        "GBP" to "EUR" -> 1.14
        "GBP" to "JPY" -> 148.2
        "GBP" to "UAH" -> 56.4
        "JPY" to "USD" -> 0.0091
        "JPY" to "EUR" -> 0.0077
        "JPY" to "GBP" -> 0.0067
        "JPY" to "UAH" -> 0.39
        "UAH" to "USD" -> 0.024
        "UAH" to "EUR" -> 0.020
        "UAH" to "GBP" -> 0.018
        "UAH" to "JPY" -> 2.56
        else -> 1.0
    }
    return (amountDouble * exchangeRate).toString()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConverterScreen(amount: String) {
    val context = LocalContext.current
    var amountInput by remember { mutableStateOf(amount) }
    var fromCurrency by remember { mutableStateOf("USD") }
    var toCurrency by remember { mutableStateOf("EUR") }
    var result by remember { mutableStateOf(amount) }
    var isMenuExpanded by remember { mutableStateOf(false) }

    val currencyOptions = listOf("USD", "EUR", "GBP", "JPY", "UAH")

    // 🔹 Оновлення конвертації при зміні валюти або суми
    LaunchedEffect(fromCurrency, toCurrency, amountInput) {
        result = convertCurrency(fromCurrency, toCurrency, amountInput)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Currency Converter", color = Color.White, fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { isMenuExpanded = true }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "Menu", tint = Color.White)
                    }
                    DropdownMenu(
                        expanded = isMenuExpanded,
                        onDismissRequest = { isMenuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Calculator") },
                            onClick = {
                                isMenuExpanded = false
                                context.startActivity(Intent(context, MainActivity::class.java))
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Exit") },
                            onClick = {
                                isMenuExpanded = false
                                (context as? ComponentActivity)?.finishAffinity()
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color.Black)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text("Enter amount:", fontSize = 18.sp, fontWeight = FontWeight.Medium, color = Color.Black)

            OutlinedTextField(
                value = amountInput,
                onValueChange = { amountInput = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Gray,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            CurrencyDropdown("From", fromCurrency, currencyOptions) { selected ->
                fromCurrency = selected
            }

            Spacer(modifier = Modifier.height(8.dp))

            CurrencyDropdown("To", toCurrency, currencyOptions) { selected ->
                toCurrency = selected
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Converted amount: $result",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyDropdown(
    label: String,
    selectedCurrency: String,
    currencyOptions: List<String>,
    onSelectionChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(label, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.Black)

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = selectedCurrency,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                trailingIcon = {
                    Icon(Icons.Filled.ArrowDropDown, contentDescription = "Expand", tint = Color.Black)
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Black,
                    unfocusedBorderColor = Color.Gray,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.zIndex(1f) // Виправлено накладання меню
            ) {
                currencyOptions.forEach { currency ->
                    DropdownMenuItem(
                        text = { Text(currency, color = Color.Black) },
                        onClick = {
                            onSelectionChange(currency)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
