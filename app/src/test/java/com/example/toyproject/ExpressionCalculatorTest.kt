package com.example.toyproject

import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

class ExpressionCalculatorTest {

    private lateinit var calculator: ExpressionCalculator

    @Before
    fun setUp() {
        calculator = ExpressionCalculator()
    }

    // ── 기본 사칙연산 ────────────────────────────────────────────

    @Test
    fun `덧셈`() {
        assertResult("1 + 2", "3")
    }

    @Test
    fun `뺄셈`() {
        assertResult("5 - 3", "2")
    }

    @Test
    fun `곱셈`() {
        assertResult("3 * 4", "12")
    }

    @Test
    fun `나눗셈`() {
        assertResult("10 / 4", "2.5")
    }

    // ── 연산자 우선순위 ──────────────────────────────────────────

    @Test
    fun `곱셈이 덧셈보다 먼저`() {
        assertResult("1 + 2 * 3", "7")
    }

    @Test
    fun `나눗셈이 뺄셈보다 먼저`() {
        assertResult("10 - 6 / 2", "7")
    }

    @Test
    fun `복합 우선순위`() {
        assertResult("2 + 3 * 4 - 6 / 2", "11")
    }

    // ── 괄호 ────────────────────────────────────────────────────

    @Test
    fun `괄호로 우선순위 변경`() {
        assertResult("(1 + 2) * 3", "9")
    }

    @Test
    fun `중첩 괄호`() {
        assertResult("((2 + 3) * (4 - 1))", "15")
    }

    // ── 음수 ────────────────────────────────────────────────────

    @Test
    fun `음수 피연산자`() {
        assertResult("-3 + 5", "2")
    }

    @Test
    fun `음수 괄호`() {
        assertResult("-(3 + 2)", "-5")
    }

    @Test
    fun `음수끼리 곱셈`() {
        assertResult("-2 * -3", "6")
    }

    // ── 소수 ────────────────────────────────────────────────────

    @Test
    fun `소수 덧셈`() {
        assertResult("1.5 + 2.5", "4.0")
    }

    @Test
    fun `소수 나눗셈`() {
        assertResult("1 / 3", "0.33333333333333333333")
    }

    // ── 공백 처리 ────────────────────────────────────────────────

    @Test
    fun `공백 없는 수식`() {
        assertResult("1+2*3", "7")
    }

    @Test
    fun `공백 여러 개`() {
        assertResult("10  /  2", "5")
    }

    // ── 예외 케이스 ──────────────────────────────────────────────

    @Test(expected = ArithmeticException::class)
    fun `0으로 나누기`() {
        calculator.evaluate("5 / 0")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `빈 문자열`() {
        calculator.evaluate("")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `닫는 괄호 누락`() {
        calculator.evaluate("(1 + 2")
    }

    // ── 부분 연산 (불완전한 수식) ─────────────────────────────────

    @Test
    fun `끝에 덧셈 연산자`() {
        assertResult("1 + 2 +", "3")
    }

    @Test
    fun `끝에 곱셈 연산자`() {
        assertResult("3 + 2 * 4 /", "11")
    }

    @Test
    fun `끝에 나눗셈 연산자`() {
        assertResult("5 /", "5")
    }

    @Test
    fun `잘못된 문자 포함 시 유효 부분까지 연산`() {
        assertResult("1 + a", "1")
    }

    // ── 헬퍼 ────────────────────────────────────────────────────

    private fun assertResult(expression: String, expected: String) {
        val result = calculator.evaluate(expression)
        assertEquals(
            "수식 '$expression' 의 결과가 다릅니다.",
            BigDecimal(expected).stripTrailingZeros(),
            result.stripTrailingZeros()
        )
    }
}
