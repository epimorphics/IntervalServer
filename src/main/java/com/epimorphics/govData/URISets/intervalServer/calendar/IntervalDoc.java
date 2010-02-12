package com.epimorphics.govData.URISets.intervalServer.calendar;

import java.net.URI;
import java.util.Calendar;
import com.epimorphics.govData.URISets.intervalServer.util.EnglishCalendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.epimorphics.govData.URISets.intervalServer.util.CalendarUtils;
import com.epimorphics.govData.vocabulary.INTERVALS;
import com.epimorphics.govData.vocabulary.SCOVO;
import com.epimorphics.govData.vocabulary.SKOS;
import com.epimorphics.govData.vocabulary.TIME;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

@Path(URITemplate.INTERVAL_DOC_STEM)
public class IntervalDoc extends Doc {
	
	int d_years  = -1;
	int d_months = -1;
	int d_days   = -1;
	int d_hours  = -1; 
	int d_mins   = -1;
	int d_secs   = -1;;

	
	protected void reset(int year, int month, int day, int hour, int min, int sec, String duration) {
		reset();
		setWeekOfYearAndMonth(year, month, day);
		this.year  = year;
		this.month = month;
		this.half =((month-1)/6)+1;
		this.quarter = ((month-1)/3)+1;
		this.day = day;
		this.hour = hour;
		this.min = min;
		this.sec = sec;
		
		Pattern p = Pattern.compile(DURATION_REGEX);
		Matcher m = p.matcher(duration);
		
		if(m.matches()) {
			String s = m.group(DURATION_YEARS);
			if(s!=null && !s.equals("")) 
				d_years = Integer.parseInt(s);

			s = m.group(DURATION_MONTHS);
			if(s!=null && !s.equals("")) 
				d_months = Integer.parseInt(s);

			s = m.group(DURATION_DAYS);		
			if(s!=null && !s.equals("")) 
				d_days = Integer.parseInt(s);
			
			s = m.group(DURATION_HOURS);
			if(s!=null && !s.equals("")) 
				d_hours = Integer.parseInt(s);

			s = m.group(DURATION_MINUTES);
			if(s!=null && !s.equals("")) 
				d_mins = Integer.parseInt(s);
			
			s = m.group(DURATION_SECONDS);
			if(s!=null && !s.equals("")) 
				d_secs = Integer.parseInt(s);
		}
	}
	
	@GET
	@Path(INTERVAL_PATTERN+EXT_PATTERN)
	@Produces()
	public Response getResponse(
			@PathParam(YEAR_TOKEN)  	int year,
			@PathParam(MONTH_TOKEN) 	int month,
			@PathParam(DAY_TOKEN)   	int day,
			@PathParam(HOUR_TOKEN)		int hour,
 			@PathParam(MINUTE_TOKEN) 	int min,
 			@PathParam(SECOND_TOKEN) 	int sec,
			@PathParam(EXT_TOKEN)  		String ext,
			@PathParam(DURATION_TOKEN)  String duration) {
		reset(year, month, day, hour, min, sec, duration);
		this.ext   = ext;
		return doGet();
	}

	@GET
	@Path(INTERVAL_PATTERN)
	@Produces("text/plain")
	public Response getNTriple(
			@PathParam(YEAR_TOKEN)  	int year,
			@PathParam(MONTH_TOKEN) 	int month,
			@PathParam(DAY_TOKEN)   	int day,
			@PathParam(HOUR_TOKEN)		int hour,
 			@PathParam(MINUTE_TOKEN) 	int min,
 			@PathParam(SECOND_TOKEN) 	int sec,
			@PathParam(DURATION_TOKEN)  String duration) {
		reset(year, month, day, hour, min, sec, duration);
		return doGetNTriple().contentLocation(URI.create(ui.getPath()+"."+EXT_NT)).build();
	}

	@GET
	@Path(INTERVAL_PATTERN)
	@Produces("application/rdf+xml")
	public Response getRDF(
			@PathParam(YEAR_TOKEN)  	int year,
			@PathParam(MONTH_TOKEN) 	int month,
			@PathParam(DAY_TOKEN)   	int day,
			@PathParam(HOUR_TOKEN)		int hour,
 			@PathParam(MINUTE_TOKEN) 	int min,
 			@PathParam(SECOND_TOKEN) 	int sec,
			@PathParam(DURATION_TOKEN)  String duration) {
		reset(year, month, day, hour, min, sec, duration);
		return doGetRDF().contentLocation(URI.create(ui.getPath()+"."+EXT_RDF)).build();

	}
	@GET
	@Path(INTERVAL_PATTERN)
	@Produces("application/json")
	public Response getJson(
			@PathParam(YEAR_TOKEN)  	int year,
			@PathParam(MONTH_TOKEN) 	int month,
			@PathParam(DAY_TOKEN)   	int day,
			@PathParam(HOUR_TOKEN)		int hour,
 			@PathParam(MINUTE_TOKEN) 	int min,
 			@PathParam(SECOND_TOKEN) 	int sec,
			@PathParam(DURATION_TOKEN)  String duration) {
		reset(year, month, day, hour, min, sec, duration);
		return doGetJson().contentLocation(URI.create(ui.getPath()+"."+EXT_JSON)).build();
	}
	
