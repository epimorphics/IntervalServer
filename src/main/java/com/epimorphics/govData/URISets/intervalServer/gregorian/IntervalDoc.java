/******************************************************************
 * File:        IntervalDoc.java
 * Created by:  Stuart Williams
 * Created on:  13 Feb 2010
 * 
 * (c) Copyright 2010, Epimorphics Limited
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * $Id:  $
 *****************************************************************/

package com.epimorphics.govData.URISets.intervalServer.gregorian;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Locale;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.epimorphics.govData.URISets.intervalServer.BaseURI;
import com.epimorphics.govData.URISets.intervalServer.util.CalendarUtils;
import com.epimorphics.govData.URISets.intervalServer.util.Duration;
import com.epimorphics.govData.URISets.intervalServer.util.GregorianOnlyCalendar;
import com.epimorphics.govData.URISets.intervalServer.util.MediaTypeUtils;
import com.epimorphics.govData.vocabulary.DGU;
import com.epimorphics.govData.vocabulary.FOAF;
import com.epimorphics.govData.vocabulary.INTERVALS;
import com.epimorphics.govData.vocabulary.SCOVO;
import com.epimorphics.govData.vocabulary.SKOS;
import com.epimorphics.govData.vocabulary.TIME;
import com.epimorphics.govData.vocabulary.VOID;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
@Path(GregorianURITemplate.DOC_STEM+GregorianURITemplate.INTERVAL_SEGMENT+GregorianURITemplate.SET_EXT_PATTERN)

public class IntervalDoc extends Doc {
	
	Duration duration = null;

	
	protected void populateModel(int year, int month, int day, int hour, int min, int sec, String duration) {
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

		super.populateModel();

	}
	
	@GET
	@Path(INTERVAL_PATTERN+EXT_PATTERN)
	@Produces()
	public Response getResponse(
			@PathParam(YEAR_TOKEN) 		int year,
			@PathParam(MONTH_TOKEN) 	int month,
			@PathParam(DAY_TOKEN)   	int day,
			@PathParam(HOUR_TOKEN)		int hour,
 			@PathParam(MINUTE_TOKEN) 	int min,
 			@PathParam(SECOND_TOKEN) 	int sec,
			@PathParam(DURATION_TOKEN)  String duration,
			@PathParam(EXT_TOKEN)  		String ext2 ) {
		if(ext2.equals(""))
			throw new WebApplicationException(Status.NOT_FOUND);
		
		MediaType mt = MediaTypeUtils.extToMediaType(ext2);
		ext = ext2;
		return respond(year,month, day, hour, min, sec, duration, mt, false);
	}
	
	@GET
	@Path(INTERVAL_PATTERN)
	public Response getResponse2(
			@PathParam(YEAR_TOKEN) 		int year, 
			@PathParam(MONTH_TOKEN) 	int month,
			@PathParam(DAY_TOKEN)   	int day,
			@PathParam(HOUR_TOKEN)		int hour,
			@PathParam(MINUTE_TOKEN) 	int min,
			@PathParam(SECOND_TOKEN) 	int sec,
			@PathParam(DURATION_TOKEN)  String duration) {

		MediaType mt = MediaTypeUtils.pickMediaType(hdrs.getAcceptableMediaTypes());
		ext = MediaTypeUtils.getExtOfMediaType(mt);
		return respond(year, month, day, hour, min, sec, duration, mt, true);
	}

	private Response respond(int year, int month, int day, int hour, int min, int sec, String duration, MediaType mt, boolean addExtent) {
		base = getBaseUri();
		try {
				loc = new URI(base + ui.getPath());
				contentURI = new URI(loc.toString() + (addExtent? "."+ ext :""));
				setURI = new URI(base + SET_STEM + YEAR_SEGMENT);
		} catch (URISyntaxException e) {
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}
		
		String lang = MediaTypeUtils.getLangOfMediaType(mt);

		populateModel(year, month, day, hour, min, sec, duration);

		if (lang.equals("JSON"))
			return doGetJson().type(mt).contentLocation(contentURI).build();
 
		return doGet(lang).type(mt).contentLocation(contentURI).build();
	}
	
