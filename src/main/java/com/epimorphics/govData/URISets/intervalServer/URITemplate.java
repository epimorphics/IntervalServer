/******************************************************************
 * File:        URITemplate.java
 * Created by:  Stuart Williams
 * Created on:  13 Feb 2010
 * 
 * (c) Copyright 2010, Epimorphics Limited
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  
 * $Id:  $
 *****************************************************************/

package com.epimorphics.govData.URISets.intervalServer;


public class URITemplate {

	protected static final String CALENDAR_ACT_URI="http://www.legislation.gov.uk/id/apgb/Geo2/24/23";
	protected static final String GREGORIAN_CALENDAR_REF ="http://en.wikipedia.org/wiki/Gregorian_calendar";
	
	protected static final String DBPEDIA_SUBJECT_GREGORIAN_CALENDAR 	= "http://dbpedia.org/resources/Gregorian_calendar";
	protected static final String DBPEDIA_SUBJECT_YEAR 				    = "http://dbpedia.org/resources/Year";
	protected static final String DBPEDIA_SUBJECT_MONTH 				= "http://dbpedia.org/resources/Month";
	protected static final String DBPEDIA_SUBJECT_QUARTER 			    = "http://dbpedia.org/resource/Fiscal_quarter";
//	protected static final String DBPEDIA_SUBJECT_HALF 				    = "http://dbpedia.org/resources/Year";
	protected static final String DBPEDIA_SUBJECT_WEEK 			    	= "http://dbpedia.org/resources/Week";
	protected static final String DBPEDIA_SUBJECT_DAY 					= "http://dbpedia.org/resources/Day";
	protected static final String DBPEDIA_SUBJECT_HOUR 					= "http://dbpedia.org/resources/Hour";
	protected static final String DBPEDIA_SUBJECT_MINUTE				= "http://dbpedia.org/resources/Minute";
	protected static final String DBPEDIA_SUBJECT_SECOND 				= "http://dbpedia.org/resources/Second";
//	protected static final String DBPEDIA_SUBJECT_INSTANT 				= "http://dbpedia.org/resources/Year";
	protected static final String DBPEDIA_SUBJECT_INTERVAL 				= "http://dbpedia.org/resources/Interval_(time)";
	
	
	protected static final String YEAR_TOKEN 			= "year";
	protected static final String HALF_TOKEN 			= "half";
	protected static final String QUARTER_TOKEN 		= "quarter";
	protected static final String MONTH_TOKEN 			= "month";
	protected static final String WEEK_TOKEN 			= "week";
	protected static final String DAY_TOKEN 			= "day";
	protected static final String HOUR_TOKEN 			= "hour";
	protected static final String MINUTE_TOKEN 			= "minute";
	protected static final String SECOND_TOKEN 			= "second";
	protected static final String EXT_TOKEN 			= "ext";
	protected static final String EXT2_TOKEN 			= "ext2";
	protected static final String DURATION_TOKEN		= "duration";

	public static final String ID_STEM 				= "id/";
	public static final String DOC_STEM 			= "doc/";
	public static final String SET_STEM				= "id/";
	
	protected static final String DURATION_REGEX 		= "P(([0-9]+)Y)?(([0-9]+)M)?(([0-9]+)D)?(T(([0-9]+)H)?(([0-9]+)M)?(([0-9]+)S)?)?";
	                                                      // ^2          ^4           ^6        ^7 ^9          ^11         ^13	
	protected static int DURATION_YEARS 	= 2;
	protected static int DURATION_MONTHS 	= 4;
	protected static int DURATION_DAYS	 	= 6;
	protected static int DURATION_HOURS 	= 9;
	protected static int DURATION_MINUTES 	= 11;
	protected static int DURATION_SECONDS 	= 13;

	public static final String EXT_RDF = "rdf";
	public static final String EXT_NT  = "nt";
	public static final String EXT_TTL = "ttl";
	public static final String EXT_JSON = "json";
	public static final String EXT_N3 = "n3";
	public static final String SET_EXT_PATTERN ="{"+EXT2_TOKEN+": (((\\."+EXT_RDF+")|(\\."+EXT_NT+")|(\\."+EXT_TTL+")|(\\."+EXT_N3+")||(\\."+EXT_JSON+"))$)?}";
	public static final String EXT_PATTERN =".{"+EXT_TOKEN+": (("+EXT_RDF+")|("+EXT_NT+")|("+EXT_TTL+")|("+EXT_N3+")||("+EXT_JSON+"))}";
}