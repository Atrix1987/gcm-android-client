package com.abhisheknandi.sampleapp;

import android.content.Context;
import android.os.Bundle;

import com.gcm.client.GcmHelper;
import com.helpshift.Core;

class HelpShiftMessageReceiver implements GcmHelper.GcmMessageListener {

  @Override
  public void onMessageReceived(Context context, String from, Bundle data) {
    Core.handlePush(context, data);
  }

  @Override
  public boolean canHandleMessage(Bundle data) {
    return data.containsKey(KEY_ORIGIN) && ORIGIN_HELPSHIFT.equals(data.getString(KEY_ORIGIN));
  }

  private static final String KEY_ORIGIN = "origin";
  private static final String ORIGIN_HELPSHIFT = "helpshift";
}