	@GET
	public Response getSetResponse(@PathParam(EXT2_TOKEN) String ext2) {
		MediaType mt;
		base = getBaseUri();
		//Remove leading .
		ext2 = (ext2!=null && !ext2.equals("")) ? ext2.substring(1) : null ; //skip the '.'
		try {
			// Sort out media type from extent or pick media type and ext.
			if (ext2 != null && !ext2.equals("")) {
				mt = MediaTypeUtils.extToMediaType(ext2);
				loc = new URI(base + ui.getPath());
				ext = ext2;
				contentURI = new URI(loc.toString());
				setURI = new URI(base + SET_STEM + YEAR_SEGMENT);
			} else {
				mt = MediaTypeUtils.pickMediaType(hdrs.getAcceptableMediaTypes());
				ext = MediaTypeUtils.getExtOfMediaType(mt);
				loc = new URI(base + ui.getPath());
				contentURI = new URI(loc.toString()+ "."+ ext);
				setURI = new URI(base + SET_STEM + YEAR_SEGMENT);
			}
		} catch (URISyntaxException e) {
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}
		
		String lang = MediaTypeUtils.getLangOfMediaType(mt);

		populateIntervalSet();

		if (lang.equals("JSON"))
			return doGetJson().type(mt).contentLocation(contentURI).build();

		return doGet(lang).type(mt).contentLocation(contentURI).build();
	}

	static public Resource createResourceAndLabels(URI base, Model m, Calendar cal2 , Duration duration) {
		GregorianOnlyCalendar cal = new GregorianOnlyCalendar(Locale.UK);
		cal.setLenient(false);
		cal.setTimeInMillis(cal2.getTimeInMillis());
		
		String relPart = CalendarUtils.toXsdDateTime(cal);
		String durPart = duration.toString();

		int year  = cal.get(Calendar.YEAR);
		int moy   = cal.get(Calendar.MONTH)+1-Calendar.JANUARY;
		int dom   = cal.get(Calendar.DATE);
		int hod   = cal.get(Calendar.HOUR);
		int moh   = cal.get(Calendar.MINUTE);
		int som   = cal.get(Calendar.SECOND);
			
		String s_intURI = base + INTERVAL_ID_STEM + relPart +"/" + durPart;
		
		Resource r_int = m.createResource(s_intURI, TIME.Interval);
		
		String s_label = ""+CALENDAR_NAME+" Interval:" + relPart + "/" +durPart;
		m.add(r_int, SKOS.prefLabel, s_label, "en");
		m.add(r_int, RDFS.label, s_label, "en");
	
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
				+ " " + year +" in the "+CALENDAR_NAME+" Calendar", "en");
	
