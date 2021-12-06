package hr.algebra.calculator.utils

import android.util.Log
import java.util.*

fun String.removeUnnecessaryZerosFromDoubleNumber(): String {
    var result = this
    val index = this.length

    if (this[index-1] == '0'){
        result = this.removeRange(index - 1, index)
        result.removeUnnecessaryZerosFromDoubleNumber()
    }

    return result
}

fun Double.isWholeNumber(): Boolean {
    return this - this.toInt() == 0.0
}


fun <E> LinkedList<E>.remove(startIndex: Int, endIndex: Int) {
    val totalItemsToRemove = endIndex - startIndex

    for (it in totalItemsToRemove downTo 0){
        this.removeAt(startIndex)
    }
}