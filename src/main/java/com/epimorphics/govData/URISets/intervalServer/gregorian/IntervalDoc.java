package com.epimorphics.govData.URISets.intervalServer.gregorian;

import java.net.URI;
import java.util.Calendar;
import java.util.Locale;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.epimorphics.govData.URISets.intervalServer.util.Duration;
import com.epimorphics.govData.URISets.intervalServer.util.GregorianOnlyCalendar;
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
	
	Duration duration = null;

	
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
		
		this.duration = new Duration(duration);
		
		startTime = new GregorianOnlyCalendar(Locale.UK);
		startTime.setLenient(false);
		startTime.set(year, month-1, day, hour, min, sec);

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

	static protected Resource createResourceAndLabels(URI base, Model m,int year, int moy, int dom, int hod, int moh, int som, Duration duration) {
		String relPart = toXsdDateTime(year, moy, dom, hod, moh, som);
		String durPart = duration.toString();
			
		String s_intURI = base + INTERVAL_ID_STEM + relPart +"/" + durPart;
		
		Resource r_int = m.createResource(s_intURI, TIME.Interval);
		
		String s_label = "Gregorian Interval:" + relPart + "/" +durPart;
		m.add(r_int, SKOS.prefLabel, s_label, "en");
		m.add(r_int, RDFS.label, s_label, "en");
	
		GregorianOnlyCalendar cal = new GregorianOnlyCalendar(Locale.UK);
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
				(duration.getYears()<=0 ? "" : duration.getYears() + " year" +    (duration.getYears()>1?"s " : " ")) + 
				(duration.getMonths()<=0 ? "" : duration.getMonths() + " month" + (duration.getMonths()>1?"s " : " "))  +
				(duration.getDays()<=0 ? "" : duration.getDays() + " day"+     (duration.getDays()>1?"s " : " ")) +
				(duration.getHours()<=0 ? "" : duration.getHours() + " hour" + (duration.getHours()>1?"s " : " ")) +
				(duration.getMins()<=0 ? "" : duration.getMins() + " minute" + (duration.getMins()>1?"s " : " "))+
				(duration.getSecs()<=0 ? "" : duration.getSecs() + " second"+  (duration.getSecs()>1?"s " : " ")) +
				"beginning at the start of the " + (som+1) + s_somSuffix + " second of " + (moh+1)
				+ s_mohSuffix + " minute of " + (hod+1) + s_hodSuffix + " hour of "
				+ s_dayOfWeek + " the " + dom + s_domSuffix + " " + s_month
				+ " " + year +" in the Gregorian Calendar", "en");
	
		return r_int;
	}
	
	public static Resource createResourceAndLabels(URI base, Model model, Calendar cal, Duration duration) {
		GregorianOnlyCalendar d = new GregorianOnlyCalendar(Locale.UK);
		d.setTime(cal.getTime());
		return createResourceAndLabels(base, model, 
				d.get(Calendar.YEAR),
				d.get(Calendar.MONTH),
				d.get(Calendar.DATE),
				d.get(Calendar.HOUR),
				d.get(Calendar.MINUTE),
				d.get(Calendar.SECOND),
				duration);
	}
	
	protected static Resource createResource(URI base, Model model, Calendar cal, Duration duration) {
		GregorianOnlyCalendar d = new GregorianOnlyCalendar(Locale.UK);
		d.setTime(cal.getTime());
		return createResource(base, model, 
				d.get(Calendar.YEAR),
				d.get(Calendar.MONTH),
				d.get(Calendar.DATE),
				d.get(Calendar.HOUR),
				d.get(Calendar.MINUTE),
				d.get(Calendar.SECOND),
				duration);
	}

	static protected Resource createResource(URI base, Model m,	int year, int moy, int dom, int hod, int moh, int som, Duration duration) {
		
		Resource r_int = createResourceAndLabels(base, m, year, moy, dom, hod, moh, som, duration);
		m.add(r_int, RDF.type, SCOVO.Dimension);
		GregorianOnlyCalendar cal = new GregorianOnlyCalendar(year, moy-1 , dom, hod, moh, som);
		cal.setLenient(false);
		
		String dur = duration.toString();
		Literal l = ResourceFactory.createTypedLiteral(dur, XSDDatatype.XSDduration);

		m.add(r_int, INTERVALS.hasXsdDurationDescription, l);
		
		Resource r_instant = InstantDoc.createResource(base, m, cal);	
		m.add(r_int, TIME.hasBeginning, r_instant);
		m.add(r_int, SCOVO.min, formatScvDate(cal, iso8601dateTimeformat, XSDDatatype.XSDdateTime) );

		duration.addToCalendar(cal);

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
		r_thisTemporalEntity = createResource(base, model, year, month ,day, hour, min, sec, duration);
	}

}