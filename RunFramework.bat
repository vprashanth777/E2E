@echo off
cd /d %~dp0Framework
SET CLASSPATH=..\Framework\*;..\Framework\lib\*;
SET PATH=%PATH%;.

cls
"C:\Program Files\Java\jre1.8.0_121\bin\java" com.java.RunTest
cd..
pause