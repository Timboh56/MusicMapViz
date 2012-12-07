package it.polito.lastfm.query;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import net.roarsoftware.lastfm.Album;
import net.roarsoftware.lastfm.Artist;
import net.roarsoftware.lastfm.Event;
import net.roarsoftware.lastfm.Geo;
import net.roarsoftware.lastfm.Track;
import net.roarsoftware.lastfm.User;

public class QueryEngine {

	private String apiKey;
	private TypeManager typeMan;
	private SymbolTable symTable;
	private List<String> inputList;

	public QueryEngine() {
		apiKey = "8a40b5d1cc78a25b30f8983b4117a940";
		this.typeMan = new TypeManager();
		symTable = SymbolTable.getInstance();
		inputList = new LinkedList<String>();
	}


	private void reportConnectionError(){
		System.err.println("Service unavailable. Are you connected to the Internet?");
	}

	public void setInputList(List<String> list){
		inputList.clear();
		if (list == null)
			return;
		for (String string : list){
			if (string == null)
				return;
		}
		for (String string : list){
			inputList.add(typeMan.string2StringType(string));
		}
	}

	public HashMap<String, Collection<Event>> eventAND(HashMap<String, Collection<Event>>c1, HashMap<String, Collection<Event>>c2){

		if (c1 == null || c2 == null)
			return null;

		HashMap<String, Collection<Event>> result = new HashMap<String, Collection<Event>>();

		for (String key : c1.keySet()){
			Collection<Event> eventsOk = new LinkedList<Event>();
			for (Event outer : c1.get(key)){
				for (Event inner : c2.get(key))
					if (outer.getId() == inner.getId()){ 
						eventsOk.add(outer);
						continue;
					}
			}
			result.put(key, eventsOk);
		}

		return result;	

	}

	public HashMap<String, Collection<Event>> eventOR(HashMap<String, Collection<Event>>c1, HashMap<String, Collection<Event>>c2){

		if (c1 == null || c2 == null)
			return null;

		HashMap<String, Collection<Event>> result = new HashMap<String, Collection<Event>>();		

		for (String key : c1.keySet()){
			Collection<Event> eventsOk = new LinkedList<Event>();

			for (Event outer : c1.get(key)){
				boolean present = false;		
				for (Event inner : c2.get(key)){
					if (outer.getId() == inner.getId()){	
						present = true;
						break;
					}
				}
				if (present == false)
					eventsOk.add(outer);
			}	
			for (Event a : c2.get(key))
				eventsOk.add(a);
			result.put(key, eventsOk);

		}

		return result;

	}



	public HashMap<String, Collection<Album>> albumAND(HashMap<String, Collection<Album>>c1, HashMap<String, Collection<Album>>c2){

		if (c1 == null || c2 == null)
			return null;

		HashMap<String, Collection<Album>> result = new HashMap<String, Collection<Album>>();

		for (String artistName : c1.keySet()){
			Collection<Album> albumsOk = new LinkedList<Album>();
			for (Album outer : c1.get(artistName)){
				for (Album inner : c2.get(artistName))
					if (outer.getName().equals(inner.getName())){
						albumsOk.add(outer);
						continue;
					}
			}
			result.put(artistName, albumsOk);
		}

		return result;	
	}


	public HashMap<String, Collection<Album>> albumOR(HashMap<String, Collection<Album>>c1, HashMap<String, Collection<Album>>c2){

		if (c1 == null || c2 == null)
			return null;

		HashMap<String, Collection<Album>> result = new HashMap<String, Collection<Album>>();		

		for (String artistName : c1.keySet()){
			Collection<Album> albumsOk = new LinkedList<Album>();

			for (Album outer : c1.get(artistName)){
				boolean present = false;		
				for (Album inner : c2.get(artistName)){
					if (outer.getName().equals(inner.getName())){
						present = true;
						break;
					}
				}
				if (present == false)
					albumsOk.add(outer);
			}	
			for (Album a : c2.get(artistName))
				albumsOk.add(a);
			result.put(artistName, albumsOk);

		}

		return result;

	}

