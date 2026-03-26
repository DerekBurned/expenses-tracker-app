package com.example.expenses_tracker_app.presentation.features.expense.ui.addTransaction

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.expenses_tracker_app.domain.model.ExpenseType
import com.example.expenses_tracker_app.domain.model.IncomeType
import com.example.expenses_tracker_app.domain.model.Transaction
import com.example.expenses_tracker_app.presentation.features.expense.ui.AddNewCategoryScreen.AddNewCategoryDialog
import com.example.expenses_tracker_app.presentation.features.expense.ui.viewModels.AddTransactionViewModel
import java.time.LocalDate
import java.util.UUID

private val IncomeGreen = Color(0xFF00C896)
private val ExpenseRed  = Color(0xFFFF5C5C)
private val SurfaceDark = Color(0xFF1C1C2E)
private val CardDark    = Color(0xFF28283E)
private val SubtleText  = Color(0xFF8A8AA8)
private val White       = Color(0xFFFFFFFF)

private const val ADD_CATEGORY_SENTINEL = "__ADD__"

private val expenseCategories = ExpenseType.entries.toList()
private val incomeCategories  = IncomeType.entries.toList()

@Composable
fun AddTransactionScreen(
    viewModel: AddTransactionViewModel,
    onNavigateBack: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is AddTransactionViewModel.Effect.NavigateBack ->
                    onNavigateBack()
                is AddTransactionViewModel.Effect.ShowError ->
                    snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { _ ->
        AddTransactionContent(
            onSubmit = { viewModel.addTransaction(it) },
            onCancel = onNavigateBack,
            isSaving = viewModel.isSaving
        )
    }
}

