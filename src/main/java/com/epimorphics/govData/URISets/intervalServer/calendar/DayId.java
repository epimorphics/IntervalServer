package com.epimorphics.govData.URISets.intervalServer.calendar;

import java.util.GregorianCalendar;
import java.util.Locale;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;


@Path(URITemplate.DAY_ID_STEM+URITemplate.DAY_PATTERN)
public class DayId extends Id {

	@GET
	public Response redirector(
			@PathParam(YEAR_TOKEN)  int year, 
			@PathParam(MONTH_TOKEN) int month,
			@PathParam(DAY_TOKEN)   int day ) {
		
		//Check that the date is in the Calendar
		GregorianCalendar cal = new GregorianCalendar(Locale.UK);
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