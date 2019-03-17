package com.example.projetmobile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projetmobile.Interfaces.OnGetGenresCallback;
import com.example.projetmobile.Interfaces.OnGetMoviesCallback;
import com.example.projetmobile.Interfaces.OnMoviesClickCallback;
import com.example.projetmobile.Models.Genre;
import com.example.projetmobile.Models.Movie;
import com.example.projetmobile.Models.MoviesAdapter;
import com.example.projetmobile.Models.MoviesRepository;

import java.util.List;



public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;

    private RecyclerView moviesList;
    private MoviesAdapter adapter;

    private MoviesRepository moviesRepository;

    private List<Genre> movieGenres;

    private boolean isFetchingMovies;
    private int currentPage = 1;

    private String sortBy = MoviesRepository.POPULAR;

    private EditText searchEdit;

    private Button buttonClear;

    private MenuItem nav_login;

    private MenuItem nav_favs;

    private MenuItem nav_settings;

    private LayoutAnimationController controller;

    private TextView textView3;

    private SwipeRefreshLayout swipeRefresh;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nav_login = findViewById(R.id.nav_login);

        nav_favs = findViewById(R.id.nav_favs);

        nav_settings = findViewById(R.id.nav_settings);

        drawerLayout = findViewById(R.id.drawer_layout);

        moviesRepository = MoviesRepository.getInstance();

        moviesList = findViewById(R.id.movies_list);
        Context recyContext = moviesList.getContext();
        controller = AnimationUtils.loadLayoutAnimation(recyContext, R.anim.layout_fall_down);

        searchEdit = findViewById(R.id.searchEdit);

        buttonClear = findViewById(R.id.buttonClear);

        textView3 = findViewById(R.id.textView3);

        swipeRefresh = findViewById(R.id.swipeRefreshLayout);

        setupOnScrollListener();

        getGenres();

        sharedPreferences = getSharedPreferences("MOVIES",MODE_PRIVATE);


        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        drawerLayout.closeDrawers();
                        Intent intent;
                        switch (menuItem.getItemId()) {
                            case R.id.nav_settings:
                                intent = new Intent(MainActivity.this, SettingsActivity.class);
                                startActivity(intent);
                                return true;
                            case R.id.nav_login:
                                intent = new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(intent);
                                return true;
                            case R.id.nav_favs:
                                intent = new Intent(MainActivity.this, FavoritesActivity.class);
                                startActivity(intent);
                                return true;
                            default:
                                return false;
                        }
                    }
                });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);


        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()!=0)
                {
                    getSearchRes(s.toString());
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        buttonClear.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                searchEdit.setText("");
                getMovies(1);
            }
        });

        if(moviesRepository.languageEN)
        {
            textView3.setVisibility(View.INVISIBLE);
        }

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter = null;

                getMovies(1);
                swipeRefresh.setRefreshing(false);
            }
        });
    }

    private void RunAnimation(RecyclerView recyclerView, List<Movie> movies)
    {
        Context context = recyclerView.getContext();
        controller = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_fall_down);

        adapter = new MoviesAdapter(movies, movieGenres, callback);
        moviesList.setAdapter(adapter);
        moviesList.setLayoutAnimation(controller);
        moviesList.getAdapter().notifyDataSetChanged();
        moviesList.scheduleLayoutAnimation();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sort_movies, menu);
        return super.onCreateOptionsMenu(menu);
    }


    private void showSortMenu() {
        PopupMenu sortMenu = new PopupMenu(this, findViewById(R.id.sort));
        sortMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                /*
                 * Every time we sort, we need to go back to page 1
                 */
                currentPage = 1;

                switch (item.getItemId()) {
                    case R.id.popular:
                        sortBy = MoviesRepository.POPULAR;
                        getMovies(currentPage);
                        return true;
                    case R.id.top_rated:
                        sortBy = MoviesRepository.TOP_RATED;
                        getMovies(currentPage);
                        return true;
                    case R.id.upcoming:
                        sortBy = MoviesRepository.UPCOMING;
                        getMovies(currentPage);
                        return true;
                    default:
                        return false;
                }
            }
        });
        sortMenu.inflate(R.menu.menu_movies_sort);
        sortMenu.show();
    }

    OnMoviesClickCallback callback = new OnMoviesClickCallback() {
        @Override
        public void onClick(Movie movie) {
            Intent intent = new Intent(MainActivity.this, MovieActivity.class);
            intent.putExtra(MovieActivity.MOVIE_ID, movie.getId());
            startActivity(intent);
        }
    };

    private void setupOnScrollListener() {
        final LinearLayoutManager manager = new LinearLayoutManager(this);
        moviesList.setLayoutManager(manager);

        moviesList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int totalItemCount = manager.getItemCount();
                int visibleItemCount = manager.getChildCount();
                int firstVisibleItem = manager.findFirstVisibleItemPosition();

                if (firstVisibleItem + visibleItemCount >= totalItemCount / 2) {
                    if (!isFetchingMovies) {
                        getMovies(currentPage + 1);
                    }
                }
            }
        });
    }

    public void onClickLanguage(View v) {
            moviesRepository.changeLanguage();
            getGenres();
        if(moviesRepository.languageEN)
        {
            textView3.setVisibility(View.INVISIBLE);
        }
    }

    private void getGenres() {
        moviesRepository.getGenres(new OnGetGenresCallback() {
            @Override
            public void onSuccess(List<Genre> genres) {
                movieGenres = genres;
                getMovies(currentPage);
            }

            @Override
            public void onError() {
                showError();
            }
        });
    }

    private void getMovies(int page) {
        isFetchingMovies = true;
        moviesRepository.getMovies(page, sortBy, new OnGetMoviesCallback() {
            @Override
            public void onSuccess(int page, List<Movie> movies) {
                Log.d("MoviesRepository", "Current Page = " + page);
                if (adapter == null) {
                    RunAnimation(moviesList, movies);
                } else {
                    if (page == 1) {
                        adapter.clearMovies();
                    }
                    adapter.appendMovies(movies);
                }
                currentPage = page;
                isFetchingMovies = false;

            }

            @Override
            public void onError() {
                showError();
            }
        });
    }


    private void showError() {
        Toast.makeText(MainActivity.this, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
    }

    private void getSearchRes(String query){
        isFetchingMovies = true;
        moviesRepository.getSearchResults(query, new OnGetMoviesCallback(){
            @Override
            public void onSuccess(int page, List<Movie> movies){
                Log.d("MoviesRepository", "Current Page = " + page);
                if (adapter == null) {
                    adapter = new MoviesAdapter(movies, movieGenres, callback);
                    moviesList.setAdapter(adapter);
                } else {
                    if (page == 1) {
                        adapter.clearMovies();
                    }
                    adapter.appendMovies(movies);
                }
                currentPage = page;
                isFetchingMovies = false;
            }

            @Override
            public void onError() {
                showError();
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.sort:
                showSortMenu();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
