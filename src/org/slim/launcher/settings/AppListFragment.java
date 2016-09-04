package org.slim.launcher.settings;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.launcher3.FolderInfo;
import com.android.launcher3.R;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.compat.LauncherActivityInfoCompat;
import com.android.launcher3.compat.LauncherActivityInfoCompatVL;

import org.slim.launcher.SlimLauncher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AppListFragment extends SettingsPreferenceFragment {

    private FolderInfo mFolderInfo;

    public AppListFragment() {
    }

    public AppListFragment(FolderInfo info) {
        mFolderInfo = info;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.app_list, parent, false);
        recyclerView.setHasFixedSize(true);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        AppAdapter adapter = new AppAdapter();
        recyclerView.setAdapter(adapter);

        return recyclerView;
    }

    class AppViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView title;
        CheckBox checkBox;

        AppViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.icon);
            title = (TextView) view.findViewById(R.id.title);
            checkBox = (CheckBox) view.findViewById(R.id.checkbox);
        }
    }

    class AppAdapter extends RecyclerView.Adapter<AppViewHolder> {

        List<ResolveInfo> mApps = new ArrayList<>();

        AppAdapter() {
            Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            mApps = mContext.getPackageManager().queryIntentActivities(mainIntent, 0);
            Collections.sort(mApps, new ResolveInfo.DisplayNameComparator(mContext.getPackageManager()));
        }

        @Override
        public AppViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.app_item, parent, false);
            return new AppViewHolder(view);
        }

        @Override
        public void onBindViewHolder(AppViewHolder holder, final int position) {
            holder.title.setText(mApps.get(position).loadLabel(mContext.getPackageManager()));
            holder.imageView.setImageDrawable(mApps.get(position).loadIcon(mContext.getPackageManager()));

            final ShortcutInfo sInfo = getShortcutInfoForResolveInfo(mApps.get(position));

            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        mFolderInfo.add(sInfo);
                    } else {
                        mFolderInfo.remove(sInfo);
                    }
                    SlimLauncher.getInstance().getAppsView().updateDrawerFolders();
                }
            });

            holder.checkBox.setChecked(mFolderInfo.contents.contains(sInfo));
        }

        @Override
        public int getItemCount() {
            return mApps.size();
        }

        ResolveInfo getItem(int position) {
            return mApps.get(position);
        }
    }

    private ShortcutInfo getShortcutInfoForResolveInfo(ResolveInfo resolveInfo) {
        LauncherActivityInfoCompat activityInfo = LauncherActivityInfoCompat.fromResolveInfo(
                resolveInfo, mContext);
        return ShortcutInfo.fromActivityInfo(activityInfo,mContext);
    }
}
