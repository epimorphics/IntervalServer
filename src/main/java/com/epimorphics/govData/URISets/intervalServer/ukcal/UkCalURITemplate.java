/******************************************************************
 * File:        GregorianCalURITemplate.java
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

import com.epimorphics.govData.URISets.intervalServer.URITemplate;
import com.epimorphics.govData.URISets.intervalServer.gregorian.GregorianCalURITemplate;


public class UkCalURITemplate extends URITemplate {

	public static final String CALENDAR_STEM 		= 	"";
	public static final String CALENDAR_SET			=	"uk-calendar";
	public static final String CALENDAR_NAME		= 	"British";
	
	public static final String CALENDAR_SET_LABEL 	= CALENDAR_NAME+" calendar aligned intervals."; 
	public static final String YEAR_SET_LABEL 		= CALENDAR_NAME+" calendar aligned one year intervals."; 
	public static final String HALF_SET_LABEL 		= CALENDAR_NAME+" calendar aligned one half year intervals."; 
	public static final String QUARTER_SET_LABEL 	= CALENDAR_NAME+" calendar aligned one quarter year intervals."; 
	public static final String MONTH_SET_LABEL 		= CALENDAR_NAME+" calendar aligned one month intervals."; 
	public static final String WEEK_SET_LABEL 		= CALENDAR_NAME+" calendar aligned ISO8601 numbered week long intervals."; 
	public static final String DAY_SET_LABEL 		= CALENDAR_NAME+" calendar aligned one day intervals."; 
	public static final String HOUR_SET_LABEL 		= CALENDAR_NAME+" calendar aligned one hour intervals."; 
	public static final String MINUTE_SET_LABEL 	= CALENDAR_NAME+" calendar aligned one minute intervals."; 
	public static final String SECOND_SET_LABEL 	= CALENDAR_NAME+" calendar aligned one second intervals."; 
	public static final String INTERVAL_SET_LABEL 	= GregorianCalURITemplate.INTERVAL_SET_LABEL; 
	public static final String INSTANT_SET_LABEL 	= GregorianCalURITemplate.INSTANT_SET_LABEL; 

	public static final String CALENDAR_SEGMENT 	= CALENDAR_STEM;
	public static final String YEAR_SEGMENT 		= CALENDAR_STEM + "year";
	public static final String HALF_SEGMENT 		= CALENDAR_STEM + "half";
	public static final String QUARTER_SEGMENT 		= CALENDAR_STEM + "quarter";
	public static final String MONTH_SEGMENT 		= CALENDAR_STEM + "month";
	public static final String WEEK_SEGMENT 		= CALENDAR_STEM + "week";
	public static final String DAY_SEGMENT 			= CALENDAR_STEM + "day";
	public static final String HOUR_SEGMENT 		= CALENDAR_STEM + "hour";
	public static final String MINUTE_SEGMENT 		= CALENDAR_STEM + "minute";
	public static final String SECOND_SEGMENT		= CALENDAR_STEM + "second";
	public static final String INSTANT_SEGMENT		= CALENDAR_STEM + "instant";
	public static final String INTERVAL_SEGMENT		= CALENDAR_STEM + "interval";
	
	public static final String YEAR_ID_STEM 		= ID_STEM + YEAR_SEGMENT +"/";
	public static final String HALF_ID_STEM 		= ID_STEM + HALF_SEGMENT + "/";
	public static final String QUARTER_ID_STEM 		= ID_STEM + QUARTER_SEGMENT +"/";
	public static final String MONTH_ID_STEM 		= ID_STEM + MONTH_SEGMENT + "/";
	public static final String WEEK_ID_STEM 		= ID_STEM + WEEK_SEGMENT + "/";
	public static final String DAY_ID_STEM 			= ID_STEM + DAY_SEGMENT + "/";
	public static final String HOUR_ID_STEM 		= ID_STEM + HOUR_SEGMENT + "/";
	public static final String MINUTE_ID_STEM 		= ID_STEM + MINUTE_SEGMENT + "/";
	public static final String SECOND_ID_STEM		= ID_STEM + SECOND_SEGMENT + "/";
	public static final String INSTANT_ID_STEM		= ID_STEM + INSTANT_SEGMENT + "/";
	public static final String INTERVAL_ID_STEM		= ID_STEM + INTERVAL_SEGMENT + "/";
	
	public static final String YEAR_DOC_STEM 		= DOC_STEM + YEAR_SEGMENT +"/";
	public static final String HALF_DOC_STEM 		= DOC_STEM + HALF_SEGMENT + "/";
	public static final String QUARTER_DOC_STEM 	= DOC_STEM + QUARTER_SEGMENT +"/";
	public static final String MONTH_DOC_STEM 		= DOC_STEM + MONTH_SEGMENT + "/";
	public static final String WEEK_DOC_STEM 		= DOC_STEM + WEEK_SEGMENT + "/";
	public static final String DAY_DOC_STEM 		= DOC_STEM + DAY_SEGMENT + "/";
	public static final String HOUR_DOC_STEM 		= DOC_STEM + HOUR_SEGMENT + "/";
	public static final String MINUTE_DOC_STEM 		= DOC_STEM + MINUTE_SEGMENT + "/";
	public static final String SECOND_DOC_STEM		= DOC_STEM + SECOND_SEGMENT + "/";
	public static final String INSTANT_DOC_STEM		= DOC_STEM + INSTANT_SEGMENT + "/";
	public static final String INTERVAL_DOC_STEM	= DOC_STEM + INTERVAL_SEGMENT + "/";

	public static final String YEAR_SET_RELURI 		= SET_STEM + YEAR_SEGMENT;
	public static final String HALF_SET_RELURI 		= SET_STEM + HALF_SEGMENT;
	public static final String QUARTER_SET_RELURI 	= SET_STEM + QUARTER_SEGMENT;
	public static final String MONTH_SET_RELURI 	= SET_STEM + MONTH_SEGMENT;
	public static final String WEEK_SET_RELURI 		= SET_STEM + WEEK_SEGMENT;
	public static final String DAY_SET_RELURI 		= SET_STEM + DAY_SEGMENT;
	public static final String HOUR_SET_RELURI 		= SET_STEM + HOUR_SEGMENT;
	public static final String MINUTE_SET_RELURI 	= SET_STEM + MINUTE_SEGMENT;
	public static final String SECOND_SET_RELURI	= SET_STEM + SECOND_SEGMENT;
	public static final String INSTANT_SET_RELURI	= SET_STEM + GregorianCalURITemplate.INSTANT_SEGMENT;
	public static final String INTERVAL_SET_RELURI	= SET_STEM + GregorianCalURITemplate.INTERVAL_SEGMENT;
	
	public static final String HALF_PREFIX			= "-H";
	public static final String QUARTER_PREFIX		= "-Q";
	public static final String MONTH_PREFIX			= "-";
	public static final String WEEK_PREFIX			= "-W";
	public static final String DAY_PREFIX			= "-";
	public static final String HOUR_PREFIX			= "T";
	public static final String MINUTE_PREFIX		= ":";
	public static final String SECOND_PREFIX		= ":";
	public static final String DURATION_PREFIX		= "/";
	
	public static final String YEAR_PATTERN 		=                                     "{" + YEAR_TOKEN    + ":([0-9]{4})|([1-9][0-9]{3,})}";
	public static final String HALF_PATTERN 		= YEAR_PATTERN 	   + HALF_PREFIX    + "{" + HALF_TOKEN	  + ":[1-2]}";		// H1 or H2
	public static final String QUARTER_PATTERN		= YEAR_PATTERN 	   + QUARTER_PREFIX + "{" + QUARTER_TOKEN + ":[1-4]}";     // Q1, Q2, Q3 or Q4 
	public static final String MONTH_PATTERN 		= YEAR_PATTERN 	   + MONTH_PREFIX   + "{" + MONTH_TOKEN	  + ":[0-1][0-9]}"; // Months 01 or 12 (well 19 but catch later)
	public static final String WOY_PATTERN 			= YEAR_PATTERN 	   + WEEK_PREFIX    + "{" + WEEK_TOKEN	  + ":[0-5][0-9]}"; // Week of year 01 to 53 (well 59 but catch later)
	public static final String WOM_PATTERN 			= YEAR_PATTERN 	   + MONTH_PREFIX   + "{" + MONTH_TOKEN   + ":[0-3][0-9]}" + WEEK_PREFIX    + "{" + WEEK_TOKEN	  + ":[1-5]}"; // Week of year 01 to 53 (well 59 but catch later)
	public static final String DAY_PATTERN 			= MONTH_PATTERN    + DAY_PREFIX     + "{" + DAY_TOKEN     + ":[0-3][0-9]}"; // Day of month 01 to 31 (well 39 but catch later)
	public static final String HOUR_PATTERN 		= DAY_PATTERN 	   + HOUR_PREFIX    + "{" + HOUR_TOKEN    + ":[0-2][0-9]}"; // Hour of day 
	public static final String MINUTE_PATTERN 		= HOUR_PATTERN 	   + MINUTE_PREFIX  + "{" + MINUTE_TOKEN  + ":[0-5][0-9]}"; // Minute of hour
	public static final String SECOND_PATTERN 		= MINUTE_PATTERN   + SECOND_PREFIX  + "{" + SECOND_TOKEN  + ":[0-5][0-9]}"; // Second of minute
	public static final String INSTANT_PATTERN 		= SECOND_PATTERN;
	public static final String INTERVAL_PATTERN  	= SECOND_PATTERN    + "/"			+ "{" + DURATION_TOKEN + ":"+ DURATION_REGEX + "}";
	
	public static final String SUMMARY_PATTERN_PLAIN = "-[^/]+/.+";

	public static final String YEAR_PATTERN_PLAIN 	= "([0-9]{4})|([1-9][0-9]{3,})*";
	public static final String HALF_PATTERN_PLAIN 	= YEAR_PATTERN_PLAIN+HALF_PREFIX+"[1-2]";
	public static final String QUARTER_PATTERN_PLAIN = YEAR_PATTERN_PLAIN+QUARTER_PREFIX+"[1-4]";
	public static final String MONTH_PATTERN_PLAIN 	= YEAR_PATTERN_PLAIN+MONTH_PREFIX+"[0-1][0-9]";
	public static final String WEEK_PATTERN_PLAIN 	= YEAR_PATTERN_PLAIN+WEEK_PREFIX+"[0-5][0-9]";
	public static final String DAY_PATTERN_PLAIN  	= MONTH_PATTERN_PLAIN+DAY_PREFIX+"[0-3][0-9]";
	public static final String HOUR_PATTERN_PLAIN 	= DAY_PATTERN_PLAIN+HOUR_PREFIX+"[0-2][0-9]";
	public static final String MIN_PATTERN_PLAIN  	= HOUR_PATTERN_PLAIN+MINUTE_PREFIX+"[0-5][0-9]";
	public static final String SEC_PATTERN_PLAIN  	= MIN_PATTERN_PLAIN+SECOND_PREFIX+"[0-5][0-9]";
	public static final String INSTANT_PATTERN_PLAIN = SEC_PATTERN_PLAIN;
	public static final String INTERVAL_PATTERN_PLAIN = INSTANT_PATTERN_PLAIN+DURATION_PREFIX+DURATION_REGEX;

	public final static String PATH_REGEX =
		"("+CALENDAR_SET+")|"+
//		"("+INSTANT_SEGMENT+")|"+
//		"("+INTERVAL_SEGMENT+")|" +
		"("+YEAR_SEGMENT+")|"+
		"("+HALF_SEGMENT+")|"+
		"("+QUARTER_SEGMENT+")|"+
		"("+MONTH_SEGMENT+")|"+
		"("+DAY_SEGMENT+")|"+
		"("+WEEK_SEGMENT+")|"+
		"("+HOUR_SEGMENT+")|"+
		"("+MINUTE_SEGMENT+")|"+
		"("+SECOND_SEGMENT+")";

}