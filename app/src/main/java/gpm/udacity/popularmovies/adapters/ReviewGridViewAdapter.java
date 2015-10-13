package gpm.udacity.popularmovies.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import gpm.udacity.popularmovies.R;
import gpm.udacity.popularmovies.model.Review;

/**
 * Created by gmi on 07/10/15.
 */
public class ReviewGridViewAdapter extends BaseAdapter {
    private final Context context;
    private final ArrayList<Review> Reviews;

    public ReviewGridViewAdapter(Context context) {
        this.context = context;
        this.Reviews = new ArrayList<Review>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;
        Log.w("COMMENT", String.valueOf(position));
        if (convertView == null) {
            //gridView = new View(context);
            // get layout from mobile.xml
            gridView = inflater.inflate(R.layout.item, null);
            TextView comment = (TextView) gridView.findViewById(R.id.item_review);
            TextView user = (TextView) gridView.findViewById(R.id.item_user);
            comment.setText(this.Reviews.get(position).comment);
            user.setText(this.Reviews.get(position).user);

        } else {
            gridView = (View) convertView;
        }

        return gridView;
    }


    public void addAll(ArrayList<Review> reviews) {
        Reviews.addAll(reviews);
        notifyDataSetChanged();
    }

    public void clear() {
        Reviews.clear();
    }

    @Override
    public int getCount() {
        return this.Reviews.size();
    }

    @Override public String getItem(int position) {
        return this.Reviews.get(position).getComment();
    }

    public Review getReview(int position) {
        return this.Reviews.get(position);

    }
    public ArrayList<Review> getReviews() {
        return this.Reviews;

    }
    @Override
    public long getItemId(int i) {
        return this.Reviews.indexOf(i);
    }
}


