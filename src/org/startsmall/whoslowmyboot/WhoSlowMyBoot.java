package org.startsmall.whoslowmyboot;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.*;

public class WhoSlowMyBoot extends ListActivity {

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

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.main);

        PackageManager pm = getPackageManager();
        List<ResolveInfo> actions =
            pm.queryBroadcastReceivers(prepareQueryIntent(), 0);

        setListAdapter(new MyAdapter(this, actions));
    }

    private Intent prepareQueryIntent() {
        Intent intent = new Intent(Intent.ACTION_BOOT_COMPLETED);
        // intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
        return intent;
    }
}
