About Plugin:
	Bamboo is the continuous integration solution from Atlassian. It helps build application artifacts.
	
	This Plugin connects Bamboo to Continuous Delivery Automation (formerly known as Automic Release Automation).

	Thanks to Continuous Delivery Automation and the numerous tools that it integrates with, the new version of the business application behind the build artifact is rapidly installed and tested (compliance and security, functional quality and performance).

	
Features:

The plugin adds the following Task to Bamboo:

"Create CDA Package": Creates a Package in Continuous Delivery Automation.
	You can add the build artifact to the new Package
	Continuous Delivery Automation will then automatically deploy the artifact (configuration required)

Steps to Install Bamboo Plugin:
	
	1. Install Bamboo by following steps given in the link: https://confluence.atlassian.com/bamboo/installing-bamboo-on-windows-289276813.html
	2. Open a browser window and navigate to bamboo http://<computer_name_or_IP_address>:<port> E.g. (http://localhost:8085/bamboo). To log in, use the credentials: admin/admin
	3. Go to Settings --> Add-ons --> Upload app
		Upload the jar/obr file generated in the target folder of the myPlugin Project.
	4. You should see your plugin under User-Installed apps.

Setting up Development Environment:

	Install the Atlassian SDK
		1. Java should be installed on the system
		2. Download the latest version of the SDK and follow the prompts to install the SDK
		3. Verify the SDK is installed using the command atlas-version
	
	Create a plugin skeleton project
		1. Navigate to the directory on your system where you’d like to create your plugin.
		2. Create an add-on project using below command from a Command Prompt window. The command will create a folder with the plugin directories inside.
		   atlas-create-bamboo-plugin
		3. You will be prompted to provide some information about your plugin.
			* Define value for groupId: : com.atlassian.tutorial
			* Define value for artifactId: : myPlugin
			* Define value for version: 1.0.0-SNAPSHOT: : 1.0.0-SNAPSHOT
			* Define value for package: com.atlassian.tutorial: : com.atlassian.tutorial.myPlugin
		4. You will then be prompted to confirm your configuration. Verify the details to ensure they are correct, and then type Y once you are ready to proceed.
		5. The basic skeleton for your Atlassian JIRA plugin is created in a new myPlugin directory
		6. To make the plugin work in eclipse use command:
			atlas-mvn eclipse:eclipse
		
Bamboo Plugin depends on

	- Automation.Engine » AutomationEngine (minimum version Automation.Engine 12.2)
	- Bamboo Plugin Version » 6.7.0

