package com.example.calculatorapp

import android.content.Intent
import androidx.compose.ui.platform.LocalConfiguration
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.privacysandbox.tools.core.model.Type
import com.example.calculatorapp.ui.theme.CalculatorAppTheme
import com.example.calculatorapp.ui.theme.LightGray
import com.example.calculatorapp.ui.theme.MediumGray
import com.example.calculatorapp.ui.theme.Orange
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.WindowInsetsCompat



class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalculatorAppTheme {
                CalculatorScreen(onNavigateToConverter = {
                    val intent = Intent(this, ConverterActivity::class.java)
                    startActivity(intent)
                })
            }
        }
    }
}
@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(onNavigateToConverter: () -> Unit) {
    val viewModel = viewModel<CalculatorViewModel>()
    val state = viewModel.state
    val buttonSpacing = 8.dp
    val context = LocalContext.current

    // Стан для DropdownMenu
    var isMenuExpanded by remember { mutableStateOf(false) }

    // Стан для AlertDialog
    var isExitDialogVisible by remember { mutableStateOf(false) }

    // Функція для відображення діалогу виходу
    fun showExitDialog() {
        isExitDialogVisible = true
    }

    // Функція для закриття додатку
    fun exitApp() {
        if (context is ComponentActivity) {
            context.finishAffinity() // Завершує всі активності
        }
    }

    // Отримуємо орієнтацію екрану
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(
                    "Calculator",
                    color = Color.Transparent,
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 30.sp,
                        brush = Brush.linearGradient(
                            colors = listOf(Color.Red, Color.Yellow, Color.Green, Color.Blue),
                            start = Offset(0f, 0f),
                            end = Offset(200f, 200f)
                        )
                    )
                )
            },
            actions = {
                // Іконка трьох точок для меню
                IconButton(onClick = { isMenuExpanded = true }) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_more_vert_24),
                        contentDescription = "Menu",
                        tint = Color.White
                    )
                }
                // Відкриття меню
                DropdownMenu(
                    expanded = isMenuExpanded,
                    onDismissRequest = { isMenuExpanded = false }
                ) {
                    // Пункт меню для конвертора
                    DropdownMenuItem(
                        text = { Text("Конвертор валют") },
                        onClick = {
                            isMenuExpanded = false
                            val result = state.number1 + (state.operation?.symbol ?: "") + state.number2 // Отримаємо результат
                            val intent = Intent(context, ConverterActivity::class.java).apply {
                                putExtra("calculationResult", result) // Передаємо результат в Intent
                            }
                            context.startActivity(intent) // Відкриваємо нову активність
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Вийти") },
                        onClick = {
                            isMenuExpanded = false
                            showExitDialog() // Викликаємо діалог підтвердження
                        }
                    )
                }
            },
            colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color.DarkGray)
        )

        // Діалог виходу з додатку
        if (isExitDialogVisible) {
            AlertDialog(
                onDismissRequest = { isExitDialogVisible = false },
                title = { Text("Підтвердження виходу") },
                text = { Text("Ви впевнені, що хочете вийти з додатку?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            isExitDialogVisible = false
                            exitApp() // Закрити додаток
                        }
                    ) {
                        Text("Так")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            isExitDialogVisible = false // Закрити діалог без виходу
                        }
                    ) {
                        Text("Ні")
                    }
                }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.DarkGray)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                verticalArrangement = Arrangement.spacedBy(buttonSpacing)
            ) {
                Text(
                    text = state.number1 + (state.operation?.symbol ?: "") + state.number2,
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = if (isPortrait) 32.dp else 16.dp), // Зменшені відступи для горизонтальної орієнтації
                    fontWeight = FontWeight.Light,
                    fontSize = 80.sp,
                    color = Color.White,
                    maxLines = 2
                )

                // Адаптація кількості кнопок в рядках
                val rowArrangement = if (isPortrait) Arrangement.spacedBy(buttonSpacing) else Arrangement.Center

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = rowArrangement
                ) {
                    CalculatorButton(
                        symbol = "AC",
                        color = LightGray,
                        modifier = Modifier
                            .aspectRatio(if (isPortrait) 2f else 1.5f) // Зменшуємо ширину кнопки для горизонтальної орієнтації
                            .weight(2f)
                    ) {
                        viewModel.onAction(CalculatorAction.Clear)
                    }
                    CalculatorButton(
                        symbol = "Del",
                        color = LightGray,
                        modifier = Modifier
                            .aspectRatio(1f)
                            .weight(1f)
                    ) {
                        viewModel.onAction(CalculatorAction.Delete)
                    }
                    CalculatorButton(
                        symbol = "/",
                        color = Orange,
                        modifier = Modifier
                            .aspectRatio(1f)
                            .weight(1f)
                    ) {
                        viewModel.onAction(CalculatorAction.Operation(CalculatorOperation.Divide))
                    }
                }

                // Повторюємо для інших рядків кнопок, адаптуючи їх під орієнтацію
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = rowArrangement
                ) {
                    CalculatorButton(
                        symbol = "7",
                        color = MediumGray,
                        modifier = Modifier
                            .aspectRatio(1f)
                            .weight(1f)
                    ) {
                        viewModel.onAction(CalculatorAction.Number(7))
                    }
                    CalculatorButton(
                        symbol = "8",
                        color = MediumGray,
                        modifier = Modifier
                            .aspectRatio(1f)
                            .weight(1f)
                    ) {
                        viewModel.onAction(CalculatorAction.Number(8))
                    }
                    CalculatorButton(
                        symbol = "9",
                        color = MediumGray,
                        modifier = Modifier
                            .aspectRatio(1f)
                            .weight(1f)
                    ) {
                        viewModel.onAction(CalculatorAction.Number(9))
                    }
                    CalculatorButton(
                        symbol = "x",
                        color = Orange,
                        modifier = Modifier
                            .aspectRatio(1f)
                            .weight(1f)
                    ) {
                        viewModel.onAction(CalculatorAction.Operation(CalculatorOperation.Multiply))
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = rowArrangement
                ) {
                    CalculatorButton(
                        symbol = "4",
                        color = MediumGray,
                        modifier = Modifier
                            .aspectRatio(1f)
                            .weight(1f)
                    ) {
                        viewModel.onAction(CalculatorAction.Number(4))
                    }
                    CalculatorButton(
                        symbol = "5",
                        color = MediumGray,
                        modifier = Modifier
                            .aspectRatio(1f)
                            .weight(1f)
                    ) {
                        viewModel.onAction(CalculatorAction.Number(5))
                    }
                    CalculatorButton(
                        symbol = "6",
                        color = MediumGray,
                        modifier = Modifier
                            .aspectRatio(1f)
                            .weight(1f)
                    ) {
                        viewModel.onAction(CalculatorAction.Number(6))
                    }
                    CalculatorButton(
                        symbol = "-",
                        color = Orange,
                        modifier = Modifier
                            .aspectRatio(1f)
                            .weight(1f)
                    ) {
                        viewModel.onAction(CalculatorAction.Operation(CalculatorOperation.Subtract))
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = rowArrangement
                ) {
                    CalculatorButton(
                        symbol = "1",
                        color = MediumGray,
                        modifier = Modifier
                            .aspectRatio(1f)
                            .weight(1f)
                    ) {
                        viewModel.onAction(CalculatorAction.Number(1))
                    }
                    CalculatorButton(
                        symbol = "2",
                        color = MediumGray,
                        modifier = Modifier
                            .aspectRatio(1f)
                            .weight(1f)
                    ) {
                        viewModel.onAction(CalculatorAction.Number(2))
                    }
                    CalculatorButton(
                        symbol = "3",
                        color = MediumGray,
                        modifier = Modifier
                            .aspectRatio(1f)
                            .weight(1f)
                    ) {
                        viewModel.onAction(CalculatorAction.Number(3))
                    }
                    CalculatorButton(
                        symbol = "+",
                        color = Orange,
                        modifier = Modifier
                            .aspectRatio(1f)
                            .weight(1f)
                    ) {
                        viewModel.onAction(CalculatorAction.Operation(CalculatorOperation.Add))
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = rowArrangement
                ) {
                    CalculatorButton(
                        symbol = "0",
                        color = MediumGray,
                        modifier = Modifier
                            .aspectRatio(2f)
                            .weight(2f)
                    ) {
                        viewModel.onAction(CalculatorAction.Number(0))
                    }
                    CalculatorButton(
                        symbol = ".",
                        color = MediumGray,
                        modifier = Modifier
                            .aspectRatio(1f)
                            .weight(1f)
                    ) {
                        viewModel.onAction(CalculatorAction.Decimal)
                    }
                    CalculatorButton(
                        symbol = "=",
                        color = Orange,
                        modifier = Modifier
                            .aspectRatio(1f)
                            .weight(1f)
                    ) {
                        viewModel.onAction(CalculatorAction.Calculate)
                    }
                }
            }
        }
    }
}
