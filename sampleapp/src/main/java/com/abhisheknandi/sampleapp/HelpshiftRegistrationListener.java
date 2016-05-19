package com.abhisheknandi.sampleapp;

import android.content.Context;
import android.util.Log;

import com.gcm.client.GcmHelper;
import com.helpshift.Core;


class HelpshiftRegistrationListener implements GcmHelper.GcmRegistrationListener {
  /**
   * Called when GCM tokens are available. If the callback is added to the helper with a STICKY
   * request then it would be immediately triggered with the value of updated as false
   *
   * @param context An instance of the Application context
   * @param token The value of the token.
   * @param updated A boolean denotes whether this token was recently updated or is an existing
   */
  @Override public void onTokenAvailable(Context context, String token, boolean updated) {
    Core.registerDeviceToken(context, token);
    Log.d("HSRL", "New token available, setting it to helpshift");
  }

  /**
   * Called when GCM tokens are deleted.
   *
   * @param context An instance of the application {@link Context}
   */
  @Override public void onTokenDeleted(Context context) {
    //TODO delete the token
  }
}
