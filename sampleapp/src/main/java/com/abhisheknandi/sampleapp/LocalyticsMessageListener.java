package com.abhisheknandi.sampleapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.gcm.client.GcmHelper;
import com.localytics.android.Localytics;
import com.localytics.android.PushTrackingActivity;

import org.json.JSONException;
import org.json.JSONObject;


class LocalyticsMessageListener implements GcmHelper.GcmMessageListener {
  @Override
  public void onMessageReceived(Context context, String from, Bundle data) {
    Intent dummyIntent = new Intent();
    dummyIntent.putExtras(data);
    Localytics.handlePushNotificationReceived(dummyIntent);

    String message = data.getString("message");
    if (!TextUtils.isEmpty(message)) {
      Intent trackingIntent = new Intent(context, PushTrackingActivity.class);
      trackingIntent.putExtras(data); // add all extras from received intent

      int requestCode = getRequestCode(data);
      PendingIntent contentIntent = PendingIntent.getActivity(context, requestCode, trackingIntent, PendingIntent.FLAG_UPDATE_CURRENT);

      NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
              .setSmallIcon(R.mipmap.ic_launcher)
              .setContentTitle(context.getString(R.string.app_name))
              .setContentText(message)
              .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
              .setContentIntent(contentIntent)
              .setDefaults(Notification.DEFAULT_ALL)
              .setAutoCancel(true);

      NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
      notificationManager.notify(requestCode, builder.build());
    }
  }

  @Override
  public boolean canHandleMessage(Bundle data) {
    return data.containsKey("ll");
  }

  /**
   * Get a unique requestCode so we don't override other unopened pushes. The Localytics SDK
   * uses the campaign ID (ca) within the 'll' JSON string extra. Use that value if it exists.
   */
  private int getRequestCode(Bundle extras) {
    int requestCode = 1;
    if (extras != null && extras.containsKey("ll")) {
      try {
        JSONObject llObject = new JSONObject(extras.getString("ll"));
        requestCode = llObject.getInt("ca");
      } catch (JSONException e) {
      }
    }
    return requestCode;
  }
}