	@GET
	@Path(INTERVAL_PATTERN)
	@Produces( { "text/turtle", "application/x-turtle", "text/n3" })
	public Response getTurtle(
			@PathParam(YEAR_TOKEN)  	int year,
			@PathParam(MONTH_TOKEN) 	int month,
			@PathParam(DAY_TOKEN)   	int day,
			@PathParam(HOUR_TOKEN)		int hour,
 			@PathParam(MINUTE_TOKEN) 	int min,
 			@PathParam(SECOND_TOKEN) 	int sec,
			@PathParam(DURATION_TOKEN)  String duration) {
		reset(year, month, day, hour, min, sec, duration);
		return doGetTurtle().contentLocation(URI.create(ui.getPath()+"."+EXT_TTL)).build();
	}

	static protected Resource createResourceAndLabels(URI base, Model m,int year, int moy, int dom, int hod, int moh, int som,
			                                                            int d_years, int d_months, int d_days, int d_hours, int d_mins, int d_secs) {
		String relPart = toXsdDateTime(year, moy, dom, hod, moh, som);
		String durPart = CalendarUtils.makeIsoDuration(d_years, d_months, d_days, d_hours, d_mins, d_secs);
			
		String s_intURI = base + INTERVAL_ID_STEM + relPart +"/" + durPart;
		
		Resource r_int = m.createResource(s_intURI, TIME.Interval);
		
		String s_label = "Calendar Interval:" + relPart + "/" +durPart;
		m.add(r_int, SKOS.prefLabel, s_label, "en");
		m.add(r_int, RDFS.label, s_label, "en");
	
		EnglishCalendar cal = new EnglishCalendar(Locale.UK);
		cal.set(year, moy - 1, dom);
		String s_month = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.UK);
		String s_dayOfWeek = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.UK);
		String s_domSuffix = getDecimalSuffix(dom);
		String s_hodSuffix = getDecimalSuffix(hod+1);
		String s_mohSuffix = getDecimalSuffix(moh+1);
		String s_somSuffix = getDecimalSuffix(som+1);
	
		// String s_dayOfMonth = cal.getDisplayName(Calendar.DAY_OF_MONTH,
		// Calendar.LONG , Locale.UK);
		m.add(r_int, RDFS.comment,"An interval of " + 
				(d_years<0 ? "" : d_years + " year" +    (d_years>1?"s " : " ")) + 
				(d_months<0 ? "" : d_months + " month" + (d_months>1?"s " : " "))  +
				(d_days<0 ? "" : d_days + " day"+     (d_days>1?"s " : " ")) +
				(d_hours<0 ? "" : d_hours + " hour" + (d_hours>1?"s " : " ")) +
				(d_mins<0 ? "" : d_mins + " minute" + (d_mins>1?"s " : " "))+
				(d_secs<0 ? "" : d_secs + " second"+  (d_secs>1?"s " : " ")) +
				"beginning at the start of the " + (som+1) + s_somSuffix + " second of " + (moh+1)
				+ s_mohSuffix + " minute of " + (hod+1) + s_hodSuffix + " hour of "
				+ s_dayOfWeek + " the " + dom + s_domSuffix + " " + s_month
				+ " " + year, "en");
	
		return r_int;
	}

	static protected Resource createResource(URI base, Model m,	int year, int moy, int dom, int hod, int moh, int som,
																int d_years, int d_months, int d_days, int d_hours, int d_mins, int d_secs) {
		
		Resource r_int = createResourceAndLabels(base, m, year, moy, dom, hod, moh, som, d_years, d_months, d_days, d_hours, d_mins, d_secs);
		m.add(r_int, RDF.type, SCOVO.Dimension);
		EnglishCalendar cal = new EnglishCalendar(year, moy-1 , dom, hod, moh, som);
		cal.setLenient(false);
		
		String dur = CalendarUtils.makeIsoDuration(d_years, d_months, d_days, d_hours, d_mins, d_secs);
		Literal l = ResourceFactory.createTypedLiteral(dur, XSDDatatype.XSDduration);

		m.add(r_int, INTERVALS.hasXsdDurationDescription, l);
		
		Resource r_instant = InstantDoc.createResource(base, m, cal);	
		m.add(r_int, TIME.hasBeginning, r_instant);
		m.add(r_int, SCOVO.min, formatScvDate(cal, iso8601dateTimeformat, XSDDatatype.XSDdateTime) );
		
		if(d_years>0)
			cal.add(Calendar.YEAR, d_years);

		if(d_months>0)
			cal.add(Calendar.MONTH, d_months);

		if(d_days>0)
			cal.add(Calendar.DATE, d_days);
		
		if(d_hours>0)
			cal.add(Calendar.HOUR, d_hours);
		
		if(d_mins>0)
			cal.add(Calendar.MINUTE, d_mins);
		
		if(d_secs>0)
			cal.add(Calendar.SECOND, d_secs);

		Resource r_EndInstant = InstantDoc.createResource(base, m, cal);	
		m.add(r_int, TIME.hasEnd, r_EndInstant);
		
		cal.add(Calendar.SECOND, -1);
		m.add(r_int, SCOVO.max, formatScvDate(cal, iso8601dateTimeformat, XSDDatatype.XSDdateTime) );

		return r_int;
	}

	@Override
	void addContainedIntervals() {
		// Bottomed Out at seconds - nothing to do.
	}

	@Override
	void addContainingIntervals() {
		//Nothing to do here
	}

	@Override
	void addNeighboringIntervals() {
		// Nothing to do here
	}

	@Override
	void addThisTemporalEntity() {
		r_thisTemporalEntity = createResource(base, model, year, month ,day, hour, min, sec, d_years, d_months, d_days, d_hours, d_mins, d_secs);
	}
}