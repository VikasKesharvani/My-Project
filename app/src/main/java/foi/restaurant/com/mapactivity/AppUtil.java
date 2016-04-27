package foi.restaurant.com.mapactivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;



import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings.Secure;
import android.text.GetChars;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;

public class AppUtil {

    private static String groupID = "";

    /**
     * GET THE DEVICE TOKEN ID
     *
     * @return
     */
    public static String getDeviceTokenId(Context context) {
        return Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        Log.e("Manufacture", manufacturer);
        String model = Build.MODEL;
        return manufacturer;
    }

    /**
     * THIS MEHTOD RETURN EITHER DEVICE IS ANDROID OR I-PHONE
     *
     * @return
     */
    public static String getDevice() {
        return "ANDROID";
    }

    /**
     * THIS METHOD RETURNS WHETHER DEVICE IS MOBILE OR TAB
     *
     * @return
     */
    public static String getDeviceSizeType(Context context) {
        if ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE)
            return "TAB";
        else
            return "MOBILE";

        // DisplayMetrics metrics = new DisplayMetrics();
        //
        // getWindowManager().getDefaultDisplay().getMetrics(metrics);
        //
        // float yInches = metrics.heightPixels / metrics.ydpi;
        // float xInches = metrics.widthPixels / metrics.xdpi;
        // double diagonalInches = Math.sqrt(xInches * xInches + yInches *
        // yInches);
        // if (diagonalInches >= 6.5) {
        // // 6.5inch device or bigger
        // } else {
        // // smaller device
        // }
    }

    /**
     * GET THE DEVICE CURRENT LOCATION
     *
     * @return
     */
    public static ArrayList<String> getDeviceLocation(Context context) {
        ArrayList<String> locationList = new ArrayList<>();
        AppLocationService locationService = new AppLocationService(context);
        Location location =
                locationService.getLocation(LocationManager.GPS_PROVIDER);

        if (location != null) {
            // Toast.makeText(context, "long= " + location.getLongitude() +
            // "\n Lat = " + location.getLatitude(), Toast.LENGTH_SHORT).show();
            locationList.add(String.valueOf(location.getLatitude()));
            locationList.add(String.valueOf(location.getLongitude()));
            return locationList;
        }
        return locationList;
    }

    /**
     * THIS METHOD CONVERT INPUT STREAM TO STRING
     *
     * @param is
     * @return
     * @throws IOException
     */
    public static String convertStringFromInputStream(InputStream is) throws IOException {
        try {
            byte[] bytes = new byte[1024];

            StringBuilder x = new StringBuilder();

            int numRead = 0;
            while ((numRead = is.read(bytes)) >= 0) {
                x.append(new String(bytes, 0, numRead));
            }

            return x.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Checking for all possible internet providers, return true if data
     * connection is available else false
     **/
    public static boolean isConnectedToInternet(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }

        }
        return false;
    }

    public static void showCustomDialog(Context context, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setIcon(android.R.drawable.ic_dialog_alert).show();
    }

    public static void buildAlertMessageNoGps(final Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?").setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface
                                                dialog, @SuppressWarnings("unused") final int id) {
                        context.startActivity(new
                                Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog,
                                @SuppressWarnings("unused") final int id) {
                dialog.cancel();
            }
        });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Convert image to base64 string
     *
     * @param lPtProfileImage
     * @return
     */
    public static String convertImageToBaseCode(ImageView lPtProfileImage) {
        String imageString;
        lPtProfileImage.buildDrawingCache();
        Bitmap bmappg = lPtProfileImage.getDrawingCache();
        ByteArrayOutputStream bospg = new ByteArrayOutputStream();
        bmappg.compress(CompressFormat.PNG, 100, bospg);
        byte[] bb = bospg.toByteArray();
        imageString = Base64.encodeToString(bb, Base64.DEFAULT);
        return imageString;
    }

    /**
     * Convert image to base64 string
     *
     * @param lPtProfileImage
     * @return
     */
    public static String convertImageToBaseCode(Bitmap lPtProfileImage) {
        String imageString;
        ByteArrayOutputStream bospg = new ByteArrayOutputStream();
        lPtProfileImage.compress(CompressFormat.PNG, 50, bospg);
        byte[] bb = bospg.toByteArray();
        imageString = Base64.encodeToString(bb, Base64.DEFAULT);
        return imageString;
    }

    public static void setGroupId(String groupId) {
        groupID = groupId;
    }

    public static String getGroupId() {
        return groupID;
    }


}
