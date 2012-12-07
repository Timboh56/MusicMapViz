package it.polito.lastfm.query;

import java_cup.runtime.*;

%%

%class Lexer
%unicode
%cup
%line
%column

%{
  private Symbol symbol(int type) {
    return new Symbol(type, yyline, yycolumn);
  }
  private Symbol symbol(int type, Object value) {
    return new Symbol(type, yyline, yycolumn, value);
	
  }

%}

string		= \"([^\n\r\"]+|\\\")*\" 
int 		= [0-9]+	
nl 			= \r|\n|\r\n
ws 			= [ \t]
id			= [a-zA-Z_][a-zA-Z_0-9]*
relop       = (">"|"<"|"=="|"!="|"<="|">=")
comment		= "#".*{nl}

%%

{comment}		{	return symbol(sym.COMMENT);}

{string}		{	return symbol(sym.STRING, new String(yytext()));}
{int}			{	return symbol(sym.INT, new Integer(yytext()));}
","				{	return symbol(sym.CM);}
";"				{	return symbol(sym.SMCL);}
"show"			{	return symbol(sym.SHOW);}
"info"			{	return symbol(sym.INFO);}
"output" 		{	/*System.out.println("output"); */return symbol(sym.OUTPUT);}
"topalbums" 	{	return symbol(sym.TOPALBUMS);}
"stdout"		{	return symbol(sym.STDOUT);}
"events"		{	return symbol(sym.EVENTS);}
"userevents"	{	return symbol(sym.USEREVENTS);}
"toptracks" 	{	return symbol(sym.TOPTRACKS);}
"or"			{	return symbol(sym.OR);}
"and"	 		{	return symbol(sym.AND);}

"playcount"		{	return symbol(sym.PLAYCOUNT);}
"listeners"		{	return symbol(sym.LISTENERS);}
"country"		{	return symbol(sym.COUNTRY);}
"attendance"	{	return symbol(sym.ATTENDANCE);}
"artist"		{	return symbol(sym.ARTIST);}
"title"			{	return symbol(sym.TITLE);}

"("				{	return symbol(sym.LBR);}
")"				{	return symbol(sym.RBR);}
"where"			{	return symbol(sym.WHERE);}
">>"			{	return symbol(sym.REDIR);}

{relop}			{	return symbol(sym.RELOP, new String(yytext()));}
"="				{	return symbol(sym.ASSIGN);}
"-"				{	return symbol(sym.MINUS);}

{nl}			{	return symbol(sym.NL);}
{ws}			{	;}

{id}".add"		{	return symbol(sym.ADD, new String(yytext()));}
{id}			{	return symbol(sym.ID, new String(yytext()));}

"\\"{ws}*{nl}	{	/* new line without executing a command */;	}

.			{	;}
