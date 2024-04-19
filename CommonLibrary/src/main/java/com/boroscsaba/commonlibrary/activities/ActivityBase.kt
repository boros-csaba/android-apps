package com.boroscsaba.commonlibrary.activities

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.util.TypedValue
import android.view.*
import android.widget.ProgressBar
import com.boroscsaba.commonlibrary.*
import com.boroscsaba.commonlibrary.activities.helpers.ToolbarButton
import com.boroscsaba.commonlibrary.activities.helpers.AdHelper
import com.boroscsaba.commonlibrary.activities.helpers.LayoutBuilder
import com.boroscsaba.commonlibrary.activities.helpers.ViewModel
import com.boroscsaba.commonlibrary.splashscreen.SplashScreenActivity
import com.boroscsaba.commonlibrary.viewelements.InformationPopup


/**
 * Created by Boros Csaba
 */

abstract class ActivityBase : AppCompatActivity() {

    val options = ActivityOptions()
    val buttons = ArrayList<ToolbarButton>()
    var viewModel: ViewModel? = null
    private val menuItems = ArrayList<MenuItem>()
    private var savingProgressBar: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as ApplicationBase
        app.appStartLog += "${this.javaClass.simpleName}->ActivityBase.onCreate(${savedInstanceState == null}) "

        if (app.initialized <= 0) {
            app.appStartLog = " AppNotInitialized "
            val intent = Intent(this, SplashScreenActivity::class.java)
            intent.putExtra("CLOSE_AFTER_INIT", true)
            app.appStartLog += "${this.javaClass.simpleName} closeAfterInit "
            startActivity(intent)
        }
        setupLayout()

        setupActionBar()

        setListeners()
        createToolbarButtons()

        val adSettings = options.adsOptions
        if (adSettings != null) {
            AdHelper(this).addAdView(adSettings, options.canShowAdConsentPopup)
        }

