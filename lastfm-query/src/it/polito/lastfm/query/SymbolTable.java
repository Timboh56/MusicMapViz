package it.polito.lastfm.query;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class SymbolTable{

	private Map<String, String> str_symbol_table;
	private Map<String, Integer> int_symbol_table;
	private Map<String, List<Object>> list_symbol_table;

	private static SymbolTable instance = null;

	private TypeManager typeMan;

	public static SymbolTable getInstance(){
		if (instance == null){
			instance = new SymbolTable(); 
		}
		return instance;
	}

	private SymbolTable(){
		str_symbol_table = new HashMap<String, String>();
		int_symbol_table = new HashMap<String, Integer>();
		list_symbol_table = new HashMap<String, List<Object>>();

		typeMan = new TypeManager();
	}

	@SuppressWarnings("unchecked")
	public List<String> stringListById(String idVal) throws SymbolTableException{

		List<String> result = new LinkedList<String>();

		Object object = this.getSymbolObject(idVal);
		if (object == null)
			throw new SymbolTableException("Error: symbol " + idVal + " does not exist");
		if (object instanceof Integer)
			throw new SymbolTableException("Error: symbol " + idVal + " is not a string or a list of strings");
		if (object instanceof String){
			String string = object.toString();
			result.add(string);
		}
		else if (object instanceof List){
			for (Object o : (List)object){
				if (o instanceof String == false)
					throw new SymbolTableException("Error: " + idVal + " is not a list of strings");
				result.add(o.toString());
			}
		}

		return result;
	}

	public Integer getIntById(String id){
		if (this.int_symbol_table.containsKey(id))
			return this.int_symbol_table.get(id);
		return null;
	}

	public String getStringById(String id){
		if (this.str_symbol_table.containsKey(id))
			return this.str_symbol_table.get(id);
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<String> addId2StringList(List list, String symId) throws SymbolTableException{
		
		if (this.list_symbol_table.containsKey(symId) == false && this.str_symbol_table.containsKey(symId) == false){
			throw new SymbolTableException("Error: string list or string " + symId + " does not exist");
		}
		
		List<String> result = new LinkedList<String>();
		
		for (Object string : list)
			result.add((String)string.toString());
		
		if (this.list_symbol_table.containsKey(symId)){
			List listFromId = this.list_symbol_table.get(symId);
		
			for (Object field : listFromId){
				if (field instanceof String == false)
					throw new SymbolTableException("Error: " + symId + " is not a string list");
			}
			for (Object field : listFromId){
				result.add((String)field.toString());
			}
		}
		else{
			result.add((String)this.str_symbol_table.get(symId));
		}
		
		
		return result;
	}

	@SuppressWarnings("unchecked")
	public void add2List(Object add_sym, Object assigned, Boolean suppress) throws SymbolTableException{

		String symId = (String)add_sym;
		int len = symId.indexOf('.');

		StringBuffer buffer = new StringBuffer();

		for (int i = 0; i < len; i ++)
			buffer.append(symId.charAt(i));

		symId = buffer.toString();

		if (this.list_symbol_table.containsKey(symId) == false){

			throw new SymbolTableException("Error: list " + symId + " does not exist");
		}
		if (assigned instanceof List){

			for (Object o : (List)assigned){
				if (o instanceof Integer)
					this.list_symbol_table.get(symId).add(o);
				else if (o instanceof String){
					if (typeMan.isId(o)){
						String id = typeMan.string2StringType(o.toString());
						Object oValue = this.getSymbolObject(id);
						if (oValue == null){
							throw new SymbolTableException("Error: symbol " + id + " does not exist");
						}
						if (oValue instanceof Integer)
							this.list_symbol_table.get(symId).add(oValue);
						else if (oValue instanceof String)
							this.list_symbol_table.get(symId).add((String)oValue.toString());
						else {
							List<Object> temp = new LinkedList<Object>();
							for (Object listObj : (List)oValue){
								if (listObj instanceof String)
									temp.add((String)listObj.toString());
								else
									temp.add(listObj);
							}
							for (Object listObj : temp)
								this.list_symbol_table.get(symId).add(listObj);
						}
					}

					else 
						this.list_symbol_table.get(symId).add((String)o.toString());
				}

			}
		}

		else if (assigned instanceof Integer)
			this.list_symbol_table.get(symId).add(assigned);
		else if (assigned instanceof String){
			if (typeMan.isId(assigned)){
				String id = typeMan.string2StringType(assigned.toString());

				Object idvalue = this.getSymbolObject(id);
				if (idvalue == null)
					throw new SymbolTableException("Error: symbol " + id + " does not exist");
				if (idvalue instanceof Integer)
					this.list_symbol_table.get(symId).add(idvalue);
				else if  (idvalue instanceof String){
					this.list_symbol_table.get(symId).add((String)idvalue.toString());
				}
				else if (idvalue instanceof List)
					for (Object listObj : (List)idvalue)
						if (listObj instanceof String)
							this.list_symbol_table.get(symId).add((String)listObj.toString());
						else
							this.list_symbol_table.get(symId).add(listObj);
			}
			else{
				this.list_symbol_table.get(symId).add(assigned);
			}

		}

		if (suppress == false){
			this.printSymbolContent(symId);
		}

	}

	public void setNewSymbol(Object idname, Object value, Boolean suppress) throws SymbolTableException{
		if (value instanceof String){
			if (this.typeMan.isId(value)){
				this.setId(idname, value);
			}
			else{ 
				this.setString(idname, typeMan.string2StringType(value.toString()));
			}
		}

		if (value instanceof List)
			this.setList(idname, value);	
		if (value instanceof Integer){
			this.setInteger(idname, value);
		}
		if (suppress == false){
			this.printSymbolContent(idname.toString());
		}

	}

	public void printSymbolTable() throws SymbolTableException{

		for (String id : this.int_symbol_table.keySet())
			this.printSymbolContent(id);
		for (String id : this.str_symbol_table.keySet())
			this.printSymbolContent(id);
		for (String id : this.list_symbol_table.keySet())
			this.printSymbolContent(id);

	}

	@SuppressWarnings("unchecked")
	public void printSymbolContent(String id) throws SymbolTableException{	

		if (this.getSymbolObject(id) == null)
			throw new SymbolTableException("Error: symbol " + id + " does not exist");

		Object res = this.getSymbolObject(id);

		StringBuffer sb = new StringBuffer();

		sb.append(id + " = ");
		if (res instanceof List){
			boolean first = true;
			sb.append("[");
			for (Object o : (List)res){
				if (first)
					first = false;
				else
					sb.append(", ");
				if (o instanceof String)
					sb.append("\"" + typeMan.string2StringType((String)o.toString()) + "\"");
				else{
					sb.append(o);
				}
			}
			sb.append("]");
		}
		else if (res instanceof String)
			sb.append("\"" + res + "\"");
		else 
			sb.append(res);

		System.out.println(sb.toString());
	}

	private void setString(Object id, Object stringValue){
		this.eraseSymbol(id.toString());
		this.str_symbol_table.put(id.toString(), (String)typeMan.string2StringType(stringValue.toString()));
	}

	private void setInteger(Object id, Object integerValue){
		this.eraseSymbol(id.toString());
		this.int_symbol_table.put(id.toString(), Integer.valueOf(integerValue.toString()));
	}

	@SuppressWarnings("unchecked")
	private void setList(Object id, Object listValue) throws SymbolTableException{

		List list = (List)listValue;

		List<Object> resultList = new LinkedList<Object>();

		for (Object object : list){

			if (object instanceof Integer)
				resultList.add(object);
			else {
				if (typeMan.isId(object)){
					Object o = this.getSymbolObject(object.toString());
					if (o == null)
						throw new SymbolTableException("Error: symbol " + object.toString() + " does not exist");
					if (o instanceof List){
						for (Object item : (List)o){
							if (item instanceof String)
								resultList.add(typeMan.string2StringType((String)item.toString()));
							else
								resultList.add(item);
						}
					}
					else if (o instanceof String){
						resultList.add(typeMan.string2StringType(o.toString()));
					}
					else{
						resultList.add(o);
					}
				}
				else
					resultList.add(typeMan.string2StringType(object.toString()));
			}
		}

		this.eraseSymbol(id.toString());
		this.list_symbol_table.put(id.toString(), resultList);

	}


	@SuppressWarnings("unchecked")
	private void setId(Object id, Object idName) throws SymbolTableException { 

		Object idValue = this.getSymbolObject(idName.toString());
		if (idValue == null)
			throw new SymbolTableException("Error: symbol " + idName.toString() + " does not exist"); 

		if (id.toString().equals(idName.toString()))
			return;

		this.eraseSymbol(id.toString());

		if (idValue instanceof String){
			this.str_symbol_table.put(id.toString(), typeMan.string2StringType(idValue.toString()));
		}
		else if (idValue instanceof Integer){
			this.int_symbol_table.put(id.toString(), (Integer)idValue);
		}
		else if (idValue instanceof List){
			this.list_symbol_table.put(id.toString(), (List<Object>)idValue);
		}
	}

	private void eraseSymbol(String id){
		if (this.str_symbol_table.containsKey(id))
			this.str_symbol_table.remove(id);
		if (this.list_symbol_table.containsKey(id))
			this.list_symbol_table.remove(id);
		if (this.int_symbol_table.containsKey(id))
			this.int_symbol_table.remove(id);
	}

	public Object getSymbolObject(String id){

		if (this.int_symbol_table.containsKey(id))
			return this.int_symbol_table.get(id);
		if (this.str_symbol_table.containsKey(id))
			return this.str_symbol_table.get(id);
		if (this.list_symbol_table.containsKey(id))
			return this.list_symbol_table.get(id);

		return null;

	}

	public String getFilename(String idname) throws SymbolTableException{

		String result = this.getStringById(idname);
		if (result == null)
			throw new SymbolTableException("Error: symbol " + idname + " is not a string");

		return result;
	}


}
