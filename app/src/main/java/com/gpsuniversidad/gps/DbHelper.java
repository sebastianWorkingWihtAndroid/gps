package com.gpsuniversidad.gps;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DbHelper extends SQLiteOpenHelper {


    public
    DbHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory,
                    int version) {
        super(context, "gps", factory, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table descripcion (id INTEGER PRIMARY KEY AUTOINCREMENT,latitud TEXT," +
                " longitud TEXT, altitud TEXT, descripcion TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("drop table if exists descripcion");

    }

    public boolean insertarDatos(String latitud, String longitud, String altitud, String descripcion){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();

        valores.put("latitud",latitud);
        valores.put("longitud",longitud);
        valores.put("altitud",altitud);
        valores.put("descripcion",descripcion);

        long resultado = db.insert("descripcion",null,valores);
        if (resultado==-1)
            return false;
        else
            return true;

    }
}
