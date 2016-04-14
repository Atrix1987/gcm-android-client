/**
 * Copyright 2016 Abhishek Nandi. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gcm.client;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import java.io.IOException;
import java.util.ArrayList;

public class RegistrationIntentService extends IntentService {
  public static final String REGISTRATION_COMPLETE = "com.gcm.client.REGISTRATION.COMPLETE";

  public static final String ACTION_UNSUBSCRIBE = "com.gcm.client.UNSUBSCRIBE";
  public static final String ACTION_SUBSCRIBE = "com.gcm.client.SUBSCRIBE";
  public static final String ACTION_REGISTER = "com.gcm.client.REGISTER";
  public static final String ACTION_UNREGISTER = "com.gcm.client.UNREGISTER";

  static final String EXTRA_TOPIC_LIST = "topiclist";

  public RegistrationIntentService() {
    super("RegistrationIntentService");
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    try {
      if (null != intent) {
        String action  = intent.getAction();
        String senderId = GcmHelper.getInstance().getAuthorizedEntity();

        InstanceID instanceID = InstanceID.getInstance(this);
        if (ACTION_REGISTER.equals(action)) {
          String token = registerForToken(instanceID, senderId);
          //subscribe topics
          ArrayList<String> topics = GcmHelper.getInstance().getSubscribedTopics();
          if (null != topics && topics.isEmpty()){
            String[] stringArray = new String[topics.size()];
            // Subscribe to topic channels
            subscribeTopics(token, topics.toArray(stringArray));
          }
          // Notify UI that registration has completed, so the progress indicator can be hidden.
          Intent registrationComplete = new Intent(REGISTRATION_COMPLETE);
          LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
          if(GcmHelper.DEBUG_ENABLED)Log.d(GcmHelper.TAG, "RegistrationIntentService: registration completed");
          return;
        }else if (ACTION_UNREGISTER.equals(action)) {
          unRegisterToken(instanceID, senderId);
          if(GcmHelper.DEBUG_ENABLED)Log.d(GcmHelper.TAG, "RegistrationIntentService: successfully unregistered");
          return;
        }
        if(intent.hasExtra(EXTRA_TOPIC_LIST)){
          final String topics[] = intent.getStringArrayExtra(EXTRA_TOPIC_LIST);
          String pushToken = GcmHelper.getInstance().getPushToken(getApplicationContext());
          if (TextUtils.isEmpty(pushToken)){
            return;
          }
          if (ACTION_UNSUBSCRIBE.equals(action) ){
            unsubscribeTopics(pushToken, topics);
            if(GcmHelper.DEBUG_ENABLED)Log.d(GcmHelper.TAG, "RegistrationIntentService: unsubscribe completed");
          }else if( ACTION_SUBSCRIBE.equals(action)){
            subscribeTopics(pushToken, topics);
            if(GcmHelper.DEBUG_ENABLED)Log.d(GcmHelper.TAG, "RegistrationIntentService: subscription completed");
          }
        }
      }
    } catch (Exception e) {
      if(GcmHelper.DEBUG_ENABLED)Log.e(GcmHelper.TAG, "PushRegistrationService: Failed to complete token refresh", e);
    }
  }

  /**
   * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
   *
   * @param token GCM token
   * @param topics a collection of topics which the app wants to subscribe
   * @throws IOException if unable to reach the GCM PubSub service
   */
  private void subscribeTopics(String token, final String [] topics) throws IOException {
    GcmPubSub pubSub = GcmPubSub.getInstance(this);
    for (String topic : topics) {
      pubSub.subscribe(token, topic, null);
    }
  }

  /**
   * Un-Subscribe to any GCM topics of interest.
   *
   * @param token GCM token
   * @param topics The list of topics to un-subscribe from
   * @throws IOException if unable to reach the GCM PubSub service
   */
  private void unsubscribeTopics(String token, final String[] topics) throws IOException{
    GcmPubSub pubSub = GcmPubSub.getInstance(this);
    for (String topic : topics) {
      pubSub.unsubscribe(token, topic);
    }
  }

  /**
   * Unregister/delete token
   *
   * @param instanceID instance of {@link InstanceID} associated with the sender Id
   * @param senderId Sender ID for which the token has to be deleted
   */
  private void unRegisterToken(InstanceID instanceID, String senderId) throws IOException {
    instanceID.deleteToken(senderId, GoogleCloudMessaging.INSTANCE_ID_SCOPE);
    GcmHelper.getInstance().onTokenDeleted(getApplicationContext());
  }

  /**
   * Register for token for the specified sender Id
   *
   * @param instanceID instance of {@link InstanceID} associated with the sender Id
   * @param senderId Sender ID for which the token has to be deleted
   */
  private String registerForToken(InstanceID instanceID, String senderId) throws IOException {
    String token = instanceID.getToken(senderId, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
    if(GcmHelper.DEBUG_ENABLED)Log.i(GcmHelper.TAG, "PushRegistrationService: GCM Registration Token: " + token);
    //Notify the helper that the push token has been updated
    GcmHelper.getInstance().tokenUpdated(getApplicationContext(), token);
    return token;
  }

}

