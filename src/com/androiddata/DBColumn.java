package com.androiddata;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DBColumn
{
    public enum DataType
    {
        TEXT,
        INTEGER,
        BOOL
    }

    String columnName();
    DataType dataType () default DataType.TEXT;
}

