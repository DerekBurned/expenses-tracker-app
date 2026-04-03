package com.example.expenses_tracker_app.presentation.features.addtransaction

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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.expenses_tracker_app.domain.model.ExpenseType
import com.example.expenses_tracker_app.domain.model.IncomeType
import com.example.expenses_tracker_app.presentation.features.addtransaction.AddTransactionContract.ViewEffect
import com.example.expenses_tracker_app.presentation.features.addtransaction.AddTransactionContract.ViewIntent
import com.example.expenses_tracker_app.presentation.features.addtransaction.AddTransactionContract.ViewState
// AddNewCategoryDialog replaced by full AddCategoryScreen

// ── Colour tokens ─────────────────────────────────────────────────────────────

private val IncomeGreen = Color(0xFF00C896)
private val ExpenseRed  = Color(0xFFFF5C5C)
private val SurfaceDark = Color(0xFF1C1C2E)
private val CardDark    = Color(0xFF28283E)
private val SubtleText  = Color(0xFF8A8AA8)
private val White       = Color(0xFFFFFFFF)

private const val ADD_CATEGORY_SENTINEL = "__ADD__"

// ── Entry point ───────────────────────────────────────────────────────────────

/**
 * Injects the ViewModel, collects state, and wires one-time effects.
 * Everything below this composable is stateless.
 *
 * Uses [BaseMviViewModel.uiState] and [BaseMviViewModel.uiEffect] (the
 * base-class property names) instead of the old viewState/viewEffect fields.
 */
@Composable
fun AddTransactionScreen(
    viewModel: AddTransactionViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToAddCategory: (Boolean) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is ViewEffect.NavigateBack          -> onNavigateBack()
                is ViewEffect.NavigateToAddCategory -> onNavigateToAddCategory(effect.isExpense)
                is ViewEffect.ShowSnackbar          -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { _ ->
        AddTransactionContent(
            state    = state,
            onIntent = viewModel::onIntent
        )
    }
}

// ── Stateless content ─────────────────────────────────────────────────────────

@Composable
fun AddTransactionContent(
    state: ViewState,
    onIntent: (ViewIntent) -> Unit
) {
    val accentColor by animateColorAsState(
        targetValue   = if (state.isExpense) ExpenseRed else IncomeGreen,
        animationSpec = tween(400),
        label         = "accentColor"
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

            // ── Header ───────────────────────────────────────────────────────
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier          = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = { onIntent(ViewIntent.BackClicked) }) {
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

            // ── Type toggle ──────────────────────────────────────────────────
            TypeToggle(
                isExpense   = state.isExpense,
                accentColor = accentColor,
                onToggle    = { onIntent(ViewIntent.TypeToggled(it)) }
            )

            Spacer(Modifier.height(32.dp))

            // ── Amount ───────────────────────────────────────────────────────
            FieldLabel("Amount")
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value         = state.amountText,
                onValueChange = { onIntent(ViewIntent.AmountChanged(it)) },
                placeholder   = { Text("0.00", color = SubtleText) },
                leadingIcon   = {
                    Text(
                        text       = "$",
                        color      = accentColor,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 18.sp,
                        modifier   = Modifier.padding(start = 12.dp)
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine      = true,
                isError         = state.amountError,
                colors          = outlinedTextFieldColors(accentColor),
                shape           = RoundedCornerShape(14.dp),
                modifier        = Modifier.fillMaxWidth(),
                textStyle       = LocalTextStyle.current.copy(color = White, fontSize = 18.sp)
            )
            if (state.amountError) {
                ErrorText("Please enter an amount")
            }

            Spacer(Modifier.height(20.dp))

            // ── Description ──────────────────────────────────────────────────
            FieldLabel("Description")
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value         = state.description,
                onValueChange = { onIntent(ViewIntent.DescriptionChanged(it)) },
                placeholder   = { Text("What was this for?", color = SubtleText) },
                singleLine    = true,
                isError       = state.descriptionError,
                colors        = outlinedTextFieldColors(accentColor),
                shape         = RoundedCornerShape(14.dp),
                modifier      = Modifier.fillMaxWidth(),
                textStyle     = LocalTextStyle.current.copy(color = White)
            )
            if (state.descriptionError) {
                ErrorText("Please enter a description")
            }

            Spacer(Modifier.height(24.dp))

            // ── Category ─────────────────────────────────────────────────────
            FieldLabel("Category")
            Spacer(Modifier.height(12.dp))

            if (state.isExpense) {
                val customExpenseNames = state.customCategories
                    .filterKeys { it.startsWith("EXPENSE_") }
                    .values.toList()
                CategoryGrid(
                    builtInCategories = ExpenseType.entries.map { it.name },
                    customCategories  = customExpenseNames,
                    selected          = state.selectedExpenseCategory.name,
                    accentColor       = accentColor,
                    onAddCategory     = { onIntent(ViewIntent.AddCategoryClicked) },
                    onSelect          = { name ->
                        val type = ExpenseType.entries.firstOrNull { it.name == name }
                            ?: ExpenseType.DEFAULT
                        onIntent(ViewIntent.ExpenseCategorySelected(type))
                    }
                )
            } else {
                val customIncomeNames = state.customCategories
                    .filterKeys { it.startsWith("INCOME_") }
                    .values.toList()
                CategoryGrid(
                    builtInCategories = IncomeType.entries.map { it.name },
                    customCategories  = customIncomeNames,
                    selected          = state.selectedIncomeCategory.name,
                    accentColor       = accentColor,
                    onAddCategory     = { onIntent(ViewIntent.AddCategoryClicked) },
                    onSelect          = { name ->
                        val type = IncomeType.entries.firstOrNull { it.name == name }
                            ?: IncomeType.OTHER
                        onIntent(ViewIntent.IncomeCategorySelected(type))
                    }
                )
            }

            Spacer(Modifier.height(40.dp))

            // ── Submit button ────────────────────────────────────────────────
            Button(
                onClick  = { onIntent(ViewIntent.SubmitClicked) },
                enabled  = !state.isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape  = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = accentColor)
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(
                        modifier    = Modifier.size(22.dp),
                        color       = White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text       = if (state.isExpense) "Add Expense" else "Add Income",
                        color      = White,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 16.sp
                    )
                }
            }

            Spacer(Modifier.height(40.dp))
        }

    }
}

