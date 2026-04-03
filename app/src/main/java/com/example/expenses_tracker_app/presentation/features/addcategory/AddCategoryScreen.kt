package com.example.expenses_tracker_app.presentation.features.addcategory

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.expenses_tracker_app.presentation.features.addcategory.AddCategoryContract.ViewEffect
import com.example.expenses_tracker_app.presentation.features.addcategory.AddCategoryContract.ViewIntent
import com.example.expenses_tracker_app.presentation.features.addcategory.AddCategoryContract.ViewState

private val IncomeGreen = Color(0xFF00C896)
private val ExpenseRed  = Color(0xFFFF5C5C)
private val SurfaceDark = Color(0xFF1C1C2E)
private val CardDark    = Color(0xFF28283E)
private val SubtleText  = Color(0xFF8A8AA8)
private val White       = Color(0xFFFFFFFF)

@Composable
fun AddCategoryScreen(
    viewModel: AddCategoryViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is ViewEffect.NavigateBack -> onNavigateBack()
                is ViewEffect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { _ ->
        AddCategoryContent(
            state    = state,
            onIntent = viewModel::onIntent
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddCategoryContent(
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

            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier          = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = { onIntent(ViewIntent.BackClicked) }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = White)
                }
                Text(
                    text       = "New Category",
                    color      = White,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 20.sp,
                    modifier   = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(Modifier.height(12.dp))

            // Type badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(accentColor.copy(alpha = 0.15f))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text       = if (state.isExpense) "Expense Category" else "Income Category",
                    color      = accentColor,
                    fontSize   = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(32.dp))

            // Category name
            Text(
                text       = "Category Name",
                color      = SubtleText,
                fontSize   = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value         = state.categoryName,
                onValueChange = { onIntent(ViewIntent.NameChanged(it)) },
                placeholder   = { Text("e.g. Groceries", color = SubtleText) },
                singleLine    = true,
                isError       = state.nameError,
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor      = accentColor,
                    unfocusedBorderColor    = Color(0xFF3A3A5C),
                    errorBorderColor        = ExpenseRed,
                    cursorColor             = accentColor,
                    focusedLabelColor       = accentColor,
                    unfocusedContainerColor = CardDark,
                    focusedContainerColor   = CardDark
                ),
                shape    = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(color = White, fontSize = 16.sp)
            )
            if (state.nameError) {
                Text(
                    text     = "Please enter a category name",
                    color    = ExpenseRed,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                )
            }

            Spacer(Modifier.height(28.dp))

            // Emoji picker
            Text(
                text       = "Choose an Icon (optional)",
                color      = SubtleText,
                fontSize   = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(12.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement   = Arrangement.spacedBy(8.dp),
                modifier              = Modifier.fillMaxWidth()
            ) {
                state.availableEmojis.forEach { emoji ->
                    EmojiChip(
                        emoji       = emoji,
                        isSelected  = state.selectedEmoji == emoji,
                        accentColor = accentColor,
                        onClick     = { onIntent(ViewIntent.EmojiSelected(emoji)) }
                    )
                }
            }

            Spacer(Modifier.height(40.dp))

            // Save button
            Button(
                onClick  = { onIntent(ViewIntent.SaveClicked) },
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
                        text       = "Save Category",
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

@Composable
private fun EmojiChip(
    emoji: String,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue   = if (isSelected) accentColor.copy(alpha = 0.2f) else CardDark,
        animationSpec = tween(250),
        label         = "emojiBg"
    )
    val scale by animateFloatAsState(
        targetValue   = if (isSelected) 1.15f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label         = "emojiScale"
    )

    Box(
        modifier = Modifier
            .size(48.dp)
            .scale(scale)
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text      = emoji,
            fontSize  = 22.sp,
            textAlign = TextAlign.Center
        )
    }
}
