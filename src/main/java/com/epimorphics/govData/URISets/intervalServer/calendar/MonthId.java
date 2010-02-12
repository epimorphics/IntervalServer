package com.epimorphics.govData.URISets.intervalServer.calendar;

import com.epimorphics.govData.URISets.intervalServer.util.EnglishCalendar;
import java.util.Locale;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;


@Path(URITemplate.MONTH_ID_STEM+URITemplate.MONTH_PATTERN)
public class MonthId extends Id {


	/**
	 * id->doc 303 Redirections for URI of the form /id/yyyy-mm
	 */
	@GET
	public Response redirector(
			@PathParam(YEAR_TOKEN) int year,
			@PathParam(MONTH_TOKEN) int month) {
		
		//Check that the date is in the Calendar
		EnglishCalendar cal = new EnglishCalendar(Locale.UK);
		cal.setLenient(false);
		cal.set(year, month-1, 1);
		try {
			cal.getTimeInMillis();
		} catch (IllegalArgumentException e) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		
		return redirector();
	}
}