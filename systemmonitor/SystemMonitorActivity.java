package com.android.settings.systemmonitor;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.android.settings.R;

import androidx.fragment.app.FragmentActivity;

public class SystemMonitorActivity extends FragmentActivity {

    private ViewPager2 mViewPager;
    private TabLayout mTabLayout;
    private SystemMonitorPagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.system_monitor_main);

        // Setup ActionBar
        if (getActionBar() != null) {
            getActionBar().setTitle(R.string.system_monitor_title);
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize Views
        mTabLayout = findViewById(R.id.tab_layout);
        mViewPager = findViewById(R.id.view_pager);

        // Setup ViewPager with Adapter
        mAdapter = new SystemMonitorPagerAdapter(this);
        mViewPager.setAdapter(mAdapter);

        // Connect TabLayout with ViewPager2
        new TabLayoutMediator(mTabLayout, mViewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText(R.string.tab_file_system);
                            break;
                        case 1:
                            tab.setText(R.string.tab_processes);
                            break;
                        case 2:
                            tab.setText(R.string.tab_resources);
                            break;
                    }
                }).attach();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.system_monitor_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.action_refresh) {
            refreshCurrentFragment();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void refreshCurrentFragment() {
        int currentItem = mViewPager.getCurrentItem();
        mAdapter.refreshFragment(currentItem);
    }
}