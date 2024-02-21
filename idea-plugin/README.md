# Chutney Plugin for IntelliJ IDEA-based IDEs
<!-- Plugin description -->
This plugin provides support for Writing/Running Chutney scenarios files in IntelliJ IDEA based IDEs.
<!-- Plugin description end -->
![welcome](docs/Screenshot 2020-09-06 at 16.54.55.png)


## Getting started
####  Installing the plugin to IDEA
###### From a local build
1. Go to File -> Settings (IntelliJ IDEA -> Preferences) and select Plugins.
2. Click Install plugin from disc button and select the deployed plugin zip file. Please make sure to install the Zip file, not the extracted Jar files. This zip contains an additional library as well. Without this library, the plugin will not work properly.

![Install plugin from](docs/to_add.png)

3. Restart IDEA.

#### Setting up the project

In order to take part in Chutney plugin development, you need to:

1. Install IntelliJ IDEA 2019.3 or higher

2. Clone this repository to your computer

  ```
  $ git clone https://github.com/chutney-testing/chutney-idea-plugin.git
  ```

3. Open IntelliJ IDEA, select `File -> New -> Module from existing sources`, point to
the directory where Chutney plugin repository is and then import the Chutney-idea.iml.

4. When importing is finished, in order to run the plugin fix the Intellij Idea Plugin SDK 

![Intellij Idea Plugin SDK](docs/to_add.png)

4. Select the IDEA run configuration and select the `Run` or `Debug` button to build and start a development version
of IDEA with the Scala plugin.

![RUN](docs/to_add.png)

##  Features
#### Create a scenario file from menu

> use the **\*.chutney.json** as name pattern for your scenarios

#### Completion
###### Based on embedded JSON Schema
The plugin use custom Chutney JSON Schemas to enable code completion in your JSON Scenarios files and validate them.


###### Advanced completion

**targets**

**SpEL**


#### Folding
###### Tasks Folding


###### Targets Folding


#### Run A Scenario
###### Run Configuration
**Right click on a scenario**


**Edit Configuration ADD/EDIT**


**Configure the server**

You can use an embedded or a remote Chutney server.


> you can open the ChutneyConfig file from the IDE by invoking Open Chutney config file action to configure your targets

  
> The embedded server will run on a toolWindow wher you can see the logs

 **Show the results**

 **click to see the difference for assertions**


###### Run from gutter icon


#### Run All Scenarios in directory

In the same way you can run all scenarios on a directory.


#### Run Multiple Scenarios 

In the same way you can run multiple scenarios by selecting scenarios.



#### LiveTemplates

invoke [ctrl] + [j] and filter by cht-


#### Convert from HJSON to JSON
If you copy paste HJSON content the IDE will automatically transform it to json!

> if you want to paste without transformation click the cancel Button.

#### Actions to synchronise with a remote Chutney server

###### Configure your remote server

Configure your remote server on the setting(ctrl+alt+s) Tools>Chutney

###### Get All scenarios of a campaign
On an empty directory call the action **Get scenarios from Campaign**


###### Update remote scenario from local

###### Update local scenario from remote server

**notification** you can open the scenario on remote after successful update


###### Show diff between local scenario file and remote


###### Open remote scenario file in browser


###### Editor Notifications
When you open an Chutney Scenario File and if the scenario is different from the remote version, a notification with action is shown as below.