	public Collection<Artist>infoAND(Collection<Artist>c1, Collection<Artist>c2){

		if (c1 == null || c2 == null)
			return null;

		Collection<Artist> result = new HashSet<Artist>();

		for (Artist outer : c1){
			for (Artist inner : c2)
				if (outer.getName().equals(inner.getName())){
					result.add(outer);
					continue;
				}
		}
		return result;

	}
	public Collection<Artist>infoOR(Collection<Artist>c1, Collection<Artist>c2){

		if (c1 == null || c2 == null)
			return null;

		Collection<Artist> result = new HashSet<Artist>();

		for (Artist outer : c1){
			boolean present = false;		
			for (Artist inner : c2){
				if (outer.getName().equals(inner.getName())){
					present = true;
					break;
				}
			}
			if (present == false)
				result.add(outer);
		}

		for (Artist a : c2)
			result.add(a);

		return result;	

	}

	public Collection<Artist> infoByListeners(String relation, String id) throws SymbolTableException{
		Integer intval = symTable.getIntById(id);

		if (intval == null)
			throw new SymbolTableException();

		return this.infoByListeners(relation, intval);
	}

	public Collection<Artist> infoByListeners(String relation, Integer intval)
	{

		Collection<Artist> temp = new HashSet<Artist>();
		try {
			for (String artistName : inputList){
				Artist artist = Artist.getInfo(artistName, apiKey);
				if (relation.equals(">")){
					if (artist.getListeners() <= intval.intValue())
						continue;
				}
				else if(relation.equals("<")){
					if (artist.getListeners() >= intval.intValue())
						continue;
				}
				else if(relation.equals("==")){
					if (artist.getListeners() != intval.intValue())
						continue;
				}
				else if(relation.equals("!=")){
					if (artist.getListeners() == intval.intValue())
						continue;
				}
				else if(relation.equals("<=")){
					if (artist.getListeners() > intval.intValue())
						continue;
				}
				else if(relation.equals(">=")){
					if (artist.getListeners() < intval.intValue())
						continue;
				}

				temp.add(artist);

			}
		}catch (net.roarsoftware.lastfm.CallException e){
			this.reportConnectionError();
		}

		return temp;
	}


	public Collection<Artist> infoByPlaycount(String relation, String id) throws SymbolTableException{
		Integer intval = symTable.getIntById(id);

		if (intval == null)
			throw new SymbolTableException("Error: invalid symbol " + id);

		return this.infoByPlaycount(relation, intval);
	}

	public Collection<Artist> simpleInfoQuery(){
		Collection<Artist> response = new HashSet<Artist>();
		for (String artistName : inputList)
			response.add(Artist.getInfo(artistName, apiKey));
		return response;
	}

	public HashMap<String, Collection<Event>> simpleEventsQuery(){
		HashMap<String, Collection<Event>> response = new HashMap<String, Collection<Event>>();

		try {
			for (String artistName : inputList){
				Collection<Event> events = Artist.getEvents(artistName, apiKey);
				response.put(artistName, events);
			}
		}catch (net.roarsoftware.lastfm.CallException e){
			this.reportConnectionError();
		}

		return response;
	}

	public HashMap<String, Collection<Track>> simpleTopTracksQuery(){
		HashMap<String, Collection<Track>> response = new HashMap<String, Collection<Track>>();

		try {
			for (String nation : inputList){
				Collection<Track> tracks = Geo.getTopTracks(nation, apiKey);
				response.put(nation, tracks);
			}
		}catch (net.roarsoftware.lastfm.CallException e){
			this.reportConnectionError();
		}

		return response;
	}


	public HashMap<String, Collection<Event>> simpleUsereventsQuery(){
		HashMap<String, Collection<Event>> response = new HashMap<String, Collection<Event>>();

		try {
			for (String userName : inputList){
				Collection<Event> events = User.getEvents(userName, apiKey);
				response.put(userName, events);
			}
		}catch (net.roarsoftware.lastfm.CallException e){
			this.reportConnectionError();
		}

		return response;
	}



	public HashMap<String, Collection<Album>> simpleAlbumsQuery(){
		HashMap<String, Collection<Album>> response = new HashMap<String, Collection<Album>>();

		try{
			for (String artistName : inputList){
				Collection<Album> albums = Artist.getTopAlbums(artistName, apiKey);
				response.put(artistName, albums);
			}

		}catch (net.roarsoftware.lastfm.CallException e){
			this.reportConnectionError();
		}
		return response;
	}


