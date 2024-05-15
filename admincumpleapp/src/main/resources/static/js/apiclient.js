var apiclient = (function () {

    var urlAPI = "http://localhost:8080/"

    function getAllUsers(callback){
        var data = $.ajax({
            url: urlAPI+"api/v1/persons/",
            type: "GET",
            success : function (data, text) {
                Swal.close()
                callback(data);
            },
            error : function (request) {
                Swal.close()
                Swal.fire({
                    icon: 'error',
                    title: 'Oops...',
                    text: request.responseText,
                })
            },
        });
        return data;
    }

    function shutdown(){
        var data = $.ajax({
            url: urlAPI+"actuator/shutdown",
            type: "POST",
            success : function (data) {
                var urlLog = urlAPI+"log.html"
                Swal.close()
                Swal.fire({
                    text: "Aplicacion terminada correctamente",
                    type: "success",
                }).then(function(){
                    location.href = "https://www.escuelaing.edu.co/es/"
                })
            },
            error : function (request) {
                Swal.close()
                Swal.fire({
                    icon: 'error',
                    title: 'Oops...',
                    text: request.responseText,
                })
            },
        });
        return data;
    }

    function cleanLogs(callback){
        var data = $.ajax({
            url: urlAPI+"api/v1/persons/logs",
            type: "DELETE",
            success : function (data, text) {
                Swal.close()
                Swal.fire({
                    text: "Logs eliminados correctamente",
                    type: "success",
                }).then(function(){
                    location.reload()
                })
            }
        });
        return data;
    }

    function uploadExcel(formData){
        var data = $.ajax({
            url: urlAPI+"api/v1/persons/excel",
            type: "POST",
            data: formData,
            enctype : 'multipart/form-data',
            contentType : false,
            cache : false,
            processData : false,
            success : function (data) {
                var urlLog = urlAPI+"log.html"
                Swal.close()
                Swal.fire({
                    text: "Archivo cargado correctamente",
                    type: "success",
                    footer: '<a href="http://localhost:8080/log.html">Ver logs</a>'
                }).then(function(){
                    location.href = urlAPI+"persons.html"
                })
            },
            error : function (request) {
                Swal.close()
                Swal.fire({
                    icon: 'error',
                    title: 'Oops...',
                    text: request.responseText,
                })
            },
        });
        return data;
    }

    function deleteExcel(formData){
        var data = $.ajax({
            url: urlAPI+"api/v1/persons/excel",
            type: "DELETE",
            data: formData,
            enctype : 'multipart/form-data',
            contentType : false,
            cache : false,
            processData : false,
            success : function (data) {
                var urlLog = urlAPI+"log.html"
                Swal.close()
                Swal.fire({
                    text: "Informacion eliminada correctamente",
                    type: "success",
                    footer: '<a href="http://localhost:8080/log.html">Ver logs</a>'
                }).then(function(){
                    location.href = urlAPI+"index.html"
                })
            },
            error : function (request) {
                Swal.close()
                Swal.fire({
                    icon: 'error',
                    title: 'Oops...',
                    text: request.responseText,
                })
            },
        });
        return data;
    }

    function deletePerson(person){
        var data = $.ajax({
            url: urlAPI+"api/v1/persons/",
            type: "DELETE",
            data: JSON.stringify(person),
            contentType: "application/json",
            success : function (data, text) {
                Swal.close()
                Swal.fire({
                    text: "Personas eliminadas correctamente",
                    type: "success"
                }).then(function(){
                    location.reload()
                })
            },
        });
        return data;
    }

    function getPersonById(personId, callback){
        var data = $.ajax({
            url: urlAPI+"api/v1/persons/personid",
            type: "POST",
            data: JSON.stringify(personId),
            contentType: "application/json",
            success : function (data) {
                callback(data);
            },
            error: function (request){

                Swal.fire({
                    icon: 'error',
                    title: 'Oops...',
                    text: request.responseText,
                })
            }

        });
        return data;
    }

    function updatePerson(oldPerson,updatedPerson){
        var data = $.ajax({
            url: urlAPI+"api/v1/persons",
            type: "PUT",
            data: JSON.stringify([oldPerson,updatedPerson]),
            contentType: "application/json",
            success : function (data) {
                Swal.fire({
                    text: "Persona actualizada correctamente",
                    type: "success"
                }).then(function(){
                    location.href = urlAPI+"profile.html?nombre="+data.personId.nombre+"&apellido="+data.personId.apellido+"&perfil="+data.personId.perfil
                })
            },
            error: function (request){
                Swal.fire({
                    icon: 'error',
                    title: 'Oops...',
                    text: request.responseText,
                })
            }

        });
        return data;
    }

    function deleteAllPersons(personsToDelete){
        console.log(personsToDelete)
        var data = $.ajax({
            url: urlAPI+"api/v1/persons/all",
            type: "DELETE",
            data: JSON.stringify(personsToDelete),
            contentType: "application/json",
            success : function (data) {
                Swal.close()
                Swal.fire({
                    text: "Personas eliminadas correctamente",
                    type: "success"
                }).then(function(){
                    location.reload()
                })
            },
            error: function (request){
                Swal.fire({
                    icon: 'error',
                    title: 'Oops...',
                    text: request.responseText,
                })
            }

        });
        return data;
    }

    return {
        getAllUsers:getAllUsers,
        deletePerson:deletePerson,
        getPersonById:getPersonById,
        updatePerson:updatePerson,
        uploadExcel:uploadExcel,
        deleteExcel:deleteExcel,
        deleteAllPersons:deleteAllPersons,
        cleanLogs:cleanLogs,
        shutdown:shutdown,
    };

})();