package com.abhisheknandi.gcm.client;

import android.content.Context;

import com.gcm.client.GcmHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.when;


public class GcmHelperTest {

  @Mock
  GoogleApiAvailability apiAvailability;

  @Mock
  Context context;

  @Before
  public void setup() {  }

  @Test
  public void getInstanceTest(){
    assertNotNull(GcmHelper.getInstance());
  }

  @Test
  public void setAuthorizedEntityTest(){
    GcmHelper.getInstance().setAuthorizedEntity("12345678910");
    assertEquals(GcmHelper.getInstance().getAuthorizedEntity(), "12345678910");
  }

}
