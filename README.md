![GCM Lib](https://raw.githubusercontent.com/Atrix1987/gcm-android-client/master/gcm_helper_logo.png)
# Android GCM Library
A library which helps you register Google Cloud Messaging tokens and listen for GCM messages. It can be used to attach multiple providers and seamlessly deliver data and token to all the listeners.
This library is very helpful for those you use multiple push notification provider.

[![Build Status](https://travis-ci.org/Atrix1987/gcm-android-client.svg?branch=master)](https://travis-ci.org/Atrix1987/gcm-android-client)  [![Download](https://api.bintray.com/packages/atrix1987/maven/gcm-lib/images/download.svg) ](https://bintray.com/atrix1987/maven/gcm-lib/_latestVersion) [![Coverage Status](https://coveralls.io/repos/github/Atrix1987/gcm-android-client/badge.svg?branch=master)](https://coveralls.io/github/Atrix1987/gcm-android-client?branch=master) [![Javadoc](https://img.shields.io/badge/javadoc-OK-blue.svg)](https://atrix1987.github.io/gcm-lib/javadoc/1.0.1/) [![Gitter](https://badges.gitter.im/Atrix1987/gcm-android-client.svg)](https://gitter.im/Atrix1987/gcm-android-client?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)

### Salient Features
 * Takes care of GCM Registration
 * Token rotation, updates
 * Provides callbacks for listening to registration changes
 * Provides callbacks for GCM message callbacks

For example, A lot of apps use multiple push providers. This library helps them to maintain a clean code and also loosely couple the providers still providing support for all

## How to use ?

Implementing a Registration Listener

```java

public class CustomRegistrationListener implements GcmHelper.GcmRegistrationListener{

  @Override public void onTokenAvailable(Context context, String token, boolean updated) {
    //TODO do something with the token
  }


  @Override public void onTokenDeleted(Context context) {
    //TODO delete the token
  }
}
```

Implementing the Message Listener

```java
public class CustomMessageReceivedListener implements GcmHelper.GcmMessageListener {

  @Override public void onMessageReceived(Context context, String from, Bundle data) {
    //TODO start your service or do something else
  }

  @Override public boolean canHandleMessage(Bundle data){
    //TODO check the bundle to see if the listener can handle payload
    return false;
  }
}
```

Now in your application class `onCreate` method following:

```java
GcmHelper.getInstance()
        .setAuthorizedEntity("889908101771")
        .addRegistrationCallback(getApplicationContext(), new CustomRegistrationListener(), true)
        .addOnMessageReceivedCallback(new CustomMessageReceivedListener())
        .init(getApplication());

```

`setAuthorizedEntity` is the sender id or the Google Project number

## Minimum Requirements Google Play Services GCM library 7.5

As seen [here](https://developers.google.com/cloud-messaging/registration)
```
GCM register() is deprecated starting May 28, 2015. New app development should use the Instance ID API to handle the creation, rotation, and updating of registration tokens.
```



## License
GCMLib is [Apache Licence](./LICENSE).

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
