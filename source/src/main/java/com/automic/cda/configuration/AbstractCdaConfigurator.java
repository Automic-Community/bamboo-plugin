package com.automic.cda.configuration;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.bamboo.collections.ActionParametersMap;
import com.atlassian.bamboo.credentials.ConfigurableSharedCredentialDepender;
import com.atlassian.bamboo.credentials.CredentialsAccessor;
import com.atlassian.bamboo.credentials.CredentialsData;
import com.atlassian.bamboo.credentials.UsernamePasswordCredentialType;
import com.atlassian.bamboo.task.AbstractTaskConfigurator;
import com.atlassian.bamboo.task.TaskDefinition;
import com.atlassian.bamboo.util.BambooIterables;
import com.atlassian.bamboo.utils.Pair;
import com.atlassian.bamboo.utils.error.ErrorCollection;
import com.automic.cda.constants.BambooConstants;
import com.automic.cda.util.BambooValidationUtility;
import com.automic.cda.util.CdaProperties;
import com.automic.cda.util.CommonUtil;
import com.automic.cda.util.RestServiceUtility;
import com.automic.cda.ws.rest.exceptions.BambooCdaRuntimeException;
import com.sun.jersey.api.client.ClientHandlerException;

public abstract class AbstractCdaConfigurator extends AbstractTaskConfigurator implements ConfigurableSharedCredentialDepender {
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCdaConfigurator.class);
	protected static final CdaProperties CDA_PROPERTY = CdaProperties.getInstance();
	public static final String PASSWRD_AUTH_CREDENTIALS_SOURCE = "passwordAuthTypeSource";
	public static final String PASSWRD_SHARED_CREDENTIALS_ID = "passwordSharedCredentialsId";
	private static final String PASSWRD_AUTH_TYPE_SOURCES = "passwordAuthTypeSources";
	private static final String USER_PASSWRD_SHARED_CREDENTIALS = "userPasswordSharedCredentials";
	private static final String NO_USER_PASSWRD_SHARED_CREDENTIALS = "noUserPasswordSharedCredentials";
	public static final String SHARED_CREDENTIALS_ID = "sharedCredentialsId";

	private static final String EMPTY_SHARED_CREDENTIALS_ID = "-1";

	final CredentialsAccessor credentialsAccessor;

	
	public AbstractCdaConfigurator( final CredentialsAccessor credentialsAccessor) {
		this.credentialsAccessor = credentialsAccessor;
	}

	public enum AuthCredentialsSource {
		USER("cda.authentication.source.user"), SHARED_CREDENTIALS("cda.authentication.source.shared.credentials");

		private final String labelKey;

		AuthCredentialsSource(String labelKey) {
			this.labelKey = labelKey;
		}

		public String getKey() {
			return name();
		}

		public String getLabelKey() {
			return labelKey;
		}
	}

	@Override
	public void populateContextForCreate(@NotNull final Map<String, Object> context) {
		super.populateContextForCreate(context);
		populateCommonAuthSettings(context);
		context.put(PASSWRD_AUTH_CREDENTIALS_SOURCE, AuthCredentialsSource.USER);
	}

	private void populateCommonAuthSettings(@NotNull Map<String, Object> context) {
		context.put(PASSWRD_AUTH_TYPE_SOURCES, getAuthCredentialsSources(AuthCredentialsSource.USER, AuthCredentialsSource.SHARED_CREDENTIALS));
		fillSharedCredentialsData(context, UsernamePasswordCredentialType.PLUGIN_KEY, USER_PASSWRD_SHARED_CREDENTIALS, NO_USER_PASSWRD_SHARED_CREDENTIALS);
	}

	private void fillSharedCredentialsData(@NotNull Map<String, Object> context, String pluginKey, String settingsKey, String noSharedCredentialsKey) {
		final List<Pair<String, String>> userPasswordSharedCredentials = getSharedCredentials(pluginKey);
		if (userPasswordSharedCredentials.isEmpty()) {
			context.put(noSharedCredentialsKey, true);
		} else {
			context.put(settingsKey, userPasswordSharedCredentials);
		}
	}

	private List<Pair<String, String>> getSharedCredentials(@NotNull final String pluginKey) {
		return BambooIterables.stream(credentialsAccessor.getAllCredentials()).filter(credentialsData -> pluginKey.equals(credentialsData.getPluginKey()))
				.map(credentials -> Pair.make(String.valueOf(credentials.getId()), credentials.getName())).collect(Collectors.toList());
	}

	private Map<String, String> getAuthCredentialsSources(AuthCredentialsSource... source) {
		final Map<String, String> result = new LinkedHashMap<>();
		for (AuthCredentialsSource credentialsSource : source) {
			result.put(credentialsSource.name(), CDA_PROPERTY.getProperty(credentialsSource.getLabelKey()));
		}
		return result;
	}

	@Override
	public void populateContextForEdit(@NotNull final Map<String, Object> context, @NotNull final TaskDefinition taskDefinition) {
		super.populateContextForEdit(context, taskDefinition);
		context.put(BambooConstants.CDA_URL, taskDefinition.getConfiguration().get(BambooConstants.CDA_URL));
		populateAuthorizationSettings(context, taskDefinition);

	}

	private void populateAuthorizationSettings(@NotNull Map<String, Object> context, @NotNull TaskDefinition taskDefinition) {
		final String username = taskDefinition.getConfiguration().get(BambooConstants.CDA_USERNAME);
		final String sharedCredentialsId = taskDefinition.getConfiguration().getOrDefault(SHARED_CREDENTIALS_ID, EMPTY_SHARED_CREDENTIALS_ID);
		if (isSharedCredentialsNotSet(sharedCredentialsId)) {
			context.put(BambooConstants.CDA_USERNAME, username);
			context.put(PASSWRD_AUTH_CREDENTIALS_SOURCE, AuthCredentialsSource.USER.getKey());
		} else {
			context.put(PASSWRD_AUTH_CREDENTIALS_SOURCE, AuthCredentialsSource.SHARED_CREDENTIALS.getKey());
			context.put(PASSWRD_SHARED_CREDENTIALS_ID, sharedCredentialsId);
		}

		context.put(BambooConstants.CDA_PASSWRD, taskDefinition.getConfiguration().get(BambooConstants.CDA_PASSWRD));
		populateCommonAuthSettings(context);
	}

	@NotNull
	@Override
	public Map<String, String> generateTaskConfigMap(@NotNull final ActionParametersMap params, @Nullable final TaskDefinition previousTaskDefinition) {

		final Map<String, String> config = super.generateTaskConfigMap(params, previousTaskDefinition);

		config.put(BambooConstants.CDA_URL, CommonUtil.trimmedValue(params.getString(BambooConstants.CDA_URL)));
		populateTaskConfigForPasswordAuthentication(params, config);
		return config;
	}

	private void populateTaskConfigForPasswordAuthentication(@NotNull ActionParametersMap params, @NotNull final Map<String, String> config) {
		String authTypeSource = params.getString(PASSWRD_AUTH_CREDENTIALS_SOURCE, "");
		final String sharedCredentialsId = params.getString(PASSWRD_SHARED_CREDENTIALS_ID, EMPTY_SHARED_CREDENTIALS_ID);
		if (AuthCredentialsSource.USER.getKey().equals(authTypeSource)) {
			config.put(BambooConstants.CDA_USERNAME, params.getString(BambooConstants.CDA_USERNAME));
			config.put(BambooConstants.CDA_PASSWRD, params.getString(BambooConstants.CDA_PASSWRD));
		} else if (AuthCredentialsSource.SHARED_CREDENTIALS.getKey().equals(authTypeSource) && !isSharedCredentialsNotSet(sharedCredentialsId)) {
			config.put(SHARED_CREDENTIALS_ID, sharedCredentialsId);
		}
	}

	@Override
	public void validate(@NotNull final ActionParametersMap params, @NotNull final ErrorCollection errorCollection) {
		LOGGER.info("validating server Details");
		super.validate(params, errorCollection);
		boolean valid = true;
		String serverName = CommonUtil.trimmedValue(params.getString(BambooConstants.CDA_URL));
		String serverErrMsg = BambooValidationUtility.validateServerURL(serverName);
		if (serverErrMsg != null) {
			valid = false;
			errorCollection.addError(BambooConstants.CDA_URL, serverErrMsg);
		}
		String userError = validateUserPasswordChoice(params, errorCollection);

		if (userError != null) {
			valid = false;
		}
		String tempPass = params.getString(BambooConstants.CDA_PASSWRD);
		String tempUser = params.getString(BambooConstants.CDA_USERNAME);
		String authTypeSource = params.getString(PASSWRD_AUTH_CREDENTIALS_SOURCE, "");

		if (valid && AuthCredentialsSource.USER.getKey().equals(authTypeSource) && CommonUtil.isNotEmpty(tempUser)) {
			try (RestServiceUtility ws = new RestServiceUtility(serverName, tempUser, tempPass)) {
				if (!ws.validateCredentails()) {
					errorCollection.addError(BambooConstants.CDA_PASSWRD, "Invalid Connection Parameters");
				}
			} catch (ClientHandlerException | BambooCdaRuntimeException e) {
				errorCollection.addError(BambooConstants.CDA_PASSWRD, "Check connection Parameters: " + e.getMessage());
			}
		}

	}

	private String validateUserPasswordChoice(@NotNull ActionParametersMap params, @NotNull ErrorCollection errorCollection) {
		final AuthCredentialsSource authCredentialsSource = getAuthTypeSource(params, PASSWRD_AUTH_CREDENTIALS_SOURCE);
		String errMsg = null;
		if (authCredentialsSource == AuthCredentialsSource.USER) {
			errMsg = validateUsername(BambooConstants.CDA_USERNAME, params, errorCollection);
		} else if (authCredentialsSource == AuthCredentialsSource.SHARED_CREDENTIALS) {
			errMsg = validateSharedCredentialsOption(params, PASSWRD_SHARED_CREDENTIALS_ID, errorCollection);
		} else {
			errMsg = CDA_PROPERTY.getProperty("cda.auth.source.required");
			errorCollection.addError(PASSWRD_AUTH_CREDENTIALS_SOURCE, errMsg);
		}
		return errMsg;
	}

	@Nullable
	private AuthCredentialsSource getAuthTypeSource(@NotNull ActionParametersMap params, String key) {
		final String authTypeSourceRaw = params.getString(key);
		final AuthCredentialsSource authCredentialsSource;
		if (authTypeSourceRaw != null) {
			authCredentialsSource = AuthCredentialsSource.valueOf(authTypeSourceRaw);
		} else {
			authCredentialsSource = null;
		}
		return authCredentialsSource;
	}

	private String validateUsername(String usernameFieldName, @NotNull ActionParametersMap params, @NotNull ErrorCollection errorCollection) {
		String username = params.getString(usernameFieldName);
		String errorMsg = BambooValidationUtility.validateUserName(username);
		if (errorMsg != null) {
			errorCollection.addError(usernameFieldName, errorMsg);
		}
		return errorMsg;
	}

	private boolean isSharedCredentialsNotSet(String sharedCredentialsId) {
		return EMPTY_SHARED_CREDENTIALS_ID.equals(sharedCredentialsId);
	}

	private String validateSharedCredentialsOption(ActionParametersMap params, String key, @NotNull ErrorCollection errorCollection) {
		final String sharedCredentialsId = params.getString(key);
		String errMsg = null;
		if (StringUtils.isEmpty(sharedCredentialsId)) {
			errMsg = CDA_PROPERTY.getProperty("cda.sharedCredentials.default");
			errorCollection.addError(key, errMsg);
		} else {
			if (isSharedCredentialsNotSet(sharedCredentialsId)) {
				errMsg = CDA_PROPERTY.getProperty("cda.sharedCredentials.default");
				errorCollection.addError(key, errMsg);
			} else {
				final CredentialsData credentials = credentialsAccessor.getCredentials(NumberUtils.toInt(sharedCredentialsId, -1));
				if (credentials == null) {
					errMsg = CDA_PROPERTY.getProperty("cda.shared.credentials.not.found" + Collections.singletonList(sharedCredentialsId));
					errorCollection.addError(key, errMsg);
				}
			}
		}
		return errMsg;
	}

	@NotNull
	@Override
	public Iterable<Long> getSharedCredentialIds(@NotNull Map<String, String> configuration) {
		if (configuration.containsKey(BambooConstants.SHARED_CREDENTIALS_ID)) {
			try {
				final long sharedCredentialsId = Long.parseLong(configuration.getOrDefault(BambooConstants.SHARED_CREDENTIALS_ID, "-1"));
				return Collections.singletonList(sharedCredentialsId);
			} catch (NumberFormatException e) {
				//
			}
		}
		return Collections.emptyList();
	}

}
