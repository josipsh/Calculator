package hr.algebra.calculator.model.operators

import hr.algebra.calculator.model.Value
import hr.algebra.calculator.model.Variable

abstract class Operator: Comparable<Operator> {
    abstract val priority: Int

    abstract fun isValid(variableBefore: Variable): Boolean

    @Throws(ArithmeticException::class)
    abstract fun calculate(valueBeforeSymbol: Value, valueAfterSymbol: Value): Double

    override fun compareTo(other: Operator): Int {
        return -priority.compareTo(other.priority)
    }
}