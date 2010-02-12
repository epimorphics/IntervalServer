package com.epimorphics.govData.URISets.intervalServer.govcalendar;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import com.epimorphics.govData.URISets.intervalServer.util.GregorianOnlyCalendar;
import java.util.Locale;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.epimorphics.govData.vocabulary.FOAF;
import com.epimorphics.govData.vocabulary.INTERVALS;
import com.epimorphics.govData.vocabulary.SKOS;
import com.epimorphics.govData.vocabulary.TIME;
import com.epimorphics.jsonrdf.Encoder;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.DCTypes;
import com.hp.hpl.jena.vocabulary.DC_11;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

abstract public class Doc extends URITemplate {
	@Context UriInfo ui;
	protected URI loc, base;
	protected String ext;
	protected int year, half, quarter, month, day, hour, min, sec;
	
	protected Model model;
	protected GregorianOnlyCalendar startTime;
	protected Resource r_thisTemporalEntity;
	
	static final protected Literal oneSecond = ResourceFactory.createTypedLiteral("PT1S", XSDDatatype.XSDduration);
	static final protected Literal oneMinute = ResourceFactory.createTypedLiteral("PT1M", XSDDatatype.XSDduration);
	static final protected Literal oneHour	= ResourceFactory.createTypedLiteral("PT1H", XSDDatatype.XSDduration);
	static final protected Literal oneDay 	= ResourceFactory.createTypedLiteral("P1D", XSDDatatype.XSDduration);
	static final protected Literal oneWeek 	= ResourceFactory.createTypedLiteral("P7D", XSDDatatype.XSDduration);
	static final protected Literal oneMonth 	= ResourceFactory.createTypedLiteral("P1M", XSDDatatype.XSDduration);
	static final protected Literal oneQuarter = ResourceFactory.createTypedLiteral("P3M", XSDDatatype.XSDduration);
	static final protected Literal oneHalf 	= ResourceFactory.createTypedLiteral("P6M", XSDDatatype.XSDduration);
	static final protected Literal oneYear 	= ResourceFactory.createTypedLiteral("P1Y", XSDDatatype.XSDduration);
	
	static final SimpleDateFormat iso8601format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

	protected void  reset() {
		init();
		half = quarter = month = day = 1;
		hour = min = sec = 0;
	}
	

	protected Response doGet() {
		return ext.equals(EXT_RDF) ?     (doGetRDF().contentLocation(loc).type("application/rdf").build()) : 
			   ext.equals(EXT_TTL) ?  (doGetTurtle().contentLocation(loc).type("text/turtle").build())  : 
			   ext.equals(EXT_JSON) ?  (doGetJson().contentLocation(loc).type("application/json").build())  : 
			   ext.equals(EXT_N3)  ?  (doGetTurtle().contentLocation(loc).type("text/n3").build()) : 
				                     (doGetNTriple().contentLocation(loc).type("text/plain").build());
	}
	
	protected void populateModel () {
		model = ModelFactory.createDefaultModel();
		startTime = new GregorianOnlyCalendar(Locale.UK);
		startTime.setLenient(false);
		startTime.set(year, month-1, day, hour, min , sec);
		try {
			startTime.getTimeInMillis();
		} catch (IllegalArgumentException e) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		setNamespaces();
		addThisInterval();
		addDocInfo();
		addNeighboringIntervals();
		addContainingIntervals();
		addContainedIntervals();	
	}
	
	abstract void  addThisInterval();
	abstract void  addNeighboringIntervals();
	abstract void  addContainingIntervals();
	abstract void  addContainedIntervals();
	

