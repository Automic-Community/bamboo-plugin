[#assign conditionIndex=1]
<style>
.component {

	position: relative;
    padding: 5%;
	width:80%;
	margin-bottom: 20px;
	box-shadow: 1px 1px 10px grey;
    -moz-box-shadow: 1px 1px 10px grey;
    -webkit-box-shadow: 1px 1px 10px grey;
}

.delete {
    position: absolute;
    float: right;
	text: white;
    top: -1px;
    right: 5%;
	border: transparent;
}
</style>
<script type="text/javascript">

var componentIndex = parseInt(AJS.$("#componentIndex").val());
var conditionIndex = parseInt(AJS.$("#conditionIndex").val());
	
function addComponent(elementId){
	
	AJS.$("#"+elementId).append(generateComponent(componentIndex));
	componentIndex++;
}

function removeComponent(index){
	
	AJS.$("#component_"+index).remove();           
}


function generateComponent(index) {
	
	return '<div id = "component_'+index+'" class="filed-group component">\
				<div class="field-group">\
					<label for="component_name_'+index+'">Name<span class="aui-icon icon-required content">(required)</span>\
					<span class="content"> (required)</span></label>\
					<input type="text" id= "component_name_'+index+'" name="component_name_' +index+'" class="text long-field">\
					<div class="description">The Name of Component you want to include in your package</div>\
				</div>\
				<div class="field-group">\
					<div id="conditions_'+index+'" class="filed-group"> </div>\
					<div class="field-group">\
					<button type = "button" class="button" onclick="addCondition('+index+')">Add Conditions</button>\
					</div>\
				</div>\
				<div id="artifact_'+index+'" class="field-group">\
				</div>\
				<div id="artifact_button_'+index+'" class="field-group">\
					<button type = "button" class="button" id="addArtifact_'+index+'" onclick="addArtifact('+index+')">Add Artifact</button>\
				</div>\
				<button type = "button" class="button delete" onclick="removeComponent('+index+')">X</button>\
			</div>';
}


function addArtifact(componentIndex){
	
	AJS.$("#artifact_" +componentIndex).append(generateArtifact(componentIndex));
	AJS.$("#addArtifact_" + componentIndex).remove();
}

function generateArtifact(componentIndex) {

	return '<div id="artifact_component_'+componentIndex+'" class="field-group component" style="padding:5%; width:80%; left:10%; margin-bottom: 10px;">\
				<div class="field-group">\
			 		<label class="label" for="artifact_source_'+componentIndex+'">Artifact Source<span class="aui-icon icon-required content">(required)</span>\
					<span class="content"> (required)</span></label>\
			 		<input type=text id="artifact_source_'+componentIndex+'" name="artifact_source_'+ componentIndex +'" class="text long-field">\
					<div class="description">Provide name of the artifact source from which artifact will be retrieved.</div>\
			 	</div>\
				<div class="field-group">\
				 	<label class="label" for="artifact_name">Artifact Name<span class="aui-icon icon-required content">(required)</span>\
					<span class="content"> (required)</span></label>\
			 		<input type=text id="artifact_name_'+componentIndex+'" name="artifact_name_'+ componentIndex +'" class="text long-field">\
					<div class="description">Provide name of the artifact to be included in the package.</div>\
			 	</div>\
				<div class="field-group">\
				 	<label class="label" for="artifact_custom_fields">Custom Fields</label>\
			 		<textarea rows="4" cols="50" type=textarea id="artifact_custom_field_'+ componentIndex +'" name="artifact_custom_field_'+ componentIndex +'" class="text long-field"></textarea>\
					<div class="description">Provide custom fields in valid JSON key value pairs. E.g {"source_path":"/ftp/mysample.war"}</div>\
				</div>\
				<button type="button" class="button delete" onclick="removeArtifact('+componentIndex+')">X</button>\
			</div>';
	}
	
function removeArtifact(elementId) {

	AJS.$("#artifact_component_"+elementId).remove();
	AJS.$("#artifact_button_"+elementId).append('<button type = "button" class="button" id="addArtifact_'+elementId+'" onclick="addArtifact('+elementId+', '+elementId+')">Add Artifact</button>')
}
function addCondition(componentIndex) {

	AJS.$("#conditions_" + componentIndex).append(generateCondition(componentIndex, conditionIndex));
	conditionIndex++;
}

function generateCondition(componentIndex, conditionIndex){

	return '<div id="condition_' +componentIndex +'_'+ conditionIndex +'" class="field-group component" style="padding:5%; width:80%; margin-bottom: 10px;">\
					<div class="field-group">\
						<label for="condition_build_log' +componentIndex +'_'+ conditionIndex + '" class="label"> Build Log</label>\
						<select class="select" id="condition_build_log_' +componentIndex +'_'+ conditionIndex +'" name="condition_build_log_' +componentIndex +'_'+ conditionIndex +'" >\
  							<option value="Contains">Contains</option>\
  							<option value="Does not contain"> Does not contain</option>\
						</select><div class="description">Specify whether existence or non existence of value will be checked in build log.</div>\
					</div>\
					<div class="field-group">\
						<label for="condition_value_' +componentIndex +'_'+ conditionIndex +'" class="label"> Value</label>\
						<input type="text" id="condition_value_' +componentIndex +'_'+ conditionIndex +'" name="condition_value_' +componentIndex +'_'+ conditionIndex +'" class="text long-field">\
						<div class="description">Specify the value that you want to search in the build log. Search is case insensitive and also supports regular expression.</div>\
					</div>\
					<button type = "button" class="button delete" onclick="removeCondition('+componentIndex+', '+conditionIndex+')" >X</button>\
			</div>';
}

function removeCondition( componentIndex,  elementId) {

	AJS.$("#condition_" + componentIndex+ "_" + elementId).remove();
}

</script>

<div class="field-group">
	<label for="components">Components</label>
	<div class="description">
	The component will be assigned to package when all the corresponding conditions are met.
	Note:If no component is specified, all the available components for the specified application will be added to the package</div>
	<div id="components" class="field-group">
		[#list components as component]
			<div id = "component_${component_index+1}" class="filed-group component">
				 	<div class="field-group">
				 		<label for="component_name_${component_index+1}">Name<span class="aui-icon icon-required content">(required)</span>
						<span class="content"> (required)</span></label>
						<input type="text" id= "component_name_${component_index+1}" name="component_name_${component_index+1}" class="text long-field" value="${component.name}">
						[#if !component.name?has_content]
							<div class="error control-form-error">You must specify the name of component</div>
						[/#if]
						<div class="description">The Name of Component you want to include in your package</div>
					</div>	

				<div id="conditions_${component_index+1}" class="field-group">
					[#list component.conditions as condition]
				 		<div id="condition_${component_index+1}_${conditionIndex}" class="field-group component" style="padding:5%; width:80%; margin-bottom:10px;">
							<div class="field-group">
								<label for="condition_build_log_${component_index+1}_${conditionIndex}" class="label"> Build Log</label>
								<select class="select" id="condition_build_log_${component_index+1}_${conditionIndex}" name="condition_build_log_${component_index+1}_${conditionIndex}">
  									[#if condition.buildLog == "Does not contain"]
										<option value="Contains">Contains</option>
										<option value="Does not contain" selected="true"> Does not contain</option>
									[#else]
										<option value="Contains" selected="true">Contains</option>
										<option value="Does not contain"> Does not contain</option>
									[/#if]
								</select>
								<div class="description">Specify whether existence or non existence of value will be checked in build log.</div>
							</div>
							<div class="field-group">
								<label for="condition_value_${component_index+1}_${conditionIndex}" class="label"> Value</label>
								<input type="text" id="condition_value_${component_index+1}_${conditionIndex}" name="condition_value_${component_index+1}_${conditionIndex}" class="text long-field" value="${condition.value}">
								<div class="description">Specify the value that you want to search in the build log. Search is case insensitive and also supports regular expression.</div>
							</div>
							<button type = "button" class="button delete" onclick="removeCondition(${component_index+1}, ${conditionIndex})">X</button> 
				   		</div>
						[#assign conditionIndex = conditionIndex+1]
					[/#list]
				</div>
				<div class="field-group">
				 	<button type = "button" class="button" onclick="addCondition(${component_index+1})">Add Conditions</button>
				</div>
				<div class="field-group">
				 	[#if component.artifact??]
					 	<div id="artifact_${component_index+1}">
				 			<div id="artifact_component_${component_index+1}" class="field-group component" style="padding:5%; width:80%; left:10%; margin-bottom:10px;"> 
							<div class="field-group">
				 				<label class="label" for="artifact_source">Artifact Source<span class="aui-icon icon-required content">(required)</span>
								<span class="content"> (required)</span></label>
				 				<input type=text id="artifact_source_${component_index+1}" name="artifact_source_${component_index+1}" class="text long-field" value="${component.artifact.source}">
								[#if !component.artifact.source?has_content]
									<div class="error control-form-error">You must specify the source of artifact</div>
								[/#if]
								<div class="description">Provide name of the artifact source from which artifact will be retrieved.</div>
							</div>
							<div class="field-group">
				 				<label class="label" for="artifact_name">Artifact Name<span class="aui-icon icon-required content">(required)</span>
								<span class="content"> (required)</span></label>
				 				<input type=text id="artifact_name_${component_index+1}" name="artifact_name_${component_index+1}" class="text long-field" value="${component.artifact.name}">
								[#if !component.artifact.name?has_content]
									<div class="error control-form-error">You must specify the name of artifact</div>
								[/#if] 
								<div class="description">Provide name of the artifact to be included in the package.</div>
							</div>
							<div class="field-group">
				 				<label class="label" for="artifact_custom_fields">Custom Fields</label>
				 				<textarea rows="4" cols="50" type="textarea" id="artifact_custom_field_${component_index+1}" name="artifact_custom_field_${component_index+1}" class="text long-field">${component.artifact.customField}</textarea>
								<div class="description">Provide custom fields in valid JSON key value pairs. E.g {"source_path":"/ftp/mysample.war"}</div> 
							</div>
							<button type="button" class="button delete" onclick="removeArtifact(${component_index+1})">X</button>
				 			</div>
						</div>
						<div id="artifact_button_${component_index+1}"></div>
				 	[#else]
				 		<div id="artifact_${component_index+1}" class="field-group container"></div>
						<div id="artifact_button_${component_index+1}">
				 			<button type="button" class="button" id="addArtifact_${component_index+1}" onclick="addArtifact(${component_index+1})">Add Artifact</button>
						</div>	 
				 	[/#if]
				</div>
				<button type = "button" class="button delete" onclick="removeComponent(${component_index+1})">X</button>
				[#assign componentIndex = component_index+2] 
			</div>
		[/#list]
	</div>
	<button type="button" class="button" onclick = "addComponent('components')">Add Component</button>
	<input type="hidden" id="componentIndex" value = ${componentIndex}>
	<input type="hidden" id="conditionIndex" value = ${conditionIndex}>
</div>
