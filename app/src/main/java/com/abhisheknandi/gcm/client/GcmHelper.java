/**
 * Copyright 2016 Abhishek Nandi. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.abhisheknandi.gcm.client;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Google Cloud Messaging Helper class
 * */
public final class GcmHelper {

  public static final String TAG = "GcmLibrary";
  public static boolean DEBUG_ENABLED = false;
  /**
   * The current singleton instance of the Helper class which will be used
   */
  private static GcmHelper _INSTANCE;
  /**
   * A list of all topics registered by the app
   */
  private ArrayList<String> topics;
  /**
   * The current push token
   */
  private String pushToken;
  /**
   * A Collection of all the message callbacks which are registered by the app
   */
  private ConcurrentLinkedQueue<GcmMessageListener> messageReceivedCallbacks;
  /**
   * A collection of all the registration listeners the app has registered
   */
  private ConcurrentLinkedQueue<GcmRegistrationListener> registrationCallbacks;
  /**
   * GCM SenderID which will be used to register for push tokens
   */
  private String senderID;
  /**
   * The preference file name
   */
  final String PREF_NAME = "pref_gcm";
  /**
   * The preference key which stores the push token if any
   */
  private final String PREF_KEY_TOKEN = "token";
  /**
   * The preference key which has the list of all topics the app has subscribed to
   */
  private final String PREF_KEY_SUBSCRIPTION = "subscribed";

  /**
   * Retrieve an instance of the Google Cloud Messaging Helper class
   */
  public static GcmHelper getInstance() {
    synchronized (GcmHelper.class) {
      if (null == _INSTANCE) {
        _INSTANCE = new GcmHelper();
      }
    }
    return _INSTANCE;
  }

  private GcmHelper() {
    //intentionally made private
  }

  //Denotes whether the helper was initialized or not
  private boolean initialized = false;

  /**
   * Initialize the GcmHelper class. To be called from the application class onCreate or from the
   * onCreate of the main activity
   *
   * @param context An instance of the Application Context
   */
  public synchronized void init(@NonNull Context context) {
    if(!isGooglePlayservicesAvailable(context)){
      throw new IllegalArgumentException("Not using the recommended Play Services version");
    }
    //get the GCM sender id from strings.xml
    if (TextUtils.isEmpty(senderID)) {
      int id =
              context.getResources().getIdentifier("gcm_authorized_entity", "string", context.getPackageName());
      senderID = context.getResources().getString(id);
    }

    if(TextUtils.isEmpty(senderID)){
      throw new IllegalArgumentException("No SenderId provided!! Cannot instantiate");
    }

    SharedPreferences localPref = getSharedPreference(context);
    //get topics array
    if (localPref.contains(PREF_KEY_SUBSCRIPTION)) {
      String subscription = localPref.getString(PREF_KEY_SUBSCRIPTION, null);
      if (!TextUtils.isEmpty(subscription)) {
        try {
          JSONArray array = new JSONArray(subscription);
          int length = array.length();
          topics = new ArrayList<>();
          for (int i = 0; i < length; i++) {
            topics.add(array.getString(i));
          }
        } catch (JSONException ignored) {
          if (DEBUG_ENABLED) Log.e(TAG, "init: while processing subscription list", ignored);
        }
      }
    }

    boolean registrationReq = true;
    if (localPref.contains(PREF_KEY_TOKEN)) {
      pushToken = localPref.getString(PREF_KEY_TOKEN, null);
      if(!TextUtils.isEmpty(pushToken)){
        registrationReq = false;
        //don't pass token if already present
      }
    }
    if(registrationReq){
      //register for push token
      Intent registration = new Intent(context, RegistrationIntentService.class);
      registration.setAction(RegistrationIntentService.ACTION_REGISTER);
      context.startService(registration);
    }
    //check if debug build and enable DEBUG MODE
    DEBUG_ENABLED = (0 != (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));
    initialized = true;
  }

  /**
   * Get the shared preference used as local storage
   *
   * @param context An instance of the application {@link Context}
   */
  private SharedPreferences getSharedPreference(@NonNull Context context) {
    return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
  }

  /**
   * Store the push token which was generated from the app INSTANCE
   *
   * @param context An instance of the application {@link Context}
   * @param token   The token which needs to be stored
   */
  private void storedPushToken(@NonNull Context context,@NonNull String token) {
    SharedPreferences pref = getSharedPreference(context);
    pref.edit().putString(PREF_KEY_TOKEN, token).apply();
  }

  /**
   * Store the subscribed topics list
   *
   * @param context An instance of the application {@link Context}
   */
  private void saveSubscibedTopics(@NonNull Context context) {
    if (null == topics || topics.isEmpty()) return;
    SharedPreferences pref = getSharedPreference(context);
    JSONArray subscription = new JSONArray(topics);
    pref.edit().putString(PREF_KEY_SUBSCRIPTION, subscription.toString()).apply();
  }

