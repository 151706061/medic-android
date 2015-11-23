package org.medicmobile.webapp.mobile;

import android.location.*;
import android.webkit.*;

import com.google.android.gms.common.api.*;
import com.google.android.gms.common.api.GoogleApiClient.*;
import com.google.android.gms.location.*;

import org.json.*;

public class MedicAndroidJavascript {
	private GoogleApiClient apiClient;
	private SoundAlert soundAlert;

	public void setApiClient(GoogleApiClient apiClient) {
		this.apiClient = apiClient;
	}

	public void setSoundAlert(SoundAlert soundAlert) {
		this.soundAlert = soundAlert;
	}

	@JavascriptInterface
	public int getAppVersion() {
		return BuildConfig.VERSION_CODE;
	}

	@JavascriptInterface
	public void playAlert() {
		if(soundAlert != null) soundAlert.trigger();
	}

	@JavascriptInterface
	public String getLocation() {
		try {
			if(apiClient == null) return jsonError("apiClient not initialised.");

			LocationAvailability locAvailability = LocationServices.FusedLocationApi.getLocationAvailability(apiClient);

			if(locAvailability == null) return jsonError("Location availability not supplied by API.");

			if(!locAvailability.isLocationAvailable()) {
				return jsonError("Location not available.");
			}

			Location loc = LocationServices.FusedLocationApi.getLastLocation(apiClient);
			if(loc == null) return jsonError("Location not reported by API.");

			try {
				return new JSONObject()
						.put("lat", loc.getLatitude())
						.put("long", loc.getLongitude())
						.toString();
			} catch(Throwable t) {
				try {
					return new JSONObject()
							.put("error", true)
							.put("cause", t)
							.toString();
				} catch(JSONException ex) {
					return jsonError("Problem creating error description.");
				}
			}
		} catch(Throwable t) {
			return jsonError(t.getClass() + ": " + t.getMessage());
		}
	}

	private static String jsonError(String cause) {
		return "{ \"error\": true, \"cause\":\"" +
				jsonEscape(cause) +
				"\" }";
	}

	private static String jsonEscape(String s) {
		return s.replaceAll("\"", "'");
	}
}

