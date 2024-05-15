var persons = (function () {

    var urlAPI = "http://localhost:8080/"

    let _apiclient = urlAPI+"js/apiclient.js"

    let userToDelete = null;

    function deleteAll() {
        Swal.fire({
            title: 'Estas seguro que deseas eliminar esta informacion?',
            showDenyButton: true,
            confirmButtonText: 'Yes',
            denyButtonText: 'No',
            customClass: {
                actions: 'my-actions',
                cancelButton: 'order-1 right-gap',
                confirmButton: 'order-2',
                denyButton: 'order-3',
            }
        }).then((result) => {
            if (result.isConfirmed) {
                Swal.fire({
                    title: "Eliminando informacion ....",
                    html: "Por favor espere un momento"
                })
                Swal.showLoading()
                var table = document.getElementById('html-data-table');
                var rows = table.rows;
                var persons_delete = []
                for (var i = 1; i < rows.length; i++) {
                    if (!(rows[i].style.display === "none")) {
                        var json = {
                            "personId": {
                                "nombre": rows[i].cells[1].textContent,
                                "apellido": rows[i].cells[2].textContent,
                                "perfil": rows[i].cells[6].textContent
                            }
                        }
                        persons_delete.push(json)
                    }
                }
                $.getScript(_apiclient, function () {
                    apiclient.deleteAllPersons(persons_delete);
                });
            } else if (result.isDenied) {
                Swal.fire('La informacion no fue eliminada', '', 'info')
            }
        });

    }

    function getPersons(){


        var deleteButton = document.getElementById("deleteallbutton");
        deleteButton.addEventListener("click", function() {
            deleteAll();
        });

        Swal.fire({
            title: "Cargando informacion de las personas registradas",
            html: "Por favor espere un momento"
        })
        Swal.showLoading()
        $.getScript(_apiclient,function(){
            apiclient.getAllUsers(_table);
        });
    }

    function deletePerson(person){
        var personToDelete = {
            "personId" : {
                "nombre": person.nombre,
                "apellido": person.apellido,
                "perfil": person.perfil
            }
        };
        Swal.fire({
            title: 'Estas seguro que deseas eliminar a '+ person.nombre + ' '+ person.apellido,
            showDenyButton: true,
            confirmButtonText: 'Yes',
            denyButtonText: 'No',
            customClass: {
                actions: 'my-actions',
                cancelButton: 'order-1 right-gap',
                confirmButton: 'order-2',
                denyButton: 'order-3',
            }
        }).then((result) => {
            if (result.isConfirmed) {
                $.getScript(_apiclient,function(){
                    apiclient.deletePerson(personToDelete);
                });
            } else if (result.isDenied) {
                Swal.fire('La informacion no fue eliminada', '', 'info')
            }
        });
    }

    function _map(list){
        var mapList = null;
        return mapList = list.map(function(user){
            return {
                nombre:user.personId.nombre,
                apellido:user.personId.apellido,
                dia:user.dia,
                mes:user.mes,
                correo:user.correo,
                id:user.id,
                perfil:user.personId.perfil,
                observacion:user.observacion,
                linkedin:user.linkedin,
                foto: user.foto
            };
        });
    }

    function _table(users){
        var listUsers= _map(users);
        if (listUsers.length===0) {
            document.getElementById("html-data-table").innerHTML = "No persons were found";
        }
        else{
            $("#html-data-table > tbody").empty();
            listUsers.map(function(u){
                if(!(u.foto)){
                    u.foto = "no"
                }
                else{
                    u.foto = "si"
                }
                // var onclick = "Modulesearch.getInfo(\""+c.id+"\")";
                var stri="'"+onclick+"'";
                var delId = ("del"+u.nombre+u.apellido+u.perfil).replaceAll(' ','')
                var editId = ("edit"+u.nombre+u.apellido+u.perfil).replaceAll(' ','')
                $("#html-data-table > tbody").append(
                    "<tr onclick="+stri+" class='hoverRow' >" +
                    "<td>" + u.id + "</td>"+
                    "<td>" + u.nombre+ "</td>"+
                    "<td>" + u.apellido + "</td>"+
                    "<td>" + u.dia + "</td>"+
                    "<td>" + u.mes + "</td>"+
                    "<td>" + u.correo + "</td>"+
                    "<td>" + u.perfil + "</td>"+
                    "<td>" + u.observacion + "</td>"+
                    "<td style=\"visibility:collapse; display:none;\">" + u.linkedin + "</td>"+
                    "<td style=\"visibility:collapse; display:none;\">" + u.foto + "</td>"+
                    "<td>" + "<div>"+ "<button id =" + delId + ">eliminar" +"</button> " +
                    "<button id =" + editId + ">editar" +"</button> "+
                    "</tr>"
                );
                var deleteButton = document.getElementById(delId);
                deleteButton.addEventListener("click", function() {
                    deletePerson(u,true)
                });

                var editButton = document.getElementById(editId);
                editButton.addEventListener("click", function() {
                    location.href =  urlAPI+'profile.html?nombre='+u.nombre+'&apellido='+u.apellido+'&perfil='+u.perfil;
                });

            });
        }
    }

    return {
        getPersons:getPersons,
        deletePerson:deletePerson,
        userToDelete:userToDelete
    };
})();