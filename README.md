parody-generator
================

"Parody generators are computer programs which generate syntactically correct, but meaningless text in the style of a particular writer"
([Wikipedia](https://en.wikipedia.org/wiki/Parody_generator)).

This is a parody-generator that focuses on Hebrew texts. 

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

A working website can be found here: [http://imitatorgwt.appspot.com](http://imitatorgwt.appspot.com)

CREDITS:
* Scientific supervision: Prof. Moshe Koppel.
* M.Sc. thesis: Roni (Aharon) Vered (2007-2009).
* Desktop application programming ("Imitator"): Vladimir Torgovitzki and Yuri Yeverbaum (2010).
* Web application and algorithm enhancements: Erel Segal-Halevi (2011-2013).

