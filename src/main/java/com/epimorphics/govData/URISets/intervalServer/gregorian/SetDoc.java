/******************************************************************
 * File:        SetDoc.java
 * Created by:  skw
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

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import com.epimorphics.govData.URISets.intervalServer.util.Duration;
import com.epimorphics.govData.URISets.intervalServer.util.GregorianOnlyCalendar;
import com.epimorphics.govData.URISets.intervalServer.util.MediaTypeUtils;
import com.epimorphics.govData.vocabulary.DCTERMS;
import com.epimorphics.govData.vocabulary.DGU;
import com.epimorphics.govData.vocabulary.FOAF;
import com.epimorphics.govData.vocabulary.INTERVALS;
import com.epimorphics.govData.vocabulary.PROVENANCE;
import com.epimorphics.govData.vocabulary.SCOVO;
import com.epimorphics.govData.vocabulary.SKOS;
import com.epimorphics.govData.vocabulary.TIME;
import com.epimorphics.govData.vocabulary.VOID;
import com.epimorphics.jsonrdf.Encoder;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.DCTypes;
import com.hp.hpl.jena.vocabulary.DC_11;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;

@Path(URITemplate.SET_DOC_STEM)
public class SetDoc extends URITemplate {
	@Context
	UriInfo ui;
	@Context
	HttpHeaders hdrs;

	URI loc;
	URI base;
	URI contentURI;
	URI setURI;
	
	
	static final String CALENDAR_ACT_URI="http://www.legislation.gov.uk/id/apgb/Geo2/24/23";
	static final String GREGORIAN_CALENDAR_REF ="http://en.wikipedia.org/wiki/Gregorian_calendar";
	
	static final String DBPEDIA_SUBJECT_GREGORIAN_CALENDAR 	= "http://dbpedia.org/resources/Gregorian_calendar";
	static final String DBPEDIA_SUBJECT_YEAR 				= "http://dbpedia.org/resources/Year";
	static final String DBPEDIA_SUBJECT_MONTH 				= "http://dbpedia.org/resources/Month";
	static final String DBPEDIA_SUBJECT_QUARTER 			= "http://dbpedia.org/resource/Fiscal_quarter";
//	static final String DBPEDIA_SUBJECT_HALF 				= "http://dbpedia.org/resources/Year";
	static final String DBPEDIA_SUBJECT_WEEK 				= "http://dbpedia.org/resources/Week";
	static final String DBPEDIA_SUBJECT_DAY 				= "http://dbpedia.org/resources/Day";
	static final String DBPEDIA_SUBJECT_HOUR 				= "http://dbpedia.org/resources/Hour";
	static final String DBPEDIA_SUBJECT_MINUTE				= "http://dbpedia.org/resources/Minute";
	static final String DBPEDIA_SUBJECT_SECOND 				= "http://dbpedia.org/resources/Second";
//	static final String DBPEDIA_SUBJECT_INSTANT 			= "http://dbpedia.org/resources/Year";
	static final String DBPEDIA_SUBJECT_INTERVAL 			= "http://dbpedia.org/resources/Interval_(time)";
	
	Model model = ModelFactory.createDefaultModel();
	{ setNamespaces();};

	/*********************************************************************************
	 * 
	 * Top-level Gregorian URI Set
	 * 
	 ********************************************************************************/
	@GET
	@Path(CALENDAR_STEM)
	@Produces( { "application/rdf+xml", "text/turtle", "application/json",
			"text/n3", "application/x-turtle", "text/plain" })
	public Response calSetGetResponse() {
		MediaType mt = MediaTypeUtils.pickMediaType(hdrs.getAcceptableMediaTypes());
		String lang = MediaTypeUtils.getLangOfMediaType(mt);
		String ext = MediaTypeUtils.getExtOfMediaType(mt);

		loc = ui.getAbsolutePath();
		base = ui.getBaseUri();
		try {
			contentURI = new URI(loc.toString() + ext);
			setURI = new URI(base + SET_STEM + CALENDAR_STEM);
		} catch (URISyntaxException e) {
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}

		populateCalSet();

		if (lang.equals("JSON"))
			return doGetJson().contentLocation(contentURI).build();

		return doGet(lang).type(mt).contentLocation(contentURI).build();
	}

	@GET
	@Path(CALENDAR_STEM + EXT_PATTERN)
	public Response calSetGetRDFResponse(@PathParam(EXT_TOKEN) String ext) {
		MediaType mt = MediaTypeUtils.extToMediaType(ext);
		String lang = MediaTypeUtils.getLangOfMediaType(mt);


		loc = ui.getAbsolutePath();
		base = ui.getBaseUri();
		contentURI = loc;
	
		try {
			setURI = new URI(base + SET_STEM + CALENDAR_STEM);
		} catch (URISyntaxException e) {
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}

		populateCalSet();
		return doGet(lang).type(mt).contentLocation(contentURI).build();
	}
		
	private void populateCalSet() {
		Resource r_set = model.createResource(setURI.toString(), DGU.URIset);
		Resource r_doc = model.createResource(contentURI.toString(), FOAF.Document);
		initModel(r_set, r_doc);
		
		
		model.add(r_set, RDFS.label, "Gregorian calendar aligned intervals.","en");
		model.add(r_set, SKOS.prefLabel, "Gregorian calendar aligned intervals.","en");
		
		model.add(r_set, RDFS.comment, "A dataset of Gregorian Calendar aligned time intervals formed from the union" +
				                     " of datasets that contain calendar aligned intervals one year, one half year," +
				                     " one quarter, one month, one day, one hour, one minute or one second.", "en");
		model.add(r_set, RDF.type, VOID.Dataset);
		
		String base_reg = base.toString().replaceAll("\\.", "\\\\.");
		
		model.add(r_set, DGU.itemType, INTERVALS.Interval);
//		model.add(r_set, VOID.uriRegexPattern, base_reg+YEAR_ID_STEM+YEAR_PATTERN_PLAIN);
//		model.add(r_set, VOID.uriRegexPattern, base_reg+HALF_ID_STEM+HALF_PATTERN_PLAIN);
//		model.add(r_set, VOID.uriRegexPattern, base_reg+QUARTER_ID_STEM+QUARTER_PATTERN_PLAIN);
//		model.add(r_set, VOID.uriRegexPattern, base_reg+MONTH_ID_STEM+MONTH_PATTERN_PLAIN);
//		model.add(r_set, VOID.uriRegexPattern, base_reg+DAY_ID_STEM+DAY_PATTERN_PLAIN);
//		model.add(r_set, VOID.uriRegexPattern, base_reg+HOUR_ID_STEM+HOUR_PATTERN_PLAIN);
//		model.add(r_set, VOID.uriRegexPattern, base_reg+MINUTE_ID_STEM+MIN_PATTERN_PLAIN);
//		model.add(r_set, VOID.uriRegexPattern, base_reg+SECOND_ID_STEM+SEC_PATTERN_PLAIN);
//		model.add(r_set, VOID.uriRegexPattern, base_reg+INSTANT_ID_STEM+INSTANT_PATTERN_PLAIN);
//		model.add(r_set, VOID.uriRegexPattern, base_reg+INTERVAL_ID_STEM+INTERVAL_PATTERN_PLAIN);
		
		model.add(r_set, VOID.uriRegexPattern, base_reg+ID_STEM+CALENDAR_STEM+SUMMARY_PATTERN_PLAIN);
		
		model.add(r_set, VOID.exampleResource, YearDoc.createResourceAndLabels(base, model, 1752));
		model.add(r_set, VOID.exampleResource, HalfDoc.createResourceAndLabels(base, model, 2010, 1));
		model.add(r_set, VOID.exampleResource, QuarterDoc.createResourceAndLabels(base, model, 1644, 3));
		model.add(r_set, VOID.exampleResource, MonthDoc.createResourceAndLabels(base, model, 1958, 11));
		model.add(r_set, VOID.exampleResource, WeekOfYearDoc.createResourceAndLabels(base, model, 2009, 52 ));
		model.add(r_set, VOID.exampleResource, DayDoc.createResourceAndLabels(base, model, 1960, 3, 12));
		model.add(r_set, VOID.exampleResource, HourDoc.createResourceAndLabels(base, model, 1752, 9, 2, 23));
		model.add(r_set, VOID.exampleResource, MinuteDoc.createResourceAndLabels(base, model, 1752, 9, 2, 23, 59));
		model.add(r_set, VOID.exampleResource, SecDoc.createResourceAndLabels(base, model, 1234, 4, 1, 22, 35, 41));
		model.add(r_set, VOID.exampleResource, InstantDoc.createResource(base, model, new GregorianOnlyCalendar(1977, 10, 1, 12, 22, 45)));
		model.add(r_set, VOID.exampleResource, IntervalDoc.createResourceAndLabels(base, model, new GregorianOnlyCalendar(1977, 10, 1, 12, 22, 45), new Duration("P2Y1MT1H6S") ));
		

		addGregorianSourceRef(r_set);
		
		Resource r_yearSet, r_halfSet, r_quarterSet, r_monthSet, r_weekSet, r_daySet, r_hourSet, r_minSet, r_secSet, r_intervalSet, r_instantSet;
		
		model.add(r_set, VOID.subset, r_yearSet=createYearSet());
		model.add(r_set, VOID.subset, r_halfSet=createHalfSet());
		model.add(r_set, VOID.subset, r_quarterSet=createQuarterSet());
		model.add(r_set, VOID.subset, r_monthSet=createMonthSet());
		model.add(r_set, VOID.subset, r_weekSet=createWeekSet());
		model.add(r_set, VOID.subset, r_daySet=createDaySet());
		model.add(r_set, VOID.subset, r_hourSet=createHourSet());
		model.add(r_set, VOID.subset, r_minSet=createMinSet());
		model.add(r_set, VOID.subset, r_secSet=createSecSet());
		model.add(r_set, VOID.subset, r_intervalSet=createIntervalSet());
		model.add(r_set, VOID.subset, r_instantSet=createInstantSet());
			
	}



	/*********************************************************************************
	 * 
	 * Gregorian Year Interval URI Set
	 * 
	 ********************************************************************************/

	@GET
	@Path(YEAR_SEGMENT)
	@Produces( { "application/rdf+xml", "text/turtle", "application/json",
			"text/n3", "application/x-turtle", "text/plain" })

	public Response yearSetGetResponse() {
		MediaType mt = MediaTypeUtils.pickMediaType(hdrs.getAcceptableMediaTypes());
		String lang = MediaTypeUtils.getLangOfMediaType(mt);
		String ext = MediaTypeUtils.getExtOfMediaType(mt);

		loc = ui.getAbsolutePath();
		base = ui.getBaseUri();
		try {
			contentURI = new URI(loc.toString() + ext);
			setURI = new URI(base + SET_STEM + YEAR_SEGMENT);
		} catch (URISyntaxException e) {
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}

		populateYearSet();

		if (lang.equals("JSON"))
			return doGetJson().contentLocation(contentURI).build();

		return doGet(lang).type(mt).contentLocation(contentURI).build();
	}

	@GET
	@Path(YEAR_SEGMENT + EXT_PATTERN)
	public Response yearSetGetRDFResponse(@PathParam(EXT_TOKEN) String ext) {
		MediaType mt = MediaTypeUtils.extToMediaType(ext);
		String lang = MediaTypeUtils.getLangOfMediaType(mt);


		loc = ui.getAbsolutePath();
		base = ui.getBaseUri();
		contentURI = loc;
	
		try {
			setURI = new URI(base + SET_STEM + YEAR_SEGMENT);
		} catch (URISyntaxException e) {
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}

		populateYearSet();
		
		return doGet(lang).type(mt).contentLocation(contentURI).build();
	}
		
	private void populateYearSet() {
		//Resource r_set = model.createResource(setURI.toString(), DGU.URIset);
		Resource r_set = createYearSet();
		Resource r_doc = model.createResource(contentURI.toString(), FOAF.Document);
		initModel(r_set, r_doc);
		
		model.add(r_set, RDFS.comment, "A dataset of Gregorian calendar aligned time intervals of one year duration" +
									   " starting at midnight on the 1st of January of a given year.","en");
		model.add(r_set, RDF.type, VOID.Dataset);
		
		String base_reg = base.toString().replaceAll("\\.", "\\\\.");
		
		model.add(r_set, DGU.itemType, INTERVALS.CalendarYear);
		model.add(r_set, VOID.uriRegexPattern, base_reg+YEAR_ID_STEM+YEAR_PATTERN_PLAIN);
		
		model.add(r_set, VOID.exampleResource, YearDoc.createResourceAndLabels(base, model, 1752));
		
		addGregorianSourceRef(r_set);	
		
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
				"Gregorian year to half year interval containment links",
				"Links between Gregorian calandar aligned years and the half years they contain.");

		addLinkset(r_set, r_set, r_quarterSet, INTERVALS.intervalContainsQuarter, 
				"Gregorian year to quarter year interval containment links",
				"Links between Gregorian calandar aligned years and the quarter years they contain.");

		addLinkset(r_set, r_set, r_monthSet, INTERVALS.intervalContainsMonth, 
				"Gregorian year to month interval containment links",
				"Links between Gregorian calandar aligned years and the months they contain.");

		addLinkset(r_set, r_set, r_instantSet, TIME.hasBeginning, 
				"Gregorian year to starting instant links",
				"Links between Gregorian calandar aligned years and their starting instant.");		

		addLinkset(r_set, r_set, r_instantSet, TIME.hasEnd, 
				"Gregorian year to ending instant links",
				"Links between Gregorian calandar aligned years and their ending instant.");
		
		addLinkset(r_set, r_set, r_intervalSet, TIME.intervalEquals, 
				"Gregorian year to generic interval links",
				"Links between Gregorian calandar aligned years and their corresponding generic interval.");		

	}

	/*********************************************************************************
	 *
	 * Gregorian Half Year URI Set
	 * 
	 ********************************************************************************/
	@GET
	@Path(HALF_SEGMENT)
	@Produces( { "application/rdf+xml", "text/turtle", "application/json",
			"text/n3", "application/x-turtle", "text/plain" })

	public Response halfSetGetResponse() {
		MediaType mt = MediaTypeUtils.pickMediaType(hdrs.getAcceptableMediaTypes());
		String lang = MediaTypeUtils.getLangOfMediaType(mt);
		String ext = MediaTypeUtils.getExtOfMediaType(mt);

		loc = ui.getAbsolutePath();
		base = ui.getBaseUri();
		try {
			contentURI = new URI(loc.toString() + ext);
			setURI = new URI(base + SET_STEM + HALF_SEGMENT);
		} catch (URISyntaxException e) {
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}

		populateHalfSet();

		if (lang.equals("JSON"))
			return doGetJson().contentLocation(contentURI).build();

		return doGet(lang).type(mt).contentLocation(contentURI).build();
	}

	@GET
	@Path(HALF_SEGMENT + EXT_PATTERN)
	public Response halfSetGetRDFResponse(@PathParam(EXT_TOKEN) String ext) {
		MediaType mt = MediaTypeUtils.extToMediaType(ext);
		String lang = MediaTypeUtils.getLangOfMediaType(mt);


		loc = ui.getAbsolutePath();
		base = ui.getBaseUri();
		contentURI = loc;
	
		try {
			setURI = new URI(base + SET_STEM + HALF_SEGMENT);
		} catch (URISyntaxException e) {
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}

		populateHalfSet();
		
		return doGet(lang).type(mt).contentLocation(contentURI).build();
	}
		
	private void populateHalfSet() {
		//Resource r_set = model.createResource(setURI.toString(), DGU.URIset);
		Resource r_set = createHalfSet();
		Resource r_doc = model.createResource(contentURI.toString(), FOAF.Document);
		initModel(r_set, r_doc);
		
		model.add(r_set, RDFS.comment, "A dataset of Gregorian calendar aligned time intervals of one half year (6 calendar month) duration" +
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
				"Gregorian half year to year interval containment links",
				"Links between Gregorian calandar aligned half years and years in which they occur.");

		addLinkset(r_set, r_set, r_quarterSet, INTERVALS.intervalContainsQuarter, 
				"Gregorian half year to quarter year interval containment links",
				"Links between Gregorian calandar aligned half years and the quarter years they contain.");

		addLinkset(r_set, r_set, r_monthSet, INTERVALS.intervalContainsMonth, 
				"Gregorian half year to month interval containment links",
				"Links between Gregorian calandar aligned half years and the months they contain.");

		addLinkset(r_set, r_set, r_instantSet, TIME.hasBeginning, 
				"Gregorian half year to starting instant links",
				"Links between Gregorian calandar aligned half years and their starting instant.");		

		addLinkset(r_set, r_set, r_instantSet, TIME.hasEnd, 
				"Gregorian half year to ending instant links",
				"Links between Gregorian calandar aligned half years and their ending instant.");
		
		addLinkset(r_set, r_set, r_intervalSet, TIME.intervalEquals, 
				"Gregorian half year to generic interval links",
				"Links between Gregorian calandar aligned half years and their corresponding generic interval.");		
	}

	/*********************************************************************************
	 *
	 * Gregorian Quarter Year URI Set
	 * 
	 ********************************************************************************/
	@GET
	@Path(QUARTER_SEGMENT)
	@Produces( { "application/rdf+xml", "text/turtle", "application/json",
			"text/n3", "application/x-turtle", "text/plain" })

	public Response quarterSetGetResponse() {
		MediaType mt = MediaTypeUtils.pickMediaType(hdrs.getAcceptableMediaTypes());
		String lang = MediaTypeUtils.getLangOfMediaType(mt);
		String ext = MediaTypeUtils.getExtOfMediaType(mt);

		loc = ui.getAbsolutePath();
		base = ui.getBaseUri();
		try {
			contentURI = new URI(loc.toString() + ext);
			setURI = new URI(base + SET_STEM + QUARTER_SEGMENT);
		} catch (URISyntaxException e) {
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}

		populateQuarterSet();

		if (lang.equals("JSON"))
			return doGetJson().contentLocation(contentURI).build();

		return doGet(lang).type(mt).contentLocation(contentURI).build();
	}

	@GET
	@Path(QUARTER_SEGMENT + EXT_PATTERN)
	public Response quarterSetGetRDFResponse(@PathParam(EXT_TOKEN) String ext) {
		MediaType mt = MediaTypeUtils.extToMediaType(ext);
		String lang = MediaTypeUtils.getLangOfMediaType(mt);


		loc = ui.getAbsolutePath();
		base = ui.getBaseUri();
		contentURI = loc;
	
		try {
			setURI = new URI(base + SET_STEM + QUARTER_SEGMENT);
		} catch (URISyntaxException e) {
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}

		populateQuarterSet();
		
		return doGet(lang).type(mt).contentLocation(contentURI).build();
	}
		
	private void populateQuarterSet() {
		//Resource r_set = model.createResource(setURI.toString(), DGU.URIset);
		Resource r_set = createQuarterSet();
		Resource r_doc = model.createResource(contentURI.toString(), FOAF.Document);
		initModel(r_set, r_doc);
		
		model.add(r_set, RDFS.comment, "A dataset of Gregorian calendar aligned time intervals of one quarter year (3 calendar month) duration" +
									   " starting at midnight on the 1st day of a given quarter year.","en");
		model.add(r_set, RDF.type, VOID.Dataset);
		
		String base_reg = base.toString().replaceAll("\\.", "\\\\.");
		
		model.add(r_set, DGU.itemType, INTERVALS.CalendarQuarter);
		model.add(r_set, VOID.uriRegexPattern, base_reg+QUARTER_ID_STEM+QUARTER_PATTERN_PLAIN);

		model.add(r_set, VOID.exampleResource, QuarterDoc.createResourceAndLabels(base, model, 1644, 3));
	
		addGregorianSourceRef(r_set);	
		
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
				"Gregorian quarter year to year interval containment links",
				"Links between Gregorian calandar aligned half years and years in which they occur.");

		addLinkset(r_set, r_set, r_halfSet, TIME.intervalDuring, 
				"Gregorian quarter year to half year interval containment links",
				"Links between Gregorian calandar aligned quarter years and calendar aligned half years in which they occur.");

		addLinkset(r_set, r_set, r_monthSet, INTERVALS.intervalContainsMonth, 
				"Gregorian quarter year to month interval containment links",
				"Links between Gregorian calandar aligned quarter years and the months they contain.");

		addLinkset(r_set, r_set, r_instantSet, TIME.hasBeginning, 
				"Gregorian quarter year to starting instant links",
				"Links between Gregorian calandar aligned quarter years and their starting instant.");		

		addLinkset(r_set, r_set, r_instantSet, TIME.hasEnd, 
				"Gregorian quarter year to ending instant links",
				"Links between Gregorian calandar aligned quarter years and their ending instant.");
		
		addLinkset(r_set, r_set, r_intervalSet, TIME.intervalEquals, 
				"Gregorian quarter year to generic interval links",
				"Links between Gregorian calandar aligned quarter years and their corresponding generic interval.");		
	}
	
	/*********************************************************************************
	 *
	 * Gregorian Month URI Set
	 * 
	 ********************************************************************************/
	@GET
	@Path(MONTH_SEGMENT)
	@Produces( { "application/rdf+xml", "text/turtle", "application/json",
			"text/n3", "application/x-turtle", "text/plain" })

	public Response monthSetGetResponse() {
		MediaType mt = MediaTypeUtils.pickMediaType(hdrs.getAcceptableMediaTypes());
		String lang = MediaTypeUtils.getLangOfMediaType(mt);
		String ext = MediaTypeUtils.getExtOfMediaType(mt);

		loc = ui.getAbsolutePath();
		base = ui.getBaseUri();
		try {
			contentURI = new URI(loc.toString() + ext);
			setURI = new URI(base + SET_STEM + MONTH_SEGMENT);
		} catch (URISyntaxException e) {
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}

		populateMonthSet();

		if (lang.equals("JSON"))
			return doGetJson().contentLocation(contentURI).build();

		return doGet(lang).type(mt).contentLocation(contentURI).build();
	}

	@GET
	@Path(MONTH_SEGMENT + EXT_PATTERN)
	public Response monthSetGetRDFResponse(@PathParam(EXT_TOKEN) String ext) {
		MediaType mt = MediaTypeUtils.extToMediaType(ext);
		String lang = MediaTypeUtils.getLangOfMediaType(mt);

		loc = ui.getAbsolutePath();
		base = ui.getBaseUri();
		contentURI = loc;
	
		try {
			setURI = new URI(base + SET_STEM + MONTH_SEGMENT);
		} catch (URISyntaxException e) {
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}

		populateMonthSet();
		
		return doGet(lang).type(mt).contentLocation(contentURI).build();
	}
		
	private void populateMonthSet() {
		//Resource r_set = model.createResource(setURI.toString(), DGU.URIset);
		Resource r_set = createMonthSet();
		Resource r_doc = model.createResource(contentURI.toString(), FOAF.Document);
		initModel(r_set, r_doc);
		
		model.add(r_set, RDFS.comment, "A dataset of Gregorian calendar aligned time intervals of one calendar month duration" +
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
				"Gregorian calendar month to calendar year interval containment links",
				"Links between Gregorian calendar months and the calendar years in which they occur.");

		addLinkset(r_set, r_set, r_halfSet, TIME.intervalDuring, 
				"Gregorian calendar month to half year interval containment links",
				"Links between Gregorian calendar months and the calendar aligned half years in which they occur.");

		addLinkset(r_set, r_set, r_quarterSet, TIME.intervalDuring, 
				"Gregorian calendar month to calendar quarter year interval containment links",
				"Links between Gregorian calendar months and the calendar aligned quarter years in which they occur.");

		addLinkset(r_set, r_set, r_daySet, INTERVALS.intervalContainsDay, 
				"Gregorian calendar month to calendar day interval containment links",
				"Links between Gregorian calendar months and the calendar days they contain.");

		addLinkset(r_set, r_set, r_instantSet, TIME.hasBeginning, 
				"Gregorian calendar month to starting instant links",
				"Links between Gregorian calendar months and their starting instant.");		

		addLinkset(r_set, r_set, r_instantSet, TIME.hasEnd, 
				"Gregorian calendar month to ending instant links",
				"Links between Gregorian calendar months and their ending instant.");
		
		addLinkset(r_set, r_set, r_intervalSet, TIME.intervalEquals, 
				"Gregorian calendar month to generic interval links",
				"Links between Gregorian calendar months and their corresponding generic interval.");		
	}

	/*********************************************************************************
	 *
	 * Gregorian Day URI Set
	 * 
	 ********************************************************************************/
	@GET
	@Path(DAY_SEGMENT)
	@Produces( { "application/rdf+xml", "text/turtle", "application/json",
			"text/n3", "application/x-turtle", "text/plain" })

	public Response daySetGetResponse() {
		MediaType mt = MediaTypeUtils.pickMediaType(hdrs.getAcceptableMediaTypes());
		String lang = MediaTypeUtils.getLangOfMediaType(mt);
		String ext = MediaTypeUtils.getExtOfMediaType(mt);

		loc = ui.getAbsolutePath();
		base = ui.getBaseUri();
		try {
			contentURI = new URI(loc.toString() + ext);
			setURI = new URI(base + SET_STEM + DAY_SEGMENT);
		} catch (URISyntaxException e) {
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}

		populateDaySet();

		if (lang.equals("JSON"))
			return doGetJson().contentLocation(contentURI).build();

		return doGet(lang).type(mt).contentLocation(contentURI).build();
	}

	@GET
	@Path(DAY_SEGMENT + EXT_PATTERN)
	public Response daySetGetRDFResponse(@PathParam(EXT_TOKEN) String ext) {
		MediaType mt = MediaTypeUtils.extToMediaType(ext);
		String lang = MediaTypeUtils.getLangOfMediaType(mt);

		loc = ui.getAbsolutePath();
		base = ui.getBaseUri();
		contentURI = loc;
	
		try {
			setURI = new URI(base + SET_STEM + DAY_SEGMENT);
		} catch (URISyntaxException e) {
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}

		populateDaySet();
		
		return doGet(lang).type(mt).contentLocation(contentURI).build();
	}
		
	private void populateDaySet() {
		//Resource r_set = model.createResource(setURI.toString(), DGU.URIset);
		Resource r_set = createDaySet();
		Resource r_doc = model.createResource(contentURI.toString(), FOAF.Document);
		initModel(r_set, r_doc);
		
		model.add(r_set, RDFS.comment, "A dataset of Gregorian calendar aligned time intervals of one calendar day duration" +
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
				"Gregorian calendar day to calendar year interval containment links",
				"Links between Gregorian calendar days and the calendar years in which they occur.");

		addLinkset(r_set, r_set, r_halfSet, TIME.intervalDuring, 
				"Gregorian calendar day to half year interval containment links",
				"Links between Gregorian calendar days and the calendar aligned half years in which they occur.");

		addLinkset(r_set, r_set, r_quarterSet, TIME.intervalDuring, 
				"Gregorian calendar days to calendar quarter year interval containment links",
				"Links between Gregorian calendar days and the calendar aligned quarter years in which they occur.");

		addLinkset(r_set, r_set, r_weekSet, TIME.intervalDuring, 
				"Gregorian calendar days to calendar week interval containment links",
				"Links between Gregorian calendar days and the ISO 8601 numbered week in which they occur.");

		addLinkset(r_set, r_set, r_hourSet, INTERVALS.intervalContainsHour, 
				"Gregorian calendar day to calendar aligned hour interval containment links",
				"Links between Gregorian calendar days and the calendar aligned hours they contain.");

		addLinkset(r_set, r_set, r_instantSet, TIME.hasBeginning, 
				"Gregorian calendar day to starting instant links",
				"Links between Gregorian calendar days and their starting instant.");		

		addLinkset(r_set, r_set, r_instantSet, TIME.hasEnd, 
				"Gregorian calendar days to ending instant links",
				"Links between Gregorian calendar days and their ending instant.");
		
		addLinkset(r_set, r_set, r_intervalSet, TIME.intervalEquals, 
				"Gregorian calendar days to generic interval links",
				"Links between Gregorian calendar days and their corresponding generic interval.");		
	}
	
	/*********************************************************************************
	 *
	 * Gregorian Hour URI Set
	 * 
	 ********************************************************************************/
	@GET
	@Path(HOUR_SEGMENT)
	@Produces( { "application/rdf+xml", "text/turtle", "application/json",
			"text/n3", "application/x-turtle", "text/plain" })

	public Response hourSetGetResponse() {
		MediaType mt = MediaTypeUtils.pickMediaType(hdrs.getAcceptableMediaTypes());
		String lang = MediaTypeUtils.getLangOfMediaType(mt);
		String ext = MediaTypeUtils.getExtOfMediaType(mt);

		loc = ui.getAbsolutePath();
		base = ui.getBaseUri();
		try {
			contentURI = new URI(loc.toString() + ext);
			setURI = new URI(base + SET_STEM + HOUR_SEGMENT);
		} catch (URISyntaxException e) {
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}

		populateHourSet();

		if (lang.equals("JSON"))
			return doGetJson().contentLocation(contentURI).build();

		return doGet(lang).type(mt).contentLocation(contentURI).build();
	}

	@GET
	@Path(HOUR_SEGMENT + EXT_PATTERN)
	public Response hourSetGetRDFResponse(@PathParam(EXT_TOKEN) String ext) {
		MediaType mt = MediaTypeUtils.extToMediaType(ext);
		String lang = MediaTypeUtils.getLangOfMediaType(mt);

		loc = ui.getAbsolutePath();
		base = ui.getBaseUri();
		contentURI = loc;
	
		try {
			setURI = new URI(base + SET_STEM + HOUR_SEGMENT);
		} catch (URISyntaxException e) {
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}

		populateHourSet();
		
		return doGet(lang).type(mt).contentLocation(contentURI).build();
	}
		
	private void populateHourSet() {
		//Resource r_set = model.createResource(setURI.toString(), DGU.URIset);
		Resource r_set = createDaySet();
		Resource r_doc = model.createResource(contentURI.toString(), FOAF.Document);
		initModel(r_set, r_doc);
		
		model.add(r_set, RDFS.comment, "A dataset of Gregorian calendar aligned time intervals of one hour duration.","en");
		model.add(r_set, RDF.type, VOID.Dataset);
		
		String base_reg = base.toString().replaceAll("\\.", "\\\\.");
		
		model.add(r_set, DGU.itemType, INTERVALS.CalendarHour);
		model.add(r_set, VOID.uriRegexPattern, base_reg+HOUR_ID_STEM+HOUR_PATTERN_PLAIN);

		model.add(r_set, VOID.exampleResource, HourDoc.createResourceAndLabels(base, model, 1752, 9, 2, 23));
	
		addGregorianSourceRef(r_set);	
		
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
				"Gregorian calendar hour to calendar year interval containment links",
				"Links between Gregorian calendar hours and the calendar years in which they occur.");

		addLinkset(r_set, r_set, r_halfSet, TIME.intervalDuring, 
				"Gregorian calendar hour to half year interval containment links",
				"Links between Gregorian calendar hours and the calendar aligned half years in which they occur.");

		addLinkset(r_set, r_set, r_quarterSet, TIME.intervalDuring, 
				"Gregorian calendar hours to calendar quarter year interval containment links",
				"Links between Gregorian calendar hours and the calendar aligned quarter years in which they occur.");

		addLinkset(r_set, r_set, r_weekSet, TIME.intervalDuring, 
				"Gregorian calendar days to calendar week interval containment links",
				"Links between Gregorian calendar days and the ISO 8601 numbered week in which they occur.");

		addLinkset(r_set, r_set, r_daySet, TIME.intervalDuring, 
				"Gregorian calendar hours to calendar day interval containment links",
				"Links between Gregorian calendar days and the calendar day in which they occur.");

		addLinkset(r_set, r_set, r_minSet, INTERVALS.intervalContainsMinute, 
				"Gregorian calendar hour to calendar aligned minute interval containment links",
				"Links between Gregorian calendar hour and the calendar aligned minutes they contain.");

		addLinkset(r_set, r_set, r_instantSet, TIME.hasBeginning, 
				"Gregorian calendar hour to starting instant links",
				"Links between Gregorian calendar hours and their starting instant.");		

		addLinkset(r_set, r_set, r_instantSet, TIME.hasEnd, 
				"Gregorian calendar hours to ending instant links",
				"Links between Gregorian calendar hours and their ending instant.");
		
		addLinkset(r_set, r_set, r_intervalSet, TIME.intervalEquals, 
				"Gregorian calendar hour to generic interval links",
				"Links between Gregorian calendar hours and their corresponding generic interval.");		
	}

	/*********************************************************************************
	 *
	 * Gregorian Minute URI Set
	 * 
	 ********************************************************************************/
	@GET
	@Path(MINUTE_SEGMENT)
	@Produces( { "application/rdf+xml", "text/turtle", "application/json",
			"text/n3", "application/x-turtle", "text/plain" })

	public Response minuteSetGetResponse() {
		MediaType mt = MediaTypeUtils.pickMediaType(hdrs.getAcceptableMediaTypes());
		String lang = MediaTypeUtils.getLangOfMediaType(mt);
		String ext = MediaTypeUtils.getExtOfMediaType(mt);

		loc = ui.getAbsolutePath();
		base = ui.getBaseUri();
		try {
			contentURI = new URI(loc.toString() + ext);
			setURI = new URI(base + SET_STEM + MINUTE_SEGMENT);
		} catch (URISyntaxException e) {
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}

		populateMinuteSet();

		if (lang.equals("JSON"))
			return doGetJson().contentLocation(contentURI).build();

		return doGet(lang).type(mt).contentLocation(contentURI).build();
	}

	@GET
	@Path(MINUTE_SEGMENT + EXT_PATTERN)
	public Response minuteSetGetRDFResponse(@PathParam(EXT_TOKEN) String ext) {
		MediaType mt = MediaTypeUtils.extToMediaType(ext);
		String lang = MediaTypeUtils.getLangOfMediaType(mt);

		loc = ui.getAbsolutePath();
		base = ui.getBaseUri();
		contentURI = loc;
	
		try {
			setURI = new URI(base + SET_STEM + HOUR_SEGMENT);
		} catch (URISyntaxException e) {
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}

		populateMinuteSet();
		
		return doGet(lang).type(mt).contentLocation(contentURI).build();
	}
		
	private void populateMinuteSet() {
		//Resource r_set = model.createResource(setURI.toString(), DGU.URIset);
		Resource r_set = createMinSet();
		Resource r_doc = model.createResource(contentURI.toString(), FOAF.Document);
		initModel(r_set, r_doc);
		
		model.add(r_set, RDFS.comment, "A dataset of Gregorian calendar aligned time intervals of one minute duration.","en");
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
				"Gregorian calendar minute to calendar year interval containment links",
				"Links between Gregorian calendar minutes and the calendar years in which they occur.");

		addLinkset(r_set, r_set, r_halfSet, TIME.intervalDuring, 
				"Gregorian calendar minute to half year interval containment links",
				"Links between Gregorian calendar minute and the calendar aligned half years in which they occur.");

		addLinkset(r_set, r_set, r_quarterSet, TIME.intervalDuring, 
				"Gregorian calendar minute to calendar quarter year interval containment links",
				"Links between Gregorian calendar minutes and the calendar aligned quarter years in which they occur.");

		addLinkset(r_set, r_set, r_weekSet, TIME.intervalDuring, 
				"Gregorian calendar minute to calendar week interval containment links",
				"Links between Gregorian calendar minutes and the ISO 8601 numbered week in which they occur.");

		addLinkset(r_set, r_set, r_daySet, TIME.intervalDuring, 
				"Gregorian calendar minutes to calendar day interval containment links",
				"Links between Gregorian calendar minutes and the calendar day in which they occur.");

		addLinkset(r_set, r_set, r_hourSet, TIME.intervalDuring, 
				"Gregorian calendar minutes to calendar hour interval containment links",
				"Links between Gregorian calendar minutes and the calendar hour in which they occur.");

		addLinkset(r_set, r_set, r_secSet, INTERVALS.intervalContainsSecond, 
				"Gregorian calendar minute to calendar aligned second interval containment links",
				"Links between Gregorian calendar minutes and the calendar aligned seconds they contain.");

		addLinkset(r_set, r_set, r_instantSet, TIME.hasBeginning, 
				"Gregorian calendar minute to starting instant links",
				"Links between Gregorian calendar minutes and their starting instant.");		

		addLinkset(r_set, r_set, r_instantSet, TIME.hasEnd, 
				"Gregorian calendar second to ending instant links",
				"Links between Gregorian calendar minutes and their ending instant.");
		
		addLinkset(r_set, r_set, r_intervalSet, TIME.intervalEquals, 
				"Gregorian calendar minute to generic interval links",
				"Links between Gregorian calendar minute and their corresponding generic interval.");
		
	}
	
	/*********************************************************************************
	 *
	 * Gregorian Second URI Set
	 * 
	 ********************************************************************************/
	@GET
	@Path(SECOND_SEGMENT)
	@Produces( { "application/rdf+xml", "text/turtle", "application/json",
			"text/n3", "application/x-turtle", "text/plain" })

	public Response secondSetGetResponse() {
		MediaType mt = MediaTypeUtils.pickMediaType(hdrs.getAcceptableMediaTypes());
		String lang = MediaTypeUtils.getLangOfMediaType(mt);
		String ext = MediaTypeUtils.getExtOfMediaType(mt);

		loc = ui.getAbsolutePath();
		base = ui.getBaseUri();
		try {
			contentURI = new URI(loc.toString() + ext);
			setURI = new URI(base + SET_STEM + SECOND_SEGMENT);
		} catch (URISyntaxException e) {
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}

		populateSecondSet();

		if (lang.equals("JSON"))
			return doGetJson().contentLocation(contentURI).build();

		return doGet(lang).type(mt).contentLocation(contentURI).build();
	}

	@GET
	@Path(SECOND_SEGMENT + EXT_PATTERN)
	public Response secondSetGetRDFResponse(@PathParam(EXT_TOKEN) String ext) {
		MediaType mt = MediaTypeUtils.extToMediaType(ext);
		String lang = MediaTypeUtils.getLangOfMediaType(mt);

		loc = ui.getAbsolutePath();
		base = ui.getBaseUri();
		contentURI = loc;
	
		try {
			setURI = new URI(base + SET_STEM + SECOND_SEGMENT);
		} catch (URISyntaxException e) {
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}

		populateSecondSet();
		
		return doGet(lang).type(mt).contentLocation(contentURI).build();
	}
		
	private void populateSecondSet() {
		Resource r_set = createSecSet();
		Resource r_doc = model.createResource(contentURI.toString(), FOAF.Document);
		initModel(r_set, r_doc);
		
		model.add(r_set, RDFS.comment, "A dataset of Gregorian calendar aligned time intervals of one second duration.","en");
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
				"Gregorian calendar second to calendar year interval containment links",
				"Links between Gregorian calendar seconds and the calendar years in which they occur.");

		addLinkset(r_set, r_set, r_halfSet, TIME.intervalDuring, 
				"Gregorian calendar second to half year interval containment links",
				"Links between Gregorian calendar second and the calendar aligned half years in which they occur.");

		addLinkset(r_set, r_set, r_quarterSet, TIME.intervalDuring, 
				"Gregorian calendar second to calendar quarter year interval containment links",
				"Links between Gregorian calendar seconds and the calendar aligned quarter years in which they occur.");

		addLinkset(r_set, r_set, r_weekSet, TIME.intervalDuring, 
				"Gregorian calendar second to calendar week interval containment links",
				"Links between Gregorian calendar seconds and the ISO 8601 numbered week in which they occur.");

		addLinkset(r_set, r_set, r_daySet, TIME.intervalDuring, 
				"Gregorian calendar seconds to calendar day interval containment links",
				"Links between Gregorian calendar seconds and the calendar day in which they occur.");

		addLinkset(r_set, r_set, r_hourSet, TIME.intervalDuring, 
				"Gregorian calendar seconds to calendar hour interval containment links",
				"Links between Gregorian calendar seconds and the calendar hour in which they occur.");

		addLinkset(r_set, r_set, r_minSet, TIME.intervalDuring, 
				"Gregorian calendar seconds to calendar minute interval containment links",
				"Links between Gregorian calendar seconds and the calendar minutes in which they occur.");

		addLinkset(r_set, r_set, r_instantSet, TIME.hasBeginning, 
				"Gregorian calendar second to starting instant links",
				"Links between Gregorian calendar seconds and their starting instant.");		

		addLinkset(r_set, r_set, r_instantSet, TIME.hasEnd, 
				"Gregorian calendar second to ending instant links",
				"Links between Gregorian calendar seconds and their ending instant.");
		
		addLinkset(r_set, r_set, r_intervalSet, TIME.intervalEquals, 
				"Gregorian calendar second to generic interval links",
				"Links between Gregorian calendar second and their corresponding generic interval.");
		
	}	
	
	/*********************************************************************************
	 *
	 * Gregorian Week URI Set
	 * 
	 ********************************************************************************/
	@GET
	@Path(WEEK_SEGMENT)
	@Produces( { "application/rdf+xml", "text/turtle", "application/json",
			"text/n3", "application/x-turtle", "text/plain" })

	public Response weekSetGetResponse() {
		MediaType mt = MediaTypeUtils.pickMediaType(hdrs.getAcceptableMediaTypes());
		String lang = MediaTypeUtils.getLangOfMediaType(mt);
		String ext = MediaTypeUtils.getExtOfMediaType(mt);

		loc = ui.getAbsolutePath();
		base = ui.getBaseUri();
		try {
			contentURI = new URI(loc.toString() + ext);
			setURI = new URI(base + SET_STEM + WEEK_SEGMENT);
		} catch (URISyntaxException e) {
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}

		populateWeekSet();

		if (lang.equals("JSON"))
			return doGetJson().contentLocation(contentURI).build();

		return doGet(lang).type(mt).contentLocation(contentURI).build();
	}

	@GET
	@Path(WEEK_SEGMENT + EXT_PATTERN)
	public Response weekSetGetRDFResponse(@PathParam(EXT_TOKEN) String ext) {
		MediaType mt = MediaTypeUtils.extToMediaType(ext);
		String lang = MediaTypeUtils.getLangOfMediaType(mt);

		loc = ui.getAbsolutePath();
		base = ui.getBaseUri();
		contentURI = loc;
	
		try {
			setURI = new URI(base + SET_STEM + WEEK_SEGMENT);
		} catch (URISyntaxException e) {
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}

		populateWeekSet();
		
		return doGet(lang).type(mt).contentLocation(contentURI).build();
	}
		
	private void populateWeekSet() {
		//Resource r_set = model.createResource(setURI.toString(), DGU.URIset);
		Resource r_set = createWeekSet();
		Resource r_doc = model.createResource(contentURI.toString(), FOAF.Document);
		initModel(r_set, r_doc);
		
		model.add(r_set, RDFS.comment, "A dataset of ISO 8601 numbered Gregorian calendar aligned time intervals of one week duration" +
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
				"Gregorian calendar week to calendar day interval containment links",
				"Links between ISO 8601 numbered Gregorian calendar weeks and the calendar days they contain.");

		addLinkset(r_set, r_set, r_instantSet, TIME.hasBeginning, 
				"Gregorian calendar week to starting instant links",
				"Links between ISO 8601 numbered Gregorian calendar weeks and their starting instant.");		

		addLinkset(r_set, r_set, r_instantSet, TIME.hasEnd, 
				"Gregorian calendar week to ending instant links",
				"Links between ISO 8601 numbered Gregorian calendar weeks and their ending instant.");
		
		addLinkset(r_set, r_set, r_intervalSet, TIME.intervalEquals, 
				"Gregorian calendar week to generic interval links",
				"Links between ISO 8601 numbered Gregorian calendar weeks and their corresponding generic interval.");		
	}

	
	/*********************************************************************************
	 *
	 * Gregorian Interval URI Set
	 * 
	 ********************************************************************************/
	@GET
	@Path(INTERVAL_SEGMENT)
	@Produces( { "application/rdf+xml", "text/turtle", "application/json",
			"text/n3", "application/x-turtle", "text/plain" })

	public Response intervalSetGetResponse() {
		MediaType mt = MediaTypeUtils.pickMediaType(hdrs.getAcceptableMediaTypes());
		String lang = MediaTypeUtils.getLangOfMediaType(mt);
		String ext = MediaTypeUtils.getExtOfMediaType(mt);

		loc = ui.getAbsolutePath();
		base = ui.getBaseUri();
		try {
			contentURI = new URI(loc.toString() + ext);
			setURI = new URI(base + SET_STEM + INTERVAL_SEGMENT);
		} catch (URISyntaxException e) {
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}

		populateIntervalSet();

		if (lang.equals("JSON"))
			return doGetJson().contentLocation(contentURI).build();

		return doGet(lang).type(mt).contentLocation(contentURI).build();
	}

	@GET
	@Path(INTERVAL_SEGMENT + EXT_PATTERN)
	public Response intervalSetGetRDFResponse(@PathParam(EXT_TOKEN) String ext) {
		MediaType mt = MediaTypeUtils.extToMediaType(ext);
		String lang = MediaTypeUtils.getLangOfMediaType(mt);

		loc = ui.getAbsolutePath();
		base = ui.getBaseUri();
		contentURI = loc;
	
		try {
			setURI = new URI(base + SET_STEM + INTERVAL_SEGMENT);
		} catch (URISyntaxException e) {
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}

		populateIntervalSet();
		
		return doGet(lang).type(mt).contentLocation(contentURI).build();
	}
		
	private void populateIntervalSet() {
		Resource r_set = createSecSet();
		Resource r_doc = model.createResource(contentURI.toString(), FOAF.Document);
		initModel(r_set, r_doc);
		
		model.add(r_set, RDFS.comment, "A dataset of Gregorian general purpose time intervals of arbitary duration.","en");
		model.add(r_set, RDF.type, VOID.Dataset);
		
		String base_reg = base.toString().replaceAll("\\.", "\\\\.");
		
		model.add(r_set, DGU.itemType, INTERVALS.Interval);
		model.add(r_set, VOID.uriRegexPattern, base_reg+INTERVAL_ID_STEM+INTERVAL_PATTERN_PLAIN);

		model.add(r_set, VOID.exampleResource, IntervalDoc.createResourceAndLabels(base, model, new GregorianOnlyCalendar(1977, 10, 1, 12, 22, 45), new Duration("P2Y1MT1H6S") ));

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
		r_secSet=createSecSet();
//		r_intervalSet=createIntervalSet());
		r_instantSet=createInstantSet();
		
		addLinkset(r_set, r_yearSet, r_set, TIME.intervalEquals, 
				"Gregorian calendar year to generic interval links.",
				"Links between Gregorian calendar years and their corresponding generic intervals.");

		addLinkset(r_set, r_halfSet, r_set, TIME.intervalEquals, 
				"Gregorian calendar half year to generic interval links.",
				"Links between Gregorian calendar half years and their corresponding generic intervals.");
		
		addLinkset(r_set, r_quarterSet, r_set, TIME.intervalEquals, 
				"Gregorian calendar quarter year to generic interval links.",
				"Links between Gregorian calendar quarter years and their corresponding generic intervals.");
		
		addLinkset(r_set, r_monthSet, r_set, TIME.intervalEquals, 
				"Gregorian calendar month to generic interval links.",
				"Links between Gregorian calendar months and their corresponding generic intervals.");
		
		addLinkset(r_set, r_weekSet, r_set, TIME.intervalEquals, 
				"Gregorian calendar week to generic interval links.",
				"Links between ISO 8610 numbered Gregorian calendar weeks and their corresponding generic intervals.");
		
		addLinkset(r_set, r_daySet, r_set, TIME.intervalEquals, 
				"Gregorian calendar day to generic interval links.",
				"Links between Gregorian calendar days and their corresponding generic intervals.");
		
		addLinkset(r_set, r_hourSet, r_set, TIME.intervalEquals, 
				"Gregorian calendar hour to generic interval links.",
				"Links between Gregorian calendar hours and their corresponding generic intervals.");
		
		addLinkset(r_set, r_minSet, r_set, TIME.intervalEquals, 
				"Gregorian calendar minute to generic interval links.",
				"Links between Gregorian calendar minutes and their corresponding generic intervals.");
		
		addLinkset(r_set, r_secSet, r_set, TIME.intervalEquals, 
				"Gregorian calendar second to generic interval links.",
				"Links between Gregorian calendar seconds and their corresponding generic intervals.");

		addLinkset(r_set, r_set, r_instantSet, TIME.hasBeginning, 
				"Gregorian calendar generic interval to starting instant links",
				"Links between Gregorian calendar generic intervals and their starting instant.");		

		addLinkset(r_set, r_set, r_instantSet, TIME.hasEnd, 
				"Gregorian calendar generic interval to ending instant links",
				"Links between Gregorian calendar generic intervals and their ending instant.");
		
	}	
	
	/*********************************************************************************
	 *
	 * Gregorian Instant URI Set
	 * 
	 ********************************************************************************/
	@GET
	@Path(INSTANT_SEGMENT)
	@Produces( { "application/rdf+xml", "text/turtle", "application/json",
			"text/n3", "application/x-turtle", "text/plain" })

	public Response instantSetGetResponse() {
		MediaType mt = MediaTypeUtils.pickMediaType(hdrs.getAcceptableMediaTypes());
		String lang = MediaTypeUtils.getLangOfMediaType(mt);
		String ext = MediaTypeUtils.getExtOfMediaType(mt);

		loc = ui.getAbsolutePath();
		base = ui.getBaseUri();
		try {
			contentURI = new URI(loc.toString() + ext);
			setURI = new URI(base + SET_STEM + INSTANT_SEGMENT);
		} catch (URISyntaxException e) {
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}

		populateInstantSet();

		if (lang.equals("JSON"))
			return doGetJson().contentLocation(contentURI).build();

		return doGet(lang).type(mt).contentLocation(contentURI).build();
	}

	@GET
	@Path(INSTANT_SEGMENT + EXT_PATTERN)
	public Response instantSetGetRDFResponse(@PathParam(EXT_TOKEN) String ext) {
		MediaType mt = MediaTypeUtils.extToMediaType(ext);
		String lang = MediaTypeUtils.getLangOfMediaType(mt);

		loc = ui.getAbsolutePath();
		base = ui.getBaseUri();
		contentURI = loc;
	
		try {
			setURI = new URI(base + SET_STEM + INSTANT_SEGMENT);
		} catch (URISyntaxException e) {
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}

		populateInstantSet();
		
		return doGet(lang).type(mt).contentLocation(contentURI).build();
	}
		
	private void populateInstantSet() {
		Resource r_set = createSecSet();
		Resource r_doc = model.createResource(contentURI.toString(), FOAF.Document);
		initModel(r_set, r_doc);
		
		model.add(r_set, RDFS.comment, "A dataset of time instant on the Gregorian time line.","en");
		model.add(r_set, RDF.type, VOID.Dataset);
		
		String base_reg = base.toString().replaceAll("\\.", "\\\\.");
		
		model.add(r_set, DGU.itemType, INTERVALS.Interval);
		model.add(r_set, VOID.uriRegexPattern, base_reg+INSTANT_ID_STEM+INSTANT_PATTERN_PLAIN);
		
		model.add(r_set, VOID.exampleResource, InstantDoc.createResource(base, model, new GregorianOnlyCalendar(1977, 10, 1, 12, 22, 45)));

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
		r_secSet=createSecSet();
		r_intervalSet=createIntervalSet();
//		r_instantSet=createInstantSet();
		
		addLinkset(r_set, r_yearSet, r_set, TIME.hasBeginning, 
				"Gregorian calendar year to starting instant links.",
				"Links between Gregorian calendar years and their corresponding starting instants.");

		addLinkset(r_set, r_halfSet, r_set, TIME.hasBeginning, 
				"Gregorian calendar half year to starting instant links.",
				"Links between Gregorian calendar half years and their corresponding starting instants.");
		
		addLinkset(r_set, r_quarterSet, r_set, TIME.hasBeginning, 
				"Gregorian calendar quarter year to starting instant links.",
				"Links between Gregorian calendar quarter years and their corresponding starting instants.");
		
		addLinkset(r_set, r_monthSet, r_set, TIME.hasBeginning, 
				"Gregorian calendar month to starting instant links.",
				"Links between Gregorian calendar months and their corresponding starting instants.");
		
		addLinkset(r_set, r_weekSet, r_set, TIME.hasBeginning, 
				"Gregorian calendar week to starting instant links.",
				"Links between ISO 8610 numbered Gregorian calendar weeks and their corresponding starting instants.");
		
		addLinkset(r_set, r_daySet, r_set, TIME.hasBeginning, 
				"Gregorian calendar day to starting instant links.",
				"Links between Gregorian calendar days and their corresponding starting instants.");
		
		addLinkset(r_set, r_hourSet, r_set, TIME.hasBeginning, 
				"Gregorian calendar hour to starting instant links.",
				"Links between Gregorian calendar hours and their corresponding starting instants.");
		
		addLinkset(r_set, r_minSet, r_set, TIME.hasBeginning, 
				"Gregorian calendar minute to starting instant links.",
				"Links between Gregorian calendar minutes and their corresponding starting instants.");
		
		addLinkset(r_set, r_secSet, r_set, TIME.hasBeginning, 
				"Gregorian calendar second to starting instant links.",
				"Links between Gregorian calendar seconds and their corresponding starting instants.");
		
		addLinkset(r_set, r_intervalSet, r_set, TIME.hasBeginning, 
				"Gregorian calendar generic intervals to starting instant links.",
				"Links between Gregorian calendar generic intervals and their corresponding starting instants.");
		
		// Now the hadEnd links

		addLinkset(r_set, r_yearSet, r_set, TIME.hasEnd, 
				"Gregorian calendar year to ending instant links.",
				"Links between Gregorian calendar years and their corresponding ending instants.");

		addLinkset(r_set, r_halfSet, r_set, TIME.hasEnd, 
				"Gregorian calendar half year to ending instant links.",
				"Links between Gregorian calendar half years and their corresponding ending instants.");
		
		addLinkset(r_set, r_quarterSet, r_set, TIME.hasEnd, 
				"Gregorian calendar quarter year to ending instant links.",
				"Links between Gregorian calendar quarter years and their corresponding ending instants.");
		
		addLinkset(r_set, r_monthSet, r_set, TIME.hasEnd, 
				"Gregorian calendar month to ending instant links.",
				"Links between Gregorian calendar months and their corresponding ending instants.");
		
		addLinkset(r_set, r_weekSet, r_set, TIME.hasEnd, 
				"Gregorian calendar week to ending instant links.",
				"Links between ISO 8610 numbered Gregorian calendar weeks and their corresponding ending instants.");
		
		addLinkset(r_set, r_daySet, r_set, TIME.hasEnd, 
				"Gregorian calendar day to ending instant links.",
				"Links between Gregorian calendar days and their corresponding ending instants.");
		
		addLinkset(r_set, r_hourSet, r_set, TIME.hasEnd, 
				"Gregorian calendar hour to ending instant links.",
				"Links between Gregorian calendar hours and their corresponding ending instants.");
		
		addLinkset(r_set, r_minSet, r_set, TIME.hasEnd, 
				"Gregorian calendar minute to ending instant links.",
				"Links between Gregorian calendar minutes and their corresponding ending instants.");
		
		addLinkset(r_set, r_secSet, r_set, TIME.hasEnd, 
				"Gregorian calendar second to ending instant links.",
				"Links between Gregorian calendar seconds and their corresponding ending instants.");
		
		addLinkset(r_set, r_intervalSet, r_set, TIME.hasBeginning, 
				"Gregorian calendar generic intervals to ending instant links.",
				"Links between Gregorian calendar generic intervals and their corresponding ending instants.");
		
	}	
	
	/*********************************************************************************
	 * 
	 ********************************************************************************/
	
	protected ResponseBuilder doGet(final String lang) {
		StreamingOutput so = new StreamingOutput() {
			public void write(OutputStream os) throws IOException {
				model.write(os, lang);
			}
		};
		return Response.ok(so);
	}

	protected ResponseBuilder doGetRDF() {
		return doGet("RDF/XML-ABBREV");
	}

	protected ResponseBuilder doGetNTriple() {
		return doGet("N-TRIPLE");
	}

	protected ResponseBuilder doGetTurtle() {
		return doGet("N3");
	}

	protected ResponseBuilder doGetJson() {
		StreamingOutput so = new StreamingOutput() {
			public void write(OutputStream output) throws IOException,
					WebApplicationException {
				Encoder enc = Encoder.get();
				OutputStreamWriter osw = new OutputStreamWriter(output);
				enc.encode(model, osw, true);
			}
		};
		return Response.ok(so);
	}

	/*********************************************************************************
	 * 
	 ********************************************************************************/	
	protected void setNamespaces() {
		model
		.setNsPrefix("rdfs", RDFS.getURI())
		.setNsPrefix("rdf", RDF.getURI())
		.setNsPrefix("owl", OWL.NS)
		.setNsPrefix("time", TIME.NS)
		.setNsPrefix("skos", SKOS.NS)
		.setNsPrefix("interval", INTERVALS.NS)
		.setNsPrefix("foaf", FOAF.NS)
		.setNsPrefix("dc", DC_11.NS)
		.setNsPrefix("dct", DCTypes.NS)
		.setNsPrefix("xsd", XSD.getURI())
		.setNsPrefix("scv",SCOVO.NS)
		.setNsPrefix("dcterms", DCTerms.NS)
		.setNsPrefix("dgu", DGU.NS)
		.setNsPrefix("void", VOID.NS)
		.setNsPrefix("prv", PROVENANCE.NS)
		;		
	}
	
	protected void initModel(Resource r_set, Resource r_doc) {

		setNamespaces();

		//Statements to make in every set.
		model.add(r_set, DGU.status, DGU.draft);
		model.add(r_set, FOAF.isPrimaryTopicOf, r_doc);
		model.add(r_doc, FOAF.primaryTopic, r_set);
	}

	private void addLinkset(Resource r_superSet, 
							Resource r_subjectSet,
							Resource r_objectSet, 
							Resource r_linkPredicate, 
							String s_label,
							String s_comment) {
		Resource r_linkSet = model.createResource(VOID.Linkset);
		model.add(r_superSet, VOID.subset, r_linkSet);
		model.add(r_linkSet, VOID.linkPredicate, r_linkPredicate);
		model.add(r_linkSet, VOID.subjectsTarget, r_subjectSet);
		model.add(r_linkSet, VOID.objectsTarget, r_objectSet);
		model.add(r_linkSet, RDFS.label, s_label, "en");
		model.add(r_linkSet, RDFS.comment, s_comment, "en");
	}

	private Resource createIntervalSet() {
		Resource r_secs = model.createResource(base+INTERVAL_SET_RELURI, VOID.Dataset);
		model.add(r_secs, RDFS.label, "Arbitary duration intervals on the Gregorian timeline.","en");
		model.add(r_secs, SKOS.prefLabel, "Arbitary duration intervals on the Gregorian timeline.","en");
		return r_secs;
	}
	private Resource createInstantSet() {
		Resource r_secs = model.createResource(base+INSTANT_SET_RELURI, VOID.Dataset);
		model.add(r_secs, RDFS.label, "Arbitary instants on the Gregorian timeline.","en");
		model.add(r_secs, SKOS.prefLabel, "Arbitary instants on the Gregorian timeline.","en");
		return r_secs;
	}

	private Resource createSecSet() {
		Resource r_secs = model.createResource(base+SECOND_SET_RELURI, VOID.Dataset);
		model.add(r_secs, RDFS.label, "Gregorian calendar aligned one second intervals.","en");
		model.add(r_secs, SKOS.prefLabel, "Gregorian calendar aligned one second year intervals.","en");
		return r_secs;
	}

	private Resource createMinSet() {
		Resource r_mins =model.createResource(base+MINUTE_SET_RELURI, VOID.Dataset);
		model.add(r_mins, RDFS.label, "Gregorian calendar aligned one minute intervals.","en");
		model.add(r_mins, SKOS.prefLabel, "Gregorian calendar aligned one minute intervals.","en");
		return r_mins;
	}

	private Resource createHourSet() {
		Resource r_hours=model.createResource(base+HOUR_SET_RELURI, VOID.Dataset);
		model.add(r_hours, RDFS.label, "Gregorian calendar aligned one hour intervals.","en");
		model.add(r_hours, SKOS.prefLabel, "Gregorian calendar aligned one hour intervals.","en");
		return r_hours;
	}

	private Resource createDaySet() {
		Resource r_days = model.createResource(base+DAY_SET_RELURI, VOID.Dataset);
		model.add(r_days, RDFS.label, "Gregorian calendar aligned one calendar day intervals.","en");
		model.add(r_days, SKOS.prefLabel, "Gregorian calendar aligned one calendar day intervals.","en");
		return r_days;
	}

	private Resource createWeekSet() {
		Resource r_weeks = model.createResource(base+WEEK_SET_RELURI, VOID.Dataset);
		model.add(r_weeks, RDFS.label, "Gregorian calendar aligned ISO8601 numbered week long intervals.","en");
		model.add(r_weeks, SKOS.prefLabel, "Gregorian calendar aligned ISO8601 numbered week long intervals.","en");
		return r_weeks;
	}

	private Resource createMonthSet() {
		Resource r_months = model.createResource(base+MONTH_SET_RELURI, VOID.Dataset);
		model.add(r_months, RDFS.label, "Gregorian calendar aligned one calendar month intervals.","en");
		model.add(r_months, SKOS.prefLabel, "Gregorian calendar aligned one calendar month intervals,","en");
		return r_months;
	}

	private Resource createQuarterSet() {
		Resource r_quarters = model.createResource(base+QUARTER_SET_RELURI, VOID.Dataset);
		model.add(r_quarters, RDFS.label, "Gregorian calendar aligned one quarter year intervals.","en");
		model.add(r_quarters, SKOS.prefLabel, "Gregorian calendar aligned one quarter year intervals.","en");
		return r_quarters;
	}

	private Resource createHalfSet() {
		Resource r_halves = model.createResource(base+HALF_SET_RELURI, VOID.Dataset);
		model.add(r_halves, RDFS.label, "Gregorian calendar aligned one half year intervals.","en");
		model.add(r_halves, SKOS.prefLabel, "Gregorian calendar aligned one half year intervals.","en");
		return r_halves;
	}

	private Resource createYearSet() {
		Resource  r_years=model.createResource(base+YEAR_SET_RELURI, VOID.Dataset);
		model.add(r_years, RDFS.label, "Gregorian calendar aligned one year intervals.","en");
		model.add(r_years, SKOS.prefLabel, "Gregorian Calendar aligned one year intervals.","en");
		return r_years;
	}

	private void addCalendarActRef(Resource r_set) {
		Resource r_calendarAct;
		model.add(r_set, DCTERMS.source, r_calendarAct=model.createResource(CALENDAR_ACT_URI));
		model.add(r_calendarAct, RDFS.label, "Calendar (New Style) Act 1750.","en");
		model.add(r_calendarAct, SKOS.prefLabel, "Calendar (New Style) Act 1750.","en");
	}	
	
	private void addGregorianSourceRef(Resource r_set) {
		Resource r_calendarAct;
		model.add(r_set, DCTERMS.source, r_calendarAct=model.createResource("http://en.wikipedia.org/wiki/Gregorian_calendar"));
		model.add(r_calendarAct, RDFS.label, "Wikipedia on Gregorian Calendar","en");
		model.add(r_calendarAct, SKOS.prefLabel, "Wikipedia on Gregorian Calendar","en");
	}
	
}
