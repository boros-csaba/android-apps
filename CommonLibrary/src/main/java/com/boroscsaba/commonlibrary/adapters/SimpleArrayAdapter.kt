package com.boroscsaba.commonlibrary.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter

class SimpleArrayAdapter<T>(private val context: Context, private val layoutResourceId: Int, private val objects: ArrayList<T>, private val onItemBinding: (view: View, item: T) -> Unit, private val getItemId: (T) -> String): BaseAdapter() {

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(layoutResourceId, null)
        onItemBinding.invoke(view, getItem(position))
        return view
    }

    override fun getItem(position: Int): T {
        return objects[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun getPosition(item: T): Int {
        return objects.indexOf(objects.first { i -> getItemId(i) == getItemId(item) })
    }

    override fun getCount(): Int {
        return objects.size
    }
}