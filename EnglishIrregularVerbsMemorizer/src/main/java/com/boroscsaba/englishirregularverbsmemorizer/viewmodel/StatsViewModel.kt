package com.boroscsaba.englishirregularverbsmemorizer.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.boroscsaba.englishirregularverbsmemorizer.logic.VerbLogic
import com.boroscsaba.englishirregularverbsmemorizer.model.Verb

class StatsViewModel (application: Application) : AndroidViewModel(application) {

    val verbs = MutableLiveData<ArrayList<Verb>>()

    fun initialize() {
        VerbLogic(getApplication()).getVerbs(verbs)
    }

    fun search(text: String) {
        VerbLogic(getApplication()).search(text, verbs)
    }
}