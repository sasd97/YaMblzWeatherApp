package me.grechka.yamblz.yamblzweatherapp.presentation.main;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;

import butterknife.BindColor;
import butterknife.BindView;
import me.grechka.yamblz.yamblzweatherapp.events.OnDrawerLocked;
import me.grechka.yamblz.yamblzweatherapp.events.OnErrorListener;
import me.grechka.yamblz.yamblzweatherapp.models.City;
import me.grechka.yamblz.yamblzweatherapp.presentation.AboutFragment;
import me.grechka.yamblz.yamblzweatherapp.R;
import me.grechka.yamblz.yamblzweatherapp.WeatherApp;
import me.grechka.yamblz.yamblzweatherapp.presentation.base.AdaptiveActivity;
import me.grechka.yamblz.yamblzweatherapp.presentation.favorites.FavoritesFragment;
import me.grechka.yamblz.yamblzweatherapp.presentation.settings.SettingsFragment;
import me.grechka.yamblz.yamblzweatherapp.presentation.weather.WeatherFragment;

public class MainActivity extends AdaptiveActivity
        implements MainView, OnDrawerLocked,
        OnErrorListener,
        NavigationView.OnNavigationItemSelectedListener {

    @InjectPresenter MainPresenter presenter;

    @BindColor(R.color.colorWhite) int colorWhite;
    @BindView(R.id.main_activity_navigation_view) NavigationView navigationView;
    @Nullable @BindView(R.id.extend_fragment_container) View extendedFragmentContainer;
    @Nullable @BindView(R.id.main_activity_drawer_layout) DrawerLayout drawerLayout;

    @Nullable private TextView cityTitleHeaderTextView;
    @Nullable private TextView cityAreaHeaderTextView;
    private boolean isDrawerHidden = false;
    private ActionBarDrawerToggle toggle;

    @ProvidePresenter
    public MainPresenter providePresenter() {
        return WeatherApp
                .get(this)
                .getAppComponent()
                .addMainComponent()
                .getMainPresenter();
    }

    @Override
    protected int obtainLayoutView() {
        return R.layout.activity_main;
    }

    @Override
    protected int obtainAdaptationMode() {
        return extendedFragmentContainer == null ? PHONE : TABLET;
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        setTheme(R.style.AppTheme_NoActionBar);
    }

    @Override
    protected void onPhoneInit() {
        super.onPhoneInit();
        showWeather();
    }

    @Override
    protected void onTabletInit() {
        super.onTabletInit();
        navigateToFragment(new FavoritesFragment(), R.id.fragment_container, false);
        navigateToFragment(new WeatherFragment(), R.id.extend_fragment_container, true);
    }

    @Override
    protected void onDeviceViewsCreated(@Nullable Bundle savedInstanceState) {
        super.onDeviceViewsCreated(savedInstanceState);
        setSupportActionBar(R.id.toolbar);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onPhoneViewsCreated(@Nullable Bundle savedInstanceState) {
        super.onPhoneViewsCreated(savedInstanceState);

        toggle = new ActionBarDrawerToggle(this, drawerLayout,
                getToolbar(), R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        View navigationHeaderView = navigationView.getHeaderView(0);
        onHeaderInit(navigationHeaderView);
    }

    private void onHeaderInit(@Nullable View headerView) {
        if (headerView  == null) return;

        View currentCityCardView = headerView.findViewById(R.id.main_activity_choose_city);
        cityAreaHeaderTextView = headerView.findViewById(R.id.fragment_weather_header_city_area);
        cityTitleHeaderTextView = headerView.findViewById(R.id.fragment_weather_header_city_title);

        currentCityCardView.setOnClickListener(v -> showFavorites());
    }

    @Override
    public void onMissingCity() {
        presenter.showMissedCity();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        presenter.navigate(item.getItemId());
        closeDrawer();
        return true;
    }

    @Override
    public void showWeather() {
        navigateToFragment(new WeatherFragment(), R.id.fragment_container, true);
    }

    @Override
    public void showSettings() {
        navigateToFragment(new SettingsFragment(), R.id.fragment_container, true);
    }

    @Override
    public void showAbout() {
        navigateToFragment(new AboutFragment(), R.id.fragment_container, true);
    }

    @Override
    public void showFavorites() {
        navigateToFragment(new FavoritesFragment(), R.id.fragment_container, true);
        closeDrawer();
    }

    @Override
    public void onCityMissedError() {
        showFavorites();
    }

    @Override
    public void navigate(int screenId) {
        if (isDrawerHidden) return;

        switch (screenId) {
            case R.id.nav_favorites:
                presenter.showFavorites();
                break;
            case R.id.nav_settings:
                presenter.showSettings();
                break;
            case R.id.nav_about:
                presenter.showAbout();
                break;
        }
    }

    @Override
    public void setCityToHeader(@NonNull City city) {
        if (!isPhone()) return;

        cityTitleHeaderTextView.setText(city.getTitle());
        cityAreaHeaderTextView.setText(city.getExtendedTitle());
    }

    @Override
    public void selectBurgerButtonNavigation() {
        isDrawerHidden = false;
        if (!isPhone()) return;

        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.setToolbarNavigationClickListener(null);
    }

    @Override
    public void selectBackButtonNavigation() {
        isDrawerHidden = false;
        if (!isPhone()) return;

        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        toggle.setDrawerIndicatorEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toggle.setToolbarNavigationClickListener(v -> goBack());
    }

    private boolean closeDrawer() {
        if (!isPhone()) return false;

        if (!drawerLayout.isDrawerOpen(GravityCompat.START)) return false;
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void hideDrawer() {
        isDrawerHidden = true;
        if (!isPhone()) return;

        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        toggle.setDrawerIndicatorEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toggle.setToolbarNavigationClickListener(null);
    }

    @Override
    public boolean onSupportNavigateUp() {
        goBack();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (isDrawerHidden) {
            finish();
            return;
        }

        presenter.goBack();
    }

    @Override
    public void goBack() {
        if (!closeDrawer()) super.onBackPressed();
    }

    private void navigateToFragment(@NonNull Fragment fragment,
                                    @IdRes int layoutRes,
                                    boolean isIncludedToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(layoutRes, fragment);

        if (isIncludedToBackStack) transaction.addToBackStack(null);

        transaction.commit();
    }
}
