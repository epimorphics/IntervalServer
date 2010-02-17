/******************************************************************
 * File:        SecDoc.java
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

import com.epimorphics.govData.URISets.intervalServer.BaseURI;
import com.epimorphics.govData.URISets.intervalServer.util.CalendarUtils;
import com.epimorphics.govData.URISets.intervalServer.util.GregorianOnlyCalendar;
import com.epimorphics.govData.URISets.intervalServer.util.MediaTypeUtils;

import java.util.Locale;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.epimorphics.govData.vocabulary.DGU;
import com.epimorphics.govData.vocabulary.FOAF;
import com.epimorphics.govData.vocabulary.INTERVALS;
import com.epimorphics.govData.vocabulary.SCOVO;
import com.epimorphics.govData.vocabulary.SKOS;
import com.epimorphics.govData.vocabulary.TIME;
import com.epimorphics.govData.vocabulary.VOID;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

@Path(GregorianURITemplate.DOC_STEM+GregorianURITemplate.SECOND_SEGMENT+GregorianURITemplate.SET_EXT_PATTERN)
public class SecDoc extends Doc {
	
	protected void populateModel(int year, int month, int day, int hour, int min, int sec) {
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
		
		startTime = new GregorianOnlyCalendar(Locale.UK);
		startTime.setLenient(false);
		startTime.set(year, month-1, day, hour, min, sec);
		
		super.populateModel();
	}
	
	@GET
	@Path(SECOND_PATTERN+EXT_PATTERN)
	@Produces()
	public Response getResponse(
			@PathParam(YEAR_TOKEN) int year,
			@PathParam(MONTH_TOKEN) int month,
			@PathParam(DAY_TOKEN)   int day,
			@PathParam(HOUR_TOKEN)	int hour,
 			@PathParam(MINUTE_TOKEN) int min,
 			@PathParam(SECOND_TOKEN) int sec,
			@PathParam(EXT_TOKEN)  String ext2 ) {
		if(ext2.equals(""))
			throw new WebApplicationException(Status.NOT_FOUND);
		
		MediaType mt = MediaTypeUtils.extToMediaType(ext2);
		ext = ext2;
		return respond(year, month, day, hour, min, sec,  mt, false);
	}
	
	@GET
	@Path(SECOND_PATTERN)
	public Response getResponse2(
			@PathParam(YEAR_TOKEN) int year,
			@PathParam(MONTH_TOKEN) int month,
			@PathParam(DAY_TOKEN)   int day,
			@PathParam(HOUR_TOKEN)	int hour,
			@PathParam(MINUTE_TOKEN) int min,
			@PathParam(SECOND_TOKEN) int sec ) {
		MediaType mt = MediaTypeUtils.pickMediaType(hdrs.getAcceptableMediaTypes());
		ext = MediaTypeUtils.getExtOfMediaType(mt);
		return respond(year, month, day, hour, min, sec, mt, true);
	}

	private Response respond(int year, int month, int day, int hour, int min, int sec, MediaType mt, boolean addExtent) {
		base = getBaseUri();
		try {
				loc = new URI(base + ui.getPath());
				contentURI = new URI(loc.toString() + (addExtent? "."+ ext :""));
				setURI = new URI(base + SET_STEM + YEAR_SEGMENT);
		} catch (URISyntaxException e) {
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}
		
		String lang = MediaTypeUtils.getLangOfMediaType(mt);

		populateModel(year, month, day, hour, min, sec);

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

		populateSecondSet();

		if (lang.equals("JSON"))
			return doGetJson().type(mt).contentLocation(contentURI).build();

		return doGet(lang).type(mt).contentLocation(contentURI).build();
	}

	static protected Resource createResourceAndLabels(URI base, Model m,int year, int moy, int dom, int hod, int moh, int som) {
		String relPart = year + MONTH_PREFIX + String.format("%02d", moy)
		+ DAY_PREFIX + String.format("%02d", dom) + HOUR_PREFIX
		+ String.format("%02d", hod) + MINUTE_PREFIX 
		+ String.format("%02d", moh) + SECOND_PREFIX
		+ String.format("%02d", som);
		
		GregorianOnlyCalendar cal = new GregorianOnlyCalendar(Locale.UK);
		cal.setLenient(false);
		cal.set(year, moy-1, dom, hod, moh, som);
			
		String s_secURI = base + SECOND_ID_STEM + relPart;
		Resource r_sec = m.createResource(s_secURI, INTERVALS.CalendarSecond);
		
		String s_label = ""+CALENDAR_NAME+" Second:" + relPart;
		m.add(r_sec, SKOS.prefLabel, s_label, "en");
		m.add(r_sec, RDFS.label, s_label, "en");
	
		String s_month = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.UK);
		String s_dayOfWeek = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.UK);
		String s_domSuffix = getDecimalSuffix(dom);
		String s_hodSuffix = getDecimalSuffix(hod+1);
		String s_mohSuffix = getDecimalSuffix(moh+1);
		String s_somSuffix = getDecimalSuffix(som+1);
	
		// String s_dayOfMonth = cal.getDisplayName(Calendar.DAY_OF_MONTH,
		// Calendar.LONG , Locale.UK);
		m.add(r_sec, RDFS.comment, "The " + (som+1) + s_somSuffix + " second of " + (moh+1)
				+ s_mohSuffix + " minute of " + (hod+1) + s_hodSuffix + " hour of "
				+ s_dayOfWeek + " the " + dom + s_domSuffix + " " + s_month
				+ " of the "+CALENDAR_NAME+" calendar year " + year, "en");
	
		return r_sec;
	}

	static protected Resource createResource(URI base, Model m,	int year, int moy, int dom, int hod, int moh, int som) {
		Resource r_sec = createResourceAndLabels(base, m, year, moy, dom, hod, moh, som);
		m.add(r_sec, RDF.type, SCOVO.Dimension);
		GregorianOnlyCalendar cal = new GregorianOnlyCalendar(year, moy-1 , dom, hod, moh, som);
		cal.setLenient(false);

		m.add(r_sec, INTERVALS.hasXsdDurationDescription, oneSecond);
		m.add(r_sec, TIME.hasDurationDescription, INTERVALS.one_second);
		
		Resource r_instant = InstantDoc.createResource(base, m, cal);	
		m.add(r_sec, TIME.hasBeginning, r_instant);
		m.add(r_sec, SCOVO.min, CalendarUtils.formatScvDate(cal, CalendarUtils.iso8601dateTimeformat, XSDDatatype.XSDdateTime) );
		
		cal.add(Calendar.SECOND, 1);
		Resource r_EndInstant = InstantDoc.createResource(base, m, cal);	
		m.add(r_sec, TIME.hasEnd, r_EndInstant);
		
		cal.add(Calendar.SECOND, -1);
		m.add(r_sec, SCOVO.max, CalendarUtils.formatScvDate(cal, CalendarUtils.iso8601dateTimeformat, XSDDatatype.XSDdateTime) );

		return r_sec;
	}

	@Override
	void addContainedIntervals() {
		// Bottomed Out at seconds - nothing to do.
	}

	@Override
	void addContainingIntervals() {
		Resource r_thisYear = YearDoc.createResourceAndLabels(base, model,year);
		// Create resources for this quarter and its neighbours.
		Resource r_thisQuarter = QuarterDoc.createResourceAndLabels(base, model,year,((month-1)/3)+1);
		Resource r_thisHalf    = HalfDoc.createResourceAndLabels(base, model, year, ((month-1)/6)+1);
		Resource r_thisMonth   = MonthDoc.createResourceAndLabels(base, model, year, month);
		Resource r_thisDay     = DayDoc.createResourceAndLabels(base, model, year, month, day);
		Resource r_thisHour    = HourDoc.createResourceAndLabels(base, model, year, month, day, hour);
		Resource r_thisMin     = MinuteDoc.createResourceAndLabels(base, model, year, month, day, hour, min);
		Resource r_thisWeek    = WeekOfYearDoc.createResource(base, model, woy_year, woy_week);

		// Link second to its containing year, half, quarter, month, day, hour and minute.
		connectToContainingInterval(model, r_thisYear, r_thisTemporalEntity);
		connectToContainingInterval(model, r_thisHalf, r_thisTemporalEntity);
		connectToContainingInterval(model, r_thisQuarter, r_thisTemporalEntity);
		connectToContainingInterval(model, r_thisMonth, r_thisTemporalEntity);
		connectToContainingInterval(model, r_thisDay, r_thisTemporalEntity);
		connectToContainingInterval(model, r_thisHour, r_thisTemporalEntity);
		connectToContainingInterval(model, r_thisMin, r_thisTemporalEntity);	
		connectToContainingInterval(model, r_thisWeek, r_thisTemporalEntity);
	}

	@Override
	void addNeighboringIntervals() {
		Resource  r_nextSec, r_prevSec;
		GregorianOnlyCalendar cal;
		try {
			cal = (GregorianOnlyCalendar) startTime.clone();
			cal.add(Calendar.SECOND,1);
			r_nextSec = createResourceAndLabels(base, model, cal.get(Calendar.YEAR),cal.get(Calendar.MONTH)+1,cal.get(Calendar.DAY_OF_MONTH),cal.get(Calendar.HOUR_OF_DAY),cal.get(Calendar.MINUTE),cal.get(Calendar.SECOND));
			cal.set(year, month-1, day, hour, min, sec);
			cal.add(Calendar.SECOND,-1);
			r_prevSec = createResourceAndLabels(base, model, cal.get(Calendar.YEAR),cal.get(Calendar.MONTH)+1,cal.get(Calendar.DAY_OF_MONTH),cal.get(Calendar.HOUR_OF_DAY),cal.get(Calendar.MINUTE),cal.get(Calendar.SECOND));
			cal.set(year, month-1, day, hour, min, sec);
		} catch (IllegalArgumentException e) {
			throw new WebApplicationException(e, Response.Status.NOT_FOUND);
		}
		// Link adjacent seconds
		connectToNeigbours(model, r_thisTemporalEntity, r_nextSec, r_prevSec);	
	}

	@Override
	void addThisTemporalEntity() {
		r_thisTemporalEntity = createResource(base, model, year, month ,day, hour, min, sec);
		r_thisTemporalEntity.addProperty(DGU.uriSet,createSecSet());
		addGeneralIntervalTimeLink(model, startTime, oneSecond);
	}
	
	private void populateSecondSet() {
		Resource r_set = createSecSet();
		Resource r_doc = model.createResource(loc.toString(), FOAF.Document);
		initSetModel(r_set, r_doc, SECOND_SET_LABEL);
		
		model.add(r_set, RDFS.comment, "A dataset of "+CALENDAR_NAME+" calendar aligned time intervals of one second duration.","en");
		model.add(r_set, RDF.type, VOID.Dataset);
		
		String base_reg = base.toString().replaceAll("\\.", "\\\\.");
		
		model.add(r_set, DGU.itemType, INTERVALS.CalendarMinute);
		model.add(r_set, VOID.uriRegexPattern, base_reg+SECOND_ID_STEM+SEC_PATTERN_PLAIN);

		model.add(r_set, VOID.exampleResource, SecDoc.createResourceAndLabels(base, model, 1234, 4, 1, 22, 35, 41));

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
//		r_secSet=createSecSet();
		r_intervalSet=createIntervalSet();
		r_instantSet=createInstantSet();
		
		addLinkset(r_set, r_set, r_yearSet, TIME.intervalDuring, 
				""+CALENDAR_NAME+" calendar second to calendar year interval containment links",
				"Links between "+CALENDAR_NAME+" calendar seconds and the calendar years in which they occur.");

		addLinkset(r_set, r_set, r_halfSet, TIME.intervalDuring, 
				""+CALENDAR_NAME+" calendar second to half year interval containment links",
				"Links between "+CALENDAR_NAME+" calendar second and the calendar aligned half years in which they occur.");

		addLinkset(r_set, r_set, r_quarterSet, TIME.intervalDuring, 
				""+CALENDAR_NAME+" calendar second to calendar quarter year interval containment links",
				"Links between "+CALENDAR_NAME+" calendar seconds and the calendar aligned quarter years in which they occur.");

		addLinkset(r_set, r_set, r_weekSet, TIME.intervalDuring, 
				""+CALENDAR_NAME+" calendar second to calendar week interval containment links",
				"Links between "+CALENDAR_NAME+" calendar seconds and the ISO 8601 numbered week in which they occur.");

		addLinkset(r_set, r_set, r_daySet, TIME.intervalDuring, 
				""+CALENDAR_NAME+" calendar seconds to calendar day interval containment links",
				"Links between "+CALENDAR_NAME+" calendar seconds and the calendar day in which they occur.");

		addLinkset(r_set, r_set, r_hourSet, TIME.intervalDuring, 
				""+CALENDAR_NAME+" calendar seconds to calendar hour interval containment links",
				"Links between "+CALENDAR_NAME+" calendar seconds and the calendar hour in which they occur.");

		addLinkset(r_set, r_set, r_minSet, TIME.intervalDuring, 
				""+CALENDAR_NAME+" calendar seconds to calendar minute interval containment links",
				"Links between "+CALENDAR_NAME+" calendar seconds and the calendar minutes in which they occur.");

		addLinkset(r_set, r_set, r_instantSet, TIME.hasBeginning, 
				""+CALENDAR_NAME+" calendar second to starting instant links",
				"Links between "+CALENDAR_NAME+" calendar seconds and their starting instant.");		

		addLinkset(r_set, r_set, r_instantSet, TIME.hasEnd, 
				""+CALENDAR_NAME+" calendar second to ending instant links",
				"Links between "+CALENDAR_NAME+" calendar seconds and their ending instant.");
		
		addLinkset(r_set, r_set, r_intervalSet, TIME.intervalEquals, 
				""+CALENDAR_NAME+" calendar second to generic interval links",
				"Links between "+CALENDAR_NAME+" calendar second and their corresponding generic interval.");
		
	}	
}