package com.gworks.richedittext

import com.gworks.richedittext.converters.AttributeConverter
import com.gworks.richedittext.markups.Markup
import kotlin.reflect.full.createType

internal fun createMarkup(type: Class<out Markup>, attr: Any?): Markup {
    if (attr == null) return type.newInstance()
    val attrType = attr::class.createType()
    val constructor = type.kotlin.constructors.firstOrNull {
        it.parameters.size == 1
                && it.parameters[0].type == attrType
    }
    return constructor?.call(attr) ?: ReflectionUtil.createMarkup(type, attr)
}

internal fun <T : Any> createMarkup(type: Class<out Markup>, attributeConverter: AttributeConverter<T>, attr: T): Markup {
    val converterType = AttributeConverter::class
    val constructor = type.kotlin.constructors.firstOrNull {
        it.parameters.size == 2
                && it.parameters[0].type.classifier == converterType
    }
    return constructor?.call(attributeConverter, attr) ?: ReflectionUtil.createMarkup(type, attributeConverter, attr)
}
