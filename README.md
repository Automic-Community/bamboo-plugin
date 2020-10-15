## Getting Started:


###### Description
	
Bamboo is the continuous integration solution from Atlassian. It helps build application artifacts.
	
This Plugin connects Bamboo to Continuous Delivery Automation (formerly known as Automic Release Automation).

Thanks to Continuous Delivery Automation and the numerous tools that it integrates with, the new version of the business application behind the build artifact is rapidly installed and tested (compliance and security, functional quality and performance).

	
###### Features:

The plugin adds the following Task to Bamboo:

"Create CDA Package": Creates a Package in Continuous Delivery Automation.
	You can add the build artifact to the new Package
	Continuous Delivery Automation will then automatically deploy the artifact (configuration required)

Steps to Install Bamboo Plugin:
	
1. Install Bamboo by following steps given in the link: https://confluence.atlassian.com/bamboo/installing-bamboo-on-windows-289276813.html
2. Open a browser window and navigate to bamboo http://<computer_name_or_IP_address>:<port> E.g. (http://localhost:8085/bamboo). To log in, use the credentials
3. Create Bamboo Plugin Artifact Run the Maven command 'mvn clean package' in the directory which contains pom.xml : (bamboo-plugin/)
3. Go to Settings --> Add-ons --> Upload app
		Upload the obr file generated in the target folder of the myPlugin Project.
4. You should see your plugin under User-Installed apps.

###### Setting up Development Environment:
    
  Install the Atlassian SDK on a [Windows](https://developer.atlassian.com/server/framework/atlassian-sdk/install-the-atlassian-sdk-on-a-windows-system/) or [Linux or Mac](https://developer.atlassian.com/server/framework/atlassian-sdk/install-the-atlassian-sdk-on-a-linux-or-mac-system/) system
		
  1. Java should be installed on the system
  2. Download the latest version of the SDK and follow the prompts to install the SDK
  3. Verify the SDK is installed using the command atlas-version
	
		
###### Bamboo Plugin depends on

- Automation.Engine » AutomationEngine (minimum version Automation.Engine 12.2)
- Bamboo Plugin Version » 6.7.0

