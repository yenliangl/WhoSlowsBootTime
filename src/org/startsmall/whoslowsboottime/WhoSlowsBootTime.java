package org.startsmall.whoslowsboottime;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.*;
import java.util.concurrent.*;

public class WhoSlowsBootTime extends ListActivity {
    class AppInfo {
        public String name;
        public CharSequence label;
        public Drawable icon;
    }

    private static class AppInfoAdapter extends BaseAdapter {
        static class WidgetCache {
            ImageView imageView;
            TextView labelView;
            TextView nameView;
        }

        // private Map<String, AppInfo> mAppInfoMap = new HashMap<String, AppInfo>();
        private List<AppInfo> mAppInfoList = new LinkedList<AppInfo>();
        private LayoutInflater mInflater;

        public AppInfoAdapter(Context context) {
            mInflater = (LayoutInflater)context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        }

        public void clear() {
            mAppInfoList.clear();
        }

        public void add(AppInfo appInfo) {
            mAppInfoList.add(appInfo);
        }

        @Override
        public int getCount() {
            return mAppInfoList.size();
        }

        @Override
        public Object getItem(int position) {
            return mAppInfoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            Log.d(TAG, "====> getView(): position=" + position + ", convertView=" + convertView);

            AppInfo appInfo = (AppInfo)getItem(position);

            WidgetCache widgetCache;
            if (convertView == null) { // this is a new view
                convertView =
                    mInflater.inflate(R.layout.list_item, parent, false);
                widgetCache = new WidgetCache();
                widgetCache.imageView = (ImageView)convertView.findViewById(R.id.icon);
                widgetCache.labelView = (TextView)convertView.findViewById(R.id.label);
                widgetCache.nameView = (TextView)convertView.findViewById(R.id.name);
                convertView.setTag(widgetCache);
            } else {
                widgetCache = (WidgetCache)convertView.getTag();
            }

            widgetCache.imageView.setImageDrawable(appInfo.icon);
            widgetCache.labelView.setText(appInfo.label);
            widgetCache.nameView.setText(appInfo.name);
            return convertView;
        }

        public void updateView() {
            if (!mAppInfoList.isEmpty()) {
                notifyDataSetChanged();
            }
        }
    }

    private class LoadAppInfoTask implements Runnable {
        public void run() {
            mHandler.sendEmptyMessage(ADD_PACKAGE_BEGIN);
            List<ResolveInfo> resolveInfoList =
                mPackageManager.queryBroadcastReceivers(new Intent(Intent.ACTION_BOOT_COMPLETED), 0);
            // int count = 0;
            for (ResolveInfo info : resolveInfoList) {
                ActivityInfo activityInfo = info.activityInfo;
                AppInfo appInfo = new AppInfo();
                appInfo.name = activityInfo.name;
                appInfo.label = activityInfo.loadLabel(mPackageManager);
                appInfo.icon = activityInfo.loadIcon(mPackageManager);
                mHandler.sendMessage(mHandler.obtainMessage(ADD_PACKAGE, appInfo));

                // if (count % 5 == 0) {
                //     mHandler.sendEmptyMessage(ADD_PACKAGE_UPDATE);
                // }

                // count++;
            }
            mHandler.sendEmptyMessage(ADD_PACKAGE_END);
        }
    }

    private Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                case ADD_PACKAGE_BEGIN:
                    setProgressBarIndeterminateVisibility(true);
                    mAdapter.clear();
                    break;
                case ADD_PACKAGE:
                    AppInfo appInfo = (AppInfo)msg.obj;
                    mAdapter.add(appInfo);
                    break;

                // case ADD_PACKAGE_UPDATE:
                //     Log.d(TAG, "===> handleMessage(): ADD_PACKAGE_UPDATE");


                //     mAdapter.updateView();
                //     break;

                case ADD_PACKAGE_END:

                    Log.d(TAG, "===> handleMessage(): ADD_PACKAGE_END");

                    mAdapter.updateView();
                    setProgressBarIndeterminateVisibility(false);
                    break;
                }
            }
        };

    private static final String TAG = "WhoSlowsBootTime";

    private static final int ADD_PACKAGE_BEGIN = 0;
    private static final int ADD_PACKAGE = 1;
    private static final int ADD_PACKAGE_UPDATE = 2;
    private static final int ADD_PACKAGE_END = 3;

    private ExecutorService mExecutor;
    private PackageManager mPackageManager;
    private AppInfoAdapter mAdapter;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Request progress bar
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.main);

        mPackageManager = getPackageManager();
        mExecutor = Executors.newSingleThreadExecutor();
        mAdapter = new AppInfoAdapter(this);

        setListAdapter(mAdapter);

        mExecutor.execute(new LoadAppInfoTask());
        mExecutor.shutdown();
    }
}
