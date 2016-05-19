package com.abhisheknandi.sampleapp;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.gcm.client.GcmHelper;

class MixpanelMessageListener implements GcmHelper.GcmMessageListener {
  /**
   * Called when a downstream GCM message is received. <strong>Runs on UI Thread<strong/>
   *
   * @param context An instance of the application {@link Context}
   * @param from SenderID of the sender.
   * @param data Data bundle containing message data as key/value pairs.
   */
  @Override public void onMessageReceived(Context context, String from, Bundle data) {
    String notificationMesg = data.getString("mp_message");
    Log.d("MP", notificationMesg);
  }

  @Override
  public boolean canHandleMessage(Bundle data) {
    return data.containsKey("mp_message");
  }
}
