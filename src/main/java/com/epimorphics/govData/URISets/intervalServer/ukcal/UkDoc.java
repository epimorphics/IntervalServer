/******************************************************************
 * File:        UkDoc.java
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

package com.epimorphics.govData.URISets.intervalServer.ukcal;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.util.Calendar;
import java.util.Locale;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.epimorphics.govData.URISets.intervalServer.BaseURI;
import com.epimorphics.govData.URISets.intervalServer.Constants;
import com.epimorphics.govData.URISets.intervalServer.gregorian.IntervalDoc;
import com.epimorphics.govData.URISets.intervalServer.util.BritishCalendar;
import com.epimorphics.govData.URISets.intervalServer.util.CalendarUtils;
import com.epimorphics.govData.URISets.intervalServer.util.Duration;
import com.epimorphics.govData.URISets.intervalServer.util.GregorianOnlyCalendar;
import com.epimorphics.govData.vocabulary.DCTERMS;
import com.epimorphics.govData.vocabulary.DGU;
import com.epimorphics.govData.vocabulary.DOAP;
import com.epimorphics.govData.vocabulary.FOAF;
import com.epimorphics.govData.vocabulary.FRBR;
import com.epimorphics.govData.vocabulary.INTERVALS;
import com.epimorphics.govData.vocabulary.PROVENANCE;
import com.epimorphics.govData.vocabulary.SCOVO;
import com.epimorphics.govData.vocabulary.SKOS;
import com.epimorphics.govData.vocabulary.TIME;
import com.epimorphics.govData.vocabulary.VOID;
import com.epimorphics.jsonrdf.Encoder;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.DCTypes;
import com.hp.hpl.jena.vocabulary.DC_11;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;

abstract public class UkDoc extends UkCalURITemplate implements Constants {
	@Context UriInfo ui;
	@Context HttpHeaders hdrs;

	static private Logger logger = LoggerFactory.getLogger(UkDoc.class);
	
	protected URI loc;
	protected URI base;
	protected URI contentURI;
	protected URI setURI;
	
	protected String ext;
	
	protected int year, half, quarter, month, day, hour, min, sec;
	protected int woy_week, woy_year;
	
	protected Calendar startTime;
	
	protected Resource r_thisTemporalEntity;
	
	protected Model model = ModelFactory.createDefaultModel();
	
	static final protected Literal oneSecond = ResourceFactory.createTypedLiteral("PT1S", XSDDatatype.XSDduration);
	static final protected Literal oneMinute = ResourceFactory.createTypedLiteral("PT1M", XSDDatatype.XSDduration);
	static final protected Literal oneHour	= ResourceFactory.createTypedLiteral("PT1H", XSDDatatype.XSDduration);
	static final protected Literal oneDay 	= ResourceFactory.createTypedLiteral("P1D", XSDDatatype.XSDduration);
	static final protected Literal oneWeek 	= ResourceFactory.createTypedLiteral("P7D", XSDDatatype.XSDduration);
	static final protected Literal oneMonth 	= ResourceFactory.createTypedLiteral("P1M", XSDDatatype.XSDduration);
	static final protected Literal oneQuarter = ResourceFactory.createTypedLiteral("P3M", XSDDatatype.XSDduration);
	static final protected Literal oneHalf 	= ResourceFactory.createTypedLiteral("P6M", XSDDatatype.XSDduration);
	static final protected Literal oneYear 	= ResourceFactory.createTypedLiteral("P1Y", XSDDatatype.XSDduration);
	
	
	
	protected URI getBaseUri() {
		return BaseURI.getBase() == null ? ui.getBaseUri() : BaseURI.getBase();
	}

	
	protected void setWeekOfYearAndMonth(int year, int month, int day) {
		BritishCalendar cal = new BritishCalendar(Locale.UK);
		cal.setLenient(false);
		cal.set(year, month-1, day, 0, 0, 0);
		try {
			woy_week = cal.get(Calendar.WEEK_OF_YEAR);
			woy_year = CalendarUtils.getWeekOfYearYear(cal);
		} catch (IllegalArgumentException e) {
			throw new WebApplicationException(e, Status.NOT_FOUND);
		}
	}
	
	protected void  reset() {
		half = quarter = month = day = 1;
		woy_week = 1;
		hour = min = sec = 0;
	}
	

	protected Response doGet() {
		return ext.equals(EXT_RDF) ?     (doGetRDF().contentLocation(loc).type("application/rdf+xml").build()) : 
			   ext.equals(EXT_TTL) ?  (doGetTurtle().contentLocation(loc).type("text/turtle").build())  : 
			   ext.equals(EXT_JSON) ?  (doGetJson().contentLocation(loc).type("application/json").build())  : 
			   ext.equals(EXT_N3)  ?  (doGetTurtle().contentLocation(loc).type("text/n3").build()) : 
				                          (doGetNTriple().contentLocation(loc).type("text/plain").build());
	}
	
	protected void populateModel () {
		try {
			startTime.getTimeInMillis();
		} catch (IllegalArgumentException e) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		setNamespaces();
		addThisTemporalEntity();
		addDocInfo();
		addNeighboringIntervals();
		addContainingIntervals();
		addContainedIntervals();	
	}
	
	abstract void  addThisTemporalEntity();
	abstract void  addNeighboringIntervals();
	abstract void  addContainingIntervals();
	abstract void  addContainedIntervals();
	

	protected void addDocInfo() {
		String documentStem = r_thisTemporalEntity.getURI().replaceFirst(ID_STEM, DOC_STEM);
		Resource r_doc = model.createResource(loc.toString(), FOAF.Document);
		
		model.add(r_thisTemporalEntity, FOAF.isPrimaryTopicOf, r_doc);
		model.add(r_doc, FOAF.primaryTopic, r_thisTemporalEntity);

		Statement s_comment = model.getProperty(r_thisTemporalEntity, RDFS.label);
		String l = (s_comment!=null) ? s_comment.getString() : null;
		addDocumentLabels(r_doc, l);
		
		r_doc.addProperty(RDF.type, PROVENANCE.DataItem);
		addProvenanceMetadata(r_doc, false);
	}
	
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

		if (contained.hasProperty(RDF.type,INTERVALS.Half) ||
			contained.hasProperty(RDF.type,INTERVALS.CalendarHalf) ||
			contained.hasProperty(RDF.type,INTERVALS.BusinessHalf)) {
		
			typedContainerProperty = INTERVALS.intervalContainsHalf;
				
		} else if (contained.hasProperty(RDF.type,INTERVALS.Quarter) ||
				   contained.hasProperty(RDF.type,INTERVALS.CalendarQuarter) ||
				   contained.hasProperty(RDF.type,INTERVALS.BusinessQuarter)) {
			
			typedContainerProperty = INTERVALS.intervalContainsQuarter;
		
		} else if (contained.hasProperty(RDF.type,INTERVALS.Month) ||
					contained.hasProperty(RDF.type,INTERVALS.CalendarMonth)){ 
					
			typedContainerProperty = INTERVALS.intervalContainsMonth;
						
		} else if (contained.hasProperty(RDF.type,INTERVALS.Day) ||
				   contained.hasProperty(RDF.type,INTERVALS.CalendarDay) ) {
		
			typedContainerProperty = INTERVALS.intervalContainsDay;
			
		} else if (contained.hasProperty(RDF.type,INTERVALS.Hour) ||
		      	   contained.hasProperty(RDF.type,INTERVALS.CalendarHour)) {
			
			typedContainerProperty = INTERVALS.intervalContainsHour;
								
		} else if (contained.hasProperty(RDF.type,INTERVALS.Minute) ||
		      	   contained.hasProperty(RDF.type,INTERVALS.CalendarMinute)) {
			
			typedContainerProperty = INTERVALS.intervalContainsMinute;
								
		} else if (contained.hasProperty(RDF.type,INTERVALS.Second) ||
		      	   contained.hasProperty(RDF.type,INTERVALS.CalendarSecond)) {
			
			typedContainerProperty = INTERVALS.intervalContainsSecond;
								
		} else {
			typedContainerProperty = null;
		}

		if(typedContainerProperty!=null)
				container.addProperty(typedContainerProperty, contained);
		container.addProperty(TIME.intervalContains, contained);
		contained.addProperty(TIME.intervalDuring, container);
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
		.setNsPrefix("prv", PROVENANCE.NS)
		.setNsPrefix("void",VOID.NS)
		.setNsPrefix("doap", DOAP.NS)
		.setNsPrefix("frbr", FRBR.NS)
		;
	}

	protected void addGeneralIntervalTimeLink(Model model, Calendar d, Literal isoDuration) {
		Duration dur = new Duration(isoDuration.getLexicalForm());
		GregorianOnlyCalendar gcal = new GregorianOnlyCalendar(Locale.UK);
		gcal.setTimeInMillis(d.getTimeInMillis());

		Calendar calEnd = (Calendar) d.clone();
		GregorianOnlyCalendar gcEnd = (GregorianOnlyCalendar) gcal.clone();
		
		dur.addToCalendar(calEnd);
		dur.addToCalendar(gcEnd);
		Resource r_interval;
		
		if(calEnd.getTimeInMillis()==gcEnd.getTimeInMillis()) {
			// All is fine and dandy!
			r_interval = IntervalDoc.createResourceAndLabels(base, model , gcal, dur);
		} else  {
			Duration dur2 = gcal.getDurationTo(calEnd);
			r_interval = IntervalDoc.createResourceAndLabels(base, model , gcal , dur2);
		}	
		r_thisTemporalEntity.addProperty(TIME.intervalEquals, r_interval);
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
	
	protected void initSetModel(Resource r_set, Resource r_doc, String docLabel) {

		setNamespaces();

		//Statements to make in every set.
		model.add(r_set, DGU.status, DGU.draft);
		model.add(r_set, FOAF.isPrimaryTopicOf, r_doc);
		model.add(r_doc, FOAF.primaryTopic, r_set);
		
		addDocumentLabels(r_doc, docLabel);
		addProvenanceMetadata(r_doc, true);
	}

	private void addProvenanceMetadata(Resource r_doc, boolean includeMaintainer) {

		// Identify the publisher as user of the sofware that provides the data providing service
		Resource r_publisher = model.createResource(PROVENANCE.DataPublisher);
		r_publisher.addProperty(RDF.type, FRBR.CorporateBody);
		r_publisher.addProperty(FOAF.name, PUBLISHER_NAME, "en");
		
		Resource r_actor = model.createResource(PROVENANCE.DataProvidingService);
		r_actor.addProperty(DOAP.name, SERVICE_NAME, "en");
		r_actor.addProperty(PROVENANCE.operatedBy,r_publisher);

		// Add some DCTERMS about the document
		r_doc.addProperty(DCTERMS.publisher, r_publisher);
		r_doc.addProperty(DCTERMS.license, model.createResource(LICENSE_URI));
		r_doc.addLiteral(DCTERMS.dateCopyrighted, XSDDatatype.XSDdate.parse(RELEASE_DATE));
		r_doc.addProperty(DCTERMS.rightsHolder, r_publisher);
		r_doc.addProperty(DCTERMS.creator, r_actor);

		// Attribute the document creation to a data providing service
		Resource r_creation = model.createResource(PROVENANCE.DataCreation);
		r_doc.addProperty(PROVENANCE.createdBy, r_creation);
		r_creation.addProperty(PROVENANCE.performedBy, r_actor);
		r_creation.addLiteral(PROVENANCE.performedAt,XSDDatatype.XSDdate.parse(RELEASE_DATE));


		// Add some DOAP about the release of the data providing service software
		Resource r_project = model.createResource(DOAP.Project);
		r_project.addProperty(DOAP.name, PROJECT_NAME);
		r_actor.addProperty(PROVENANCE.employedArtifact, r_project);

		// Add some information about this revision of the software.
		Resource r_version = model.createResource(DOAP.Version);
		r_version.addProperty(DOAP.name, RELEASE_NAME, "en");
		r_version.addProperty(DOAP.created, RELEASE_DATE, XSDDatatype.XSDdate);
		r_version.addProperty(DOAP.revision, RELEASE_REVISION);
		r_project.addProperty(DOAP.release, r_version);
		r_project.addProperty(PROVENANCE.usedBy, r_publisher);
		
		if(includeMaintainer) {
			// Attribute maintenance of the data providing service
			Resource r_maintainer = model.createResource(FOAF.Person);
			r_maintainer.addProperty(FOAF.name, MAINTAINER_NAME);
			r_maintainer.addProperty(FOAF.mbox_sha1sum, MAINTAINER_MBOX_SHA1);
			r_project.addProperty(DOAP.maintainer, r_maintainer);
		}
	}
	
	private void addDocumentLabels(Resource r_doc, String docLabel) {
		if(loc.equals(contentURI)) {
			String s_mediaType = 
				ext.equals(EXT_NT)   ? "text/plain" :
				ext.equals(EXT_N3)   ? "text/n3" :
				ext.equals(EXT_TTL)  ? "text/turtle" :
				ext.equals(EXT_JSON) ? "application/json" :
			    ext.equals(EXT_RDF)  ? "application/rdf+xml" :"application/octet-stream" ;
			
			String s_preamble = 
				ext.equals(EXT_NT)   ? "N-Triple document" :
				ext.equals(EXT_N3)   ? "N3 document" :
				ext.equals(EXT_TTL)  ? "Turtle document" :
				ext.equals(EXT_JSON) ? "JSON document" :
			    ext.equals(EXT_RDF)  ? "RDF/XML document" :"Unknown format document" ;

			if(docLabel != null && !docLabel.equals("")) {
				String l = s_preamble +" about: "+docLabel;
				model.add(r_doc, RDFS.label, l, "en" );
			}
			Resource r_mediaType = model.createResource();
			r_mediaType.addProperty(RDFS.label, s_mediaType, XSDDatatype.XSDstring);
			model.add(r_doc, DCTERMS.format, r_mediaType);
			
		} else {
			Resource r_ntDoc   = createDocResource(loc+"."+EXT_NT,   "text/plain");
			Resource r_rdfDoc  = createDocResource(loc+"."+EXT_RDF,  "application/rdf+xml");
			Resource r_ttlDoc  = createDocResource(loc+"."+EXT_TTL,  "text/turtle");
			Resource r_n3Doc   = createDocResource(loc+"."+EXT_N3,   "text/n3");
			Resource r_jsonDoc = createDocResource(loc+"."+EXT_JSON,  "application/json");
			
			r_doc.addProperty(DCTERMS.hasFormat, r_ntDoc);
			r_doc.addProperty(DCTERMS.hasFormat, r_rdfDoc);
			r_doc.addProperty(DCTERMS.hasFormat, r_ttlDoc);
			r_doc.addProperty(DCTERMS.hasFormat, r_n3Doc);
			r_doc.addProperty(DCTERMS.hasFormat, r_jsonDoc);
			
			if(docLabel != null && !docLabel.equals("")) {
				
				String label = "Generic Dataset document about: "+docLabel;
				model.add(r_doc, RDFS.label, label, "en" );
				
				label = "N-Triple document about: "+docLabel;
				model.add(r_ntDoc, RDFS.label, label, "en" );
				
				label = "N3 document about: "+docLabel;
				model.add(r_n3Doc, RDFS.label, label, "en" );
				
				label = "Turtle document about: "+docLabel;
				model.add(r_ttlDoc, RDFS.label, label, "en" );
				
				label = "JSON document about: "+docLabel;
				model.add(r_jsonDoc, RDFS.label, label, "en" );
				
				label = "RDF/XML document about: "+docLabel;
				model.add(r_rdfDoc, RDFS.label, label, "en" );
			}
		}
	}

	private Resource createDocResource(String docURI, String mediaType) {
		Resource r_doc = model.createResource(docURI, FOAF.Document);
		Resource r_mediaType = model.createResource();
		r_mediaType.addProperty(RDFS.label, mediaType);
		model.add(r_doc, DCTERMS.format, r_mediaType);
		return r_doc;
	}
	
	protected void addLinkset(Resource r_superSet, 
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

	protected Resource createSet(String uri, String label) {
		Resource r_set = model.createResource(uri, VOID.Dataset);
		r_set.addProperty(RDF.type, DGU.URIset);
		r_set.addProperty(RDFS.label, label, "en");
		r_set.addProperty(SKOS.prefLabel, label, "en");
		return r_set;
	}

	protected Resource createIntervalSet() {
		return createSet(base+INTERVAL_SET_RELURI, INTERVAL_SET_LABEL);
	}
	protected Resource createInstantSet() {
		return createSet(base+INSTANT_SET_RELURI, INSTANT_SET_LABEL);
	}

	protected Resource createSecSet() {
		return createSet(base+SECOND_SET_RELURI, SECOND_SET_LABEL);
	}

	protected Resource createMinSet() {
		return createSet(base+MINUTE_SET_RELURI, MINUTE_SET_LABEL);
	}

	protected Resource createHourSet() {
		return createSet(base+HOUR_SET_RELURI, HOUR_SET_LABEL);
	}

	protected Resource createDaySet() {
		return createSet(base+DAY_SET_RELURI, DAY_SET_LABEL);
	}

	protected Resource createWeekSet() {
		return createSet(base+WEEK_SET_RELURI, WEEK_SET_LABEL);
	}

	protected Resource createMonthSet() {
		return createSet(base+MONTH_SET_RELURI, MONTH_SET_LABEL);
	}

	protected Resource createQuarterSet() {
		return createSet(base+QUARTER_SET_RELURI, QUARTER_SET_LABEL);
	}

	protected Resource createHalfSet() {
		return createSet(base+HALF_SET_RELURI, HALF_SET_LABEL);
	}

	protected Resource createYearSet() {
		return createSet(base+YEAR_SET_RELURI, YEAR_SET_LABEL);
	}

	protected void addCalendarActRef(Resource r_set) {
		Resource r_calendarAct;
		model.add(r_set, DCTERMS.source, r_calendarAct=model.createResource(CALENDAR_ACT_URI));
		model.add(r_calendarAct, RDFS.label, "Calendar (New Style) Act 1750.","en");
		model.add(r_calendarAct, SKOS.prefLabel, "Calendar (New Style) Act 1750.","en");
	}	
	
	protected void addGregorianSourceRef(Resource r_set) {
		Resource r_calendarAct;
		model.add(r_set, DCTERMS.source, r_calendarAct=model.createResource("http://en.wikipedia.org/wiki/Gregorian_calendar"));
		model.add(r_calendarAct, RDFS.label, "Wikipedia on "+CALENDAR_NAME+" Calendar","en");
		model.add(r_calendarAct, SKOS.prefLabel, "Wikipedia on "+CALENDAR_NAME+" Calendar","en");
	}
	
	/**
	 * Add Ordinal fields to the current temporal entity.
	 * 
	 */
	protected static void addCalendarOrdinals(Resource res, int year, int moy, int dom, int hod, int moh, int som ) {
		res.addProperty(INTERVALS.ordinalSecondOfMinute, Integer.toString(som), XSDDatatype.XSDinteger);
		addCalendarOrdinals(res, year, moy, dom, hod, moh );
	}
	
	protected static void addCalendarOrdinals(Resource res, int year, int moy, int dom, int hod, int moh) {
		res.addProperty(INTERVALS.ordinalMinuteOfHour,   Integer.toString(moh), XSDDatatype.XSDinteger);
		addCalendarOrdinals(res, year, moy, dom, hod );
	}

	protected static void addCalendarOrdinals(Resource res, int year, int moy, int dom, int hod) {
		res.addProperty(INTERVALS.ordinalHourOfDay,   Integer.toString(hod), XSDDatatype.XSDinteger);
		addCalendarOrdinals(res, year, moy, dom);
	}
	
	protected static void addCalendarWoyOrdinals(Resource res, int year, int woy) {
		res.addProperty(INTERVALS.ordinalWeekOfYear,     Integer.toString(woy), XSDDatatype.XSDinteger);
		res.addProperty(INTERVALS.ordinalWeekOfYearYear, Integer.toString(year),  XSDDatatype.XSDinteger);
	}
	
	protected static void addCalendarOrdinals(Resource res, int year, int moy, int dom) {
		res.addProperty(INTERVALS.ordinalDayOfMonth, 	  Integer.toString(dom), XSDDatatype.XSDinteger);

		BritishCalendar cal = new BritishCalendar(Locale.UK);
		cal.set(year, moy-1,dom);
		int woy = cal.get(Calendar.WEEK_OF_YEAR);
		int dow = CalendarUtils.calendarDayToOrdinalDay(cal.get(Calendar.DAY_OF_WEEK));
		int doy =  cal.get(Calendar.DAY_OF_YEAR);
		int woy_year = CalendarUtils.getWeekOfYearYear(cal);

		addCalendarWoyOrdinals(res, woy_year, woy);

		res.addProperty(INTERVALS.ordinalDayOfWeek,      Integer.toString(dow), XSDDatatype.XSDinteger);
		res.addProperty(INTERVALS.dayOfWeek,     		  CalendarUtils.ordinalDayOfWeekToDayOfWeek(dow));
		res.addProperty(INTERVALS.ordinalDayOfYear,      Integer.toString(doy), XSDDatatype.XSDinteger);

		addCalendarOrdinals(res, year, moy);
	}

	protected static void addCalendarOrdinals(Resource res, int year, int moy) {
//		int hoy = ((moy-1)/6)+1;
		int qoy = ((moy-1)/3)+1;
		res.addProperty(INTERVALS.ordinalMonthOfYear,	  Integer.toString(moy), XSDDatatype.XSDinteger);
		res.addProperty(INTERVALS.monthOfYear,	  		  CalendarUtils.ordinalMonthOfYearToMonthOfYear(moy));
		
		addCalendarQoyOrdinals(res, year, qoy);
	}

	protected static void addCalendarQoyOrdinals(Resource res, int year, int qoy) {
		res.addProperty(INTERVALS.ordinalQuarterOfYear,     Integer.toString(qoy), XSDDatatype.XSDinteger);
		addCalendarHoyOrdinals(res, year, ((qoy-1)/2)+1);
	}	
	
	protected static void addCalendarHoyOrdinals(Resource res, int year, int hoy) {
		res.addProperty(INTERVALS.ordinalHalfOfYear,     Integer.toString(hoy), XSDDatatype.XSDinteger);
		addCalendarOrdinals(res, year);
	}
	
	protected static void addCalendarOrdinals(Resource res, int year) {
		res.addProperty(INTERVALS.ordinalYear, Integer.toString(year), XSDDatatype.XSDinteger);
	}
}