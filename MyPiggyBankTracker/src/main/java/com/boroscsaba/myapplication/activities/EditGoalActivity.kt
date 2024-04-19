package com.boroscsaba.myapplication.activities

import android.app.DatePickerDialog
import androidx.lifecycle.Observer
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View

import com.boroscsaba.myapplication.R
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.CustomEvent
import kotlinx.android.synthetic.main.activity_edit_goal.*

import java.text.DecimalFormat
import android.content.Intent
import android.graphics.Bitmap
import android.widget.ImageView
import android.widget.TextView
import com.boroscsaba.myapplication.BuildConfig
import com.boroscsaba.commonlibrary.*
import com.boroscsaba.commonlibrary.activities.ActivityBase
import com.boroscsaba.commonlibrary.adapters.SimpleArrayAdapter
import com.boroscsaba.commonlibrary.helpers.CameraImageHelper
import com.boroscsaba.commonlibrary.helpers.KeyboardHelper
import com.boroscsaba.commonlibrary.settings.SettingsHelper
import com.boroscsaba.commonlibrary.viewelements.currency.CurrencyManager
import com.boroscsaba.commonlibrary.viewelements.currency.Currency
import com.boroscsaba.myapplication.dataAccess.GoalMapper
import com.boroscsaba.myapplication.viewmodel.GoalEditViewModel
import com.boroscsaba.myapplication.model.Goal
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.signature.ObjectKey
import java.util.*


class EditGoalActivity : ActivityBase() {

    init {
        options.withViewModel(GoalEditViewModel::class.java)
                    .withNoNavigationDrawer()
                        .withScrollableContent(R.layout.activity_edit_goal)
    }

    private var goalModified = false
    private val cameraHelper : CameraImageHelper = CameraImageHelper(this)
    private val galleryHelper : GalleryImageHelper = GalleryImageHelper(this)

    init {
        options.toolbarId = R.id.toolbar
        options.withSaveButton = true
        options.showLoadingOnSaveButton = true
        options.withDeleteButton = true
        options.deleteAlert = R.string.activity_edit_goal_delete_alert
    }

    @SuppressWarnings("unchecked")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val adapter = fillCurrencies()

