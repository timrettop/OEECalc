package com.timpotter.oeecalc

import android.os.Bundle
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
    var plannedProductionTime by remember { mutableStateOf("480") }
    var operatingTime by remember { mutableStateOf("450") }
    var totalCount by remember { mutableStateOf("1000") }
    var goodCount by remember { mutableStateOf("950") }
    var idealCycleTime by remember { mutableStateOf("0.5") }
    var result by remember { mutableStateOf("OEE: -\nBreakdown:") }

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
    return "OEE: ${"%.2f".format(oee)}%\n\n" +
            "Breakdown:\n" +
            "Availability: ${"%.2f".format(availability * 100)}%\n" +
            "Performance: ${"%.2f".format(performance * 100)}%\n" +
            "Quality: ${"%.2f".format(quality * 100)}%"
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    OEECalculatorApp()
}