// ── Type toggle ───────────────────────────────────────────────────────────────

@Composable
private fun TypeToggle(
    isExpense: Boolean,
    accentColor: Color,
    onToggle: (Boolean) -> Unit
) {
    var trackWidthPx by remember { mutableStateOf(0) }
    val density      = LocalDensity.current
    val halfTrack: Dp = with(density) { (trackWidthPx / 2).toDp() }

    val pillOffset by animateDpAsState(
        targetValue   = if (isExpense) 0.dp else halfTrack,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label         = "pillOffset"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(CardDark)
            .onGloballyPositioned { coords -> trackWidthPx = coords.size.width }
    ) {
        Box(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth(0.5f)
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
private fun RowScope.ToggleLabel(
    label: String,
    active: Boolean,
    onClick: () -> Unit
) {
    val textColor by animateColorAsState(
        targetValue   = if (active) White else SubtleText,
        animationSpec = tween(300),
        label         = "toggleTextColor"
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
        textAlign  = TextAlign.Center,
        modifier   = Modifier
            .weight(1f)
            .scale(scale)
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp)
    )
}

// ── Category grid ─────────────────────────────────────────────────────────────

@Composable
private fun CategoryGrid(
    builtInCategories: List<String>,
    customCategories: List<String>,
    selected: String,
    accentColor: Color,
    onSelect: (String) -> Unit,
    onAddCategory: () -> Unit
) {
    val all = builtInCategories + customCategories

    if (all.size > 9) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            all.forEach { name ->
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
        val rows = (all + ADD_CATEGORY_SENTINEL).chunked(3)
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
        label         = "chipBgColor"
    )
    val textColor by animateColorAsState(
        targetValue   = if (isSelected) accentColor else SubtleText,
        animationSpec = tween(250),
        label         = "chipTextColor"
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
                fontWeight = FontWeight.SemiBold
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
        label         = "listItemBgColor"
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
private fun AddCategoryListItem(
    accentColor: Color,
    onClick: () -> Unit
) {
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

// ── Shared helpers ────────────────────────────────────────────────────────────

@Composable
private fun FieldLabel(text: String) {
    Text(
        text       = text,
        color      = SubtleText,
        fontSize   = 12.sp,
        fontWeight = FontWeight.Medium
    )
}

@Composable
private fun ErrorText(message: String) {
    Text(
        text     = message,
        color    = ExpenseRed,
        fontSize = 11.sp,
        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
    )
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