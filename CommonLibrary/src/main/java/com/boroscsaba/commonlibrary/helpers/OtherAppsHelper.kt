package com.boroscsaba.commonlibrary.helpers

import android.content.Context
import android.content.pm.PackageManager
import com.boroscsaba.commonlibrary.OtherApp
import com.boroscsaba.commonlibrary.R
import android.content.Context.MODE_PRIVATE



class OtherAppsHelper(private val context: Context) {

    fun getApps(): List<OtherApp> {
        return apps.filter { a -> a.packageName != context.packageName }
    }

    fun checkApps() {
        val apps = getApps()
        apps.forEach { a ->
            a.installed = isPackageInstalled(a.packageName, context.packageManager)
        }
    }

    fun wasPremium(): Boolean {
        return context.getSharedPreferences("sharedPreferences", MODE_PRIVATE).getBoolean("otherAppsPremium", false)
    }

    fun savePremiumState(isPremium: Boolean) {
        val editor = context.getSharedPreferences("sharedPreferences", MODE_PRIVATE).edit()
        editor.putBoolean("otherAppsPremium", isPremium)
        editor.apply()
    }

    private fun isPackageInstalled(packageName: String, packageManager: PackageManager): Boolean {
        var found = true
        try {
            packageManager.getPackageInfo(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            found = false
        }
        return found
    }

    companion object {
        private val apps = ArrayList<OtherApp>()
        init {
            val bodyMeasurementTracker = OtherApp()
            bodyMeasurementTracker.id = 1
            bodyMeasurementTracker.title = "Body Measurements Tracker"
            bodyMeasurementTracker.icon = R.drawable.body_measurement_tracker
            bodyMeasurementTracker.packageName = "com.boroscsaba.bodymeasurementtracker"
            apps.add(bodyMeasurementTracker)

            val myPiggyBankTracker = OtherApp()
            myPiggyBankTracker.id = 2
            myPiggyBankTracker.title = "My Piggy Bank Tracker"
            myPiggyBankTracker.icon = R.drawable.my_piggy_bank_tracker
            myPiggyBankTracker.packageName = "com.boroscsaba.myapplication"
            apps.add(myPiggyBankTracker)

            val fastingLifestyle = OtherApp()
            fastingLifestyle.id = 3
            fastingLifestyle.title = "Fasting Lifestyle - Timer and Tracker"
            fastingLifestyle.icon = R.drawable.fasting_lifestyle_timer_and_tracker
            fastingLifestyle.packageName = "com.boroscsaba.fastinglifestyletimerandtracker"
            apps.add(fastingLifestyle)

            val englishIrregularVerbsMemorizer = OtherApp()
            englishIrregularVerbsMemorizer.id = 4
            englishIrregularVerbsMemorizer.title = "English Irregular Verbs Memorizer"
            englishIrregularVerbsMemorizer.icon = R.drawable.english_irregular_verbs_memorizer
            englishIrregularVerbsMemorizer.packageName = "com.boroscsaba.englishirregularverbsmemorizer"
            apps.add(englishIrregularVerbsMemorizer)

            val savingsGoalsTracker = OtherApp()
            savingsGoalsTracker.id = 5
            savingsGoalsTracker.title = "Savings Goals Tracker"
            savingsGoalsTracker.icon = R.drawable.savings_goals_tracker
            savingsGoalsTracker.packageName = "com.boroscsaba.savingsgoalstracker"
            apps.add(savingsGoalsTracker)

            val bodyMeasurementsAndWeightLossTracker = OtherApp()
            bodyMeasurementsAndWeightLossTracker.id = 6
            bodyMeasurementsAndWeightLossTracker.title = "Body Measurements and Weight Loss Tracker"
            bodyMeasurementsAndWeightLossTracker.icon = R.drawable.body_measurements_and_weight_loss_tracker
            bodyMeasurementsAndWeightLossTracker.packageName = "com.boroscsaba.bodymeasurementsandweightlosstracker"
            apps.add(bodyMeasurementsAndWeightLossTracker)
        }
    }

}