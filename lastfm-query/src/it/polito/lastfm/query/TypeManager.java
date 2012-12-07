package it.polito.lastfm.query;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TypeManager{

	public TypeManager(){
	}

	public boolean isId(Object string){
		Pattern pattern = Pattern.compile("\"([^\n\r\"]+|\\\")*\""); 
		Matcher matcher = pattern.matcher(string.toString());

		return !matcher.matches();
	}


	public String string2StringType(String string){
		StringTokenizer st = new StringTokenizer(string.toString(), "\"");
		try {
			return st.nextToken();
		} catch (NoSuchElementException e){
			return new String ();
		}
	}

}
