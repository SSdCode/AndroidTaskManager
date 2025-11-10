package com.android.settings.systemmonitor;

import android.content.Context;

import com.android.settings.R;
import com.android.settings.core.BasePreferenceController;

public class TopLevelSystemMonitorPreferenceController extends BasePreferenceController {

    public TopLevelSystemMonitorPreferenceController(Context context, String preferenceKey) {
        super(context, preferenceKey);
    }

    @Override
    public int getAvailabilityStatus() {
        return AVAILABLE;
    }

    @Override
    public CharSequence getSummary() {
        return mContext.getString(R.string.system_monitor_summary);
    }
}
