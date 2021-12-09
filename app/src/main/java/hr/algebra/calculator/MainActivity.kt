package hr.algebra.calculator

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import hr.algebra.calculator.databinding.ActivityMainBinding
import hr.algebra.calculator.model.InputType
import hr.algebra.calculator.model.Value
import hr.algebra.calculator.model.Variable
import hr.algebra.calculator.model.operators.*
import hr.algebra.calculator.utils.isWholeNumber
import hr.algebra.calculator.utils.remove
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val globalEquation = LinkedList<Variable>()
    private val defaultOperator = Add()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        setupListeners()
    }

    private fun setupListeners() {
        val numberListener = View.OnClickListener {
            addNumberToEquation((it as Button).text.toString())
            calculateAndPrintResult()
        }
        val operatorListener = View.OnClickListener {
            addOperatorToEquation((it as Button).text.toString())
            printEquation()
        }

        binding.btnZero.setOnClickListener( numberListener )
        binding.btnOne.setOnClickListener( numberListener )
        binding.btnTwo.setOnClickListener( numberListener )
        binding.btnThree.setOnClickListener( numberListener )
        binding.btnFour.setOnClickListener( numberListener )
        binding.btnFive.setOnClickListener( numberListener )
        binding.btnSix.setOnClickListener( numberListener )
        binding.btnSeven.setOnClickListener( numberListener )
        binding.btnEight.setOnClickListener( numberListener )
        binding.btnNine.setOnClickListener( numberListener )
        binding.btnDot.setOnClickListener( numberListener )

        binding.btnMultiply.setOnClickListener( operatorListener )
        binding.btnDivide.setOnClickListener( operatorListener )
        binding.btnPlus.setOnClickListener( operatorListener )
        binding.btnMinus.setOnClickListener( operatorListener )
        binding.btnOpenBracket.setOnClickListener( operatorListener )
        binding.btnClosedBracket.setOnClickListener( operatorListener )

        binding.btnEquals.setOnClickListener{
            try {
                val list = LinkedList<Variable>()
                list.addAll(globalEquation)

                val result = calculate(list)

                printResult(result, binding.tvEquation)
                binding.tvResult.text = ""
            }catch (e: java.lang.Exception){
                binding.tvResult.text = getString(R.string.error)
            }
        }

        binding.btnDelete.setOnClickListener{
            if (globalEquation.size != 0){
                globalEquation.removeAt(globalEquation.size-1)
            }

            if (globalEquation.isNotEmpty() && globalEquation[globalEquation.size -1] is Value){
                calculateAndPrintResult()
            }else{
                printEquation()
            }
            binding.tvResult.text = ""
        }

        binding.btnClear.setOnClickListener{
            globalEquation.clear()
            printEquation()
            binding.tvResult.text = ""
        }

    }

    private fun addOperatorToEquation(operator: String) {
        val lastIndex = globalEquation.size -1
        when (globalEquation.size) {
            0 -> {
                binding.tvResult.text = getString(R.string.error)
            }
            else -> {
                val addedOperator = createOperator(operator)

                if (addedOperator.isValid(globalEquation[lastIndex])){
                    globalEquation.add(addedOperator as Variable)
                }else{
                    binding.tvResult.text = getString(R.string.error)
                }
            }
        }
    }

    private fun createOperator(operator: String): Operator {
        return when(operator){
            getString(R.string.multiply) ->{
                Multiply()
            }
            getString(R.string.divide) ->{
                Divide()
            }
            getString(R.string.minus) ->{
                Subtract()
            }
            getString(R.string.open_bracket) ->{
                OpenBracket()
            }
            getString(R.string.closed_bracket) ->{
                ClosedBracket()
            }
            else -> {
                Add()
            }
        }
    }

    private fun addNumberToEquation(number: String) {
        val index = globalEquation.size
        when {
            index == 0 -> {
                globalEquation.add(Value(number.toDouble(), InputType.Integer))
            }
            globalEquation[index-1] is ClosedBracket -> {
                globalEquation.add(defaultOperator)
                globalEquation.add(Value(number.toDouble(), InputType.Integer))
            }
            globalEquation[index-1] is Operator -> {
                globalEquation.add(Value(number.toDouble(), InputType.Integer))
            }
            else -> {
                (globalEquation[index-1] as Value).addDigit(number)
            }
        }
    }

    private fun calculateAndPrintResult(): Double {
        return try {
            printEquation()

            val list = LinkedList<Variable>()
            list.addAll(globalEquation)
            val result = calculate(list)

            printResult(result, binding.tvResult)
            result
        }catch (e: Exception){
            binding.tvResult.text = getString(R.string.error)
            0.0
        }
    }

    private fun calculate(equation: LinkedList<Variable>): Double {
        if (equation.size == 1)(
            return (equation[0] as Value).value
        )

        val bracketEquation = LinkedList<Variable>()
        var isBracketOpen = false
        var openBracketIndex = 0

        for (it in  equation) {

            if (it is OpenBracket){
                isBracketOpen = true
                val index = equation.indexOf(it)

                if (openBracketIndex != index) bracketEquation.clear()

                openBracketIndex = index

            } else if (it is ClosedBracket && isBracketOpen){
                val result = calculateBracket(bracketEquation)
                equation[openBracketIndex] = Value(result, InputType.Double)
                equation.remove(openBracketIndex + 1, equation.indexOf(it))
                break
            } else if (isBracketOpen){
                bracketEquation.add(it)
            }

            if (equation.size - 1 == equation.indexOf(it) && bracketEquation.isNotEmpty()){
                val result = calculateBracket(bracketEquation)
                equation[openBracketIndex] = Value(result, InputType.Double)
                equation.remove(openBracketIndex + 1, equation.indexOf(it))
            }

        }

        return if (bracketEquation.isEmpty()){
            calculateBracket(equation)
        } else{
            calculate(equation)
        }
    }

    private fun calculateBracket(bracketEquation: LinkedList<Variable>): Double {

        if (bracketEquation.size == 1){
            return (bracketEquation[0] as Value).value
        }

        var highestOperatorIndex = 1
        var highestOperator = bracketEquation[1] as Operator

        for ((index, value) in bracketEquation.withIndex()){
            if (value is Operator
                && (value as Operator).priority > highestOperator.priority){
                highestOperator = value
                highestOperatorIndex = index
            }
        }

        val rez = highestOperator.calculate(
                bracketEquation[highestOperatorIndex-1] as Value,
                bracketEquation[highestOperatorIndex+1] as Value
            )

        bracketEquation[highestOperatorIndex - 1] = Value(rez, InputType.Double)

        with(bracketEquation) {
            removeAt(highestOperatorIndex+1)
            removeAt(highestOperatorIndex)
        }

        return calculateBracket(bracketEquation)

    }

    private fun printEquation() {
        val sb = StringBuilder()
        globalEquation.forEach {
            sb.append(it.toString())
        }
        sb.toString().also { binding.tvEquation.text = it }
    }

    private fun printResult(number: Double, resultHolder: TextView) {
        with(number){
            resultHolder.text = if (this.isWholeNumber())
                this.toInt().toString()
            else
                this.toString()
        }
    }
}

