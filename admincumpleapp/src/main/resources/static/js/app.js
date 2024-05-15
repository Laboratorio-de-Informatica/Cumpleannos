
var app = (function () {

    var urlAPI = "http://localhost:8080/"

    let _apiclient = urlAPI+"js/apiclient.js"

    function getAllPersons(){
        window.location.href=urlAPI+"persons.html";
    }

    function _table(classes){
        var listClasses = _map(classes);
        if (listClasses.length===0) {
            document.getElementById("table_footer").innerHTML = "No se encontraron personas";
        }
        else{
            $("#table_class > tbody").empty();
            listClasses.map(function(c){
                var onclick = "Modulesearch.getInfo(\""+c.id+"\")";
                var stri="'"+onclick+"'";
                $("#table_class > tbody").append(
                    "<tr onclick="+stri+" class='hoverRow' >" +
                    "<td>" + c.nombre+ "</td>"+
                    "<td>" + c.description + "</td>"+
                    "<td>" + c.fechaInicio + "</td>"+
                    "<td>" + c.fechaFin + "</td>"+
                    "<td>" + c.capacity + "</td>"+
                    "</tr>"
                );
            });
        }
    }

    return {
        getAllPersons:getAllPersons,
    };
})();