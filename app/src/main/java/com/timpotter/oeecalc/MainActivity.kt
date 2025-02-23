package com.timpotter.oeecalc

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OEECalculatorApp()
        }
    }
}

@Composable
fun OEECalculatorApp() {
    var plannedProductionTime by remember { mutableStateOf("") }
    var operatingTime by remember { mutableStateOf("") }
    var totalCount by remember { mutableStateOf("") }
    var goodCount by remember { mutableStateOf("") }
    var idealCycleTime by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("OEE: -") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("OEE Calculator", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        InputField("Planned Production Time", plannedProductionTime) { plannedProductionTime = it }
        InputField("Operating Time", operatingTime) { operatingTime = it }
        InputField("Total Count", totalCount) { totalCount = it }
        InputField("Good Count", goodCount) { goodCount = it }
        InputField("Ideal Cycle Time", idealCycleTime) { idealCycleTime = it }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            result = calculateOEE(
                plannedProductionTime.toDoubleOrNull() ?: 0.0,
                operatingTime.toDoubleOrNull() ?: 0.0,
                totalCount.toDoubleOrNull() ?: 0.0,
                goodCount.toDoubleOrNull() ?: 0.0,
                idealCycleTime.toDoubleOrNull() ?: 0.0
            )
        }) {
            Text("Calculate")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(result, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun InputField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    )
}

fun calculateOEE(plannedProductionTime: Double, operatingTime: Double, totalCount: Double, goodCount: Double, idealCycleTime: Double): String {
    if (plannedProductionTime == 0.0 || operatingTime == 0.0 || totalCount == 0.0 || idealCycleTime == 0.0) {
        return "Invalid input"
    }
    val availability = operatingTime / plannedProductionTime
    val performance = (idealCycleTime * totalCount) / operatingTime
    val quality = goodCount / totalCount
    val oee = availability * performance * quality * 100
    return "OEE: ${"%.2f".format(oee)}%"
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    OEECalculatorApp()
}
