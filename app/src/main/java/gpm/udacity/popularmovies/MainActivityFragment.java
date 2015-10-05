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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;


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

import gpm.udacity.popularmovies.adapters.SampleGridViewAdapter;
import gpm.udacity.popularmovies.model.Movie;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private SampleGridViewAdapter mMovieAdapter;
    private View rootView;
    private View detailView;
    private ArrayList<String> mPosters = new ArrayList<String>();
    private ArrayList<Movie> savedMovies = new ArrayList<Movie>();
    private int page = 1;
    private String sort;
    private boolean loadFlag = true;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("MOVIES", savedMovies);
        outState.putString("SORT", sort);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_main, container);
        //detailView = inflater.inflate(R.layout.fragment_detail, container);
        mMovieAdapter = new SampleGridViewAdapter(this.getActivity());
        final GridView gridview = (GridView) rootView.findViewById(R.id.gridview_movies);
        gridview.setAdapter(mMovieAdapter);

        if (savedInstanceState == null || !savedInstanceState.containsKey("MOVIES"))
        {
            sort = getString(R.string.popular);
            mMovieAdapter.clear();
            loadMovies(sort, page);
        }
        else
        {
            savedMovies = savedInstanceState.getParcelableArrayList("MOVIES");
            sort = savedInstanceState.getString("SORT");
            mMovieAdapter.clear();
            mMovieAdapter.addAll(savedMovies);
        }


        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                DetailActivityFragment detailFrag = (DetailActivityFragment) getFragmentManager()
                        .findFragmentById(R.id.fragment_detail);
                Movie movie = mMovieAdapter.getMovie(i);
                if (detailFrag == null) {
                    Intent intent = new Intent(getActivity(), DetailActivity.class).putExtra("MOVIE", movie.toBundle());
                    //Toast.makeText(getActivity(), movie.title, Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                } else {
                    detailFrag.updateContent(movie);
                }
            }
        });
        gridview.setOnScrollListener(
                new AbsListView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {

                    }

                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                        int lastInScreen = firstVisibleItem + visibleItemCount;
                        // - 4 preaload for smooth scrooling
                        if (lastInScreen == totalItemCount - 4) {
                            if (loadFlag == false) {
                                loadFlag = true;
                                loadMovies(sort, ++page);
                            }
                        }
                    }
                }

        );
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    public void loadMovies(String sort, int page)
    {
        String[] params = new String[2];
        params[0] = sort;
        params[1] = String.valueOf(page);
        new GetMoviesTask().execute(params);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sort_popular) {
            //Log.w("POPULAR", "pressed");
            mMovieAdapter.clear();
            savedMovies.clear();
            page = 1;
            sort = getString(R.string.popular);
            loadMovies(sort, page);
            //new GetMoviesTask().execute(getString(R.string.popular));
        }
        if (id == R.id.action_sort_rate) {
            mMovieAdapter.clear();
            savedMovies.clear();
            page = 1;
            sort = getString(R.string.rating);
            loadMovies(sort, page);
            //new GetMoviesTask().execute(getString(R.string.rating));
        }

        return super.onOptionsItemSelected(item);
    }

    public class GetMoviesTask extends AsyncTask<String[] ,Void, ArrayList<Movie>> {

        private final String LOG_TAG = GetMoviesTask.class.getSimpleName();

        ArrayList<Movie> movies = null;

        @Override
        protected ArrayList<Movie> doInBackground(String[]... params) {

            if(params.length == 0){
                return null;
            }
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            Log.w("Started", "works");

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

            try {
                final String FORECAST_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
                final String SORT_PARAM = "sort_by";
                final String PAGE_PARAM = "page";
                final String API_PARAM = "api_key";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_PARAM, String.valueOf(params[0][0]))
                        .appendQueryParameter(PAGE_PARAM, String.valueOf(params[0][1]))
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
                moviesJsonStr = buffer.toString();

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

            //Log.w("JSon", moviesJsonStr);

            try {
                movies = getMoviesDataFromJson(moviesJsonStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return movies;
        }

        protected void onPostExecute(ArrayList<Movie> posters) {
            if (posters != null)
            {
                mMovieAdapter.addAll(posters);
                loadFlag = false;
            }

        }

        private ArrayList<Movie> getMoviesDataFromJson(String moviesJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String ID = "id";
            final String MOVIES_RESULT = "results";
            final String POSTER_PATH = "poster_path";
            final String VOTE_AVERAGE = "vote_average";
            final String TITLE = "title";
            final String OVERVIEW = "overview";
            final String RELEASE = "release_date";

            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray moviesArray = moviesJson.getJSONArray(MOVIES_RESULT);

            ArrayList<String> resultStrs = new ArrayList<String>();
            for(int i = 0; i < moviesArray.length(); i++) {
                JSONObject movie = moviesArray.getJSONObject(i);
                int id = movie.getInt(ID);
                String posterPath = movie.getString(POSTER_PATH);
                String overview = movie.getString(OVERVIEW);
                String title = movie.getString(TITLE);
                String release = movie.getString(RELEASE);
                double vote = movie.getDouble(VOTE_AVERAGE);

                Movie m = new Movie(id,title,overview,posterPath,vote,release);
                savedMovies.add(m);

                resultStrs.add(posterPath);
            }
            for (String s : resultStrs) {
                Log.v(LOG_TAG, "Movie Poster: " + s);
            }
            return savedMovies;
        }
    }
}
