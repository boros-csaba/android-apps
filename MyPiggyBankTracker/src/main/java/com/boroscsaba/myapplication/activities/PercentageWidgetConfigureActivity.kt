package com.boroscsaba.myapplication.activities

import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.boroscsaba.myapplication.model.Goal
import com.boroscsaba.commonlibrary.widget.WidgetConfigureActivityBase
import com.boroscsaba.myapplication.R
import com.boroscsaba.myapplication.dataAccess.GoalMapper
import com.boroscsaba.myapplication.logic.GoalLogic
import com.bumptech.glide.Glide


class PercentageWidgetConfigureActivity : WidgetConfigureActivityBase<Goal>() {

    override val widgetClass: Class<*> = PercentageWidget::class.java
    override val rowItemLayoutId: Int = R.layout.percentage_widget_item_layout

    override fun getData() {
        GoalLogic(application).getActiveGoals(values)
    }

    override fun onBindItemViewHolder(item: Goal, holder: RecyclerView.ViewHolder) {
        holder.itemView.findViewById<TextView>(R.id.title).text = item.title
        val iconView = holder.itemView.findViewById(R.id.icon) as ImageView
        if (GoalLogic(application).hasPhotoIcon(item.icon)) {
            Glide.with(this).load(GoalMapper(this).getImageUri(item.id, item.modifiedDate)).into(iconView)
        }
        else {
            Glide.with(this).load(GoalLogic(application).getIconResourceId(item.icon)).into(iconView)
        }
    }
}

