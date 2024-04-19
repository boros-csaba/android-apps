package com.boroscsaba.commonlibrary.viewelements.currency

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.boroscsaba.commonlibrary.ApplicationBase
import com.boroscsaba.commonlibrary.R
import com.boroscsaba.commonlibrary.ThemeManager
import com.boroscsaba.commonlibrary.Utils
import com.boroscsaba.commonlibrary.views.TextView


class AmountWithCurrencyView: LinearLayout {

    var amount: Double = 0.0
    private var imageSize = Utils.convertPixelsToDp(28f, context)
    private var textSize = Utils.convertPixelsToDp(16f, context)
    private var styleName: String? = null
    private var textColor = Color.parseColor("#555555")

    constructor(context: Context): super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet): super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int): super(context, attrs, defStyle) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        val themeManager = (context.applicationContext as ApplicationBase).themeManager
        inflate(context, R.layout.amount_with_currency_layout,this)
        orientation = VERTICAL
        if (attrs != null) {
            val attributes = context.obtainStyledAttributes(attrs, R.styleable.AmountWithCurrencyView, 0, 0)
            try {
                imageSize = attributes.getDimension(R.styleable.AmountWithCurrencyView_imageSize, imageSize)
                textSize = attributes.getDimension(R.styleable.AmountWithCurrencyView_textSize, textSize)
                styleName = attributes.getString(R.styleable.AmountWithCurrencyView_styleName)
            } finally {
                attributes.recycle()
            }
        }
        textColor = themeManager.getColor(ThemeManager.PRIMARY_TEXT_COLOR, styleName)
    }

    fun setup(amount: Double, currency: Currency) {
        this.amount = amount
        val textView = findViewById<TextView>(R.id.amountText)
        val imageView = findViewById<ImageView>(R.id.currencyIcon)
        textView.text = currency.getAmountString(amount)
        textView.setTextColor(textColor)
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
        if (currency.iconResourceId != null) {
            imageView.visibility = View.VISIBLE
            imageView.setImageResource(currency.iconResourceId)
            imageView.layoutParams.width = imageSize.toInt()
            imageView.layoutParams.height = imageSize.toInt()
            imageView.requestLayout()
        }
        else {
            imageView.visibility = View.GONE
        }
    }

    fun setTextColor(color: Int) {
        val textView = findViewById<TextView>(R.id.amountText)
        textView.setTextColor(color)
    }

    fun setTextSize(size: Float) {
        this.textSize = size
        this.imageSize = size
    }
}