@Composable
fun AddTransactionContent(
    onSubmit: (Transaction) -> Unit,
    onCancel: () -> Unit,
    isSaving: Boolean = false
) {
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var isExpense      by remember { mutableStateOf(true) }
    var amountText     by remember { mutableStateOf("") }
    var description    by remember { mutableStateOf("") }
    var selectedExpCat by remember { mutableStateOf(expenseCategories.first()) }
    var selectedIncCat by remember { mutableStateOf(incomeCategories.first()) }
    var showError      by remember { mutableStateOf(false) }
    //TESTING
    var customExpenseCategories by remember { mutableStateOf<List<String>>(emptyList()) }
    var customIncomeCategories  by remember { mutableStateOf<List<String>>(emptyList()) }

    val accentColor by animateColorAsState(
        targetValue   = if (isExpense) ExpenseRed else IncomeGreen,
        animationSpec = tween(400),
        label         = "accent"
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
                modifier          = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onCancel) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = White)
                }
                Text(
                    text       = "New Transaction",
                    color      = White,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 20.sp,
                    modifier   = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(Modifier.height(32.dp))

            TypeToggle(
                isExpense   = isExpense,
                accentColor = accentColor,
                onToggle    = { isExpense = it; showError = false }
            )

            Spacer(Modifier.height(32.dp))

            Text("Amount", color = SubtleText, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value         = amountText,
                onValueChange = { new -> amountText = new.filter { c -> c.isDigit() || c == '.' } },
                placeholder   = { Text("0.00", color = SubtleText) },
                leadingIcon   = {
                    Text(
                        "$",
                        color      = accentColor,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 18.sp,
                        modifier   = Modifier.padding(start = 12.dp)
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine      = true,
                isError         = showError && amountText.isBlank(),
                colors          = outlinedTextFieldColors(accentColor),
                shape           = RoundedCornerShape(14.dp),
                modifier        = Modifier.fillMaxWidth(),
                textStyle       = LocalTextStyle.current.copy(color = White, fontSize = 18.sp)
            )
            if (showError && amountText.isBlank()) {
                Text(
                    "Please enter an amount",
                    color    = ExpenseRed,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                )
            }

            Spacer(Modifier.height(20.dp))

            Text("Description", color = SubtleText, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value         = description,
                onValueChange = { description = it },
                placeholder   = { Text("What was this for?", color = SubtleText) },
                singleLine    = true,
                isError       = showError && description.isBlank(),
                colors        = outlinedTextFieldColors(accentColor),
                shape         = RoundedCornerShape(14.dp),
                modifier      = Modifier.fillMaxWidth(),
                textStyle     = LocalTextStyle.current.copy(color = White)
            )
            if (showError && description.isBlank()) {
                Text(
                    "Please enter a description",
                    color    = ExpenseRed,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                )
            }

            Spacer(Modifier.height(24.dp))

            Text("Category", color = SubtleText, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(12.dp))

            if (isExpense) {
                CategoryGrid(
                    categories    = expenseCategories.map { it.name } + customExpenseCategories,
                    selected      = selectedExpCat.name,
                    accentColor   = accentColor,
                    onAddCategory = { showAddCategoryDialog = true },
                    onSelect      = { name ->
                        selectedExpCat = expenseCategories.firstOrNull { it.name == name }
                            ?: expenseCategories.first()
                    }
                )
            } else {
                CategoryGrid(
                    categories    = incomeCategories.map { it.name } + customIncomeCategories,
                    selected      = selectedIncCat.name,
                    accentColor   = accentColor,
                    onAddCategory = { showAddCategoryDialog = true },
                    onSelect      = { name ->
                        selectedIncCat = incomeCategories.firstOrNull { it.name == name }
                            ?: incomeCategories.first()
                    }
                )
            }

// Dialog


            Spacer(Modifier.height(40.dp))

            Button(
                // FIX 1: disabled while saving — prevents multiple taps queuing
                // multiple inserts before the screen has a chance to close
                onClick  = {
                    val amount = amountText.toDoubleOrNull()
                    if (amount == null || description.isBlank()) {
                        showError = true
                        return@Button
                    }
                    val finalAmount = if (isExpense) -amount else amount
                    val transaction: Transaction = if (isExpense) {
                        Transaction.Expense(
                            localId     = UUID.randomUUID().toString(),
                            amount      = finalAmount,
                            description = description,
                            date        = LocalDate.now().toString(),
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
                    onSubmit(transaction)
                },
                enabled  = !isSaving,   // disabled while save is in flight
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape  = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = accentColor)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier  = Modifier.size(22.dp),
                        color     = White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text       = if (isExpense) "Add Expense" else "Add Income",
                        color      = White,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 16.sp
                    )
                }
            }

            Spacer(Modifier.height(40.dp))

        }
        if (showAddCategoryDialog) {
            AddNewCategoryDialog(
                isExpense = isExpense,
                onDismiss = { showAddCategoryDialog = false },
                onConfirm = { name ->
                    if (isExpense) {
                        customExpenseCategories = customExpenseCategories + name
                    } else {
                        customIncomeCategories = customIncomeCategories + name
                    }
                    showAddCategoryDialog = false
                }
            )
        }

    }
}

// ─── Type toggle ──────────────────────────────────────────────────────────────

@Composable
private fun TypeToggle(
    isExpense: Boolean,
    accentColor: Color,
    onToggle: (Boolean) -> Unit
) {
    // FIX 2: measure the actual track width at runtime so the pill offset
    // is always exactly half the track, regardless of screen density or size.
    // The old hardcoded 160.dp was based on an assumed screen width and caused
    // the pill to only slide within the first half of the toggle on many devices.
    var trackWidthPx by remember { mutableStateOf(0) }
    val density      = LocalDensity.current
    val halfTrack: Dp = with(density) { (trackWidthPx / 2).toDp() }

    val pillOffset by animateDpAsState(
        targetValue   = if (isExpense) 0.dp else halfTrack,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label         = "pill"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(CardDark)
            .onGloballyPositioned { coords -> trackWidthPx = coords.size.width }
    ) {
        // Pill — occupies exactly half the track width
        Box(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth(0.5f)          // always exactly 50 % of the track
                .fillMaxHeight()
                .offset(x = pillOffset)
                .clip(RoundedCornerShape(13.dp))
                .background(accentColor)
        )

        Row(
            modifier              = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            ToggleLabel(label = "Expense", active = isExpense,  onClick = { onToggle(true) })
            ToggleLabel(label = "Income",  active = !isExpense, onClick = { onToggle(false) })
        }
    }
}

