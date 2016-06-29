/******************************************************************
 * File:        DayDoc.java
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

@Path(GregorianCalURITemplate.DOC_STEM+GregorianCalURITemplate.DAY_SEGMENT+GregorianCalURITemplate.SET_EXT_PATTERN)
public class DayDoc extends Doc {
	
	protected void populateModel(int year, int month, int day) {
		reset();
		setWeekOfYearAndMonth(year, month, day);		
		this.year  = year;
		this.month = month;
		this.half =((month-1)/6)+1;
		this.quarter = ((month-1)/3)+1;
		this.day = day;
		
		startTime = new GregorianOnlyCalendar(Locale.UK);
		startTime.setLenient(false);
		startTime.set(year, month-1, day, hour, min, sec);
		
		super.populateModel();
	}
	
	@GET
	@Path(DAY_PATTERN+EXT_PATTERN)
	@Produces()
	public Response getResponse(
			@PathParam(YEAR_TOKEN) int year,
			@PathParam(MONTH_TOKEN) int month,
			@PathParam(DAY_TOKEN)   int day,
			@PathParam(EXT_TOKEN)  String ext2 ) {
		if(ext2.equals(""))
			throw new WebApplicationException(Status.NOT_FOUND);
		
		MediaType mt = MediaTypeUtils.extToMediaType(ext2);
		ext = ext2;
		return respond(year, month, day, mt, false);
	}
	
	@GET
	@Path(DAY_PATTERN)
	public Response getResponse2(
			@PathParam(YEAR_TOKEN) int year,
			@PathParam(MONTH_TOKEN) int month,
			@PathParam(DAY_TOKEN)   int day ) {
		MediaType mt = MediaTypeUtils.pickMediaType(hdrs.getAcceptableMediaTypes());
		ext = MediaTypeUtils.getExtOfMediaType(mt);
		return respond(year, month, day, mt, true);
	}

	private Response respond(int year,int month, int day, MediaType mt, boolean addExtent) {
		base = getBaseUri();
		try {
				loc = new URI(base + ui.getPath());
				contentURI = new URI(loc.toString() + (addExtent? "."+ ext :""));
				setURI = new URI(base + SET_STEM + YEAR_SEGMENT);
		} catch (URISyntaxException e) {
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}
		
		String lang = MediaTypeUtils.getLangOfMediaType(mt);

		populateModel(year, month, day);

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

		populateDaySet();

		if (lang.equals("JSON"))
			return doGetJson().type(mt).contentLocation(contentURI).build();

		return doGet(lang).type(mt).contentLocation(contentURI).build();
	}
	
	
	static protected Resource createResourceAndLabels(URI base, Model m, int year, int moy, int dom) {
		String relPart = String.format("%04d",year) + MONTH_PREFIX + String.format("%02d", moy)
				+ DAY_PREFIX + String.format("%02d", dom);
	
		String s_dayURI = base + DAY_ID_STEM + relPart;
		Resource r_day = m.createResource(s_dayURI, INTERVALS.CalendarDay);
		r_day.addProperty(RDF.type, INTERVALS.Day);

		
		String s_label = ""+CALENDAR_NAME+" Day:" + relPart;
		m.add(r_day, SKOS.prefLabel, s_label, "en");
		m.add(r_day, RDFS.label, s_label, "en");
	
		GregorianOnlyCalendar cal = new GregorianOnlyCalendar(Locale.UK);
		cal.set(year, moy - 1, dom);
	
		String s_month = cal.getDisplayName(Calendar.MONTH, Calendar.LONG,Locale.UK);
		String s_dayOfWeek = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.UK);
		String s_domSuffix = getDecimalSuffix(dom);
		m.add(r_day, RDFS.comment, s_dayOfWeek + " the " + dom + s_domSuffix + " of " + s_month + " in the "+CALENDAR_NAME+" calendar year " + String.format("%04d",year) , "en");
	
		return r_day;
	}

	static protected Resource createResource(URI base, Model m, int year, int moy, int dom) {
		Resource r_day = createResourceAndLabels(base, m, year, moy, dom);
		m.add(r_day, RDF.type, SCOVO.Dimension);
		GregorianOnlyCalendar cal = new GregorianOnlyCalendar(Locale.UK);
		cal.setLenient(false);
		cal.set(year, moy-1, dom,0 , 0, 0);
		cal.getTimeInMillis();
		
		addCalendarOrdinals(r_day, year, moy, dom);
		
		m.add(r_day, INTERVALS.hasXsdDurationDescription, oneDay);
		m.add(r_day, TIME.hasDurationDescription, INTERVALS.one_day);
		m.add(r_day, SCOVO.min, CalendarUtils.formatScvDateTime(cal), XSDDatatype.XSDdateTime);


		Resource r_instant = InstantDoc.createResource(base, m, cal);	
		m.add(r_day, TIME.hasBeginning, r_instant);

		int i_dow = cal.get(Calendar.DAY_OF_WEEK);
		setDayOfWeek(m, r_day, i_dow);

		cal.add(Calendar.DAY_OF_MONTH,1);
		Resource r_EndInstant = InstantDoc.createResource(base, m, cal);	
		m.add(r_day, TIME.hasEnd, r_EndInstant);
		
		cal.add(Calendar.SECOND, -1);
		m.add(r_day, SCOVO.max, CalendarUtils.formatScvDateTime(cal), XSDDatatype.XSDdateTime);


		return r_day;
	}

	@Override
	void addContainedIntervals() {
		GregorianOnlyCalendar cal = (GregorianOnlyCalendar) startTime.clone();
		ArrayList<Resource> hours = new ArrayList<Resource>();
		
		cal.set(year, month-1, day);
		int i_max_hid = cal.getActualMaximum(Calendar.HOUR_OF_DAY);
		int i_min_hid = cal.getActualMinimum(Calendar.HOUR_OF_DAY);
		
		for (int hour = i_min_hid; hour <= i_max_hid; hour++) {
			Resource r_hour = HourDoc.createResourceAndLabels(base, model, year, month, day, hour);
			connectToContainingInterval(model, r_thisTemporalEntity, r_hour);
			hours.add(r_hour);
		}
		RDFList r_hours = model.createList(hours.iterator());
		model.add(r_thisTemporalEntity, INTERVALS.intervalContainsHours, r_hours);
		
	}

	@Override
	void addContainingIntervals() {
		Resource r_thisYear = YearDoc.createResourceAndLabels(base, model,year);
		Resource r_thisQuarter = QuarterDoc.createResourceAndLabels(base, model,year,((month-1)/3)+1);
		Resource r_thisHalf = HalfDoc.createResourceAndLabels(base, model, year, ((month-1)/6)+1);
		Resource r_thisMonth = MonthDoc.createResourceAndLabels(base, model, year, month);
		
		Resource r_thisWeek = WeekOfYearDoc.createResource(base, model, woy_year, woy_week); /**/
		
		// Link day to its containing year, half, quarter and month
		connectToContainingInterval(model, r_thisYear,  r_thisTemporalEntity);
		connectToContainingInterval(model, r_thisHalf,  r_thisTemporalEntity);
		connectToContainingInterval(model, r_thisQuarter, r_thisTemporalEntity);
		connectToContainingInterval(model, r_thisMonth, r_thisTemporalEntity);

		connectToContainingInterval(model, r_thisWeek,  r_thisTemporalEntity);
	}

	@Override
	void addNeighboringIntervals() {
		Resource r_nextDay, r_prevDay;
		GregorianOnlyCalendar cal;
		
		try {
			cal = (GregorianOnlyCalendar) startTime.clone();
			cal.add(Calendar.DAY_OF_MONTH,1);
			r_nextDay = createResourceAndLabels(base, model, cal.get(Calendar.YEAR),cal.get(Calendar.MONTH)+1,cal.get(Calendar.DAY_OF_MONTH));
			cal = (GregorianOnlyCalendar) startTime.clone();
			cal.add(Calendar.DAY_OF_MONTH,-1);
			r_prevDay = createResourceAndLabels(base, model, cal.get(Calendar.YEAR),cal.get(Calendar.MONTH)+1,cal.get(Calendar.DAY_OF_MONTH));
		} catch (IllegalArgumentException e) {
			throw new WebApplicationException(e, Response.Status.NOT_FOUND);
		}		
		// Link adjacent days
		connectToNeigbours(model, r_thisTemporalEntity, r_nextDay, r_prevDay);
	}

	@Override
	void addThisTemporalEntity() {
		r_thisTemporalEntity = createResource(base, model, year, month, day);
		r_thisTemporalEntity.addProperty(DGU.uriSet,createDaySet());
		
		addGeneralIntervalTimeLink(model, startTime, oneDay);	
	}
	
	private void populateDaySet() {
		//Resource r_set = model.createResource(setURI.toString(), DGU.URIset);
		Resource r_set = createDaySet();
		Resource r_doc = model.createResource(contentURI.toString(), FOAF.Document);
		initSetModel(r_set, r_doc, DAY_SET_LABEL);
		
		r_thisTemporalEntity = r_set;
		
		model.add(r_set, RDFS.comment, "A dataset of "+CALENDAR_NAME+" calendar aligned time intervals of one calendar day duration" +
									   " starting at midnight on a given day.","en");
		model.add(r_set, RDF.type, VOID.Dataset);
		
		String base_reg = base.toString().replaceAll("\\.", "\\\\.");
		
		model.add(r_set, DGU.itemType, INTERVALS.CalendarDay);
		model.add(r_set, VOID.uriRegexPattern, base_reg+DAY_ID_STEM+DAY_PATTERN_PLAIN);

		model.add(r_set, VOID.exampleResource, DayDoc.createResourceAndLabels(base, model, 1960, 3, 12));
	
		addGregorianSourceRef(r_set);	
		
		Resource r_yearSet, r_halfSet, r_quarterSet, r_monthSet, r_weekSet, r_daySet, r_hourSet, r_minSet, r_secSet, r_intervalSet, r_instantSet;
		
		r_yearSet=createYearSet();
		r_halfSet=createHalfSet();
		r_quarterSet=createQuarterSet();
		r_monthSet=createMonthSet();
		r_weekSet=createWeekSet();
//		r_daySet=createDaySet();
		r_hourSet=createHourSet();
//		r_minSet=createMinSet();
//		r_secSet=createSecSet();
		r_intervalSet=createIntervalSet();
		r_instantSet=createInstantSet();
		
		addLinkset(r_set, r_set, r_yearSet, TIME.intervalDuring, 
				""+CALENDAR_NAME+" calendar day to calendar year interval containment links",
				"Links between "+CALENDAR_NAME+" calendar days and the calendar years in which they occur.");

		addLinkset(r_set, r_set, r_halfSet, TIME.intervalDuring, 
				""+CALENDAR_NAME+" calendar day to half year interval containment links",
				"Links between "+CALENDAR_NAME+" calendar days and the calendar aligned half years in which they occur.");

		addLinkset(r_set, r_set, r_quarterSet, TIME.intervalDuring, 
				""+CALENDAR_NAME+" calendar days to calendar quarter year interval containment links",
				"Links between "+CALENDAR_NAME+" calendar days and the calendar aligned quarter years in which they occur.");

		addLinkset(r_set, r_set, r_weekSet, TIME.intervalDuring, 
				""+CALENDAR_NAME+" calendar days to calendar week interval containment links",
				"Links between "+CALENDAR_NAME+" calendar days and the ISO 8601 numbered week in which they occur.");

		addLinkset(r_set, r_set, r_hourSet, INTERVALS.intervalContainsHour, 
				""+CALENDAR_NAME+" calendar day to calendar aligned hour interval containment links",
				"Links between "+CALENDAR_NAME+" calendar days and the calendar aligned hours they contain.");

		addLinkset(r_set, r_set, r_instantSet, TIME.hasBeginning, 
				""+CALENDAR_NAME+" calendar day to starting instant links",
				"Links between "+CALENDAR_NAME+" calendar days and their starting instant.");		

		addLinkset(r_set, r_set, r_instantSet, TIME.hasEnd, 
				""+CALENDAR_NAME+" calendar days to ending instant links",
				"Links between "+CALENDAR_NAME+" calendar days and their ending instant.");
		
		addLinkset(r_set, r_set, r_intervalSet, TIME.intervalEquals, 
				""+CALENDAR_NAME+" calendar days to generic interval links",
				"Links between "+CALENDAR_NAME+" calendar days and their corresponding generic interval.");		
	}
	
}