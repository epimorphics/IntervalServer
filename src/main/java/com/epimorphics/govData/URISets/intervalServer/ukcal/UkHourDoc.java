/******************************************************************
 * File:        UkHourDoc.java
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
 * $UkId:  $
 *****************************************************************/

package com.epimorphics.govData.URISets.intervalServer.ukcal;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
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

import com.epimorphics.govData.URISets.intervalServer.gregorian.InstantDoc;
import com.epimorphics.govData.URISets.intervalServer.util.BritishCalendar;
import com.epimorphics.govData.URISets.intervalServer.util.CalendarUtils;
import com.epimorphics.govData.URISets.intervalServer.util.MediaTypeUtils;
import com.epimorphics.govData.vocabulary.DGU;
import com.epimorphics.govData.vocabulary.FOAF;
import com.epimorphics.govData.vocabulary.INTERVALS;
import com.epimorphics.govData.vocabulary.SCOVO;
import com.epimorphics.govData.vocabulary.SKOS;
import com.epimorphics.govData.vocabulary.TIME;
import com.epimorphics.govData.vocabulary.VOID;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFList;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

@Path(UkCalURITemplate.DOC_STEM+UkCalURITemplate.HOUR_SEGMENT+UkCalURITemplate.SET_EXT_PATTERN)
public class UkHourDoc extends UkDoc {
	
	protected void populateModel(int year, int month, int day, int hour) {
		reset();
		
		setWeekOfYearAndMonth(year, month, day);
		
		this.year  = year;
		this.month = month;
		this.half =((month-1)/6)+1;
		this.quarter = ((month-1)/3)+1;
		this.day = day;
		this.hour = hour;
		
		startTime = new BritishCalendar(Locale.UK);
		startTime.setLenient(false);
		startTime.set(year, month-1, day, hour, min, sec);
		super.populateModel();

	}
	
	@GET
	@Path(HOUR_PATTERN+EXT_PATTERN)
	@Produces()
	public Response getResponse(
			@PathParam(YEAR_TOKEN) int year,
			@PathParam(MONTH_TOKEN) int month,
			@PathParam(DAY_TOKEN)   int day,
			@PathParam(HOUR_TOKEN)	int hour,
			@PathParam(EXT_TOKEN)  String ext2 ) {
		if(ext2.equals(""))
			throw new WebApplicationException(Status.NOT_FOUND);
		
		MediaType mt = MediaTypeUtils.extToMediaType(ext2);
		ext = ext2;
		return respond(year, month, day, hour, mt, false);
	}
	
	@GET
	@Path(HOUR_PATTERN)
	public Response getResponse2(
			@PathParam(YEAR_TOKEN) int year,
			@PathParam(MONTH_TOKEN) int month,
			@PathParam(DAY_TOKEN)   int day,
			@PathParam(HOUR_TOKEN)	int hour ) {
		MediaType mt = MediaTypeUtils.pickMediaType(hdrs.getAcceptableMediaTypes());
		ext = MediaTypeUtils.getExtOfMediaType(mt);
		return respond(year, month, day, hour, mt, true);
	}

	private Response respond(int year,int month, int day, int hour, MediaType mt, boolean addExtent) {
		base = getBaseUri();
		try {
				loc = new URI(base + ui.getPath());
				contentURI = new URI(loc.toString() + (addExtent? "."+ ext :""));
				setURI = new URI(base + SET_STEM + YEAR_SEGMENT);
		} catch (URISyntaxException e) {
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}
		
		String lang = MediaTypeUtils.getLangOfMediaType(mt);

		populateModel(year, month, day, hour);

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

		populateHourSet();

		if (lang.equals("JSON"))
			return doGetJson().type(mt).contentLocation(contentURI).build();

		return doGet(lang).type(mt).contentLocation(contentURI).build();
	}
	

