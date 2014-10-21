/**
 * For Scraping HTML data from a Notice of Default search on http://www.utahcounty.gov/LandRecords
 * 
 * ------------------------START
 * http://www.utahcounty.gov/LandRecords/DocKoiForm.asp
 * Enter Kind of Instrument (KOI) "ND" //Notice of Default
 * Beginning Recording Date "<TODAYSDATE>" //Search only for today
 * http://www.utahcounty.gov/LandRecords/DocKoi.asp?avKoi=ND&avEntryDate=10%2F02%2F2014&Submit=Search //autogenerated URL
 * html>body>table>tbody>tr>td>table>tbody>tr>td>a //sequence of address of info
 * <a href="document.asp?avEntry=70625&amp;avYear=2014">70625</a>
 * Go to Address
 * http://www.utahcounty.gov/LandRecords/document.asp?avEntry=70625&avYear=2014
 * html>body>table>tbody>tr>td>table>tbody>tr>td>a
 * <a href="PartyName.asp?avname=KANTARIS, DWAYNE">KANTARIS, DWAYNE</a> //STORE NAME
 * Use above Name for
 * http://www.utahcountyonline.org/LandRecords/NameSearchForm.asp
 * Enter Name to search for: "NAME"
 * http://www.utahcountyonline.org/LandRecords/NameSearch.asp?av_name=KANTARIS%2C+DWAYNE&av_valid=%25&Submit=Search
 * Go to Serial# next to Years Valid line with Green text and ellipses 
 * OWNER NAME		SERIAL NUMBER	TAX D	YEARS	PROPERTY ADDRESS //APPEARS ON PAGE
 * KANTARIS, DWAYNE	49:352:0010	 	(150)	2004...	324 E 1000 NORTH - SPANISH FORK //APPEARS ON PAGE
 * html>body>table>tbody>tr>td>table>tbody>tr>td>a
 * <a href="property.asp?av_serial=493520010003">49:352:0010</a> //STORE SERIAL #
 * http://www.utahcounty.gov/LandRecords/Property.asp?av_serial=493520010003 //URL OF SERIAL # FOR PAGE
 * Property Address:  324 E 1000 NORTH - SPANISH FORK //APPEARS ON PAGE
 * Mailing Address: 522 N 600 E OREM, UT 84097-4208 //APPEARS ON PAGE
 * html>body>table>tbody>tr>td>table>tbody>tr>td>table>tbody>tr>td>strong
 * <strong>Property Address:</strong>
 * html>body>table>tbody>tr>td>table>tbody>tr>td>table>tbody>tr>td>(text)
 * &nbsp; 324 E 1000 NORTH - SPANISH FORK //STORE AS PROPERTY ADDRESS
 * html>body>table>tbody>tr>td>table>tbody>tr>td>table>tbody>tr>td>strong
 * <strong>Mailing Address:</strong>
 * html>body>table>tbody>tr>td>table>tbody>tr>td>table>tbody>tr>td>(text)
 *  " 522 N 600 E  OREM, UT 84097-4208" //STORE AS MAILING ADDRESS
 * html>body>table>tbody>tr>td>table>tbody>tr>td>select#jumpMenu.forcestyle>option
 * <option value="Abstract.asp?av_serial=49:352:0010">Abstract</option>
 * http://www.utahcounty.gov/LandRecords/Abstract.asp?av_serial=49:352:0010 //STORE ALL HTML
 * html>body>table>tbody>tr>td>em.italic>table>tbody>tr
 * Need any number $ on far right table CONSIDERATION, SATISFACTION, TIE ENTRY NO
 * Get left side for values other than 0, GRANTOR, GRANTEE, COMMENTS //STORE ALL GRANTOR/GRANTEE INFO
 * ------------------------END
 * 
 * http://jsoup.org/apidocs/ //Documentation for Java HTML parser API
 * https://github.com/PhotonPhighter/NODScraper //Location of SOURCE CODE
 */

package nodscraper;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @author Ali M (PhotonPhighter/Admiral Nero the XCIV)
 */

