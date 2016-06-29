/******************************************************************
 * File:        MinuteDoc.java
 * Created by:  Stuart Williams
 * Created on:  13 Feb 2010
 * 
 * (c) Copyright 2010, Epimorphics Limited
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  
 * $Id:  $
 *****************************************************************/

package com.epimorphics.govData.URISets.intervalServer.gregorian;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
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
import com.hp.hpl.jena.rdf.model.RDFList;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

@Path(GregorianCalURITemplate.DOC_STEM+GregorianCalURITemplate.MINUTE_SEGMENT+GregorianCalURITemplate.SET_EXT_PATTERN)
public class MinuteDoc extends Doc {
	
	protected void populateModel(int year, int month, int day, int hour, int min) {
		reset();	
		setWeekOfYearAndMonth(year, month, day);
		this.year  = year;
		this.month = month;
		this.half =((month-1)/6)+1;
		this.quarter = ((month-1)/3)+1;
		this.day = day;
		this.hour = hour;
		this.min = min;
		
		startTime = new GregorianOnlyCalendar(Locale.UK);
		startTime.setLenient(false);
		startTime.set(year, month-1, day, hour, min, sec);
		
		super.populateModel();
	}
	
	@GET
	@Path(MINUTE_PATTERN+EXT_PATTERN)
	@Produces()
	public Response getResponse(
			@PathParam(YEAR_TOKEN) int year,
			@PathParam(MONTH_TOKEN) int month,
			@PathParam(DAY_TOKEN)   int day,
			@PathParam(HOUR_TOKEN)	int hour,
 			@PathParam(MINUTE_TOKEN) int min,
			@PathParam(EXT_TOKEN)  String ext2 ) {
		if(ext2.equals(""))
			throw new WebApplicationException(Status.NOT_FOUND);
		
		MediaType mt = MediaTypeUtils.extToMediaType(ext2);
		ext = ext2;
		return respond(year, month, day, hour, min, mt, false);
	}
	
	@GET
	@Path(MINUTE_PATTERN)
	public Response getResponse2(
			@PathParam(YEAR_TOKEN) int year,
			@PathParam(MONTH_TOKEN) int month,
			@PathParam(DAY_TOKEN)   int day,
			@PathParam(HOUR_TOKEN)	int hour,
			@PathParam(MINUTE_TOKEN) int min ) {
		MediaType mt = MediaTypeUtils.pickMediaType(hdrs.getAcceptableMediaTypes());
		ext = MediaTypeUtils.getExtOfMediaType(mt);
		return respond(year, month, day, hour, min, mt, true);
	}

	private Response respond(int year,int month, int day, int hour, int min, MediaType mt, boolean addExtent) {
		base = getBaseUri();
		try {
				loc = new URI(base + ui.getPath());
				contentURI = new URI(loc.toString() + (addExtent? "."+ ext :""));
				setURI = new URI(base + SET_STEM + YEAR_SEGMENT);
		} catch (URISyntaxException e) {
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}
		
		String lang = MediaTypeUtils.getLangOfMediaType(mt);

		populateModel(year, month, day, hour, min);

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

		populateMinuteSet();

		if (lang.equals("JSON"))
			return doGetJson().type(mt).contentLocation(contentURI).build();

		return doGet(lang).type(mt).contentLocation(contentURI).build();
	}
	
	
	protected static Resource createResourceAndLabels(URI base, Model m, int year,	int moy, int dom, int hod, int moh) {
		String relPart = String.format("%04d",year) + MONTH_PREFIX + String.format("%02d", moy)
				+ DAY_PREFIX + String.format("%02d", dom) + HOUR_PREFIX
				+ String.format("%02d", hod) + MINUTE_PREFIX
				+ String.format("%02d", moh);

		String s_minURI = base + MINUTE_ID_STEM + relPart;
		Resource r_min = m.createResource(s_minURI, INTERVALS.CalendarMinute);
		r_min.addProperty(RDF.type, INTERVALS.Minute);

		String s_label = ""+CALENDAR_NAME+" Minute:" + relPart;

		m.add(r_min, SKOS.prefLabel, s_label, "en");
		m.add(r_min, RDFS.label, s_label, "en");

		GregorianOnlyCalendar cal = new GregorianOnlyCalendar(Locale.UK);
		cal.set(year, moy - 1, dom, 0, 0, 0);
		String s_month = cal.getDisplayName(Calendar.MONTH, Calendar.LONG,
				Locale.UK);
		String s_dayOfWeek = cal.getDisplayName(Calendar.DAY_OF_WEEK,
				Calendar.LONG, Locale.UK);
		String s_domSuffix = getDecimalSuffix(dom);
		String s_hodSuffix = getDecimalSuffix(hod+1);
		String s_mohSuffix = getDecimalSuffix(moh+1);

		// String s_dayOfMonth = cal.getDisplayName(Calendar.DAY_OF_MONTH,
		// Calendar.LONG , Locale.UK);
		m.add(r_min, RDFS.comment, "The " + (moh+1) + s_mohSuffix + " minute of " + (hod+1)
				+ s_hodSuffix + " hour of " + s_dayOfWeek + " the " + dom
				+ s_domSuffix + " " + s_month + " of the "+CALENDAR_NAME+" calendar year " + String.format("%04d",year) , "en");

		return r_min;
	}

