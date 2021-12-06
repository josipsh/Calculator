package hr.algebra.calculator.model.operators

import hr.algebra.calculator.model.Value
import hr.algebra.calculator.model.Variable

class Divide: Operator(), Variable {
    override val priority: Int
        get() = OperatorPriority.MEDIUM.value

    override fun isValid(variableBefore: Variable): Boolean =
        when (variableBefore) {
            is Value -> {
                true
            }
            is ClosedBracket -> {
                true
            }
            else -> {
                false
            }
        }

    @Throws(ArithmeticException::class)
    override fun calculate(valueBeforeSymbol: Value, valueAfterSymbol: Value): Double {
        if (valueAfterSymbol.value == 0.0){
            throw ArithmeticException("You can not divide with zero!")
        }
        return valueBeforeSymbol.value / valueAfterSymbol.value
    }

    override fun toString(): String {
        return "/"
    }
}