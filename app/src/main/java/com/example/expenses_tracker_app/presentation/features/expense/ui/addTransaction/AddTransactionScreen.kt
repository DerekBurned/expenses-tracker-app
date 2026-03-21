package com.example.expenses_tracker_app.presentation.features.expense.ui.addTransaction

import android.widget.ListView
import androidx.compose.runtime.Composable


import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expenses_tracker_app.domain.model.ExpenseType
import com.example.expenses_tracker_app.domain.model.IncomeType
import com.example.expenses_tracker_app.domain.model.Transaction
import java.time.LocalDate
import java.util.UUID

// ─── Colors ───────────────────────────────────────────────────────────────────
private val IncomeGreen   = Color(0xFF00C896)
private val ExpenseRed    = Color(0xFFFF5C5C)
private val SurfaceDark   = Color(0xFF1C1C2E)
private val CardDark      = Color(0xFF28283E)
private val SubtleText    = Color(0xFF8A8AA8)
private val White         = Color(0xFFFFFFFF)

private val expenseCategories = ExpenseType.entries.toList()
private val incomeCategories  = IncomeType.entries.toList()

sealed class AddTransactionIntent {
    data class Submit(val transaction: Transaction) : AddTransactionIntent()
    object Cancel : AddTransactionIntent()
}


@Composable
fun AddTransactionScreen(
    onIntent: (AddTransactionIntent) -> Unit
) {
    var isExpense       by remember { mutableStateOf(true) }
    var amountText      by remember { mutableStateOf("") }
    var description     by remember { mutableStateOf("") }
    var selectedExpCat  by remember { mutableStateOf(expenseCategories.first()) }
    var selectedIncCat  by remember { mutableStateOf(incomeCategories.first()) }
    var showError       by remember { mutableStateOf(false) }

    val accentColor by animateColorAsState(
        targetValue = if (isExpense) ExpenseRed else IncomeGreen,
        animationSpec = tween(400), label = "accent"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(56.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = { onIntent(AddTransactionIntent.Cancel) }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = White)
                }
                Text(
                    text = "New Transaction",
                    color = White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(Modifier.height(32.dp))


            TypeToggle(
                isExpense = isExpense,
                accentColor = accentColor,
                onToggle = {
                    isExpense = it
                    showError = false
                }
            )

            Spacer(Modifier.height(32.dp))

            Text("Amount", color = SubtleText, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it.filter { c -> c.isDigit() || c == '.' } },
                placeholder = { Text("0.00", color = SubtleText) },
                leadingIcon = {
                    Text(
                        "$",
                        color = accentColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                isError = showError && amountText.isBlank(),
                colors = outlinedTextFieldColors(accentColor),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(color = White, fontSize = 18.sp)
            )
            if (showError && amountText.isBlank()) {
                Text("Please enter an amount", color = ExpenseRed, fontSize = 11.sp,
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp))
            }

            Spacer(Modifier.height(20.dp))

            Text("Description", color = SubtleText, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                placeholder = { Text("What was this for?", color = SubtleText) },
                singleLine = true,
                isError = showError && description.isBlank(),
                colors = outlinedTextFieldColors(accentColor),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(color = White)
            )
            if (showError && description.isBlank()) {
                Text("Please enter a description", color = ExpenseRed, fontSize = 11.sp,
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp))
            }

            Spacer(Modifier.height(24.dp))

            // ── Category Chips ────────────────────────────────────────────────
            Text("Category", color = SubtleText, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(12.dp))

            if (isExpense) {
                CategoryGrid(
                    categories = expenseCategories.map { it.name },
                    selected = selectedExpCat.name,
                    accentColor = accentColor,
                    onSelect = { name ->
                        selectedExpCat = expenseCategories.first { it.name == name }
                    }
                )
            } else {
                CategoryGrid(
                    categories = incomeCategories.map { it.name },
                    selected = selectedIncCat.name,
                    accentColor = accentColor,
                    onSelect = { name ->
                        selectedIncCat = incomeCategories.first { it.name == name }
                    }
                )
            }

            Spacer(Modifier.height(40.dp))

            // ── Submit Button ─────────────────────────────────────────────────
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull()
                    if (amount == null || description.isBlank()) {
                        showError = true
                        return@Button
                    }
                    val finalAmount = if (isExpense) -amount else amount
                    val transaction: Transaction = if (isExpense) {
                        Transaction.Expense(
                            localId    = UUID.randomUUID().toString(),
                            amount     = finalAmount,
                            description = description,
                            date       = LocalDate.now().toString(),
                            expenseType = selectedExpCat
                        )
                    } else {
                        Transaction.Income(
                            localId     = UUID.randomUUID().toString(),
                            amount      = finalAmount,
                            description = description,
                            date        = LocalDate.now().toString(),
                            incomeType  = selectedIncCat
                        )
                    }
                    onIntent(AddTransactionIntent.Submit(transaction))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = accentColor)
            ) {
                Spacer(Modifier.width(8.dp))
                Text(
                    text = if (isExpense) "Add Expense" else "Add Income",
                    color = White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

// ─── Type Toggle ──────────────────────────────────────────────────────────────
@Composable
private fun TypeToggle(
    isExpense: Boolean,
    accentColor: Color,
    onToggle: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(CardDark)
    ) {
        // Sliding pill
        val pillOffset by animateDpAsState(
            targetValue = if (isExpense) 0.dp else 160.dp,   // half of track width approx
            animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
            label = "pill"
        )

        Box(
            modifier = Modifier
                .padding(4.dp)
                .width(160.dp)
                .fillMaxHeight()
                .offset(x = pillOffset)
                .clip(RoundedCornerShape(13.dp))
                .background(accentColor)
        )

        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ToggleLabel("Expense", isExpense) { onToggle(true) }
            ToggleLabel("Income", !isExpense) { onToggle(false) }
        }
    }
}