	protected static Resource createResource(URI base, Model m, int year, int moy, int dom, int hod, int moh) {
		Resource r_min = createResourceAndLabels(base, m, year, moy, dom, hod, moh);
		m.add(r_min, RDF.type, SCOVO.Dimension);
		
		addCalendarOrdinals(r_min, year, moy, dom, hod, moh);

		GregorianOnlyCalendar cal = new GregorianOnlyCalendar(year, moy-1, dom, hod, moh, 0);
		cal.setLenient(false);

		m.add(r_min, INTERVALS.hasXsdDurationDescription, oneMinute);
		m.add(r_min, TIME.hasDurationDescription, INTERVALS.one_minute);
	
		Resource r_instant = InstantDoc.createResource(base, m, cal);		
		m.add(r_min, TIME.hasBeginning, r_instant);
		m.add(r_min, SCOVO.min, CalendarUtils.formatScvDateTimeLiteral(cal) );

		
		cal.add(Calendar.MINUTE, 1);
		
		Resource r_EndInstant = InstantDoc.createResource(base, m, cal);		
		m.add(r_min, TIME.hasEnd, r_EndInstant);
		
		cal.add(Calendar.SECOND, -1);
		m.add(r_min, SCOVO.max, CalendarUtils.formatScvDateTimeLiteral(cal) );

		
		return r_min;
	}

	@Override
	void addContainedIntervals() {
		GregorianOnlyCalendar cal = (GregorianOnlyCalendar) startTime.clone();
		ArrayList<Resource> seconds = new ArrayList<Resource>();

		// Add the hours to the day
		cal.set(year, month-1, day, hour, 0);
		int i_max_som = cal.getActualMaximum(Calendar.SECOND);
		int i_min_som = cal.getActualMinimum(Calendar.SECOND);
		
		for (int sec = i_min_som; sec <= i_max_som; sec++) {
			Resource r_sec = SecDoc.createResourceAndLabels(base, model, year, month, day, hour, min, sec);
			connectToContainingInterval(model, r_thisTemporalEntity, r_sec);
			seconds.add(r_sec);
		}		
		RDFList r_seconds = model.createList(seconds.iterator());
		model.add(r_thisTemporalEntity, INTERVALS.intervalContainsSeconds, r_seconds);
	}

	@Override
	void addContainingIntervals() {
		Resource r_thisYear = YearDoc.createResourceAndLabels(base, model, year);
		// Create resources for this quarter and its neighbours.
		Resource r_thisQuarter = QuarterDoc.createResourceAndLabels(base, model,year,((month-1)/3)+1);
		Resource r_thisHalf    = HalfDoc.createResourceAndLabels(base, model, year, ((month-1)/6)+1);
		Resource r_thisMonth   = MonthDoc.createResourceAndLabels(base, model, year, month);
		Resource r_thisDay     = DayDoc.createResourceAndLabels(base, model, year, month, day);
		Resource r_thisHour    = HourDoc.createResourceAndLabels(base, model, year, month, day, hour);
		Resource r_thisWeek    = WeekOfYearDoc.createResource(base, model, woy_year, woy_week);
		
		// Link minute to its containing year, half, quarter, day and hour.
		connectToContainingInterval(model, r_thisYear, r_thisTemporalEntity);
		connectToContainingInterval(model, r_thisHalf, r_thisTemporalEntity);
		connectToContainingInterval(model, r_thisQuarter, r_thisTemporalEntity);
		connectToContainingInterval(model, r_thisMonth, r_thisTemporalEntity);
		connectToContainingInterval(model, r_thisDay, r_thisTemporalEntity);
		connectToContainingInterval(model, r_thisHour, r_thisTemporalEntity);
		connectToContainingInterval(model, r_thisWeek, r_thisTemporalEntity);
	}

	@Override
	void addNeighboringIntervals() {
		Resource r_nextMin, r_prevMin;
		GregorianOnlyCalendar cal;
		
		try {
			cal = (GregorianOnlyCalendar) startTime.clone();
			cal.add(Calendar.MINUTE,1);
			r_nextMin = createResourceAndLabels(base, model, cal.get(Calendar.YEAR),cal.get(Calendar.MONTH)+1,cal.get(Calendar.DAY_OF_MONTH),cal.get(Calendar.HOUR_OF_DAY),cal.get(Calendar.MINUTE));
			cal.set(year, month-1, day, hour, min);
			cal.add(Calendar.MINUTE,-1);
			r_prevMin = createResourceAndLabels(base, model, cal.get(Calendar.YEAR),cal.get(Calendar.MONTH)+1,cal.get(Calendar.DAY_OF_MONTH),cal.get(Calendar.HOUR_OF_DAY),cal.get(Calendar.MINUTE));
			cal.set(year, month-1, day, hour, min);
		} catch (IllegalArgumentException e) {
			throw new WebApplicationException(e, Response.Status.NOT_FOUND);

		}
		connectToNeigbours(model, r_thisTemporalEntity, r_nextMin, r_prevMin);
	}

