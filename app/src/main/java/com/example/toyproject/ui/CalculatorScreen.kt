package com.example.toyproject.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.toyproject.CalculatorViewModel
import com.example.toyproject.ui.theme.ToyProjectTheme

// 버튼 종류를 구분하기 위한 타입
private enum class ButtonType { NUMBER, OPERATOR, ACTION_CLEAR, ACTION_DELETE, EQUALS }

private data class CalcButtonSpec(val label: String, val type: ButtonType)

private val buttonRows = listOf(
    listOf(
        CalcButtonSpec("AC", ButtonType.ACTION_CLEAR),
        CalcButtonSpec("⌫", ButtonType.ACTION_DELETE),
        CalcButtonSpec("/", ButtonType.OPERATOR),
        CalcButtonSpec("*", ButtonType.OPERATOR),
    ),
    listOf(
        CalcButtonSpec("7", ButtonType.NUMBER),
        CalcButtonSpec("8", ButtonType.NUMBER),
        CalcButtonSpec("9", ButtonType.NUMBER),
        CalcButtonSpec("-", ButtonType.OPERATOR),
    ),
    listOf(
        CalcButtonSpec("4", ButtonType.NUMBER),
        CalcButtonSpec("5", ButtonType.NUMBER),
        CalcButtonSpec("6", ButtonType.NUMBER),
        CalcButtonSpec("+", ButtonType.OPERATOR),
    ),
    listOf(
        CalcButtonSpec("1", ButtonType.NUMBER),
        CalcButtonSpec("2", ButtonType.NUMBER),
        CalcButtonSpec("3", ButtonType.NUMBER),
        CalcButtonSpec(".", ButtonType.NUMBER),
    ),
)

@Composable
fun CalculatorScreen(
    modifier: Modifier = Modifier,
    viewModel: CalculatorViewModel = viewModel()
) {
    val expression by viewModel.expression.collectAsState()
    val result by viewModel.result.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        CalculatorDisplay(
            expression = expression,
            result = result,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        CalculatorButtonGrid(
            onButtonClick = { label ->
                when (label) {
                    "AC" -> viewModel.onClear()
                    "⌫" -> viewModel.onDelete()
                    "=" -> viewModel.onCalculate()
                    else -> viewModel.onInput(label)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun CalculatorDisplay(
    expression: String,
    result: String?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.End
    ) {
        CalculatorInputField(
            expression = expression,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        CalculatorResultText(
            result = result,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun CalculatorInputField(
    expression: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = expression,
        onValueChange = {},
        modifier = modifier.focusProperties { canFocus = false },
        readOnly = true,
        label = { Text("수식") },
        textStyle = MaterialTheme.typography.headlineMedium.copy(
            textAlign = TextAlign.End,
        ),
        singleLine = true,
    )
}

@Composable
fun CalculatorResultText(
    result: String?,
    modifier: Modifier = Modifier
) {
    Text(
        text = result ?: "",
        modifier = modifier,
        fontSize = 40.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.End,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
private fun CalculatorButtonGrid(
    onButtonClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        buttonRows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { spec ->
                    CalculatorButton(
                        spec = spec,
                        modifier = Modifier.weight(1f),
                        onClick = { onButtonClick(spec.label) }
                    )
                }
            }
        }

        // 마지막 행: 0 (2칸), = (2칸)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalculatorButton(
                spec = CalcButtonSpec("0", ButtonType.NUMBER),
                modifier = Modifier.weight(2f),
                onClick = { onButtonClick("0") }
            )
            CalculatorButton(
                spec = CalcButtonSpec("=", ButtonType.EQUALS),
                modifier = Modifier.weight(2f),
                onClick = { onButtonClick("=") }
            )
        }
    }
}

@Composable
private fun CalculatorButton(
    spec: CalcButtonSpec,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val colors = when (spec.type) {
        ButtonType.NUMBER -> ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        ButtonType.OPERATOR -> ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        )
        ButtonType.ACTION_CLEAR -> ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer,
        )
        ButtonType.ACTION_DELETE -> ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        )
        ButtonType.EQUALS -> ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        )
    }

    Button(
        onClick = onClick,
        modifier = modifier.height(64.dp),
        shape = RoundedCornerShape(12.dp),
        colors = colors,
    ) {
        Text(
            text = spec.label,
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            softWrap = false,
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun CalculatorScreenPreview() {
    ToyProjectTheme {
        CalculatorScreen()
    }
}
