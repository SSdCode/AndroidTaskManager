package com.android.settings.systemmonitor;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class SystemMonitorPagerAdapter extends FragmentStateAdapter {

    private FileSystemFragment mFileSystemFragment;
    private ProcessesFragment mProcessesFragment;
    private ResourcesFragment mResourcesFragment;

    public SystemMonitorPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                if (mFileSystemFragment == null) {
                    mFileSystemFragment = new FileSystemFragment();
                }
                return mFileSystemFragment;
            case 1:
                if (mProcessesFragment == null) {
                    mProcessesFragment = new ProcessesFragment();
                }
                return mProcessesFragment;
            case 2:
                if (mResourcesFragment == null) {
                    mResourcesFragment = new ResourcesFragment();
                }
                return mResourcesFragment;
            default:
                return new FileSystemFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public void refreshFragment(int position) {
        switch (position) {
            case 0:
                if (mFileSystemFragment != null) {
                    mFileSystemFragment.refreshData();
                }
                break;
            case 1:
                if (mProcessesFragment != null) {
                    mProcessesFragment.refreshData();
                }
                break;
            case 2:
                if (mResourcesFragment != null) {
                    mResourcesFragment.refreshData();
                }
                break;
        }
    }
}