package com.android.settings.systemmonitor;

import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ProgressBar;
import com.android.settings.R;
import java.io.File;
import android.util.Log;
import java.text.DecimalFormat;
import java.util.List;

public class FileSystemFragment extends Fragment {

    private static final String TAG = "FileSystemFragment";

    private TextView mInternalUsedText, mInternalFreeText, mInternalTotalText;
    private TextView mExternalUsedText, mExternalFreeText, mExternalTotalText;
    private ProgressBar mInternalProgressBar, mExternalProgressBar;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_file_system, container, false);

        // Initialize Views
        mInternalUsedText = view.findViewById(R.id.internal_used);
        mInternalFreeText = view.findViewById(R.id.internal_free);
        mInternalTotalText = view.findViewById(R.id.internal_total);
        mInternalProgressBar = view.findViewById(R.id.internal_progress);

        mExternalUsedText = view.findViewById(R.id.external_used);
        mExternalFreeText = view.findViewById(R.id.external_free);
        mExternalTotalText = view.findViewById(R.id.external_total);
        mExternalProgressBar = view.findViewById(R.id.external_progress);

        refreshData();
        return view;
    }

    public void refreshData() {
        new Thread(() -> {
            // Get Internal Storage Info
            File internalDir = Environment.getDataDirectory();
            StatFs internalStat = new StatFs(internalDir.getPath());
            long internalBlockSize = internalStat.getBlockSizeLong();
            long internalTotalBlocks = internalStat.getBlockCountLong();
            long internalAvailableBlocks = internalStat.getAvailableBlocksLong();
            
            long internalTotal = internalTotalBlocks * internalBlockSize;
            long internalFree = internalAvailableBlocks * internalBlockSize;
            long internalUsed = internalTotal - internalFree;
    
            // Get External Storage Info - IMPROVED LOGIC
            long externalTotal = 0, externalFree = 0, externalUsed = 0;
            boolean hasExternalStorage = false;
            
            // Check for removable storage using StorageManager
            try {
                android.os.storage.StorageManager storageManager = 
                    (android.os.storage.StorageManager) getContext().getSystemService(android.content.Context.STORAGE_SERVICE);
                
                if (storageManager != null) {
                    // Get all storage volumes
                    java.lang.reflect.Method getVolumesMethod = storageManager.getClass().getMethod("getVolumes");
                    List<?> volumes = (List<?>) getVolumesMethod.invoke(storageManager);
                    
                    if (volumes != null) {
                        for (Object volume : volumes) {
                            // Check if volume is removable and mounted
                            java.lang.reflect.Method isRemovableMethod = volume.getClass().getMethod("isRemovable");
                            java.lang.reflect.Method getStateMethod = volume.getClass().getMethod("getState");
                            java.lang.reflect.Method getPathMethod = volume.getClass().getMethod("getPath");
                            
                            boolean isRemovable = (Boolean) isRemovableMethod.invoke(volume);
                            String state = (String) getStateMethod.invoke(volume);
                            File path = (File) getPathMethod.invoke(volume);
                            
                            if (isRemovable && "mounted".equals(state) && path != null) {
                                try {
                                    StatFs externalStat = new StatFs(path.getPath());
                                    long externalBlockSize = externalStat.getBlockSizeLong();
                                    long externalTotalBlocks = externalStat.getBlockCountLong();
                                    long externalAvailableBlocks = externalStat.getAvailableBlocksLong();
                                    
                                    externalTotal = externalTotalBlocks * externalBlockSize;
                                    externalFree = externalAvailableBlocks * externalBlockSize;
                                    externalUsed = externalTotal - externalFree;
                                    hasExternalStorage = true;
                                    Log.d(TAG, "Found external storage at: " + path.getPath());
                                    break; // Found removable storage
                                } catch (Exception e) {
                                    Log.e(TAG, "Error reading external storage: " + e.getMessage());
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "StorageManager reflection failed: " + e.getMessage());
            }
            
            // Fallback: Check common SD card mount points if StorageManager fails
            if (!hasExternalStorage) {
                String[] externalPaths = {
                    "/storage/sdcard1",
                    "/storage/extSdCard",
                    "/mnt/extSdCard",
                    "/mnt/external_sd",
                    "/storage/external_SD",
                    "/mnt/sdcard/external_sd"
                };
                
                for (String path : externalPaths) {
                    File testDir = new File(path);
                    if (testDir.exists() && testDir.canRead()) {
                        try {
                            StatFs testStat = new StatFs(path);
                            long testTotal = testStat.getBlockCountLong() * testStat.getBlockSizeLong();
                            
                            // Verify it's different from internal storage (allow 5% margin)
                            if (Math.abs(testTotal - internalTotal) > (internalTotal * 0.05)) {
                                externalTotal = testTotal;
                                externalFree = testStat.getAvailableBlocksLong() * testStat.getBlockSizeLong();
                                externalUsed = externalTotal - externalFree;
                                hasExternalStorage = true;
                                Log.d(TAG, "Found external storage at: " + path);
                                break;
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Failed to check path " + path + ": " + e.getMessage());
                        }
                    }
                }
            }
    
            // Update UI on main thread
            final long finalInternalTotal = internalTotal;
            final long finalInternalFree = internalFree;
            final long finalInternalUsed = internalUsed;
            final long finalExternalTotal = externalTotal;
            final long finalExternalFree = externalFree;
            final long finalExternalUsed = externalUsed;
            final boolean finalHasExternal = hasExternalStorage;
    
            mHandler.post(() -> updateUI(
                finalInternalTotal, finalInternalFree, finalInternalUsed,
                finalExternalTotal, finalExternalFree, finalExternalUsed,
                finalHasExternal
            ));
        }).start();
    }
    
    
    private void updateUI(long internalTotal, long internalFree, long internalUsed,
                         long externalTotal, long externalFree, long externalUsed,
                         boolean hasExternal) {
        
        // Update Internal Storage
        mInternalTotalText.setText(formatBytes(internalTotal));
        mInternalFreeText.setText(formatBytes(internalFree));
        mInternalUsedText.setText(formatBytes(internalUsed));
        
        if (internalTotal > 0) {
            int internalProgress = (int) ((internalUsed * 100) / internalTotal);
            mInternalProgressBar.setProgress(internalProgress);
        }
    
        // Update External Storage
        if (hasExternal && externalTotal > 0) {
            mExternalTotalText.setText(formatBytes(externalTotal));
            mExternalFreeText.setText(formatBytes(externalFree));
            mExternalUsedText.setText(formatBytes(externalUsed));
            
            int externalProgress = (int) ((externalUsed * 100) / externalTotal);
            mExternalProgressBar.setProgress(externalProgress);
        } else {
            mExternalTotalText.setText("No external storage");
            mExternalFreeText.setText("N/A");
            mExternalUsedText.setText("N/A");
            mExternalProgressBar.setProgress(0);
        }
    }
    

    private String formatBytes(long bytes) {
        DecimalFormat df = new DecimalFormat("#.##");
        if (bytes >= 1024L * 1024L * 1024L) {
            return df.format(bytes / (1024.0 * 1024.0 * 1024.0)) + " GB";
        } else if (bytes >= 1024L * 1024L) {
            return df.format(bytes / (1024.0 * 1024.0)) + " MB";
        } else {
            return df.format(bytes / 1024.0) + " KB";
        }
    }
}
