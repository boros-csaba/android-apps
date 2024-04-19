package com.boroscsaba.commonlibrary.activities.historyView

import com.boroscsaba.commonlibrary.R
import com.boroscsaba.commonlibrary.Utils
import com.boroscsaba.commonlibrary.viewelements.popupEditor.PopupEditor
import com.boroscsaba.dataaccess.EntityBase
import kotlinx.android.synthetic.main.base_layout_history_fragment_list_footer.view.*

class AddNewButtonComponent<T: EntityBase>(buttonImageResourceId: Int?, buttonTextResourceId: Int?): Component<T>(R.layout.base_layout_history_fragment_list_footer, { holder, _, classType, dao, activity, _ ->
    if (buttonImageResourceId != null) {
        Utils.setImageViewSource(buttonImageResourceId, holder.itemView.addNewButtonImage, activity)
    }
    if (buttonTextResourceId != null) {
        holder.itemView.addNewButtonText.setText(buttonTextResourceId)
    }
    holder.itemView.setOnClickListener {
        PopupEditor(classType, dao, activity)
                .withNewInstance()
                .show()
    }
})