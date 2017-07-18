# SAI Weblab
A bare-bones spring web-app for experimenting with the [SAI](https://github.com/jmorwick/sai) framework. 

## Weblab's interface

Weblab provides a web interface for loading SAI-compatible databases with graphs, collecting statistics
on those databases, running retrieval and other experiments, and viewing statistics from logs of those experiments. 
Weblab monitors task progress and allows some limited intervention. 
Weblab code shouldn't require modification to run your own custom experiments, but feel free to do so if you wish! 

## Configuring and Running Weblab

Weblab uses a Spring configuration to manage plugins and experiment structure. SAI plugins are declared in a Spring 
XML file named weblab-components.xml which is loaded from the classpath. Edit the file to incorporate the plugins 
and experiment tasks you wish to work with. 

Ex. Using the default configuration with 3rd party jars and weblab-components.xml in the working directory:

> java -jar sai-weblab-1.0.jar

You can build this jar from scratch using maven:

> mvn package spring-boot:repackage
