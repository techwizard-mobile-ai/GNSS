package com.lysaan.malikab.addgroceries.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.widget.Toast


val DATABASE_NAME ="MyDB"
val TABLE_CATEGORY="Category"
val COL_CATEGORY_NAME = "category_name"
val COL_ID = "id"

class DatabaseAdapter(var context: Context) : SQLiteOpenHelper(context,DATABASE_NAME,null,1){

    val TAG = "DatabaseAdapter"
    override fun onCreate(db: SQLiteDatabase?) {

        val createCategoryTable = "CREATE TABLE " + TABLE_CATEGORY +" (" +
                COL_ID +" INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_CATEGORY_NAME + " VARCHAR(256))"
        db?.execSQL(createCategoryTable)

    }

    override fun onUpgrade(db: SQLiteDatabase?,oldVersion: Int,newVersion: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
//
//    fun insertCategory(category: Category){
//        val db = this.writableDatabase
//        var cv = ContentValues()
//        cv.put(COL_CATEGORY_NAME,category.name)
//        var result = db.insert(TABLE_CATEGORY,null,cv)
//        if(result == -1.toLong()){
//            Toast.makeText(context,"Category ${category.name} Not Inserted.",Toast.LENGTH_SHORT)
//                    .show()
//        }else{
//            Toast.makeText(context,"Category ${category.name} Saved Successfully.",Toast
//                    .LENGTH_SHORT)
//                    .show()
//        }
//    }
//    fun getAllCategories() : MutableList<Category>{
//        var list : MutableList<Category> = ArrayList()
//
//        val db = this.readableDatabase
//        val query = "Select * from " + TABLE_CATEGORY
//        val result = db.rawQuery(query,null)
//        if(result.moveToFirst()){
//            do {
//                var user = Category()
//                user.id = result.getString(result.getColumnIndex(COL_ID)).toInt()
//                user.name = result.getString(result.getColumnIndex(COL_CATEGORY_NAME))
//                list.add(user)
//            }while (result.moveToNext())
//        }
//
//        result.close()
//        db.close()
//        return list
//    }
//    fun deleteCategoryById(id:Int): Int {
//        val db = this.writableDatabase
//
//        var affectedRow = db.delete(TABLE_CATEGORY, COL_ID + " = " + id, null)
//        Log.e("DeletedCategory:",affectedRow.toString())
//        db.close()
//
//        var productsDeleted = deleteProductsByCategoryId(id)
//        Log.e("DeletedProducts:",productsDeleted.toString())
//        return affectedRow
//    }

}