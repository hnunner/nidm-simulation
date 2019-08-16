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
 9. You will find to files (_CIDMDataGenerator_, _UserInterface_) which you can start by right clicking on them and selecting: _Run as_ - _Java application_.
Note: Depending on the settings in _src/main/resources/config.properties_ the _data generator_ may generate large amounts of data taking a very long time to finish.

## Using the _GUI_ version of the NIDM Simulator
When starting the _GUI_ version of the NIDM Simulator two windows appear. The main window allows to define the parameter settings, control the simulation, export network data, and displays the network simulations. The second window shows detailed statistics for the whole network (_Global Stats_) and for a single agent (_Agent Stats_) that can be selected by clicking on the corresponding node in the white network area once agents have been added.

### Running a simulation
Running a minimal simulation requires the following steps:
 1. Define the model and its parameters in the _Model_ tab:
   1. Select the type of model (currently only the _CIDM_ model is available).
   2. Set the parameters to values of interest. The initial settings provide a scenario with high social benefits, risk avoiding agents, and severe infections.
 2. Add agents and start the simulation in the _Simulation_ tab:
   1. Add agents by clicking the __Add agent__ button at the top of the window multiple times.
   2. Start the simulation by clicking the __Start__ button at the bottom of the window.
   3. Infect an agent by either clicking on the __Infect random agent__ button, or activating _On node click:_ - _Toggle infection_ and clicking on a network node.

### Exporting data ###
The simulator allows static and dynamic network exports.

#### Static network exports ####
Static network exports use the data of the network at the time the export is created. In order to ensure that the network structure does not change, it is advised to push the __Pause__ button on the _Simulation_ tab first. Three data types are available:
 * _GEXF_: A detailed graph representation optimized for [Gephi](https://gephi.org/).
 * _Edge List_: A list of all connections between nodes. This file is a plain text file and has no ending by default.
 * _Adjacency Matrix_: A matrix of all nodes with a _1_ at the intersection of two nodes if a tie exists and _0_ otherwise. This file is a plain text file and has no ending by default.

Exporting static networks is the same for all static network types:
 1. Run a simulation as described above.
 2. Pause the simulation by pushing the __Pause__ button on the _Simulation_ tab.
 3. Select the preferred data type in the _Export_ tab. In case of _GEXF_ make sure to select _Network type:_ - _Static_.
 4. Push the __Export__ button and select a folder and file to store the data in.


### _GUI_ components

#### Main window ####
The main window consists of two areas. The left area contains tabs to define the parameter settings, control the simulation, and export network data. The right area displays the network simulations.

##### Model tab #####
The model tab contains a drop-down list to select model types (currently only the _CIDM_ model is available). Parameter values of the selected model can be defined using the interactive fields below. These differ according to the selected model type.

##### Simulation tab #####
The simulation tab contains a number of different components:
 * Button __Add agent__: adds agents to the network. The amount can be defined in the text field to its right.
 * Button __Remove agent__: removes agents to the network. The amount can be defined in the text field to its right.











