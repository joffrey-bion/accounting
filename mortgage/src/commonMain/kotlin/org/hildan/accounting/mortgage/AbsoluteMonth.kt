package org.hildan.accounting.mortgage

data class AbsoluteMonth(val year: Int, val month: Int) {

    fun next() = AbsoluteMonth(
        year = if (month == 12) year + 1 else year,
        month = month % 12 + 1,
    )

    override fun toString(): String = "$year-${month.toString().padStart(2, '0')}"
}
