package io.github.gyroskalitz.adherequest.core.io

import io.github.gyroskalitz.adherequest.core.PersonProperty
import io.github.gyroskalitz.adherequest.core.PersonPropertyInfo
import java.io.File
/**
 * `Property Metadata`在`Property`工作目录中对应的 JSON 文件名称。
 */
private const val PropertyMetadataJsonName="property_metadata.json"

/**
 * 用于管理[PersonProperty]工作目录的作用域。
 *
 * 该类负责通过文件系统的访问`Property`。主要用于处理`Property`相关的序列化和反序列化。
 *
 * @property propertyWorkDir 该`Property`对应的工作目录。
 */
open class PropertyWorkDirScope(val propertyWorkDir: File){

    /**
     * 获取指向存储[PersonPropertyInfo]的 JSON 文件对应的 `File` 对象。
     *
     * @return `Metadata`对应的 `File` 对象。
     */
    open fun metadataFile(): File {
        return File(propertyWorkDir,PropertyMetadataJsonName)
    }
    /**
     * 将提供的[PersonPropertyInfo]保存到工作目录中。
     *
     * 如果 [propertyWorkDir] 尚不存在，该方法会先创建`propertyWorkDir`对应的目录。
     * `Metadata`将以 JSON 格式写入 [PropertyMetadataJsonName] 文件。
     *
     * @param propInfo 要保存的`PersonPropertyInfo`。
     */
    open fun saveMetadata(propInfo: PersonPropertyInfo){
        if (!propertyWorkDir.exists()) propertyWorkDir.mkdirs()
        val metadata=metadataFile()
        metadata.createNewFile()
        metadata.writeText(NoHtmlEscapingGson.toJson(propInfo))
    }

    /**
     * 从`Property`工作目录中读取并解析`Property Metadata`。
     *
     * 该方法会读取 [PropertyMetadataJsonName] 文件的内容，并将其反序列化为 [PersonPropertyInfo] 对象。
     *
     * @return 解析出的[PersonPropertyInfo]。
     */
    open fun extractPropertyInfo(): PersonPropertyInfo {
        val metadataFile = metadataFile()
        return NoHtmlEscapingGson.fromJson(metadataFile.readText(), PersonPropertyInfo::class.java)
    }
}

