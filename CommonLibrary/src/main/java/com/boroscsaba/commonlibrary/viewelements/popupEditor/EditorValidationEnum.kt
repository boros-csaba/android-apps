package com.boroscsaba.commonlibrary.viewelements.popupEditor

import com.boroscsaba.commonlibrary.R

enum class EditorValidationEnum(val validationTextResourceId : Int) {
    MORE_THAN_ZERO(R.string.must_be_larger_than_0),
    CANNOT_BE_ZERO(R.string.cannot_be_zero),
    AT_LEAST_3_CHARACTERS(R.string.too_short)
}