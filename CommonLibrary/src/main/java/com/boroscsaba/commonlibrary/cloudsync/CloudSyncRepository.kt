package com.boroscsaba.commonlibrary.cloudsync

import android.content.Context
import com.boroscsaba.commonlibrary.ApplicationBase
import com.boroscsaba.dataaccess.*
import org.json.JSONStringer

open class CloudSyncRepository<T : PersistentObject>(private val context: Context, dataAccess: DataAccess, classType: Class<*>, private val mapper: DataMapper<T>)
    : RepositoryBaseBase<T>(context, dataAccess, classType, mapper) {

    override fun upsert(persistentObject: T, alreadySynced: Boolean): Int {
        val syncService = (context.applicationContext as ApplicationBase).getCloudSyncService()
        if (syncService != null) {
            val syncStartedOnDevice = syncService.getLastUpSyncDate() > 0
            if (!alreadySynced && syncStartedOnDevice) {
                val result: Int
                if (persistentObject.id != 0) {
                    val original = getObjectById(persistentObject.id)
                    result = super.upsert(persistentObject, alreadySynced)
                    syncUpdatedObject(original!!, persistentObject, syncService)
                } else {
                    result = super.upsert(persistentObject, alreadySynced)
                    syncNewObject(persistentObject, syncService)
                }
                return result
            }
        }
        return super.upsert(persistentObject, alreadySynced)
    }

    override fun delete(id: Int, alreadySynced: Boolean) {
        val syncService = (context.applicationContext as ApplicationBase).getCloudSyncService()
        if (syncService != null) {
            val syncStartedOnDevice = syncService.getLastUpSyncDate() > 0
            if (!alreadySynced && syncStartedOnDevice) {
                val persistentObject = getObjectById(id)
                if (persistentObject != null && persistentObject.guid != "") {
                    syncObjectDelete(persistentObject.guid, syncService)
                }
            }
        }
        super.delete(id, alreadySynced)
    }

    fun getNewObjectChangeString(persistentObject: T): String {
        var jsonStringer = JSONStringer().`object`()
                .key("object_type").value(mapper.tableName)
                .key("operation").value("add")
        mapper.getProperties(persistentObject).filter { property -> property.value != null }.forEach { property ->
            jsonStringer = jsonStringer.key(property.name).value(property.value)
        }

        val foreignProperties = mapper.getForeignKeyGuidIds(persistentObject)
        foreignProperties.forEach { property ->
            jsonStringer = jsonStringer.key(property.key).value(property.value)
        }

        jsonStringer = jsonStringer.endObject()
        return jsonStringer.toString()
    }

    private fun syncNewObject(persistentObject: T, syncService: CloudSyncServiceBase) {
        syncService.saveChange(getNewObjectChangeString(persistentObject))
    }

    private fun syncUpdatedObject(original: T, modified: T, syncService: CloudSyncServiceBase) {
        val originalProperties = mapper.getProperties(original)
        var jsonStringer = JSONStringer().`object`()
                .key("object_type").value(mapper.tableName)
                .key("operation").value("update")
        mapper.getProperties(modified).forEach { property ->
            val originalProperty = originalProperties.first { p -> p.name == property.name }
            if (originalProperty.value != property.value || property.name == "guid") {
                jsonStringer = jsonStringer.key(property.name).value(property.value)
            }
        }

        val foreignProperties = mapper.getForeignKeyGuidIds(modified)
        foreignProperties.forEach { property ->
            jsonStringer = jsonStringer.key(property.key).value(property.value)
        }

        jsonStringer = jsonStringer.endObject()
        syncService.saveChange(jsonStringer.toString())
    }

    fun syncUpdatedImages(persistentObject: T) {
        val syncService = (context.applicationContext as ApplicationBase).getCloudSyncService()
        if (syncService != null) {
            val images = getImagesInBase64(persistentObject)
            images.forEach { image ->
                val jsonStringer = JSONStringer().`object`()
                        .key("object_type").value(mapper.tableName)
                        .key("guid").value(persistentObject.guid)
                        .key("data").value(image)
                        .endObject()
                syncService.saveImage(jsonStringer.toString())
            }
        }
    }

    open fun getImagesInBase64(persistentObject: T): ArrayList<String> { return ArrayList() }

    private fun syncObjectDelete(guid: String, syncService: CloudSyncServiceBase) {
        val jsonStringer = JSONStringer().`object`()
                .key("object_type").value(mapper.tableName)
                .key("operation").value("delete")
                .key("guid").value(guid)
                .endObject()
        syncService.saveChange(jsonStringer.toString())
    }
}