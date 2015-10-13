package gpm.udacity.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import gpm.udacity.popularmovies.adapters.ReviewGridViewAdapter;
import gpm.udacity.popularmovies.adapters.TrailerGridViewAdapter;
import gpm.udacity.popularmovies.helpers.ExpandableGridView;
import gpm.udacity.popularmovies.model.Movie;
import gpm.udacity.popularmovies.model.Review;
import gpm.udacity.popularmovies.model.Trailer;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    private static final String LOG_TAG = DetailActivityFragment.class.getName();

    private Bundle mMovieBundle;
    private View rootView;
    private Movie mMovie;
    private TrailerGridViewAdapter mTrailerAdapter;
    private ReviewGridViewAdapter mReviewAdapter;

    private ArrayList<Trailer> savedTrailers = new ArrayList<Trailer>();
    private ArrayList<Review> savedReviews = new ArrayList<Review>();

    private TextView title;
    private TextView rating;
    private TextView release;
    private TextView plot;
    private ImageView poster;


    public DetailActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Intent intent = getActivity().getIntent();
        rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        if(intent != null && intent.hasExtra("MOVIE"))
        {
            mTrailerAdapter = new TrailerGridViewAdapter(this.getActivity());
            ExpandableGridView gridview = (ExpandableGridView) rootView.findViewById(R.id.gridview_trailers);
            gridview.setExpanded(true);
            gridview.setAdapter(mTrailerAdapter);

            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Trailer trailer = mTrailerAdapter.getTrailer(i);
                    Intent intent = new Intent(Intent.ACTION_VIEW, trailer.buildTrailerUri());
                    Log.w("URL YOTUBE", trailer.buildTrailerUri().toString());
                    startActivity(intent);
                }
            });
            mReviewAdapter = new ReviewGridViewAdapter(this.getActivity());
            ExpandableGridView gridview1 = (ExpandableGridView) rootView.findViewById(R.id.gridview_reviews);
            gridview1.setExpanded(true);
            gridview1.setAdapter(mReviewAdapter);

            mMovieBundle = intent.getBundleExtra("MOVIE");
            mMovie = new Movie(mMovieBundle);

            this.title = ((TextView) rootView.findViewById(R.id.detail_title));
            this.title.setText(mMovie.title);
            this.rating = ((TextView) rootView.findViewById(R.id.detail_rating));
            this.rating.setText(mMovie.getRating());
            this.release = ((TextView) rootView.findViewById(R.id.detail_release));
            this.release.setText(mMovie.release_date);
            this.plot = ((TextView) rootView.findViewById(R.id.detail_plot));
            this.plot.setText(mMovie.overview);
            this.poster = ((ImageView) rootView.findViewById(R.id.detail_poster));

            mTrailerAdapter.clear();
            loadTrailers(mMovie.id);
            loadReviews(mMovie.id);

            Glide.with(getActivity())
                    .load(mMovie.buildPosterUri("w185"))
                    .into(poster);

        }
        return rootView;
    }

    public void loadTrailers(long movieId)
    {
        new GetTrailersTask().execute(movieId);
    }

    public void loadReviews(long movieId)
    {
        new GetReviewsTask().execute(movieId);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    }

    public void updateContent(Movie movie)
    {
        this.title = ((TextView) rootView.findViewById(R.id.detail_title));
        this.title.setText(movie.title);
        this.rating = ((TextView) rootView.findViewById(R.id.detail_rating));
        this.rating.setText(movie.getRating());
        this.release = ((TextView) rootView.findViewById(R.id.detail_release));
        this.release.setText(movie.release_date);
        this.plot = ((TextView) rootView.findViewById(R.id.detail_plot));
        this.plot.setText(movie.overview);
        mTrailerAdapter = new TrailerGridViewAdapter(this.getActivity());
        ExpandableGridView gridview = (ExpandableGridView) rootView.findViewById(R.id.gridview_trailers);
        gridview.setExpanded(true);
        gridview.setAdapter(mTrailerAdapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Trailer trailer = mTrailerAdapter.getTrailer(i);
                Intent intent = new Intent(Intent.ACTION_VIEW, trailer.buildTrailerUri());
                Log.w("URL YOTUBE",trailer.buildTrailerUri().toString());
                startActivity(intent);
            }
        });
        mReviewAdapter = new ReviewGridViewAdapter(this.getActivity());
        ExpandableGridView gridview1 = (ExpandableGridView) rootView.findViewById(R.id.gridview_reviews);
        gridview1.setExpanded(true);
        gridview1.setAdapter(mReviewAdapter);
        mTrailerAdapter.clear();
        mReviewAdapter.clear();
        savedTrailers.clear();
        savedReviews.clear();
        loadTrailers(movie.id);
        loadReviews(movie.id);

        this.poster = ((ImageView) rootView.findViewById(R.id.detail_poster));

        Glide.with(getActivity())
                .load(movie.buildPosterUri("w185"))
                .into(poster);
    }


    public class GetTrailersTask extends AsyncTask<Long ,Void, ArrayList<Trailer>> {

        private final String LOG_TAG = GetTrailersTask.class.getSimpleName();

        ArrayList<Trailer> trailers = null;

        @Override
        protected ArrayList<Trailer> doInBackground(Long... params) {

            if(params.length == 0){
                return null;
            }
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            Log.w("Trailers", "works");

            // Will contain the raw JSON response as a string.
            String trailersJsonStr = null;

            try {
                final String FORECAST_BASE_URL = "http://api.themoviedb.org/3/movie/{id}/videos";
                final String API_PARAM = "api_key";
                String TRAILER_URL = FORECAST_BASE_URL.replace("{id}",String.valueOf(params[0]));

                Log.w("TRAILER URL", TRAILER_URL);

                Uri builtUri = Uri.parse(TRAILER_URL).buildUpon()
                        .appendQueryParameter(API_PARAM, getString(R.string.movie_db_key))
                        .build();
                URL url = new URL(builtUri.toString());

                //Log.w(LOG_TAG, "BuiltUri URI " + builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                trailersJsonStr = buffer.toString();

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                trailers = getTrailersDataFromJson(trailersJsonStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return trailers;
        }

        protected void onPostExecute(ArrayList<Trailer> trailers) {
            if (trailers != null)
            {
                mTrailerAdapter.addAll(trailers);
                savedTrailers.addAll(trailers);
            }

        }

        private ArrayList<Trailer> getTrailersDataFromJson(String trailersJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String ID = "id";
            final String TRAILERS_RESULT = "results";
            final String PATH = "key";
            final String TITLE = "name";
            final String SITE = "site";
            final String TYPE = "type";

            JSONObject trailersJson = new JSONObject(trailersJsonStr);
            JSONArray trailersArray = trailersJson.getJSONArray(TRAILERS_RESULT);
            ArrayList<Trailer> loadedTrailers = new ArrayList<Trailer>();

            ArrayList<String> resultStrs = new ArrayList<String>();
            for(int i = 0; i < trailersArray.length(); i++) {
                JSONObject trailer = trailersArray.getJSONObject(i);
                String id = trailer.getString(ID);
                String path = trailer.getString(PATH);
                String title = trailer.getString(TITLE);
                String site  = trailer.getString(SITE);
                String type = trailer.getString(TYPE);

                Trailer t = new Trailer(id,title,path,site,type);
                loadedTrailers.add(t);

                resultStrs.add(path);
            }
            for (String s : resultStrs) {
                Log.v(LOG_TAG, "trailer paths: " + s);
            }
            return loadedTrailers;
        }
    }


    public class GetReviewsTask extends AsyncTask<Long ,Void, ArrayList<Review>> {

        private final String LOG_TAG = GetReviewsTask.class.getSimpleName();

        ArrayList<Review> Reviews = null;

        @Override
        protected ArrayList<Review> doInBackground(Long... params) {

            if(params.length == 0){
                return null;
            }
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            Log.w("Reviews", "works");

            // Will contain the raw JSON response as a string.
            String reviewsJsonStr = null;

            try {
                final String FORECAST_BASE_URL = "http://api.themoviedb.org/3/movie/{id}/reviews";
                final String API_PARAM = "api_key";
                String REVIEW_URL = FORECAST_BASE_URL.replace("{id}",String.valueOf(params[0]));

                Log.w("Review URL", REVIEW_URL);

                Uri builtUri = Uri.parse(REVIEW_URL).buildUpon()
                        .appendQueryParameter(API_PARAM, getString(R.string.movie_db_key))
                        .build();
                URL url = new URL(builtUri.toString());

                //Log.w(LOG_TAG, "BuiltUri URI " + builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                reviewsJsonStr = buffer.toString();

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                Reviews = getReviewsDataFromJson(reviewsJsonStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return Reviews;
        }

        protected void onPostExecute(ArrayList<Review> reviews) {
            if (Reviews != null)
            {
                mReviewAdapter.addAll(reviews);
                savedReviews.addAll(reviews);
            }

        }

        private ArrayList<Review> getReviewsDataFromJson(String reviewsJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String ID = "id";
            final String REVIEWS_RESULT = "results";
            final String USER = "author";
            final String COMMENT = "content";

            JSONObject reviewsJson = new JSONObject(reviewsJsonStr);
            JSONArray reviewsArray = reviewsJson.getJSONArray(REVIEWS_RESULT);
            ArrayList<Review> loadedReviews = new ArrayList<Review>();

            ArrayList<String> resultStrs = new ArrayList<String>();
            for(int i = 0; i < reviewsArray.length(); i++) {
                JSONObject Review = reviewsArray.getJSONObject(i);
                String id = Review.getString(ID);
                String user = Review.getString(USER);
                String comment = Review.getString(COMMENT);

                Review m = new Review(id,user,comment);

                loadedReviews.add(m);
                resultStrs.add(comment);
            }
            for (String s : resultStrs) {
                Log.v(LOG_TAG, "Review paths: " + s);
            }
            return loadedReviews;
        }
    }
}

