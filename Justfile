help:
    just --list

test:
	rm -rf *.class
	javac test\*.java
	javac geometry\*.java
	javac geometry\test\*.java