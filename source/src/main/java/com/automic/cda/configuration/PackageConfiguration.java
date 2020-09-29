package com.automic.cda.configuration;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;

import com.atlassian.bamboo.configuration.ConfigurationMap;
import com.atlassian.bamboo.task.CommonTaskContext;
import com.automic.cda.constants.BambooConstants;
import com.automic.cda.modal.Artifact;
import com.automic.cda.modal.Condition;
import com.automic.cda.modal.PackageComponent;
import com.automic.cda.modal.PackageDynamicProperty;
import com.automic.cda.modal.PackageProperty;

/**
 * 
 * @author yogitadalal
 *
 */
public final class PackageConfiguration {

	private String server;
	private String username;
	private String password;
	private String pkgName;
	private String pkgType;
	private String appName;
	private String folder;
	private String owner;
	private List<PackageProperty> props;
	private List<PackageDynamicProperty> dynProps;
	private List<PackageComponent> comps;
	private List<String> logs;

	@Autowired
	public PackageConfiguration(@NotNull CommonTaskContext taskContext) {
		ConfigurationMap cfm = taskContext.getConfigurationMap();
		this.server = cfm.get(BambooConstants.CDA_URL);
		this.username = cfm.get(BambooConstants.CDA_USERNAME);
		this.password = cfm.get(BambooConstants.CDA_PASSWRD);
		this.pkgName = cfm.get(BambooConstants.PKG_NAME);
		this.pkgType = cfm.get(BambooConstants.PKG_TYPE);
		this.appName = cfm.get(BambooConstants.APP_NAME);
		this.folder = cfm.get(BambooConstants.FOLDER_NAME);
		this.owner = cfm.get(BambooConstants.OWNER_NAME);
		this.comps = getComps(cfm);
		this.props = getProps(cfm);
		this.dynProps = getDynProps(cfm);
	}

	private List<PackageComponent> getComps(ConfigurationMap cfm) {
		List<PackageComponent> components = new ArrayList<>();

		for (int i = 1;; i++) {

			if (!cfm.containsKey(BambooConstants.COMPONENT_NAME + i)) {

				break;
			}
			PackageComponent component = new PackageComponent();
			component.setName(cfm.get(BambooConstants.COMPONENT_NAME + i));

			String artifactName = cfm.get(BambooConstants.ARTIFACT_NAME + i);
			String artifactSource = cfm.get(BambooConstants.ARTIFACT_SRC + i);
			String artifactCustomField = cfm.get(BambooConstants.ARTIFACT_FIELD + i);

			if (StringUtils.isNotBlank(artifactName) || StringUtils.isNotBlank(artifactSource)
					|| StringUtils.isNotBlank(artifactCustomField)) {

				component.setArtifact(new Artifact(artifactName, artifactSource, artifactCustomField));
			}

			ArrayList<Condition> conditions = new ArrayList<>();

			for (int j = 1;; j++) {
				if (!cfm.containsKey(BambooConstants.CONDITION_LOG + i + "_" + j)) {
					break;
				}
				String conditionBuildLog = cfm.get(BambooConstants.CONDITION_LOG + i + "_" + j);
				String conditionValue = cfm.get(BambooConstants.CONDITION_VALUE + i + "_" + j);
				if (StringUtils.isNotBlank(conditionBuildLog) || StringUtils.isNotBlank(conditionValue)) {
					conditions.add(new Condition(conditionBuildLog, conditionValue));
				}
			}

			component.setConditions(conditions);
			components.add(component);
		}
		return components;
	}

	private List<PackageProperty> getProps(ConfigurationMap cfm) {

		List<PackageProperty> properties = new ArrayList<>();
		for (int k = 1;; k++) {

			if (!cfm.containsKey(BambooConstants.PROPERTY_NAME + k)) {

				break;
			} else {

				String name = cfm.get(BambooConstants.PROPERTY_NAME + k);
				String value = cfm.get(BambooConstants.PROPERTY_VALUE + k);
				String type = cfm.get(BambooConstants.PROPERTY_TYPE + k);

				if (StringUtils.isNotBlank(name) || StringUtils.isNotBlank(value) || StringUtils.isNotBlank(type)) {

					properties.add(new PackageProperty(name, value, type));
				}
			}
		}
		return properties;
	}

	private List<PackageDynamicProperty> getDynProps(ConfigurationMap cfm) {
		List<PackageDynamicProperty> dynamicProperties = new ArrayList<>();
		for (int k = 1;; k++) {

			if (!cfm.containsKey(BambooConstants.DYN_PROPERTY_NAME + k)) {

				break;
			} else {

				String name = cfm.get(BambooConstants.DYN_PROPERTY_NAME + k);
				String value = cfm.get(BambooConstants.DYN_PROPERTY_VALUE + k);

				if (StringUtils.isNotBlank(name) || StringUtils.isNotBlank(value)) {

					dynamicProperties.add(new PackageDynamicProperty(name, value));
				}
			}
		}
		return dynamicProperties;
	}

	public List<PackageProperty> getProps() {
		return props;
	}

	public List<PackageDynamicProperty> getDynProps() {
		return dynProps;
	}

	public List<PackageComponent> getComps() {
		return comps;
	}

	public String getServer() {
		return server;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getPkgName() {
		return pkgName;
	}

	public String getPkgType() {
		return pkgType;
	}

	public String getAppName() {
		return appName;
	}

	public String getFolder() {
		return folder;
	}

	public String getOwner() {
		return owner;
	}

	public List<String> getLogs() {
		return logs;
	}

}
