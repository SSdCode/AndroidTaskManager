package com.android.settings.systemmonitor;

import android.content.Context;
import android.content.Intent;
import androidx.preference.Preference;

import com.android.settings.R;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexable;

@SearchIndexable
public class SystemMonitorDashboardFragment extends DashboardFragment {

    private static final String TAG = "SystemMonitorDashboard";
    private static final String KEY_LAUNCH_MONITOR = "launch_system_monitor";

    @Override
    protected int getPreferenceScreenResId() {
        return R.xml.system_monitor_dashboard;
    }

    @Override
    protected String getLogTag() {
        return TAG;
    }

    @Override
    public int getMetricsCategory() {
        return 1000; // Custom metrics category for System Monitor
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        if (KEY_LAUNCH_MONITOR.equals(preference.getKey())) {
            Context context = getActivity();
            if (context != null) { // Null check for robustness
                Intent intent = new Intent(context, SystemMonitorActivity.class);
                startActivity(intent);
                return true;
            }
        }
        return super.onPreferenceTreeClick(preference);
    }

    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider(R.xml.system_monitor_dashboard);
}
