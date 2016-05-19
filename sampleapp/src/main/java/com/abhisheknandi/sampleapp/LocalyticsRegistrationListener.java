package com.abhisheknandi.sampleapp;

import android.content.Context;

import com.gcm.client.GcmHelper;
import com.localytics.android.Localytics;


class LocalyticsRegistrationListener implements GcmHelper.GcmRegistrationListener {
  @Override
  public void onTokenAvailable(Context context, String token, boolean updated) {
    Localytics.setPushRegistrationId(token);
  }

  @Override
  public void onTokenDeleted(Context context) {

  }
}
