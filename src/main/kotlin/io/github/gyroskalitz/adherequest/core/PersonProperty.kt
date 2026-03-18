package io.github.gyroskalitz.adherequest.core


/**
 * [PersonProperty]对应的`Metadata`，是对一个`Property`格式的定义。
 * 用于描述一个数据项的名称及其预期的值类型。
 *
 * @property name `Property`的显示名称
 * @property valueTypeString 值的类型标识符，用于匹配对应的`PersonPropertyProcessor`
 */
data class PersonPropertyInfo(val name: String, val valueTypeString: String)

/**
 * `Property`的数据实例。
 *
 * @property info 指向该`Property`的定义信息 [PersonPropertyInfo]
 * @property value `Property`的实际值，类型需要与 `info.valueTypeString` 对应
 */
data class PersonProperty(val info: PersonPropertyInfo, val value: Any)