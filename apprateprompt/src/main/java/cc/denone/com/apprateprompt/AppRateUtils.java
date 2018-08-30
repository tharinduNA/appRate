package cc.denone.com.apprateprompt;


/**
 * @author Tharindu 13-Aug-2018
 **/


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AppRateUtils {

    private static final String PREF_KEY_INSTALLED_DATE = "android_rate_app_installed_date";
    private static final String PREF_KEY_INSTALLED_DAYS_THREASHOLD = "android_rate_app_installed_days_threashold";
    private static final String PREF_KEY_LAUNCH_TIMES = "android_rate_app_launch_times";
    private static final String PREF_KEY_NEVER_SHOW = "android_rate_app_never_show";
    private static final String PREF_KEY_MAY_BE_LATER = "android_rate_app_may_be_later";
    private static final String PREF_KEY_MAY_BE_LATER_SET_ON = "android_rate_app_may_be_later_set_on";
    private static final String PREF_KEY_MAY_BE_LATER_THRESHOLD = "android_rate_app_may_be_later_threashold";

    private static final String GOOGLE_PLAY = "https://play.google.com/store/apps/details?id=";
    private static final String GOOGLE_PLAY_PACKAGE_NAME = "com.android.vending";
    private static int daysUsed = 5;
    private static int remindThreashold = 5;
    private static int launchThreashold = 5;

    private static String alertTitle = "Rate!";
    private static String alertMessage = "Can you give a rating on google play";
    private static String alertPositive = "Rate Now";
    private static String alertNegative = "Never";
    private static String alertNeutral = "Not now";


    private static SharedPreferences.Editor getPreferencesEditor(Context context) {
        return getPreferences(context).edit();
    }

    private static SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    }

    private static void setInstallDate(Context context) {
        SharedPreferences.Editor editor = getPreferencesEditor(context);
        editor.putLong(PREF_KEY_INSTALLED_DATE, new Date().getTime());
        editor.apply();
    }

    private static long getInstalledDate(Context context) {
        return getPreferences(context).getLong(PREF_KEY_INSTALLED_DATE, 0);
    }

    private static int getDaysUsed(Context context, long flagedDay) {
        if (AppRateUtils.getInstalledDate(context) != 0) {
            long difference = new Date().getTime() - flagedDay;
            return (int) TimeUnit.DAYS.convert(difference, TimeUnit.MILLISECONDS);
        } else {
            return 0;
        }
    }

    private static void setDaysToFirstReminder(Context context, int daysToUse) {
        AppRateUtils.daysUsed = daysToUse;
        SharedPreferences.Editor editor = getPreferencesEditor(context);
        editor.putInt(PREF_KEY_INSTALLED_DAYS_THREASHOLD, daysUsed);
        editor.apply();
    }

    private static int getDaysUsedThreashold (Context context) {
        return getPreferences(context).getInt(PREF_KEY_INSTALLED_DAYS_THREASHOLD, daysUsed);
    }

    private static void setLaunchTimes(Context context) {
        SharedPreferences.Editor editor = getPreferencesEditor(context);
        editor.putInt(PREF_KEY_LAUNCH_TIMES, getLaunchTimes(context) + 1);
        editor.apply();
    }

    private static int getLaunchTimes(Context context) {
        return getPreferences(context).getInt(PREF_KEY_LAUNCH_TIMES, 0);
    }

    private static boolean isNeverShow(Context context) {
        return getPreferences(context).getBoolean(PREF_KEY_NEVER_SHOW, false);
    }

    private static void setNeverShow(Context context) {
        SharedPreferences.Editor editor = getPreferencesEditor(context);
        editor.putBoolean(PREF_KEY_NEVER_SHOW, true);
        editor.apply();
    }

    private static void setMayBeLater(Context context) {
        SharedPreferences.Editor editor = getPreferencesEditor(context);
        editor.putBoolean(PREF_KEY_MAY_BE_LATER, true);
        editor.putLong(PREF_KEY_MAY_BE_LATER_SET_ON, new Date().getTime());
        editor.apply();
    }

    private static long getMayBeLaterDate(Context context){
        return getPreferences(context).getLong(PREF_KEY_MAY_BE_LATER_SET_ON, 0);
    }

    private static boolean isMayBeLater(Context context) {
        return getPreferences(context).getBoolean(PREF_KEY_MAY_BE_LATER, false);
    }

    private static int getRemindInterval(Context context) {
        return getPreferences(context).getInt(PREF_KEY_MAY_BE_LATER_THRESHOLD, remindThreashold);
    }

    private static void setRemindInterval(Context context, int remindInterval) {
        AppRateUtils.remindThreashold = remindInterval;
        SharedPreferences.Editor editor = getPreferencesEditor(context);
        editor.putInt(PREF_KEY_MAY_BE_LATER_THRESHOLD, remindInterval);
        editor.apply();
    }

    private static int getLauchThreashold(Context context) {
        return launchThreashold;
    }

    private static void rateAppWhenConditionsMeet(final Activity activity) {

        if (!isNeverShow(activity)) {
            if (isMayBeLater(activity)) {
                if (getDaysUsed(activity, getMayBeLaterDate(activity)) >= getRemindInterval(activity)) {
                    showAlert(activity);
                }
            } else {
                if (getDaysUsed(activity, getInstalledDate(activity)) >= getDaysUsedThreashold(activity)
                        && getLaunchTimes(activity) >= getLauchThreashold(activity)) {
                    showAlert(activity);
                }
            }
        }
    }

    private static void showAlert (final Activity activity) {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                showAlertDialog(activity, getAlertTitle(),
                        getAlertMessage(), getAlertNegative(), getAlertPositive(), getAlertNeutral(),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                setNeverShow(activity);
                            }
                        },
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                activity.startActivity(createIntentForGooglePlay(activity));
                                setNeverShow(activity);
                            }
                        },
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                setMayBeLater(activity);
                            }
                        });
            }
        });
    }

    private static Intent createIntentForGooglePlay(Context context) {
        String packageName = context.getPackageName();
        Intent intent = new Intent(Intent.ACTION_VIEW, getGooglePlay(packageName));
        if (isPackageExists(context, GOOGLE_PLAY_PACKAGE_NAME)) {
            intent.setPackage(GOOGLE_PLAY_PACKAGE_NAME);
        }
        return intent;
    }

    private static Uri getGooglePlay(String packageName) {
        return packageName == null ? null : Uri.parse(GOOGLE_PLAY + packageName);
    }

    private static boolean isPackageExists(Context context, String targetPackage) {
        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(0);
        for (ApplicationInfo packageInfo : packages) {
            if (packageInfo.packageName.equals(targetPackage)) return true;
        }
        return false;
    }

