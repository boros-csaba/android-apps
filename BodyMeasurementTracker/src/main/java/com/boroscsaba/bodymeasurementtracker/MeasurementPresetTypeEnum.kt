package com.boroscsaba.bodymeasurementtracker

import java.util.*

/**
* Created by boros on 2/23/2018.
*/
enum class MeasurementPresetTypeEnum(val value : Int) {
    CUSTOM(0),
    HEIGHT(1),
    WEIGHT(2),
    WAIST(3),
    HIPS(4),
    BMI(101);

    companion object {

        val colors = arrayOf("#F44336", "#2196F3", "#009688", "#FFEB3B", "#795548", "#4CAF50", "#9C27B0", "#9E9E9E", "#E91E63", "#FF9800")

        fun fromInt(type: Int): MeasurementPresetTypeEnum {
            return when(type) {
                1 -> HEIGHT
                2 -> WEIGHT
                3 -> WAIST
                4 -> HIPS
                101 -> BMI
                else -> CUSTOM
            }
        }

        fun getIcon(type: MeasurementPresetTypeEnum): Int {
            return when(type) {
                HEIGHT -> R.drawable.ic_ruler
                WEIGHT -> R.drawable.ic_weight
                WAIST -> R.drawable.ic_waist
                HIPS -> R.drawable.ic_hips
                else -> 0
            }
        }

        fun getIconPadding(type: MeasurementPresetTypeEnum): Int {
            return when(type) {
                HEIGHT -> 5
                WEIGHT -> 9
                WAIST -> 3
                HIPS -> 3
                else -> 0
            }
        }

        fun getDefaultTitleResource(type: MeasurementPresetTypeEnum): Int {
            return when(type) {
                HEIGHT -> R.string.height
                WEIGHT -> R.string.weight
                WAIST -> R.string.waist
                HIPS -> R.string.hips
                else -> 0
            }
        }

        fun getDefaultIconColor(type: MeasurementPresetTypeEnum): String {
            return when(type) {
                HEIGHT -> "#FF9800"
                WEIGHT -> "#2196F3"
                WAIST -> "#795548"
                HIPS -> "#009688"
                else -> ""
            }
        }

        fun getRandomColor(): String {
            return colors[Random().nextInt(colors.size)]
        }
    }
}