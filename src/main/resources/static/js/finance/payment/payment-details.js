$(function () {

    $.get("/project/project_id",{"id":152},function (data) {
        console.log(data);
        showDataByObject(data);
    },"json")


});

function showDataByObject(object) {
    $("[name]").each(function (i,ele) {
        let key = $(ele).attr("name");
        let value = getValueByKey(object, key);
        console.log(value);
        $(ele).val(value);
    })
}

function getValueByKey(object,key) {
    if(key.indexOf(".") === -1){
        if (object.hasOwnProperty(key)) {
            return object[key];
        }
    }
    let split = key.split(".");
    let tempObj = object[split[0]];
    if(tempObj === null){
        return "";
    }
    console.log(split);
    for (let i = 1; i < split.length; i++) {
        var tempKey = split[i];
        if (i === split.length - 1){
            return tempObj[tempKey] === undefined ? "" : tempObj[tempKey];
        }
        tempObj = tempObj[tempKey];
        console.log(tempObj);
    }

    return "";
}
