/******************************************************************
 * File:        UkGovYearDoc.java
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
import com.epimorphics.govData.URISets.intervalServer.ukcal.UkCalURITemplate;
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
import com.hp.hpl.jena.reasoner.rulesys.builtins.AddOne;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

@Path(UkGovCalURITemplate.DOC_STEM+UkGovCalURITemplate.YEAR_SEGMENT+UkGovCalURITemplate.SET_EXT_PATTERN)
public class UkGovYearDoc extends UkGovDoc {
	
	protected void populateModel(int year) {
		reset();
		this.year = year;
		startTime = new BritishCalendar(Locale.UK);
		startTime.setLenient(false);
		startTime.set(year, Calendar.APRIL, 1, 0, 0, 0);
		startTime.getTimeInMillis();
		super.populateModel();
	}
	
	@GET
	@Path(YEAR_PATTERN+EXT_PATTERN)
	@Produces()
	public Response getResponse(
			@PathParam(YEAR_TOKEN) int year,
			@PathParam(YEAR2_TOKEN) int year2,
			@PathParam(EXT_TOKEN)  String ext2 ) {
		
		if(ext2.equals("") || (year2-year!=1) )
			throw new WebApplicationException(Status.NOT_FOUND);
		
		MediaType mt = MediaTypeUtils.extToMediaType(ext2);
		ext = ext2;
		return respond(year, mt, false);
		
	}
	
	@GET
	@Path(YEAR_PATTERN)
	public Response getResponse2(
			@PathParam(YEAR_TOKEN) int year,
		@PathParam(YEAR2_TOKEN) int year2 ) {
		
		if((year2-year!=1) )
			throw new WebApplicationException(Status.NOT_FOUND);

		MediaType mt = MediaTypeUtils.pickMediaType(hdrs.getAcceptableMediaTypes());
		ext = MediaTypeUtils.getExtOfMediaType(mt);
		return respond(year, mt, true);
		
	}

	private Response respond(int year,  MediaType mt, boolean addExtent) {
		base = getBaseUri();
		try {
				loc = new URI(base + ui.getPath());
				contentURI = new URI(loc.toString() + (addExtent? "."+ ext :""));
				setURI = new URI(base + SET_STEM + YEAR_SEGMENT);
		} catch (URISyntaxException e) {
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}
		
		String lang = MediaTypeUtils.getLangOfMediaType(mt);

		populateModel(year);

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

		populateYearSet();

		if (lang.equals("JSON"))
			return doGetJson().type(mt).contentLocation(contentURI).build();

		return doGet(lang).type(mt).contentLocation(contentURI).build();
	}

	public static Resource createResourceAndLabels(URI base, Model model, int year) {
		String s_yearURI = base + YEAR_ID_STEM + String.format("%04d",year) + "-" + String.format("%04d", year+1);

		Resource r_year = model.createResource(s_yearURI, INTERVALS.BusinessYear);
		r_year.addProperty(RDF.type, INTERVALS.Year);
		
		String s_label = CALENDAR_NAME+" Year:" + String.format("%04d",year)+ "-" + String.format("%04d", year+1);
		model.add(r_year, SKOS.prefLabel, s_label, "en");
		model.add(r_year, RDFS.label, s_label, "en");
		model.add(r_year, RDFS.comment, "The "+CALENDAR_NAME+" calendar year of " + String.format("%04d",year) + "-" + String.format("%04d", year+1) , "en");

		return r_year;
	}
	
	static protected Resource createResource(URI base, Model model, int year) {
		Resource r_year = createResourceAndLabels(base, model, year);
		model.add(r_year, RDF.type, SCOVO.Dimension);
		BritishCalendar cal = new BritishCalendar(year, Calendar.APRIL, 1, 0, 0, 0);	
		cal.setLenient(false);
		
		addCalendarOrdinals(r_year, year);
		
		model.add(r_year, INTERVALS.hasXsdDurationDescription, oneYear);
		model.add(r_year, TIME.hasDurationDescription, INTERVALS.one_year);
		
		Resource r_instant = InstantDoc.createResource(base, model, cal);	
		model.add(r_year, TIME.hasBeginning, r_instant);

		model.add(r_year, SCOVO.min, CalendarUtils.formatScvDateLiteral(cal));	
		
		cal.add(Calendar.YEAR, 1);
		Resource r_EndInstant = InstantDoc.createResource(base, model, cal);	
		model.add(r_year, TIME.hasEnd, r_EndInstant);

		cal.add(Calendar.SECOND, -1);
		model.add(r_year, SCOVO.max, CalendarUtils.formatScvDateLiteral(cal));	

		return r_year;
	}

	@Override
	protected void addContainedIntervals() {
		Resource halves[] = new Resource[2];
		Resource quarters[] = new Resource[4];
		Resource months[] = new Resource[12];
			
		// Add the Half Years
		for (int half = 0; half < 2; half++) {
			Resource r_half = UkGovHalfDoc.createResourceAndLabels(base, model, year, half+1);
			halves[half] = r_half;
			connectToContainingInterval(model, r_thisTemporalEntity, r_half);
		}
		RDFList r_halves = model.createList(halves);
		model.add(r_thisTemporalEntity, INTERVALS.intervalContainsHalves, r_halves);
	

		// Add the Quarters
		for (int quarter = 0; quarter < 4; quarter++) {
			Resource r_quarter = UkGovQuarterDoc.createResourceAndLabels(base, model, year, quarter+1);
			connectToContainingInterval(model, r_thisTemporalEntity, r_quarter);
			quarters[quarter] = r_quarter;
		}
		Resource r_quarters = model.createList(quarters);
		model.add(r_thisTemporalEntity, INTERVALS.intervalContainsQuarters, r_quarters);
		
		// Add the Months
		BritishCalendar cal = new BritishCalendar(year, Calendar.APRIL, 1, 0, 0, 0);
		for (int month = 0; month < 12; month++) {
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
	protected void addContainingIntervals() {
		//Do nothing for Years
		
	}

	@Override
	protected void addNeighboringIntervals() {
		BritishCalendar cal = (BritishCalendar) startTime.clone();
		
		cal.add(Calendar.YEAR, 1);
		Resource r_nextYear = createResourceAndLabels(base, model, cal.get(Calendar.YEAR));
		cal = (BritishCalendar) startTime.clone();
		cal.add(Calendar.YEAR, -1);
		Resource r_prevYear = createResourceAndLabels(base, model, cal.get(Calendar.YEAR));
		connectToNeigbours(model, r_thisTemporalEntity, r_nextYear, r_prevYear);
	}

	@Override
	protected void addThisTemporalEntity() {
		r_thisTemporalEntity = createResource(base, model, year);
		r_thisTemporalEntity.addProperty(DGU.uriSet,createYearSet());
		
		addGeneralIntervalTimeLink(model, startTime, oneYear);
		
	}
	
	private void populateYearSet() {
		//Resource r_set = model.createResource(setURI.toString(), DGU.URIset);
		Resource r_set = createYearSet();
		Resource r_doc = model.createResource(loc.toString(), FOAF.Document);
		initSetModel(r_set, r_doc, YEAR_SET_LABEL);
		
		r_thisTemporalEntity = r_set;
		
		model.add(r_set, RDFS.comment, "A dataset of "+CALENDAR_NAME+" calendar aligned time intervals of one year duration" +
									   " starting at midnight on the 1st of April of a given "+UkCalURITemplate.CALENDAR_NAME+"year.","en");
		model.add(r_set, RDF.type, VOID.Dataset);
		
		String base_reg = base.toString().replaceAll("\\.", "\\\\.");
		
		model.add(r_set, DGU.itemType, INTERVALS.BusinessYear);
		model.add(r_set, VOID.uriRegexPattern, base_reg+YEAR_ID_STEM+YEAR_PATTERN_PLAIN);
		
		model.add(r_set, VOID.exampleResource, createResourceAndLabels(base, model, 1752));
		
		addCalendarActRef(r_set);	
		
		Resource r_yearSet, r_halfSet, r_quarterSet, r_monthSet, r_weekSet, r_daySet, r_hourSet, r_minSet, r_secSet, r_intervalSet, r_instantSet;
		
//		r_yearSet=createYearSet();
		r_halfSet=createHalfSet();
		r_quarterSet=createQuarterSet();
		r_monthSet=createMonthSet();
//		r_weekSet=createWeekSet();
//		r_daySet=createDaySet();
//		r_hourSet=createHourSet();
//	    r_minSet=createMinSet();
//		r_secSet=createSecSet();
		r_intervalSet=createIntervalSet();
		r_instantSet=createInstantSet();
		
		addLinkset(r_set, r_set, r_halfSet, INTERVALS.intervalContainsHalf, 
				""+CALENDAR_NAME+" year to half year interval containment links",
				"Links between "+CALENDAR_NAME+" calendar aligned years and the half years they contain.");

		addLinkset(r_set, r_set, r_quarterSet, INTERVALS.intervalContainsQuarter, 
				""+CALENDAR_NAME+" year to quarter year interval containment links",
				"Links between "+CALENDAR_NAME+" calendar aligned years and the quarter years they contain.");

		addLinkset(r_set, r_set, r_monthSet, INTERVALS.intervalContainsMonth, 
				""+CALENDAR_NAME+" year to month interval containment links",
				"Links between "+CALENDAR_NAME+" calendar aligned years and the months they contain.");

		addLinkset(r_set, r_set, r_instantSet, TIME.hasBeginning, 
				""+CALENDAR_NAME+" year to starting instant links",
				"Links between "+CALENDAR_NAME+" calendar aligned years and their starting instant.");		

		addLinkset(r_set, r_set, r_instantSet, TIME.hasEnd, 
				""+CALENDAR_NAME+" year to ending instant links",
				"Links between "+CALENDAR_NAME+" calendar aligned years and their ending instant.");
		
		addLinkset(r_set, r_set, r_intervalSet, TIME.intervalEquals, 
				""+CALENDAR_NAME+" year to generic interval links",
				"Links between "+CALENDAR_NAME+" calendar aligned years and their corresponding generic interval.");		

	}

}