package com.boroscsaba.commonlibrary.drawer

import android.app.Activity

class DrawerButton(val titleResourceId: Int, var iconResourceId: Int, val group: Int, val order: Int, val action: ((activity: Activity) -> Unit))