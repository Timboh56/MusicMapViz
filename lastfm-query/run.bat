@echo off

java  -cp bin;lib\java-cup-0.11a-beta-20060608-runtime.jar;lib\last.fm-bindings.jar;lib\entity-strip-insert.jar it.polito.lastfm.query.Main %1

echo on

