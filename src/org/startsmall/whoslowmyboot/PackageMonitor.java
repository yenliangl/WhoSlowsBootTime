/**
 * @file   PackageMonitor.java
 * @author josh <yenliangl at gmail dot com>
 * @date   Tue Nov 10 17:47:36 2009
 *
 * @brief Receiver that is called when package is added, removed or changed.
 *
 */

package org.startsmall.whoslowmyboot;

import android.content.Context;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

public class PackageMonitor extends BroadcastReceiver {
    private static final String TAG = "InitReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
    }
}
