package com.boroscsaba.englishirregularverbsmemorizer.logic

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import com.boroscsaba.commonlibrary.AsyncTask
import com.boroscsaba.commonlibrary.Utils
import com.boroscsaba.dataaccess.DataObserverFactory
import com.boroscsaba.englishirregularverbsmemorizer.dataAccess.AnswerMapper
import com.boroscsaba.englishirregularverbsmemorizer.dataAccess.AnswerRepository
import com.boroscsaba.englishirregularverbsmemorizer.dataAccess.DailyResultMapper
import com.boroscsaba.englishirregularverbsmemorizer.dataAccess.DailyResultRepository
import com.boroscsaba.englishirregularverbsmemorizer.model.Answer
import com.boroscsaba.englishirregularverbsmemorizer.model.DailyResult
import com.boroscsaba.englishirregularverbsmemorizer.model.Verb
import java.util.*
import kotlin.math.max

class VerbLogic(private val application: Application) {

    fun getNumberOfReps(): Int {
        //todo from settings
        return 10
    }

    fun getTodayProgress(liveData: MutableLiveData<Int>) {
        DataObserverFactory<Int>(application).observe(DailyResultMapper(application).getUri(), liveData) { getTodayProgress() }
        AsyncTask().execute({
            val result = getTodayProgress()
            Handler(Looper.getMainLooper()).post{ liveData.value = result }
        })
    }

    private fun getTodayProgress(): Int {
        val dailyResult = DailyResultRepository(application).getObjects("day > ?", arrayOf((System.currentTimeMillis() - 24 * 60 * 60 * 1000).toString()), null)
                .firstOrNull { r -> Utils.areOnSameDay(System.currentTimeMillis(), r.day) }
        return dailyResult?.correctGuesses ?: 0
    }

    fun getDayStreak(dayStreakLiveData: MutableLiveData<Int>, bestDayStreakLiveData: MutableLiveData<Int>) {
        DataObserverFactory<Int>(application).observe(DailyResultMapper(application).getUri(), dayStreakLiveData) { getDayStreak() }
        DataObserverFactory<Int>(application).observe(DailyResultMapper(application).getUri(), bestDayStreakLiveData) { getBestDayStreak() }
        AsyncTask().execute({
            val dayStreak = getDayStreak()
            val bestDayStreak = getBestDayStreak()
            Handler(Looper.getMainLooper()).post{
                dayStreakLiveData.value = dayStreak
                bestDayStreakLiveData.value = bestDayStreak
            }
        })
    }

    private fun getDayStreak(): Int {
        val dailyResults = DailyResultRepository(application).getObjects(null, null, "day desc")
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.add(Calendar.HOUR_OF_DAY, -24)
        var streakCount = 0
        var found = false
        dailyResults.forEach { dailyResult ->
            if (!found) {
                if (!Utils.areOnSameDay(System.currentTimeMillis(), dailyResult.day)) {
                    if (Utils.areOnSameDay(calendar.timeInMillis, dailyResult.day)) {
                        streakCount++
                        calendar.add(Calendar.HOUR_OF_DAY, -24)
                    }
                    else {
                        found = true
                    }
                }
            }
        }
        return streakCount + 1
    }
    private fun getBestDayStreak(): Int {
        val dailyResults = DailyResultRepository(application).getObjects(null, null, "day desc")
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        var streakCount = 0
        var bestStreak = 0
        dailyResults.forEach { dailyResult ->
            if (Utils.areOnSameDay(calendar.timeInMillis, dailyResult.day)) {
                streakCount++
                calendar.add(Calendar.HOUR_OF_DAY, -24)
            }
            else {
                bestStreak = streakCount
                streakCount = 0
            }
        }
        return max(bestStreak, streakCount)
    }


    fun getVerbs(liveData: MutableLiveData<ArrayList<Verb>>) {
        AsyncTask().execute({
            val result = getVerbs()
            Handler(Looper.getMainLooper()).post{ liveData.value = result }
        })
    }

    private fun getVerbs(): ArrayList<Verb> {
        VerbLogic.verbs.forEach { v -> v.isFilteredOut = false }
        return VerbLogic.verbs
    }

    fun getNextVerb(liveData: MutableLiveData<Verb?>, isFirstStart: Boolean) {
        AsyncTask().execute({
            val result = getNextVerb(isFirstStart)
            Handler(Looper.getMainLooper()).post{ liveData.value = result }
        })
    }

