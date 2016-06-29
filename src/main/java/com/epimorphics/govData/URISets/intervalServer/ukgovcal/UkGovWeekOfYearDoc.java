/******************************************************************
 * File:        UkGovWeekOfYearDoc.java
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

package com.epimorphics.govData.URISets.intervalServer.ukgovcal;

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
import com.epimorphics.govData.URISets.intervalServer.ukcal.UkDayDoc;
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

@Path(UkGovCalURITemplate.DOC_STEM+UkGovCalURITemplate.WEEK_SEGMENT+UkGovCalURITemplate.SET_EXT_PATTERN)
public class UkGovWeekOfYearDoc extends UkGovDoc {

	BritishCalendar lastYear, nextYear, thisYear;
	
	protected void populateModel(int year, int week) {
		reset();

		lastYear = getBritishCalAtGovWeekOne(year-1);
		nextYear = getBritishCalAtGovWeekOne(year+1);
		startTime = getBritishCalAtGovWeekOne(year);
		thisYear = (BritishCalendar) startTime.clone();
		
		//Advance to the start of the requested Gov Week
		startTime.add(Calendar.DATE, 7*(week-1));
		
		//We haven't gone into the following year
		if(startTime.getTimeInMillis()>=nextYear.getTimeInMillis())
			throw new WebApplicationException(Status.NOT_FOUND);
		
		this.woy_week = week;
		this.woy_year = year;
		
		this.year = year;
				
		super.populateModel();
	}

	public static BritishCalendar getBritishCalAtGovWeekOne(int year) {
		BritishCalendar cal = new BritishCalendar(Locale.UK);
		cal.setLenient(false);
		cal.set(year,Calendar.APRIL, 4, 0,0,0);
		
		// Step back to the Monday at the beginning of the week.
		while(cal.get(Calendar.DAY_OF_WEEK)!=Calendar.MONDAY) {
			cal.add(Calendar.DATE, -1);
		}
		return cal;
	}
	
	@GET
	@Path(WOY_PATTERN+EXT_PATTERN)
	@Produces()
	public Response getResponse(
			@PathParam(YEAR_TOKEN) int year,
			@PathParam(YEAR2_TOKEN) int year2,
			@PathParam(WEEK_TOKEN) int week,
			@PathParam(EXT_TOKEN)  String ext2 ) {
		
		if(ext2.equals("") || (year2-year!=1) )
			throw new WebApplicationException(Status.NOT_FOUND);
		
		MediaType mt = MediaTypeUtils.extToMediaType(ext2);
		ext = ext2;
		return respond(year, week, mt, false);
	}
	
	@GET
	@Path(WOY_PATTERN)
	public Response getResponse2(
			@PathParam(YEAR_TOKEN) int year , 
			@PathParam(YEAR2_TOKEN) int year2,
			@PathParam(WEEK_TOKEN) int week ){
		
		if(year2-year!=1)
			throw new WebApplicationException(Status.NOT_FOUND);
		
		MediaType mt = MediaTypeUtils.pickMediaType(hdrs.getAcceptableMediaTypes());
		ext = MediaTypeUtils.getExtOfMediaType(mt);
		return respond(year, week, mt, true);
	}

	private Response respond(int year, int week, MediaType mt, boolean addExtent) {
		base = getBaseUri();
		try {
				loc = new URI(base + ui.getPath());
				contentURI = new URI(loc.toString() + (addExtent? "."+ ext :""));
				setURI = new URI(base + SET_STEM + YEAR_SEGMENT);
		} catch (URISyntaxException e) {
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}
		
		String lang = MediaTypeUtils.getLangOfMediaType(mt);

		populateModel(year, week);

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

		populateWeekSet();

		if (lang.equals("JSON"))
			return doGetJson().type(mt).contentLocation(contentURI).build();

		return doGet(lang).type(mt).contentLocation(contentURI).build();
	}
		
	static protected Resource createResourceAndLabels(URI base, Model m, int year, int woy) {
		String relPart = String.format("%04d",year) + "-" +String.format("%04d",year+1) + WEEK_PREFIX + String.format("%02d", woy);
	
		String s_weekURI = base + WEEK_ID_STEM + relPart;
		Resource r_week = m.createResource(s_weekURI, INTERVALS.Iso8601Week);
		r_week.addProperty(RDF.type,INTERVALS.Week);
		
		String s_label = "Modern HMG Week:" + relPart;
		m.add(r_week, SKOS.prefLabel, s_label, "en");
		m.add(r_week, RDFS.label, s_label, "en");
	
		m.add(r_week, RDFS.comment, "Week " + woy + " of the "+CALENDAR_NAME+" calendar year " + String.format("%04d",year)+"-"+String.format("%04d",year+1),"en" );
			
		return r_week;
	}

	public static Resource createResource(URI base, Model m, int year, int woy) {
		Resource r_week = createResourceAndLabels(base, m, year, woy);
		m.add(r_week, RDF.type, SCOVO.Dimension);
		
		addCalendarWoyOrdinals(r_week, year, woy);

		BritishCalendar cal = getBritishCalAtGovWeekOne(year);
		cal.add(Calendar.DATE, (woy-1)*7);

		m.add(r_week, INTERVALS.hasXsdDurationDescription, oneWeek);
		m.add(r_week, TIME.hasDurationDescription, INTERVALS.one_week );
		m.add(r_week, SCOVO.min, CalendarUtils.formatScvDate(cal), XSDDatatype.XSDdate);

		Resource r_instant = InstantDoc.createResource(base, m, cal);	
		m.add(r_week, TIME.hasBeginning, r_instant);
		cal.add(Calendar.DATE, 7);
		Resource r_EndInstant = InstantDoc.createResource(base, m, cal);	
		m.add(r_week, TIME.hasEnd, r_EndInstant);
		
		cal.add(Calendar.SECOND, -1);
		m.add(r_week, SCOVO.max, CalendarUtils.formatScvDate(cal), XSDDatatype.XSDdate);

		return r_week;
	}

	@Override
	void addContainedIntervals() {
		ArrayList<Resource> days = new ArrayList<Resource>();
		BritishCalendar cal = (BritishCalendar) startTime.clone();
		
		int i_initial_woy = cal.get(Calendar.WEEK_OF_YEAR);

		while(cal.get(Calendar.WEEK_OF_YEAR) == i_initial_woy) {
			Resource r_day = UkDayDoc.createResourceAndLabels(base, model, cal.get(Calendar.YEAR),cal.get(Calendar.MONTH)+1,cal.get(Calendar.DAY_OF_MONTH));
			connectToContainingInterval(model, r_thisTemporalEntity, r_day);
			days.add(r_day);
			cal.add(Calendar.DAY_OF_MONTH, 1);
		}		
		RDFList r_days = model.createList(days.iterator());
		model.add(r_thisTemporalEntity,INTERVALS.intervalContainsDays, r_days);
	}

	@Override
	void addContainingIntervals() {
		//TODO
	}

	@Override
	void addNeighboringIntervals() {
		BritishCalendar cal;
		Resource r_nextWeek = null;
		Resource r_prevWeek = null;
		try {
			int y = woy_year;
			int w = woy_week;
			
			cal = (BritishCalendar) startTime.clone();
			cal.add(Calendar.DATE,7);
			
			//Add a 10000 (10sec)to force us over the line when close.
			if((cal.getTimeInMillis()+10000)>=nextYear.getTimeInMillis()) {
				y++; w=1;
			} else {
				w++;
			}
			r_nextWeek = createResourceAndLabels(base, model, y,w);
			cal = (BritishCalendar) startTime.clone();
			y = woy_year;
			w = woy_week;
			cal.add(Calendar.DATE,-7);
			
			if(cal.getTimeInMillis()<thisYear.getTimeInMillis()) {
				y--;
				// Millsec between year starts + 1 day(for leap second and other rounding errors) 
				// divided by milliseconds on a week
				w=(int)(((thisYear.getTimeInMillis()-lastYear.getTimeInMillis())+(1000*60*60+24))/(1000*60*60*24*7));
			} else {
				w--;
			}
			r_prevWeek = createResourceAndLabels(base, model, y ,w);
		} catch (IllegalArgumentException e) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		// Link adjacent months
		connectToNeigbours(model, r_thisTemporalEntity, r_nextWeek, r_prevWeek);
	}

	@Override
	void addThisTemporalEntity() {
		r_thisTemporalEntity = createResource(base, model, woy_year, woy_week);
		r_thisTemporalEntity.addProperty(DGU.uriSet,createWeekSet());
		
		
		addGeneralIntervalTimeLink(model, startTime, oneWeek);		
	}
	
	private void populateWeekSet() {
		//Resource r_set = model.createResource(setURI.toString(), DGU.URIset);
		Resource r_set = createWeekSet();
		Resource r_doc = model.createResource(loc.toString(), FOAF.Document);
		initSetModel(r_set, r_doc, WEEK_SET_LABEL);
		
		r_thisTemporalEntity = r_set;
		
		model.add(r_set, RDFS.comment, "A dataset of ISO 8601 numbered "+CALENDAR_NAME+" calendar aligned time intervals of one week duration" +
									   " starting at midnight on the Monday of a given week.","en");
		model.add(r_set, RDF.type, VOID.Dataset);
		
		String base_reg = base.toString().replaceAll("\\.", "\\\\.");
		
		model.add(r_set, DGU.itemType, INTERVALS.Iso8601Week);
		model.add(r_set, VOID.uriRegexPattern, base_reg+WEEK_ID_STEM+WEEK_PATTERN_PLAIN);
		
		model.add(r_set, VOID.exampleResource, UkGovWeekOfYearDoc.createResourceAndLabels(base, model, 2009, 52 ));
	
		addCalendarActRef(r_set);
		
		Resource r_yearSet, r_halfSet, r_quarterSet, r_monthSet, r_weekSet, r_daySet, r_hourSet, r_minSet, r_secSet, r_intervalSet, r_instantSet;
		
//		r_weekSet=createWeekSet());
		r_daySet=createDaySet();
//		r_hourSet=createHourSet();
//		r_minSet=createMinSet();
//		r_secSet=createSecSet();
		r_intervalSet=createIntervalSet();
		r_instantSet=createInstantSet();
		
		addLinkset(r_set, r_set, r_daySet, INTERVALS.intervalContainsDay, 
				""+CALENDAR_NAME+" calendar week to calendar day interval containment links",
				"Links between ISO 8601 numbered "+CALENDAR_NAME+" calendar weeks and the calendar days they contain.");

		addLinkset(r_set, r_set, r_instantSet, TIME.hasBeginning, 
				""+CALENDAR_NAME+" calendar week to starting instant links",
				"Links between ISO 8601 numbered "+CALENDAR_NAME+" calendar weeks and their starting instant.");		

		addLinkset(r_set, r_set, r_instantSet, TIME.hasEnd, 
				""+CALENDAR_NAME+" calendar week to ending instant links",
				"Links between ISO 8601 numbered "+CALENDAR_NAME+" calendar weeks and their ending instant.");
		
		addLinkset(r_set, r_set, r_intervalSet, TIME.intervalEquals, 
				""+CALENDAR_NAME+" calendar week to generic interval links",
				"Links between ISO 8601 numbered "+CALENDAR_NAME+" calendar weeks and their corresponding generic interval.");		
	}

}