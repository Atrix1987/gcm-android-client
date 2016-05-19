package com.abhisheknandi.sampleapp;

import android.provider.Settings;
import android.support.multidex.MultiDexApplication;

import com.gcm.library.GcmHelper;
import com.helpshift.All;
import com.helpshift.Core;
import com.localytics.android.LocalyticsActivityLifecycleCallbacks;
import com.mixpanel.android.mpmetrics.MixpanelAPI;


public class App extends MultiDexApplication {

  public MixpanelAPI getMixpanelAPI(){
    return mixpanel;
  }

  public static App getInstance(){
    return _THIS;
  }

  private static App _THIS;

  private MixpanelAPI mixpanel;

  @Override
  public void onCreate() {
    super.onCreate();
    _THIS = this;
    GcmHelper.getInstance()
            .setAuthorizedEntity("889908101771")
            .addRegistrationCallback(getApplicationContext(), new HelpshiftRegistrationListener(), true)
            .addRegistrationCallback(getApplicationContext(), new MixpanelRegistrationListener(), true)
            .addRegistrationCallback(getApplicationContext(), new LocalyticsRegistrationListener(), true)
            .addRegistrationCallback(getApplicationContext(), new MoERegistrationListener(), true)
            .addOnMessageReceivedCallback(new HelpShiftMessageReceiver())
            .addOnMessageReceivedCallback(new MixpanelMessageListener())
            .addOnMessageReceivedCallback(new LocalyticsMessageListener())
            .addOnMessageReceivedCallback(new MoEMessageListener())
            .init(getApplicationContext());

    String projectToken = "6a883c0a07fd8efd5a19daa9c323d37e";
    mixpanel = MixpanelAPI.getInstance(this, projectToken);

    mixpanel.getPeople().identify(Settings.Secure.getString(getContentResolver(),
            Settings.Secure.ANDROID_ID));

    Core.init(All.getInstance());
    Core.install(this, "f861cd29855b46bd640ba6626faf0268", "push.helpshift.com", "push_platform_20160412113439725-49d43dd69da15ed");

    registerActivityLifecycleCallbacks(
            new LocalyticsActivityLifecycleCallbacks(this));

  }
}
