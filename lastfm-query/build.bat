@echo off

java -jar .\lib\JFlex.jar --nobak -d .\generated\it\polito\lastfm\query .\cuplex_src\scanner.jflex
java -jar .\lib\java-cup-0.11a-beta-20060608.jar -destdir .\generated\it\polito\lastfm\query -package it.polito.lastfm.query .\cuplex_src\parser.cup

mkdir bin
javac -cp .;.\lib\java-cup-0.11a-beta-20060608.jar;.\lib\last.fm-bindings.jar;.\lib\last.fm-bindings.jar;.\lib\entity-strip-insert.jar -d bin\ .\generated\it\polito\lastfm\query\*.java .\src\it\polito\lastfm\query\*.java

echo on