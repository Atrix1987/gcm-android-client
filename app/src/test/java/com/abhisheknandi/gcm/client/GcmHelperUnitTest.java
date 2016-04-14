package com.abhisheknandi.gcm.client;

import android.content.Context;
import android.content.SharedPreferences;

import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class GcmHelperUnitTest {

  @Mock
  Context mMockContext;

  @Mock
  SharedPreferences mMockSharedPreferences;

  @Test
  public void testAuthorizedEntity() throws Exception{
    GcmHelper.getInstance().setAuthorizedEntity("12345678");
    assertEquals(GcmHelper.getInstance().getAuthorizedEntity(), "12345678");
  }

  @Test(expected=IllegalArgumentException.class)
  public void testInitWithNoAuthorizedEntity(){
    GcmHelper.getInstance().init(mMockContext);
  }
}