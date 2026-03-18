package io.github.gyroskalitz.adherequest.core.processor

import io.github.gyroskalitz.adherequest.core.PersonProperty
import io.github.gyroskalitz.adherequest.core.PersonPropertyInfo
import java.io.File

/**
 * `PropertyProcessor`接口。
 * 负责处理特定类型的 [PersonPropertyInfo]。
 */
interface PersonPropertyProcessor {
    companion object{
        open class PropertyProcessException(val property: PersonProperty,val processor: PersonPropertyProcessor, override val message: String?, override val cause: Throwable?=null): RuntimeException(message,cause)
        /**
         * 当[PersonPropertyProcessor]不支持处理给定的[PersonProperty]时抛出的异常。
         */
        class PropertyTypeNotSupportedException(property: PersonProperty,processor: PersonPropertyProcessor, override val cause: Throwable?=null): PropertyProcessException(property,processor,"Property Type Not Support",cause)
        /**
         * 当[PersonPropertyProcessor]在加载[PersonProperty]失败时抛出的异常。
         */
        class PropertyLoadFailedException(property: PersonProperty,processor: PersonPropertyProcessor, override val cause: Throwable?=null): PropertyProcessException(property,processor,"Property Load Failed",cause)
        /**
         * 当[PersonPropertyProcessor]在保存[PersonProperty]失败时抛出的异常。
         */
        class PropertySaveFailedException(property: PersonProperty,processor: PersonPropertyProcessor, override val cause: Throwable?=null): PropertyProcessException(property,processor,"Property Save Failed",cause)
    }
    /**
     * 判断当前`PropertyProcessor`是否支持处理给定的[PersonPropertyInfo]。
     */
    fun typeMatches(propInfo: PersonPropertyInfo): Boolean
    /**
     * 校验两个[PersonPropertyInfo]在格式上是否兼容（用于自动填充中验证`Property`是否相同）。
     */
    fun formatMatches(propInfo1: PersonPropertyInfo, propInfo2: PersonPropertyInfo): Boolean
    /**
     * 从工作目录中加载[PersonProperty]。
     * @param workDir [PersonProperty]的工作目录。
     * @throws PropertyTypeNotSupportedException 当`PersonProperty` 类型不被该`PropertyProcessor`支持时抛出。
     * @throws PropertyLoadFailedException 当加载[PersonProperty]失败时抛出。
     */
    fun load(propInfo: PersonPropertyInfo, workDir: File): PersonProperty
    /**
     * 将[PersonProperty]保存到工作目录中。
     * @param workDir [PersonProperty]的工作目录
     * @throws PropertyTypeNotSupportedException 当`PersonProperty` 类型不被该`PropertyProcessor`支持时抛出。
     * @throws PropertySaveFailedException 当保存[PersonProperty]失败时抛出。
     */
    fun save(property: PersonProperty, workDir: File)
    /**
     * 为支持处理的[PersonPropertyInfo]生成一个默认的初始`PersonProperty`实例。
     * @throws PropertyTypeNotSupportedException 当`PersonProperty` 类型不被该`PropertyProcessor`支持时抛出。
     */
    fun generateDefaultProperty(propInfo: PersonPropertyInfo, workDir: File): PersonProperty
}