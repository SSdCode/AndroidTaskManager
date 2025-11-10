package com.android.settings.systemmonitor;

import android.app.ActivityManager;
import androidx.fragment.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.R;
import java.util.ArrayList;
import java.util.List;
import android.widget.TextView;

public class ProcessesFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private ProcessAdapter mAdapter;
    private ActivityManager mActivityManager;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_processes, container, false);

        mRecyclerView = view.findViewById(R.id.processes_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mActivityManager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        mAdapter = new ProcessAdapter();
        mRecyclerView.setAdapter(mAdapter);

        refreshData();
        return view;
    }

    public void refreshData() {
        new Thread(() -> {
            List<ProcessInfo> processInfos = new ArrayList<>();

            List<ActivityManager.RunningAppProcessInfo> runningApps =
                    mActivityManager.getRunningAppProcesses();

            if (runningApps != null) {
                for (ActivityManager.RunningAppProcessInfo processInfo : runningApps) {
                    try {
                        Debug.MemoryInfo[] memoryInfos = mActivityManager.getProcessMemoryInfo(
                                new int[]{processInfo.pid});

                        if (memoryInfos != null && memoryInfos.length > 0) {
                            Debug.MemoryInfo memInfo = memoryInfos[0];
                            int memoryKb = memInfo.getTotalPss();

                            ProcessInfo info = new ProcessInfo();
                            info.processName = processInfo.processName;
                            info.pid = processInfo.pid;
                            info.memoryKb = memoryKb;

                            processInfos.add(info);
                        }
                    } catch (Exception e) {
                        // Skip processes we can't access
                    }
                }
            }

            mHandler.post(() -> mAdapter.updateProcesses(processInfos));
        }).start();
    }

    private static class ProcessInfo {
        String processName;
        int pid;
        int memoryKb;
    }

    private class ProcessAdapter extends RecyclerView.Adapter<ProcessViewHolder> {
        private List<ProcessInfo> mProcesses = new ArrayList<>();

        public void updateProcesses(List<ProcessInfo> processes) {
            mProcesses.clear();
            mProcesses.addAll(processes);
            notifyDataSetChanged();
        }

        @Override
        public ProcessViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.process_item, parent, false);
            return new ProcessViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ProcessViewHolder holder, int position) {
            ProcessInfo process = mProcesses.get(position);
            holder.bind(process);
        }

        @Override
        public int getItemCount() {
            return mProcesses.size();
        }
    }

    private class ProcessViewHolder extends RecyclerView.ViewHolder {
        private TextView mProcessName, mPid, mMemory;

        public ProcessViewHolder(View itemView) {
            super(itemView);
            mProcessName = itemView.findViewById(R.id.process_name);
            mPid = itemView.findViewById(R.id.process_pid);
            mMemory = itemView.findViewById(R.id.process_memory);
        }

        public void bind(ProcessInfo process) {
            mProcessName.setText(process.processName);
            mPid.setText(String.valueOf(process.pid));
            mMemory.setText(String.format("%.1f MB", process.memoryKb / 1024.0f));
        }
    }
}