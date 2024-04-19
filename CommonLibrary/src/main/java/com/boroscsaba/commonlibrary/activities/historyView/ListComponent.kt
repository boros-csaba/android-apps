package com.boroscsaba.commonlibrary.activities.historyView

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.boroscsaba.commonlibrary.IDao
import com.boroscsaba.dataaccess.EntityBase

class ListComponent<T:EntityBase>(elementLayoutResourceId: Int, onBindViewHolder: (holder: RecyclerView.ViewHolder, element: T?, classType: Class<T>, dao: IDao<T>, activity: AppCompatActivity, elements: List<T>) -> Unit): Component<T>(elementLayoutResourceId, onBindViewHolder)