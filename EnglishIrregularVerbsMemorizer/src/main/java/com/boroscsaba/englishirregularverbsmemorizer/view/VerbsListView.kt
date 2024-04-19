package com.boroscsaba.englishirregularverbsmemorizer.view

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.TextWatcher
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.boroscsaba.commonlibrary.LoggingHelper
import com.boroscsaba.commonlibrary.activities.FragmentBase
import com.boroscsaba.commonlibrary.adapters.SimpleRecyclerViewAdapter
import com.boroscsaba.englishirregularverbsmemorizer.R
import com.boroscsaba.englishirregularverbsmemorizer.model.Verb
import com.boroscsaba.englishirregularverbsmemorizer.viewmodel.StatsViewModel
import kotlinx.android.synthetic.main.fragment_verbs_list.*
import java.util.*


class VerbsListView: FragmentBase(R.layout.fragment_verbs_list), TextToSpeech.OnInitListener {

    private var viewModel: StatsViewModel? = null
    private var textToSpeech: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        textToSpeech = TextToSpeech(context, this)
    }

    @SuppressWarnings("unchecked")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = activity?.run {
            ViewModelProviders.of(this).get(StatsViewModel::class.java)
        }
        viewModel?.verbs?.observe(this, Observer { verbs ->
            if (verbs != null) {
                if (recyclerView.adapter == null) {
                    recyclerView.layoutManager = LinearLayoutManager(activity)
                    val adapter = SimpleRecyclerViewAdapter(verbs, R.layout.verb_list_item_layout, { holder, verb ->
                        holder.itemView.findViewById<TextView>(R.id.infinitive).text = verb.infinitive
                        holder.itemView.findViewById<TextView>(R.id.simplePast).text = verb.simplePast
                        holder.itemView.findViewById<TextView>(R.id.pastParticiple).text = verb.pastParticiple
                        holder.itemView.findViewById<ImageView>(R.id.toggleVisibility).setOnClickListener {
                            var text = "${verb.infinitive}, "
                            text += if (verb.simplePast.contains("/")) {
                                val parts = verb.simplePast.split("/")
                                "${parts[0]}, ${parts[1]}, "
                            } else {
                                verb.simplePast + ", "
                            }
                            text += if (verb.pastParticiple.contains("/")) {
                                val parts = verb.pastParticiple.split("/")
                                "${parts[0]}, ${parts[1]}"
                            } else {
                                verb.pastParticiple
                            }
                            textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, text)
                        }
                    })
                    recyclerView.adapter = adapter
                }
                else {
                    @Suppress("UNCHECKED_CAST")
                    val adapter = recyclerView.adapter as SimpleRecyclerViewAdapter<Verb>
                    adapter.changeValues(ArrayList(verbs.filter { v -> !v.isFilteredOut }))
                    adapter.notifyDataSetChanged()
                }
            }
        })
    }

    override fun onInit(status: Int) {
        if(status != TextToSpeech.ERROR) {
            textToSpeech?.language = Locale.ENGLISH
            textToSpeech?.setSpeechRate(0.3f)
        }
        else {
            LoggingHelper.logException(Exception("TextToSpeech error $status"), context!!)
        }
    }

    override fun onPause() {
        super.onPause()
        textToSpeech?.stop()
        textToSpeech?.shutdown()
    }

    private var previousSearch = ""
    override fun setListeners() {
        activity?.findViewById<TextView>(R.id.searchEditText)?.addTextChangedListener(object: TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val search = s.toString()
                if (previousSearch != search) {
                    previousSearch = search
                    viewModel?.search(search)
                }
            }
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        })
    }
}