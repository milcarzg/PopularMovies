package gpm.udacity.popularmovies.model;

import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by gmi on 19/08/15.
 */
public class Movie implements Parcelable {

    public long id;
    public String title;
    public String overview;
    public String poster_path;
    public double vote_average;
    public String release_date;

    public Movie(long id,
                 String title, String overview, String poster_path,
                 double vote_average, String release_date) {
        this.id = id;
        this.title = title;
        this.overview = overview;
        this.poster_path = poster_path;
        this.vote_average = vote_average;
        this.release_date = release_date;
    }

    private Movie(Parcel in) {
        id = in.readLong();
        title = in.readString();
        overview = in.readString();
        poster_path = in.readString();
        vote_average = in.readDouble();
        release_date = in.readString();
    }

    public Uri buildPosterUri(String size) {
        final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";

        Uri builtUri = Uri.parse(BASE_IMAGE_URL).buildUpon()
                .appendPath(size)
                .appendEncodedPath(poster_path)
                .build();

        return builtUri;
    }

    public Movie(Bundle bundle) {
        this(
                bundle.getLong("ID"),
                bundle.getString("TITLE"),
                bundle.getString("OVERVIEW"),
                bundle.getString("POSTER"),
                bundle.getDouble("VOTE"),
                bundle.getString("RELEASE")
        );
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();

        bundle.putLong("ID", id);
        bundle.putString("TITLE", title);
        bundle.putString("OVERVIEW", overview);
        bundle.putString("POSTER", poster_path);
        bundle.putDouble("VOTE", vote_average);
        bundle.putString("RELEASE", release_date);


        return bundle;
    }

    public String getRating() {
        return vote_average + "/ 10";
    }

    public void setRating(double vote)
    {
        this.vote_average = vote;
    }

    public String getPoster() {
        return poster_path;
    }

    public void setPoster(String uri)
    {
        this.poster_path = uri;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(title);
        parcel.writeString(overview);
        parcel.writeString(poster_path);
        parcel.writeDouble(vote_average);
        parcel.writeString(release_date);

    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
