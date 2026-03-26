package com.example.tips

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import java.text.NumberFormat
import java.util.Locale

// --- PALETA DE COLORES "ELECTRIC GLOW" PRO ---
val DeepDarkBase = Color(0xFF080A0D)
val CardBackground = Color(0xFF14171B)
val ElectricCyanGradient = Brush.linearGradient(listOf(Color(0xFF00C7D9), Color(0xFF0FD7CE)))
val ElectricBluePurpleGradient = Brush.linearGradient(listOf(Color(0xFF00B2FF), Color(0xFF6A1BFF)))
val ElectricCyanSolid = Color(0xFF0FEBCB)
val ElectricBlueSolid = Color(0xFF00B2FF)
val TextPrimaryWhite = Color(0xFFFFFFFF)
val TextSecondaryGray = Color(0xFF8B92A5)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(color = DeepDarkBase) {
                    TipMasterProApp()
                }
            }
        }
    }
}

@Composable
fun TipMasterProApp() {
    val context = LocalContext.current

    // --- ESTADOS DE LA UI ---
    var billAmountInput by remember { mutableStateOf("150.40") }

    var selectedTip by remember { mutableIntStateOf(15) }
    var isCustomTip by remember { mutableStateOf(false) }
    var customTipInput by remember { mutableStateOf("") }

    var numPeopleSplit by remember { mutableIntStateOf(2) }

    // Estado para controlar si el Modal está visible o no
    var showSummaryModal by remember { mutableStateOf(false) }

    // --- CÁLCULOS LÓGICOS EN TIEMPO REAL ---
    val billAmount = billAmountInput.toDoubleOrNull() ?: 0.0

    val effectiveTipPercentage = if (isCustomTip) {
        customTipInput.toDoubleOrNull() ?: 0.0
    } else {
        selectedTip.toDouble()
    }

    val tipAmount = billAmount * (effectiveTipPercentage / 100.0)
    val totalWithTip = billAmount + tipAmount
    val splitResult = if (numPeopleSplit > 0) totalWithTip / numPeopleSplit else 0.0

    val formatter = NumberFormat.getCurrencyInstance(Locale.getDefault())
    val tileHeight = 85.dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepDarkBase)
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- CABECERA ---
        Text(
            text = "Tips",
            color = ElectricCyanSolid,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(text = "TOTAL BILL", color = TextSecondaryGray, fontSize = 12.sp)

        // --- INPUT PRINCIPAL ---
        BasicTextField(
            value = billAmountInput,
            onValueChange = { billAmountInput = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            textStyle = TextStyle(
                color = ElectricBlueSolid,
                fontSize = 50.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            ),
            cursorBrush = SolidColor(ElectricBlueSolid),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "$",
                        color = ElectricBlueSolid,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(end = 4.dp, bottom = 4.dp)
                    )
                    innerTextField()
                }
            }
        )

        // --- SELECCIÓN DE PROPINA ---
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 16.dp)
        ) {
            val tipOptions = listOf(0, 10, 15, 18, 20)
            items(tipOptions.size) { index ->
                val percentage = tipOptions[index]
                TipSelectionCard(
                    percentage = percentage,
                    tipFormatted = formatter.format(billAmount * (percentage / 100.0)),
                    isSelected = !isCustomTip && selectedTip == percentage,
                    height = tileHeight,
                    onClick = {
                        selectedTip = percentage
                        isCustomTip = false
                    }
                )
            }

            item {
                CustomTipCard(
                    textValue = customTipInput,
                    onValueChange = { customTipInput = it },
                    isSelected = isCustomTip,
                    height = tileHeight,
                    onClick = { isCustomTip = true }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- SECCIÓN SPLIT BILL ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardBackground, shape = RoundedCornerShape(24.dp))
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "SPLIT BILL",
                    color = TextSecondaryGray,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = formatter.format(totalWithTip),
                    color = TextPrimaryWhite,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RoundSplitButton(
                        text = "-",
                        gradient = ElectricBluePurpleGradient,
                        onClick = { if (numPeopleSplit > 1) numPeopleSplit-- }
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$numPeopleSplit",
                            color = TextPrimaryWhite,
                            fontSize = 38.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(text = "PEOPLE", color = TextSecondaryGray, fontSize = 12.sp)
                    }

                    RoundSplitButton(
                        text = "+",
                        gradient = ElectricCyanGradient,
                        onClick = { numPeopleSplit++ }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(0.1f))

        // --- TARJETA DE RESULTADO FINAL ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = CardBackground, shape = RoundedCornerShape(28.dp))
                .padding(2.dp)
                .background(ElectricCyanGradient, shape = RoundedCornerShape(28.dp))
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "TOTAL PER PERSON:",
                    color = TextPrimaryWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                AnimatedContent(
                    targetState = splitResult,
                    transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(300)) },
                    label = "TotalAnimation"
                ) { targetAmount ->
                    Text(
                        text = formatter.format(targetAmount),
                        color = TextPrimaryWhite,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- BOTÓN FINAL DE PAGO (Abre el Modal) ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .background(ElectricBluePurpleGradient, shape = RoundedCornerShape(20.dp))
                .clip(RoundedCornerShape(20.dp))
                .clickable {
                    // Al hacer clic, mostramos el modal de resumen
                    showSummaryModal = true
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "PAY NOW",
                color = TextPrimaryWhite,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )
        }
    }

    // --- MODAL DE RESUMEN (Se dibuja por encima cuando es true) ---
    if (showSummaryModal) {
        Dialog(onDismissRequest = { showSummaryModal = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = CardBackground,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, ElectricCyanGradient, RoundedCornerShape(24.dp))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Order Summary",
                        color = ElectricCyanSolid,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Desglose de la cuenta
                    SummaryRow(label = "Subtotal", value = formatter.format(billAmount))
                    SummaryRow(
                        label = "Tip (${if (effectiveTipPercentage % 1.0 == 0.0) effectiveTipPercentage.toInt() else effectiveTipPercentage}%)",
                        value = formatter.format(tipAmount)
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = TextSecondaryGray.copy(alpha = 0.2f))

                    SummaryRow(label = "Total", value = formatter.format(totalWithTip), isTotal = true)
                    SummaryRow(label = "Split by", value = "$numPeopleSplit people")

                    Spacer(modifier = Modifier.height(24.dp))

                    // Resultado en Grande dentro del Modal
                    Text(text = "Amount per person", color = TextSecondaryGray, fontSize = 12.sp)
                    Text(
                        text = formatter.format(splitResult),
                        color = ElectricBlueSolid,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
                    )

                    // Botones del Modal
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .background(ElectricCyanGradient, shape = RoundedCornerShape(16.dp))
                            .clip(RoundedCornerShape(16.dp))
                            .clickable {
                                showSummaryModal = false
                                Toast.makeText(context, "Payment Successful! \uD83D\uDE80", Toast.LENGTH_LONG).show()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("CONFIRM", color = DeepDarkBase, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Cancel",
                        color = TextSecondaryGray,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .clickable { showSummaryModal = false }
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}

// --- COMPONENTES VISUALES ---

@Composable
fun SummaryRow(label: String, value: String, isTotal: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = if (isTotal) TextPrimaryWhite else TextSecondaryGray,
            fontSize = if (isTotal) 18.sp else 16.sp,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Medium
        )
        Text(
            text = value,
            color = TextPrimaryWhite,
            fontSize = if (isTotal) 18.sp else 16.sp,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
fun TipSelectionCard(
    percentage: Int,
    tipFormatted: String,
    isSelected: Boolean,
    height: Dp,
    onClick: () -> Unit
) {
    val backgroundModifier = if (isSelected) {
        Modifier.background(ElectricBluePurpleGradient, shape = RoundedCornerShape(20.dp))
    } else {
        Modifier
            .background(CardBackground, shape = RoundedCornerShape(20.dp))
            .border(2.dp, ElectricBluePurpleGradient, shape = RoundedCornerShape(20.dp))
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .then(backgroundModifier),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$percentage%",
                color = TextPrimaryWhite,
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = if (percentage == 0) "No tip" else "+ $tipFormatted",
                color = if (isSelected) TextPrimaryWhite.copy(alpha = 0.8f) else TextSecondaryGray,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun CustomTipCard(
    textValue: String,
    onValueChange: (String) -> Unit,
    isSelected: Boolean,
    height: Dp,
    onClick: () -> Unit
) {
    val backgroundModifier = if (isSelected) {
        Modifier.background(ElectricBluePurpleGradient, shape = RoundedCornerShape(20.dp))
    } else {
        Modifier
            .background(CardBackground, shape = RoundedCornerShape(20.dp))
            .border(2.dp, TextSecondaryGray.copy(alpha = 0.5f), shape = RoundedCornerShape(20.dp))
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .then(backgroundModifier),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            BasicTextField(
                value = textValue,
                onValueChange = {
                    if (it.length <= 3 && it.all { char -> char.isDigit() }) {
                        onValueChange(it)
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = TextStyle(
                    color = TextPrimaryWhite,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                ),
                cursorBrush = SolidColor(TextPrimaryWhite),
                decorationBox = { innerTextField ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (textValue.isEmpty()) {
                            Text("0", color = TextPrimaryWhite.copy(alpha = 0.5f), fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
                        }
                        innerTextField()
                        Text("%", color = TextPrimaryWhite, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
            )
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (textValue.isNotEmpty()) "$textValue%" else "Custom",
                    color = if (textValue.isNotEmpty()) TextPrimaryWhite else TextSecondaryGray,
                    fontSize = if (textValue.isNotEmpty()) 26.sp else 18.sp,
                    fontWeight = if (textValue.isNotEmpty()) FontWeight.ExtraBold else FontWeight.Medium
                )
                if (textValue.isNotEmpty()) {
                    Text(
                        text = "Custom",
                        color = TextSecondaryGray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun RoundSplitButton(text: String, gradient: Brush, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(60.dp)
            .background(gradient, shape = CircleShape)
            .clip(CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = TextPrimaryWhite,
            fontSize = 32.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 2.dp)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, locale = "es")
@Composable
fun TipMasterProAppPreview() {
    MaterialTheme {
        TipMasterProApp()
    }
}