package com.epimorphics.govData.URISets.intervalServer.gregorian;

import java.util.Locale;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.epimorphics.govData.URISets.intervalServer.util.CalendarUtils;
import com.epimorphics.govData.URISets.intervalServer.util.GregorianOnlyCalendar;


@Path(URITemplate.WEEK_ID_STEM+URITemplate.WOY_PATTERN)
public class WeekOfYearId extends Id {


	/**
	 * id->doc 303 Redirections for URI of the form /id/yyyy-Www
	 */
	@GET
	public Response redirector(
			@PathParam(YEAR_TOKEN) int year,
			@PathParam(WEEK_TOKEN) int week) {
		
		//Check that the date is in the Calendar
		GregorianOnlyCalendar cal = new GregorianOnlyCalendar(Locale.UK);
		try {
			CalendarUtils.setWeekOfYear(year,  week , cal);
		} catch (IllegalArgumentException e) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		
		return redirector();
	}
}