	public HashMap<String, Collection<Album>> albumsByPlaycount(String relation, String id) throws SymbolTableException{

		Integer intval = symTable.getIntById(id);
		if (intval == null)
			throw new SymbolTableException("Error: invalid symbol " + id);

		return this.albumsByPlaycount(relation, intval);
	}

	public HashMap<String, Collection<Album>> albumsByPlaycount(String relation, Integer intval)
	{
		HashMap<String, Collection<Album>> result = new HashMap<String, Collection<Album>>();
		try {
			for (String artistName : inputList){
				Collection<Album> albums = Artist.getTopAlbums(artistName, apiKey);

				result.put(artistName, new LinkedList<Album>());

				for (Album album : albums){
					if (relation.equals(">")){
						if (album.getPlaycount() <= intval.intValue())
							continue;
					}
					else if(relation.equals("<")){
						if (album.getPlaycount() >= intval.intValue())
							continue;
					}
					else if(relation.equals("==")){
						if (album.getPlaycount() != intval.intValue())
							continue;
					}
					else if(relation.equals("!=")){
						if (album.getPlaycount() == intval.intValue())
							continue;
					}
					else if(relation.equals("<=")){
						if (album.getPlaycount() > intval.intValue())
							continue;
					}
					else if(relation.equals(">=")){
						if (album.getPlaycount() < intval.intValue())
							continue;
					}

					result.get(artistName).add(album);
				}

			}

		}catch (net.roarsoftware.lastfm.CallException e){
			this.reportConnectionError();
		}
		return result;
	}



	public Collection<Artist> infoByPlaycount(String relation, Integer intval)
	{
		Collection<Artist> temp = new HashSet<Artist>();

		try{


			for (String artistName : inputList){
				Artist artist = Artist.getInfo(artistName, apiKey);
				if (relation.equals(">")){
					if (artist.getPlaycount() <= intval.intValue())
						continue;
				}
				else if(relation.equals("<")){
					if (artist.getPlaycount() >= intval.intValue())
						continue;
				}
				else if(relation.equals("==")){
					if (artist.getPlaycount() != intval.intValue())
						continue;
				}
				else if(relation.equals("!=")){
					if (artist.getPlaycount() == intval.intValue())
						continue;
				}
				else if(relation.equals("<=")){
					if (artist.getPlaycount() > intval.intValue())
						continue;
				}
				else if(relation.equals(">=")){
					if (artist.getPlaycount() < intval.intValue())
						continue;
				}

				temp.add(artist);

			}
		}catch (net.roarsoftware.lastfm.CallException e){
			this.reportConnectionError();
		}
		return temp;
	}

	public HashMap<String, Collection<Event>> usereventsByTitle(String relation,  Object idval) throws IllegalArgumentException, SymbolTableException{

		String id = (String)idval.toString();

		String strval = symTable.getStringById(id);
		if (strval == null)
			throw new SymbolTableException("Error: invalid symbol " + id);

		return this.usereventsByTitle(relation, strval);

	}

	public HashMap<String, Collection<Event>> usereventsByTitle(String relation, String strval) throws IllegalArgumentException {

		if (relation.equals("==") == false && relation.equals("!=") == false)
			throw new IllegalArgumentException("Error: invalid relational operator " + relation);

		HashMap<String, Collection<Event>> result = new HashMap<String, Collection<Event>>();
		try {
			for (String userName : inputList){
				Collection<Event> events = User.getEvents(userName, apiKey);

				result.put(userName, new LinkedList<Event>());

				for (Event event : events){
					if (relation.equals("==")){
						if (event.getTitle().toLowerCase().equals(typeMan.string2StringType(strval.toLowerCase())) == false)
							continue;
					}
					else if (relation.equals("!=")){
						if (event.getTitle().toLowerCase().equals(typeMan.string2StringType(strval.toLowerCase())) == true)
							continue;
					}

					result.get(userName).add(event);
				}

			}
		}catch (net.roarsoftware.lastfm.CallException e){
			this.reportConnectionError();
		}
		return result;

	}


	public HashMap<String, Collection<Event>> usereventsByArtist(String relation,  Object idval) throws IllegalArgumentException, SymbolTableException{

		String id = (String)idval.toString();

		String strval = symTable.getStringById(id);
		if (strval == null)
			throw new SymbolTableException("Error: invalid symbol " + id);

		return this.usereventsByArtist(relation, strval);

	}

