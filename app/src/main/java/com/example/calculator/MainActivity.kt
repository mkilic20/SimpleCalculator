package com.example.calculator

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Color
import kotlin.reflect.typeOf

class MainActivity : AppCompatActivity() {

    private lateinit var textDisplay: TextView
    private lateinit var resultDisplay: TextView
    private var operationAllowed = false
    private var decimalAllowed = true
    private var calculationDone = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btnEquals = findViewById<Button>(R.id.btnEquals)
        val btnPlus = findViewById<Button>(R.id.btnPlus)
        val btnMinus = findViewById<Button>(R.id.btnMinus)
        val btnMultiply = findViewById<Button>(R.id.btnMultiply)
        val btnDivide = findViewById<Button>(R.id.btnDivide)
        val btn9 = findViewById<Button>(R.id.btn9)
        val btn8 = findViewById<Button>(R.id.btn8)
        val btn7 = findViewById<Button>(R.id.btn7)
        val btn6 = findViewById<Button>(R.id.btn6)
        val btn5 = findViewById<Button>(R.id.btn5)
        val btn4 = findViewById<Button>(R.id.btn4)
        val btn3 = findViewById<Button>(R.id.btn3)
        val btn2 = findViewById<Button>(R.id.btn2)
        val btn1 = findViewById<Button>(R.id.btn1)
        val btn0 = findViewById<Button>(R.id.btn0)
        val btnClear = findViewById<Button>(R.id.btnClear)
        val btnDel = findViewById<Button>(R.id.btnDel)
        val btnDecimal = findViewById<Button>(R.id.btnDecimal)
        btnEquals.setBackgroundColor(Color.parseColor("#ED6E32"))
        btnPlus.setBackgroundColor(Color.parseColor("#ED6E32"))
        btnMinus.setBackgroundColor(Color.parseColor("#ED6E32"))
        btnMultiply.setBackgroundColor(Color.parseColor("#ED6E32"))
        btnDivide.setBackgroundColor(Color.parseColor("#ED6E32"))
        btn9.setBackgroundColor(Color.parseColor("#628B82"))
        btn8.setBackgroundColor(Color.parseColor("#628B82"))
        btn7.setBackgroundColor(Color.parseColor("#628B82"))
        btn6.setBackgroundColor(Color.parseColor("#628B82"))
        btn5.setBackgroundColor(Color.parseColor("#628B82"))
        btn4.setBackgroundColor(Color.parseColor("#628B82"))
        btn3.setBackgroundColor(Color.parseColor("#628B82"))
        btn2.setBackgroundColor(Color.parseColor("#628B82"))
        btn1.setBackgroundColor(Color.parseColor("#628B82"))
        btn0.setBackgroundColor(Color.parseColor("#628B82"))
        btnClear.setBackgroundColor(Color.DKGRAY)
        btnDecimal.setBackgroundColor(Color.DKGRAY)
        btnDel.setBackgroundColor(Color.DKGRAY)
        textDisplay = findViewById(R.id.textDisplay)
        resultDisplay = findViewById(R.id.resultDisplay)
    }

    fun onButtonClick(view: View) {
        val button = view as Button
        val buttonText = button.text.toString()

        when (buttonText) {
            "AC" -> {
                clearDisplay()
            }
            "Del" -> {
                deleteLastDigit()
            }
            "=" -> {
                performOperation()
            }
            "+", "-", "*", "/" -> {
                if (calculationDone) {
                    textDisplay.text = resultDisplay.text
                    resultDisplay.text = ""
                    calculationDone = false
                }
                handleOperator(buttonText)
            }
            else -> {
                if (calculationDone) {
                    textDisplay.text = ""
                    resultDisplay.text = ""
                    calculationDone = false
                }
                appendDigit(buttonText)
            }
        }
    }

    private fun deleteLastDigit() {
        calculationDone = false
        val currentText = textDisplay.text.toString()
        if (currentText.isNotEmpty()) {
            textDisplay.text = currentText.substring(0, currentText.length - 1)
        }
    }

    private fun appendDigit(digit: String) {
        val currentText = textDisplay.text.toString()
        if(digit == ".")
        {
            if(decimalAllowed) {
                textDisplay.text = currentText + digit
            }
            decimalAllowed = false
        }
        else {
            textDisplay.text = currentText + digit
        }
        operationAllowed = true
    }

    private fun clearDisplay() {
        textDisplay.text = ""
        resultDisplay.text = ""
        operationAllowed = false
        calculationDone = false
        decimalAllowed = true
    }

    private fun handleOperator(op: String) {
        if(operationAllowed)
        {
            val currentText = textDisplay.text.toString()
            textDisplay.text = currentText + op
            operationAllowed = false
            decimalAllowed = true
        }
    }

    fun splitTextView(): MutableList<Any> {
        val elementList = mutableListOf<Any>()
        var currentNumber = StringBuilder()

        for (char in textDisplay.text.toString()) {
            if (char.isDigit() || char == '.') {
                currentNumber.append(char)
            }
            else {
                if (currentNumber.isNotEmpty()) {
                    elementList.add(currentNumber.toString().toFloat())
                    currentNumber.clear()
                }
                elementList.add(char.toString())
            }
        }

        if (currentNumber.isNotEmpty()) {
            elementList.add(currentNumber.toString().toFloat())
        }

        return elementList
    }

    private fun calculateMulDiv(elementList: MutableList<Any>): MutableList<Any> {
        val resultList = mutableListOf<Any>()
        var i = 0

        while (i < elementList.size) {
            val current = elementList[i]
            if (current is Float) {
                resultList.add(current)
            }
            else if (current == "*" || current == "/") {
                if (i + 1 < elementList.size && elementList[i + 1] is Float) {
                    val nextNumber = elementList[i + 1] as Float
                    val prevNumber = resultList.last() as Float
                    val result = when (current) {
                        "*" -> prevNumber * nextNumber
                        else -> prevNumber / nextNumber
                    }
                    resultList[resultList.size - 1] = result
                    i++
                }
            }
            else {
                resultList.add(current)
            }
            i++
        }
        return resultList
    }

    private fun calculateAddSub(simpleList: MutableList<Any>): String {
        var result = simpleList[0] as Float

        var i = 1
        while (i < simpleList.size-1) {
            val operator = simpleList[i] as? String
            val nextDigit = simpleList[i + 1] as Float
            when (operator) {
                "+" -> result += nextDigit
                "-" -> result -= nextDigit
            }
            i += 2
        }

        if (checkInt(result)){
            return result.toInt().toString()
        }
        return result.toString()
    }


    private fun performOperation() {
        calculationDone = true
        val elementList = splitTextView()

        if (elementList.isNotEmpty()){
            val simpleList = calculateMulDiv(elementList)
            if (simpleList.isNotEmpty()){
                val returnString = calculateAddSub(simpleList)
                resultDisplay.text = returnString
                calculationDone = true
            }
        }
    }

    private fun checkInt(target: Float): Boolean {
        val intTarget = target.toInt()
        if ((target - intTarget) == 0f)
            return true
        return false
    }
}