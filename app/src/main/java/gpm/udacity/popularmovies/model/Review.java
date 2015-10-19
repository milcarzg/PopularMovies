package gpm.udacity.popularmovies.model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by gmi on 07/10/15.
 */
public class Review implements Parcelable {

    public String id;
    public String user;
    public String comment;

    public Review(String id,
                   String user, String comment) {
        this.id = id;
        this.user = user;
        this.comment = comment;
    }

    private Review(Parcel in) {
        id = in.readString();
        user = in.readString();
        comment = in.readString();
    }

    public Review(Bundle bundle) {
        this(
                bundle.getString("ID"),
                bundle.getString("USER"),
                bundle.getString("COMMENT")
        );
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putString("ID", id);
        bundle.putString("USER", user);
        bundle.putString("COMMENT", comment);
        return bundle;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment)
    {
        this.comment = comment;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(user);
        parcel.writeString(comment);
    }

    public static final Parcelable.Creator<Review> CREATOR = new Parcelable.Creator<Review>() {
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        public Review[] newArray(int size) {
            return new Review[size];
        }
    };
}