var filter = (function () {

    var urlAPI = "http://localhost:8080/"

    let _apiclient = urlAPI+"js/apiclient.js"

    function doFilter() {
        // Declare variables
        var input, filter, table, tr, td, i, txtValue;
        input = document.getElementById("foto");
        var tieneFoto = input.options[input.selectedIndex].value;
        var carnet = document.getElementById("carnet").value.toUpperCase().trim();
        var nombre = document.getElementById("nombre").value.toUpperCase().trim();
        var apellido = document.getElementById("apellido").value.toUpperCase().trim();
        var dia = document.getElementById("dia").value.toUpperCase().trim();
        var mes = document.getElementById("mes").value.toUpperCase().trim();
        var correo = document.getElementById("correo").value.toUpperCase().trim();
        var perfil = document.getElementById("perfil").value.toUpperCase().trim();
        var observacion = document.getElementById("observacion").value.toUpperCase().trim();
        var inputs = [carnet,nombre,apellido,dia,mes,correo,perfil,observacion];


        table = document.getElementById('html-data-table');
        tr = table.getElementsByTagName("tr");
        var found = true;
        // Loop through all table rows, and hide those who don't match the search query
        for (i = 1; i < tr.length; i++) {
            td = tr[i].getElementsByTagName("td");
            found = true;
            for(var j = 0; j<td.length; j++){
                var td_temp = td[j];
                txtValue = td_temp.textContent || td_temp.innerText;
                if(inputs[j] && found){
                    found = txtValue.toUpperCase().trim().indexOf(inputs[j]) > -1;
                }
            }
            if (found) {
                var tdFoto = tr[i].getElementsByTagName("td")[9];
                if (tdFoto) {
                    txtValue = tdFoto.textContent || tdFoto.innerText;
                    if (tieneFoto === "todos"){
                        tr[i].style.display = "";
                    }
                    else {
                        if (tieneFoto === "si" && txtValue === "si") {
                            tr[i].style.display = "";
                        } else if (tieneFoto === "no" && txtValue === "no") {
                            tr[i].style.display = "";
                        } else {
                            tr[i].style.display = "none";
                        }
                    }
                }
            }
            else{
                tr[i].style.display = "none";
            }
        }
    }

    return {
        doFilter: doFilter,
    };
})();