//    public static void clearData(Context context) {
//        SharedPreferences.Editor editor = getPreferencesEditor(context);
//        editor.remove(PREF_KEY_LAUNCH_TIMES);
//        editor.commit();
//    }

    public static void initRateApp(Activity activity) {
        if (getInstalledDate(activity) == 0) {
            setInstallDate(activity);
        }
        setLaunchTimes(activity);
        rateAppWhenConditionsMeet(activity);
    }

    public static void initRateApp(Activity activity, int daysThreashold, int launchCount, int remindCount) {
        daysUsed = daysThreashold;
        launchThreashold = launchCount;
        remindThreashold = remindCount;
        if (getInstalledDate(activity) == 0) {
            setInstallDate(activity);
        }
        setLaunchTimes(activity);
        rateAppWhenConditionsMeet(activity);
    }

    public static void initRateApp(Activity activity, String title, String message, String positiveBtn, String negativeBtn, String neutralBtn) {

        setAlertMessage(message);
        setAlertTitle(title);
        setAlertPositive(positiveBtn);
        setAlertNegative(negativeBtn);
        setAlertNeutral(neutralBtn);

        if (getInstalledDate(activity) == 0) {
            setInstallDate(activity);
        }
        setLaunchTimes(activity);
        rateAppWhenConditionsMeet(activity);
    }

    public static void initRateApp(Activity activity, int daysThreashold, int launchCount, int remindCount,
                                   String title, String message, String positiveBtn, String negativeBtn, String neutralBtn) {

        setAlertMessage(message);
        setAlertTitle(title);
        setAlertPositive(positiveBtn);
        setAlertNegative(negativeBtn);
        setAlertNeutral(neutralBtn);

        daysUsed = daysThreashold;
        launchThreashold = launchCount;
        remindThreashold = remindCount;

        if (getInstalledDate(activity) == 0) {
            setInstallDate(activity);
        }
        setLaunchTimes(activity);
        rateAppWhenConditionsMeet(activity);
    }

    private static void showAlertDialog(

            Activity activity,
            String title,
            String message,
            String negativeBtn,
            String positiveBtn,
            String neutralBtn,
            DialogInterface.OnClickListener negativeBtnListener,
            DialogInterface.OnClickListener positiveBtnListener,
            DialogInterface.OnClickListener neutralBtnListener) {

        if (!activity.isFinishing()) {

            AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
            alertDialog.setTitle(title);
            alertDialog.setMessage(message);
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, negativeBtn, negativeBtnListener);
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, positiveBtn, positiveBtnListener);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, neutralBtn, neutralBtnListener);
            alertDialog.show();
        }
    }

    private static String getAlertTitle() {
        return alertTitle;
    }

    private static void setAlertTitle(String alertTitle) {
        AppRateUtils.alertTitle = alertTitle;
    }

    private static String getAlertMessage() {
        return alertMessage;
    }

    private static void setAlertMessage(String alertMessage) {
        AppRateUtils.alertMessage = alertMessage;
    }

    private static String getAlertPositive() {
        return alertPositive;
    }

    private static void setAlertPositive(String alertPositive) {
        AppRateUtils.alertPositive = alertPositive;
    }

    private static String getAlertNegative() {
        return alertNegative;
    }

    private static void setAlertNegative(String alertNegative) {
        AppRateUtils.alertNegative = alertNegative;
    }

    private static String getAlertNeutral() {
        return alertNeutral;
    }

    private static void setAlertNeutral(String alertNeutral) {
        AppRateUtils.alertNeutral = alertNeutral;
    }
}
