package com.boroscsaba.commonlibrary.helpers

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.*
import com.android.billingclient.api.BillingClient.SkuType
import com.android.billingclient.api.SkuDetailsParams
import com.android.billingclient.api.BillingFlowParams
import com.boroscsaba.commonlibrary.*
import io.fabric.sdk.android.Fabric

/**
 * Created by Boros Csaba
 */

class PremiumHelper private constructor(val app: ApplicationBase) {

    init {
        firstInstallTime = app.packageManager.getPackageInfo(app.applicationContext.packageName, 0).firstInstallTime
    }

    fun initIsPremiumFlagFromSharedPreferences() {
        isPremium = app.getSharedPreferences(app.getString(R.string.shared_preferences_name), Context.MODE_PRIVATE).getBoolean("isPremium", false)
    }

    private fun refreshIsPremiumFlagInSharedPreferences() {
        val editor = app.getSharedPreferences(app.getString(R.string.shared_preferences_name), Context.MODE_PRIVATE).edit()
        if (BuildConfig.DEBUG) {
            isPremium = true
        }
        editor.putBoolean("isPremium", isPremium)
        editor.apply()
    }

    fun refreshIsPremiumFlagAsync() {
        Handler(Looper.getMainLooper()).post{ refreshIsPremiumFlag() }
    }

    private fun refreshIsPremiumFlag() {
        app.appStartLog += " refreshIsPremium "
        val billingClient = newBuilder(app).setListener(MyPurchaseUpdateListener(app)).build()
        billingClient.startConnection(object : LoggingBillingClientStateListener(app) {
            override fun onBillingSetupFinished(responseCode: Int) {
                super.onBillingSetupFinished(responseCode)
                if (responseCode == BillingResponse.OK) {
                    val purchases = billingClient.queryPurchases(SkuType.INAPP)
                    val premiumPurchase = purchases?.purchasesList?.firstOrNull { p -> p.sku == premiumSKU }
                    if (premiumPurchase != null) {
                        isPremium = true
                        orderId = premiumPurchase.orderId
                    } else if (BuildConfig.DEBUG) {
                        isPremium = true
                        orderId = "DEBUG Order Id"
                    }
                }

                if (billingClient.isReady) {
                    billingClient.endConnection()
                }

                if (!isPremium) {
                    val otherAppsHelper = OtherAppsHelper(app)
                    if (otherAppsHelper.getApps().all { a -> a.installed }) {
                        isPremium = true
                        if (BuildConfig.DEBUG) {
                            isPremium = false
                        }
                        orderId = "OAPs"
                        LoggingHelper.logEvent(app, "OtherAppsPremium")
                        if (!otherAppsHelper.wasPremium()) {
                            app.showOtherAppsPremiumWonPopup = true
                        }
                    } else {
                        if (otherAppsHelper.wasPremium()) {
                            app.showOtherAppsPremiumLostPopup = true
                        }
                    }
                    otherAppsHelper.savePremiumState(isPremium)
                }
                refreshIsPremiumFlagInSharedPreferences()
            }
        })
    }

    fun getPremiumPrice(listener: (price: String?, error: Boolean) -> Unit) {
        val billingClient = newBuilder(app).setListener(MyPurchaseUpdateListener(app)).build()
        billingClient.startConnection(object: LoggingBillingClientStateListener(app) {
            override fun onBillingSetupFinished(responseCode: Int) {
                super.onBillingSetupFinished(responseCode)
                if (responseCode == BillingResponse.OK) {
                    val params = SkuDetailsParams.newBuilder()
                    params.setSkusList(listOf(premiumSKU)).setType(SkuType.INAPP)
                    billingClient.querySkuDetailsAsync(params.build()) { queryResponseCode, skuDetailsList ->
                        if (queryResponseCode == BillingResponse.OK) {
                            val sku = skuDetailsList.find{ s -> s.sku == premiumSKU }
                            if (sku == null) {
                                LoggingHelper.logEvent(app, "BillingError", "SKU query returned null")
                                listener(null, true)
                            } else {
                                listener(sku.price, false)
                            }
                        } else {
                            LoggingHelper.logEvent(app, "BillingError", "SKU query failed: $queryResponseCode")
                            listener(null, true)
                        }
                        billingClient.endConnection()
                    }
                }
                else {
                    listener(null, true)
                    if (billingClient.isReady) {
                        billingClient.endConnection()
                    }
                }
            }
        })
    }

    fun launchPurchaseFlow(activity: Activity, listener: ((bought: Boolean, error: Boolean) -> Unit)) {
        val billingClient = newBuilder(app).setListener(MyPurchaseUpdateListener(app, listener)).build()
        billingClient.startConnection(object: LoggingBillingClientStateListener(app) {
            override fun onBillingSetupFinished(responseCode: Int) {
                super.onBillingSetupFinished(responseCode)
                if (responseCode == BillingResponse.OK) {
                    val flowParams = BillingFlowParams.newBuilder()
                            .setSku(premiumSKU)
                            .setType(SkuType.INAPP)
                            .build()
                    billingClient.launchBillingFlow(activity, flowParams)
                }
                else {
                    billingClient.endConnection()
                }
            }
        })
    }

    companion object : SingletonHolder<PremiumHelper, ApplicationBase>(::PremiumHelper) {
        private var firstInstallTime: Long = 0
        const val premiumSKU = "premium"
        var isPremium : Boolean = false
        var orderId: String = ""

        fun canShowAds(): Boolean {
            if (BuildConfig.DEBUG) {
                return true
            }
            val time = System.currentTimeMillis()
            val noAdsPeriod = (1 * 24 * 60 * 60 * 1000).toLong()
            return !isPremium && firstInstallTime + noAdsPeriod < time
        }
    }

    class MyPurchaseUpdateListener constructor(val app: ApplicationBase, private val listener: ((bought: Boolean, error: Boolean) -> Unit)? = null) : PurchasesUpdatedListener {
        override fun onPurchasesUpdated(responseCode: Int, purchases: MutableList<Purchase>?) {
            if (responseCode == BillingResponse.OK){
                if (purchases?.find{p -> p.sku == premiumSKU } != null) {
                    listener?.invoke(true, false)
                }
                else {
                    listener?.invoke(false, false)
                }
            }
            else if (responseCode == BillingResponse.USER_CANCELED) {
                LoggingHelper.logEvent(app, "Billing_cancelled")
                listener?.invoke(false, false)
            }
            else {
                LoggingHelper.logException(Exception("Billing purchase error: $responseCode"), app)
                LoggingHelper.logEvent(app, "BillingError", "Purchase error: $responseCode")
                listener?.invoke(false, true)
            }
        }
    }

    open class LoggingBillingClientStateListener(private val context: Context) : BillingClientStateListener {
        override fun onBillingSetupFinished(responseCode: Int) {
            if (responseCode == BillingResponse.OK) {

            }
            else {
                if (!Fabric.isInitialized()){
                    val fabricHelper = LoggingHelper()
                    fabricHelper.initialize(context)
                }
                LoggingHelper.logEvent(context, "BillingError", "App Launch connection error: $responseCode")
            }
        }

        override fun onBillingServiceDisconnected() {
        }
    }
}
