Clu-HitMan
==========

HitMan is a command-line utility that can start and kill other processes

Maven builds a self-contained jar. Run it without options for help:
```java -jar "clu.hitman-*-jar-with-dependencies.jar"```

The  utility has 3 functions based on command line arguments
 * ( port+cmd ): start hitMan (on port), hitMan wiil start process specified by cmd option
 * ( port+sig ): send signal to hitMan (on port)
 * ( no port ): print help / usage

See also Main class in package no.antares.clutil.hitman

Examples:
```
(DOS) java -jar "clu.hitman-*-jar-with-dependencies.jar" -port 5555 -cmd "C:\Program Files\Internet Explorer\iexplore.exe" 
(OSX) java -jar "clu.hitman-*-jar-with-dependencies.jar" -port 5555 -cmd /Applications/TextWrangler.app/Contents/MacOS/TextWrangler 
java -jar "clu.hitman-*-jar-with-dependencies.jar" -port 5555 -sig "HIT ME IN 5" 
```

