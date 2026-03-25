package com.example.toyproject

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CalculatorViewModel : ViewModel() {

    private val calculator = ExpressionCalculator()

    private val _expression = MutableStateFlow("")
    val expression: StateFlow<String> = _expression.asStateFlow()

    private val _result = MutableStateFlow<String?>(null)
    val result: StateFlow<String?> = _result.asStateFlow()

    fun onInput(value: String) {
        _expression.update { it + value }
    }

    fun onDelete() {
        _expression.update { if (it.isNotEmpty()) it.dropLast(1) else it }
    }

    fun onClear() {
        _expression.value = ""
        _result.value = null
    }

    fun onCalculate() {
        val expr = _expression.value.ifBlank { return }
        _result.value = try {
            calculator.evaluate(expr).stripTrailingZeros().toPlainString()
        } catch (e: ArithmeticException) {
            "오류: ${e.message}"
        } catch (e: Exception) {
            "오류: 잘못된 수식"
        }
        _expression.value = ""
    }
}
