# MapIt - demo app 

This is an app for learning and demonstration purposes.

Set Up - Services & Logins
--------------------------------------
<dl>
  <dt>Uses Google Maps Android API v2</dt>
  <dd>[1] https://developers.google.com/maps/documentation/android/</dd>
  <dd>[2] http://developer.android.com/google/play-services/maps.html</dd>
  <dt>Uses GeoNames.org Webservice</dt>
  <dd>[3] http://www.geonames.org/export/</dd>
</dl>

Set Up - File Modifications
---------------------------
<dl>
  <dt>`androidmanifest_template.xml`</dt>
  <dd>rename to `AndroidManifest.xml`</dd>
  <dd>Must Obtain Google Android Maps v2 API Key, instructions in links [1] above</dd>
  <dd>Since Google Maps Android API v2 is part of Google Play Services Platform, you must follow instructions at link [2] above, including the "set up" and "Getting Started guide" links found on that page.</dd>
  <dt>`res/values/strings_template.xml`</dt>
  <dd>rename to `res/values/strings.xml`</dd>
  <dd>Must acquire a username, then enable the service on your account (must login to enable, it is off by default), instructions in link [3] above.</dd>
</dl>