# AdheReQuest Core

**AdheReQuest Core** 是 `AdheReQuest` App的核心数据类型库。该库旨在通过分离“问题”与“数据内容”，将问卷内容转化为便于处理的结构化数据，并支持自动填充机制，以图实现统一的数据描述协议，实现跨App、跨平台的问卷数据标准化。

## 1. 核心理念

AdheReQuest Core 通过分离属性数据和题目，来解决传统问卷系统中题目问法多变导致数据难以识别和复用的问题。

* **属性驱动**：每一项数据对应一种“个人属性”（PersonProperty），使得不同问法的同类数据收集需求可以共享同一个数据源。
* **自动填充**：利用属性名，系统可以自动识别并填充已有的数据。
* **平衡数据传递与易用性**：用 JSON 传递信息量小的问卷，用文件和目录承载类型多样的数据。
* **类型开放**：可方便地添加自定义数据类型。
* **坏点处理**：利用提供的预置回答状态，来帮助用户标识数据的无效性（如格式不支持，不便回答），以免为问题独立设置特殊选项

## 2. 核心数据模型

### 个人属性 (PersonProperty)
* **PersonPropertyInfo**: 属性的元数据，定义了属性名称和类型标识符（`valueTypeString`）。由属性名称和类型标识符共同标识一种属性。
* **PersonProperty**: 实际的数据实体，包含元数据信息和具体的值。

### 问卷 (QuestionnaireData)
* **QuestionnaireData**: 问卷的容器，包含问卷名称、创建者信息以及一组问题列表。
* **QuestionData**: 单个问题的定义，包含问题文本和预期的回答格式（`PersonPropertyInfo`）。

### 回复 (ResponseData)
* **ResponseData**: 记录了回答者的名称，ID、填写时间戳以及每个问题的回答状态（如：未回答、已回答、标记为空、出错）。

## 3. 属性处理器 (Property Processor)

核心库通过 `PersonPropertyProcessor` 接口处理不同类型的数据。它负责数据的序列化、反序列化以及格式匹配校验。

核心库内置了一些常用的属性数据格式，目前内置的属性处理器包括：

| 属性处理器名称                                  | 说明                     |
|:-----------------------------------------|:-----------------------|
| `PlainTextPropertyProcessor`             | 纯文本格式。                 |
| `NumberPropertyProcessor`                | 数字类型，以字符串形式持久化以避免精度丢失。 |
| `SelectionPropertyProcessor`<sup>*</sup> | 选择题，支持单选/多选，带选项校验。     |
| `SorterPropertyProcessor`<sup>*</sup>    | 排序题，支持对指定项进行顺序排列。      |
| `ImagesPropertyProcessor`<sup>*</sup>    | 图片类型，支持设置允许的图片数量范围。    |
| `LabelsPropertyProcessor`                | 标签组类型，用于收集带描述的标签数据。    |

*注：SelectionPropertyProcessor,SorterPropertyProcessor,ImagesPropertyProcessor的`valueTypeString`并非常量，其同时包含了题目的结构信息。*

## 4. 存储与 IO 规范

核心库定义了标准的文件组织结构，以便于数据交换：

* **Property 工作目录**：每个属性数据保存在独立的目录下。包含 `property_metadata.json`（元数据）和实际数据文件。对于`TextSaveValuePropertyProcessor`，其序列化后的文本值存储于`value.txt`。
* **Response 工作目录**：包含 `response_metadata.json`，记录了回答的相关元数据，内以多个属性文件夹存储回答内容。注意：在文件系统中实际的属性存储路径需要自行实现。
* **序列化工具**：内置了经过配置的 Gson 实例（`NoHtmlEscapingGson`），确保 JSON 输出的美观与兼容性。

## 5. 开始使用

### 1. 添加仓库和依赖

由于本项目托管在 JitPack 上，你需要在项目的根目录 `build.gradle` (或 `settings.gradle`) 中添加 JitPack 仓库：

#### Gradle (Kotlin DSL)
```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

然后在模块的 `build.gradle` 中添加依赖：

```kotlin
dependencies {
    // 请将 version 替换为具体的版本号
    implementation("com.github.gyroskalitz:adhe-re-quest-core:version")
}
```

---

## 2. 核心操作流程

### 创建一份简单的问卷 (QuestionnaireData)

可以通过定义 `QuestionData` 来利用代码构建一份问卷。每个问题都需要指定其回答格式。

```kotlin
val nameQuestion = QuestionData(
    text = "您的姓名？",
    answerFormat = PersonPropertyInfo("name", "plain_text")
)

val ageQuestion = QuestionData(
    text = "您的年龄？",
    answerFormat = PersonPropertyInfo("age", "number")
)

val questionnaire = QuestionnaireData(
    name = "测试问卷",
    creator = "Admin",
    questions = listOf(nameQuestion, ageQuestion)
)
```

### 处理问卷回答 (ResponseData)

可以通过定义 `ResponseData` 实例，来生成一份回答的元数据。出于与文件系统解耦的目的，文件系统中实际的属性名存储目录需要自行配置，推荐与`questionnaire`中的各个属性对应。

```kotlin
val response = ResponseData(
    respondentName = "张三",
    respondentId = "(user_uuid)",
    questionnaire = questionnaire,
    createdEditTimeStamp = System.currentTimeMillis(),
    lastEditTimeStamp = System.currentTimeMillis(),
    answerStatusList = listOf(AnswerStatus.Answered, AnswerStatus.Answered)
)
```

### 使用属性处理器 (PropertyProcessor)

核心库使用属性处理器来管理不同类型数据的读写和呈现（前端）。你可以通过这些处理器来读取或保存用户的结构化属性，或通过继承来创建自己的属性数据类型及其读写协议。

```kotlin
// 创建一个属性处理器实例
val processor = PlainTextPropertyProcessor()

// 创建一个测试用的属性实例
val property = PersonProperty(
    info = nameQuestion.answerFormat,
    value = "张三"
)

// 序列化为可存储的文本
val savedString = processor.serialize(property.info, property.value)

// 从文本恢复数据
val restoredValue = processor.deserialize(property.info, savedString)

```