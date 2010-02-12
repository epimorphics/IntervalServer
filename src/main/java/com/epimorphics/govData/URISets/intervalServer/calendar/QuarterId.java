package com.epimorphics.govData.URISets.intervalServer.calendar;

import com.epimorphics.govData.URISets.intervalServer.util.EnglishCalendar;
import java.util.Locale;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;


@Path(URITemplate.QUARTER_ID_STEM+URITemplate.QUARTER_PATTERN)
public class QuarterId extends Id {

	@GET
	public Response redirector(
			@PathParam(YEAR_TOKEN) int year, 
			@PathParam(QUARTER_TOKEN) int quarter) {

		//Check that the date is in the Calendar
		EnglishCalendar cal = new EnglishCalendar(Locale.UK);
		cal.setLenient(false);
		cal.set(year, (quarter-1)*3 , 1);
		try {
			cal.getTimeInMillis();
		} catch (IllegalArgumentException e) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}	
		return redirector();
	}
}