/******************************************************************
 * File:        WeekOfYearDoc.java
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
import java.util.ArrayList;
import java.util.Calendar;

import com.epimorphics.govData.URISets.intervalServer.BaseURI;
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

import com.epimorphics.govData.URISets.intervalServer.util.CalendarUtils;
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

@Path(GregorianURITemplate.DOC_STEM+GregorianURITemplate.WEEK_SEGMENT+GregorianURITemplate.SET_EXT_PATTERN)
public class WeekOfYearDoc extends Doc {

	protected void populateModel(int year, int week) {
		reset();
		GregorianOnlyCalendar cal = new GregorianOnlyCalendar(Locale.UK);
		try {
			CalendarUtils.setWeekOfYear(year, week, cal);	
		} catch (IllegalArgumentException e) {
			throw new WebApplicationException(e, Response.Status.NOT_FOUND);
		}
		
		this.woy_week = week;
		this.woy_year = year;
		
		this.year = cal.get(Calendar.YEAR);
		this.month = cal.get(Calendar.MONTH) + 1 - Calendar.JANUARY;
		this.day = cal.get(Calendar.DAY_OF_MONTH);
		
		startTime = new GregorianOnlyCalendar(Locale.UK);
		startTime.setLenient(false);
		startTime.set(year, month-1, day, hour, min, sec);
		
		super.populateModel();
	}
	
	@GET
	@Path(WOY_PATTERN+EXT_PATTERN)
	@Produces()
	public Response getResponse(
			@PathParam(YEAR_TOKEN) int year,
			@PathParam(WEEK_TOKEN) int week,
			@PathParam(EXT_TOKEN)  String ext2 ) {
		if(ext2.equals(""))
			throw new WebApplicationException(Status.NOT_FOUND);
		
		MediaType mt = MediaTypeUtils.extToMediaType(ext2);
		ext = ext2;
		return respond(year, week, mt, false);
	}
	
	@GET
	@Path(WOY_PATTERN)
	public Response getResponse2(
			@PathParam(YEAR_TOKEN) int year , 
			@PathParam(WEEK_TOKEN) int week ){
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
		String relPart = String.format("%04d",year) + WEEK_PREFIX + String.format("%02d", woy);
	
		String s_weekURI = base + WEEK_ID_STEM + relPart;
		Resource r_week = m.createResource(s_weekURI, INTERVALS.Iso8601Week);
		r_week.addProperty(RDF.type,INTERVALS.Week);
		
		String s_label = "Iso8601 Week:" + relPart;
		m.add(r_week, SKOS.prefLabel, s_label, "en");
		m.add(r_week, RDFS.label, s_label, "en");
	
		m.add(r_week, RDFS.comment, "Week " + woy + " of the "+CALENDAR_NAME+" calendar year " + String.format("%04d",year) );
			
		return r_week;
	}

	static protected Resource createResource(URI base, Model m, int year, int woy) {
		Resource r_week = createResourceAndLabels(base, m, year, woy);
		m.add(r_week, RDF.type, SCOVO.Dimension);


		GregorianOnlyCalendar cal = new GregorianOnlyCalendar(Locale.UK);
		CalendarUtils.setWeekOfYear(year, woy , cal);

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
		GregorianOnlyCalendar cal = (GregorianOnlyCalendar) startTime.clone();
		int i_initial_woy = cal.get(Calendar.WEEK_OF_YEAR);
		while(cal.get(Calendar.WEEK_OF_YEAR) == i_initial_woy) {
			Resource r_day = DayDoc.createResourceAndLabels(base, model, cal.get(Calendar.YEAR),cal.get(Calendar.MONTH)+1,cal.get(Calendar.DAY_OF_MONTH));
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
		GregorianOnlyCalendar cal;
		Resource r_nextWeek = null;
		Resource r_prevWeek = null;
		try {
			cal = (GregorianOnlyCalendar) startTime.clone();
			cal.add(Calendar.DATE,7);
			r_nextWeek = createResourceAndLabels(base, model, CalendarUtils.getWeekOfYearYear(cal),cal.get(Calendar.WEEK_OF_YEAR));
			cal = (GregorianOnlyCalendar) startTime.clone();
			cal.add(Calendar.DATE,-7);
			r_prevWeek = createResourceAndLabels(base, model, CalendarUtils.getWeekOfYearYear(cal) ,cal.get(Calendar.WEEK_OF_YEAR));
		} catch (IllegalArgumentException e) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		// Link adjacent months
		connectToNeigbours(model, r_thisTemporalEntity, r_nextWeek, r_prevWeek);
	}

	@Override
	void addThisTemporalEntity() {
		r_thisTemporalEntity = createResource(base, model, year, woy_week);
		r_thisTemporalEntity.addProperty(DGU.uriSet,createWeekSet());
		
		addGeneralIntervalTimeLink(model, startTime, oneWeek);		
	}
	
	private void populateWeekSet() {
		//Resource r_set = model.createResource(setURI.toString(), DGU.URIset);
		Resource r_set = createWeekSet();
		Resource r_doc = model.createResource(loc.toString(), FOAF.Document);
		initSetModel(r_set, r_doc, WEEK_SET_LABEL);
		
		model.add(r_set, RDFS.comment, "A dataset of ISO 8601 numbered "+CALENDAR_NAME+" calendar aligned time intervals of one week duration" +
									   " starting at midnight on the Monday of a given week.","en");
		model.add(r_set, RDF.type, VOID.Dataset);
		
		String base_reg = base.toString().replaceAll("\\.", "\\\\.");
		
		model.add(r_set, DGU.itemType, INTERVALS.Iso8601Week);
		model.add(r_set, VOID.uriRegexPattern, base_reg+WEEK_ID_STEM+WEEK_PATTERN_PLAIN);
		
		model.add(r_set, VOID.exampleResource, WeekOfYearDoc.createResourceAndLabels(base, model, 2009, 52 ));
	
		addGregorianSourceRef(r_set);	
		
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