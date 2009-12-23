package org.startsmall.whoslowsboottime;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.*;

public class WhoSlowsBootTime extends ListActivity
                              implements DialogInterface.OnClickListener {
    private static final int MENU_ITEM_ID_DISABLE = 0;
    private static final int MENU_ITEM_ID_ENABLE = 1;

    private static final int OPTIONS_MENU_ITEM_ID_FILTER = 0;

    private static final int FILTER_OPTION_ALL = 0;
    private static final int FILTER_OPTION_SYSTEM = 1;
    private static final int FILTER_OPTION_THIRD_PARTY = 2;

    private List<ResolveInfo> mAllAppsCache;

    private class MyAdapter extends ArrayAdapter<ResolveInfo> {
        private final PackageManager mPackageManager;
        private final LayoutInflater mInflater;

        MyAdapter(Context context, List<ResolveInfo> l) {
            super(context, 0, 0, l);

            mPackageManager = context.getPackageManager();
            mInflater = (LayoutInflater)context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view =
                mInflater.inflate(R.layout.list_item, parent, false);

            ActivityInfo activityInfo = getItem(position).activityInfo;

            ImageView iconView =
                (ImageView)view.findViewById(R.id.icon);
            Drawable icon = activityInfo.loadIcon(mPackageManager);
            iconView.setImageDrawable(icon);

            TextView labelView =
                (TextView)view.findViewById(R.id.label);
            final String label =
                activityInfo.loadLabel(mPackageManager).toString();
            labelView.setText(label);

            TextView nameView = (TextView)view.findViewById(R.id.name);
            nameView.setText(activityInfo.name);

            return view;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Request progress bar
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.main);

        setProgressBarIndeterminateVisibility(true);

        PackageManager pm = getPackageManager();
        mAllAppsCache =
            pm.queryBroadcastReceivers(new Intent(Intent.ACTION_BOOT_COMPLETED), 0);

        setListAdapter(new MyAdapter(this, mAllAppsCache));

        setProgressBarIndeterminateVisibility(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, OPTIONS_MENU_ITEM_ID_FILTER, 1,
                 R.string.options_menu_item_filter).setIcon(R.drawable.ic_menu_filter_settings);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
        case OPTIONS_MENU_ITEM_ID_FILTER:
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            dialogBuilder.
                setItems(R.array.filter_options, this).
                setTitle("Select filter option").create().show();
            break;
        }

        return true;
    }

    public void onClick(DialogInterface dialog, int which) {
        updateAppList(which);
        dialog.dismiss();
    }

    private void updateAppList(int filterOption) {
        List<ResolveInfo> newAppList = mAllAppsCache;
        if (filterOption != FILTER_OPTION_ALL) {
            newAppList = getFilteredApps(filterOption);
        }
        setListAdapter(new MyAdapter(this, newAppList));
    }

    private List<ResolveInfo> getFilteredApps(int filterOption) {
        List<ResolveInfo> newList = new ArrayList<ResolveInfo>(mAllAppsCache.size());
        switch (filterOption) {
        case FILTER_OPTION_SYSTEM:
            for (ResolveInfo resolveInfo : mAllAppsCache) {
                ApplicationInfo appInfo = resolveInfo.activityInfo.applicationInfo;
                if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                    newList.add(resolveInfo);
                }
            }
            break;

        case FILTER_OPTION_THIRD_PARTY:
            for (ResolveInfo resolveInfo : mAllAppsCache) {
                ApplicationInfo appInfo = resolveInfo.activityInfo.applicationInfo;
                // if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0 ||
                //     (appInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
                //     newList.add(resolveInfo);
                // }

                if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    newList.add(resolveInfo);
                }
            }
            break;
        }

        return newList;
    }
}