	public HashMap<String, Collection<Event>> usereventsByArtist(String relation, String strval) throws IllegalArgumentException {

		if (relation.equals("==") == false && relation.equals("!=") == false)
			throw new IllegalArgumentException("Error: invalid relational operator " + relation);

		HashMap<String, Collection<Event>> result = new HashMap<String, Collection<Event>>();
		try{
			for (String userName : inputList){
				Collection<Event> events = User.getEvents(userName, apiKey);

				result.put(userName, new LinkedList<Event>());

				for (Event event : events){

					boolean present = false;				

					if (relation.equals("==")){
						for (String artist : event.getArtists()){
							if (artist.toLowerCase().equals(typeMan.string2StringType(strval.toLowerCase())) == true){
								present = true;
								break;
							}
						}
						if (present)
							result.get(userName).add(event);
					}
					else if (relation.equals("!=")){
						for (String artist : event.getArtists()){
							if (artist.toLowerCase().equals(typeMan.string2StringType(strval.toLowerCase())) == true){
								present = true;
								break;
							}
						}
						if (present == false)
							result.get(userName).add(event);
					}

				}

			}
		}catch (net.roarsoftware.lastfm.CallException e){
			this.reportConnectionError();
		}
		return result;

	}

	public HashMap<String, Collection<Event>> usereventsByCountry(String relation,  Object idval) throws IllegalArgumentException, SymbolTableException{

		String id = (String)idval.toString();

		String strval = symTable.getStringById(id);
		if (strval == null)
			throw new SymbolTableException("Error: invalid symbol " + id);

		return this.usereventsByCountry(relation, strval);

	}

	public HashMap<String, Collection<Event>> usereventsByCountry(String relation,  String strval) throws IllegalArgumentException{

		if (relation.equals("==") == false && relation.equals("!=") == false)
			throw new IllegalArgumentException("Error: invalid relational operator " + relation);

		HashMap<String, Collection<Event>> result = new HashMap<String, Collection<Event>>();
		try{
			for (String userName : inputList){
				Collection<Event> events = User.getEvents(userName, apiKey);

				result.put(userName, new LinkedList<Event>());

				for (Event event : events){
					if (relation.equals("==")){
						if (event.getVenue().getCountry().toLowerCase().equals(typeMan.string2StringType(strval.toLowerCase())) == false)
							continue;
					}
					else if (relation.equals("!=")){
						if (event.getVenue().getCountry().toLowerCase().equals(typeMan.string2StringType(strval.toLowerCase())) == true)
							continue;
					}

					result.get(userName).add(event);
				}

			}
		}catch (net.roarsoftware.lastfm.CallException e){
			this.reportConnectionError();
		}
		return result;



	}

	public HashMap<String, Collection<Event>> usereventsByAttendance(String relation, String id) throws SymbolTableException{

		Integer intval = symTable.getIntById(id);
		if (intval == null)
			throw new SymbolTableException("Error: invalid symbol " + id);

		return this.usereventsByAttendance(relation, intval);
	}

	public HashMap<String, Collection<Event>> usereventsByAttendance(String relation, Integer intval){

		HashMap<String, Collection<Event>> result = new HashMap<String, Collection<Event>>();

		try{
			for (String userName : inputList){
				Collection<Event> events = User.getEvents(userName, apiKey);

				result.put(userName, new LinkedList<Event>());

				for (Event event : events){
					if (relation.equals(">")){
						if (event.getAttendance() <= intval.intValue())
							continue;
					}
					else if(relation.equals("<")){
						if (event.getAttendance() >= intval.intValue())
							continue;
					}
					else if(relation.equals("==")){
						if (event.getAttendance() != intval.intValue())
							continue;
					}
					else if(relation.equals("!=")){
						if (event.getAttendance() == intval.intValue())
							continue;
					}
					else if(relation.equals("<=")){
						if (event.getAttendance() > intval.intValue())
							continue;
					}
					else if(relation.equals(">=")){
						if (event.getAttendance() < intval.intValue())
							continue;
					}

					result.get(userName).add(event);
				}

			}
		}catch (net.roarsoftware.lastfm.CallException e){
			this.reportConnectionError();
		}
		return result;

	}

	public HashMap<String, Collection<Event>> eventsByAttendance(String relation, String id) throws SymbolTableException{

		Integer intval = symTable.getIntById(id);
		if (intval == null)
			throw new SymbolTableException("Error: invalid symbol " + id);

		return this.eventsByAttendance(relation, intval);
	}

