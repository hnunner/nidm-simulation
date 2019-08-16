# Networking during Infectious Diseases Model (NIDM) Simulator

Welcome to the NIDM Simulator project. This project is the Java implementation of the NIDM defined by Nunner, Buskens, & Kretzschmar (2019). The project comes in two versions:
 1. A _GUI_ version that provides an easy to use graphical user interface, allows to play around with parameter settings, and produces a visualization and detailed real-time statistics of the resulting networks. This is suitable to investigate small networks with 50 agents max and test the immediate effects of different parameter combinations.
 2. A _data generator_ version that allows to produce large data with many different parameter combinations.

## Installing the NIDM Simulator
The project is a stand-alone software application that can be used in two ways:

### Downloading the project and executing the provided jar-file
This is the easier way to use the simulator and suffices if you want to use the _GUI_‚ version only.
 1. [Make sure Java Runtime Environment (JRE) is installed](https://www.baeldung.com/java-check-is-installed).
 2. [Dowload the JRE](https://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html) and install if necessary.
 3. Download the NIDM project as zip-file by clicking the __Clone or download__ button at the top this page.
 4. Unzip the downloaded file to extract the project into a folder.
 5. Execute the jar-file by double clicking: path.to.unzipped.folder/executables/nidm-`version.number`.jar

### Cloning the project and running it as a Maven project
This way allows to run both the _GUI_ and the _data generator_ versions. Further, the code can be changed to realize new features. The following steps are necessary to run the code in the Eclipse IDE:
 1. Download and install the latest version of [Eclipse](https://www.eclipse.org/downloads/).
 2. Clone (or download) the NIDM repository from this page.
 3. Start Eclipse.
 4. In the menu bar of Eclipse go to: _File_ - _Import..._
 5. In the _Import_ window navigate to: _Maven_ - _Existing Maven Projects_ and click __Next__
 6. In the _Import Maven Projects_ window click on __Browse__ and go to the root folder of the cloned (or downloaded) repository. Note: This folder needs to contain a file named _pom.xml_.
 7. Click __Finish__
 8. In the _Package Explorer_ window on the left of Eclipse navigate to: _nidm-simulation_ - _src/main/java_ - _nl.uu.socnetid.nidm.mains_
 9. You will find to files (_CIDMDataGenerator_, _UserInterface_) which you can start by right clicking on them and selecting: _Run as_ - _Java application_