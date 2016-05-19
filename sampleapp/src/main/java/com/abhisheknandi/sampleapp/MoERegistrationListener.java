package com.abhisheknandi.sampleapp;

import android.content.Context;

import com.gcm.client.GcmHelper;
import com.moengage.push.PushManager;

/**
 * Created by abhisheknandi on 14/04/16.
 */
class MoERegistrationListener implements GcmHelper.GcmRegistrationListener{
  @Override
  public void onTokenAvailable(Context context, String token, boolean updated) {
    PushManager.getInstance(context).refreshToken(context, token);
  }

  @Override
  public void onTokenDeleted(Context context) {

  }
}
