/******************************************************************
 * File:        HalfDoc.java
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

package com.epimorphics.govData.URISets.intervalServer.ukcal;

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
@Path(BritishCalendarURITemplate.DOC_STEM+BritishCalendarURITemplate.HALF_SEGMENT+BritishCalendarURITemplate.SET_EXT_PATTERN)
public class HalfDoc extends Doc {

	protected void populateModel(int year, int half) {
		reset();
		this.year = year;
		this.half = half;
		this.month=((half-1)*6)+1;
		this.quarter=((half-1)*2)+1;
		
		startTime = new BritishCalendar(Locale.UK);
		startTime.setLenient(false);
		startTime.set(year, month-1, day, hour, min, sec);

		super.populateModel();
	}
	
	@GET
	@Path(HALF_PATTERN+EXT_PATTERN)
	@Produces()
	public Response getResponse(
			@PathParam(YEAR_TOKEN) int year,
			@PathParam(HALF_TOKEN) int half,
			@PathParam(EXT_TOKEN)  String ext2 ) {
		if(ext2.equals(""))
			throw new WebApplicationException(Status.NOT_FOUND);

		MediaType mt = MediaTypeUtils.extToMediaType(ext2);
		
		ext = ext2;
		return respond(year, half, mt, false);
	}
	
	@GET
	@Path(HALF_PATTERN)
	public Response getResponse2(
			@PathParam(YEAR_TOKEN) int year,
			@PathParam(QUARTER_TOKEN) int half ) {
		MediaType mt = MediaTypeUtils.pickMediaType(hdrs.getAcceptableMediaTypes());
		ext = MediaTypeUtils.getExtOfMediaType(mt);
		return respond(year, quarter, mt, true);
	}

	private Response respond(int year, int half, MediaType mt, boolean addExtent) {
		base = getBaseUri();
		try {
				loc = new URI(base + ui.getPath());
				contentURI = new URI(loc.toString() + (addExtent? "."+ ext :""));
				setURI = new URI(base + SET_STEM + YEAR_SEGMENT);
		} catch (URISyntaxException e) {
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}
		
		String lang = MediaTypeUtils.getLangOfMediaType(mt);

		populateModel(year, half);

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

		populateHalfSet();

		if (lang.equals("JSON"))
			return doGetJson().type(mt).contentLocation(contentURI).build();

		return doGet(lang).type(mt).contentLocation(contentURI).build();
	}
	static protected Resource createResourceAndLabels(URI base, Model m, int year, int half) {
		String relPart = year + HALF_PREFIX + half;
	
		String s_halfURI = base + HALF_ID_STEM + relPart;
		Resource r_half = m.createResource(s_halfURI, INTERVALS.CalendarHalf);
		
		if(half>0 && half<=4) {
			Resource r_quarterType = half == 1 ? INTERVALS.H1 : INTERVALS.H2;
			r_half.addProperty(RDF.type, r_quarterType);
		}
	
		String s_label = ""+CALENDAR_NAME+" Half:" + relPart;
		m.add(r_half, SKOS.prefLabel, s_label, "en");
		m.add(r_half, RDFS.label, s_label, "en");
		m.add(r_half, RDFS.comment, "The " + ((half == 1) ? "first" : "second")
				+ " half of "+CALENDAR_NAME+" calendar year " + year, "en");
		return r_half;
	}

	static protected Resource createResource(URI base, Model m, int year, int half) {
		Resource r_half = createResourceAndLabels(base, m, year, half);

		//Add more rdf:type'ing
		r_half.addProperty(RDF.type, SCOVO.Dimension);
		r_half.addProperty(RDF.type, INTERVALS.Half);
		if(half>0 && half<=2 ) {
			r_half.addProperty(RDF.type, (half==1 ? INTERVALS.H1 : INTERVALS.H2 ));
		}

		BritishCalendar cal = new BritishCalendar(year, (half-1)*6, 1, 0, 0, 0);
		cal.setLenient(false);
		
		m.add(r_half, INTERVALS.hasXsdDurationDescription, oneSecond);
		m.add(r_half, TIME.hasDurationDescription, INTERVALS.one_half);

		Resource r_instant = InstantDoc.createResource(base, m, cal);	
		m.add(r_half, TIME.hasBeginning, r_instant);
		m.add(r_half, SCOVO.min, CalendarUtils.formatScvDate(cal, CalendarUtils.iso8601dateformat), XSDDatatype.XSDdate);

		cal.add(Calendar.MONTH, 6);
		Resource r_EndInstant = InstantDoc.createResource(base, m, cal);	
		m.add(r_half, TIME.hasEnd, r_EndInstant);
		
		cal.add(Calendar.SECOND, -1);
		m.add(r_half, SCOVO.max, CalendarUtils.formatScvDate(cal, CalendarUtils.iso8601dateformat), XSDDatatype.XSDdate);

		return r_half;
	}

	@Override
	void addContainedIntervals() {
		Resource quarters[] = new Resource[2];
		Resource months[] = new Resource[6];

		// Add the Quarters
		for (int quarter = 0; quarter < 2; quarter++) {
			int i_qoy = ((half - 1) * 2) + quarter + 1;
			Resource r_quarter = QuarterDoc.createResourceAndLabels(base, model, year, i_qoy);
			connectToContainingInterval(model, r_thisTemporalEntity, r_quarter);	
			quarters[quarter] = r_quarter;
		}
		RDFList r_quarters = model.createList(quarters);
		model.add(r_thisTemporalEntity, INTERVALS.intervalContainsQuarters, r_quarters);

		// Add the Months
		for (int month = 0; month < 6; month++) {
			int i_moy = ((half - 1) * 6) + month + 1;
			Resource r_month = MonthDoc.createResourceAndLabels(base, model, year, i_moy);	
			connectToContainingInterval(model, r_thisTemporalEntity, r_month);
			months[month] = r_month;
		}
		RDFList r_months = model.createList(months);
		model.add(r_thisTemporalEntity, INTERVALS.intervalContainsMonths, r_months);
	}

	@Override
	void addContainingIntervals() {
		Resource r_year = YearDoc.createResourceAndLabels(base, model, year);
		connectToContainingInterval(model, r_year, r_thisTemporalEntity);
	}

	@Override
	void addNeighboringIntervals() {
		BritishCalendar cal;
		Resource r_nextHalf;
		Resource r_prevHalf;

		try{
			cal = (BritishCalendar) startTime.clone();
			cal.getTimeInMillis();
			cal.add(Calendar.MONTH,6);
			r_nextHalf = createResourceAndLabels(base, model, cal.get(Calendar.YEAR), (cal.get(Calendar.MONTH)/6)+1);
			
			cal = (BritishCalendar) startTime.clone();
			cal.add(Calendar.MONTH,-6);
			r_prevHalf = createResourceAndLabels(base ,model, cal.get(Calendar.YEAR), (cal.get(Calendar.MONTH)/6)+1);	
			
		} catch (IllegalArgumentException e){
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		connectToNeigbours(model, r_thisTemporalEntity, r_nextHalf, r_prevHalf);
	}

	@Override
	void addThisTemporalEntity() {
		r_thisTemporalEntity = createResource(base, model, year, half);
		r_thisTemporalEntity.addProperty(DGU.uriSet,createHalfSet());
		
		addGeneralIntervalTimeLink(model, startTime, oneHalf);
	}
	private void populateHalfSet() {
		//Resource r_set = model.createResource(setURI.toString(), DGU.URIset);
		Resource r_set = createHalfSet();
		Resource r_doc = model.createResource(loc.toString(), FOAF.Document);
		initSetModel(r_set, r_doc, HALF_SET_LABEL);
		
		model.add(r_set, RDFS.comment, "A dataset of "+CALENDAR_NAME+" calendar aligned time intervals of one half year (6 calendar month) duration" +
									   " starting at midnight on the 1st day of a given half year.","en");
		model.add(r_set, RDF.type, VOID.Dataset);
		
		String base_reg = base.toString().replaceAll("\\.", "\\\\.");
		
		model.add(r_set, DGU.itemType, INTERVALS.CalendarHalf);
		model.add(r_set, VOID.uriRegexPattern, base_reg+HALF_ID_STEM+HALF_PATTERN_PLAIN);
		
		model.add(r_set, VOID.exampleResource, HalfDoc.createResourceAndLabels(base, model, 2010, 1));
		
		addGregorianSourceRef(r_set);	
		
		Resource r_yearSet, r_halfSet, r_quarterSet, r_monthSet, r_weekSet, r_daySet, r_hourSet, r_minSet, r_secSet, r_intervalSet, r_instantSet;
		
		r_yearSet=createYearSet();
//		r_halfSet=createHalfSet();
		r_quarterSet=createQuarterSet();
		r_monthSet=createMonthSet();
//		r_weekSet=createWeekSet();
//		r_daySet=createDaySet();
//		r_hourSet=createHourSet();
//		r_minSet=createMinSet();
//		r_secSet=createSecSet();
		r_intervalSet=createIntervalSet();
		r_instantSet=createInstantSet();
		
		addLinkset(r_set, r_set, r_yearSet, TIME.intervalDuring, 
				""+CALENDAR_NAME+" half year to year interval containment links",
				"Links between "+CALENDAR_NAME+" calandar aligned half years and years in which they occur.");

		addLinkset(r_set, r_set, r_quarterSet, INTERVALS.intervalContainsQuarter, 
				""+CALENDAR_NAME+" half year to quarter year interval containment links",
				"Links between "+CALENDAR_NAME+" calandar aligned half years and the quarter years they contain.");

		addLinkset(r_set, r_set, r_monthSet, INTERVALS.intervalContainsMonth, 
				""+CALENDAR_NAME+" half year to month interval containment links",
				"Links between "+CALENDAR_NAME+" calandar aligned half years and the months they contain.");

		addLinkset(r_set, r_set, r_instantSet, TIME.hasBeginning, 
				""+CALENDAR_NAME+" half year to starting instant links",
				"Links between "+CALENDAR_NAME+" calandar aligned half years and their starting instant.");		

		addLinkset(r_set, r_set, r_instantSet, TIME.hasEnd, 
				""+CALENDAR_NAME+" half year to ending instant links",
				"Links between "+CALENDAR_NAME+" calandar aligned half years and their ending instant.");
		
		addLinkset(r_set, r_set, r_intervalSet, TIME.intervalEquals, 
				""+CALENDAR_NAME+" half year to generic interval links",
				"Links between "+CALENDAR_NAME+" calandar aligned half years and their corresponding generic interval.");		
	}

}

