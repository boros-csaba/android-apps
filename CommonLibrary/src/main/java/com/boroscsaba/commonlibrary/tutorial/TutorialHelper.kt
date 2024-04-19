package com.boroscsaba.commonlibrary.tutorial

import android.app.Application
import com.boroscsaba.commonlibrary.ApplicationBase
import com.boroscsaba.commonlibrary.LoggingHelper
import com.crashlytics.android.Crashlytics

class TutorialHelper(private val application: Application) {

    fun isTutorialCompleted(tutorial: TutorialsEnum): Boolean {
        return getTutorialLog(tutorial) != null
    }

    fun completeTutorial(tutorial: TutorialsEnum) {
        createTutorialLog(tutorial)
    }

    private fun getTutorialLog(tutorial: TutorialsEnum): TutorialLogItem? {
        val tutorialLogRepository = (application as ApplicationBase).getTutorialLogItemRepository()
        return tutorialLogRepository.getObjects("title = ?", arrayOf(tutorial.value), null).firstOrNull()
    }

    private fun createTutorialLog(tutorial: TutorialsEnum) {
        LoggingHelper.logEvent(application, tutorial.value)
        if (getTutorialLog(tutorial) != null) Crashlytics.logException(Exception("Tutorial log already exists: ${tutorial.value}"))
        else {
            val tutorialLog = TutorialLogItem(application)
            tutorialLog.title = tutorial.value
            (application as ApplicationBase).getTutorialLogItemRepository().upsert(tutorialLog, false)
        }
    }
}