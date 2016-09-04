package org.slim.launcher.settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.android.launcher3.*;
import com.android.launcher3.util.LongArrayMap;

import org.slim.launcher.SlimLauncher;

public class ManageDrawerFoldersFragment extends SettingsPreferenceFragment {

    public static final int MENU_ADD = Menu.FIRST;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        updatePrefs();
    }

    private void updatePrefs() {
        PreferenceScreen screen = getPreferenceScreen();
        if (screen == null) {
            screen = getPreferenceManager().createPreferenceScreen(mContext);
            setPreferenceScreen(screen);
        }
        screen.removeAll();

        LongArrayMap<FolderInfo> drawerFolders = LauncherModel.getFolder();
        for (final FolderInfo info : drawerFolders) {
            if (info.container != SlimLauncher.CONTAINER_APP_DRAWER) continue;
            addPreference(info);
        }
        setHasOptionsMenu(true);
    }

    private void addPreference(final FolderInfo info) {
        Preference preference = new Preference(mContext);
        preference.setTitle(info.title);
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AppListFragment fragment = new AppListFragment(info);
                getFragmentManager().beginTransaction().replace(android.R.id.content, fragment)
                        .commit();
                return true;
            }
        });
        getPreferenceScreen().addPreference(preference);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(0, MENU_ADD, 0, "Add")
                .setIcon(android.R.drawable.ic_menu_add)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_ADD:
                addFolder();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addFolder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("New Folder");

        final EditText editText = new EditText(mContext);
        editText.setHint("Folder Name");

        builder.setView(editText);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                FolderInfo info = new FolderInfo();
                info.setTitle(editText.getText().toString());
                info.container = SlimLauncher.CONTAINER_APP_DRAWER;
                LauncherModel.addItemToDatabase(mContext, info, SlimLauncher.CONTAINER_APP_DRAWER,
                        0, 0, 0);
                addPreference(info);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);
        builder.show();
    }
}
