package gpm.udacity.popularmovies.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import gpm.udacity.popularmovies.R;
import gpm.udacity.popularmovies.model.Trailer;

/**
 * Created by gmi on 19/08/15.
 */
public class TrailerGridViewAdapter extends BaseAdapter {
    private final Context context;
    private final ArrayList<Trailer> Trailers;
    private final int mHeight;
    private final int mWidth;

    public TrailerGridViewAdapter(Context context) {
        this.context = context;
        this.Trailers = new ArrayList<Trailer>();
        mHeight = Math.round(context.getResources().getDimension(R.dimen.trailer_height));
        mWidth = Math.round(context.getResources().getDimension(R.dimen.trailer_width));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View gridView;
        ImageView imageView;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            // if it's not recycled, initialize some attributes
            gridView = new ImageView(context);
            gridView = inflater.inflate(R.layout.grid_movie_item, null);
            gridView.setLayoutParams(new GridView.LayoutParams(mWidth, mHeight));
            imageView = (ImageView)gridView.findViewById(R.id.grid_movie_item_image);

            //imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        String videoUrl = "http://img.youtube.com/vi/"+Trailers.get(position).getPath()+"/default.jpg";
        Glide.with(context)
                .load(videoUrl)
                .into(imageView);

        return gridView;
    }


    public void addAll(ArrayList<Trailer> trailers) {
        Trailers.addAll(trailers);
        notifyDataSetChanged();
    }

    public void clear() {
        Trailers.clear();
    }

    @Override
    public int getCount() {
        return this.Trailers.size();
    }

    @Override public String getItem(int position) {
        return this.Trailers.get(position).getPath();
    }

    public Trailer getTrailer(int position) {
        return this.Trailers.get(position);

    }

    public ArrayList<Trailer> getTrailers() {
        return this.Trailers;

    }

    @Override
    public long getItemId(int i) {
        return this.Trailers.indexOf(i);
    }
}


