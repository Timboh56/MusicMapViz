package it.polito.lastfm.query;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.HashMap;

import net.roarsoftware.lastfm.Album;
import net.roarsoftware.lastfm.Artist;
import net.roarsoftware.lastfm.Event;
import net.roarsoftware.lastfm.Track;

import com.mindprod.entities.StripEntities;

public class OutputManager {

	private boolean isStdout;
	private String outFileName;
	private TypeManager typeMan;
	private SymbolTable symTable;

	public OutputManager(){

		this.isStdout = true;
		this.outFileName = new String();
		this.typeMan = new TypeManager();
		this.symTable = SymbolTable.getInstance();

	}

	public void printCommandList(){
		//		System.out.println("Available functions:");
		//		System.out.println("");
		//		System.out.println("?\t\tprint this message");
		//		System.out.println("show\t\tshow all the variables in the workspace");
		//		System.out.println("<id>\t\tshow the content of the <id> variable"	);
		//		System.out.println("output [filename|stdout]\t\tredirect the output");
		//		System.out.println("\nAvailable commands (optional arguments are marked with *):"); 
		//		System.out.println("info [artists][conditions*][forceoutput*]\t\tretrieve infos about the artists");
		//		System.out.println("topalbums [artists][conditions*][forceoutput*]\t\tretrieve the top albums of the artists");
		//		System.out.println("toptracks [nations][conditions*][forceoutput*]\t\tretrieve the latest top tracks of the given nations");
		//		System.out.println("events [artists][conditions*][forceoutput*]\t\tretrieve the latest events of the given artists");
		//		System.out.println("userevents [users][conditions*][forceoutput*]\t\tretrieve the events that the users are attending");
		//		System.out.println("where conditions are:");
		//		System.out.println("playcount, listeners (info)");
		//		System.out.println("playcount (topalbums)");
		//		System.out.println("artist, playcount (toptracks)");
		//		System.out.println("country, attendance, title (events)");
		//		System.out.println("artist, country, attendance, title (events)");
		// (...)
	}

	private void toStdout(String text){
		System.out.print(text);
	}

	private void toFile(String text, String filename) throws IOException{
		FileOutputStream file;

		file = new FileOutputStream(typeMan.string2StringType(filename), true);
		PrintStream output = new PrintStream(file);
		output.print(text);
		output.close();
		file.close();

	}

	private void out(String string, String forceout) throws IOException{

		/* empty string -> stdout
		 * null string -> default
		 * non-empty string -> switch to forceout
		 *  
		 *  */
		if (forceout == null){
			/* default settings */
			if (this.isStdout)
				this.toStdout(string);
			else
				this.toFile(string, outFileName);
		}
		else if (forceout.equals("")){
			/* force stdout */
			this.toStdout(string);
		}
		else{
			/* force to a 'forceout' */
			this.toFile(string, forceout);
		}
	}

	public void printArtistsInfo(Collection<Artist> response, String forceout) throws IOException{

		if (response == null)
			return;

		StringBuffer sb = new StringBuffer();
		for (Artist artist : response){
			sb.append(artist.getName().toUpperCase() +  " (" + artist.getListeners() +  " listeners, " + artist.getPlaycount() + " plays)\n");
			sb.append(StripEntities.stripHTMLEntities(artist.getWikiText().replaceAll("\\<.*?>","").replaceAll("\\[.*?]", "'"), ' ') + "\n\n");
		}

		this.out(sb.toString(), forceout);

	}

	public void printAlbumsInfo(HashMap<String, Collection<Album>> response, String forceout) throws IOException{

		if (response == null)
			return;

		StringBuffer sb = new StringBuffer();

		for (String artistName : response.keySet()){
			sb.append(artistName.toUpperCase() + "'s top albums:\n");
			for (Album album : response.get(artistName))
				sb.append(album.getName() + " (" + album.getPlaycount() + " plays)\n");
			sb.append("\n");
		}

		this.out(sb.toString(), forceout);

	}

	public void printEventsInfo(HashMap<String, Collection<Event>> response, String forceout) throws IOException{

		if (response == null)
			return;

		StringBuffer sb = new StringBuffer();

		for (String artistName : response.keySet()){
			sb.append(artistName.toUpperCase() + "'s events:\n");
			for (Event event : response.get(artistName))
				sb.append(event.getTitle() + ", " + event.getVenue().getCity() + " ("+ event.getVenue().getCountry() + ") on " + event.getStartDate() + ", attendance: " + event.getAttendance() + "\n");
			sb.append("\n");
		}


		this.out(sb.toString(), forceout);

	}

	public void printUsereventsInfo(HashMap<String, Collection<Event>> response, String forceout) throws IOException{

		if (response == null)
			return;

		StringBuffer sb = new StringBuffer();

		for (String userName : response.keySet()){
			sb.append("User " + userName.toUpperCase() + " is attending:\n");
			for (Event event : response.get(userName))
				sb.append(event.getTitle() + " " + event.getArtists() + " in " + event.getVenue().getCity() + " ("+ event.getVenue().getCountry() + ") on " + event.getStartDate() + ", attendance: " + event.getAttendance() + "\n");
			sb.append("\n");
		}

		this.out(sb.toString(), forceout);

	}

	public void printTracksInfo(HashMap<String, Collection<Track>> response, String forceout) throws IOException{

		if (response == null)
			return;

		StringBuffer sb = new StringBuffer();

		for (String nation : response.keySet()){
			sb.append("Top tracks in " + nation.toUpperCase() + ":\n");
			for (Track track : response.get(nation))
				sb.append(track.getArtist() + " - " + track.getName() + ", " + track.getPlaycount() + " plays\n");
			sb.append("\n");
		}

		this.out(sb.toString(), forceout);

	}

	public void switchToFile(Object id) throws SymbolTableException{

		Object resval = symTable.getSymbolObject((String)id.toString());
		if (resval == null)
			throw new SymbolTableException("Error: symbol " + id.toString() + " does not exist"); 
		if (resval instanceof String == false)
			throw new SymbolTableException("Error: symbol " + id.toString() + " does not contain a string");

		this.switchToFile((String)resval);
	}

	public void switchToFile(String output){

		this.isStdout = false;
		this.outFileName = typeMan.string2StringType(output);		

	}

	public void switchToStdout(){

		this.isStdout = true;

	}

}
