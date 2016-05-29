/**
 * Copyright 2016 Rafael Sanchez Fuentes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author: Rafael Sanchez Fuentes rafaelsf80 at gmail dot com
 */

package es.rafaelsf80.apps.fcm.work;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class BootStart extends BroadcastReceiver{

    private static final String TAG = "BootStart";

    @Override
    public void onReceive(Context context, Intent i) {

        Intent intent;
        Log.d(TAG, "BOOT_COMPLETED intent received. Starting service ...");

        // check Play Services before launching RegistrationIntentService
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(context);
        if (resultCode == ConnectionResult.SUCCESS) {
            intent = new Intent(context, RegistrationIntentService.class);
            context.startService(intent);
        } else
            Log.i(TAG, "Error in checking PlayServices.");
    }
}
