package com.sixfourapps.use;

import android.content.Intent;

public class AppInfoWrapper {
    private String appLabel, packageName;
    private Intent launchIntent;
    private boolean isSelected;

    public AppInfoWrapper() {}

    public AppInfoWrapper(String appLabel, String packageName, Intent launchIntent) {
        this.appLabel = appLabel;
        this.packageName = packageName;
        this.launchIntent = launchIntent;
        setSelected(false);
    }

    public String getAppLabel() {
        return appLabel;
    }

    public void setAppLabel(String appLabel) {
        this.appLabel = appLabel;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Intent getLaunchIntent() {
        return launchIntent;
    }

    public void setLaunchIntent(Intent launchIntent) {
        this.launchIntent = launchIntent;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

}