        if (app.showOtherAppsPremiumWonPopup) {
            LoggingHelper.logEvent(this, "PremiumWonPopup")
            InformationPopup(this).show("Congratulations!", "Thank you for downloading and using all of our apps! Now you are a premium user!")
            app.showOtherAppsPremiumWonPopup = false
        }
        if (app.showOtherAppsPremiumLostPopup) {
            LoggingHelper.logEvent(this, "PremiumLostPopup")
            InformationPopup(this).show("New app!", "We have released some new apps! To continue being a premium user for free please download the new apps!")
            app.showOtherAppsPremiumLostPopup = false
        }
    }

    private fun setupLayout() {
        if (options.layout != null) {
            setContentView(options.layout!!)
        }
        LayoutBuilder(this, intent).build()
    }

    open fun setListeners() {}

    private fun setupActionBar() {
        setSupportActionBar(findViewById(options.toolbarId))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setHomeButtonEnabled(true)
    }

    //region Toolbar buttons
    private fun createToolbarButtons() {
        if (options.withSaveButton) {
            val saveButton = ToolbarButton(R.string.save, R.drawable.ic_check_white_48dp)
            saveButton.action = { save() }
            buttons.add(saveButton)
            if (options.showLoadingOnSaveButton) {
                savingProgressBar = ProgressBar(this)
                val size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 38f, resources.displayMetrics).toInt()
                val params = Toolbar.LayoutParams(size, size)
                params.marginEnd = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, resources.displayMetrics).toInt()
                params.gravity = Gravity.END
                savingProgressBar?.layoutParams = params
                savingProgressBar?.isIndeterminate = true
                val toolbar = findViewById<Toolbar>(options.toolbarId)
                toolbar.addView(savingProgressBar, toolbar.childCount)
                savingProgressBar?.indeterminateDrawable?.mutate()?.setColorFilter(ContextCompat.getColor(this, android.R.color.white), android.graphics.PorterDuff.Mode.SRC_IN)
                savingProgressBar?.visibility = View.GONE
            }
        }
        if (options.withDeleteButton) {
            val deleteButton = ToolbarButton(R.string.delete, R.drawable.ic_delete_forever_white_48dp)
            deleteButton.action = { showAskToDeletePopup() }
            buttons.add(deleteButton)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        val app = application as ApplicationBase
        app.appStartLog += "${this.javaClass.simpleName}->ActivityBase.onCreateOptionsMenu "

        if (options.withDeleteButton && intent.getIntExtra("Id", 0) == 0) {
            val deleteButton = buttons.first{ b -> b.titleResource == R.string.delete }
            deleteButton.isActive = false
        }

        for (button in buttons) {
            if (button.isActive) {
                val menuItem = menu.add(button.titleResource)
                if (menuItems.any { m -> m.title == getString(button.titleResource) }) {
                    LoggingHelper.logException(Exception("Menu already exists" + Thread.currentThread().stackTrace), this)
                }
                else {
                    menuItems.add(menuItem)
                    menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
                    if (button.iconResource != null) {
                        menuItem.setIcon(button.iconResource)
                    }
                }
            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        val button = buttons.firstOrNull{ b -> getString(b.titleResource) == item.title } ?: return super.onOptionsItemSelected(item)
        if (!button.executed)
        {
            button.executed = button.action?.invoke() ?: false
        }
        return true
    }

    private fun showAskToDeletePopup(): Boolean {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(getString(options.deleteAlert))
        builder.setCancelable(true)
        builder.setPositiveButton(getString(R.string.delete)) { dialog, _ ->
            dialog.cancel()
            delete()
            finish()
        }
        builder.setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.cancel() }
        val alert = builder.create()
        alert.show()
        return false
    }

    open fun delete(): Boolean { return false }

    open fun save(): Boolean { return false }

    fun showLoadingOnSaveButton() {
        val menuItem = menuItems.firstOrNull { i -> i.title == getString(R.string.save) }
        if (savingProgressBar == null || menuItem == null) return
        savingProgressBar?.visibility = View.VISIBLE
        menuItem.isVisible = false
    }

    fun hideLoadingOnSaveButton() {
        savingProgressBar?.visibility = View.GONE
        menuItems.firstOrNull { i -> i.title == getString(R.string.save) }?.isVisible = true
    }
    //endregion

    open fun showDiscardChangesWarning(): Boolean { return false }

    override fun onBackPressed() {
        if (!showDiscardChangesWarning()) return super.onBackPressed()
        val builder = AlertDialog.Builder(this)
        builder.setMessage(getString(R.string.discard_changed))
        builder.setCancelable(true)
        builder.setPositiveButton(getString(R.string.discard)) { _, _ -> super.onBackPressed() }
        builder.setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.cancel() }
        val alert = builder.create()
        alert.show()
    }

    override fun onStop() {
        super.onStop()
        val app = application as ApplicationBase
        app.appStartLog += "${this.javaClass.simpleName}->ActivityBase.onStop "
    }

    override fun onActivityReenter(resultCode: Int, data: Intent?) {
        super.onActivityReenter(resultCode, data)
        val app = application as ApplicationBase
        app.appStartLog += "${this.javaClass.simpleName}->ActivityBase.onActivityReenter "
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val app = application as ApplicationBase
        app.appStartLog += "${this.javaClass.simpleName}->ActivityBase.onAttachedToWindow "
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        val app = application as ApplicationBase
        app.appStartLog += "${this.javaClass.simpleName}->ActivityBase.onCreate2(${savedInstanceState == null}) "
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        val app = application as ApplicationBase
        app.appStartLog += "${this.javaClass.simpleName}->ActivityBase.onDetachFromView "
    }

    override fun onRestart() {
        super.onRestart()
        val app = application as ApplicationBase
        app.appStartLog += "${this.javaClass.simpleName}->ActivityBase.onRestart "
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        val app = application as ApplicationBase
        app.appStartLog += "${this.javaClass.simpleName}->ActivityBase.onRestoreInstanceState "
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onRestoreInstanceState(savedInstanceState, persistentState)
        val app = application as ApplicationBase
        app.appStartLog += "${this.javaClass.simpleName}->ActivityBase.onRestoreInstanceState2 "
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
        val app = application as ApplicationBase
        app.appStartLog += "${this.javaClass.simpleName}->ActivityBase.onSaveInstanceState "
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        val app = application as ApplicationBase
        app.appStartLog += "${this.javaClass.simpleName}->ActivityBase.onTrimMemory($level) "
    }

    override fun onWindowAttributesChanged(params: WindowManager.LayoutParams?) {
        super.onWindowAttributesChanged(params)
        val app = application as ApplicationBase
        app.appStartLog += "${this.javaClass.simpleName}->ActivityBase.onWindowAttributesChanged "
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val app = application as ApplicationBase
        app.appStartLog += "${this.javaClass.simpleName}->ActivityBase.onActivityResult "
    }

    override fun onPause() {
        super.onPause()
        val app = application as ApplicationBase
        app.appStartLog += "${this.javaClass.simpleName}->ActivityBase.onPause "
    }

    override fun onResume() {
        super.onResume()
        val app = application as ApplicationBase
        app.appStartLog += "${this.javaClass.simpleName}->ActivityBase.onResume "
    }
}