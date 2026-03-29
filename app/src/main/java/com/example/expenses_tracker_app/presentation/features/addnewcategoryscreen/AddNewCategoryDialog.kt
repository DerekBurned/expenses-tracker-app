package com.example.expenses_tracker_app.presentation.features.addnewcategoryscreen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties

// ── Colour tokens (mirrored from AddTransactionScreen) ───────────────────────
private val IncomeGreen = Color(0xFF00C896)
private val ExpenseRed  = Color(0xFFFF5C5C)
private val SurfaceDark = Color(0xFF1C1C2E)
private val CardDark    = Color(0xFF28283E)
private val SubtleText  = Color(0xFF8A8AA8)
private val White       = Color(0xFFFFFFFF)

/**
 * Dialog that lets the user type a new custom category name and confirm it.
 *
 * @param isExpense   Whether the parent screen is in "expense" mode
 *                    (drives the accent colour, matching [com.example.expenses_tracker_app.presentation.features.expense.ui.addTransaction.AddTransactionScreen]).
 * @param onDismiss   Called when the user taps outside or presses Cancel.
 * @param onConfirm   Called with the trimmed category name when the user confirms.
 *                    The dialog does **not** close itself — the caller is responsible
 *                    for removing it from composition (e.g. by toggling a `showDialog`
 *                    flag inside [onConfirm]).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNewCategoryDialog(
    isExpense: Boolean = true,
    onDismiss: () -> Unit,
    onConfirm: (name: String) -> Unit,
) {
    val accentColor by animateColorAsState(
        targetValue   = if (isExpense) ExpenseRed else IncomeGreen,
        animationSpec = tween(400),
        label         = "accent"
    )

    var categoryName by remember { mutableStateOf("") }
    var showError    by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    // Autofocus the text field when the dialog appears
    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    fun tryConfirm() {
        if (categoryName.isBlank()) {
            showError = true
        } else {
            onConfirm(categoryName.trim())
        }
    }

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        properties       = DialogProperties(usePlatformDefaultWidth = false),
        modifier         = Modifier.padding(horizontal = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(SurfaceDark)
                .border(
                    width = 1.dp,
                    color = Color(0xFF3A3A5C),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(24.dp)
        ) {
            // ── Title ────────────────────────────────────────────────────────
            Text(
                text       = "New Category",
                color      = White,
                fontSize   = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text     = "Give your custom category a name.",
                color    = SubtleText,
                fontSize = 13.sp
            )

            Spacer(Modifier.height(20.dp))

            // ── Input ────────────────────────────────────────────────────────
            Text(
                text       = "Category name",
                color      = SubtleText,
                fontSize   = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value         = categoryName,
                onValueChange = {
                    categoryName = it
                    if (showError) showError = false
                },
                placeholder     = { Text("e.g. Subscriptions", color = SubtleText) },
                singleLine      = true,
                isError         = showError,
                colors          = outlinedTextFieldColors(accentColor),
                shape           = RoundedCornerShape(14.dp),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction      = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { tryConfirm() }),
                textStyle       = LocalTextStyle.current.copy(color = White, fontSize = 15.sp),
                modifier        = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
            )

            if (showError) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text     = "Please enter a category name",
                    color    = ExpenseRed,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            Spacer(Modifier.height(28.dp))

            // ── Buttons ──────────────────────────────────────────────────────
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Cancel
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(CardDark)
                        .clickable(onClick = onDismiss)
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text       = "Cancel",
                        color      = SubtleText,
                        fontSize   = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Confirm
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(accentColor.copy(alpha = 0.15f))
                        .border(
                            width = 1.dp,
                            color = accentColor.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(14.dp)
                        )
                        .clickable(onClick = ::tryConfirm)
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector        = Icons.Default.Check,
                            contentDescription = null,
                            tint               = accentColor,
                            modifier           = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text       = "Add",
                            color      = accentColor,
                            fontSize   = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// ── Helpers ───────────────────────────────────────────────────────────────────

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

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFF1C1C2E)
@Composable
private fun AddNewCategoryDialogExpensePreview() {
    AddNewCategoryDialog(
        isExpense = true,
        onDismiss = {},
        onConfirm = {}
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF1C1C2E)
@Composable
private fun AddNewCategoryDialogIncomePreview() {
    AddNewCategoryDialog(
        isExpense = false,
        onDismiss = {},
        onConfirm = {}
    )
}