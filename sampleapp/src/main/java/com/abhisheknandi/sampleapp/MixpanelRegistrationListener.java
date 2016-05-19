package com.abhisheknandi.sampleapp;

import android.content.Context;
import android.util.Log;

import com.gcm.client.GcmHelper;
import com.mixpanel.android.mpmetrics.MixpanelAPI;


class MixpanelRegistrationListener implements GcmHelper.GcmRegistrationListener {

  @Override
  public void onTokenAvailable(Context context, String token, boolean updated) {
    App.getInstance().getMixpanelAPI().getPeople().setPushRegistrationId(token);
    Log.d("MP", "setting push token");
  }

  @Override
  public void onTokenDeleted(Context context) {
    MixpanelAPI.People people = App.getInstance().getMixpanelAPI().getPeople();
    people.clearPushRegistrationId();
  }
}
