package hr.algebra.calculator.model.operators

import hr.algebra.calculator.model.Value
import hr.algebra.calculator.model.Variable

class OpenBracket : Operator(), Variable {
    override val priority: Int
        get() = OperatorPriority.HIGH.value

    override fun isValid(variableBefore: Variable): Boolean {
        return true
    }

    override fun calculate(valueBeforeSymbol: Value, valueAfterSymbol: Value): Double {
        throw Exception()
    }


    override fun toString(): String {
        return "("
    }
}