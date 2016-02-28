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

Edit the options in the file **server.sh**, then run:

	sh server.sh

Then, in a browser, open the file: **client/index.html**

Look at the web console and make sure it connects to the server (it should say: "CLIENT: connecting to..." and then "CLIENT: connected to...").


## Demo

A working website should be found here: [http://imitatorgwt.appspot.com](http://imitatorgwt.appspot.com) or here: [http://tora.us.fm/imitator](http://tora.us.fm/imitator).

## Credits

* Scientific supervision: Prof. Moshe Koppel.
* M.Sc. thesis: Roni (Aharon) Vered (2007-2009).
* Desktop application programming ("Imitator"): Vladimir Torgovitzki and Yuri Yeverbaum (2010).
* Web application and algorithm enhancements: Erel Segal-Halevi (2011-2013).

