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

package com.epimorphics.govData.URISets.intervalServer.ukgovcal;

import com.epimorphics.govData.URISets.intervalServer.URITemplate;
import com.epimorphics.govData.URISets.intervalServer.gregorian.GregorianCalURITemplate;

public class UkGovCalURITemplate extends URITemplate {

	public static final String CALENDAR_STEM 		= 	"government";
	public static final String CALENDAR_SET			=	"government-calendar";
	public static final String CALENDAR_NAME			= "Modern HMG";
	
	public static final String CALENDAR_SET_LABEL 	= CALENDAR_NAME+" business calendar aligned intervals."; 
	public static final String YEAR_SET_LABEL 		= CALENDAR_NAME+" business calendar aligned one year intervals."; 
	public static final String HALF_SET_LABEL 		= CALENDAR_NAME+" business calendar aligned one half year intervals."; 
	public static final String QUARTER_SET_LABEL 	= CALENDAR_NAME+" business calendar aligned one quarter year intervals."; 
	public static final String WEEK_SET_LABEL 		= CALENDAR_NAME+" business calendar aligned ISO8601 numbered week long intervals."; 
	public static final String INTERVAL_SET_LABEL 	= GregorianCalURITemplate.INTERVAL_SET_LABEL; 
	public static final String INSTANT_SET_LABEL 	= GregorianCalURITemplate.INSTANT_SET_LABEL; 
	
	public static final String YEAR_TOKEN 			= "year";
	public static final String YEAR2_TOKEN 			= "year2";
	public static final String HALF_TOKEN 			= "half";
	public static final String QUARTER_TOKEN 		= "quarter";
	public static final String WEEK_TOKEN 			= "week";
	public static final String EXT_TOKEN 			= "ext";
	public static final String EXT2_TOKEN 			= "ext2";
	public static final String DURATION_TOKEN		= "duration";

	public static final String ID_STEM 				= "id/";
	public static final String DOC_STEM 			= "doc/";
	public static final String SET_STEM				= "id/";
//	public static final String SET_DOC_STEM			= "doc/set/";
	
	
	public static final String CALENDAR_SEGMENT 	= CALENDAR_STEM;
	public static final String YEAR_SEGMENT 		= CALENDAR_STEM + "-year";
	public static final String HALF_SEGMENT 		= CALENDAR_STEM + "-half";
	public static final String QUARTER_SEGMENT 		= CALENDAR_STEM + "-quarter";
	public static final String WEEK_SEGMENT 		= CALENDAR_STEM + "-week";
	public static final String INSTANT_SEGMENT		= CALENDAR_STEM + "-instant";
	public static final String INTERVAL_SEGMENT		= CALENDAR_STEM + "-interval";
	
	public static final String YEAR_ID_STEM 		= ID_STEM + YEAR_SEGMENT +"/";
	public static final String HALF_ID_STEM 		= ID_STEM + HALF_SEGMENT + "/";
	public static final String QUARTER_ID_STEM 		= ID_STEM + QUARTER_SEGMENT +"/";
	public static final String WEEK_ID_STEM 		= ID_STEM + WEEK_SEGMENT + "/";
	public static final String INSTANT_ID_STEM		= ID_STEM + INSTANT_SEGMENT + "/";
	public static final String INTERVAL_ID_STEM		= ID_STEM + INTERVAL_SEGMENT + "/";
	
	public static final String YEAR_DOC_STEM 		= DOC_STEM + YEAR_SEGMENT +"/";
	public static final String HALF_DOC_STEM 		= DOC_STEM + HALF_SEGMENT + "/";
	public static final String QUARTER_DOC_STEM 	= DOC_STEM + QUARTER_SEGMENT +"/";
	public static final String WEEK_DOC_STEM 		= DOC_STEM + WEEK_SEGMENT + "/";
	public static final String INSTANT_DOC_STEM		= DOC_STEM + INSTANT_SEGMENT + "/";
	public static final String INTERVAL_DOC_STEM	= DOC_STEM + INTERVAL_SEGMENT + "/";

	public static final String YEAR_SET_RELURI 		= SET_STEM + YEAR_SEGMENT;
	public static final String HALF_SET_RELURI 		= SET_STEM + HALF_SEGMENT;
	public static final String QUARTER_SET_RELURI 	= SET_STEM + QUARTER_SEGMENT;
	public static final String WEEK_SET_RELURI 		= SET_STEM + WEEK_SEGMENT;
	public static final String INSTANT_SET_RELURI	= SET_STEM + GregorianCalURITemplate.INSTANT_SEGMENT;
	public static final String INTERVAL_SET_RELURI	= SET_STEM + GregorianCalURITemplate.INTERVAL_SEGMENT;
	
	public static final String HALF_PREFIX			= "/H";
	public static final String QUARTER_PREFIX		= "/Q";
	public static final String WEEK_PREFIX			= "/W";
	public static final String DURATION_PREFIX		= "/";

	public static final String SINGLE_YEAR_PATTERN 	= "([0-9]{4})|([1-9][0-9]{3,})*";
	
	public static final String YEAR_PATTERN 	= "{" + YEAR_TOKEN + ":" + SINGLE_YEAR_PATTERN + "}-{" + YEAR2_TOKEN + ":" + SINGLE_YEAR_PATTERN+"}";
	public static final String HALF_PATTERN 	= YEAR_PATTERN 	   + HALF_PREFIX    + "{" + HALF_TOKEN	  + ":[1-2]}";		// H1 or H2
	public static final String QUARTER_PATTERN	= YEAR_PATTERN 	   + QUARTER_PREFIX + "{" + QUARTER_TOKEN + ":[1-4]}";     // Q1, Q2, Q3 or Q4 
	public static final String WOY_PATTERN 		= YEAR_PATTERN 	   + WEEK_PREFIX    + "{" + WEEK_TOKEN	  + ":[0-5][0-9]}"; // Week of year 01 to 53 (well 59 but catch later)
	
	public static final String SUMMARY_PATTERN_PLAIN = "-[^/]+/.+";

	public static final String YEAR_PATTERN_PLAIN 	= "(([0-9]{4})|([1-9][0-9]{3,})*)-(([0-9]{4})|([1-9][0-9]{3,})*)";
	public static final String HALF_PATTERN_PLAIN 	= YEAR_PATTERN_PLAIN+HALF_PREFIX+"[1-2]";
	public static final String QUARTER_PATTERN_PLAIN = YEAR_PATTERN_PLAIN+QUARTER_PREFIX+"[1-4]";
	public static final String WEEK_PATTERN_PLAIN 	= YEAR_PATTERN_PLAIN+WEEK_PREFIX+"[0-5][0-9]";

	public final static String PATH_REGEX =
		"("+CALENDAR_SET+")|"+
//		"("+INSTANT_SEGMENT+")|"+
//		"("+INTERVAL_SEGMENT+")|" +
		"("+YEAR_SEGMENT+")|"+
		"("+HALF_SEGMENT+")|"+
		"("+QUARTER_SEGMENT+")|"+
		"("+WEEK_SEGMENT+")";
}