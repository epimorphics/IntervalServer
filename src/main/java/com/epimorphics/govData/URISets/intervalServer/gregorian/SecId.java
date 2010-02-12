package com.epimorphics.govData.URISets.intervalServer.gregorian;

import com.epimorphics.govData.URISets.intervalServer.util.GregorianOnlyCalendar;
import java.util.Locale;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;


@Path(URITemplate.SECOND_ID_STEM+URITemplate.SECOND_PATTERN)
public class SecId extends Id{

	@GET
	public Response redirector(
			@PathParam(YEAR_TOKEN)    int year, 
			@PathParam(MONTH_TOKEN)   int month,
			@PathParam(DAY_TOKEN)     int day,
			@PathParam(HOUR_TOKEN)    int hour,
			@PathParam(MINUTE_TOKEN)  int min,
			@PathParam(SECOND_TOKEN)  int sec){
		
		//Check that the date is in the Calendar
		GregorianOnlyCalendar cal = new GregorianOnlyCalendar(Locale.UK);
		cal.setLenient(false);
		cal.set(year, month-1, day);
		try {
			cal.getTimeInMillis();
		} catch (IllegalArgumentException e) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		return redirector();	
	}
}