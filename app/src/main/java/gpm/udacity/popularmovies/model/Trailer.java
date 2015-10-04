package gpm.udacity.popularmovies.model;

import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by gmi on 04/10/15.
 */
public class Trailer implements Parcelable {

    public String id;
    public String title;
    public String path;
    public String site;
    public String type;

    public Trailer(String id,
                 String title, String path, String site, String type) {
        this.id = id;
        this.title = title;
        this.path = path;
        this.site = site;
        this.type = type;
    }

    private Trailer(Parcel in) {
        id = in.readString();
        title = in.readString();
        path = in.readString();
        site = in.readString();
        type = in.readString();
    }

    public Uri buildTrailerUri() {
        String BASE_IMAGE_URL = "http://www.youtube.com/watch?v=" + path;

        Uri builtUri = Uri.parse(BASE_IMAGE_URL).buildUpon()
                .build();

        return builtUri;
    }

    public Trailer(Bundle bundle) {
        this(
                bundle.getString("ID"),
                bundle.getString("TITLE"),
                bundle.getString("PATH"),
                bundle.getString("SITE"),
                bundle.getString("TYPE")
        );
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();

        bundle.putString("ID", id);
        bundle.putString("TITLE", title);
        bundle.putString("PATH", path);
        bundle.putString("SITE", site);
        bundle.putString("TYPE", type);


        return bundle;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String uri)
    {
        this.path = uri;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(title);
        parcel.writeString(path);
        parcel.writeString(site);
        parcel.writeString(type);

    }

    public static final Parcelable.Creator<Trailer> CREATOR = new Parcelable.Creator<Trailer>() {
        public Trailer createFromParcel(Parcel in) {
            return new Trailer(in);
        }

        public Trailer[] newArray(int size) {
            return new Trailer[size];
        }
    };
}
