parody-generator
================

This program generates random Hebrew sentences based on a Hebrew text. It was developed as an understanding test, to let people test whether they understand classic Hebrew texts. [The rationale behind the project is explained here](http://woland.ph.biu.ac.il/?page_id=154).

The name "parody generator" is from [Wikipedia](https://en.wikipedia.org/wiki/Parody_generator):
"Parody generators are computer programs which generate syntactically correct, but meaningless text in the style of a particular writer".

## Folders

The **corpora** folder contains the parsed texts and their data files:
* ShmonaKvazim - "Eight Files", by Rav Avraham Itzhak Hacohen Kook
* Rambam - "Mishne Tora", by Rav Moshe Ben Maimon
* Herzl - "Altneuland", Old-New Land, by Binyamin Zeev Herzl

**ImitatorProject** is a Java project with the following console applications (main programs):
* mainTasks.MeasureFrequencies - gets as input a corpus tagged with parts of speech, and creates files with statistical measurements.
* mainTasks.SentenceCreator - gets as input the statistical measurements, and generates random sentences.
* puzzles.PuzzleCreator - gets as input the statistical measurements, and generates puzzles with a mix of real and random sentences.
 
**ImitatorNet** is a web application, based on Java and socket.io, for creating and displaying puzzles. It depends on:
* ImitatorProject
* [netty-socketio](https://github.com/mrniko/netty-socketio).

The main server program is ImitatorServer, and the client entry point is client/index.html .

## Installation

Install Java 1.7.

Install Maven.

    git clone https://github.com/erelsgl/parody-generator.git

### Install ImitatorProject
    cd parody-generator/ImitatorProject
    mvn install

Make sure it is installed correctly:
    mvn exec:java -Dexec.mainClass=puzzles.PuzzleCreator

### Install ImitatorNet
    cd parody-generator/ImitatorNet
    mvn install

Start the server:
    export MAVEN_OPTS="-Xmx2G"
    nohup mvn exec:java -Dexec.mainClass=imitatornet.ImitatorServer -Dexec.args="[host-name] [port-number] client" &

Where:

* -Xmx2G means to allocate 2 GB to the program. You may change this value according to your system and to your experience.
* nohup is a Unix command whose goal is to keep the server up even when you log out. Ignore this command if you are on Windows.
* [host-name] is the name of the host you want to use to access the web-app. This can be "localhost" if you want to use the webapp locally, or e.g. "mydomain.com" if you want to use it remotely.
* [port-number] is a number of an unused port on your machine.

Then, in a browser, open the file: **client/index.html**

Look at the web console and make sure it connects to the server (it should say: "CLIENT: connecting to..." and then "CLIENT: connected to...").


## Demo

A working website can be found here: [http://imitatorgwt.appspot.com](http://imitatorgwt.appspot.com)

## Credits

* Scientific supervision: Prof. Moshe Koppel.
* M.Sc. thesis: Roni (Aharon) Vered (2007-2009).
* Desktop application programming ("Imitator"): Vladimir Torgovitzki and Yuri Yeverbaum (2010).
* Web application and algorithm enhancements: Erel Segal-Halevi (2011-2013).