	@Override
	void addThisTemporalEntity() {
		r_thisTemporalEntity = createResource(base, model, year, month ,day ,hour, min);
		r_thisTemporalEntity.addProperty(DGU.uriSet,createMinSet());
		addGeneralIntervalTimeLink(model, startTime, oneMinute);
	}
	
	private void populateMinuteSet() {
		//Resource r_set = model.createResource(setURI.toString(), DGU.URIset);
		Resource r_set = createMinSet();
		Resource r_doc = model.createResource(loc.toString(), FOAF.Document);
		initSetModel(r_set, r_doc, MINUTE_SET_LABEL);
		
		r_thisTemporalEntity = r_set;
		
		model.add(r_set, RDFS.comment, "A dataset of "+CALENDAR_NAME+" calendar aligned time intervals of one minute duration.","en");
		model.add(r_set, RDF.type, VOID.Dataset);
		
		String base_reg = base.toString().replaceAll("\\.", "\\\\.");
		
		model.add(r_set, DGU.itemType, INTERVALS.CalendarMinute);
		model.add(r_set, VOID.uriRegexPattern, base_reg+MINUTE_ID_STEM+MIN_PATTERN_PLAIN);

		model.add(r_set, VOID.exampleResource, MinuteDoc.createResourceAndLabels(base, model, 1752, 9, 2, 23, 59));

		addGregorianSourceRef(r_set);	
		
		Resource r_yearSet, r_halfSet, r_quarterSet, r_monthSet, r_weekSet, r_daySet, r_hourSet, r_minSet, r_secSet, r_intervalSet, r_instantSet;
		
		r_yearSet=createYearSet();
		r_halfSet=createHalfSet();
		r_quarterSet=createQuarterSet();
		r_monthSet=createMonthSet();
		r_weekSet=createWeekSet();
		r_daySet=createDaySet();
		r_hourSet=createHourSet();
//		r_minSet=createMinSet();
		model.add(r_set, VOID.subset, r_secSet=createSecSet());
		model.add(r_set, VOID.subset, r_intervalSet=createIntervalSet());
		model.add(r_set, VOID.subset, r_instantSet=createInstantSet());
		
		addLinkset(r_set, r_set, r_yearSet, TIME.intervalDuring, 
				""+CALENDAR_NAME+" calendar minute to calendar year interval containment links",
				"Links between "+CALENDAR_NAME+" calendar minutes and the calendar years in which they occur.");

		addLinkset(r_set, r_set, r_halfSet, TIME.intervalDuring, 
				""+CALENDAR_NAME+" calendar minute to half year interval containment links",
				"Links between "+CALENDAR_NAME+" calendar minute and the calendar aligned half years in which they occur.");

		addLinkset(r_set, r_set, r_quarterSet, TIME.intervalDuring, 
				""+CALENDAR_NAME+" calendar minute to calendar quarter year interval containment links",
				"Links between "+CALENDAR_NAME+" calendar minutes and the calendar aligned quarter years in which they occur.");

		addLinkset(r_set, r_set, r_weekSet, TIME.intervalDuring, 
				""+CALENDAR_NAME+" calendar minute to calendar week interval containment links",
				"Links between "+CALENDAR_NAME+" calendar minutes and the ISO 8601 numbered week in which they occur.");

		addLinkset(r_set, r_set, r_daySet, TIME.intervalDuring, 
				""+CALENDAR_NAME+" calendar minutes to calendar day interval containment links",
				"Links between "+CALENDAR_NAME+" calendar minutes and the calendar day in which they occur.");

		addLinkset(r_set, r_set, r_hourSet, TIME.intervalDuring, 
				""+CALENDAR_NAME+" calendar minutes to calendar hour interval containment links",
				"Links between "+CALENDAR_NAME+" calendar minutes and the calendar hour in which they occur.");

		addLinkset(r_set, r_set, r_secSet, INTERVALS.intervalContainsSecond, 
				""+CALENDAR_NAME+" calendar minute to calendar aligned second interval containment links",
				"Links between "+CALENDAR_NAME+" calendar minutes and the calendar aligned seconds they contain.");

		addLinkset(r_set, r_set, r_instantSet, TIME.hasBeginning, 
				""+CALENDAR_NAME+" calendar minute to starting instant links",
				"Links between "+CALENDAR_NAME+" calendar minutes and their starting instant.");		

		addLinkset(r_set, r_set, r_instantSet, TIME.hasEnd, 
				""+CALENDAR_NAME+" calendar second to ending instant links",
				"Links between "+CALENDAR_NAME+" calendar minutes and their ending instant.");
		
		addLinkset(r_set, r_set, r_intervalSet, TIME.intervalEquals, 
				""+CALENDAR_NAME+" calendar minute to generic interval links",
				"Links between "+CALENDAR_NAME+" calendar minute and their corresponding generic interval.");
		
	}
}