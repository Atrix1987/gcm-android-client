package com.abhisheknandi.sampleapp;

import android.content.Context;

import com.appboy.Appboy;
import com.gcm.client.GcmHelper;

/**
 * Created by abhisheknandi on 27/04/16.
 */
public class AppboyRegistrationListener implements GcmHelper.GcmRegistrationListener {
  @Override
  public void onTokenAvailable(Context context, String token, boolean updated) {
    Appboy.getInstance(context).registerAppboyPushMessages(token);
  }

  @Override
  public void onTokenDeleted(Context context) {
    Appboy.getInstance(context).registerAppboyPushMessages(null);
  }
}
