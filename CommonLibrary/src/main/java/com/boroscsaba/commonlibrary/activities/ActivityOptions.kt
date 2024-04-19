package com.boroscsaba.commonlibrary.activities

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.boroscsaba.commonlibrary.IDao
import com.boroscsaba.commonlibrary.R
import com.boroscsaba.commonlibrary.activities.helpers.ViewModel
import com.boroscsaba.commonlibrary.activities.historyView.*
import com.boroscsaba.commonlibrary.viewelements.charts.ChartData
import com.boroscsaba.dataaccess.EntityBase

class ActivityOptions {
    var layout: Int? = null
    var toolbarId: Int = 0
    var withDeleteButton: Boolean = false
    var deleteAlert: Int = 0
    var withSaveButton: Boolean = false
    var showLoadingOnSaveButton: Boolean = false
    var adsOptions: AdsDisplayOptions? = null
    var canShowAdConsentPopup = true







    val layoutConfig = Layout()
    class Layout {
        var baseLayoutId: Int? = null
        var viewModelClass: Class<*>? = null
        var hasViewPager = false
        var hasBottomTabLayout = false
        var viewPagerElements = ArrayList<ViewPagerElement>()
        var contentLayoutId: Int? = null
    }

    fun <T: ViewModel> withViewModel(viewModelClass: Class<T>): ActivityWithViewModel {
        toolbarId = R.id.toolbar
        layoutConfig.viewModelClass = viewModelClass
        return ActivityWithViewModel(layoutConfig)
    }

    class ActivityWithViewModel(private val layoutConfig: Layout) {

        fun withNoNavigationDrawer(): NoNavigationDrawer {
            layoutConfig.baseLayoutId = R.layout.base_layout_no_drawer
            return NoNavigationDrawer(layoutConfig)
        }

        fun withNavigationDrawer(): NavigationDrawer {
            layoutConfig.baseLayoutId = R.layout.base_layout_drawer
            return NavigationDrawer(layoutConfig)
        }
    }

    class NoNavigationDrawer(private val layoutConfig: Layout) {

        fun withViewPager(): ViewPager {
            layoutConfig.hasViewPager = true
            return ViewPager(layoutConfig)
        }

        fun withScrollableContent(layoutId: Int): NoNavigationDrawer {
            layoutConfig.contentLayoutId = layoutId
            return this
        }
    }

    class NavigationDrawer(private val layoutConfig: Layout) {

        fun withViewPager(): ViewPager {
            layoutConfig.hasViewPager = true
            return ViewPager(layoutConfig)
        }

        fun withScrollableContent(layoutId: Int): NavigationDrawer {
            layoutConfig.contentLayoutId = layoutId
            return this
        }
    }

    class ViewPager(private val layoutConfig: Layout) {
        fun withBottomTabsLayout(): ViewPager {
            layoutConfig.hasBottomTabLayout = true
            return this
        }

        fun addElement(fragmentFactory: () -> FragmentBase, iconResourceId: Int): ViewPager {
            layoutConfig.viewPagerElements.add(ViewPagerElement(fragmentFactory, iconResourceId))
            return this
        }

        fun <T: EntityBase> addHistoryView(classType: Class<T>, dao: IDao<T>, liveDataGetter: () -> LiveData<ArrayList<T>>, tabIconResourceId: Int = R.drawable.ic_assignment_black_48dp): HistoryView<T> {
            val viewPagerElement = ViewPagerHistoryElement(classType, dao, liveDataGetter, tabIconResourceId)
            layoutConfig.viewPagerElements.add(viewPagerElement)
            return HistoryView(this, viewPagerElement)
        }
    }

    class HistoryView<T:EntityBase>(private val viewPager: ViewPager, private val viewPagerElement: ViewPagerHistoryElement<*>) {

        fun withListOfElements(elementLayoutResourceId: Int, onBindViewHolder: (holder: RecyclerView.ViewHolder, element: T?, classType: Class<T>, dao: IDao<T>, activity: AppCompatActivity, items: List<T>) -> Unit): HistoryView<T> {
            viewPagerElement.components.add(ListComponent(elementLayoutResourceId, onBindViewHolder))
            return this
        }

        fun withLineChart(chartDataMapper: (T) -> ChartData): HistoryView<T> {
            viewPagerElement.components.add(LineChartComponent(chartDataMapper))
            return this
        }

        fun withAddNewElementButton(iconResourceId: Int, textResourceId: Int): HistoryView<T> {
            viewPagerElement.components.add(AddNewButtonComponent<T>(iconResourceId, textResourceId))
            return this
        }

        fun groupBy(selector: (T: EntityBase) -> String): HistoryView<T> {
            viewPagerElement.groupingSelector = selector
            return this
        }

        fun build(): ViewPager {
            return viewPager
        }

    }

    open class ViewPagerElement(val fragmentFactory: () -> FragmentBase, val iconResourceId: Int)

    class ViewPagerHistoryElement<T: EntityBase>(val classType: Class<T>, val dao: IDao<T>, val liveDataGetter: () -> LiveData<ArrayList<T>>, tabIconResourceId: Int): ViewPagerElement({ HistoryFragment<T>() }, tabIconResourceId) {
        var groupingSelector: ((T) -> String)? = null
        val components = ArrayList<Component<*>>()
    }
}