	public static Resource createResourceAndLabels(URI base, Model m, int year, int moy, int dom, int hod) {
		String relPart = String.format("%04d",year) + MONTH_PREFIX + String.format("%02d", moy)
				+ DAY_PREFIX + String.format("%02d", dom) + HOUR_PREFIX
				+ String.format("%02d", hod);
	
		String s_hourURI = base + HOUR_ID_STEM + relPart;
		Resource r_hour = m.createResource(s_hourURI, INTERVALS.CalendarHour);

		String s_label = ""+CALENDAR_NAME+" Hour:" + relPart;
	
		m.add(r_hour, SKOS.prefLabel, s_label, "en");
		m.add(r_hour, RDFS.label, s_label, "en");
	
		BritishCalendar cal = new BritishCalendar(Locale.UK);
		cal.set(year, moy - 1, dom);
		String s_month = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.UK);
		String s_dayOfWeek = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.UK);
		String s_domSuffix = getDecimalSuffix(dom);
		String s_hodSuffix = getDecimalSuffix(hod+1);
	
		// String s_dayOfMonth = cal.getDisplayName(Calendar.DAY_OF_MONTH,
		// Calendar.LONG , Locale.UK);
		m.add(r_hour, RDFS.comment, "The " + (hod+1) + s_hodSuffix + " hour of "
				+ s_dayOfWeek + " the " + dom + s_domSuffix + " " + s_month
				+ " in the "+CALENDAR_NAME+" calendar year " + String.format("%04d",year) , "en");
	
		return r_hour;
	}

	protected static Resource createResource(URI base, Model m, int year, int moy, int dom, int hod) {
		Resource r_hour = createResourceAndLabels(base, m, year, moy, dom, hod);
		m.add(r_hour, RDF.type, SCOVO.Dimension);
		BritishCalendar cal = new BritishCalendar(year, moy-1, dom, hod, 0, 0);
		cal.setLenient(false);
		
		addCalendarOrdinals(r_hour, year, moy, dom, hod);

		m.add(r_hour, INTERVALS.hasXsdDurationDescription, oneHour);
		m.add(r_hour, TIME.hasDurationDescription, INTERVALS.one_hour);
		
		Resource r_instant = InstantDoc.createResource(base, m, cal);	
		m.add(r_hour, TIME.hasBeginning, r_instant);
		m.add(r_hour, SCOVO.min, CalendarUtils.formatScvDateTimeLiteral(cal) );

		cal.add(Calendar.HOUR_OF_DAY, 1);
		Resource r_EndInstant = InstantDoc.createResource(base, m, cal);	
		m.add(r_hour, TIME.hasEnd, r_EndInstant);
		
		cal.add(Calendar.SECOND, -1);
		m.add(r_hour, SCOVO.max, CalendarUtils.formatScvDateTimeLiteral(cal) );

		return r_hour;
	}

	@Override
	void addContainedIntervals() {
		BritishCalendar cal = (BritishCalendar) startTime.clone();
		ArrayList<Resource> minutes = new ArrayList<Resource>();
		
		// Add the minutes to the hour of day
		cal.set(year, month-1, day, hour, 0);
		int i_max_moh = cal.getActualMaximum(Calendar.MINUTE);
		int i_min_moh = cal.getActualMinimum(Calendar.MINUTE);
		
		for (int min = i_min_moh; min <= i_max_moh; min++) {
			Resource r_min = UkMinuteDoc.createResourceAndLabels(base, model, year, month, day, hour, min);
			connectToContainingInterval(model, r_thisTemporalEntity, r_min);
			minutes.add(r_min);
		}
		RDFList r_minutes = model.createList(minutes.iterator());
		model.add(r_thisTemporalEntity, INTERVALS.intervalContainsMinutes, r_minutes);
	}

	@Override
	void addContainingIntervals() {
		Resource r_thisYear = UkYearDoc.createResourceAndLabels(base, model,year);
		Resource r_thisQuarter = UkQuarterDoc.createResourceAndLabels(base, model,year,((month-1)/3)+1);
		Resource r_thisHalf    = UkHalfDoc.createResourceAndLabels(base, model, year, ((month-1)/6)+1);
		Resource r_thisMonth   = UkMonthDoc.createResourceAndLabels(base, model, year, month);
		Resource r_thisDay     = UkDayDoc.createResourceAndLabels(base, model, year, month, day);
		
		Resource r_thisWeek = UkWeekOfYearDoc.createResource(base, model, woy_year, woy_week); 


		// Link hour to its containing year, half, quarter, month and day
		connectToContainingInterval(model, r_thisYear, r_thisTemporalEntity);
		connectToContainingInterval(model, r_thisHalf, r_thisTemporalEntity);
		connectToContainingInterval(model, r_thisQuarter, r_thisTemporalEntity);
		connectToContainingInterval(model, r_thisMonth, r_thisTemporalEntity);
		connectToContainingInterval(model, r_thisDay, r_thisTemporalEntity);	
		connectToContainingInterval(model, r_thisWeek,  r_thisTemporalEntity);
	}

	@Override
	void addNeighboringIntervals() {
		Resource r_nextHour, r_prevHour;
		BritishCalendar cal;
		
		try {
			cal = (BritishCalendar) startTime.clone();
			cal.add(Calendar.HOUR_OF_DAY,1);
			r_nextHour = createResourceAndLabels(base, model, cal.get(Calendar.YEAR),cal.get(Calendar.MONTH)+1,cal.get(Calendar.DAY_OF_MONTH),cal.get(Calendar.HOUR_OF_DAY));
			cal = (BritishCalendar) startTime.clone();
			cal.add(Calendar.DAY_OF_MONTH,-1);
			r_prevHour = createResourceAndLabels(base, model, cal.get(Calendar.YEAR),cal.get(Calendar.MONTH)+1,cal.get(Calendar.DAY_OF_MONTH),cal.get(Calendar.HOUR_OF_DAY));
		} catch (IllegalArgumentException e) {
			throw new WebApplicationException(e, Response.Status.NOT_FOUND);
		}		
		// Link adjacent months
		connectToNeigbours(model, r_thisTemporalEntity, r_nextHour, r_prevHour);
	}

	@Override
	void addThisTemporalEntity() {
		r_thisTemporalEntity = createResource(base, model, year, month, day, hour);
		r_thisTemporalEntity.addProperty(DGU.uriSet,createHourSet());
		addGeneralIntervalTimeLink(model, startTime, oneHour);
	}
	
	private void populateHourSet() {
		//Resource r_set = model.createResource(setURI.toString(), DGU.URIset);
		Resource r_set = createHourSet();
		Resource r_doc = model.createResource(loc.toString(), FOAF.Document);
		initSetModel(r_set, r_doc, HOUR_SET_LABEL);
		
		model.add(r_set, RDFS.comment, "A dataset of "+CALENDAR_NAME+" calendar aligned time intervals of one hour duration.","en");
		model.add(r_set, RDF.type, VOID.Dataset);
		
		String base_reg = base.toString().replaceAll("\\.", "\\\\.");
		
		model.add(r_set, DGU.itemType, INTERVALS.CalendarHour);
		model.add(r_set, VOID.uriRegexPattern, base_reg+HOUR_ID_STEM+HOUR_PATTERN_PLAIN);

		model.add(r_set, VOID.exampleResource, UkHourDoc.createResourceAndLabels(base, model, 1752, 9, 2, 23));
	
		addCalendarActRef(r_set);	
		
		Resource r_yearSet, r_halfSet, r_quarterSet, r_monthSet, r_weekSet, r_daySet, r_hourSet, r_minSet, r_secSet, r_intervalSet, r_instantSet;
		
		r_yearSet=createYearSet();
		r_halfSet=createHalfSet();
		r_quarterSet=createQuarterSet();
		r_monthSet=createMonthSet();
		r_weekSet=createWeekSet();
		r_daySet=createDaySet();
//		r_hourSet=createHourSet();
		r_minSet=createMinSet();
//		r_secSet=createSecSet();
		r_intervalSet=createIntervalSet();
		r_instantSet=createInstantSet();
		
		addLinkset(r_set, r_set, r_yearSet, TIME.intervalDuring, 
				""+CALENDAR_NAME+" calendar hour to calendar year interval containment links",
				"Links between "+CALENDAR_NAME+" calendar hours and the calendar years in which they occur.");

		addLinkset(r_set, r_set, r_halfSet, TIME.intervalDuring, 
				""+CALENDAR_NAME+" calendar hour to half year interval containment links",
				"Links between "+CALENDAR_NAME+" calendar hours and the calendar aligned half years in which they occur.");

		addLinkset(r_set, r_set, r_quarterSet, TIME.intervalDuring, 
				""+CALENDAR_NAME+" calendar hours to calendar quarter year interval containment links",
				"Links between "+CALENDAR_NAME+" calendar hours and the calendar aligned quarter years in which they occur.");

		addLinkset(r_set, r_set, r_weekSet, TIME.intervalDuring, 
				""+CALENDAR_NAME+" calendar days to calendar week interval containment links",
				"Links between "+CALENDAR_NAME+" calendar days and the ISO 8601 numbered week in which they occur.");

		addLinkset(r_set, r_set, r_daySet, TIME.intervalDuring, 
				""+CALENDAR_NAME+" calendar hours to calendar day interval containment links",
				"Links between "+CALENDAR_NAME+" calendar days and the calendar day in which they occur.");

		addLinkset(r_set, r_set, r_minSet, INTERVALS.intervalContainsMinute, 
				""+CALENDAR_NAME+" calendar hour to calendar aligned minute interval containment links",
				"Links between "+CALENDAR_NAME+" calendar hour and the calendar aligned minutes they contain.");

		addLinkset(r_set, r_set, r_instantSet, TIME.hasBeginning, 
				""+CALENDAR_NAME+" calendar hour to starting instant links",
				"Links between "+CALENDAR_NAME+" calendar hours and their starting instant.");		

		addLinkset(r_set, r_set, r_instantSet, TIME.hasEnd, 
				""+CALENDAR_NAME+" calendar hours to ending instant links",
				"Links between "+CALENDAR_NAME+" calendar hours and their ending instant.");
		
		addLinkset(r_set, r_set, r_intervalSet, TIME.intervalEquals, 
				""+CALENDAR_NAME+" calendar hour to generic interval links",
				"Links between "+CALENDAR_NAME+" calendar hours and their corresponding generic interval.");		
	}
}