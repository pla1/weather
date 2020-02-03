#!/bin/bash
#
# Build Weather locally using git, wget, javac commands and then run it.
#
/usr/bin/git pull
if [ $? -ne 0 ]
then
  /bin/echo -e "git pull failed.\n\nMake sure you have cloned weather and you are in its directory."
  /bin/echo -e "Example:\n\ngit clone https://github.com/pla1/weather.git\n./run.sh"
  exit -1
fi
urls="https://repo1.maven.org/maven2/com/google/code/gson/gson/2.8.6/gson-2.8.6.jar \
https://repo1.maven.org/maven2/org/postgresql/postgresql/42.2.9/postgresql-42.2.9.jar"
for url in $urls
do
  /bin/echo "$url"
  fileName="${url##*/}"
  if [ ! -f $fileName ]
  then
    /usr/bin/wget "$url"  --output-document="$fileName"
  fi
done

javac -encoding UTF-8 -cp .:* src/main/java/com/plawx/*.java
java -cp src/main/java:.:* com.plawx.WeatherDAO writeCurrentConditions

