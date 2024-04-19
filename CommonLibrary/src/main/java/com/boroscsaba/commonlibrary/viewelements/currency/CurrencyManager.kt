package com.boroscsaba.commonlibrary.viewelements.currency

import com.boroscsaba.commonlibrary.R

/**
 * Created by Boros Csaba
 */
class CurrencyManager {

    companion object {

        private val currencies = arrayListOf(
                Currency(1, "USD", PositionEnum.Before, "$"),
                Currency(2, "EUR", PositionEnum.Before, "€"),
                Currency(3, "HUF", PositionEnum.After, "Ft"),
                Currency(4, "RON", PositionEnum.After, "lei"),
                Currency(5, "PHP", PositionEnum.Before, "₱"),
                Currency(6, "AUD", PositionEnum.Before, "$"),
                Currency(7, "INR", PositionEnum.Before, "₹"),
                Currency(8, "GBP", PositionEnum.Before, "£"),
                Currency(9, "MYR", PositionEnum.Before, "RM"),
                Currency(10, "CAD", PositionEnum.Before, "$"),
                Currency(11, "LBP", PositionEnum.Before, "ل.ل."),
                Currency(12, "CNY", PositionEnum.Before, "¥"),
                Currency(13, "BRL", PositionEnum.Before, "R$"),
                Currency(14, "RUB", PositionEnum.After, "₽"),
                Currency(15, "JPY", PositionEnum.Before, "¥"),
                Currency(16, "IDR", PositionEnum.Before, "Rp"),
                Currency(17, "MXN", PositionEnum.Before, "$"),
                Currency(18, "TRY", PositionEnum.Before, "₺"),
                Currency(19, "AED", PositionEnum.After, "د.إ"),
                Currency(20, "SEK", PositionEnum.After, "kr"),
                Currency(21, "CHF", PositionEnum.After, "CHF"),
                Currency(22, "TWD", PositionEnum.Before, "NT$"),
                Currency(23, "KRW", PositionEnum.Before, "₩"),
                Currency(24, "PLN", PositionEnum.After, "zł"),
                Currency(25, "ZAR", PositionEnum.Before, "R"),
                Currency(26, "UAH", PositionEnum.Before, "₴"),
                Currency(27, "SAR", PositionEnum.After, "ر.س"),
                Currency(28, "KHR", PositionEnum.Before, "៛"),
                Currency(29, "DKK", PositionEnum.After, "kr."),
                Currency(30, "ISK", PositionEnum.After, "kr"),
                Currency(31, "HRK", PositionEnum.After, "kn"),
                Currency(32, "SGD", PositionEnum.Before, "S$"),
                Currency(33, "KES", PositionEnum.After, "Ksh"),
                Currency(34, "TZS", PositionEnum.After, "TSh"),
                Currency(35, "ILS", PositionEnum.Before, "₪"),
                Currency(36, "NGN", PositionEnum.Before, "₦"),
                Currency(37, "NOK", PositionEnum.After, "kr"),
                Currency(38, "IDR", PositionEnum.Before, "Rp"),
                Currency(39, "GHS", PositionEnum.Before, "GH₵"),
                Currency(46, "NZD", PositionEnum.Before, "$"),
                Currency(40, "Star", PositionEnum.After, "", R.drawable.ic_currency_star),
                Currency(41, "Coin", PositionEnum.After, "", R.drawable.ic_currency_coin),
                Currency(42, "Heart", PositionEnum.After, "", R.drawable.ic_currency_heart),
                Currency(43, "Smiley", PositionEnum.After, "", R.drawable.ic_currency_smiley),
                Currency(44, "Teddy", PositionEnum.After, "", R.drawable.ic_currency_teddy),
                Currency(45, "Ladybug", PositionEnum.After, "", R.drawable.ic_currency_ladybug))

        fun getCurrencyList(onlyText: Boolean = false): ArrayList<Currency> {
            if (onlyText) {
                return ArrayList(currencies.filter { c -> c.iconResourceId == null })
            }
            return currencies
        }

        fun getCurrency(currencyCode: String): Currency {
            val currencies = getCurrencyList()
            return currencies.firstOrNull { it.currencyCode == currencyCode } ?: currencies.first()
        }

        fun getCurrency(id: Int): Currency {
            val currencies = getCurrencyList()
            return currencies.firstOrNull { it.id == id } ?: currencies.first()
        }
    }
}
