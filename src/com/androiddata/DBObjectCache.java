package com.androiddata;

import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * Created by kurtguenther on 6/14/13.
 */
public class DBObjectCache {
    public HashMap<Field, DBColumn> maps = new HashMap<Field, DBColumn>();

    public static DBObjectCache make(Class c){
        DBObjectCache retVal = new DBObjectCache();

        Field[] allFields = c.getFields();

        for(Field f : allFields)
        {
            DBColumn dbf = f.getAnnotation(DBColumn.class);
            if(dbf != null)
            {
                retVal.maps.put(f, dbf);
            }
        }

        return retVal;
    }
}
