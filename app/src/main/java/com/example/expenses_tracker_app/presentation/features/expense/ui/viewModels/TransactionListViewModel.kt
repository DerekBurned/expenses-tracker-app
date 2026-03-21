
import androidx.lifecycle.viewModelScope
import com.example.expenses_tracker_app.domain.model.Transaction
import com.example.expenses_tracker_app.domain.repository.IExpenseRepository
import com.example.expenses_tracker_app.presentation.features.expense.TransactionsContract
import com.example.expenses_tracker_app.presentation.features.expense.TransactionUIModel
import com.example.expenses_tracker_app.presentation.mvi.BaseMviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionListViewModel @Inject constructor(
    private val repo: IExpenseRepository
) : BaseMviViewModel<
        TransactionsContract.State,
        TransactionsContract.Intent,
        TransactionsContract.Effect
        >(initialState = TransactionsContract.State()) {

    private val _state = MutableStateFlow(TransactionsContract.State())
    val state: StateFlow<TransactionsContract.State> = _state
        .onStart { fetchExpenses() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Companion.WhileSubscribed(5000L),
            initialValue = TransactionsContract.State()
        )

    private val _effect = MutableSharedFlow<TransactionsContract.Effect>()
    val effect = _effect.asSharedFlow()

    fun handleIntent(intent: TransactionsContract.Intent) {
        when (intent) {
            is TransactionsContract.Intent.LoadTransactions ->
                viewModelScope.launch { fetchExpenses() }
            is TransactionsContract.Intent.DeleteTransaction ->
                removeExpense(intent.id)
            is TransactionsContract.Intent.AddTransactionClicked ->
                viewModelScope.launch { _effect.emit(TransactionsContract.Effect.NavigateToAddExpense) }
            else -> Unit
        }
    }

    private fun fetchExpenses() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val data: List<Transaction> = repo.getAllTransaction()
                val contracts = data.map { transaction ->
                    when (transaction) {
                        is Transaction.Expense -> TransactionUIModel(
                            id = transaction.localId,
                            title = transaction.description,
                            amount = transaction.amount,
                            category = transaction.expenseType.name
                        )
                        is Transaction.Income -> TransactionUIModel(
                            id = transaction.localId,
                            title = transaction.description,
                            amount = transaction.amount,
                            category = transaction.incomeType.name
                        )
                    }
                }
                _state.update {
                    it.copy(
                        transactions = contracts,
                        balance      = data.sumOf { t -> t.amount },
                        isLoading    = false
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
                _effect.emit(TransactionsContract.Effect.ShowError(e.message ?: "Unknown error"))
            }
        }
    }

    private fun removeExpense(id: String) {
        val updatedList = _state.value.transactions.filterNot { it.id == id }
        viewModelScope.launch { repo.deleteTransaction(id) }
        _state.update { currentState ->
            currentState.copy(
                transactions = updatedList,
                balance      = updatedList.sumOf { it.amount }
            )
        }
    }

    override fun onIntent(intent: TransactionsContract.Intent) {
        handleIntent(intent)
    }


}