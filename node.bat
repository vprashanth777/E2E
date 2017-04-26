cd /d %~dp0Framework
SET PATH=%PATH%;.
java -jar ./lib/selenium-server-standalone-2.41.0.jar  -role node -hubHost localhost -hubPort 4444 -browser browserName=firefox
pause