@Composable
private fun RowScope.ToggleLabel(label: String, active: Boolean, onClick: () -> Unit) {
    val textColor by animateColorAsState(
        targetValue = if (active) White else SubtleText,
        animationSpec = tween(300), label = "labelColor"
    )
    val scale by animateFloatAsState(
        targetValue = if (active) 1f else 0.92f,
        animationSpec = spring(), label = "labelScale"
    )
    Text(
        text = label,
        color = textColor,
        fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
        fontSize = 14.sp,
        modifier = Modifier
            .weight(1f)
            .scale(scale)
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        textAlign = TextAlign.Center
    )
}

// ─── Category Grid ────────────────────────────────────────────────────────────
@Composable
private fun CategoryGrid(
    categories: List<String>,
    selected: String,
    accentColor: Color,
    onSelect: (String) -> Unit,
    onAddCategory: () -> Unit = {}
) {
    val useListView = categories.size > 9

    if (useListView) {
        // ── Scrollable list view ──────────────────────────────────────────
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            categories.forEach { name ->
                CategoryListItem(
                    label = name.replace("_", " "),
                    isSelected = name == selected,
                    accentColor = accentColor,
                    onClick = { onSelect(name) }
                )
            }
            AddCategoryListItem(accentColor = accentColor, onClick = onAddCategory)
        }
    } else {
        // ── Grid view ─────────────────────────────────────────────────────
        val allItems = categories + ADD_CATEGORY_SENTINEL
        val chunked = allItems.chunked(3)

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            chunked.forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    row.forEach { name ->
                        if (name == ADD_CATEGORY_SENTINEL) {
                            AddCategoryChip(
                                accentColor = accentColor,
                                onClick = onAddCategory,
                                modifier = Modifier.weight(1f)
                            )
                        } else {
                            CategoryChip(
                                label = name.replace("_", " "),
                                isSelected = name == selected,
                                accentColor = accentColor,
                                onClick = { onSelect(name) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        }
    }
}
@Composable
private fun CategoryListItem(
    label: String,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) accentColor.copy(alpha = 0.2f) else CardDark,
        animationSpec = tween(250), label = "listItemBg"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) accentColor else Color(0xFF3A3A5C),
        animationSpec = tween(250), label = "listItemBorder"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = if (isSelected) accentColor else White,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = accentColor,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun AddCategoryListItem(
    accentColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardDark)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add category",
            tint = accentColor,
            modifier = Modifier.size(16.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = "Add Category",
            color = accentColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun CategoryChip(
    label: String,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) accentColor.copy(alpha = 0.2f) else CardDark,
        animationSpec = tween(250), label = "chipBg"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) accentColor else Color(0xFF3A3A5C),
        animationSpec = tween(250), label = "chipBorder"
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) accentColor else SubtleText,
        animationSpec = tween(250), label = "chipText"
    )
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.04f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium), label = "chipScale"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

@Composable
private fun outlinedTextFieldColors(accentColor: Color) = OutlinedTextFieldDefaults.colors(
    focusedBorderColor   = accentColor,
    unfocusedBorderColor = Color(0xFF3A3A5C),
    errorBorderColor     = ExpenseRed,
    cursorColor          = accentColor,
    focusedLabelColor    = accentColor,
    unfocusedContainerColor = CardDark,
    focusedContainerColor   = CardDark,
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AddTransactionScreenPreview() {
    MaterialTheme {
        AddTransactionScreen(onIntent = {})
    }
}