package com.boroscsaba.myapplication.technical

import android.annotation.SuppressLint
import androidx.lifecycle.Observer
import android.content.Intent
import androidx.core.app.ActivityOptionsCompat
import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.boroscsaba.myapplication.R
import com.boroscsaba.myapplication.dataAccess.GoalMapper
import com.boroscsaba.myapplication.view.GoalDetailsView
import com.boroscsaba.myapplication.view.GoalsListView
import com.boroscsaba.myapplication.viewmodel.GoalListItemViewModel
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.completed_goal_row_layout.view.*

/**
 * Created by Boros Csaba
 */

class CompletedGoalRecyclerViewAdapter(private val context: AppCompatActivity, private var values: MutableList<GoalListItemViewModel>) : androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return ViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.completed_goal_row_layout
    }

    @SuppressLint("RestrictedApi")
    override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
        val viewModel = values[position]
        val viewHolder = holder as ViewHolder
        viewHolder.id = viewModel.id
        holder.itemView.setOnClickListener { _ ->
            val intent = Intent(context, GoalDetailsView::class.java)
            intent.putExtra("GOAL_ID", viewModel.id)
            intent.putExtra("alreadyCompleted", true)
            val iconTransition = androidx.core.util.Pair(holder.itemView.icon as View, "goalIconTransition")
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(context, iconTransition)
            context.startActivityForResult(intent, GoalsListView.GOAL_REQUEST_CODE, options.toBundle())
        }

        val iconView = holder.itemView.findViewById(R.id.icon) as ImageView
        viewModel.icon.observe(context, Observer { _ -> changeIcon(iconView, viewModel) })
        viewModel.modifiedDate.observe(context, Observer { _ -> changeIcon(iconView, viewModel) })

        viewModel.title.observe(context, Observer{ value ->
            holder.itemView.findViewById<TextView>(R.id.title).text = value
        })
    }

    private fun changeIcon(iconView: ImageView, viewModel: GoalListItemViewModel) {
        if (viewModel.hasPhotoIcon()) {
            val modifiedDate = viewModel.modifiedDate.value
            if (modifiedDate != null) {
                Glide.with(context).load(GoalMapper(context).getImageUri(viewModel.id, modifiedDate)).into(iconView)
            }
        }
        else {
            Glide.with(context).load(viewModel.getIconResourceId()).into(iconView)
        }
    }

    override fun getItemCount(): Int {
        return values.size
    }
}
