
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;



import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.*;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		try {

			new Main();
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public Main() {
		
		System.out.println("enter first url");
		
		String input1 = getLinkInput();
		
		if(input1.contains(".org/")) {
			System.out.println(input1);
			return;
		}
		
		System.out.println("enter second url");
		
		String input2 = getLinkInput();
		
		if(input2.contains(".org/")) {
			System.out.println(input2);
			return;
		}
		
		
		System.out.println("Loading...");
		search(input1, input2);
		
		
		
	}
	
	//getting rid of irrelevant urls to speed up search
	private ArrayList<String> filterURLs(Elements hrefs) {
		
		ArrayList<String> result = new ArrayList<String>();
		
		for (Element href : hrefs) {
			
			String text = href.attr("href");
			
			//removing links to images and page anchors
			if(text.indexOf("/wiki/") > -1 && text.indexOf(':') == -1 && text.indexOf('#') == -1 && !text.contains("(disambiguation)")) {
				result.add(href.attr("href"));
			}
			
			
		}
		return result;
	}
	
	private String getLinkInput() {
		
		Scanner e = new Scanner(System.in);
		
		String user = e.next();
		String search = "";
		for(int i = 0; i < user.length(); i++) {
			if(user.charAt(i) == ' ')
				search += "+";
			search += user.charAt(i);
		}
		
	
		String input = "https://en.wikipedia.org/w/index.php?search=" + search + "&title=Special%3ASearch&go=Go\\";
		
		
		
		try {
			
			Document docum = Jsoup.connect(input).get();
			
			input = docum.location();
			
			//if wikipedia recognized the url, no more formatting or editing
			
			Elements page = docum.getElementById("bodyContent").getElementsByClass("mw-parser-output");
			
			if(page.size() > 0) {
				
				
				//input has too many possibilities
				if(page.first().select("p").first().text().contains("may refer to")) {
					System.out.println("Select one of the options:");
					return "Vague input. Please restart the program and be more specific";
				}
				
			}
			
			if(docum.location().contains("/wiki/") /*&& docum.getElementById("siteSub") == null*/) {
				input = docum.location().substring(24, docum.location().length());
				System.out.println(input);
			}else {
				
				//if "did you mean" exists, use it
				
				Elements didMean = docum.getElementById("mw-content-text").getElementsByClass("searchdidyoumean");
				
				if(didMean.size() > 0) {
					
					Element[] options;
					
					//display options
					docum = Jsoup.connect("https://en.wikipedia.org" + didMean.select("a").attr("href")).get();
					Element table = docum.getElementById("mw-content-text").getElementsByClass("mw-search-results").first();
					int n = 0;
					
					options = new Element[4];
					
					
					if(docum.getElementsByClass("mw-search-nonefound").size() > 0) {
						return "No results found. Restart program";
					}
					
					System.out.println("Select one of the options:");
					
					//display only 4 options
					for (Element element : table.select("li") ) {
						if(n == 4)
							break;
						//print out options
						options[n] = element.select("a").first();
						System.out.println("" + (n + 1) + ". " + options[n].text());
						n++;
					}
					
					
					System.out.println("Your choice: (1-" + (n) + ")");
					
					int pick = e.nextInt() - 1;
				
					//check if pick is out of bounds
					if(pick <= options.length && pick >= 0) {
						input = options[pick].attr("href");
					}else {
						return "Invalid";
					}
			}else {
					//go straight to printing available options
					
					Element table = docum.getElementById("mw-content-text").getElementsByClass("mw-search-results").first();
					int n = 0;
					
					Element[] options = new Element[4];
					System.out.println("Select one of the options:");
					
					
					if(docum.getElementsByClass("mw-search-nonefound").size() > 0) {
						return "No results found. Restart program";
					}
					
					//display only 4 options
					for (Element element : table.select("li")) {
						if(n == 4)
							break;
						//print out options
						options[n] = element.select("a").first();
						System.out.println("" + (n + 1) + ". " + options[n].text());
						n++;
					}
					
					System.out.println("Your choice: (1-" + (n) + ")");
					
					int pick = e.nextInt() - 1;
				
					//check if pick is out of bounds
					if(pick <= n && pick >= 0) {
						input = options[pick].attr("href");
					}else 
						return "Invalid";
				}
				 
				
			}
			
			
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		return input;
		
	}
	
	//helper function to set up lists and starting node
	private void search(String start, String end) {
		
		ArrayList<String> to_visit = new ArrayList<String>();
		ArrayList<String> visited = new ArrayList<String>();
		ArrayList<String> path = new ArrayList<String>();
		
		to_visit.add(start);
		
		search_recursive(to_visit, visited, path, start, end, 1);
		
		//print out path when the search is finished
		System.out.println("----Path----:");
		for (String string : path) {
			System.out.println(string);
		} 
		
		
	}
	
	
	private void search_recursive(ArrayList<String> to_visit, ArrayList<String> visited, ArrayList<String> path, String input1, String input2, int count) {
		try {
			
			//for printing once we are done
			path.add(input1);
			
			Document doc = Jsoup.connect("https://en.wikipedia.org" + input1).get();
			
			//get the links on the page
			ArrayList<String> hrefs = filterURLs(doc.getElementById("bodyContent").select("a"));
				
				for (String href : hrefs) {
					
					//uncomment line for testing   v 
					//System.out.println(href.toLowerCase());
					if(href.toLowerCase().contains(input2.toLowerCase().substring(6))) {
						System.out.println("found");
						path.add(href);
						return;
					}
					
					if(!visited.contains(href) && !to_visit.contains(href)) {
						to_visit.add(href);
					}
					
				}
				
			visited.add(input1);
			to_visit.remove(0);
				
			search_recursive(to_visit, visited, path, to_visit.get(0), input2, count+1);
				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
