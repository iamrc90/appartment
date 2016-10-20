package com.appartment.helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.util.Base64;
import android.view.WindowManager;

import com.appartment.R;

import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.Set;


@SuppressWarnings("deprecation")
public class Utils {

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null ? cm.getActiveNetworkInfo().isConnectedOrConnecting() : false;

    }

    public static void showDialog(final Context context, final Class<? extends Activity> nextActivityClass) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getResources().getString(R.string.no_internet_connection))
                .setIcon(android.R.drawable.stat_notify_error)
                .setInverseBackgroundForced(true)
                .setCancelable(false)
                .setNegativeButton(context.getResources().getString(R.string.close), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((Activity)context).finish();
                    }
                })
                .setMessage(context.getResources().getString(R.string.internet_connection_alert))
                .setPositiveButton(context.getResources().getString(R.string.retry), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (Utils.isNetworkAvailable(context)) {
                            Intent intent = new Intent(context, nextActivityClass);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            context.startActivity(intent);
                        } else {
                            showDialog(context, nextActivityClass);
                        }
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


    public static void launchActivity(
            Class<? extends Activity> nextActivityClass,
            Activity currentActivity, Map<String, String> extrasMap) {
        Intent launchIntent = new Intent(currentActivity, nextActivityClass);
        if (extrasMap != null && extrasMap.size() > 0) {
            Set<String> keys = extrasMap.keySet();
            for (String key : keys) {
                launchIntent.putExtra(key, extrasMap.get(key));
            }
        }
        launchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        currentActivity.startActivity(launchIntent);
    }


    public static void hideKeyBoard(Context context) {
        ((Activity) context).getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public static Bitmap getBitmap(String imgString, int encodingType) {

        byte[] imgBytes = Base64.decode(imgString, encodingType);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        Bitmap bitmap = BitmapFactory.decodeByteArray(imgBytes, 0,
                imgBytes.length, options);

        return bitmap;
    }

    public static String getBase64StringFromBitmap(Bitmap bmp) {
        String imgString = null;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 50, stream);
        byte[] byteArray = stream.toByteArray();
        imgString = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return imgString;
    }


}
