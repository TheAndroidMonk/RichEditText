package com.gworks.richedittext;

import android.support.annotation.NonNull;

import com.gworks.richedittext.converters.AttributeConverter;
import com.gworks.richedittext.markups.Markup;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ReflectionUtil {

    @NonNull
    static Markup createMarkup(Class<? extends Markup> markupType, Object value) {
        try {
            Constructor<? extends Markup> cons = markupType.getConstructor(Object.class);
            return cons.newInstance(value);
        } catch (NoSuchMethodException
                | IllegalAccessException
                | InstantiationException
                | InvocationTargetException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Unable to create instance " + markupType);
        }
    }

    @NonNull
    static <T> Markup createMarkup(Class<? extends Markup> markupType, AttributeConverter<T> attributeConverter, T attr) {
        try {
            Constructor<? extends Markup> cons = markupType.getConstructor(AttributeConverter.class, attr.getClass());
            return cons.newInstance(attributeConverter, attr);
        } catch (NoSuchMethodException
                | IllegalAccessException
                | InstantiationException
                | InvocationTargetException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Unable to create instance " + markupType);
        }
    }

}
