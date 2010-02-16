/******************************************************************
 * File:        BaseURI.java
 * Created by:  skw
 * Created on:  16 Feb 2010
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
package com.epimorphics.govData.URISets.intervalServer;

import java.net.URI;
import java.net.URISyntaxException;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.epimorphics.govData.URISets.intervalServer.govcalendar.Id;
/**
 * @author skw
 *
 */
public class BaseURI {
	static private Logger logger = LoggerFactory.getLogger(Id.class);
	
	private static URI base=null;
	private static boolean visited = false;
	private static BaseURI the_instance = new BaseURI();
	/**
	 * 
	 */
	public static BaseURI getTheInstance(){
		return the_instance;
	}
	
	private BaseURI() {
		if (visited)
			return;

		visited = true;
		String s_uri = null;
		InitialContext ctx;
		try {
			ctx = new InitialContext();
			s_uri = (String) ctx.lookup("java:comp/env/hard-base-uri");
			try {
				base = new URI(s_uri);
				logger.info("Hard Base URI Set to: "+s_uri);
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				logger.warn("Config parameter \"hard-base-uri\" is not a valid URI. Using Servlet root URI as base URI");
			}
		} catch (NamingException e) {
			logger.info("Config parameter \"hard-base-uri\" not set. Using Servlet root URI as base URI");
		}
	}

	/**
	 * @return the base
	 */
	public static URI getBase() {
		return base;
	}
}
