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

package com.epimorphics.govData.URISets.intervalServer.interval;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URI;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.epimorphics.govData.URISets.intervalServer.BaseURI;
import com.epimorphics.govData.URISets.intervalServer.Constants;
import com.epimorphics.govData.URISets.intervalServer.URITemplate;
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

abstract public class Doc extends URITemplate implements Constants {
	@Context UriInfo ui;
	@Context HttpHeaders hdrs;

	static private Logger logger = LoggerFactory.getLogger(Doc.class);
	
	protected URI loc;
	protected URI base;
	protected URI contentURI;
	protected URI setURI;
	
	protected String ext;
	
	protected Model model = ModelFactory.createDefaultModel();

	
	protected URI getBaseUri() {
		return BaseURI.getBase() == null ? ui.getBaseUri() : BaseURI.getBase();
	}

	protected Response doGet() {
		return ext.equals(EXT_RDF) ?     (doGetRDF().contentLocation(loc).type("application/rdf+xml").build()) : 
			   ext.equals(EXT_TTL) ?  (doGetTurtle().contentLocation(loc).type("text/turtle").build())  : 
			   ext.equals(EXT_JSON) ?  (doGetJson().contentLocation(loc).type("application/json").build())  : 
			   ext.equals(EXT_N3)  ?  (doGetTurtle().contentLocation(loc).type("text/n3").build()) : 
				                          (doGetNTriple().contentLocation(loc).type("text/plain").build());
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
	
	protected void initSetModel(Resource r_set, Resource r_doc, String docLabel) {

		setNamespaces();

		//Statements to make in every set.
		model.add(r_set, DGU.status, DGU.draft);
		model.add(r_set, FOAF.isPrimaryTopicOf, r_doc);
		model.add(r_doc, FOAF.primaryTopic, r_set);
		
		addDocumentLabels(r_doc, docLabel);
		addProvenanceMetadata(r_doc);
	}

	private void addProvenanceMetadata(Resource r_doc) {
		// Attribute maintenance of the data providing service
		Resource r_maintainer = model.createResource(FOAF.Person);
		r_maintainer.addProperty(FOAF.name, MAINTAINER_NAME);
		r_maintainer.addProperty(FOAF.mbox_sha1sum, MAINTAINER_MBOX_SHA1);
		
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
		r_project.addProperty(DOAP.maintainer, r_maintainer);

		// Add some information about this revision of the software.
		Resource r_version = model.createResource(DOAP.Version);
		r_version.addProperty(DOAP.name, RELEASE_NAME, "en");
		r_version.addProperty(DOAP.created, RELEASE_DATE, XSDDatatype.XSDdate);
		r_version.addProperty(DOAP.revision, RELEASE_REVISION);
		r_project.addProperty(DOAP.release, r_version);
		r_project.addProperty(PROVENANCE.usedBy, r_publisher);
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
	
}