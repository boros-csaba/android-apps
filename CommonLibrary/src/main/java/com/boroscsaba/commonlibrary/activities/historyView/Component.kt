package com.boroscsaba.commonlibrary.activities.historyView

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.boroscsaba.commonlibrary.IDao
import com.boroscsaba.dataaccess.EntityBase

abstract class Component<T:EntityBase>(val elementLayoutResourceId: Int, val onBindViewHolder: (holder: RecyclerView.ViewHolder, element: T?, classType: Class<T>, dao: IDao<T>, activity: AppCompatActivity, items: List<T>) -> Unit)