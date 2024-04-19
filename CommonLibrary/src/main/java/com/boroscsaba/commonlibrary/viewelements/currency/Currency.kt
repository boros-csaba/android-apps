package com.boroscsaba.commonlibrary.viewelements.currency

import java.text.DecimalFormat
import kotlin.math.abs

/**
 * Created by Boros Csaba
 */

class Currency(val id: Int, val currencyCode: String, val currencyPosition: PositionEnum, val displayName: String = "", val iconResourceId: Int? = null) {

    fun getAmountString(amount: Double): String {
        val format = DecimalFormat("###,###.##")
        val formattedAmount = format.format(abs(amount))

        var sign = ""
        if (amount < 0) sign = "-"
        return if (currencyPosition == PositionEnum.Before) {
            sign + displayName + formattedAmount
        } else {
            sign + formattedAmount + displayName
        }
    }
}