	protected void addDocInfo() {
		Resource r_doc = model.createResource(ui.getAbsolutePath().toASCIIString(), FOAF.Document);
		String documentStem = r_thisTemporalEntity.getURI().replaceFirst(ID_STEM, DOC_STEM);
		
		Resource r_ntDoc   = model.createResource(documentStem+"."+EXT_NT,   FOAF.Document);
		Resource r_rdfDoc  = model.createResource(documentStem+"."+EXT_RDF,  FOAF.Document);
		Resource r_ttlDoc  = model.createResource(documentStem+"."+EXT_TTL,  FOAF.Document);
		Resource r_n3Doc   = model.createResource(documentStem+"."+EXT_N3,   FOAF.Document);
		Resource r_jsonDoc = model.createResource(documentStem+"."+EXT_JSON, FOAF.Document);
	
		model.add(r_ntDoc, DC_11.format, "text/plain");
		model.add(r_ntDoc, RDFS.label, "N-Triple variant");
		
		model.add(r_rdfDoc, DC_11.format, "application/rdf+xml");
		model.add(r_rdfDoc, RDFS.label, "RDF/XML variant");
		
		model.add(r_ttlDoc, DC_11.format, "text/turtle");
		model.add(r_ttlDoc, RDFS.label, "Turtle variant");
		
		model.add(r_n3Doc, DC_11.format, "text/turtle");
		model.add(r_n3Doc, RDFS.label, "N3 variant");

		model.add(r_jsonDoc, DC_11.format, "application/json");
		model.add(r_jsonDoc, RDFS.label, "JSON variant");
		
		model.add(r_thisTemporalEntity, FOAF.isPrimaryTopicOf, r_doc);
		model.add(r_doc, FOAF.primaryTopic, r_thisTemporalEntity);
		model.add(r_doc, RDFS.label, "Generic Document");
		model.add(r_doc, DCTerms.hasFormat, r_ntDoc);
		model.add(r_doc, DCTerms.hasFormat, r_rdfDoc);
		model.add(r_doc, DCTerms.hasFormat, r_ttlDoc);
		model.add(r_doc, DCTerms.hasFormat, r_n3Doc);
		model.add(r_doc, DCTerms.hasFormat, r_jsonDoc);
	}
	
	private void init() {
		loc = URI.create(ui.getPath());
		base = ui.getBaseUri();
	}

	protected ResponseBuilder doGet(final String lang) {
		populateModel();
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
		populateModel();
		StreamingOutput so = new StreamingOutput () {
			public void write(OutputStream output) throws IOException,
					WebApplicationException {
				Encoder enc = Encoder.get();
				OutputStreamWriter osw = new OutputStreamWriter(output);
				enc.encode(model, osw, true);	
			}};
		return Response.ok(so);
	}

	protected static void connectToNeigbours(Model model, Resource r_this,
		Resource r_next, Resource r_prev) {
		model.add(r_this, INTERVALS.nextInterval, r_next);
		model.add(r_this, TIME.intervalMeets, r_next);
		model.add(r_next, TIME.intervalMetBy, r_this);

		model.add(r_this, INTERVALS.previousInterval, r_prev);
		model.add(r_this, TIME.intervalMetBy, r_prev);
		model.add(r_prev, TIME.intervalMeets, r_this);
	}
	
	protected static void connectToNeighbour(Model model, Resource before, Resource after) {
		model.add(after, INTERVALS.previousInterval, before);
		model.add(before, TIME.intervalMeets, after);
		model.add(before, INTERVALS.nextInterval, after);
		model.add(after, TIME.intervalMetBy, before);
	}

