# Networking during Infectious Diseases Model (NIDM) Simulator

Welcome to the NIDM Simulator project. This project is the Java implementation of the NIDM defined by Nunner, Buskens, & Kretzschmar (2019). The project comes in two versions:
 1. A _GUI_ version that provides an easy to use graphical user interface, allows to play around with parameter settings, and produces a visualization and detailed real-time statistics of the resulting networks. This is suitable to investigate small networks with 50 agents max and test the immediate effects of different parameter combinations.
 2. A _Data Generator_ version that allows to produce large data with many different parameter combinations.

## Installing the NIDM Simulator
The project is a stand-alone software application that can be used in two ways:

### 1. Downloading the project and executing the provided jar-file
This is the easier way to use the simulator and suffices if you want to use the _GUI_‚ version only.
 1. [Make sure Java Runtime Environment (JRE) is installed](https://www.baeldung.com/java-check-is-installed).
 2. [Dowload the JRE](https://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html) and install if necessary.
 3. Download the NIDM project as zip-file by clicking the __Clone or download__ button at the top this page.
 4. Unzip the downloaded file to extract the project into a folder.
 5. Execute the jar-file by double clicking: path.to.unzipped.folder/executables/nidm-`version.number`.jar

### 2. Cloning the project and running it as a Maven project
This way allows to run both the _GUI_ and the _Data Generator_ versions. Further, the code can be changed to realize new features. The following steps are necessary to run the code in the Eclipse IDE:
 1. Download and install the latest version of [Eclipse](https://www.eclipse.org/downloads/).
 2. Clone (or download) the NIDM repository from this page.
 3. Start Eclipse.
 4. In the menu bar of Eclipse go to: _File_ - _Import..._
 5. In the _Import_ window navigate to: _Maven_ - _Existing Maven Projects_ and click __Next__
 6. In the _Import Maven Projects_ window click on __Browse__ and go to the root folder of the cloned (or downloaded) repository. Note: This folder needs to contain a file named _pom.xml_.
 7. Click __Finish__
 8. In the _Package Explorer_ window on the left of Eclipse navigate to: _nidm-simulation_ - _src/main/java_ - _nl.uu.socnetid.nidm.mains_
 9. The location contains two files (_CIDMDataGenerator.java_, _UserInterface.java_) which can be started by right clicking on them and selecting: _Run as_ - _Java application_.

__Note:__ Depending on the settings in _src/main/resources/config.properties_ the _Data Generator_ may generate large amounts of data taking a very long time to finish.

## Using the _GUI_ version of the NIDM Simulator
When starting the _GUI_ version of the NIDM Simulator (by double clicking path.to.unzipped.folder/executables/nidm-`version.number`.jar) two windows appear. The main window allows to define the parameter settings, control the simulation, export network data, and displays the network simulations. The second window shows detailed statistics for the whole network (_Global Stats_) and for a single agent (_Agent Stats_) that can be selected by clicking on the corresponding node in the white network area once agents have been added.

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

#### Dynamic network exports ####
Dynamic network exports track detailed network data over the course of time. This format is only supported by the _GEXF_ export type. In order to track the creation and progression of a dynamic network, please follow these steps:
 1. Reset the simulation if necessary.
    1. Pause the simulation if it is running by pushing the __Pause__ button on the _Simulation_ tab.
    2. Remove all agents and ties by pushing the __Clear all__ button on the _Simulation_ tab.
 2. Select _GEXF_ as export type on the _Export_ tab.
 3. Select _Network type:_ - _Dynamic_ on the _Export_ tab.
 4. Select a file to write the dynamic network data to by clicking the __Choose export file__ button on the _Export_ tab. Note that the file ending _.gexf_ needs to be added either here or after export.
 5. Push the button __Start recording__ on the _Export_ tab.
 6. Run a simulation (e.g., as described above).
 7. Push the __Pause__ button on the _Simulation_ tab.
 8. Push the __Stop recording__ button on the _Export_ tab.

Once the __Stop recording__ button as been pushed, the file export is complete and the corresponding file can be opened in [Gephi](https://gephi.org/).


### _GUI_ components

#### Main window ####
The main window consists of two areas. The left area contains tabs to define the parameter settings, control the simulation, and export network data. The right area displays the network simulations.

##### Model tab #####
The model tab contains a drop-down list to select model types (currently only the _CIDM_ model is available). Parameter values of the selected model can be defined using the interactive fields below. These differ according to the selected model type.

##### Simulation tab #####
The simulation tab contains a number of different components to control the network and the simulation:
 * Network controls:
    * Button __Add agent__: adds agents to the network. The amount can be defined in the text field to its right.
    * Button __Remove agent__: removes agents to the network. The amount can be defined in the text field to its right.
    * Button __Infect random agent__: infects a randomly selected susceptible agent.
    * Button __Create full network__: creates ties between all agents.
    * Button __Clear ties__: removes all ties.
    * Button __Clear all__: removes all ties and agents.
 * On node click - defining the action when clicking on a network node:
    * Check box _Show agent stats_: Shows the agent's statistic in the _Statistics_ window
    * Check box _Toggle infection_: Toggles between the different disease states (e.g., susceptible, infected, recovered)
 * Text field _Simulation delay_: controls how fast the simulation is running. The higher the value the slower the simulation.
 * Button __Start__: starts the simulation (network formation and disease transmission).
 * Button __Pause__: pauses the simulation.
 * Button __Reset__: resets the simulation by removing all ties and making all agents susceptible again.

##### Export tab #####
The export tab provides controls for network exports.
 * Drop-down list _Type_: sets the type of data for network exports.
    * _GEXF_: A detailed graph representation optimized for [Gephi](https://gephi.org/).
    * _Edge List_: A list of all connections between nodes.
    * _Adjacency Matrix_: A matrix of all nodes with a _1_ at the intersection of two nodes if a tie exists and _0_ otherwise.
 * If _GEXF_ and _Dynamic_ is selected as _Network type_:
    * Button __Start recording__: sets the starting point of dynamic network recordings.
    * Button __Stop recording__: sets the end point of dynamic network recordings and writes the final export file.
    * Button __Choose export file__: sets the file to export the data to. Please make sure to use _.gexf_ as file ending.
 * If _Edge list_, _Adjacency matrix_, or _GEXF_ and _Static_ is selected as _Network type_:
    * Button __Export__: sets and writes the file to export the data to. Please make sure to use _.txt_ as file ending for _Edge list_ and _Adjacency matrix_ exports, and _.gexf_ as file ending for _GEXF_ exports.

##### Network display #####
The network display displays the simulated network. Nodes are interactive. That is, they can be clicked (i.e., to show agent statistics, to toggle disease states, and/or to move them around).

#### Statistics window ####
The statistics window displays detailed information on the network and the simulation (_Global stats_) and on a single selected agent (_Agent stats_). Agent statistics can be selected by:
 1. Activating _Show agent stats_ for _One node click_ on the _Simulation_ tab of the main window.
 2. Clicking on a node in the network display.

## Using the _Data Generator_ version of the NIDM Simulator
The _Data Generator_ is used to generate and subsequently analyze large amounts of data for various parameter settings.

### Starting the _Data Generator_ ###
To start the _Data Generator_ you need to run the _CIDMDataGenerator.java_ file located in _path.to.nidm.simulator.project.folder/src/main/java/nl/uu/socnetid/nidm/mains/_. This can be done either in an IDE, such as Eclipse (for details see _Cloning the project and running it as a Maven project_), or through a terminal.

### Procedure ###
The _Data Generator_ procedure consists of two stages:
 1. _Data generation_ in which detailed network and disease data is being generated.
 2. _Data analysis_ (optional) in which the generated data is analyzed with standard methods stored in an R-script at __path.to.nidm.simulator.project.folder/analysis/analysis.R_.

### _Data Generator_ configuration ###
The _Data Generator_ uses a configuration file (_path.to.nidm.simulator.project.folder/src/main/resources/config.properties_) for all configurations. Comments indicate whether multiple values are allowed and how list elements need to be divided. The configuration file consists of three sections:
 1. _CIDM configuration_ to define the parameters of the CIDM model.
 2. _Data export configuration_ to define what types of data ought to be exported. Exported data files are stored in _path.to.nidm.simulator.project.folder/data/`date-time-of-data-generator-invocation`_. Possible exports are:
    * _export.summary_: creates a summary of a single simulation run (e.g., parameter settings, disease and network measures just before introducing a disease and at the end of the simulation, network and utility measures of the initially infected agent).
    * _export.summary.each.round_: creates a summary of each simulated round (e.g., parameter settings, network measures, disease states). __Note:__ This option may create very large amount of data!
    * _export.agent.details_: creates a detailed overview of each single agent (e.g., parameter settings, disease states, utilities, network measures) at the end of each simulated round. If activated _export.agent.details.reduced_ is ignored. __Note:__ This option may create very large amount of data!
    * _export.agent.details.reduced_: creates a detailed overview of each single agent (e.g., parameter settings, disease states, utilities, network measures) at the end of each simulation run. If _export.agent.details_ is activated _export.agent.details.reduced_ is ignored.
    * _export.gexf_: creates individual dynamic _.gexf_ files for each simulation run.
 3. _Data analysis configuration_ to configure and trigger (_analyze.data=true_) data analysis subsequent to data generation. Please make sure that the correct location of the _Rscript_ executable is set, if _analyze.data_ is set _true_. Analysis results are stored in _path.to.nidm.simulator.project.folder/data/`date-time-of-data-generator-invocation`_.




