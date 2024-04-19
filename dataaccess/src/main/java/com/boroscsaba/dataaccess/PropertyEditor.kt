package com.boroscsaba.dataaccess

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData

abstract class PropertyEditor<T>(val propertyName: String, val labelResourceId: Int, val iconResourceId: Int?, val validations: List<Int>, val getter: (EntityBase) -> T, val setter: (EntityBase, T) -> Unit) {
    abstract fun validateInput(container: ViewGroup): Boolean
    abstract fun mapValue(entity: EntityBase, container: ViewGroup)
    abstract fun createView(context: AppCompatActivity, liveData: LiveData<EntityBase>): View
}