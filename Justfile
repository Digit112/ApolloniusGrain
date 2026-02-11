help:
	just --list

clean:
    rm -rf output

build:
	javac -parameters -d build --module-source-path src --module ekobadd.geometry
	javac -d build --module-source-path src --module ekobadd.test
	#javac -d build -classpath build/ekobadd.geometry Apollonius.java
	javac  --module-path build --add-modules ekobadd.geometry Apollonius.java

rebuild: clean build

test:
	java --module-path build -m ekobadd.geometry/ekobadd.geometry.test.TestMain

run:
	java --module-path build --add-modules ekobadd.geometry Apollonius

document:
	javadoc --release 21 -private -d docs --module-source-path src --module ekobadd.geometry,ekobadd.test -Xdoclint:all,-missing