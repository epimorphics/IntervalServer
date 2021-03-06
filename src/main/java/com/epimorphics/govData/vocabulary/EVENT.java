/* CVS $Id: $ */
package com.epimorphics.govData.vocabulary; 
import com.hp.hpl.jena.rdf.model.*;
 
/**
 * Vocabulary definitions from http://purl.org/NET/c4dm/event.owl 
 * @author Auto-generated by schemagen on 16 Jan 2010 16:54 
 */
public class EVENT {
    /** <p>The RDF model that holds the vocabulary terms</p> */
    private static Model m_model = ModelFactory.createDefaultModel();
    
    /** <p>The namespace of the vocabulary as a string</p> */
    public static final String NS = "http://purl.org/NET/c4dm/event.owl#";
    
    /** <p>The namespace of the vocabulary as a string</p>
     *  @see #NS */
    public static String getURI() {return NS;}
    
    /** <p>The namespace of the vocabulary as a resource</p> */
    public static final Resource NAMESPACE = m_model.createResource( NS );
    
    /** <p>Relates an event to an active agent (a person, a computer, ... :-) )</p> */
    public static final Property agent = m_model.createProperty( "http://purl.org/NET/c4dm/event.owl#agent" );
    
    public static final Property agent_in = m_model.createProperty( "http://purl.org/NET/c4dm/event.owl#agent_in" );
    
    /** <p>Relates an event to a passive factor (a tool, an instrument, an abstract cause...)</p> */
    public static final Property factor = m_model.createProperty( "http://purl.org/NET/c4dm/event.owl#factor" );
    
    public static final Property factor_of = m_model.createProperty( "http://purl.org/NET/c4dm/event.owl#factor_of" );
    
    public static final Property hasAgent = m_model.createProperty( "http://purl.org/NET/c4dm/event.owl#hasAgent" );
    
    public static final Property hasFactor = m_model.createProperty( "http://purl.org/NET/c4dm/event.owl#hasFactor" );
    
    public static final Property hasLiteralFactor = m_model.createProperty( "http://purl.org/NET/c4dm/event.owl#hasLiteralFactor" );
    
    public static final Property hasProduct = m_model.createProperty( "http://purl.org/NET/c4dm/event.owl#hasProduct" );
    
    public static final Property hasSubEvent = m_model.createProperty( "http://purl.org/NET/c4dm/event.owl#hasSubEvent" );
    
    public static final Property isAgentIn = m_model.createProperty( "http://purl.org/NET/c4dm/event.owl#isAgentIn" );
    
    public static final Property isFactorOf = m_model.createProperty( "http://purl.org/NET/c4dm/event.owl#isFactorOf" );
    
    /** <p>Relates an event to a factor which can be described as a literal. This property 
     *  should not be used as-is, but should be subsumed by other, more specific, 
     *  properties (like an hypothetic :weatherCelsius, linking an event to a temperature).</p>
     */
    public static final Property literal_factor = m_model.createProperty( "http://purl.org/NET/c4dm/event.owl#literal_factor" );
    
    /** <p>Relates an event to a spatial object.</p> */
    public static final Property place = m_model.createProperty( "http://purl.org/NET/c4dm/event.owl#place" );
    
    public static final Property producedIn = m_model.createProperty( "http://purl.org/NET/c4dm/event.owl#producedIn" );
    
    public static final Property produced_in = m_model.createProperty( "http://purl.org/NET/c4dm/event.owl#produced_in" );
    
    /** <p>Relates an event to something produced during the event---a sound, a pie, 
     *  whatever...</p>
     */
    public static final Property product = m_model.createProperty( "http://purl.org/NET/c4dm/event.owl#product" );
    
    /** <p>This property provides a way to split a complex event (for example, a performance 
     *  involving several musicians) into simpler ones (one event per musician).</p>
     */
    public static final Property sub_event = m_model.createProperty( "http://purl.org/NET/c4dm/event.owl#sub_event" );
    
    /** <p>Relates an event to a time object, classifying a time region (either instantaneous 
     *  or having an extent). By using the Timeline ontology here, you can define 
     *  event happening on a recorded track or on any media with a temporal extent.</p>
     */
    public static final Property time = m_model.createProperty( "http://purl.org/NET/c4dm/event.owl#time" );
    
    /** <p>An arbitrary classification of a space/time region, by a cognitive agent. 
     *  An event may have actively participating agents, passive factors, products, 
     *  and a location in space/time.</p>
     */
    public static final Resource Event = m_model.createResource( "http://purl.org/NET/c4dm/event.owl#Event" );
    
    /** <p>Everything used as a factor in an event</p> */
    public static final Resource Factor = m_model.createResource( "http://purl.org/NET/c4dm/event.owl#Factor" );
    
    /** <p>Everything produced by an event</p> */
    public static final Resource Product = m_model.createResource( "http://purl.org/NET/c4dm/event.owl#Product" );
    
}
