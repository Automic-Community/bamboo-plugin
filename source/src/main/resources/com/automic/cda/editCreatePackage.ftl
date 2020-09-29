<!DOCTYPE HTML>

[#include "/com/automic/cda/editCdaServer.ftl"]

[@ww.textfield labelKey="package.name" name="pkgName" cssClass="long-field" required='true'/]
[@ww.textfield labelKey="package.type" name="pkgType" cssClass="long-field" required='true'/]
[@ww.textfield labelKey="application.name" name="appName" cssClass="long-field" required='true'/]
[@ww.textfield labelKey="folder.name" name="folderName" cssClass="long-field" required='true'/]
[@ww.textfield labelKey="owner.name" name="ownerName" cssClass="long-field"/]

[#include "/com/automic/cda/addComponent.ftl"]
[#include "/com/automic/cda/addProperties.ftl"]
