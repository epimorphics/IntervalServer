/******************************************************************
 * File:        UkMonthDoc.java
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

@Path(GregorianCalURITemplate.DOC_STEM+GregorianCalURITemplate.MONTH_SEGMENT+GregorianCalURITemplate.SET_EXT_PATTERN)
//@Path(GregorianCalURITemplate.MONTH_DOC_STEM)
public class MonthDoc extends Doc {

	protected void populateModel(int year, int month) {
		reset();
		this.year  = year;
		this.month = month;
		this.half =((month-1)/6)+1;
		this.quarter = ((month-1)/3)+1;
		
		startTime = new GregorianOnlyCalendar(Locale.UK);
		startTime.setLenient(false);
		startTime.set(year, month-1, day, hour, min, sec);
		
		super.populateModel();
	}
	
	
	@GET
	@Path(MONTH_PATTERN+EXT_PATTERN)
	@Produces()
	public Response getResponse(
			@PathParam(YEAR_TOKEN) int year,
			@PathParam(MONTH_TOKEN) int month,
			@PathParam(EXT_TOKEN)  String ext2 ) {
		if(ext2.equals(""))
			throw new WebApplicationException(Status.NOT_FOUND);
		
		MediaType mt = MediaTypeUtils.extToMediaType(ext2);

		ext = ext2;
		return respond(year, month, mt, false);
	}
	
	@GET
	@Path(MONTH_PATTERN)
	public Response getResponse2(
			@PathParam(YEAR_TOKEN) int year,
		@PathParam(MONTH_TOKEN) int month ) {
		MediaType mt = MediaTypeUtils.pickMediaType(hdrs.getAcceptableMediaTypes());
		ext = MediaTypeUtils.getExtOfMediaType(mt);
		return respond(year, month, mt, true);
	}

	private Response respond(int year, int month, MediaType mt, boolean addExtent) {
		base = getBaseUri();
		try {
				loc = new URI(base + ui.getPath());
				contentURI = new URI(loc.toString() + (addExtent? "."+ ext :""));
				setURI = new URI(base + SET_STEM + YEAR_SEGMENT);
		} catch (URISyntaxException e) {
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}
		
		String lang = MediaTypeUtils.getLangOfMediaType(mt);

		populateModel(year, month);

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

		populateMonthSet();

		if (lang.equals("JSON"))
			return doGetJson().type(mt).contentLocation(contentURI).build();

		return doGet(lang).type(mt).contentLocation(contentURI).build();
	}

		
	static protected Resource createResourceAndLabels(URI base, Model m, int year, int moy) {
		String relPart = String.format("%04d",year) + MONTH_PREFIX + String.format("%02d", moy);
	
		String s_monthURI = base + MONTH_ID_STEM + relPart;
		Resource r_month = m.createResource(s_monthURI, INTERVALS.CalendarMonth);
		r_month.addProperty(RDF.type, INTERVALS.Month);
		
		String s_label = ""+CALENDAR_NAME+" Month:" + relPart;
		m.add(r_month, SKOS.prefLabel, s_label, "en");
		m.add(r_month, RDFS.label, s_label, "en");
	
		GregorianOnlyCalendar cal = new GregorianOnlyCalendar(Locale.UK);
		cal.set(year, moy - 1, 01);
		String s_month = cal.getDisplayName(Calendar.MONTH, Calendar.LONG,
				Locale.UK);
	
		m.add(r_month, RDFS.comment, "The month of " + s_month
				+ " in the "+CALENDAR_NAME+" calendar year " + String.format("%04d",year) , "en");
			
		return r_month;
	}

	static protected Resource createResource(URI base, Model m, int year, int moy) {
		Resource r_month = createResourceAndLabels(base, m, year, moy);
		m.add(r_month, RDF.type, SCOVO.Dimension);
		
		addCalendarOrdinals(r_month, year, moy);

		GregorianOnlyCalendar cal = new GregorianOnlyCalendar(year, moy-1, 1, 0, 0, 0);
		cal.setLenient(false);
				
		m.add(r_month, INTERVALS.hasXsdDurationDescription, oneMonth);
		m.add(r_month, TIME.hasDurationDescription, INTERVALS.one_month );
		m.add(r_month, SCOVO.min, CalendarUtils.formatScvDate(cal), XSDDatatype.XSDdate);


		Resource r_instant = InstantDoc.createResource(base, m, cal);	
		m.add(r_month, TIME.hasBeginning, r_instant);
		cal.add(Calendar.MONTH, 1);
		Resource r_EndInstant = InstantDoc.createResource(base, m, cal);	
		m.add(r_month, TIME.hasEnd, r_EndInstant);
		
		cal.add(Calendar.SECOND, -1);
		m.add(r_month, SCOVO.max, CalendarUtils.formatScvDate(cal), XSDDatatype.XSDdate);

		return r_month;
	}

	@Override
	void addContainedIntervals() {
		ArrayList<Resource> days = new ArrayList<Resource>();
		GregorianOnlyCalendar cal = (GregorianOnlyCalendar) startTime.clone();
		while(cal.get(Calendar.MONTH)==(month-1)) {
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
		Resource r_thisYear = YearDoc.createResourceAndLabels(base, model,year);
		Resource r_thisHalf = HalfDoc.createResourceAndLabels(base, model, year, ((month-1)/6)+1);
		Resource r_thisQuarter = QuarterDoc.createResourceAndLabels(base, model,year,((month-1)/3)+1);

		// Link month to its containing year, half and quarter
		connectToContainingInterval(model, r_thisYear, r_thisTemporalEntity);
		connectToContainingInterval(model, r_thisHalf, r_thisTemporalEntity);
		connectToContainingInterval(model, r_thisQuarter, r_thisTemporalEntity);
	}

	@Override
	void addNeighboringIntervals() {
		GregorianOnlyCalendar cal;
		Resource r_nextMonth = null;
		Resource r_prevMonth = null;
		try {
			cal = (GregorianOnlyCalendar) startTime.clone();
			cal.add(Calendar.MONTH,1);
			r_nextMonth = createResourceAndLabels(base, model, cal.get(Calendar.YEAR),cal.get(Calendar.MONTH)+1);
			cal = (GregorianOnlyCalendar) startTime.clone();
			cal.add(Calendar.MONTH,-1);
			r_prevMonth = createResourceAndLabels(base, model, cal.get(Calendar.YEAR),cal.get(Calendar.MONTH)+1);
		} catch (IllegalArgumentException e) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		// Link adjacent months
		connectToNeigbours(model, r_thisTemporalEntity, r_nextMonth, r_prevMonth);
	}

	@Override
	void addThisTemporalEntity() {
		r_thisTemporalEntity = createResource(base, model, year, month);
		r_thisTemporalEntity.addProperty(DGU.uriSet,createMonthSet());
		
		addGeneralIntervalTimeLink(model, startTime, oneMonth);		
	}
	
	private void populateMonthSet() {
		//Resource r_set = model.createResource(setURI.toString(), DGU.URIset);
		Resource r_set = createMonthSet();
		Resource r_doc = model.createResource(loc.toString(), FOAF.Document);
		initSetModel(r_set, r_doc, MONTH_SET_LABEL);
		
		model.add(r_set, RDFS.comment, "A dataset of "+CALENDAR_NAME+" calendar aligned time intervals of one calendar month duration" +
									   " starting at midnight on the 1st day of a given month.","en");
		model.add(r_set, RDF.type, VOID.Dataset);
		
		String base_reg = base.toString().replaceAll("\\.", "\\\\.");
		
		model.add(r_set, DGU.itemType, INTERVALS.CalendarMonth);
		model.add(r_set, VOID.uriRegexPattern, base_reg+MONTH_ID_STEM+MONTH_PATTERN_PLAIN);

		model.add(r_set, VOID.exampleResource, MonthDoc.createResourceAndLabels(base, model, 1958, 11));
	
		addGregorianSourceRef(r_set);	
		
		Resource r_yearSet, r_halfSet, r_quarterSet, r_monthSet, r_weekSet, r_daySet, r_hourSet, r_minSet, r_secSet, r_intervalSet, r_instantSet;
		
		r_yearSet=createYearSet();
		r_halfSet=createHalfSet();
		r_quarterSet=createQuarterSet();
//		r_monthSet=createMonthSet();
//		r_weekSet=createWeekSet();
		r_daySet=createDaySet();
//		r_hourSet=createHourSet();
//		r_minSet=createMinSet();
//		r_secSet=createSecSet();
		r_intervalSet=createIntervalSet();
		r_instantSet=createInstantSet();
		
		addLinkset(r_set, r_set, r_yearSet, TIME.intervalDuring, 
				""+CALENDAR_NAME+" calendar month to calendar year interval containment links",
				"Links between "+CALENDAR_NAME+" calendar months and the calendar years in which they occur.");

		addLinkset(r_set, r_set, r_halfSet, TIME.intervalDuring, 
				""+CALENDAR_NAME+" calendar month to half year interval containment links",
				"Links between "+CALENDAR_NAME+" calendar months and the calendar aligned half years in which they occur.");

		addLinkset(r_set, r_set, r_quarterSet, TIME.intervalDuring, 
				""+CALENDAR_NAME+" calendar month to calendar quarter year interval containment links",
				"Links between "+CALENDAR_NAME+" calendar months and the calendar aligned quarter years in which they occur.");

		addLinkset(r_set, r_set, r_daySet, INTERVALS.intervalContainsDay, 
				""+CALENDAR_NAME+" calendar month to calendar day interval containment links",
				"Links between "+CALENDAR_NAME+" calendar months and the calendar days they contain.");

		addLinkset(r_set, r_set, r_instantSet, TIME.hasBeginning, 
				""+CALENDAR_NAME+" calendar month to starting instant links",
				"Links between "+CALENDAR_NAME+" calendar months and their starting instant.");		

		addLinkset(r_set, r_set, r_instantSet, TIME.hasEnd, 
				""+CALENDAR_NAME+" calendar month to ending instant links",
				"Links between "+CALENDAR_NAME+" calendar months and their ending instant.");
		
		addLinkset(r_set, r_set, r_intervalSet, TIME.intervalEquals, 
				""+CALENDAR_NAME+" calendar month to generic interval links",
				"Links between "+CALENDAR_NAME+" calendar months and their corresponding generic interval.");		
	}


}