/******************************************************************
 * File:        GregorianURITemplate.java
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
package com.epimorphics.govData.URISets.intervalServer.calendar;


public class URITemplate {
	

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
	protected static final String DURATION_TOKEN		= "duration";

	protected static final String ID_STEM 				= "id/";
	protected static final String DOC_STEM 				= "doc/";
	
	
	protected static final String CALENDAR_STEM 		= "calendar";
	protected static final String CALENDAR_ID_STEM 		= ID_STEM + CALENDAR_STEM;

	
	protected static final String YEAR_ID_STEM 			= CALENDAR_ID_STEM + "-year/";
	protected static final String HALF_ID_STEM 			= CALENDAR_ID_STEM + "-half/";
	protected static final String QUARTER_ID_STEM 		= CALENDAR_ID_STEM + "-quarter/";
	protected static final String MONTH_ID_STEM 		= CALENDAR_ID_STEM + "-month/";
	protected static final String WEEK_ID_STEM 			= CALENDAR_ID_STEM + "-week/";
	protected static final String DAY_ID_STEM 			= CALENDAR_ID_STEM + "-day/";
	protected static final String HOUR_ID_STEM 			= CALENDAR_ID_STEM + "-hour/";
	protected static final String MINUTE_ID_STEM 		= CALENDAR_ID_STEM + "-minute/";
	protected static final String SECOND_ID_STEM		= CALENDAR_ID_STEM + "-second/";
	protected static final String INSTANT_ID_STEM		= CALENDAR_ID_STEM + "-instant/";
	protected static final String INTERVAL_ID_STEM		= CALENDAR_ID_STEM + "-interval/";

	protected static final String CALENDAR_DOC_STEM 	= DOC_STEM + CALENDAR_STEM;
	protected static final String YEAR_DOC_STEM 		= CALENDAR_DOC_STEM + "-year/";
	protected static final String HALF_DOC_STEM 		= CALENDAR_DOC_STEM + "-half/";
	protected static final String QUARTER_DOC_STEM 		= CALENDAR_DOC_STEM + "-quarter/";
	protected static final String MONTH_DOC_STEM 		= CALENDAR_DOC_STEM + "-month/";
	protected static final String WEEK_DOC_STEM 		= CALENDAR_DOC_STEM + "-week/";
	protected static final String DAY_DOC_STEM 			= CALENDAR_DOC_STEM + "-day/";
	protected static final String HOUR_DOC_STEM 		= CALENDAR_DOC_STEM + "-hour/";
	protected static final String MINUTE_DOC_STEM 		= CALENDAR_DOC_STEM + "-minute/";
	protected static final String SECOND_DOC_STEM 		= CALENDAR_DOC_STEM + "-second/";
	protected static final String INSTANT_DOC_STEM		= CALENDAR_DOC_STEM + "-instant/";
	protected static final String INTERVAL_DOC_STEM		= CALENDAR_DOC_STEM + "-interval/";

	protected static final String HALF_PREFIX			= "-H";
	protected static final String QUARTER_PREFIX		= "-Q";
	protected static final String MONTH_PREFIX			= "-";
	protected static final String WEEK_PREFIX			= "-W";
	protected static final String DAY_PREFIX			= "-";
	protected static final String HOUR_PREFIX			= "T";
	protected static final String MINUTE_PREFIX			= ":";
	protected static final String SECOND_PREFIX			= ":";
	protected static final String DURATION_PREFIX		= "/";
	protected static final String DURATION_REGEX 		= "P(([0-9]+)Y)?(([0-9]+)M)?(([0-9]+)D)?(T(([0-9]+)H)?(([0-9]+)M)?(([0-9]+)S)?)?";
	                                                      // ^2          ^4           ^6        ^7 ^9          ^11         ^13
	
	protected static int DURATION_YEARS 	= 2;
	protected static int DURATION_MONTHS 	= 4;
	protected static int DURATION_DAYS	 	= 6;
	protected static int DURATION_HOURS 	= 9;
	protected static int DURATION_MINUTES 	= 11;
	protected static int DURATION_SECONDS 	= 13;
	
	protected static final String YEAR_PATTERN 		=                                     "{" + YEAR_TOKEN    + ":[1-9][0-9]*}";
	protected static final String HALF_PATTERN 		= YEAR_PATTERN 	   + HALF_PREFIX    + "{" + HALF_TOKEN	  + ":[12]}";		// H1 or H2
	protected static final String QUARTER_PATTERN	= YEAR_PATTERN 	   + QUARTER_PREFIX + "{" + QUARTER_TOKEN + ":[1234]}";     // Q1, Q2, Q3 or Q4 
	protected static final String MONTH_PATTERN 	= YEAR_PATTERN 	   + MONTH_PREFIX   + "{" + MONTH_TOKEN	  + ":[0-1][0-9]}"; // Months 01 or 12 (well 19 but catch later)
	protected static final String WOY_PATTERN 		= YEAR_PATTERN 	   + WEEK_PREFIX    + "{" + WEEK_TOKEN	  + ":[0-5][0-9]}"; // Week of year 01 to 53 (well 59 but catch later)
	protected static final String WOM_PATTERN 		= YEAR_PATTERN 	   + MONTH_PREFIX   + "{" + MONTH_TOKEN   + ":[0-3][0-9]}" + WEEK_PREFIX    + "{" + WEEK_TOKEN	  + ":[1-5]}"; // Week of year 01 to 53 (well 59 but catch later)
	protected static final String DAY_PATTERN 		= MONTH_PATTERN    + DAY_PREFIX     + "{" + DAY_TOKEN     + ":[0-3][0-9]}"; // Day of month 01 to 31 (well 39 but catch later)
	protected static final String HOUR_PATTERN 		= DAY_PATTERN 	   + HOUR_PREFIX    + "{" + HOUR_TOKEN    + ":[0-2][0-9]}"; // Hour of day 
	protected static final String MINUTE_PATTERN 	= HOUR_PATTERN 	   + MINUTE_PREFIX  + "{" + MINUTE_TOKEN  + ":[0-5][0-9]}"; // Minute of hour
	protected static final String SECOND_PATTERN 	= MINUTE_PATTERN   + SECOND_PREFIX  + "{" + SECOND_TOKEN  + ":[0-5][0-9]}"; // Second of minute
	protected static final String INSTANT_PATTERN 	= SECOND_PATTERN;
	protected static final String INTERVAL_PATTERN  = SECOND_PATTERN    + "/"			+ "{" + DURATION_TOKEN + ":"+ DURATION_REGEX + "}";

	protected static final String EXT_RDF = "rdf";
	protected static final String EXT_NT  = "nt";
	protected static final String EXT_TTL = "ttl";
	protected static final String EXT_JSON = "json";
	protected static final String EXT_N3 = "n3";
	protected static final String EXT_PATTERN =".{"+EXT_TOKEN+": (("+EXT_RDF+")|("+EXT_NT+")|("+EXT_TTL+")|("+EXT_N3+")||("+EXT_JSON+"))}";
}