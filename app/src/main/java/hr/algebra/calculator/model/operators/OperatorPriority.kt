package hr.algebra.calculator.model.operators

enum class OperatorPriority(val value: Int){
    LOW(1),
    MEDIUM_LOW(2),
    MEDIUM(3),
    MEDIUM_0HIGH(4),
    HIGH(5)
}