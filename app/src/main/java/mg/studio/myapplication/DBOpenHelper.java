package mg.studio.myapplication;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

/**
 * @programName: DBOpenHelper.java
 * @programFunction: database helper class
 * @createDate: 2018/09/29
 * @author: AnneHan
 * @version:
 * xx.   yyyy/mm/dd   ver    author    comments
 * 01.   2018/09/29   1.00   AnneHan   New Create
 */
public class DBOpenHelper extends SQLiteOpenHelper {
    public DBOpenHelper(Context context,String name, CursorFactory factory,
                        int version){
        super(context, name, factory, version);
    }

    @Override
    //used at the first time.
    public void onCreate(SQLiteDatabase db){
        //user table
        db.execSQL("create table if not exists user_tb(_id integer primary key autoincrement," +
                "userID text not null," +
                "pwd text not null," +
                "userName text not null)");
    }

    @Override//当数据库的版本发生变化时，会自动执行
    public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){

    }
}