public class Main{
	/**
	 * ARGGIMOOOLIDOO NOTHING TO SEE HERE
	 */
	public Main(){
		//Auto-generated constructor stub, exists for reasons and stuff, not really
	}//end of Main constructor
	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException{
		/** Experimental **/
		String MM = "10";	//month you want to start search at
		String DD = "01";	//day you want to start search at
		String YYYY = "2014";	//year you want to start search at
		String stringURLofStartSearchPage = "http://www.utahcounty.gov/LandRecords/DocKoi.asp?avKoi=ND&avEntryDate="+MM+"%2F"+DD+"%2F"+YYYY+"&submit=Search";	//the URL of the page including date
		print("OOO-->Fetching current ND records from Date %s/%s/%s\n site: <%s>...", MM, DD, YYYY, stringURLofStartSearchPage);	//print current action
		try{
			Document documentUponCompletedSearchPageData = Jsoup.connect(stringURLofStartSearchPage).get();	//connect to URL
			Elements linksFromDocumentUponCompletedSearchPageData = documentUponCompletedSearchPageData.select("a[href]");	//get all the links in the current document and put them in a list
			linksFromDocumentUponCompletedSearchPageData.remove(0);	//remove first link, as it is superfluous
			for (int i=0; i<4; i++){	//iterate to remove the bottom 4 links, as they are superfluous
				linksFromDocumentUponCompletedSearchPageData.remove(linksFromDocumentUponCompletedSearchPageData.size()-1);	//just said it, right there above
			}//end of for loop
			print("\n---->Number of total records from date forward: " + linksFromDocumentUponCompletedSearchPageData.size());	//print how many links are left in the list from completed search
			for (Element link : linksFromDocumentUponCompletedSearchPageData){	//for every link element in the list of links do what's below
				String stringFileEntryNumber = trim(link.text(), 35);	//collect the entry number for each case
				String stringURLOfFileEntryNumberLinkOnPage = link.attr("abs:href");	//collect the URL for connecting to particular entry number's page
				print("++++++ URL of file entry number page: <%s>", stringURLOfFileEntryNumberLinkOnPage);	//ignore, for debugging
				print(" -- File Entry Number: (%s)", stringFileEntryNumber);	//print out the gathered entry number
				Document documentEntryNumberPageData = Jsoup.connect(stringURLOfFileEntryNumberLinkOnPage).get();	//connect to the URL for the entry number's page, to get the name
				Elements linksFromEntryNumberPageData = documentEntryNumberPageData.select("a[href]");	//get all the links listed on entry number page
				print(" -- Links on file entry page: " + linksFromEntryNumberPageData.size());	//ignore, for debugging
				linksFromEntryNumberPageData.remove(0);	//remove first link as it is superfluous
				linksFromEntryNumberPageData.remove(0);	//remove second link as it is superfluous
				for (int i=linksFromEntryNumberPageData.size(); i>1; i--){	//iterate through the links and remove all the superfluous ones left at bottom of list
					linksFromEntryNumberPageData.remove(linksFromEntryNumberPageData.size()-1);	//yep
				}//end of nested for loop
				String stringURLOfGranteeNamePage = linksFromEntryNumberPageData.attr("abs:href");	//get the URL containing the grantee's name
				String stringGranteeName = trim(linksFromEntryNumberPageData.text(), 35);	//get the actual name out of the link
				for (Element link2 : linksFromEntryNumberPageData){	//for all the links left, do below
					stringURLOfGranteeNamePage = link2.attr("abs:href");	//collect URL containing grantee name
					print(" -- URL of grantee name page: <%s>", stringURLOfGranteeNamePage);	//ignore, for debugging
					print(" -- Grantee Name: (%s)", stringGranteeName);	//print the Grantee's name, going to use it for search later
					String stringURLOfNameSearchPage = "http://www.utahcounty.gov/LandRecords/NameSearch.asp?av_name="+stringGranteeName+"&av_valid=...&Submit=Search";	//URL to use in name search, will need to fix this...
					Document documentCompletedNameSearchResultsPageData = Jsoup.connect(stringURLOfNameSearchPage).get();	//connect to the URL for specific name search
					Elements linksFromCompletedNameSearchResultsPageData = documentCompletedNameSearchResultsPageData.select("a[href]");	//collect all links on the specified URL
					linksFromCompletedNameSearchResultsPageData.remove(0);	//remove first result, as it is superfluous
					for (int i=linksFromCompletedNameSearchResultsPageData.size(); i>1; i--){	//remove superfluous links on page from list
						linksFromCompletedNameSearchResultsPageData.remove(linksFromCompletedNameSearchResultsPageData.size()-1);
					}//end of nested for loop
					for (Element link3 : linksFromCompletedNameSearchResultsPageData){	//for all the links left, do below
						String stringURLOfSerialNumberPage = link3.attr("abs:href");	//collect serial number URL
						String stringSerialNumber = trim(link3.text(), 35);	//collect serial number
						print(" -- URL of name search page: <%s>", stringURLOfNameSearchPage);	//ignore, for debugging
						print(" -- Links on completed name search page: " + linksFromCompletedNameSearchResultsPageData.size());	//ignore, for debugging
						print(" -- URL of serial number page for entry: <%s>", stringURLOfSerialNumberPage);	//ignore, for debugging
						print(" -- Serial Number: (%s)", stringSerialNumber);	//ignore, for debugging
						Document documentSerialNumberPageData = Jsoup.connect(stringURLOfSerialNumberPage).get();	//connect to serial number page
						//Figure out how to navigate CSS with Jsoup so I can collect the addresses listed in the tables on the last visited page
					}//end of nested for loop
				}//end of nested for loop
			}//end of for loop
		}//end of try statement
		catch (IOException e){
			e.printStackTrace();	//print stack trace if you fuck something up
		}//end of catch statement
	}//end of Main method
	
	private static void print(String msg, Object... args){
		System.out.println(String.format(msg,  args));	//useful method for printing to console for debugging
	}//end of print method
	
	private static String trim(String s, int width){
		if (s.length() > width)
			return s.substring(0, width-1)+".";
		else
			return s;	//useful method for trimming Strings for links list and such
	}//end of trim method
}//end of Main class