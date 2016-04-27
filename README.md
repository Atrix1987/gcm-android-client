![GCM Lib](https://raw.githubusercontent.com/Atrix1987/gcm-android-client/gcm_helper_logo.png)
# Android GCM Library
A library which helps you register Google Cloud Messaging tokens and listen for GCM messages. It can be used to attach multiple providers and seamlessly deliver data and token to all the listeners.
This library is very helpful for those you use multiple push notification provider.

[![Build Status](https://travis-ci.org/Atrix1987/gcm-android-client.svg?branch=master)](https://travis-ci.org/Atrix1987/gcm-android-client)  [![Download](https://api.bintray.com/packages/atrix1987/maven/gcm-lib/images/download.svg) ](https://bintray.com/atrix1987/maven/gcm-lib/_latestVersion) [![Coverage Status](https://coveralls.io/repos/github/Atrix1987/gcm-android-client/badge.svg?branch=master)](https://coveralls.io/github/Atrix1987/gcm-android-client?branch=master)

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

Now in your application class `onCreate` method or in your launcher activity's onCreate method do the following:

```java
GcmHelper.getInstance()
        .setAuthorizedEntity("889908101771") /*This is the sender ID i.e., the Google Project Number*/
        .addRegistrationCallback(getApplicationContext(), new CustomRegistrationListener(), true)
        .addOnMessageReceivedCallback(new CustomMessageReceivedListener())
        .init(getApplication());

```


## Minimum Requirements Google Play Services GCM library 7.5

As seen [here](https://developers.google.com/cloud-messaging/registration)
```
GCM register() is deprecated starting May 28, 2015. New app development should use the Instance ID API to handle the creation, rotation, and updating of registration tokens.
```



## License
GCMLib is [Apache Licence](./LICENSE).

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