	protected void connectToContainingInterval(Model model, Resource container,
			Resource contained) {
		Property typedContainerProperty;

		if (model.contains(contained, RDF.type, INTERVALS.CalendarHalf)) {
			typedContainerProperty = INTERVALS.intervalContainsHalf;
		} else if (model.contains(contained, RDF.type,
				INTERVALS.CalendarQuarter)) {
			typedContainerProperty = INTERVALS.intervalContainsQuarter;
		} else if (model.contains(contained, RDF.type, INTERVALS.CalendarMonth)) {
			typedContainerProperty = INTERVALS.intervalContainsMonth;
		} else if (model.contains(contained, RDF.type, INTERVALS.CalendarDay)) {
			typedContainerProperty = INTERVALS.intervalContainsDay;
		} else if (model.contains(contained, RDF.type, INTERVALS.CalendarHour)) {
			typedContainerProperty = INTERVALS.intervalContainsHour;
		} else if (model
				.contains(contained, RDF.type, INTERVALS.CalendarMinute)) {
			typedContainerProperty = INTERVALS.intervalContainsMinute;
		} else if (model
				.contains(contained, RDF.type, INTERVALS.CalendarSecond)) {
			typedContainerProperty = INTERVALS.intervalContainsSecond;
		} else {
			throw new WebApplicationException(
					Response.Status.INTERNAL_SERVER_ERROR);
		}

		model.add(container, typedContainerProperty, contained);
		model.add(container, TIME.intervalContains, contained);
		model.add(contained, TIME.intervalDuring, container);
	}

	protected void setNamespaces() {
		model.setNsPrefix("time", TIME.NS).setNsPrefix("skos", SKOS.NS)
				.setNsPrefix("interval", INTERVALS.NS).setNsPrefix("owl",
						OWL.NS).setNsPrefix("foaf", FOAF.NS).setNsPrefix("dc",
						DC_11.NS).setNsPrefix("dct", DCTypes.NS).setNsPrefix(
						"dcterms", DCTerms.NS);
	}

	protected static String toXsdDateTime(int yr, int moy, int dom, int hod, int moh, int som) {
		GregorianOnlyCalendar cal = new GregorianOnlyCalendar(yr, moy+Calendar.JANUARY-1, dom, hod, moh, som);
		return iso8601format.format(cal.getTime());
	}

	protected void addPlaceTimeLink(Model model, Date d, Literal isoDuration) {
		String s_isoDate = iso8601format.format(d);
		// Link to www.placetime.com
		String s_placeTimeURI = "http://www.placetime.com/interval/gregorian/" + s_isoDate+"Z/"+isoDuration.getLexicalForm();
		Resource r_placeTimeInterval = model.createResource(s_placeTimeURI,TIME.Interval);
		model.add(r_thisTemporalEntity, TIME.intervalEquals, r_placeTimeInterval);
	}
	
	protected void addPlaceTimeInstantLink(Model model, Date d) {
		String s_isoDate = iso8601format.format(d);
		// Link to www.placetime.com
		String s_placeTimeURI = "http://www.placetime.com/instant/gregorian/" + s_isoDate+"Z";
		Resource r_placeTimeInstant = model.createResource(s_placeTimeURI,TIME.Instant);
		model.add(r_thisTemporalEntity, TIME.hasBeginning, r_placeTimeInstant);
	}

	static protected void setDayOfWeek(Model m, Resource r_day, int i_dow) {
		Resource r_dow = null;
		switch (i_dow) {
		case Calendar.MONDAY:
			r_dow = TIME.Monday;
			break;
		case Calendar.TUESDAY:
			r_dow = TIME.Tuesday;
			break;
		case Calendar.WEDNESDAY:
			r_dow = TIME.Wednesday;
			break;
		case Calendar.THURSDAY:
			r_dow = TIME.Thursday;
			break;
		case Calendar.FRIDAY:
			r_dow = TIME.Friday;
			break;
		case Calendar.SATURDAY:
			r_dow = TIME.Saturday;
			break;
		case Calendar.SUNDAY:
			r_dow = TIME.Sunday;
			break;
		}
		if (r_dow != null)
			m.add(r_day, TIME.dayOfWeek, r_dow);
	}

	static public String getDecimalSuffix(int dom) {
		dom = dom % 100;
		return (((dom != 11) && ((dom % 10) == 1)) ? "st" :
			    ((dom != 12) && ((dom % 10) == 2)) ? "nd" :
			    ((dom != 13) && ((dom % 10) == 3)) ? "rd" : "th");
	}
}