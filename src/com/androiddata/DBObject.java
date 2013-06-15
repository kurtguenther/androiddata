package com.androiddata;

import java.lang.Object;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import android.content.ContentValues;
import android.database.Cursor;

public class DBObject {

    public static HashMap<Class, DBObjectCache> globalCache = new HashMap<Class, DBObjectCache>();

    public static DBObjectCache getObjectCache(Class c){
        if(!globalCache.containsKey(c)){
            globalCache.put(c, DBObjectCache.make(c));
        }
        return globalCache.get(c);
    }


    //Some SQL constants
    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER";
    private static final String BOOL_TYPE = " INTEGER";
    private static final String DATE_TYPE = " BIGINT";

    public DBObject() { }

    public DBObject(Cursor c){
        //Get the mappings for this class
        DBObjectCache cache = getObjectCache(this.getClass());

        for(Field f : cache.maps.keySet())
        {
            DBColumn dbf = cache.maps.get(f);
            if(dbf != null)
            {
                try {
                    Object val = null;

                    switch(dbf.dataType())
                    {
                        case TEXT:
                            val = c.getString(c.getColumnIndex(dbf.columnName()));
                            break;
                        case INTEGER:
                            val = c.getInt(c.getColumnIndex(dbf.columnName()));
                            break;
                        case BOOL:
                            val = c.getInt(c.getColumnIndex(dbf.columnName())) == 1;
                        default:
                            break;
                    }

                    f.set(this, val);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public ArrayList<DBColumn> getColumns(){
        ArrayList<DBColumn> retVal = new ArrayList<DBColumn>();
        Field[] allFields = this.getClass().getFields();
        for(Field f: allFields){
            DBColumn dbf = f.getAnnotation(DBColumn.class);
            if(dbf != null){
                retVal.add(dbf);
            }
        }
        return retVal;
    }

    public String[] getColumnNames() {
        ArrayList<DBColumn> cols = getColumns();
        String[] retVal = new String[cols.size()];
        for(int i = 0; i < cols.size(); i++){
            retVal[i] = cols.get(i).columnName();
        }
        return retVal;
    }

    public String getTableName(){
        DBTable tableAnnotation = this.getClass().getAnnotation(DBTable.class);
        return tableAnnotation.tableName();
    }

    public String getCreateScript(){

        StringBuilder sb = new StringBuilder();

        sb.append("CREATE TABLE ");
        sb.append(getTableName());
        sb.append(" ( _id INTEGER PRIMARY KEY");

        for(DBColumn col : getColumns()){
            sb.append(", ");
            sb.append(col.columnName());
            switch (col.dataType()) {
                case INTEGER:
                    sb.append(INT_TYPE);
                    break;
                case TEXT:
                    sb.append(TEXT_TYPE);
                    break;
                case BOOL:
                    sb.append(BOOL_TYPE);
                    break;
            }
        }

        sb.append("); ");

        return sb.toString();
    }

    public String getDropScript(){
        return "DROP TABLE IF EXISTS " + getTableName();
    }

    protected ContentValues getContentValues()
    {
        ContentValues retVal = new ContentValues();

        DBObjectCache cache = getObjectCache(this.getClass());

        //Field[] allFields = this.getClass().getFields();

        for(Field f : cache.maps.keySet())
        {
            //DBColumn dbc =  f.getAnnotation(DBColumn.class);
            DBColumn dbc =  cache.maps.get(f);
            if(dbc != null)
            {
                try {
                    Object o = f.get(this);
                    if(o != null)
                    {
                        switch(dbc.dataType()) {
                            case BOOL:
                                if((Boolean) o)
                                    retVal.put(dbc.columnName(), "1");
                                else
                                    retVal.put(dbc.columnName(), "0");
                                break;
                            default:
                                retVal.put(dbc.columnName(), o.toString());
                                break;
                        }
                    }
                } catch (IllegalArgumentException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        return retVal;
    }


    public boolean insert(android.database.sqlite.SQLiteDatabase db){
        ContentValues values = getContentValues();
        db.insert(getTableName(), null, values);
        return true;
    }

    public void deleteAll(android.database.sqlite.SQLiteDatabase db){
        db.execSQL("delete from " + getTableName());
    }
}
