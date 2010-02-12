package com.epimorphics.govData.URISets.intervalServer.calendar;

import java.util.Calendar;
import com.epimorphics.govData.URISets.intervalServer.util.EnglishCalendar;
import java.util.Locale;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.epimorphics.govData.URISets.intervalServer.util.CalendarUtils;


@Path(URITemplate.WEEK_ID_STEM+URITemplate.WOY_PATTERN)
public class WeekOfYearId extends Id {


	/**
	 * id->doc 303 Redirections for URI of the form /id/yyyy-mm
	 */
	@GET
	public Response redirector(
			@PathParam(YEAR_TOKEN) int year,
			@PathParam(WEEK_TOKEN) int week) {
		
		//Check that the date is in the Calendar
		EnglishCalendar cal = new EnglishCalendar(Locale.UK);
		try {
			CalendarUtils.setWeekOfYear(year, CalendarUtils.inCutOverAnomaly(year, week) ? week+1 : week , cal);
		} catch (IllegalArgumentException e) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		
		return redirector();
	}
}