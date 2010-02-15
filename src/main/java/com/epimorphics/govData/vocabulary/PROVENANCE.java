/* CVS $Id: $ */
package com.epimorphics.govData.vocabulary; 
import com.hp.hpl.jena.rdf.model.*;
 
/**
 * Vocabulary definitions from http://purl.org/net/provenance/ns 
 * @author Auto-generated by schemagen on 15 Feb 2010 00:19 
 */
public class PROVENANCE {
    /** <p>The RDF model that holds the vocabulary terms</p> */
    private static Model m_model = ModelFactory.createDefaultModel();
    
    /** <p>The namespace of the vocabulary as a string</p> */
    public static final String NS = "http://purl.org/net/provenance/ns#";
    
    /** <p>The namespace of the vocabulary as a string</p>
     *  @see #NS */
    public static String getURI() {return NS;}
    
    /** <p>The namespace of the vocabulary as a resource</p> */
    public static final Resource NAMESPACE = m_model.createResource( NS );
    
    /** <p>Actor is a general class that represents actors which usually performed the 
     *  execution (see the class &lt;a xmlns="http://www.w3.org/1999/xhtml" href="#Execution" 
     *  xml:lang="en"&gt;Execution&lt;/a&gt;) of an action or a process.</p>
     */
    public static final Resource Actor = m_model.createResource( "http://purl.org/net/provenance/ns#Actor" );
    
    /** <p>Artifact is a general class that represents artifacts which can be used during 
     *  the execution (see the class &lt;a xmlns="http://www.w3.org/1999/xhtml" href="#Execution" 
     *  xml:lang="en"&gt;Execution&lt;/a&gt;) of an action or a process and which 
     *  can also be the result of such an execution.</p>
     */
    public static final Resource Artifact = m_model.createResource( "http://purl.org/net/provenance/ns#Artifact" );
    
    /** <p>CreationGuideline is a class that represents a guideline used to guide the 
     *  execution of a data creation. Examples for creation guidelines are transformation 
     *  rules, mapping definitions, entailment rules, and database queries.</p>
     */
    public static final Resource CreationGuideline = m_model.createResource( "http://purl.org/net/provenance/ns#CreationGuideline" );
    
    /** <p>DataAccess is a class that represents the completed execution of accessing 
     *  a data item on the Web.</p>
     */
    public static final Resource DataAccess = m_model.createResource( "http://purl.org/net/provenance/ns#DataAccess" );
    
    /** <p>DataCreation is a class that represents the completed creation of a data item.</p> */
    public static final Resource DataCreation = m_model.createResource( "http://purl.org/net/provenance/ns#DataCreation" );
    
    /** <p>DataItem is a general class that represents data items of any kind.</p> */
    public static final Resource DataItem = m_model.createResource( "http://purl.org/net/provenance/ns#DataItem" );
    
    /** <p>DataProvidingService is a class that represents a non-human actor - usually 
     *  a Web service or a server - that processes data access requests and actually 
     *  sends the requested Web representations (i.e. &lt;a xmlns="http://www.w3.org/1999/xhtml" 
     *  href="#Representation" xml:lang="en"&gt;prv:Representation&lt;/a&gt;) over 
     *  the Web.</p>
     */
    public static final Resource DataProvidingService = m_model.createResource( "http://purl.org/net/provenance/ns#DataProvidingService" );
    
    /** <p>DataPublisher is a class that represents entities such as persons, groups, 
     *  or organizations who use a data providing service (see class &lt;a xmlns="http://www.w3.org/1999/xhtml" 
     *  href="#DataProvidingService" xml:lang="en"&gt;prv:DataProvidingService&lt;/a&gt;) 
     *  to publish data on the Web.</p>
     */
    public static final Resource DataPublisher = m_model.createResource( "http://purl.org/net/provenance/ns#DataPublisher" );
    
    /** <p>Execution is a general class that represents completed executions of actions 
     *  or processes. An execution is usually performed by an actor (see the class 
     *  &lt;a xmlns="http://www.w3.org/1999/xhtml" href="#Actor" xml:lang="en"&gt;Actor&lt;/a&gt;) 
     *  and an execution, in most cases, yielded an artifact (see the class &lt;a 
     *  xmlns="http://www.w3.org/1999/xhtml" href="#Artifact" xml:lang="en"&gt;Artifact&lt;/a&gt;).</p>
     */
    public static final Resource Execution = m_model.createResource( "http://purl.org/net/provenance/ns#Execution" );
    
    /** <p>File is a general class that represents files/documents of any kind.</p> */
    public static final Resource File = m_model.createResource( "http://purl.org/net/provenance/ns#File" );
    
    /** <p>HumanActor is a general class that represents actors who are social beings 
     *  such as persons, organizations, companies.</p>
     */
    public static final Resource HumanActor = m_model.createResource( "http://purl.org/net/provenance/ns#HumanActor" );
    
    /** <p>NonHumanActor is a general class that represents actors who are not social 
     *  beings.</p>
     */
    public static final Resource NonHumanActor = m_model.createResource( "http://purl.org/net/provenance/ns#NonHumanActor" );
    
    /** <p>Representation is a general concept that represents a Web representation of 
     *  an information resource such as a data item. Such a Web representation was 
     *  used during the retrieval of a data item from the Web.</p>
     */
    public static final Resource Representation = m_model.createResource( "http://purl.org/net/provenance/ns#Representation" );
    
    public static final Resource __ = m_model.createResource( "http://purl.org/net/provenance/ns#" );
    
}