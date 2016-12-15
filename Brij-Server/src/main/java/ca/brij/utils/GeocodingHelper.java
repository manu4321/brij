package ca.brij.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;

@Component
public class GeocodingHelper {

	public ApplicationProperties properties;
	
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public GeoApiContext context;
	
	@Autowired
	public GeocodingHelper(ApplicationProperties properties){
		this.properties = properties;
		context = new GeoApiContext().setApiKey(properties.getGoogleKey());
	}
	
	public LatLng getLocationFromAddress(String address){
		System.out.println(properties.getGoogleKey());
		GeoApiContext context = new GeoApiContext().setApiKey(properties.getGoogleKey());
		GeocodingResult[] results = {};
		try {
			results = GeocodingApi.geocode(context,
			    address).await();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		if(results.length > 0){
			return results[0].geometry.location;

		}else{
			return null;
		}
	}

	
}
