package com.example.toyproject

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

/**
 * 사칙연산(+, -, *, /) 수식 문자열을 BigDecimal로 계산하는 클래스.
 *
 * 지원 기능:
 * - 사칙연산: +, -, *, /
 * - 괄호: (, )
 * - 음수: -1, -(3+2)
 * - 소수: 1.5 * 2.0
 *
 * 사용 예시:
 * ```
 * val calculator = ExpressionCalculator()
 * val result = calculator.evaluate("1 + 2 * 3")  // 7
 * val result2 = calculator.evaluate("(1 + 2) * 3")  // 9
 * ```
 */
class ExpressionCalculator {

    companion object {
        private val DIVISION_MATH_CONTEXT = MathContext(20, RoundingMode.HALF_UP)
    }

    private var input: String = ""
    private var pos: Int = 0

    /**
     * 수식 문자열을 계산합니다.
     * 수식이 불완전하거나 끝에 연산자가 남아있는 경우, 유효한 부분까지만 계산한 결과를 반환합니다.
     * 예: "3+2*4/" → 11, "1+2+" → 3
     *
     * @param expression 계산할 수식 문자열 (예: "1 + 2 * 3")
     * @return 계산 결과 BigDecimal
     * @throws IllegalArgumentException 수식이 비어있거나 시작부터 파싱할 수 없을 때
     * @throws ArithmeticException 0으로 나눌 때
     */
    fun evaluate(expression: String): BigDecimal {
        input = expression.replace(" ", "")
        if (input.isEmpty()) throw IllegalArgumentException("수식이 비어있습니다.")
        pos = 0

        return parseExpression()
    }

    // expression = term (('+' | '-') term)*
    // 오른쪽 피연산자 파싱 실패 시 연산자 소비 전으로 되돌리고 현재 결과 반환
    private fun parseExpression(): BigDecimal {
        var result = parseTerm()

        while (pos < input.length && (input[pos] == '+' || input[pos] == '-')) {
            val savedPos = pos
            val op = input[pos++]
            try {
                val term = parseTerm()
                result = if (op == '+') result.add(term) else result.subtract(term)
            } catch (e: IllegalArgumentException) {
                pos = savedPos
                break
            }
        }

        return result
    }

    // term = factor (('*' | '/') factor)*
    // 오른쪽 피연산자 파싱 실패 시 연산자 소비 전으로 되돌리고 현재 결과 반환
    private fun parseTerm(): BigDecimal {
        var result = parseFactor()

        while (pos < input.length && (input[pos] == '*' || input[pos] == '/')) {
            val savedPos = pos
            val op = input[pos++]
            try {
                val factor = parseFactor()
                result = if (op == '*') {
                    result.multiply(factor)
                } else {
                    if (factor.compareTo(BigDecimal.ZERO) == 0) {
                        throw ArithmeticException("0으로 나눌 수 없습니다.")
                    }
                    result.divide(factor, DIVISION_MATH_CONTEXT)
                }
            } catch (e: IllegalArgumentException) {
                pos = savedPos
                break
            }
        }

        return result
    }

    // factor = number | '-' factor | '+' factor | '(' expression ')'
    private fun parseFactor(): BigDecimal {
        if (pos >= input.length) {
            throw IllegalArgumentException("수식이 불완전합니다.")
        }

        return when (input[pos]) {
            '(' -> {
                pos++ // '(' 소비
                val result = parseExpression()
                if (pos >= input.length || input[pos] != ')') {
                    throw IllegalArgumentException("닫는 괄호 ')'가 없습니다.")
                }
                pos++ // ')' 소비
                result
            }
            '-' -> {
                pos++
                parseFactor().negate()
            }
            '+' -> {
                pos++
                parseFactor()
            }
            else -> parseNumber()
        }
    }

    private fun parseNumber(): BigDecimal {
        val start = pos
        var hasDot = false

        while (pos < input.length && (input[pos].isDigit() || (input[pos] == '.' && !hasDot))) {
            if (input[pos] == '.') hasDot = true
            pos++
        }

        if (start == pos) {
            throw IllegalArgumentException("숫자가 예상되는 위치 $pos 에서 '${input[pos]}' 발견")
        }

        return BigDecimal(input.substring(start, pos))
    }
}
