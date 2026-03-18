package io.github.gyroskalitz.adherequest.core

import io.github.gyroskalitz.adherequest.core.processor.PersonPropertyProcessor

/**
 * 单个`Question`的定义。
 *
 * @property question `Question`的内容/标题。
 * @property answerFormat 定义了该`Question`期望的`Answer`格式。
 * 系统会根据此 [PersonPropertyInfo] 的类型寻找匹配的[PersonPropertyProcessor]。
 */
data class QuestionData(val question: String, val answerFormat: PersonPropertyInfo)

/**
 * `Questionnaire`的结构定义。
 * 包含`Questionnaire`的基本信息（名称、创建者）以及一个[QuestionData]的列表。
 *
 * @property name 问卷标题。
 * @property creatorName 创建者名称。起辅助作用，主要看创建者的ID（[creatorId]）。
 * @property creatorId 创建者的ID。
 * @property questions 问卷包含的`QuestionData`问题列表。
 */
data class QuestionnaireData(val name: String, val creatorName: String, val creatorId: String, val questions: List<QuestionData>)
