package org.hildan.accounting.taxes

import org.hildan.accounting.money.*

/**
 * A tax or a tax credit item that shows up in a tax table.
 */
data class TaxItem(
    /**
     * The name of this tax item.
     */
    val name: String,
    /**
     * An optional description of what this tax item is.
     */
    val description: String? = null,
    /**
     * The total amount that is owed/received for this tax item.
     * Usually this amount is rounded to whole euros, so it may differ from the sum of the sub items in the [breakdown].
     */
    val totalAmount: Amount,
    /**
     * Optional details about why or how we get this amount (possibly used as a tooltip on the amount).
     * If a [breakdown] is provided, this is usually null.
     */
    val details: String? = null,
    /**
     * An optional breakdown of the total amount into one or more parts.
     * If present, the sum of the [TaxSubItem.amount]s must be equal to [totalAmount] (apart from rounding differences).
     */
    val breakdown: List<TaxSubItem>? = null,
    /**
     * Type of item defining whether this money is received or owed (credit or debit).
     */
    val type: TaxItemType,
)

enum class TaxItemType {
    /**
     * Some part of the income.
     */
    INCOME,
    /**
     * A tax deduction, subtracted from some form of income to get a taxable income.
     * It is neither added nor removed from the total taxes.
     */
    TAX_DEDUCTION,
    /**
     * A tax credit, subtracted from taxes.
     */
    TAX_CREDIT,
    /**
     * A tax amount that must be paid.
     */
    TAX,
}

/**
 * A sub item of a tax item, used in a breakdown of the total amount of the item.
 */
data class TaxSubItem(
    /**
     * The name of this sub item, describing what it represents in a breakdown.
     */
    val name: String,
    /**
     * An optional general description of this sub item (not about the particular value, see [details]).
     */
    val description: String?,
    /**
     * The amount that this sub item represents from the total.
     */
    val amount: Amount,
    /**
     * Details about why or how we get this [amount] (possibly used as a tooltip on the amount).
     *
     * For example, for a wage tax, this could include information about how much of the salary fell into the tax
     * bracket that this sub item represents, and what the tax rate is for this tax bracket.
     */
    val details: String?,
)