	public HashMap<String, Collection<Event>> eventsByAttendance(String relation, Integer intval){

		HashMap<String, Collection<Event>> result = new HashMap<String, Collection<Event>>();

		try{
			for (String artistName : inputList){
				Collection<Event> events = Artist.getEvents(artistName, apiKey);

				result.put(artistName, new LinkedList<Event>());

				for (Event event : events){
					if (relation.equals(">")){
						if (event.getAttendance() <= intval.intValue())
							continue;
					}
					else if(relation.equals("<")){
						if (event.getAttendance() >= intval.intValue())
							continue;
					}
					else if(relation.equals("==")){
						if (event.getAttendance() != intval.intValue())
							continue;
					}
					else if(relation.equals("!=")){
						if (event.getAttendance() == intval.intValue())
							continue;
					}
					else if(relation.equals("<=")){
						if (event.getAttendance() > intval.intValue())
							continue;
					}
					else if(relation.equals(">=")){
						if (event.getAttendance() < intval.intValue())
							continue;
					}

					result.get(artistName).add(event);
				}

			}
		}catch (net.roarsoftware.lastfm.CallException e){
			this.reportConnectionError();
		}
		return result;

	}

	public HashMap<String, Collection<Event>> eventsByTitle(String relation,  Object idval) throws IllegalArgumentException, SymbolTableException{

		String id = (String)idval.toString();

		String strval = symTable.getStringById(id);
		if (strval == null)
			throw new SymbolTableException("Error: invalid symbol " + id);
		return this.eventsByTitle(relation, strval);

	}

	public HashMap<String, Collection<Event>> eventsByTitle(String relation,  String strval) throws IllegalArgumentException{

		if (relation.equals("==") == false && relation.equals("!=") == false)
			throw new IllegalArgumentException("Error: invalid relational operator " + relation);

		HashMap<String, Collection<Event>> result = new HashMap<String, Collection<Event>>();

		try{
			for (String artistName : inputList){
				Collection<Event> events = Artist.getEvents(artistName, apiKey);

				result.put(artistName, new LinkedList<Event>());

				for (Event event : events){
					if (relation.equals("==")){
						if (event.getTitle().toLowerCase().equals(typeMan.string2StringType(strval.toLowerCase())) == false)
							continue;
					}
					else if (relation.equals("!=")){
						if (event.getTitle().toLowerCase().equals(typeMan.string2StringType(strval.toLowerCase())) == true)
							continue;
					}

					result.get(artistName).add(event);
				}

			}
		}catch (net.roarsoftware.lastfm.CallException e){
			this.reportConnectionError();
		}
		return result;

	}

	public HashMap<String, Collection<Event>> eventsByCountry(String relation,  Object idval) throws IllegalArgumentException, SymbolTableException{

		String id = (String)idval.toString();

		String strval = symTable.getStringById(id);
		if (strval == null)
			throw new SymbolTableException("Error: invalid symbol " + id);

		return this.eventsByCountry(relation, strval);

	}

	public HashMap<String, Collection<Event>> eventsByCountry(String relation,  String strval) throws IllegalArgumentException{

		if (relation.equals("==") == false && relation.equals("!=") == false)
			throw new IllegalArgumentException("Error: invalid relational operator " + relation);

		HashMap<String, Collection<Event>> result = new HashMap<String, Collection<Event>>();

		try{
			for (String artistName : inputList){
				Collection<Event> events = Artist.getEvents(artistName, apiKey);

				result.put(artistName, new LinkedList<Event>());

				for (Event event : events){
					if (relation.equals("==")){
						if (event.getVenue().getCountry().toLowerCase().equals(typeMan.string2StringType(strval.toLowerCase())) == false)
							continue;
					}
					else if (relation.equals("!=")){
						if (event.getVenue().getCountry().toLowerCase().equals(typeMan.string2StringType(strval.toLowerCase())) == true)
							continue;
					}

					result.get(artistName).add(event);
				}

			}
		}catch (net.roarsoftware.lastfm.CallException e){
			this.reportConnectionError();
		}
		return result;

	}

	public HashMap<String, Collection<Track>> topTracksByArtist(String relation,  Object idval) throws IllegalArgumentException, SymbolTableException{

		String id = (String)idval.toString();

		String strval = symTable.getStringById(id);
		if (strval == null)
			throw new SymbolTableException("Error: invalid symbol " + id);

		return this.topTracksByArtist(relation, strval);

	}

