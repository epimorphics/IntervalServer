package com.epimorphics.govData.URISets.intervalServer.gregorian;

import com.epimorphics.govData.URISets.intervalServer.util.GregorianOnlyCalendar;
import java.util.Locale;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;


@Path(URITemplate.INTERVAL_ID_STEM+URITemplate.INTERVAL_PATTERN)
//@Path(URITemplate.INTERVAL_ID_STEM+URITemplate.SECOND_PATTERN+"/{duration : P([0-9]+Y)?([0-9]+M)?([0-9]+D)?(T([0-9]+H)?([0-9]+M)?([0-9]+S)?)?}")
public class IntervalId extends Id{

	@GET
	public Response redirector(
			@PathParam(YEAR_TOKEN)     int year, 
			@PathParam(MONTH_TOKEN)    int month,
			@PathParam(DAY_TOKEN)      int day,
			@PathParam(HOUR_TOKEN)     int hour,
			@PathParam(MINUTE_TOKEN)   int min,
			@PathParam(SECOND_TOKEN)   int sec,
			@PathParam(DURATION_TOKEN) String duration){
		
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