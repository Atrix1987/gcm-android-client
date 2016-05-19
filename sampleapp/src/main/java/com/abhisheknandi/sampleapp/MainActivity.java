package com.abhisheknandi.sampleapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.appboy.Appboy;
import com.gcm.client.RegistrationIntentService;
import com.localytics.android.Localytics;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        Snackbar.make(view, "Click to send an email to the maker", Snackbar.LENGTH_LONG)
            .setAction("EMAIL", new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "iwizard.abhi@gmail.com", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Regarding:GCMLibrary");
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
              }
            })
            .show();
      }
    });

    receiver = new RegistrationCompletedReceiver();
    filter = new IntentFilter(RegistrationIntentService.REGISTRATION_COMPLETE);
    App.getInstance().getMixpanelAPI().track("App_Opened");
    App.getInstance().getMixpanelAPI().getPeople().set("last_active", new Date());
    Localytics.tagEvent("App_Opened");

  }

  @Override protected void onStart() {
    super.onStart();
    LocalBroadcastManager.getInstance(this).registerReceiver(receiver,filter);
    Appboy.getInstance(MainActivity.this).openSession(MainActivity.this);
  }

  @Override protected void onStop() {
    super.onStop();
    LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    Appboy.getInstance(MainActivity.this).closeSession(MainActivity.this);
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  private RegistrationCompletedReceiver receiver;
  private IntentFilter filter;

  private class RegistrationCompletedReceiver extends BroadcastReceiver{

    @Override public void onReceive(Context context, Intent intent) {
      Toast.makeText(MainActivity.this, "GCM registration completed", Toast.LENGTH_LONG).show();
    }
  }
}
