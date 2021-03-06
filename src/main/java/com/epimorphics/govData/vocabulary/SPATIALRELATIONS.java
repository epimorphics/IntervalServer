/* CVS $Id: $ */
package com.epimorphics.govData.vocabulary; 
import com.hp.hpl.jena.rdf.model.*;
 
/**
 * Vocabulary definitions from ../../../../../resources/vocabulary/spatialrelations.owl 
 * @author Auto-generated by schemagen on 04 Jun 2010 13:58 
 */
public class SPATIALRELATIONS {
    /** <p>The RDF model that holds the vocabulary terms</p> */
    private static Model m_model = ModelFactory.createDefaultModel();
    
    /** <p>The namespace of the vocabulary as a string</p> */
    public static final String NS = "http://data.ordnancesurvey.co.uk/ontology/spatialrelations/";
    
    /** <p>The namespace of the vocabulary as a string</p>
     *  @see #NS */
    public static String getURI() {return NS;}
    
    /** <p>The namespace of the vocabulary as a resource</p> */
    public static final Resource NAMESPACE = m_model.createResource( NS );
    
    public static final Property containedBy = m_model.createProperty( "http://data.ordnancesurvey.co.uk/ontology/spatialrelations/containedBy" );
    
    public static final Property contains = m_model.createProperty( "http://data.ordnancesurvey.co.uk/ontology/spatialrelations/contains" );
    
    public static final Property disjoint = m_model.createProperty( "http://data.ordnancesurvey.co.uk/ontology/spatialrelations/disjoint" );
    
    public static final Property easting = m_model.createProperty( "http://data.ordnancesurvey.co.uk/ontology/spatialrelations/easting" );
    
    public static final Property equals = m_model.createProperty( "http://data.ordnancesurvey.co.uk/ontology/spatialrelations/equals" );
    
    public static final Property northing = m_model.createProperty( "http://data.ordnancesurvey.co.uk/ontology/spatialrelations/northing" );
    
    public static final Property partiallyOverlaps = m_model.createProperty( "http://data.ordnancesurvey.co.uk/ontology/spatialrelations/partiallyOverlaps" );
    
    public static final Property touches = m_model.createProperty( "http://data.ordnancesurvey.co.uk/ontology/spatialrelations/touches" );
    
}