  /**
   * Checks if a compatible version of Google play services is available or not.
   *
   * @param context An instance of the application {@link Context}
   * @return true if a compatible version of play services is available
   */
  private static boolean isGooglePlayservicesAvailable(@NonNull Context context) {
    int status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);
    return status == ConnectionResult.SUCCESS;
  }

  /**
   * Get a list of all subscribed topics. Might be null if no topics have been subscribed to
   *
   * @return a list of all subscribed topics
   */
  public ArrayList<String> getSubscribedTopics() {
    return topics;
  }

  /**
   * Subscribe to a list of topics.
   *
   * @param context An instance of the application {@link Context}
   * @return true if it is a new topic
   */
  public boolean subscribeTopic(@NonNull Context context,@NonNull String[] newTopics) {
    if (!initialized) init(context);
    if (newTopics.length == 0) return false;
    if (null == topics) {
      topics = new ArrayList<>();
    }
    for (String topic : newTopics) {
      if (topics.contains(topic)) {
        return false;
      }
      topics.add(topic);
    }
    saveSubscibedTopics(context);
    Intent intent = new Intent(context, RegistrationIntentService.class);
    intent.setAction(RegistrationIntentService.ACTION_UNSUBSCRIBE);
    intent.putExtra(RegistrationIntentService.EXTRA_TOPIC_LIST, newTopics);
    context.startService(intent);
    return true;
  }

  /**
   * Un-subscribe from specific topics
   *
   * @param context      An instance of the application {@link Context}
   * @param removeTopics list of topics which needs to be removed from subscription
   */
  public boolean unSubscribeTopic(@NonNull Context context,@NonNull String[] removeTopics) {
    if (!initialized) init(context);
    if (removeTopics.length == 0) return false;
    for (String topic : removeTopics) {
      topics.remove(topic);
    }
    Intent intent = new Intent(context, RegistrationIntentService.class);
    intent.setAction(RegistrationIntentService.ACTION_UNSUBSCRIBE);
    intent.putExtra(RegistrationIntentService.EXTRA_TOPIC_LIST, removeTopics);
    context.startService(intent);
    return true;
  }

  /**
   * Get the GCM Sender ID
   *
   * @return the GCM sender ID
   */
  public String getAuthorizedEntity() {
    return senderID;
  }

  /**
   * Set the GCM Sender ID
   *
   * @param senderID the Google Cloud Messaging Sender ID which will be used for registering push
   *                 notifications
   */
  public GcmHelper setAuthorizedEntity(@NonNull String senderID) {
    this.senderID = senderID;
    return this;
  }

  /**
   * Retrieve the push token
   *
   * @param context An instance of the application context
   */
  public synchronized String getPushToken(Context context) {
    if (!initialized) init(context);
    return pushToken;
  }

  /**
   * Add a GCM Registration listener. This does not replace the existing listeners but adds to the list of listeners
   *
   * @param context              An instance of the application context
   * @param registrationListener The instance of {@link GcmRegistrationListener} which needs to be added
   * @param sticky               if true then token is passed even if it was generated earlier
   */
  public synchronized GcmHelper addRegistrationCallback(@NonNull Context context,
                                                        @NonNull GcmRegistrationListener registrationListener, boolean sticky) {
    if (null == registrationCallbacks) {
      registrationCallbacks = new ConcurrentLinkedQueue<>();
    }
    registrationCallbacks.add(registrationListener);
    if (sticky && !TextUtils.isEmpty(this.pushToken)) {
      registrationListener.onTokenAvailable(context, pushToken, false);
    }
    return this;
  }

  /**
   * Add a GCM Messaging listener. This listener will be triggered when a GCM payload is received
   *
   * @param messageReceivedListener The instance of {@link GcmMessageListener} which needs to be added
   */
  public synchronized GcmHelper addOnMessageReceivedCallback(
          @NonNull GcmMessageListener messageReceivedListener) {
    if (null == messageReceivedCallbacks) {
      messageReceivedCallbacks = new ConcurrentLinkedQueue<>();
    }
    messageReceivedCallbacks.add(messageReceivedListener);
    return this;
  }

  /**
   *
   */
  synchronized void tokenUpdated(Context context, String pushToken) {
    if (!TextUtils.isEmpty(pushToken) && !pushToken.equals(this.pushToken)) {
      //store in shared preference file
      storedPushToken(context, pushToken);
      this.pushToken = pushToken;
      if (null != registrationCallbacks && !registrationCallbacks.isEmpty()) {
        //trigger all callbacks to notify of push token change
        for (GcmRegistrationListener registrationListener : registrationCallbacks) {
          //exception in one listener should not affect the other one
          try {
            registrationListener.onTokenAvailable(context, pushToken, true);
          } catch (Exception e) {
            if (DEBUG_ENABLED) Log.e(TAG, "tokenUpdated:", e);
          }
        }
      }
    } else if (DEBUG_ENABLED) {
      Log.d(TAG, " No listeners registered for token refresh");
    }
  }

  /**
   * Delete the existing push token
   * @param context An instance of the application Context
   */
  public synchronized void deleteToken(Context context) {
    if (!TextUtils.isEmpty(pushToken)) {
      Intent registration = new Intent(context, RegistrationIntentService.class);
      registration.setAction(RegistrationIntentService.ACTION_UNREGISTER);
      context.startService(registration);

      SharedPreferences pref = getSharedPreference(context);
      pref.edit().remove(PREF_KEY_TOKEN).apply();
    }
  }

  /**
   * Iterates over {@link #registrationCallbacks} and calls
   *    {@link GcmRegistrationListener#deleteToken(Context)}
   * @param context An instance of the application Context
   */
  synchronized void onTokenDeleted(Context context) {
    this.pushToken = null;
    if (null != registrationCallbacks && !registrationCallbacks.isEmpty()) {
      //trigger all callbacks to notify of push token change
      for (GcmRegistrationListener registrationListener : registrationCallbacks) {
        //exception in one listener should not affect the other one
        try {
          registrationListener.onTokenDeleted(context);
        } catch (Exception e) {
          if (DEBUG_ENABLED) Log.e(TAG, "tokenUpdated:", e);
        }
      }
    }
  }

  /**
   * Iterates over {@link #messageReceivedCallbacks }
   * and invokes {@link GcmMessageListener#onMessageReceived(Context, String, Bundle)}
   * if {@link GcmMessageListener#canHandleMessage(Bundle)} returns true
   *
   * @param context An instance of the application context
   * @param from    SenderID of the sender
   * @param data    Data bundle containing message data as key/value pairs.
   *                For Set of keys use data.keySet().
   */
  synchronized void messageReceived(Context context, String from, Bundle data) {
    //trigger all callbacks to notify of push message received
    if (null != messageReceivedCallbacks && !messageReceivedCallbacks.isEmpty()) {
      for (GcmMessageListener messageListener : messageReceivedCallbacks) {
        //exception in one listener should not affect the other one
        try {
          if (messageListener.canHandleMessage(data)) {
            messageListener.onMessageReceived(context, from, data);
          }
        } catch (Exception e) {
          if (DEBUG_ENABLED) Log.e(TAG, "messageReceived", e);
        }
      }
    } else if (DEBUG_ENABLED) {
      Log.d(TAG, " No listeners registered for message callback");
    }
  }

  /**
   * A debugging method which helps print the entire bundle
   *
   * @param data instance of the {@link Bundle} which needs to be printed
   */
  static void printBundle(Bundle data) {
    try {
      if (null == data) {
        Log.d(TAG, "printBundle:No extras to log");
        return;
      }
      Set<String> ketSet = data.keySet();
      if (null == ketSet || ketSet.isEmpty()) return;
      Log.d(TAG, "------Start of bundle extras------");
      for (String key : ketSet) {
        Object obj = data.get(key);
        if (null != obj) {
          Log.d(TAG, "[ " + key + " = " + obj.toString() + " ]");
        }
      }
      Log.d(TAG, "-------End of bundle extras-------");
    } catch (Exception e) {
      if (DEBUG_ENABLED) Log.e(TAG, "printBundle", e);
    }
  }

  /**
   * Interface definition for a callback to be invoked when a Google Cloud Messaging tokens for the
   * app changes.
   */
  public interface GcmRegistrationListener {

    /**
     * Called when GCM tokens are available. If the callback is added to the helper with a STICKY
     * request then it would be immediately triggered with the value of updated as false
     *
     * @param context An instance of the Application context
     * @param token   The value of the token.
     * @param updated A boolean denotes whether this token was recently updated or is an existing
     *                token
     */
    void onTokenAvailable(Context context, String token, boolean updated);

    /**
     * Called when GCM tokens are deleted.
     *
     * @param context An instance of the application {@link Context}
     */
    void onTokenDeleted(Context context);
  }

  /**
   * Interface definition for a callback to be invoked when a downstream message is received from
   * the GCM server
   */
  public interface GcmMessageListener {

    /**
     * Called when a downstream GCM message is received. <strong>Runs on UI Thread<strong/>
     *
     * @param context An instance of the application {@link Context}
     * @param from    SenderID of the sender.
     * @param data    Data bundle containing message data as key/value pairs.
     *                For Set of keys use data.keySet().
     */
    void onMessageReceived(Context context, String from, Bundle data);

    /**
     * Tells whether the GCM payload can be handled by this listener
     *
     * @param data Data bundle containing message data as key/value pairs.
     */
    boolean canHandleMessage(Bundle data);
  }
}
