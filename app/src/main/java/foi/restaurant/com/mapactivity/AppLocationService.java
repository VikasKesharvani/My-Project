package foi.restaurant.com.mapactivity;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;

public class AppLocationService extends Service implements LocationListener {

	protected LocationManager mLocationManager;
	Location mLocation;

	private static final long MIN_DISTANCE_FOR_UPDATE = 10;
	private static final long MIN_TIME_FOR_UPDATE = 1000 * 60 * 2;

	public AppLocationService(Context context) {
		mLocationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
	}

	public Location getLocation(String provider) {
		if (mLocationManager.isProviderEnabled(provider)) {
			if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				// TODO: Consider calling
				//    ActivityCompat#requestPermissions
				// here to request the missing permissions, and then overriding
				//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
				//                                          int[] grantResults)
				// to handle the case where the user grants the permission. See the documentation
				// for ActivityCompat#requestPermissions for more details.
				//return TODO;
			}
			mLocationManager.requestLocationUpdates(provider, MIN_TIME_FOR_UPDATE, MIN_DISTANCE_FOR_UPDATE, this);
			if (mLocationManager != null) {
				mLocation = mLocationManager.getLastKnownLocation(provider);
				return mLocation;
			}
		}
		return null;
	}

	@Override
	public void onLocationChanged(Location location) {
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

}