    private fun getNextVerb(isFirstStart: Boolean): Verb? {
        val completedVerbs = AnswerRepository(application).getObjects("correct_answers - wrong_answers >= ?", arrayOf(getNumberOfReps().toString()), null)
        val verbs = if (isFirstStart) {
            VerbLogic.verbs.filter { v -> v.infinitive == "begin" }
        }
        else {
            VerbLogic.verbs.filter { v -> completedVerbs.none { vv -> vv.verbId == v.id } }
        }
        if (verbs.isEmpty()) return null
        val randomIndex = Math.floor(Math.random() * verbs.size).toInt()
        return verbs[randomIndex]
    }

    fun saveAnswerAndGetNextVerb(id: Int, isCorrect: Boolean, liveData: MutableLiveData<Verb?>, goalMetLiveData: MutableLiveData<Boolean>) {
        AsyncTask().execute({
            saveAnswer(id, isCorrect, goalMetLiveData)
            val result = getNextVerb(false)
            Handler(Looper.getMainLooper()).post{ liveData.value = result }
        })
    }

    private fun saveAnswer(id: Int, isCorrect: Boolean, goalMetLiveData: MutableLiveData<Boolean>) {
        var answer = AnswerRepository(application).getObjects("verb_id = ?", arrayOf(id.toString()), null).firstOrNull()
        if (answer == null) {
            answer = Answer(application)
            answer.verbId = id
        }
        if (isCorrect) {
            answer.correctAnswers++
        }
        else {
            answer.wrongAnswers++
        }
        AnswerRepository(application).upsert(answer, false)

        var dailyResult = DailyResultRepository(application).getObjects("day > ?", arrayOf((System.currentTimeMillis() - 24 * 60 * 60 * 1000).toString()), null)
                .firstOrNull { r -> Utils.areOnSameDay(System.currentTimeMillis(), r.day) }
        if (dailyResult == null) {
            dailyResult = DailyResult(application)
            dailyResult.day = System.currentTimeMillis()
        }
        if (isCorrect) {
            dailyResult.correctGuesses++
        }
        else {
            dailyResult.missedGuesses++
        }
        if (!dailyResult.goalMet) {
            if (getTodayProgress() >= getNumberOfReps()) {
                dailyResult.goalMet = true
                goalMetLiveData.postValue(true)
            }
        }
        DailyResultRepository(application).upsert(dailyResult, false)
    }

    fun isSimplePastCorrect(simplePast: String, verb: Verb?): Boolean {
        if (verb == null) return false
        return isMatching(verb.simplePast, simplePast)
    }

    fun isPastParticipleCorrect(pastParticiple: String, verb: Verb?): Boolean {
        if (verb == null) return false
        return isMatching(verb.pastParticiple, pastParticiple)
    }

    private fun isMatching(verb: String, answer: String): Boolean {
        val parts = verb.split("/")
        val part1 = parts[0].trim()
        val part2 = if (parts.size > 1) { parts[1].trim() } else { null }
        return answer == part1 || (part2 != null && answer == part2)
    }

    fun getProgressText(liveData: MutableLiveData<String>) {
        DataObserverFactory<String>(application).observe(AnswerMapper(application).getUri(), liveData) { getProgressText() }
        AsyncTask().execute({
            val result = getProgressText()
            Handler(Looper.getMainLooper()).post{ liveData.value = result }
        })
    }

    private fun getProgressText(): String {
        val total = getNumberOfReps() * VerbLogic.verbs.size
        val allAnswers = AnswerRepository(application).getObjects(null, null, null)
        val done = allAnswers.sumBy { v -> v.correctAnswers - v.wrongAnswers }
        return "%.3f".format(100f * done / total) + "%"
    }

    fun search(text: String, liveData: MutableLiveData<ArrayList<Verb>>) {
        liveData.value?.forEach { verb ->
            verb.isFilteredOut = !verb.infinitive.contains(text) && !verb.simplePast.contains(text) && !verb.pastParticiple.contains(text)
        }
        liveData.postValue(liveData.value)
    }