@Composable
private fun RowScope.ToggleLabel(label: String, active: Boolean, onClick: () -> Unit) {
    val textColor by animateColorAsState(
        targetValue   = if (active) White else SubtleText,
        animationSpec = tween(300),
        label         = "toggleText"
    )
    val scale by animateFloatAsState(
        targetValue   = if (active) 1f else 0.92f,
        animationSpec = spring(),
        label         = "toggleScale"
    )
    Text(
        text       = label,
        color      = textColor,
        fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
        fontSize   = 14.sp,
        modifier   = Modifier
            .weight(1f)
            .scale(scale)
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        textAlign  = TextAlign.Center
    )
}

// ─── Category grid ────────────────────────────────────────────────────────────

@Composable
private fun CategoryGrid(
    categories: List<String>,
    selected: String,
    accentColor: Color,
    onSelect: (String) -> Unit,
    onAddCategory: () -> Unit = {}
) {
    if (categories.size > 9) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            categories.forEach { name ->
                CategoryListItem(
                    label       = name.replace("_", " "),
                    isSelected  = name == selected,
                    accentColor = accentColor,
                    onClick     = { onSelect(name) }
                )
            }
            AddCategoryListItem(accentColor = accentColor, onClick = onAddCategory)
        }
    } else {
        val allItems = categories + ADD_CATEGORY_SENTINEL
        val rows     = allItems.chunked(3)
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            rows.forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    row.forEach { name ->
                        if (name == ADD_CATEGORY_SENTINEL) {
                            AddCategoryChip(
                                accentColor = accentColor,
                                onClick     = onAddCategory,
                                modifier    = Modifier.weight(1f)
                            )
                        } else {
                            CategoryChip(
                                label       = name.replace("_", " "),
                                isSelected  = name == selected,
                                accentColor = accentColor,
                                onClick     = { onSelect(name) },
                                modifier    = Modifier.weight(1f)
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
private fun CategoryChip(
    label: String,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor by animateColorAsState(
        targetValue   = if (isSelected) accentColor.copy(alpha = 0.2f) else CardDark,
        animationSpec = tween(250),
        label         = "chipBg"
    )
    val textColor by animateColorAsState(
        targetValue   = if (isSelected) accentColor else SubtleText,
        animationSpec = tween(250),
        label         = "chipText"
    )
    val scale by animateFloatAsState(
        targetValue   = if (isSelected) 1.04f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label         = "chipScale"
    )
    Box(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text       = label,
            color      = textColor,
            fontSize   = 11.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            textAlign  = TextAlign.Center,
            maxLines   = 1
        )
    }
}

@Composable
private fun AddCategoryChip(
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(CardDark)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector        = Icons.Default.Add,
                contentDescription = "Add category",
                tint               = accentColor,
                modifier           = Modifier.size(14.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text       = "Add",
                color      = accentColor,
                fontSize   = 11.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign  = TextAlign.Center
            )
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
        targetValue   = if (isSelected) accentColor.copy(alpha = 0.2f) else CardDark,
        animationSpec = tween(250),
        label         = "listBg"
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text       = label,
            color      = if (isSelected) accentColor else White,
            fontSize   = 14.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
        if (isSelected) {
            Icon(
                imageVector        = Icons.Default.Check,
                contentDescription = "Selected",
                tint               = accentColor,
                modifier           = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun AddCategoryListItem(accentColor: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardDark)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector        = Icons.Default.Add,
            contentDescription = "Add category",
            tint               = accentColor,
            modifier           = Modifier.size(16.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text       = "Add Category",
            color      = accentColor,
            fontSize   = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun outlinedTextFieldColors(accentColor: Color) = OutlinedTextFieldDefaults.colors(
    focusedBorderColor      = accentColor,
    unfocusedBorderColor    = Color(0xFF3A3A5C),
    errorBorderColor        = ExpenseRed,
    cursorColor             = accentColor,
    focusedLabelColor       = accentColor,
    unfocusedContainerColor = CardDark,
    focusedContainerColor   = CardDark
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AddTransactionContentPreview() {
    MaterialTheme {
        AddTransactionContent(onSubmit = {}, onCancel = {})
    }
}