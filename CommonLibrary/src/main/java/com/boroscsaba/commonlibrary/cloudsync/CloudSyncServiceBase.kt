package com.boroscsaba.commonlibrary.cloudsync

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import com.boroscsaba.commonlibrary.ApplicationBase
import com.boroscsaba.commonlibrary.AsyncTask
import com.boroscsaba.commonlibrary.LoggingHelper
import com.boroscsaba.commonlibrary.R
import com.boroscsaba.commonlibrary.settings.SettingsHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONObject
import org.json.JSONStringer
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet


open class CloudSyncServiceBase(private val application: ApplicationBase) {

    fun synchronizeData() {
        if (!SettingsHelper(application).isCloudSyncEnabled()) return
        val user = FirebaseAuth.getInstance().currentUser ?: return

        val db = FirebaseFirestore.getInstance()
        if (getLastUpSyncDate() <= 0) {
            val data = HashMap<String, Any>()
            data["uid"] = user.uid
            data["name"] = user.displayName ?: ""
            data["email"] = user.email ?: ""
            db.collection("users").document(user.uid).set(data)
                    .addOnSuccessListener {
                        val changeSet = HashMap<String, Any>()
                        changeSet["date"] = FieldValue.serverTimestamp()
                        changeSet["data"] = getInitialChangeString()
                        changeSet["device"] = LoggingHelper().getDeviceId(application)
                        val initialChangeId = LoggingHelper().getDeviceId(application)
                        db.collection("users").document(user.uid).collection("changes").document(initialChangeId).set(changeSet)
                                .addOnSuccessListener {
                                    setLastUpSyncDate()
                                    setChangeListener(user)
                                }

                        val images = getInitialImages()
                        images.forEach { imageString ->
                            val imageChange = HashMap<String, Any>()
                            imageChange["date"] = FieldValue.serverTimestamp()
                            imageChange["data"] = imageString.second
                            imageChange["device"] = LoggingHelper().getDeviceId(application)
                            db.collection("users").document(user.uid).collection("images").document(imageString.first).set(imageChange)
                        }
                    }
        }
        else {
            val localImages = getLocalChanges("images")
            if (localImages.isNotEmpty()) {
                localImages.forEach { imageString ->
                    val imageChange = HashMap<String, Any>()
                    imageChange["date"] = FieldValue.serverTimestamp()
                    imageChange["data"] = localImages
                    imageChange["device"] = LoggingHelper().getDeviceId(application)
                    try {
                        db.collection("users").document(user.uid).collection("images").document(JSONObject(imageString).getString("guid")).set(imageChange)
                    }
                    catch (e: Exception) {
                        var localImagesDump = ""
                        localImages.forEach { l -> localImagesDump += " -> $l" }
                        LoggingHelper.logException(e, application, localImagesDump)
                    }
                }
                clearLocalChanges("images")
            }

            val localChanges = getLocalChanges("changes")
            if (localChanges.isNotEmpty()) {
                var jsonStringer = JSONStringer()
                        .`object`()
                        .key("items").array()

                localChanges.forEach { change ->
                    jsonStringer = jsonStringer.value(change)
                }
                jsonStringer = jsonStringer.endArray()
                        .endObject()

                val changeSet = HashMap<String, Any>()
                changeSet["date"] = FieldValue.serverTimestamp()
                changeSet["data"] = jsonStringer.toString()
                changeSet["device"] = LoggingHelper().getDeviceId(application)
                db.collection("users").document(user.uid).collection("changes").add(changeSet)
                        .addOnSuccessListener {
                            setLastUpSyncDate()
                            clearLocalChanges("changes")
                        }
                        .addOnFailureListener { e ->
                            LoggingHelper.logException(e, application)
                        }

            }
            setChangeListener(user)
        }
    }

