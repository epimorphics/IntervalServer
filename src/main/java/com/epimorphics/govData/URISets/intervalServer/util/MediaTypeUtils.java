/******************************************************************
 * File:        MediaTypeUtils.java
 * Created by:  skw
 * Created on:  14 Feb 2010
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
package com.epimorphics.govData.URISets.intervalServer.util;

import java.util.List;

import javax.ws.rs.core.MediaType;

/**
 * @author skw
 *
 */
public class MediaTypeUtils {
		
	private final static MediaType rdfMediaTypes[] = { new MediaType("application", "rdf+xml"),
										 new MediaType("text","turtle"),
										 new MediaType("application","json"),
										 new MediaType("application","x-turtle"),
										 new MediaType("text","n3"),
										 new MediaType("text","plain")};
	
	private static String languages[] =  {"RDF/XML-ABBREV","N3", "JSON", "N3", "N3", "N-TRIPLE"};
	
	private static String exts[] = {"rdf", "ttl", "json", "ttl", "n3", "nt" };
	
	/**
	 * @param mt
	 * @return
	 */
	public static String getLangOfMediaType(MediaType mt) {
		int i = indexOfMt(mt);	
		return (i>=0) ? languages[i] : languages[0];
	}
	
	/**
	 * @param mt
	 * @return
	 */
	public static String getExtOfMediaType(MediaType mt) {
		int i = indexOfMt(mt);
		return (i>=0) ? exts[i] : exts[0];
	}
	
	/**
	 * @param acceptableMediaTypes
	 * @return
	 */
	public static MediaType pickMediaType(List<MediaType> acceptableMediaTypes) {
		MediaType res = rdfMediaTypes[0]; //Default to RDF/XML
		int i = -1;
		for(MediaType mt : acceptableMediaTypes) {
			int j = indexOfMt(mt);
			if(j<0)
				continue;
			
			if( i==-1  ||
			   (j>=0 && j<i)) {
				res = mt;
				i = j;
			}
		}
		return res;
	}
	
	public static String pickLang(List<MediaType> acceptableMediaTypes) {
		MediaType best = pickMediaType(acceptableMediaTypes);
		return getLangOfMediaType(best);
	}
	
	private static  int indexOfMt(MediaType mt) {
		String type = mt.getType();
		String subType = mt .getSubtype();
		
		for(int i=0; i<rdfMediaTypes.length; i++) {
			MediaType other = rdfMediaTypes[i];
			if(other.getType().equals(type) && 
			   other.getSubtype().equals(subType))  
				return i;
		}
		return -1;
	}
	
	private static  int indexOfExt(String ext) {
		for(int i=0; i<exts.length; i++) {
			if(exts[i].equals(ext)){
				return i;
			}
		}
		return -1;
	}

	/**
	 * @param ext
	 * @return
	 */
	public static MediaType extToMediaType(String ext) {
		int i = indexOfExt(ext);
		
		return i>=0 ? rdfMediaTypes[i] : rdfMediaTypes[0]; 
	}

}
