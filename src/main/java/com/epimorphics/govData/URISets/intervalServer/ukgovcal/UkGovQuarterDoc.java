/******************************************************************
 * File:        UkGovQuarterDoc.java
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
import com.epimorphics.govData.URISets.intervalServer.ukcal.UkMonthDoc;
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
@Path(UkGovCalURITemplate.DOC_STEM+UkGovCalURITemplate.QUARTER_SEGMENT+UkGovCalURITemplate.SET_EXT_PATTERN)
public class UkGovQuarterDoc extends UkGovDoc {


	private void populateModel(int year, int quarter) {
		reset();
		this.year    = year;
		this.quarter = quarter;
		this.half    = ((quarter-1)/2)+1;
		this.month   = ((quarter-1)*3)+1;
			
		startTime = new BritishCalendar(Locale.UK);
		startTime.setLenient(false);

		startTime.set(year, Calendar.APRIL,1, 0, 0, 0);
		startTime.add(Calendar.MONTH, month-1);

		super.populateModel();
	}

	
	@GET
	@Path(QUARTER_PATTERN+EXT_PATTERN)
	@Produces()
	public Response getResponse(
			@PathParam(YEAR_TOKEN)    int year,
			@PathParam(YEAR2_TOKEN)   int year2,
			@PathParam(QUARTER_TOKEN) int quarter,
			@PathParam(EXT_TOKEN)  String ext2 ) {
		
		if(ext2.equals("") || (year2-year!=1) )
			throw new WebApplicationException(Status.NOT_FOUND);
		
		
		MediaType mt = MediaTypeUtils.extToMediaType(ext2);
		
		ext = ext2;
		return respond(year, quarter, mt, false);
	}
	
	@GET
	@Path(QUARTER_PATTERN)
	public Response getResponse2(
			@PathParam(YEAR_TOKEN) int year,
			@PathParam(YEAR2_TOKEN)   int year2,
			@PathParam(QUARTER_TOKEN) int quarter ) {
		
		if(year2-year!=1) 
			throw new WebApplicationException(Status.NOT_FOUND);
		
		MediaType mt = MediaTypeUtils.pickMediaType(hdrs.getAcceptableMediaTypes());
		ext = MediaTypeUtils.getExtOfMediaType(mt);
		return respond(year, quarter, mt, true);
	}

	private Response respond(int year, int quarter, MediaType mt, boolean addExtent) {
		base = getBaseUri();
		try {
				loc = new URI(base + ui.getPath());
				contentURI = new URI(loc.toString() + (addExtent? "."+ ext :""));
				setURI = new URI(base + SET_STEM + YEAR_SEGMENT);
		} catch (URISyntaxException e) {
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}
		
		String lang = MediaTypeUtils.getLangOfMediaType(mt);

		populateModel(year, quarter);

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

		populateQuarterSet();

		if (lang.equals("JSON"))
			return doGetJson().type(mt).contentLocation(contentURI).build();

		return doGet(lang).type(mt).contentLocation(contentURI).build();
	}
	
	public static Resource createResourceAndLabels(URI base, Model m, int year, int quarter) {
		String relPart = String.format("%04d",year) + "-" + String.format("%04d",year+1) + QUARTER_PREFIX + quarter;

		String s_quarterURI = base + QUARTER_ID_STEM + relPart;
		Resource r_quarter = m.createResource(s_quarterURI,	INTERVALS.BusinessQuarter);
		r_quarter.addProperty(RDF.type, INTERVALS.Quarter);
		
		if(quarter>0 && quarter<=4) {
			Resource r_quarterType = quarter == 1 ? INTERVALS.Q1 :
				                     quarter == 2 ? INTERVALS.Q2 :
				                     quarter == 3 ? INTERVALS.Q3 : INTERVALS.Q4;
			r_quarter.addProperty(RDF.type, r_quarterType);
		}
		
		String s_label = ""+CALENDAR_NAME+" Quarter:" + relPart;
		m.add(r_quarter, SKOS.prefLabel, s_label, "en");
		m.add(r_quarter, RDFS.label, s_label, "en");
		m.add(r_quarter, RDFS.comment, "The "
				+ ((quarter == 1) ? "first" 
				 : (quarter == 2) ? "second"
				 : (quarter == 3) ? "third" : "forth")
				+ " quarter of the "+CALENDAR_NAME+" calendar year " + String.format("%04d",year) + "-" +String.format("%04d",year+1) , "en");
		return r_quarter;
	}

	static protected Resource createResource(URI base, Model m, int year, int quarter) {
		Resource r_quarter = createResourceAndLabels(base, m,  year, quarter);
		r_quarter.addProperty(RDF.type, SCOVO.Dimension);
		
		addCalendarQoyOrdinals(r_quarter, year, quarter);

		BritishCalendar cal = new BritishCalendar(year, Calendar.APRIL, 1, 0, 0, 0); 
		
		cal.add(Calendar.MONTH, ((quarter-1)*3));
		cal.setLenient(false);

		m.add(r_quarter, INTERVALS.hasXsdDurationDescription, oneQuarter);
		m.add(r_quarter, TIME.hasDurationDescription, INTERVALS.one_quarter);
		
		
		Resource r_instant = InstantDoc.createResource(base, m, cal);	
		m.add(r_quarter, TIME.hasBeginning, r_instant);
		m.add(r_quarter, SCOVO.min, CalendarUtils.formatScvDate(cal), XSDDatatype.XSDdate);
		
		cal.add(Calendar.MONTH, 3);
		Resource r_EndInstant = InstantDoc.createResource(base, m, cal);
		m.add(r_quarter, TIME.hasEnd, r_EndInstant);

		cal.add(Calendar.SECOND, -1);
		m.add(r_quarter, SCOVO.max, CalendarUtils.formatScvDate(cal), XSDDatatype.XSDdate);

		return r_quarter;
	}

	@Override
	void addContainedIntervals() {
		Resource months[] = new Resource[3];
		
		BritishCalendar cal = new BritishCalendar(year, Calendar.APRIL, 1, 0, 0, 0); 
		cal.add(Calendar.MONTH, ((quarter-1)*3));
		
		for (int month = 0; month < 3; month++) {
			int ukCalYear  = cal.get(Calendar.YEAR);
			int ukCalMonth = cal.get(Calendar.MONTH);
			Resource r_month = UkMonthDoc.createResourceAndLabels(base, model, ukCalYear, ukCalMonth+1);
			connectToContainingInterval(model, r_thisTemporalEntity, r_month);
			months[month] = r_month;
			cal.add(Calendar.MONTH, 1);

		}

		RDFList r_months = model.createList(months);
		model.add(r_thisTemporalEntity, INTERVALS.intervalContainsMonths, r_months);
		
	}

	@Override
	void addContainingIntervals() {
		Resource r_thisYear = UkGovYearDoc.createResourceAndLabels(base, model, year);
		Resource r_thisHalf = UkGovHalfDoc.createResourceAndLabels(base, model, year, half);
		connectToContainingInterval(model, r_thisYear, r_thisTemporalEntity);
		connectToContainingInterval(model, r_thisHalf, r_thisTemporalEntity);		
	}

	@Override
	void addNeighboringIntervals() {
		BritishCalendar cal;
		Resource r_nextQuarter;
		Resource r_prevQuarter;
		int y,q;
		
		q = quarter>=4? 1: quarter+1;
		y = quarter>=4? year+1 : year;
		r_nextQuarter = createResourceAndLabels(base, model, y, q);

		q = quarter<=1? 4: quarter-1;
		y = quarter<=1? year-1 : year;
		r_prevQuarter = createResourceAndLabels(base ,model, y, q);	

		connectToNeigbours(model, r_thisTemporalEntity, r_nextQuarter, r_prevQuarter);
	}

	@Override
	void addThisTemporalEntity() {
		r_thisTemporalEntity = createResource(base, model, year, quarter);	
		r_thisTemporalEntity.addProperty(DGU.uriSet,createQuarterSet());
		
		addGeneralIntervalTimeLink(model, startTime, oneQuarter);
	}

	private void populateQuarterSet() {
		//Resource r_set = model.createResource(setURI.toString(), DGU.URIset);
		Resource r_set = createQuarterSet();
		Resource r_doc = model.createResource(loc.toString(), FOAF.Document);
		initSetModel(r_set, r_doc, QUARTER_SET_LABEL);
		
		r_thisTemporalEntity = r_set;
		
		model.add(r_set, RDFS.comment, "A dataset of "+CALENDAR_NAME+" calendar aligned time intervals of one quarter year (3 calendar month) duration" +
									   " starting at midnight on the 1st day of a given quarter year.","en");
		model.add(r_set, RDF.type, VOID.Dataset);
		
		String base_reg = base.toString().replaceAll("\\.", "\\\\.");
		
		model.add(r_set, DGU.itemType, INTERVALS.BusinessQuarter);
		model.add(r_set, VOID.uriRegexPattern, base_reg+QUARTER_ID_STEM+QUARTER_PATTERN_PLAIN);

		model.add(r_set, VOID.exampleResource, UkGovQuarterDoc.createResourceAndLabels(base, model, 1644, 3));
	
		addCalendarActRef(r_set);
		
		Resource r_yearSet, r_halfSet, r_quarterSet, r_monthSet, r_weekSet, r_daySet, r_hourSet, r_minSet, r_secSet, r_intervalSet, r_instantSet;
		
		r_yearSet=createYearSet();
		r_halfSet=createHalfSet();
//		r_quarterSet=createQuarterSet();
		r_monthSet=createMonthSet();
//		r_weekSet=createWeekSet();
//		r_daySet=createDaySet();
//		r_hourSet=createHourSet();
//		r_minSet=createMinSet();
//		r_secSet=createSecSet();
		r_intervalSet=createIntervalSet();
		r_instantSet=createInstantSet();
		
		addLinkset(r_set, r_set, r_yearSet, TIME.intervalDuring, 
				""+CALENDAR_NAME+" quarter year to year interval containment links",
				"Links between "+CALENDAR_NAME+" calendar aligned half years and years in which they occur.");

		addLinkset(r_set, r_set, r_halfSet, TIME.intervalDuring, 
				""+CALENDAR_NAME+" quarter year to half year interval containment links",
				"Links between "+CALENDAR_NAME+" calendar aligned quarter years and calendar aligned half years in which they occur.");

		addLinkset(r_set, r_set, r_monthSet, INTERVALS.intervalContainsMonth, 
				""+CALENDAR_NAME+" quarter year to month interval containment links",
				"Links between "+CALENDAR_NAME+" calendar aligned quarter years and the months they contain.");

		addLinkset(r_set, r_set, r_instantSet, TIME.hasBeginning, 
				""+CALENDAR_NAME+" quarter year to starting instant links",
				"Links between "+CALENDAR_NAME+" calendar aligned quarter years and their starting instant.");		

		addLinkset(r_set, r_set, r_instantSet, TIME.hasEnd, 
				""+CALENDAR_NAME+" quarter year to ending instant links",
				"Links between "+CALENDAR_NAME+" calendar aligned quarter years and their ending instant.");
		
		addLinkset(r_set, r_set, r_intervalSet, TIME.intervalEquals, 
				""+CALENDAR_NAME+" quarter year to generic interval links",
				"Links between "+CALENDAR_NAME+" calendar aligned quarter years and their corresponding generic interval.");		
	}
}