    companion object {
        val verbs = arrayListOf(
                Verb(1, "arise", "arose", "arisen"),
                Verb(2, "awake", "awoke", "awoken"),
                Verb(3, "be", "was/were", "been"),
                Verb(4, "bear", "bore", "born/borne"),
                Verb(5, "beat", "beat", "beaten/beat"),
                Verb(6, "become", "became", "become"),
                Verb(7, "begin", "began", "begun"),
                Verb(8, "bend", "bent", "bent"),
                Verb(9, "bet", "bet/betted", "bet/betted"),
                Verb(10, "bid", "bid/bade", "bid/bidden"),
                Verb(11, "bind", "bound", "bound"),
                Verb(12, "bite", "bit", "bitten"),
                Verb(13, "bleed", "bled", "bled"),
                Verb(14, "blow", "blew", "blown"),
                Verb(15, "break", "broke", "broken"),
                Verb(16, "breed", "bred", "bred"),
                Verb(17, "bring", "brought", "brought"),
                Verb(18, "build", "built", "built"),
                Verb(20, "burst", "burst", "burst"),
                Verb(21, "buy", "bought", "bought"),
                Verb(22, "cast", "cast", "cast"),
                Verb(23, "catch", "caught", "caught"),
                Verb(24, "choose", "chose", "chosen"),
                Verb(25, "cling", "clung", "clung"),
                Verb(26, "come", "came", "come"),
                Verb(27, "cost", "cost", "cost"),
                Verb(28, "creep", "crept", "crept"),
                Verb(29, "cut", "cut", "cut"),
                Verb(30, "deal", "dealt", "dealt"),
                Verb(31, "dig", "dug", "dug"),
                Verb(32, "do", "did", "done"),
                Verb(33, "draw", "drew", "drawn"),
                Verb(35, "drink", "drank", "drunk"),
                Verb(36, "drive", "drove", "driven"),
                Verb(37, "dwell", "dwelt/dwelled", "dwelt/dwelled"),
                Verb(38, "eat", "ate", "eaten"),
                Verb(39, "fall", "fell", "fallen"),
                Verb(40, "feed", "fed", "fed"),
                Verb(41, "feel", "felt", "felt"),
                Verb(42, "fight", "fought", "fought"),
                Verb(43, "find", "found", "found"),
                Verb(44, "fit", "fitted/fit", "fitted/fit"),
                Verb(45, "flee", "fled", "fled"),
                Verb(46, "fling", "flung", "flung"),
                Verb(47, "fly", "flew", "flown"),
                Verb(48, "forbid", "forbade", "forbidden"),
                Verb(49, "forget", "forgot", "forgotten/forgot"),
                Verb(50, "forgive", "forgave", "forgiven"),
                Verb(51, "freeze", "froze", "frozen"),
                Verb(52, "get", "got", "gotten/got"),
                Verb(53, "give", "gave", "given"),
                Verb(54, "go", "went", "gone"),
                Verb(55, "grind", "ground", "ground"),
                Verb(56, "grow", "grew", "grown"),
                Verb(57, "hang", "hung", "hung"),
                Verb(58, "have", "had", "had"),
                Verb(59, "hear", "heard", "heard"),
                Verb(60, "hide", "hid", "hidden/hid"),
                Verb(61, "hit", "hit", "hit"),
                Verb(62, "hold", "held", "held"),
                Verb(63, "hurt", "hurt", "hurt"),
                Verb(64, "input", "input/inputted", "input/inputted"),
                Verb(65, "keep", "kept", "kept"),
                Verb(66, "kneel", "knelt/kneeled", "knelt/kneeled"),
                Verb(67, "knit", "knitted/knit", "knitted/knit"),
                Verb(68, "know", "knew", "known"),
                Verb(69, "lay", "laid", "laid"),
                Verb(70, "lead", "led", "led"),
                Verb(71, "lean", "leaned/leant", "leaned/leant"),
                Verb(72, "leap", "leaped/leapt", "leaped/leapt"),
                Verb(74, "leave", "left", "left"),
                Verb(75, "lend", "lent", "lent"),
                Verb(76, "let", "let", "let"),
                Verb(77, "lie", "lay", "lain"),
                Verb(78, "lie", "lied", "lied"),
                Verb(79, "light", "lit/lighted", "lit/lighted"),
                Verb(80, "lose", "lost", "lost"),
                Verb(81, "make", "made", "made"),
                Verb(82, "mean", "meant", "meant"),
                Verb(83, "meet", "met", "met"),
                Verb(84, "mistake", "mistook", "mistaken"),
                Verb(85, "mow", "mowed", "mowed/mown"),
                Verb(86, "pay", "paid", "paid"),
                Verb(87, "plead", "pleaded/pled", "pleaded/pled"),
                Verb(88, "prove", "proved", "proven/proved"),
                Verb(89, "put", "put", "put"),
                Verb(90, "quit", "quit/quitted", "quit/quitted"),
                Verb(91, "read", "read", "read"),
                Verb(92, "rid", "rid", "rid"),
                Verb(93, "ride", "rode", "ridden"),
                Verb(94, "ring", "rang", "rung"),
                Verb(95, "rise", "rose", "risen"),
                Verb(96, "run", "ran", "run"),
                Verb(97, "saw", "sawed", "sawed/sawn"),
                Verb(98, "say", "said", "said"),
                Verb(99, "see", "saw", "seen"),
                Verb(100, "seek", "sought", "sought"),
                Verb(101, "sell", "sold", "sold"),
                Verb(102, "send", "sent", "sent"),
                Verb(103, "set", "set", "set"),
                Verb(104, "shake", "shook", "shaken"),
                Verb(105, "shave", "shaved", "shaved/shaven"),
                Verb(106, "shed", "shed", "shed"),
                Verb(107, "shine", "shined/shone", "shined/shone"),
                Verb(109, "shoot", "shot", "shot"),
                Verb(110, "show", "showed", "shown/showed"),
                Verb(111, "shrink", "shrank/shrunk", "shrunk"),
                Verb(112, "shut", "shut", "shut"),
                Verb(113, "sing", "sang", "sung"),
                Verb(114, "sink", "sank/sunk", "sunk"),
                Verb(115, "sit", "sat", "sat"),
                Verb(116, "sleep", "slept", "slept"),
                Verb(117, "slide", "slid", "slid"),
                Verb(118, "sling", "slung", "slung"),
                Verb(119, "slit", "slit", "slit"),
                Verb(120, "smell", "smelled/smelt", "smelled/smelt"),
                Verb(121, "sneak", "sneaked/snuck", "sneaked/snuck"),
                Verb(122, "sow", "sowed", "sown/sowed"),
                Verb(123, "speak", "spoke", "spoken"),
                Verb(124, "speed", "sped/speeded", "sped/speeded"),
                Verb(125, "spell", "spelled/spelt", "spelled/spelt"),
                Verb(126, "spend", "spent", "spent"),
                Verb(127, "spill", "spilled/spilt", "spilled/spilt"),
                Verb(128, "spin", "spun", "spun"),
                Verb(129, "spit", "spit/spat", "spit/spat"),
                Verb(130, "split", "split", "split"),
                Verb(131, "spoil", "spoiled/spoilt", "spoiled/spoilt"),
                Verb(132, "spread", "spread", "spread"),
                Verb(133, "spring", "sprang/sprung", "sprung"),
                Verb(134, "stand ", "stood", "stood"),
                Verb(135, "steal", "stole", "stolen"),
                Verb(136, "stick", "stuck", "stuck"),
                Verb(137, "sting", "stung", "stung"),
                Verb(138, "stink", "stunk/stank", "stunk"),
                Verb(139, "stride", "strode", "stridden"),
                Verb(140, "strike", "struck", "struck/stricken"),
                Verb(141, "string", "strung", "strung"),
                Verb(142, "strive", "strove/strived", "striven/strived"),
                Verb(143, "swear", "swore", "sworn"),
                Verb(144, "sweat", "sweat/sweated", "sweat/sweated"),
                Verb(145, "sweep", "swept", "swept"),
                Verb(146, "swell", "swelled", "swollen/swelled"),
                Verb(147, "swim", "swam", "swum"),
                Verb(148, "swing", "swung", "swung"),
                Verb(149, "take", "took", "taken"),
                Verb(150, "teach", "taught", "taught"),
                Verb(151, "tear", "tore", "torn"),
                Verb(152, "tell", "told", "told"),
                Verb(153, "think", "thought", "thought"),
                Verb(154, "throw", "threw", "thrown"),
                Verb(155, "thrust", "thrust", "thrust"),
                Verb(156, "tread", "trod", "trodden/trod"),
                Verb(157, "understand", "understood", "understood"),
                Verb(158, "upset", "upset", "upset"),
                Verb(159, "wake", "woke/waked", "woken/waked"),
                Verb(160, "wear", "wore", "worn"),
                Verb(161, "weave", "wove/weaved", "woven/weaved"),
                Verb(162, "weep", "wept", "wept"),
                Verb(163, "win", "won", "won"),
                Verb(164, "wind", "wound", "wound"),
                Verb(165, "withdraw", "withdrew", "withdrawn"),
                Verb(166, "wring", "wrung", "wrung"),
                Verb(167, "write", "wrote", "written"))
    }
}