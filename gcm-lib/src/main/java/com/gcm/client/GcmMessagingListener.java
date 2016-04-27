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

import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.gcm.GcmListenerService;


public class GcmMessagingListener extends GcmListenerService {

  /**
   * Called when message is received.
   *
   * @param from SenderID of the sender.
   * @param data Data bundle containing message data as key/value pairs.
   *             For Set of keys use data.keySet().
   */
  @Override
  public void onMessageReceived(String from, Bundle data) {
    if(GcmHelper.DEBUG_ENABLED){
      Log.d(GcmHelper.TAG, "GcmMessagingListener From: " + from);
      GcmHelper.printBundle(data);
    }
    GcmHelper.getInstance().messageReceived(getApplicationContext(), from, data);
  }
}