		return r_int;
	}
		

	static public Resource createResource(URI base, Model m, Calendar cal2, Duration duration) {
		GregorianOnlyCalendar cal = new GregorianOnlyCalendar(Locale.UK);
		cal.setLenient(false);
		cal.setTime(cal2.getTime());

		Resource r_int = createResourceAndLabels(base, m,  cal, duration);
		m.add(r_int, RDF.type, SCOVO.Dimension);
		
		String dur = duration.toString();
		Literal l = ResourceFactory.createTypedLiteral(dur, XSDDatatype.XSDduration);

		m.add(r_int, INTERVALS.hasXsdDurationDescription, l);
		
		Resource r_instant = InstantDoc.createResource(base, m, cal);	
		m.add(r_int, TIME.hasBeginning, r_instant);
		m.add(r_int, SCOVO.min, CalendarUtils.formatScvDate(cal, CalendarUtils.iso8601dateTimeformat, XSDDatatype.XSDdateTime) );

		duration.addToCalendar(cal);

		Resource r_EndInstant = InstantDoc.createResource(base, m, cal);	
		m.add(r_int, TIME.hasEnd, r_EndInstant);
		
		cal.add(Calendar.SECOND, -1);
		m.add(r_int, SCOVO.max, CalendarUtils.formatScvDate(cal, CalendarUtils.iso8601dateTimeformat, XSDDatatype.XSDdateTime) );

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
		r_thisTemporalEntity = createResource(base, model, startTime, duration);
	}
	
	private void populateIntervalSet() {
		Resource r_set = createSecSet();
		Resource r_doc = model.createResource(loc.toString(), FOAF.Document);
		initSetModel(r_set, r_doc, INTERVAL_SET_LABEL);
		
		model.add(r_set, RDFS.comment, "A dataset of "+CALENDAR_NAME+" general purpose time intervals of arbitary duration.","en");
		model.add(r_set, RDF.type, VOID.Dataset);
		
		String base_reg = base.toString().replaceAll("\\.", "\\\\.");
		
		model.add(r_set, DGU.itemType, INTERVALS.Interval);
		model.add(r_set, VOID.uriRegexPattern, base_reg+INTERVAL_ID_STEM+INTERVAL_PATTERN_PLAIN);

		model.add(r_set, VOID.exampleResource, IntervalDoc.createResourceAndLabels(base, model, new GregorianOnlyCalendar(1977, 10, 1, 12, 22, 45), new Duration("P2Y1MT1H6S") ));

		addGregorianSourceRef(r_set);	
		
		Resource r_yearSet, r_halfSet, r_quarterSet, r_monthSet, r_weekSet, r_daySet, r_hourSet, r_minSet, r_secSet, r_intervalSet, r_instantSet;
		
		r_yearSet=createYearSet();
		r_halfSet=createHalfSet();
		r_quarterSet=createQuarterSet();
		r_monthSet=createMonthSet();
		r_weekSet=createWeekSet();
		r_daySet=createDaySet();
		r_hourSet=createHourSet();
		r_minSet=createMinSet();
		r_secSet=createSecSet();
//		r_intervalSet=createIntervalSet());
		r_instantSet=createInstantSet();
		
		addLinkset(r_set, r_yearSet, r_set, TIME.intervalEquals, 
				""+CALENDAR_NAME+" calendar year to generic interval links.",
				"Links between "+CALENDAR_NAME+" calendar years and their corresponding generic intervals.");

		addLinkset(r_set, r_halfSet, r_set, TIME.intervalEquals, 
				""+CALENDAR_NAME+" calendar half year to generic interval links.",
				"Links between "+CALENDAR_NAME+" calendar half years and their corresponding generic intervals.");
		
		addLinkset(r_set, r_quarterSet, r_set, TIME.intervalEquals, 
				""+CALENDAR_NAME+" calendar quarter year to generic interval links.",
				"Links between "+CALENDAR_NAME+" calendar quarter years and their corresponding generic intervals.");
		
		addLinkset(r_set, r_monthSet, r_set, TIME.intervalEquals, 
				""+CALENDAR_NAME+" calendar month to generic interval links.",
				"Links between "+CALENDAR_NAME+" calendar months and their corresponding generic intervals.");
		
		addLinkset(r_set, r_weekSet, r_set, TIME.intervalEquals, 
				""+CALENDAR_NAME+" calendar week to generic interval links.",
				"Links between ISO 8610 numbered "+CALENDAR_NAME+" calendar weeks and their corresponding generic intervals.");
		
		addLinkset(r_set, r_daySet, r_set, TIME.intervalEquals, 
				""+CALENDAR_NAME+" calendar day to generic interval links.",
				"Links between "+CALENDAR_NAME+" calendar days and their corresponding generic intervals.");
		
		addLinkset(r_set, r_hourSet, r_set, TIME.intervalEquals, 
				""+CALENDAR_NAME+" calendar hour to generic interval links.",
				"Links between "+CALENDAR_NAME+" calendar hours and their corresponding generic intervals.");
		
		addLinkset(r_set, r_minSet, r_set, TIME.intervalEquals, 
				""+CALENDAR_NAME+" calendar minute to generic interval links.",
				"Links between "+CALENDAR_NAME+" calendar minutes and their corresponding generic intervals.");
		
		addLinkset(r_set, r_secSet, r_set, TIME.intervalEquals, 
				""+CALENDAR_NAME+" calendar second to generic interval links.",
				"Links between "+CALENDAR_NAME+" calendar seconds and their corresponding generic intervals.");

		addLinkset(r_set, r_set, r_instantSet, TIME.hasBeginning, 
				""+CALENDAR_NAME+" calendar generic interval to starting instant links",
				"Links between "+CALENDAR_NAME+" calendar generic intervals and their starting instant.");		

		addLinkset(r_set, r_set, r_instantSet, TIME.hasEnd, 
				""+CALENDAR_NAME+" calendar generic interval to ending instant links",
				"Links between "+CALENDAR_NAME+" calendar generic intervals and their ending instant.");
		
	}	
	

}