package com.boroscsaba.commonlibrary.activities.helpers

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.boroscsaba.commonlibrary.ApplicationBase
import com.boroscsaba.commonlibrary.LoggingHelper
import com.boroscsaba.commonlibrary.helpers.PremiumHelper
import com.boroscsaba.commonlibrary.activities.AdsDisplayOptions
import com.boroscsaba.commonlibrary.activities.ActivityBase
import com.boroscsaba.commonlibrary.settings.AdsSettings
import com.boroscsaba.commonlibrary.settings.SettingsHelper
import com.boroscsaba.commonlibrary.settings.SettingsPopupHelper
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.boroscsaba.commonlibrary.R
import com.google.android.gms.ads.InterstitialAd


class AdHelper(private val activity: ActivityBase): AdListener() {

    private var adContainer: LinearLayout? = null
    private var adView: AdView? = null

    fun addAdView(adsOptions: AdsDisplayOptions, canShowAdConsentPopup: Boolean): AdView? {
        if (PremiumHelper.canShowAds()) {
            val adsSettings = SettingsHelper(activity.application).getAdsSetting()
            if (adsSettings == AdsSettings.NO_ADS) {
                if (canShowAdConsentPopup) {
                    val app = activity.application as ApplicationBase
                    val adSetting = app.settingsConfigurations.first { s -> s.name == SettingsHelper.ADS_SETTINGS }
                    LoggingHelper.logEvent(activity, "ad_consent_popup")
                    SettingsPopupHelper.showSettingPopup(adSetting, activity, false)
                }
            }
            else {
                adView = AdView(activity)
                adView?.adSize = adsOptions.adSize
                adView?.adUnitId = adsOptions.id
                adView?.adListener = this

                adContainer = activity.findViewById(adsOptions.container)
                if (adContainer != null) {
                    adContainer?.visibility = View.VISIBLE
                    adContainer?.addView(adView)
                    adView?.loadAd(getAdRequest())
                    return adView
                }
            }
        }
        return null
    }

    private fun getAdRequest(): AdRequest {
        val adsSettings = SettingsHelper(activity.application).getAdsSetting()
        val extras = Bundle()
        if (adsSettings == AdsSettings.NON_PERSONALIZED) {
            extras.putString("npa", "1")
        }
        return AdRequest.Builder()
                .addNetworkExtrasBundle(AdMobAdapter::class.java, extras)
                .addTestDevice("067B4E336DB4874DAA91BEF56F7E4013").build()
    }

    override fun onAdLoaded() {
        super.onAdLoaded()
        val imageView = getAdPlaceholderImageView() ?: return
        imageView.visibility = View.GONE
        adView?.visibility = View.VISIBLE
    }

    override fun onAdFailedToLoad(p0: Int) {
        super.onAdFailedToLoad(p0)
        val imageView = getAdPlaceholderImageView() ?: return
        imageView.visibility = View.VISIBLE
        adView?.visibility = View.GONE
        LoggingHelper.logEvent(activity, "PlaceholderAdView")
        imageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.boroscsaba.bouncingballsbrickbreaker"))
            LoggingHelper.logEvent(activity, "PlaceholderAdClick")
            activity.startActivity(intent)
        }
    }

    private fun getAdPlaceholderImageView(): ImageView? {
        val adContainer = this.adContainer ?: return null
        for (i in 0 until adContainer.childCount) {
            if (adContainer.getChildAt(i) is ImageView) {
                return adContainer.getChildAt(i) as ImageView
            }
        }
        return null
    }

    fun setupPlayAdButton(button: View) {
        val app = activity.applicationContext as ApplicationBase
        if (PremiumHelper.canShowAds()) { //todo majd mindig
            val interstitialAd = InterstitialAd(activity)
            button.setOnClickListener {
                if (interstitialAd.isLoaded) interstitialAd.show()
                button.visibility = View.GONE
            }
            interstitialAd.adUnitId = app.thankYouAdId
            interstitialAd.loadAd(getAdRequest())
            interstitialAd.adListener = object: AdListener() {
                override fun onAdLoaded() {
                    button.visibility = View.VISIBLE
                }

                override fun onAdClosed() {
                    interstitialAd.loadAd(getAdRequest())
                    button.visibility = View.GONE
                    Toast.makeText(activity, R.string.thank_you, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}