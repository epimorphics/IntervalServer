package com.epimorphics.govData.URISets.intervalServer.govcalendar;

import com.epimorphics.govData.URISets.intervalServer.util.GregorianOnlyCalendar;
import java.util.Locale;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path(URITemplate.YEAR_ID_STEM+URITemplate.YEAR_PATTERN)
public class YearId extends Id {
	@GET
	public Response redirector(
			@PathParam(Y1_TOKEN) int year1,
			@PathParam(Y2_TOKEN) int year2) {
		//Check that the date is in the Gregorian Calendar
		if(year2!=year1+1) {
			return Response.status(Status.NOT_FOUND).build();
		}
		GregorianOnlyCalendar cal = new GregorianOnlyCalendar(Locale.UK);
		cal.setLenient(false);
		cal.set(year1, 0, 1);
		try {
			cal.getTimeInMillis();
		} catch (IllegalArgumentException e) {
			return Response.status(Status.NOT_FOUND).build();
		}
		return redirector();
	
	}
}