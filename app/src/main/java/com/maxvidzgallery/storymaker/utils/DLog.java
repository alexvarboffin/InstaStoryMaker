package com.maxvidzgallery.storymaker.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by huangwei on 2015/5/25.
 */
public class DLog {

    public static final boolean DEBUG = true;

    private static final String TAG = "@@@";


    /**
     * Log Level Error
     **/
    public static void e(String message) {
        if (DEBUG) {
            Log.d(TAG, buildLogMsg(message));
        }
    }

    /**
     * Log Level Warning
     **/
    public static void w(String message) {
        if (DEBUG) Log.w(TAG, buildLogMsg(message));
    }

    /**
     * Log Level Information
     **/
    public static void i(String message) {
        if (DEBUG) Log.i(TAG, buildLogMsg(message));
    }

    /**
     * Log Level Debug
     **/
    public static void d(String message) {
        if (DEBUG) Log.d(TAG, buildLogMsg(message));
    }

    /**
     * Log Level Verbose
     **/
    public static void v(String message) {
        if (DEBUG) DLog.d( buildLogMsg(message));
    }


    private static String buildLogMsg(String message) {

        StackTraceElement ste = Thread.currentThread().getStackTrace()[4];

        StringBuilder sb = new StringBuilder();


        //Class<? extends StackTraceElement> obj = ste.getClass();
        //if (ste.getClass() instanceof Fragment) {
        //    sb.append("#");
        //}
        sb.append("█ ");
        sb.append(ste.getFileName().replace(".java", ""));
        sb.append("::");
        sb.append(ste.getMethodName());
        sb.append(" ● ");
        sb.append(message);
        sb.append(" █");

        return sb.toString();

    }


    public static boolean nonNull(Object o) {
        if (DEBUG) {
            Log.i(TAG, "nonNull: " + ((o == null) ? o : o.toString()));
        }
        return o != null;
    }

//    context.getString(R.string.app_name) + " v"
// + versionName + " (build " + versionCode + ")"

    public static String getAppVersion(Context context) {
        try {

//            versionCode = context.getPackageManager()
//                    .getPackageInfo(context.getPackageName(),
//                            0).versionCode;

            return context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "Unknown";
        }
    }

    public static String timeStamp(Context context) {
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), 0);
            ZipFile zf = new ZipFile(applicationInfo.sourceDir);
            ZipEntry ze = zf.getEntry("classes.dex");
            long time = ze.getTime();
            String s = SimpleDateFormat.getInstance().format(new java.util.Date(time));
            zf.close();
            return s;
        } catch (Exception e) {
            return "Unknown";
        }
    }

    public static void handleException(Exception e) {
        Log.d(TAG, buildLogMsg(e.getClass().getSimpleName()
                + " @@@ EX @@@ " + (e.getMessage() == null ?
                "NULL" :
                //ff(
                e.getMessage()
                //.replace("Column{", "\n")
                //)
        )));
    }

    //    private static String ff(String replace) {
//
//        if (replace.contains("Found:")) {
//            String[] mm = replace.replace("Expected:", "").split("Found:");
//            String[] c = mm[0].split(",");
//            String[] c1 = mm[1].split(",");
//            for (int i = 0; i < c.length; i++) {
//                if (!c[i].equals(c1[i])) {
//                    DLog.d(c[i] + " __> " + c1[i]);
//                }
//            }
//            return mm[0] + "\n" + mm[1];
//        }
//        return replace;
//    }

    public static void i(String tag, String msg){
        if(DEBUG){
            Log.i(tag,msg);
        }
    }

    public static void v(String tag, String msg){
        if(DEBUG){
            Log.v(tag, msg);
        }
    }

    public static void d(String tag, String msg){
        if(DEBUG){
            Log.d(tag, msg);
        }
    }

    public static void w(String tag, String msg,Throwable t){
        if(DEBUG){
            Log.w(tag, msg,t);
        }
    }

    public static void w(String tag, String msg){
        if(DEBUG){
            Log.w(tag, msg);
        }
    }

    public static void e(String tag, String msg){
        if(DEBUG){
            Log.e(tag, msg);
        }
    }

    public static void e(String tag, String msg,Throwable t){
        if(DEBUG){
            Log.e(tag,msg,t);
        }
    }

}
