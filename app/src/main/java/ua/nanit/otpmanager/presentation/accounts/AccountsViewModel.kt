package ua.nanit.otpmanager.presentation.accounts

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ua.nanit.otpmanager.domain.account.Account
import ua.nanit.otpmanager.domain.account.AccountInteractor
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class AccountsViewModel(
    private val dispatcher: CoroutineContext,
    private val interactor: AccountInteractor
) : ViewModel() {

    val accounts = MutableLiveData<List<Account>>()

    init {
        viewModelScope.launch(dispatcher) {
            accounts.postValue(interactor.getAll())
        }
    }

    fun editAccount() {

    }

    fun removeAccount() {

    }
}

class AccountsViewModelFactory @Inject constructor(
    private val interactor: AccountInteractor
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AccountsViewModel(Dispatchers.Default, interactor) as T
    }
}