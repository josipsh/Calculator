package hr.algebra.calculator.model

import hr.algebra.calculator.utils.removeUnnecessaryZerosFromDoubleNumber

data class Value(
        var value: Double,
        var inputType: InputType
    ) : Variable {

    fun addDigit(number: String) {
        when (number) {
            "." -> {
                inputType = InputType.Double
            }
            else -> {
                val valueString = getStringFromValue()
                value = (valueString + number).toDouble()
            }
        }
    }

    private fun getStringFromValue(): String
        = if (inputType == InputType.Integer)
                value.toInt().toString()
            else
                value.toString().removeUnnecessaryZerosFromDoubleNumber()


    override fun toString(): String {
        return getStringFromValue()
    }
}