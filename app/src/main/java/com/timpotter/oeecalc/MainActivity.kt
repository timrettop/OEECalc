package com.timpotter.oeecalc

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Obtain the FirebaseAnalytics instance.
        firebaseAnalytics = Firebase.analytics
        setContent {
            OEECalculatorApp(firebaseAnalytics)
        }
    }
}

@Composable
fun OEECalculatorApp(firebaseAnalytics: FirebaseAnalytics) {
    var plannedProductionTime by remember { mutableStateOf("480") }
    var operatingTime by remember { mutableStateOf("450") }
    var totalCount by remember { mutableStateOf("1000") }
    var badCount by remember { mutableStateOf("50") }
    var idealCycleTime by remember { mutableStateOf("0.5") }
    var result by remember { mutableStateOf("OEE: -\nBreakdown:") }
    var errorFields by remember { mutableStateOf(setOf<String>()) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF5F5F5)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("OEE Calculator", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Text("Availability", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                Column(modifier = Modifier.padding(16.dp)) {
                    InputField("Planned Production Time (minutes)", plannedProductionTime, "Enter time in minutes", "plannedProductionTime" in errorFields) { plannedProductionTime = it }
                    InputField("Operating Time (minutes)", operatingTime, "Enter time in minutes", "operatingTime" in errorFields) { operatingTime = it }
                }
                Text("Performance", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                Column(modifier = Modifier.padding(16.dp)) {
                    InputField("Total Count (units)", totalCount, "Enter total produced units", "totalCount" in errorFields) { totalCount = it }
                    InputField("Ideal Cycle Time (minutes per unit)", idealCycleTime, "Enter cycle time per unit", "idealCycleTime" in errorFields) { idealCycleTime = it }
                }
                Text("Quality", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                Column(modifier = Modifier.padding(16.dp)) {
                    InputField("Reject Count (units)", badCount, "Enter number of reject units", "badCount" in errorFields) { badCount = it }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    val errors = mutableSetOf<String>()
                    val pTime = plannedProductionTime.toDoubleOrNull() ?: -1.0
                    val oTime = operatingTime.toDoubleOrNull() ?: -1.0
                    val tCount = totalCount.toDoubleOrNull() ?: -1.0
                    val bCount = badCount.toDoubleOrNull() ?: -1.0
                    val iCycle = idealCycleTime.toDoubleOrNull() ?: -1.0

                    if (pTime <= 0) errors.add("plannedProductionTime")
                    if (oTime <= 0) errors.add("operatingTime")
                    if (tCount <= 0) errors.add("totalCount")
                    if (bCount <= 0) errors.add("badCount")
                    if (iCycle <= 0) errors.add("idealCycleTime")

                    errorFields = errors

                    result = if (errors.isEmpty()) {
                        calculateOEE(pTime, oTime, tCount, bCount, iCycle, firebaseAnalytics)
                    } else {
                        "Invalid input: All values must be positive numbers."
                    }
                },
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Calculate", fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Text(result, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))
            }
        }
    }
}

@Composable
fun InputField(label: String, value: String, placeholder: String, isError: Boolean, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        isError = isError,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = if (isError) Color.Red else MaterialTheme.colorScheme.primary,
            unfocusedIndicatorColor = if (isError) Color.Red else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        ),
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    )
}

fun calculateOEE(plannedProductionTime: Double, operatingTime: Double, totalCount: Double, badCount: Double, idealCycleTime: Double, firebaseAnalytics: FirebaseAnalytics): String {
    val availability = operatingTime / plannedProductionTime
    val performance = (idealCycleTime * totalCount) / operatingTime
    val quality = (totalCount - badCount) / totalCount
    val oee = availability * performance * quality * 100

    val bundle = Bundle().apply {
        putDouble("planned_production_time", plannedProductionTime)
        putDouble("operating_time", operatingTime)
        putDouble("total_count", totalCount)
        putDouble("bad_count", badCount)
        putDouble("ideal_cycle_time", idealCycleTime)
        putDouble("oee_value", oee)
    }
    firebaseAnalytics.logEvent("oee_calculated", bundle)

    return "OEE: ${"%.2f".format(oee)}%\n\n" +
            "Breakdown:\n" +
            "Availability: ${"%.2f".format(availability * 100)}% (time efficiency)\n" +
            "Performance: ${"%.2f".format(performance * 100)}% (speed efficiency)\n" +
            "Quality: ${"%.2f".format(quality * 100)}% (good units produced)"
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    OEECalculatorApp(firebaseAnalytics = Firebase.analytics)
}