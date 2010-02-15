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
	
	Model model = ModelFactory.createDefaultModel();
	{ setNamespaces();};

	/*********************************************************************************
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
			setURI = new URI(base + SET_DOC_STEM + CALENDAR_STEM);
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
			setURI = new URI(base + SET_DOC_STEM + CALENDAR_STEM);
		} catch (URISyntaxException e) {
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}

		populateCalSet();
		return doGet(lang).type(mt).contentLocation(contentURI).build();
	}
		
	private void populateCalSet() {
		Resource r_set = model.createResource(setURI.toString(), DGU.URIset);
		
		model.add(r_set, RDFS.label, "Gregorian calendar aligned intervals.","en");
		model.add(r_set, SKOS.prefLabel, "Gregorian calendar aligned intervals.","en");
		
		model.add(r_set, RDFS.comment, "A dataset of Gregorian Calendar aligned time intervals formed from the union" +
				                     " of datasets that contain calendar aligned intervals one year, one half year," +
				                     " one quarter, one month, one day, one hour, one minute or one second.", "en");
		model.add(r_set, RDF.type, VOID.Dataset);
		
		String base_reg = base.toString().replaceAll("\\.", "\\\\.");
		
		model.add(r_set, DGU.itemType, INTERVALS.Interval);
		model.add(r_set, VOID.uriRegexPattern, base_reg+YEAR_ID_STEM+YEAR_PATTERN_PLAIN);
		model.add(r_set, VOID.uriRegexPattern, base_reg+HALF_ID_STEM+HALF_PATTERN_PLAIN);
		model.add(r_set, VOID.uriRegexPattern, base_reg+QUARTER_ID_STEM+QUARTER_PATTERN_PLAIN);
		model.add(r_set, VOID.uriRegexPattern, base_reg+MONTH_ID_STEM+MONTH_PATTERN_PLAIN);
		model.add(r_set, VOID.uriRegexPattern, base_reg+DAY_ID_STEM+DAY_PATTERN_PLAIN);
		model.add(r_set, VOID.uriRegexPattern, base_reg+HOUR_ID_STEM+HOUR_PATTERN_PLAIN);
		model.add(r_set, VOID.uriRegexPattern, base_reg+MINUTE_ID_STEM+MIN_PATTERN_PLAIN);
		model.add(r_set, VOID.uriRegexPattern, base_reg+SECOND_ID_STEM+SEC_PATTERN_PLAIN);
		model.add(r_set, VOID.uriRegexPattern, base_reg+INSTANT_ID_STEM+INSTANT_PATTERN_PLAIN);
		model.add(r_set, VOID.uriRegexPattern, base_reg+INTERVAL_ID_STEM+INTERVAL_PATTERN_PLAIN);
		
		model.add(r_set, VOID.exampleResource, YearDoc.createResourceAndLabels(base, model, 1752));
		model.add(r_set, VOID.exampleResource, HalfDoc.createResourceAndLabels(base, model, 2010, 1));
		model.add(r_set, VOID.exampleResource, QuarterDoc.createResourceAndLabels(base, model, 1644, 3));
		model.add(r_set, VOID.exampleResource, MonthDoc.createResourceAndLabels(base, model, 1958, 11));
		model.add(r_set, VOID.exampleResource, DayDoc.createResourceAndLabels(base, model, 1960, 3, 12));
		model.add(r_set, VOID.exampleResource, HourDoc.createResourceAndLabels(base, model, 1752, 9, 2, 23));
		model.add(r_set, VOID.exampleResource, MinuteDoc.createResourceAndLabels(base, model, 1752, 9, 2, 23, 59));
		model.add(r_set, VOID.exampleResource, SecDoc.createResourceAndLabels(base, model, 1234, 4, 1, 22, 35, 41));
		model.add(r_set, VOID.exampleResource, InstantDoc.createResource(base, model, new GregorianOnlyCalendar(1977, 10, 1, 12, 22, 45)));
		model.add(r_set, VOID.exampleResource, IntervalDoc.createResourceAndLabels(base, model, new GregorianOnlyCalendar(1977, 10, 1, 12, 22, 45), new Duration("P2Y1MT1H6S") ));
		

		addGregorianSourceRef(r_set);	
		model.add(r_set, VOID.subset, createYearSet(r_set));
		model.add(r_set, VOID.subset, createHalfSet(r_set));
		model.add(r_set, VOID.subset, createQuarterSet(r_set));
		model.add(r_set, VOID.subset, createMonthSet(r_set));
		model.add(r_set, VOID.subset, createDaySet(r_set));
		model.add(r_set, VOID.subset, createHourSet(r_set));
		model.add(r_set, VOID.subset, createMinSet(r_set));
		model.add(r_set, VOID.subset, createSecSet(r_set));
		model.add(r_set, VOID.subset, createIntervalSet(r_set));
		model.add(r_set, VOID.subset, createInstantSet(r_set));
	}

	/*********************************************************************************
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
			setURI = new URI(base + SET_DOC_STEM + CALENDAR_STEM);
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
			setURI = new URI(base + SET_DOC_STEM + CALENDAR_STEM);
		} catch (URISyntaxException e) {
			throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
		}

		populateYearSet();
		return doGet(lang).type(mt).contentLocation(contentURI).build();
	}
		
	private void populateYearSet() {
		Resource r_set = model.createResource(setURI.toString(), DGU.URIset);
		
		model.add(r_set, RDFS.comment, "A dataset of Gregorian calendar aligned time intervals of one year duration" +
									   " starting at midnight on the 1st of January of a given year.","en");
		model.add(r_set, RDF.type, VOID.Dataset);
		
		String base_reg = base.toString().replaceAll("\\.", "\\\\.");
		
		model.add(r_set, DGU.itemType, INTERVALS.CalendarYear);
		model.add(r_set, VOID.uriRegexPattern, base_reg+YEAR_ID_STEM+YEAR_PATTERN_PLAIN);
		
		model.add(r_set, VOID.exampleResource, YearDoc.createResourceAndLabels(base, model, 1752));
		
		addGregorianSourceRef(r_set);	
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

	/*********************************************************************************
	 * 
	 ********************************************************************************/

	private Resource createIntervalSet(Resource r_set) {
		Resource r_secs = model.createResource(base+INTERVAL_SET_RELURI, VOID.Dataset);
		model.add(r_secs, RDFS.label, "Arbitary duration intervals on the Gregorian timeline.","en");
		model.add(r_secs, SKOS.prefLabel, "Arbitary duration intervals on the Gregorian timeline.","en");
		return r_secs;
	}
	private Resource createInstantSet(Resource r_set) {
		Resource r_secs = model.createResource(base+INSTANT_SET_RELURI, VOID.Dataset);
		model.add(r_secs, RDFS.label, "Arbitary instants on the Gregorian timeline.","en");
		model.add(r_secs, SKOS.prefLabel, "Arbitary instants on the Gregorian timeline.","en");
		return r_secs;
	}

	private Resource createSecSet(Resource r_set) {
		Resource r_secs = model.createResource(base+SECOND_SET_RELURI, VOID.Dataset);
		model.add(r_secs, RDFS.label, "Gregorian calendar aligned one second intervals.","en");
		model.add(r_secs, SKOS.prefLabel, "Gregorian calendar aligned one second year intervals.","en");
		return r_secs;
	}

	private Resource createMinSet(Resource r_set) {
		Resource r_mins =model.createResource(base+MINUTE_SET_RELURI, VOID.Dataset);
		model.add(r_mins, RDFS.label, "Gregorian calendar aligned one minute intervals.","en");
		model.add(r_mins, SKOS.prefLabel, "Gregorian calendar aligned one minute intervals.","en");
		return r_mins;
	}

	private Resource createHourSet(Resource r_set) {
		Resource r_hours=model.createResource(base+HOUR_SET_RELURI, VOID.Dataset);
		model.add(r_hours, RDFS.label, "Gregorian calendar aligned one hour intervals.","en");
		model.add(r_hours, SKOS.prefLabel, "Gregorian calendar aligned one hour intervals.","en");
		return r_hours;
	}

	private Resource createDaySet(Resource r_set) {
		Resource r_days = model.createResource(base+DAY_SET_RELURI, VOID.Dataset);
		model.add(r_days, RDFS.label, "Gregorian calendar aligned one calendar day intervals.","en");
		model.add(r_days, SKOS.prefLabel, "Gregorian calendar aligned one calendar day intervals.","en");
		return r_days;
	}

	private Resource createMonthSet(Resource r_set) {
		Resource r_months = model.createResource(base+MONTH_SET_RELURI, VOID.Dataset);
		model.add(r_months, RDFS.label, "Gregorian calendar aligned one calendar month intervals.","en");
		model.add(r_months, SKOS.prefLabel, "Gregorian calendar aligned one calendar month intervals,","en");
		return r_months;
	}

	private Resource createQuarterSet(Resource r_set) {
		Resource r_quarters = model.createResource(base+QUARTER_SET_RELURI, VOID.Dataset);
		model.add(r_quarters, RDFS.label, "Gregorian calendar aligned one quarter year intervals.","en");
		model.add(r_quarters, SKOS.prefLabel, "Gregorian calendar aligned one quarter year intervals.","en");
		return r_quarters;
	}

	private Resource createHalfSet(Resource r_set) {
		Resource r_halves = model.createResource(base+HALF_SET_RELURI, VOID.Dataset);
		model.add(r_halves, RDFS.label, "Gregorian calendar aligned one half year intervals.","en");
		model.add(r_halves, SKOS.prefLabel, "Gregorian calendar aligned one half year intervals.","en");
		return r_halves;
	}

	private Resource createYearSet(Resource r_set) {
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
