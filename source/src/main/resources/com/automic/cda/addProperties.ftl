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

td {
    padding: 5px;
}
tr {
    border-bottom: 1px solid gainsboro;
}

tr:nth-child(even) {
    background-color: ghostwhite;
}
</style>
<script type="text/javascript">

var propertyIndex = parseInt(AJS.$("#propertyIndex").val());
var dPropertyIndex = parseInt(AJS.$("#dPropertyIndex").val());

function generateProperty(index) {

    return '<tr id="property_'+index+'">\
                    <td><input id="property_type_'+index+'" type="text" class="text" name="property_type_'+index+'"></td>\
                    <td><input id="property_name_'+index+'" type="text" class="text" name="property_name_'+index+'"></td>\
                    <td><input id="property_value_'+index+'" type="text" class="text" name="property_value_'+index+'"></td>\
                    <td><button class="aui-button aui-button-link" onclick="removeProperty('+index+')">Remove</button></td>\
                </tr>'
} 

function addProperty() {

    var isTypeValid = AJS.$("#property_type").filter(function (){
                            return AJS.$.trim(AJS.$(this).val()).length === 0
                        }).length === 0;
    var isNameValid = AJS.$("#property_name").filter(function (){
                          return AJS.$.trim(AJS.$(this).val()).length === 0
                        }).length === 0;

    AJS.$("#proeperty_type_err_msg").hide();
    AJS.$("#proeperty_name_err_msg").hide();

    if(isTypeValid && isNameValid) {
 
    AJS.$("#properties").append(generateProperty(propertyIndex));
    AJS.$("#property_type_" + propertyIndex).val(AJS.$("#property_type").val());
    AJS.$("#property_name_" + propertyIndex).val(AJS.$("#property_name").val());
    AJS.$("#property_value_" + propertyIndex).val(AJS.$("#property_value").val());
    AJS.$("#property_type").val("");
    AJS.$("#property_name").val("");
    AJS.$("#property_value").val("");
    propertyIndex++;
    } 
    if(!isTypeValid) {

        AJS.$("#proeperty_type_err_msg").show();
    } 
    if(!isNameValid) {   
        AJS.$("#proeperty_name_err_msg").show();
    }
}

function removeProperty(elementId) {

    AJS.$("#property_"+elementId).remove();
}

function generateDynamicProperty(index) {

    return  '<tr id="d_property_'+index+'">\
                <td><input type="text" class="text" id ="d_property_name_'+index+'" name="d_property_name_'+index+'"></td>\
                <td><input type="text" class="text" id ="d_property_value_'+index+'" name="d_property_value_'+index+'"></td>\
                <td><button class="aui-button aui-button-link" onclick="removeDynamicProperty('+index+')">Remove</button></td>\
            </tr>'
} 

function addDynamicProperty(elementId) {

    var isNameValid = AJS.$("#d_property_name").filter(function (){
                            return AJS.$.trim(AJS.$(this).val()).length === 0
    }).length ===0;

    AJS.$("#d_proeperty_name_err_msg").hide();

    if(isNameValid) {
 
    AJS.$("#d_properties").append(generateDynamicProperty(dPropertyIndex));
    AJS.$("#d_property_name_" + dPropertyIndex).val(AJS.$("#d_property_name").val());
    AJS.$("#d_property_value_" + dPropertyIndex).val(AJS.$("#d_property_value").val());
    AJS.$("#d_property_name").val("");
    AJS.$("#d_property_value").val("");
    dPropertyIndex++;
    } 
    if(!isNameValid) {   
        AJS.$("#d_proeperty_name_err_msg").show();
    }
}

function removeDynamicProperty(elementId) {

    AJS.$("#d_property_"+elementId).remove();
}

</script>

