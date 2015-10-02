package gpm.udacity.popularmovies;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import gpm.udacity.popularmovies.model.Movie;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    private static final String LOG_TAG = DetailActivityFragment.class.getName();

    private Bundle mMovieBundle;
    private Movie mMovie;

    public DetailActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Intent intent = getActivity().getIntent();
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        if(intent != null && intent.hasExtra("MOVIE"))
        {
            mMovieBundle = intent.getBundleExtra("MOVIE");
            mMovie = new Movie(mMovieBundle);
            ((TextView) rootView.findViewById(R.id.detail_title)).setText(mMovie.title);
            ((TextView) rootView.findViewById(R.id.detail_rating)).setText(mMovie.getRating());
            ((TextView) rootView.findViewById(R.id.detail_release)).setText(mMovie.release_date);
            ((TextView) rootView.findViewById(R.id.detail_plot)).setText(mMovie.overview);
            ImageView poster = ((ImageView) rootView.findViewById(R.id.detail_poster));


            Glide.with(getActivity())
                    .load(mMovie.buildPosterUri("w185"))
                    .into(poster);
        }
        return rootView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    }

}

