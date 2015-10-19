package gpm.udacity.popularmovies.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by gmi on 14/10/15.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;
    private static final String DB_TABLE_NAME = "FAVOURITES";
    private static final String DB_TABLE_CREATE =
            "CREATE TABLE FAVOURITES(_ID INT PRIMARY KEY, id, title, vote, release_date, overview, poster);";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DB_TABLE_NAME;

    DatabaseHelper(Context context, int version) {
        super(context, "favourite.db", null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DB_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
