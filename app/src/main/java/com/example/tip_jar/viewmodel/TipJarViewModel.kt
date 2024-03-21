package com.example.tip_jar.viewmodel

import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tip_jar.database.entity.TipHistory
import com.example.tip_jar.di.repository.SavedPaymentsRepository
import com.example.tip_jar.ui.composables.toDateString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject
import kotlin.math.round

@HiltViewModel
class TipJarViewModel @Inject constructor(private val repository: SavedPaymentsRepository) : ViewModel() {


    val amountValue = mutableStateOf("")
    val peopleCount = mutableIntStateOf(1)
    val tipValue = mutableStateOf("10")
    val isTakePhotoChecked = mutableStateOf(false)
    val totalTip = mutableDoubleStateOf(0.0)
    val imageData = mutableStateOf("")
    val perPerson = mutableDoubleStateOf(0.0)
    val paymentHistory = mutableStateOf<List<TipHistory>>(emptyList())
    val amountErrorVisible = mutableStateOf(false)
    val tipErrorVisible = mutableStateOf(false)
    var cachedPaymentList = listOf<TipHistory>()
    var isSearchStarting = true
    var isSearching = mutableStateOf(false)
    var query = mutableStateOf("")

    init{
        calculateTotal()
        loadPaymentHistory()
    }


    fun searchPaymentList() = viewModelScope.launch {
            val listToSearch = if(isSearchStarting){
                paymentHistory.value
            }else{
                cachedPaymentList
            }
            if(query.value.isEmpty()){
                paymentHistory.value = cachedPaymentList
                isSearching.value = false
                isSearchStarting = true
                return@launch
            }

            val results = listToSearch.filter {
                it.amount.contains(query.value.trim(), ignoreCase = true) ||
                        it.totalTip.toString().contains(query.value.trim(), ignoreCase = true) ||
                        it.timestamp.toDateString("yyyy MMMM dd")
                            .contains(query.value.trim(), ignoreCase = true)
            }

            if(isSearchStarting){
                cachedPaymentList = paymentHistory.value
                isSearchStarting = false
            }

            paymentHistory.value = results
            isSearching.value = true
        }



    fun loadPaymentHistory () = viewModelScope.launch {
        paymentHistory.value = repository.paymentHistory()
    }


    fun addPayment () = viewModelScope.launch {
        val imageString = let{
            if(isTakePhotoChecked.value){
                imageData.value
            }else{
                imageData.value = ""
                imageData.value
            }
        }
        repository.addPayment(
            TipHistory(
                System.currentTimeMillis(),
                amountValue.value,
                peopleCount.value,
                tipValue.value,
                totalTip.value,
                perPerson.value,
                imageString,
                isTakePhotoChecked.value
            )
        )
    }

    fun deletePayment (tipHistory: TipHistory) = viewModelScope.launch{
        repository.deletePayment(tipHistory)
        paymentHistory.value = paymentHistory.value.filter { it != tipHistory }
        cachedPaymentList = cachedPaymentList.filter { it != tipHistory }
    }

    fun calculateTotal() = viewModelScope.launch {
        val amount = let {
            if(amountValue.value.isEmpty()){
                0.0
            }else{
                try{
                    amountValue.value.toDouble()
                }catch (e : Exception){
                    0.0
                }
            }
        }
        val tip = let {
            if(tipValue.value.isEmpty()){
                0.0
            }else{
                try{
                    tipValue.value.toDouble()
                }catch (e : Exception){
                    0.0
                }
            }
        }

        var convertTip = tip / 100.0
        totalTip.value = convertTip * amount
        perPerson.value = totalTip.value / peopleCount.value
    }

    fun amountValidation(input : String) : Boolean {
        var isValid = true
        if(input.isEmpty()){
            isValid = false
        }else{
            try{
                input.toDouble()
            }catch (e : Exception){
                isValid = false
            }
        }
        return isValid
    }

    fun tipValidation(input : String) : Boolean {
        var isValid = true
        if(input.isEmpty()){
            isValid = false
        }else{
            try{
                input.toDouble()
            }catch (e : Exception){
                isValid = false
            }
        }
        return isValid
    }
}