	public HashMap<String, Collection<Track>> topTracksByArtist(String relation, String strval) throws IllegalArgumentException {

		if (relation.equals("==") == false && relation.equals("!=") == false)
			throw new IllegalArgumentException("Error: invalid relational operator " + relation);

		HashMap<String, Collection<Track>> result = new HashMap<String, Collection<Track>>();

		try{
			for (String nation : inputList){
				Collection<Track> tracks = Geo.getTopTracks(nation, apiKey);

				result.put(nation, new LinkedList<Track>());

				for (Track track : tracks){
					if (relation.equals("==")){
						if (track.getArtist().toLowerCase().equals(typeMan.string2StringType(strval).toLowerCase()) == false)
							continue;
					}
					else if (relation.equals("!=")){
						if (track.getArtist().toLowerCase().equals(typeMan.string2StringType(strval).toLowerCase()) == true)
							continue;
					}

					result.get(nation).add(track);
				}

			}
		}catch (net.roarsoftware.lastfm.CallException e){
			this.reportConnectionError();
		}
		return result;


	}

	public HashMap<String, Collection<Track>> topTracksByPlaycount(String relation, String id) throws SymbolTableException{

		Integer intval = symTable.getIntById(id);
		if (intval == null)
			throw new SymbolTableException("Error: invalid symbol " + id);

		return this.topTracksByPlaycount(relation, intval);
	}

	public HashMap<String, Collection<Track>> topTracksByPlaycount(String relation, Integer intval)
	{
		HashMap<String, Collection<Track>> result = new HashMap<String, Collection<Track>>();
		try{	
			for (String nation : inputList){
				Collection<Track> tracks = Geo.getTopTracks(nation, apiKey);

				result.put(nation, new LinkedList<Track>());

				for (Track track: tracks){
					if (relation.equals(">")){
						if (track.getPlaycount() <= intval.intValue())
							continue;
					}
					else if(relation.equals("<")){
						if (track.getPlaycount() >= intval.intValue())
							continue;
					}
					else if(relation.equals("==")){
						if (track.getPlaycount() != intval.intValue())
							continue;
					}
					else if(relation.equals("!=")){
						if (track.getPlaycount() == intval.intValue())
							continue;
					}
					else if(relation.equals("<=")){
						if (track.getPlaycount() > intval.intValue())
							continue;
					}
					else if(relation.equals(">=")){
						if (track.getPlaycount() < intval.intValue())
							continue;
					}

					result.get(nation).add(track);
				}

			}
		}catch (net.roarsoftware.lastfm.CallException e){
			this.reportConnectionError();
		}
		return result;
	}

	public HashMap<String, Collection<Track>> toptrackAND(HashMap<String, Collection<Track>>c1, HashMap<String, Collection<Track>>c2){
		if (c1 == null || c2 == null)
			return null;

		HashMap<String, Collection<Track>> result = new HashMap<String, Collection<Track>>();

		for (String nation : c1.keySet()){

			Collection<Track> tracksOk = new LinkedList<Track>();
			for (Track outer : c1.get(nation)){
				for (Track inner : c2.get(nation)){
					if (outer.getName().equals(inner.getName()) && outer.getArtist().equals(inner.getArtist())){
						tracksOk.add(outer);
						continue;
					}
				}
			}
			result.put(nation, tracksOk);
		}

		return result;	
	}


	public HashMap<String, Collection<Track>> toptrackOR(HashMap<String, Collection<Track>>c1, HashMap<String, Collection<Track>>c2){
		if (c1 == null || c2 == null)
			return null;

		HashMap<String, Collection<Track>> result = new HashMap<String, Collection<Track>>();		

		for (String nation : c1.keySet()){
			Collection<Track> tracksOk = new LinkedList<Track>();

			for (Track outer : c1.get(nation)){
				boolean present = false;		
				for (Track inner : c2.get(nation)){
					if (outer.getName().equals(inner.getName()) && outer.getArtist().equals(inner.getArtist())){
						present = true;
						break;
					}
				}
				if (present == false)
					tracksOk.add(outer);
			}	
			for (Track t : c2.get(nation))
				tracksOk.add(t);
			result.put(nation, tracksOk);

		}

		return result;

	}


}
