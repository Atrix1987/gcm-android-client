package com.abhisheknandi.sampleapp;

import android.content.Context;
import android.os.Bundle;

import com.gcm.client.GcmHelper;

/**
 * Created by abhisheknandi on 27/04/16.
 */
public class AppboyMessageListener implements GcmHelper.GcmMessageListener {
  @Override
  public void onMessageReceived(Context context, String from, Bundle data) {

  }

  @Override
  public boolean canHandleMessage(Bundle data) {
    return false;
  }
}