    fun setImage(guid: String, task: (ByteArray) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser
        db.collection("users").document(user!!.uid).collection("images").document(guid).addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            if (firebaseFirestoreException != null) {
                LoggingHelper.logException(firebaseFirestoreException, application)
            }
            else {
                val string = querySnapshot?.getString("data")
                if (string != null) {
                    val imageBytes = JSONObject(string).getString("data")
                    task.invoke(Base64.decode(imageBytes, Base64.URL_SAFE))
                }
            }
        }
    }

    private fun setChangeListener(user: FirebaseUser) {
        val db = FirebaseFirestore.getInstance()
        application.cloudChangeListener = db.collection("users").document(user.uid).collection("changes")
                .whereGreaterThan("date", Date(getLastDownSyncDate()))
                .orderBy("date")
                .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                    if (firebaseFirestoreException != null) {
                        LoggingHelper.logException(firebaseFirestoreException, application)
                    }
                    else {
                        querySnapshot?.documents?.forEach { document ->
                            if (document["date"] != null) {
                                val timestamp = document.getDate("date")?.time
                                if (timestamp != null && timestamp > getLastDownSyncDate() && document["device"] != LoggingHelper().getDeviceId(application)) {
                                    AsyncTask().execute({ processChange(document.getString("data") ?: "") })
                                    setLastDownSyncDate(timestamp)
                                }
                            }
                        }
                    }
                }
    }

    private fun processChange(changeString: String) {
        val json = JSONObject(changeString)
        if (json.has("items")) {
            val items = json.getJSONArray("items")
            for (i in 0 until items.length()) {
                processChangeItem(items.getString(i))
            }
        }
        else if (json.has("object_type")) {
            processChangeItem(changeString)
        }
    }

    private fun processChangeItem(changeString: String) {
        val json = JSONObject(changeString)
        val type = json.getString("object_type")
        val operation = json.getString("operation")
        val properties = HashMap<String, Any>()
        json.keys().forEach { key ->
            properties[key] = json.get(key)
        }
        processSyncedObject(type, operation, properties)
    }

    open fun processSyncedObject(type: String, operation: String, properties: HashMap<String, Any>) { }

    open fun getInitialChangeString(): String { return "" }

    open fun getInitialImages(): ArrayList<Pair<String, String>> { return ArrayList() }

    fun saveChange(changeString: String) {
        val isCloudSyncEnabled = SettingsHelper(application).isCloudSyncEnabled()
        val user = FirebaseAuth.getInstance().currentUser
        if (isCloudSyncEnabled && user != null) {
            val db = FirebaseFirestore.getInstance()
            val changeSet = HashMap<String, Any>()
            changeSet["date"] = FieldValue.serverTimestamp()
            changeSet["data"] = changeString
            changeSet["device"] = LoggingHelper().getDeviceId(application)
            db.collection("users").document(user.uid).collection("changes").add(changeSet)
                    .addOnSuccessListener {
                        setLastUpSyncDate()
                    }
                    .addOnFailureListener { e ->
                        LoggingHelper.logException(e, application)
                        saveChangeLocally(changeString)
                    }
        }
        else {
            saveChangeLocally(changeString)
        }
    }

    fun saveImage(imageString: String) {
        val isCloudSyncEnabled = SettingsHelper(application).isCloudSyncEnabled()
        val user = FirebaseAuth.getInstance().currentUser
        val syncStartedOnDevice = getLastUpSyncDate() > 0
        if (syncStartedOnDevice) {
            if (isCloudSyncEnabled && user != null) {
                val db = FirebaseFirestore.getInstance()
                val changeSet = HashMap<String, Any>()
                changeSet["date"] = FieldValue.serverTimestamp()
                changeSet["data"] = imageString
                changeSet["device"] = LoggingHelper().getDeviceId(application)
                db.collection("users").document(user.uid).collection("images").document(JSONObject(imageString).getString("guid")).set(changeSet)
                        .addOnSuccessListener {
                            setLastUpSyncDate()
                        }
                        .addOnFailureListener { e ->
                            LoggingHelper.logException(e, application)
                            saveImageLocally(imageString)
                        }
            } else {
                saveImageLocally(imageString)
            }
        }
    }

    private fun saveChangeLocally(change: String) {
        val sharedPreferences = getSharedPreferences()
        val localChanges = sharedPreferences.getStringSet("changesForSync", HashSet<String>())
        val newSet = HashSet(localChanges)
        newSet.add(change)
        with (sharedPreferences.edit()) {
            putStringSet("changesForSync", newSet)
            apply()
        }
    }

    private fun saveImageLocally(change: String) {
        val sharedPreferences = getSharedPreferences()
        val localChanges = sharedPreferences.getStringSet("imagesForSync", HashSet<String>())
        val newSet = HashSet(localChanges)
        newSet.add(change)
        with (sharedPreferences.edit()) {
            putStringSet("imagesForSync", newSet)
            apply()
        }
    }

    private fun clearLocalChanges(type: String) {
        val sharedPreferences = getSharedPreferences()
        with (sharedPreferences.edit()) {
            putStringSet(type + "ForSync", HashSet<String>())
            apply()
        }
    }

    private fun getLocalChanges(type: String): HashSet<String> {
        val sharedPreferences = getSharedPreferences()
        val set = sharedPreferences.getStringSet(type + "ForSync", HashSet<String>()) ?: HashSet<String>()
        return set.toHashSet()
    }

    private fun getSharedPreferences(): SharedPreferences {
        return application.getSharedPreferences(application.getString(R.string.shared_preferences_name), Context.MODE_PRIVATE)
    }

    fun getLastUpSyncDate(): Long {
        return getSharedPreferences().getLong("lastUpSyncDate", 0)
    }

    private fun setLastUpSyncDate() {
        with (getSharedPreferences().edit()) {
            putLong("lastUpSyncDate", System.currentTimeMillis())
            apply()
        }
    }

    private fun getLastDownSyncDate(): Long {
        return getSharedPreferences().getLong("lastDownSyncDate", 0)
    }

    private fun setLastDownSyncDate(date: Long) {
        if (date > getLastDownSyncDate()) {
            with(getSharedPreferences().edit()) {
                putLong("lastDownSyncDate", date)
                apply()
            }
        }
    }
}