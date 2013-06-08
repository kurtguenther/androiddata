package com.androiddata;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class DBObject {

    //Some SQL constants
    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER";
    private static final String BOOL_TYPE = " INTEGER";
    private static final String DATE_TYPE = " BIGINT";

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
            sb.append(col.columName());
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

}
