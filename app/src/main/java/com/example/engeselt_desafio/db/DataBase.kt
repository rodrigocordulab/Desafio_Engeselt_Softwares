package com.example.engeselt_desafio.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.widget.Toast
import com.example.engeselt_desafio.data.model.FormLocal
import com.example.engeselt_desafio.data.model.FormLocalUpdateDelete

//Definições do Banco de Dados, utilizando o SQLite
val DATABASE_NAME = "MyLocations"

val TABLE_NAME = "Locations"
val COL_ID = "id"
val COL_NAMEPOINT = "namePoint"
val COL_LATITUDEPOINT = "latitudePoint"
val COL_LONGITUDEPOINT = "longitudePoint"
val COL_IMAGE = "image_data"

class DataBase(var context: Context) : SQLiteOpenHelper(context, DATABASE_NAME,null,1){
    //Cria o banco de dados
    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = "CREATE TABLE " + TABLE_NAME +" ("+
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_NAMEPOINT + " VARCHAR(256)," +
                COL_LATITUDEPOINT + " REAL," +
                COL_LONGITUDEPOINT + " REAL," +
                COL_IMAGE + " BLOB)";

        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("not implemented")
    }

    //Para adição de um novo local
    fun insertData(localPoint : FormLocal){
        val db = this.writableDatabase

        var cv = ContentValues()
        cv.put(COL_NAMEPOINT, localPoint.namePoint)
        cv.put(COL_LATITUDEPOINT, localPoint.latitudePoint)
        cv.put(COL_LONGITUDEPOINT, localPoint.longitudePoint)

        var result = db.insert(TABLE_NAME,null,cv)
        if(result == (-1).toLong()){
            Toast.makeText(context, "Falhou!",Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(context, "Local inserido com sucesso!",Toast.LENGTH_SHORT).show()
        }
    }

    //Buscar todos os Locais cadastrado no banco
    fun getAll() : MutableList<FormLocal>{
        var list : MutableList<FormLocal> = ArrayList()

        val db = this.readableDatabase
        val query = "Select * from $TABLE_NAME"
        val result = db.rawQuery(query, null)
        if(result.moveToFirst()){
            do {
                var local = FormLocal()
                local.id = result.getString(result.getColumnIndex(COL_ID)).toInt()
                local.namePoint = result.getString(result.getColumnIndex(COL_NAMEPOINT))
                local.longitudePoint = result.getDouble(result.getColumnIndex(COL_LONGITUDEPOINT))
                local.latitudePoint = result.getDouble(result.getColumnIndex(COL_LATITUDEPOINT))

                list.add(local)
            }while (result.moveToNext())
        }

        result.close()
        db.close()
        return list
    }

    //Edição de um Local cadastrado no Banco atraves do seu ID
    fun editData(localPoint: FormLocalUpdateDelete){
        val db = this.writableDatabase

        var cv = ContentValues()
        cv.put(COL_NAMEPOINT,localPoint.namePoint)
        cv.put(COL_LATITUDEPOINT,localPoint.latitudePoint)
        cv.put(COL_LONGITUDEPOINT,localPoint.longitudePoint)

        var result = db.update(TABLE_NAME,cv, "$COL_ID=?",arrayOf(localPoint.id.toString()))
        if(result == (-1).toInt()){
            Toast.makeText(context, "Falhou!",Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(context, "Local editado com sucesso!",Toast.LENGTH_SHORT).show()
        }
        db.close()
    }

    //Deletar um Local cadastrado no Banco atraves do seu ID
    fun deleteID(id: String): Boolean {
        val db = this.writableDatabase
        Log.d("teste", arrayOf(id).toString())
        return db.delete(
            TABLE_NAME,
            "$COL_ID=?",
            arrayOf(id)
        ) > 0
    }

}