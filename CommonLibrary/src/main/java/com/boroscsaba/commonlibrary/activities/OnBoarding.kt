package com.boroscsaba.commonlibrary.activities

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.boroscsaba.commonlibrary.ApplicationBase
import com.boroscsaba.commonlibrary.LoggingHelper
import com.boroscsaba.commonlibrary.R
import com.boroscsaba.commonlibrary.activities.helpers.OnBoardingPage
import com.boroscsaba.commonlibrary.tutorial.TutorialHelper
import com.boroscsaba.commonlibrary.tutorial.TutorialsEnum
import kotlinx.android.synthetic.main.activity_onboarding.*

class OnBoarding: ActivityBase(), androidx.viewpager.widget.ViewPager.OnPageChangeListener {

    private val dots = ArrayList<ImageView>()

    init {
        options.layout = R.layout.activity_onboarding
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as ApplicationBase
        viewPager.adapter = OnBoardingPagerAdapter(this, app.onBoardingPages)
        viewPager.addOnPageChangeListener(this)
        addDots(app.onBoardingPages)
    }

    override fun setListeners() {
        val app = application as ApplicationBase
        nextButton.setOnClickListener {
            if (viewPager.currentItem == app.onBoardingPages.size - 1) {
                val intent = Intent(this, app.mainActivity)
                startActivity(intent)
                finish()
                LoggingHelper.logEvent(this, "Tutorial_completed")
                TutorialHelper(app).completeTutorial(TutorialsEnum.ON_BOARDING)
            }
            else {
                viewPager.setCurrentItem(viewPager.currentItem + 1, true)
            }
        }
    }

    private fun addDots(pages: ArrayList<OnBoardingPage>) {
        val dotSize = (7 * Resources.getSystem().displayMetrics.density).toInt()
        val spacingSize = (4 * Resources.getSystem().displayMetrics.density).toInt()
        val linearLayout = findViewById<LinearLayout>(R.id.dots)
        pages.forEach { _ ->
            val dot = ImageView(this)
            dot.setImageResource(R.drawable.circle)
            dot.setColorFilter(Color.parseColor("#cccccc"))
            val dotLayoutParams = LinearLayout.LayoutParams(dotSize, dotSize)
            dotLayoutParams.marginStart = spacingSize
            dotLayoutParams.marginEnd = spacingSize
            dot.layoutParams = dotLayoutParams
            dots.add(dot)
            linearLayout.addView(dot)
        }
        dots[0].setColorFilter(Color.parseColor("#aaaaaa"))
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
        dots.forEach{ d -> d.setColorFilter(Color.parseColor("#bbbbbb")) }
        dots[position].setColorFilter(Color.parseColor("#888888"))
        if (position == dots.size - 1) {
            nextButton.text = resources.getText(com.boroscsaba.commonlibrary.R.string.start).toString().toUpperCase()
        }
        else {
            nextButton.text = resources.getText(com.boroscsaba.commonlibrary.R.string.next).toString().toUpperCase()
        }
    }

    class OnBoardingPagerAdapter(private val context: Context, private val pages: ArrayList<OnBoardingPage>): androidx.viewpager.widget.PagerAdapter() {

        override fun isViewFromObject(view: View, obj: Any): Boolean {
            return view == obj
        }

        override fun getCount(): Int {
            return pages.size
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = layoutInflater.inflate(R.layout.onboarding_page_layout, container, false)

            val backgroundColor = pages[position].backgroundColor
            view.setBackgroundColor(Color.parseColor(backgroundColor))

            val imageView = view.findViewById<ImageView>(R.id.image)
            val titleTextView = view.findViewById<TextView>(R.id.title)
            val descriptionTextView = view.findViewById<TextView>(R.id.description)

            val imageResourceId = pages[position].imageResourceId
            if (imageResourceId != null) {
                imageView.setImageResource(imageResourceId)
            }
            else {
                imageView.visibility = View.GONE
            }

            val titleResourceId = pages[position].titleResourceId
            if (titleResourceId != null) {
                titleTextView.text = context.getText(titleResourceId)
                titleTextView.setTextColor(Color.parseColor(pages[position].textColor))
            }

            val descriptionResourceId = pages[position].descriptionResourceId
            if (descriptionResourceId != null) {
                descriptionTextView.text = context.getText(descriptionResourceId)
                descriptionTextView.setTextColor(Color.parseColor(pages[position].textColor))
            }

            container.addView(view)
            return view
        }

        override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
            container.removeView(obj as View)
        }
    }

}