.PHONY: build run clean

build:
	javac -g Polaris_v8.java

run:
	java Polaris_v8

clean:
	find . -type f -name '*.class' -delete
