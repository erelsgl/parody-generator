parody-generator
================

"Parody generators are computer programs which generate syntactically correct, but meaningless text in the style of a particular writer"
([Wikipedia](https://en.wikipedia.org/wiki/Parody_generator)).

This is a parody-generator that focuses on Hebrew texts. The parsed texts and their data files are under the "corpora" folder:

* ShmonaKvazim - by Rav Avraham Itzhak Hacohen Kook
* Rambam - Mishne Tora 
* Herzl - Altneuland

**ImitatorProject** is the basic Java project. It contains the following main programs:
* mainTasks.MeasureFrequencies - gets as input a corpus tagged with parts of speech, and creates files with statistical measurements.
* mainTasks.SentenceCreator - gets as input the statistical measurements, and generates random sentences.
 
**ImitatorNet** is a web-server for creating and displaying puzzles based on ImitatorProject. It depends on:
* ImitatorProject 
* [netty-socketio](https://github.com/mrniko/netty-socketio).
The main program is ImitatorServer.

CREDITS:
* Scientific consultation: Prof. Moshe Koppel.
* M.Sc. thesis: Roni (Aharon) Vered (2007-2009).
* Desktop application programming ("Imitator"): Vladimir Torgovitzki and Yuri Yeverbaum (2010).
* Web application programming: Erel Segal-Halevi (2011-2013).
