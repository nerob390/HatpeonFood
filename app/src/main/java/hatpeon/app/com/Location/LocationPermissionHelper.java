package hatpeon.app.com.Location;


import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.RequiresApi;

public final class LocationPermissionHelper {
    private static final int LOCATION_CODE = 123;
    private static final String LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION;
   // private static final String ActivityRecognition = Manifest.permission.ACTIVITY_RECOGNITION;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean hasLocationPermission(Activity activity){
        return activity.checkSelfPermission(LOCATION_PERMISSION) == PackageManager.PERMISSION_GRANTED;
    }
/*    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean hasActivityPermission(Activity activity){
        return activity.checkSelfPermission(ActivityRecognition)==PackageManager.PERMISSION_GRANTED;
    }*/

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void requestLocationPermission(Activity activity){
        activity.requestPermissions(new String[]{LOCATION_PERMISSION},LOCATION_CODE);
    }
/*    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void requestActivityPermission(Activity activity){
        activity.requestPermissions(new String[]{ActivityRecognition},1);
    }*/
}
