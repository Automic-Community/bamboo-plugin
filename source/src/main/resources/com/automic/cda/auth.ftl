
[#macro authenticationSettings]

    [@ui.bambooSection]
        [@s.radio name='passwordAuthTypeSource' descriptionKey='cda.password.auth.type.source.description'
            listKey='key' listValue='value' toggle=true required=true list=passwordAuthTypeSources cssClass='radio-group' /]
        [@ui.bambooSection dependsOn="passwordAuthTypeSource" showOn="USER"]
            [@ww.textfield labelKey="cda.username" name="username_password" required="true"/]
            [@ww.password labelKey="cda.password" name="cdaPassword" /]
        [/@ui.bambooSection]
        [@ui.bambooSection dependsOn="passwordAuthTypeSource" showOn="SHARED_CREDENTIALS"]
            [#if stack.findValue("noUserPasswordSharedCredentials")!false]
                [@noCredentialsMessageBox id='sharedCredentials.infoBox'/]
            [#else]
                [@s.select id='passwordSharedCredentialsId' labelKey='cda.sharedCredentials' name='passwordSharedCredentialsId'
                list=userPasswordSharedCredentials listKey='first' listValue='second']
                    [@s.param name="headerKey"]-1[/@s.param]
                    [@s.param name="headerValue"][@s.text name='cda.sharedCredentials.default'/][/@s.param]
                [/@s.select]
            [/#if]
        [/@ui.bambooSection]
    [/@ui.bambooSection]

[/#macro]

[#macro noCredentialsMessageBox id]
    [@ui.messageBox type='info']
    <div id="${id}">
        <p>
            [#if fn.hasRestrictedAdminPermission()]
                    [@s.text name='sharedCredentials.info.noCredentialsDefined.admin']
                [@s.param][@s.url action='configureSharedCredentials' namespace='/admin/credentials'/][/@s.param]
            [/@s.text]
                [#else]
                [@s.text name='sharedCredentials.info.noCredentialsDefined.nonAdmin']
                    [@s.param][@s.url action='viewAdministrators' namespace=''/][/@s.param]
                [/@s.text]
            [/#if]
        </p>
        <p>
            [@s.text name='sharedCredentials.info.noCredentialsDefined.moreInfo']
                    [@s.param][@help.href pageKey="shared.credentials"/][/@s.param]
                [/@s.text]
        </p>
    </div>
    [/@ui.messageBox]
[/#macro]