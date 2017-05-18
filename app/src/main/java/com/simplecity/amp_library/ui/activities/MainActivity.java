package com.simplecity.amp_library.ui.activities;

import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.greysonparrelli.permiso.Permiso;
import com.simplecity.amp_library.R;
import com.simplecity.amp_library.ui.fragments.MainController;

import java.util.ArrayList;
import java.util.List;

import test.com.androidnavigation.base.NavigationController;
import test.com.androidnavigation.fragment.BackPressHandler;

public class MainActivity extends BaseCastActivity implements
        ToolbarListener,
        BackPressHandler {

    private static final String TAG = "MainActivity";

    private List<NavigationController> backPressListeners = new ArrayList<>();

    private DrawerLayout drawerLayout;

    private View navigationView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Permiso.getInstance().setActivity(this);

        navigationView = findViewById(R.id.navView);

        //Ensure the drawer draws a content scrim over the status bar.
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setOnApplyWindowInsetsListener((view, windowInsets) -> {
            navigationView.dispatchApplyWindowInsets(windowInsets);
            return windowInsets.replaceSystemWindowInsets(0, 0, 0, 0);
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.mainContainer, MainController.newInstance())
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (!backPressListeners.isEmpty()) {
                for (int i = backPressListeners.size() - 1; i >= 0; i--) {
                    NavigationController backPressListener = backPressListeners.get(i);
                    if (backPressListener.consumeBackPress()) {
                        return;
                    }
                }
            }
            super.onBackPressed();
        }
    }

    @Override
    public void toolbarAttached(Toolbar toolbar) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, 0);
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public void addBackPressListener(NavigationController listener) {
        if (!backPressListeners.contains(listener)) {
            backPressListeners.add(listener);
        }
    }

    @Override
    public void removeBackPressListener(NavigationController listener) {
        if (backPressListeners.contains(listener)) {
            backPressListeners.remove(listener);
        }
    }

    @Override
    protected String screenName() {
        return "MainActivity";
    }
}