        val viewModel = viewModel as GoalEditViewModel
        viewModel.title.observe(this, Observer { value ->
            val goalEditText = goalTitleInput!!.editText
            goalEditText?.setText(value)
        })
        val format = DecimalFormat("#.##")
        viewModel.targetAmount.observe(this, Observer { value ->
            val targetAmountEditText = targetAmountInput!!.editText
            if (targetAmountEditText != null && value != 0.0) {
                val targetAmountText = format.format(value)
                targetAmountEditText.setText(targetAmountText)
            }
        })
        viewModel.initialAmount.observe(this, Observer { value ->
            val startingAmountEditText = startingAmountInput!!.editText
            if (startingAmountEditText != null && (value != 0.0 || intent.getIntExtra("GOAL_ID", 0) > 0)) {
                val startingAmountText = format.format(value)
                startingAmountEditText.setText(startingAmountText)
            }
        })
        viewModel.currencyCode.observe(this, Observer { value ->
            val currency = CurrencyManager.getCurrency(value)
            currencySpinner.setSelection(adapter.getPosition(currency))
        })
        viewModel.iconId.observe(this, Observer { changeIcon() })
        viewModel.iconUri.observe(this, Observer { changeIcon() })
        viewModel.dueDate.observe(this, Observer { value ->
            val text = if (value ?:0 > 0) SettingsHelper(application).getDateFormat().format(value) else null
            dueDateInput.editText?.setText(text)
        })
        viewModel.notificationEnabled.observe(this, Observer { enabled ->
            if (enabled == false) notificationButton.setImageResource(R.drawable.ic_notifications_off_black_48dp)
            else notificationButton.setImageResource(R.drawable.ic_notifications_active_black_48dp)
        })
    }

    override fun setListeners() {
        val viewModel = viewModel as GoalEditViewModel
        homeIcon.setOnClickListener { view -> onClick(view) }
        carIcon.setOnClickListener { view -> onClick(view) }
        universityIcon.setOnClickListener { view -> onClick(view) }
        travelIcon.setOnClickListener { view -> onClick(view) }
        devicesIcon.setOnClickListener { view -> onClick(view) }
        giftIcon.setOnClickListener { view -> onClick(view) }

        selectImageButton.setOnClickListener {
            galleryHelper.startGetImageFromGallery()
        }
        takePhotoButton.setOnClickListener {
            cameraHelper.startGetImageFromCamera(BuildConfig.FILES_AUTHORITY)
        }

        dueDateInput.editText?.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                val newDate = Calendar.getInstance()
                newDate.set(year, monthOfYear, dayOfMonth)
                if (viewModel.dueDate.value != newDate.timeInMillis) goalModified = true
                viewModel.dueDate.value = newDate.timeInMillis
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
            datePickerDialog.setOnCancelListener {
                if (viewModel.dueDate.value ?: 0 > 0) goalModified = true
                viewModel.dueDate.value = 0
            }
            datePickerDialog.show()
        }

        notificationButton.setOnClickListener {
            if (notificationButton.tag as String == "enabled") {
                notificationButton.setImageResource(R.drawable.ic_notifications_off_black_48dp)
                notificationButton.tag = "disabled"
            }
            else {
                notificationButton.setImageResource(R.drawable.ic_notifications_active_black_48dp)
                notificationButton.tag = "enabled"
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.edit_goal_menu, menu)
        if (intent.getIntExtra("GOAL_ID", 0) == 0) {
            (0 until menu.size())
                    .map { menu.getItem(it) }
                    .filter { it.title === getString(R.string.delete) }
                    .forEach { it.isVisible = false }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun showDiscardChangesWarning(): Boolean {
        return mapChangesToGoal()
    }

    override fun save(): Boolean {
        val viewModel = viewModel as GoalEditViewModel
        var hasErrors = false
        val goalEditText = goalTitleInput!!.editText
        if (goalEditText != null) {
            val goalTitle = goalEditText.text.toString()
            if (goalTitle.length < 3) {
                goalTitleInput!!.error = getString(R.string.too_short)
                hasErrors = true
            } else {
                goalTitleInput!!.error = null
            }
        }

        var targetAmount = 0.0
        val targetAmountEditText = targetAmountInput!!.editText
        if (targetAmountEditText != null) {
            val targetAmountText = targetAmountEditText.text.toString()
            targetAmount = try {
                    java.lang.Double.parseDouble(targetAmountText)
                } catch (ex: Exception) {
                    0.0
                }

            if (targetAmount == 0.0) {
                targetAmountInput!!.error = getString(R.string.cannot_be_zero)
                hasErrors = true
            } else {
                targetAmountInput!!.error = null
            }
        }

        val startingAmountEditText = startingAmountInput!!.editText
        if (startingAmountEditText != null) {
            val startingAmountText = startingAmountEditText.text.toString()
            val startingAmount: Double
            startingAmount = try {
                    java.lang.Double.parseDouble(startingAmountText)
                } catch (ex: Exception) {
                    0.0
                }

            if (startingAmount >= targetAmount && targetAmount != 0.0) {
                startingAmountInput!!.error = getString(R.string.activity_edit_goal_target_smaller_than_starting_error)
                hasErrors = true
            } else {
                startingAmountInput!!.error = null
            }
        }

        if (!hasErrors) {
            if (mapChangesToGoal()) {
                viewModel.save()
            }
            finish()
            return true
        }
        else {
            val keyboardHelper = KeyboardHelper(this)
            keyboardHelper.hideKeyboard()
        }
        return false
    }

    private fun mapChangesToGoal() : Boolean {
        val viewModel = viewModel as GoalEditViewModel
        var changed = false

        val goalEditText = goalTitleInput!!.editText
        if (goalEditText != null) {
            val goalText = goalEditText.text.toString()
            if (viewModel.title.value != goalText) {
                viewModel.title.value = goalText
                changed = true
            }
        }

        val targetAmountEditText = targetAmountInput!!.editText
        if (targetAmountEditText != null) {
            val targetAmountText = targetAmountEditText.text.toString()
            val targetAmount: Double?
            targetAmount = try {
                java.lang.Double.parseDouble(targetAmountText)
            } catch (ex: Exception) {
                0.0
            }
            if (viewModel.targetAmount.value != targetAmount) {
                viewModel.targetAmount.value = targetAmount
                changed = true
            }
        }

        val startingAmountEditText = startingAmountInput!!.editText
        if (startingAmountEditText != null) {
            val startingAmountText = startingAmountEditText.text.toString()
            val startingAmount: Double?
            startingAmount = try {
                java.lang.Double.parseDouble(startingAmountText)
            }
            catch (ex: Exception) {
                0.0
            }
            if (viewModel.initialAmount.value != startingAmount) {
                viewModel.initialAmount.value = startingAmount
                changed = true
            }
        }

        if (currencySpinner!!.selectedItem != null) {
            val currency = currencySpinner!!.selectedItem as Currency
            if (viewModel.currencyCode.value != currency.currencyCode) {
                viewModel.currencyCode.value = currency.currencyCode
                changed = true
                Answers.getInstance().logCustom(CustomEvent("Currency changed").putCustomAttribute("To", currency.currencyCode))
            }
        }

        if (notificationButton.tag as String == "enabled") {
            if (viewModel.notificationEnabled.value == false) {
                viewModel.notificationEnabled.value = true
                changed = true
            }
        }
        else if (viewModel.notificationEnabled.value == true) {
            viewModel.notificationEnabled.value = false
            changed = true
        }

        if (changed) {
            goalModified = changed
        }
        return changed || goalModified
    }

    override fun delete(): Boolean {
        val viewModel = viewModel as GoalEditViewModel
        viewModel.delete()
        finish()
        return true
    }

    private fun changeIcon() {
        val viewModel = viewModel as GoalEditViewModel
        if (viewModel.hasPhotoIcon()) {
            if (viewModel.iconUri.value != null) {
                val options = RequestOptions.bitmapTransform(CircleBitmapTransformation()).signature(ObjectKey(System.currentTimeMillis()))
                Glide.with(this).asBitmap().load(viewModel.iconUri.value).apply(options).listener(object: RequestListener<Bitmap> {
                    override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        if (resource != null) {
                            viewModel.photoIcon.value = CircleBitmapTransformation().transform(resource, Goal.MAX_IMAGE_SIZE)
                        }
                        return false
                    }
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
                        return false
                    }
                }).into(bigIcon)
            }
            else {
                Glide.with(this).load(GoalMapper(this).getImageUri(viewModel.goalId, viewModel.modifiedDate)).into(bigIcon)
            }
        }
        else {
            Glide.with(this).load(viewModel.getIconResourceId()).into(bigIcon)
        }
    }

    private fun fillCurrencies(): SimpleArrayAdapter<Currency> {
        val dataAdapter = SimpleArrayAdapter(this, R.layout.currency_spinner_dropdown_item, CurrencyManager.getCurrencyList(), { view, item ->
            val titleTextView = view.findViewById<TextView>(R.id.currencyName)
            val iconImage = view.findViewById<ImageView>(R.id.currencyImage)
            if (item.iconResourceId == null) {
                titleTextView.visibility = View.VISIBLE
                titleTextView.text = item.currencyCode
                iconImage.visibility = View.GONE
            }
            else {
                titleTextView.visibility = View.GONE
                iconImage.setImageResource(item.iconResourceId!!)
                iconImage.visibility = View.VISIBLE
            }
        }, { item -> item.currencyCode })
        currencySpinner.adapter = dataAdapter
        return dataAdapter
    }

    private fun onClick(view: View) {
        val viewModel = viewModel as GoalEditViewModel
        var iconId = 0
        when (view.id) {
            R.id.homeIcon -> iconId = 1
            R.id.carIcon -> iconId = 2
            R.id.universityIcon -> iconId = 3
            R.id.travelIcon -> iconId = 4
            R.id.devicesIcon -> iconId = 5
            R.id.giftIcon -> iconId = 6
        }
        if (viewModel.iconId.value != iconId) {
            viewModel.iconId.value = iconId
            goalModified = true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val viewModel = viewModel as GoalEditViewModel
        val imageUri = cameraHelper.getImageFromCameraCallback(requestCode, resultCode) ?:
                       galleryHelper.getImageFromGalleryCallback(requestCode, resultCode, data)
        if (imageUri != null) {
            viewModel.iconUri.value = imageUri
            viewModel.iconId.value = 1000
            goalModified = true
        }
    }
}
