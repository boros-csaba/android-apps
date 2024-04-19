package com.boroscsaba.commonlibrary.drawer

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View

import androidx.appcompat.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import com.boroscsaba.commonlibrary.ApplicationBase
import com.boroscsaba.commonlibrary.LoggingHelper
import com.boroscsaba.commonlibrary.helpers.PremiumHelper
import com.boroscsaba.commonlibrary.R
import com.boroscsaba.commonlibrary.activities.ActivityBase
import com.bumptech.glide.Glide


class GoPremiumActivity: ActivityBase(), androidx.viewpager.widget.ViewPager.OnPageChangeListener {

    private val dots = ArrayList<TextView>()

    private val defaultImageResources = arrayOf(R.drawable.dark_mode, R.drawable.remove_ads, R.drawable.upcoming_features)
    private val defaultTitleResources = arrayOf(R.string.dark_mode, R.string.activity_go_premium_remove_ads_title, R.string.activity_go_premium_upcoming_features_title)
    private val defaultDescriptionResources = arrayOf(R.string.dark_mode_description, R.string.activity_go_premium_remove_ads_description, R.string.activity_go_premium_upcoming_features_description)

    init {
        options.layout = R.layout.activity_go_premium
        options.toolbarId = R.id.toolbar
        options.canShowAdConsentPopup = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LoggingHelper.logEvent(this, "GoPremiumActivity")

        val premiumHelper = PremiumHelper.getInstance(application as ApplicationBase)
        premiumHelper.getPremiumPrice{ price, error ->
            val loadingGif = findViewById<ProgressBar>(R.id.loadingGif)
            val buyButton = findViewById<Button>(R.id.buyButton)
            loadingGif.visibility = View.GONE
            buyButton.visibility = View.VISIBLE
            if (error) {
                buyButton.text = getString(R.string.error)
            }
            else {
                buyButton.text = price
                buyButton.setOnClickListener {
                    premiumHelper.launchPurchaseFlow(this) { bought, error ->
                        if (bought) {
                            buyButton.text = getString(R.string.thank_you)
                            buyButton.isClickable = false
                            val alertDialog = AlertDialog.Builder(this).create()
                            alertDialog.setTitle(R.string.congratulations)
                            alertDialog.setMessage(getString(R.string.go_premium_congratulations_message))
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK") { _, _ -> }
                            try { alertDialog.show() }
                            catch (e: Exception) {}
                            PremiumHelper.getInstance(application as ApplicationBase).refreshIsPremiumFlagAsync()
                        } else if (error) {
                            buyButton.text = getString(R.string.error)
                            buyButton.isClickable = false
                        }
                    }
                }
            }
        }

        val app = (application as ApplicationBase)
        val imageResources = app.premiumFeatureImageResources.plus(defaultImageResources)
        val titleResources = app.premiumFeatureTitleResources.plus(defaultTitleResources)
        val descriptionResources = app.premiumFeatureDescriptionResources.plus(defaultDescriptionResources)

        val viewPager = findViewById<androidx.viewpager.widget.ViewPager>(R.id.viewPager)
        viewPager.adapter = PremiumFeaturesPagerAdapter(this, imageResources, titleResources, descriptionResources)
        viewPager.addOnPageChangeListener(this)
        addDots(imageResources)
    }

    override fun setListeners() {
    }

    private fun addDots(imageResources: Array<Int>) {
        val linearLayout = findViewById<LinearLayout>(R.id.dots)
         imageResources.forEach { _ ->
             val dotTextView = TextView(this)
             dotTextView.text = getText(R.string.dot)
             dotTextView.setTextColor(Color.parseColor("#bbbbbb"))
             dotTextView.textSize = 10f
             val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
             layoutParams.marginStart = 15
             layoutParams.marginEnd = 15
             dotTextView.layoutParams = layoutParams
             dots.add(dotTextView)
             linearLayout.addView(dotTextView)
         }
        dots[0].setTextColor(Color.parseColor("#888888"))
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
        dots.forEach{ d -> d.setTextColor(Color.parseColor("#bbbbbb")) }
        dots[position].setTextColor(Color.parseColor("#888888"))
    }

    class PremiumFeaturesPagerAdapter(private val context: Context, private val imageResources: Array<Int>, private val titleResources: Array<Int>, private val descriptionResources: Array<Int>): androidx.viewpager.widget.PagerAdapter() {

        override fun isViewFromObject(view: View, obj: Any): Boolean {
            return view == obj
        }

        override fun getCount(): Int {
            return imageResources.size
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = layoutInflater.inflate(R.layout.premium_feature_layout, container, false)

            val imageView = view.findViewById<ImageView>(R.id.image)
            val titleTextView = view.findViewById<TextView>(R.id.title)
            val descriptionTextView = view.findViewById<TextView>(R.id.description)

            Glide.with(view).load(imageResources[position]).into(imageView)
            titleTextView.text = context.getText(titleResources[position])
            descriptionTextView.text = context.getText(descriptionResources[position])

            container.addView(view)
            return view
        }

        override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
            container.removeView(obj as View)
        }
    }
}
