package com.sixfourapps.use;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;

import com.google.android.material.appbar.MaterialToolbar;
import com.sixfourapps.use.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity implements AppCardAdapter.OnAppCardClickListener {

    private ActivityMainBinding binding;
    private List<AppInfoWrapper> launchableApps, allowedApps, restrictedApps;
    private PackageManager packageManager;
    private AppCardAdapter adapter;
    private AppInfoWrapper selectedApp;
    private MaterialToolbar toolbar;
    private ImageButton switchAppListButton;
    private Window topWindow;
    private ActionMode actionMode;
    protected FocusMode focusMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //set binding instance in activity
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //set up the default FocusMode
        focusMode = FocusMode.USE_FULL;

        //set up the Theme for status bar and action bar
        topWindow = getWindow();
        toolbar = findViewById(R.id.app_toolbar);
        switchAppListButton = binding.switchAppListButton;
        switchAppListButton.setOnClickListener(v -> {
            focusMode = (focusMode == FocusMode.USE_LESS) ? FocusMode.USE_FULL : FocusMode.USE_LESS;
            updateTheme();
            updateAppList();
        });
        updateTheme();
        setSupportActionBar(toolbar);


        //get the package manager for MainActivity class
        packageManager = getPackageManager();

        //get the list of launchable applications
        launchableApps = getListOfLaunchableApps();

        //get the list of allowed apps (initialize with all apps)
        allowedApps = new ArrayList<>(launchableApps);

        //get the list of restricted apps (initialize with an empty list)
        restrictedApps = new ArrayList<>();

        //set layout manager to RecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        binding.recyclerView.setLayoutManager(layoutManager);

        //set up the adapter
        adapter = new AppCardAdapter(this, allowedApps, this);
        binding.recyclerView.setAdapter(adapter);


    }

    private void updateAppList() {
        switch (focusMode) {
            case USE_FULL:
                adapter = new AppCardAdapter(this, allowedApps, this);
                binding.recyclerView.setAdapter(adapter);
                break;
            case USE_LESS:
                adapter = new AppCardAdapter(this, restrictedApps, this);
                binding.recyclerView.setAdapter(adapter);
        }
    }

    /**
     * change the theme to green or red based the current focus mode
     */
    private void updateTheme() {
        switch (focusMode) {
            case USE_FULL:
                topWindow.setStatusBarColor(getResources().getColor(R.color.green_700, getTheme()));
                topWindow.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                toolbar.setTitle("Use Full");
                toolbar.setTitleTextColor(getResources().getColor(R.color.black, getTheme()));
                toolbar.setBackground(ContextCompat.getDrawable(this, R.color.green_500));
                switchAppListButton.setImageResource(R.drawable.ic_goto_useless);
                break;

            case USE_LESS:
                topWindow.setStatusBarColor(getResources().getColor(R.color.red_700, getTheme()));
                int flags = topWindow.getDecorView().getSystemUiVisibility() ^ View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                topWindow.getDecorView().setSystemUiVisibility(flags);
                toolbar.setTitle("Use Less");
                toolbar.setTitleTextColor(getResources().getColor(R.color.white, getTheme()));
                toolbar.setBackground(ContextCompat.getDrawable(this, R.color.red_500));
                switchAppListButton.setImageResource(R.drawable.ic_goto_usefull);
                break;

            default:
                toolbar.setTitle("Use");
                break;
        }
    }

    /**
     * set up what happens on entering and exiting ActionMode
     */
    ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            switch (focusMode) {
                case USE_FULL:
                    getMenuInflater().inflate(R.menu.add_to_useless_menu, menu);
                    break;
                case USE_LESS:
                    getMenuInflater().inflate(R.menu.add_to_usefull_menu, menu);
                    break;
            }
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (focusMode) {
                case USE_FULL:
                    if (item.getItemId() == R.id.add_to_useless_button) {
                        List<AppInfoWrapper> tempList = new ArrayList<>(allowedApps);
                        tempList.forEach(appInfo -> {
                            if (appInfo.isSelected()) {
                                allowedApps.remove(appInfo);
                                restrictedApps.add(appInfo);
                            }
                        });
                    }
                    restrictedApps.forEach(appInfo -> appInfo.setSelected(false));
                    restrictedApps.sort((app1, app2) -> app1.getAppLabel().compareToIgnoreCase(app2.getAppLabel()));
                    break;

                case USE_LESS:
                    if (item.getItemId() == R.id.add_to_usefull_button) {
                        List<AppInfoWrapper> tempList = new ArrayList<>(restrictedApps);
                        tempList.forEach(appInfo -> {
                            if (appInfo.isSelected()) {
                                restrictedApps.remove(appInfo);
                                allowedApps.add(appInfo);
                            }
                        });
                    }
                    restrictedApps.forEach(appInfo -> appInfo.setSelected(false));
                    allowedApps.sort((app1, app2) -> app1.getAppLabel().compareToIgnoreCase(app2.getAppLabel()));
                    break;
            }
            adapter.notifyDataSetChanged();
            actionMode.finish();
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            allowedApps.forEach(appInfo -> appInfo.setSelected(false));
            adapter.notifyDataSetChanged();
            actionMode = null;
            toolbar.setTitle(focusMode == FocusMode.USE_FULL ? "Use Full" : "Use Less");
            switchAppListButton.setVisibility(View.VISIBLE);
        }
    };

    /**
     * fetches the app the list in the system,
     * filters out the non launchable apps
     * and converts them to AppInfoWrapper objects
     *
     * @return a list of AppInfoWrapper objects
     */
    private List<AppInfoWrapper> getListOfLaunchableApps() {
        return packageManager.getInstalledApplications(0)
                .stream().filter(appInfo -> packageManager.getLaunchIntentForPackage(appInfo.packageName) != null)
                .sorted((app1, app2) ->
                        packageManager.getApplicationLabel(app1).toString()
                                .compareToIgnoreCase(packageManager.getApplicationLabel(app2).toString())
                )
                .map(appInfo -> new AppInfoWrapper(
                        packageManager.getApplicationLabel(appInfo).toString(),
                        appInfo.packageName,
                        packageManager.getLaunchIntentForPackage(appInfo.packageName)))
                .collect(Collectors.toList());
    }

    @Override
    public void onAppCardClick(int position) {
        if (actionMode != null) {
            toggleSelection(position);
        } else {
            selectedApp = focusMode == FocusMode.USE_FULL ? allowedApps.get(position) : restrictedApps.get(position);
            Intent launchIntent = selectedApp.getLaunchIntent();
            startActivity(launchIntent);
        }
    }

    private void toggleSelection(int position) {
        int count;
        switch (focusMode) {
            case USE_FULL:
                selectedApp = allowedApps.get(position);
                allowedApps.get(position).setSelected(!selectedApp.isSelected());
                adapter.notifyDataSetChanged();

                count = (int) allowedApps.stream().filter(AppInfoWrapper::isSelected).count();
                if (count == 0 || count == 1) {
                    actionMode.setTitle(count + " App selected");
                } else {
                    actionMode.setTitle(count + " Apps selected");
                }
                break;

            case USE_LESS:
                selectedApp = restrictedApps.get(position);
                restrictedApps.get(position).setSelected(!selectedApp.isSelected());
                adapter.notifyDataSetChanged();

                count = (int) restrictedApps.stream().filter(AppInfoWrapper::isSelected).count();
                if (count == 0 || count == 1) {
                    actionMode.setTitle(count + " App selected");
                } else {
                    actionMode.setTitle(count + " Apps selected");
                }
                break;
        }
    }

    @Override
    public void onAppCardLongClick(int position) {
        if (actionMode == null) {
            toolbar.setTitle("");
            switchAppListButton.setVisibility(View.GONE);
            actionMode = startActionMode(actionModeCallback);
        }
        toggleSelection(position);
    }
}