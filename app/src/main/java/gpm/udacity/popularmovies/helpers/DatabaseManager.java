package gpm.udacity.popularmovies.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import gpm.udacity.popularmovies.model.Movie;

/**
 * Created by gmi on 14/10/15.
 */
public class DatabaseManager {
    private static DatabaseManager manager;
    private DatabaseHelper dbHelper;


    private DatabaseManager(Context context) {
        dbHelper = new DatabaseHelper(context, 1);
    }

    public static DatabaseManager create(Context context) {
        if (manager == null) {
            manager = new DatabaseManager(context);
        }
        return manager;
    }


    public void add(Movie movie) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", movie.id);
        values.put("title", movie.title);
        values.put("vote", movie.vote_average);
        values.put("release_date", movie.release_date);
        values.put("overview", movie.overview);
        values.put("poster", movie.getPoster());

        database.insert("FAVOURITES", null, values);
        database.close();
    }


    public void remove(Movie movie) {
        Log.d("zosta", "Movie to be delted: " + movie);
        Log.d("zosta", "Movie id: " + String.valueOf(movie.id));

        SQLiteDatabase database = dbHelper.getWritableDatabase();
        int result = database.delete("FAVOURITES", "id = " + movie.id, null);
        Log.d("zosta", "deleting data result: " + result);
        database.close();
    }

    public ArrayList<Movie> getMovies() {
        ArrayList<Movie> movies = new ArrayList<>();
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor cursor = database.query("FAVOURITES", null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            Movie movie = new Movie(
                    cursor.getInt(cursor.getColumnIndex("id")),
                    cursor.getString(cursor.getColumnIndex("title")),
                    cursor.getString(cursor.getColumnIndex("overview")),
                    cursor.getString(cursor.getColumnIndex("poster")),
                    cursor.getDouble(cursor.getColumnIndex("vote")),
                    cursor.getString(cursor.getColumnIndex("release_date")),
                    true);
            movies.add(movie);
        }
        cursor.close();
        return movies;
    }

}
