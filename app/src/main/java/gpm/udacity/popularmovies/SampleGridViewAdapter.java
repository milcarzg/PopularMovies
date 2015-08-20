package gpm.udacity.popularmovies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import gpm.udacity.popularmovies.model.Movie;

/**
 * Created by gmi on 19/08/15.
 */
public class SampleGridViewAdapter extends BaseAdapter {
    private final Context context;
    private final ArrayList<Movie> Posters;
    private final int mHeight;
    private final int mWidth;

    public SampleGridViewAdapter(Context context) {
        this.context = context;
        this.Posters = new ArrayList<Movie>();
        mHeight = Math.round(context.getResources().getDimension(R.dimen.poster_height));
        mWidth = Math.round(context.getResources().getDimension(R.dimen.poster_width));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Log.v("ADAPTER", "ADAPTER");
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(mWidth, mHeight));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        } else {
            imageView = (ImageView) convertView;
        }

        String url = context.getString(R.string.BASE_IMAGE_URL) + context.getString(R.string.BASE_IMAGE_SIZE) + getItem(position);
        //Log.v("ADAPTER", url);
        Glide.with(context)
                .load(url)
                .into(imageView);

        return imageView;
    }


    public void addAll(ArrayList<Movie> posters) {
        Posters.addAll(posters);
        notifyDataSetChanged();
    }

    public void clear() {
        Posters.clear();
    }

    @Override
    public int getCount() {
        return this.Posters.size();
    }

    @Override public String getItem(int position) {
        return this.Posters.get(position).getPoster();
    }

    public Movie getMovie(int position) {
        return this.Posters.get(position);

    }

    @Override
    public long getItemId(int i) {
        return this.Posters.indexOf(i);
    }
}