[#assign propertyIndex = 1]
[#assign dPropertyIndex =1]

<div class="field-group">
    <label for="properties">Properties</label>
    <div class="description">The properties will be set on the package. Property type and property name must be specified. Type of the property can either be Link or Others.</div>
    <div class="field-group">
        <table>
            <thead>
                <tr>
                    <th>Type of Property</th>
                    <th> Property Name</th>
                    <th>Value</th>
                    <th></th>
                </tr>
            </thead>
            <tbody id="properties">
                <tr id="property">
                    <td>
                        <input id="property_type" type="text" class="text">
	    		        <div id="proeperty_type_err_msg" class="error control-form-error" style="display: none;">You must specify the type of property</div>
	    	        </td>
                    <td>
                        <input id="property_name" type="text" class="text">
                        <div id="proeperty_name_err_msg" class="error control-form-error" style="display: none;">You must specify the name of property</div>
	    	        </td>
                    <td>
                        <input id="property_value" type="text" class="text">
                    </td>
                    <td>
                        <button type="button" class="button button-custom" onclick = "addProperty()">Add</button>
                    </td>    
                </tr>
                [#list properties as property]
                    <tr id="property_${property_index+1}">
                        <td>
                            <input type="text" class="text" id="property_type_${property_index+1}" name="property_type_${property_index+1}" value="${property.type}">
                             [#if !property.type?has_content]
	    			            <div class="error control-form-error">You must specify the type of property</div>
	    	                [/#if]
                        </td> 
                        <td>
                            <input type="text" class="text" id="property_name_${property_index+1}" name="property_name_${property_index+1}" value="${property.name}">
                            [#if !property.name?has_content]
	    			            <div class="error control-form-error">You must specify the name of property</div>
	    	                [/#if]
                        </td>  
                        <td>
                            <input type="text" class="text" id="property_value_${property_index+1}" name="property_value_${property_index+1}" value="${property.value}">
                        </td> 
                        <td>
                            <button type="button" class="aui-button aui-button-link" onclick="removeProperty(${property_index+1})">Remove</button>
                        </td>    
                    </tr>
                    [#assign propertyIndex = propertyIndex+1]
                [/#list]
            </tbody>
        </table>
    </div>
</div>
<div class="field-group">
    <label for="d_properties">Dynamic Properties</label>
    <div class="description">The dynamic property will be added to the package. Property name and property value must be specified. Property name must be in fully qualified name format ( example /db/user or /user ).</div>
    <div class="field-group">
        <table>
            <thead>
                <tr>
                    <th>Property Name</th>
                    <th>Property Value</th>
                    <th></th>
                </tr>
            </thead>
            <tbody id="d_properties">
                <tr id="d_property">
                    <td>
                        <input id="d_property_name" type="text" class="text">
                        <div id="d_proeperty_name_err_msg" class="error control-form-error" style="display: none;">You must specify the name of property</div>
                    </td>
                    <td>
                        <input id="d_property_value" type="text" class="text">
                    </td>
                    <td>
                        <button type="button" class="button button-custom" onclick = "addDynamicProperty(${dPropertyIndex})">Add</button>
                    </td>
                </tr>
                [#list dynamicProperties as property]
                    <tr id="d_property_${property_index+1}">
                        <td>
                            <input id="d_property_name_${property_index+1}" type="text" class="text" name="d_property_name_${property_index+1}" value="${property.name}">
                            [#if !property.name?has_content]
    	    	    	        <div class="error control-form-error">You must specify the name of property</div>
    	    	            [/#if]
                        </td>
                        <td>
                            <input id="d_property_value_${property_index+1}" type="text" class="text" name="d_property_value_${property_index+1}" value="${property.value}">
                        </td>
                        <td>
                            <button class="aui-button aui-button-link" onclick="removeDynamicProperty(${property_index+1})">Remove</button>
                        </td>
                    </tr>
                    [#assign dPropertyIndex = dPropertyIndex+1]
                [/#list]
            </tbody>
        </table>
    </div>
</div>

<input type="hidden" id="propertyIndex" value="${propertyIndex}">
<input type="hidden" id="dPropertyIndex" value="${dPropertyIndex}">