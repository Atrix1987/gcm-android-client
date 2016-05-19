package com.abhisheknandi.sampleapp;

import android.content.Context;
import android.os.Bundle;

import com.gcm.client.GcmHelper;
import com.moengage.push.MoEngageNotificationUtils;
import com.moengage.push.PushManager;

class MoEMessageListener implements GcmHelper.GcmMessageListener{
  @Override
  public void onMessageReceived(Context context, String from, Bundle data) {
    PushManager.getInstance(context).messageReceived(context, data);
  }

  @Override
  public boolean canHandleMessage(Bundle data) {
    return MoEngageNotificationUtils.isFromMoEngagePlatform(data);
  }
}
