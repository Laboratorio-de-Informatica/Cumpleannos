var profile = (function () {

    let userToDelete = null;

    var urlAPI = "http://localhost:8080/"

    let _apiclient = urlAPI+"js/apiclient.js"

    var oldFoto = null;

    function GetURLParameter(sParam)
    {
        var sPageURL = window.location.search.substring(1);
        var sURLVariables = sPageURL.split('&');
        for (var i = 0; i < sURLVariables.length; i++)
        {
            var sParameterName = sURLVariables[i].split('=');
            if (sParameterName[0] === sParam)
            {
                return unescape(sParameterName[1]);
            }
        }
    }

    function loadData(){
        var nombre = GetURLParameter("nombre")
        var apellido = GetURLParameter("apellido")
        var perfil = GetURLParameter("perfil")
        var person = {
            "personId": {
                "nombre": nombre,
                "apellido": apellido,
                "perfil": perfil
            }
        };
        $.getScript(_apiclient,function(){
            apiclient.getPersonById(person.personId,fillData);
        });
    }

    function fileToByteArray(file) {
        return new Promise((resolve, reject) => {
            try {
                let reader = new FileReader();
                let fileByteArray = [];
                reader.readAsArrayBuffer(file);
                reader.onloadend = (evt) => {
                    if (evt.target.readyState == FileReader.DONE) {
                        let arrayBuffer = evt.target.result,
                            array = new Uint8Array(arrayBuffer);
                        for (byte of array) {
                            fileByteArray.push(byte);
                        }
                    }
                    resolve(fileByteArray);
                }
            }
            catch (e) {
                reject(e);
            }
        })
    }



    function fillData(data){

        oldFoto = data.foto
        document.getElementById("blobpng-image").src = "data:image/jpg;base64," + data.foto;
        document.getElementById("profile-nombre").value = data.personId.nombre
        document.getElementById("profile-apellido").value = data.personId.apellido
        var per =document.getElementById("perfil");
        var found = false;
        for (let i = 0; i < per.children.length; i++) {
            if (per.children.item(i).textContent.trim() === data.personId.perfil.trim()) {
                per.options.item(i).selected = 'selected';
                per.options.selectedIndex = i;
                found = true;
            }
        }
        if(!found){
            per.options.item(per.children.length-1).selected = 'selected';
            per.options.selectedIndex = per.children.length-1
            document.getElementById("perfil-otro").value = data.personId.perfil;
            document.getElementById("perfil-otro").readOnly = false;
        }
        document.getElementById("profile-dia").value = data.dia
        document.getElementById("profile-mes").value = data.mes
        document.getElementById("profile-observacion").value = data.observacion
        document.getElementById("profile-correo").value = data.correo
        document.getElementById("profile-linkedin").value = data.linkedin
        document.getElementById("profile-id").value = data.id
    }

    function deleteAll(){
        var emails = [];
        var table = document.getElementsByTagName('table')[0];
        var rows = table.rows;
        for (var i = 0; i < rows.length; i++) {
            var rowText = rows[i].firstChild.textContent;
            if (~rowText.indexOf('@')) { // If the content of the row contains a '@' character (This could be replaced with a regex check)
                // Also, I personally prefer to use '~' over '> -1' for indexOf(), but both would work.
                emails.push(rowText);
            }
        }
        console.log(emails);

    }

    function validateEmail(email) {
        var re = /\S+@\S+\.\S+/;
        return re.test(email);
    }

    function validateSelect() {
        var input = document.getElementById("perfil");
        var perfil = input.options[input.selectedIndex].value;
        var otroPerfil = document.getElementById("perfil-otro")
        if(perfil==="Otro"){
            otroPerfil.readOnly = false;
        }
        else{
            otroPerfil.innerText = "";
            otroPerfil.innerHTML = "";
            otroPerfil.readOnly = true;
        }
    }

    async function saveProfile(){
        var nombre = document.getElementById("profile-nombre").value
        var apellido = document.getElementById("profile-apellido").value
        var input = document.getElementById("perfil");
        var perfil = input.options[input.selectedIndex].value;
        if(perfil==="Otro"){
            var otroPerfil = document.getElementById("perfil-otro")
            perfil = otroPerfil.value;
        }
        var dia = document.getElementById("profile-dia").value
        var mes = document.getElementById("profile-mes").value
        var observacion = document.getElementById("profile-observacion").value
        var correo = document.getElementById("profile-correo").value
        var linkedin = document.getElementById("profile-linkedin").value
        var id = document.getElementById("profile-id").value
        var fotoFile = document.getElementById("input-newfoto").files[0]
        console.log(fotoFile)
        let foto = oldFoto;
        if(!(fotoFile === undefined)){
            foto = await fileToByteArray(fotoFile);
        }

        if (nombre === ""){
            Swal.fire({
                icon: 'error',
                title: 'Oops...',
                text: 'El nombre no puede estar vacio',
            })
        }
        else if (apellido === ""){
            Swal.fire({
                icon: 'error',
                title: 'Oops...',
                text: 'El apellido no puede estar vacio',
            })
        }
        else if (perfil === ""){
            Swal.fire({
                icon: 'error',
                title: 'Oops...',
                text: 'El perfil no puede estar vacio',
            })
        }
        else if (correo === ""){
            Swal.fire({
                icon: 'error',
                title: 'Oops...',
                text: 'El correo no puede estar vacio',
            })
        }
        else if (!validateEmail(correo)){
            Swal.fire({
                icon: 'error',
                title: 'Oops...',
                text: 'El formato del correo no es valido',
            })
        }

        else {

            var updatedPerson = {
                "personId": {
                    "nombre": nombre,
                    "apellido": apellido,
                    "perfil": perfil
                },
                "dia": dia,
                "mes": mes,
                "observacion": observacion,
                "correo": correo,
                "linkedin": linkedin,
                "id": id,
                "foto": foto
            }

            var oldPerson = {
                "personId": {
                    "nombre": GetURLParameter("nombre"),
                    "apellido": GetURLParameter("apellido"),
                    "perfil": GetURLParameter("perfil")
                },
            }

            Swal.fire({
                title: 'Estas seguro que actualizar el perfil de '+oldPerson.personId.nombre+ ' '+ oldPerson.personId.apellido,
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
                    $.getScript(_apiclient, function () {
                        apiclient.updatePerson(oldPerson,updatedPerson);
                    });
                } else if (result.isDenied) {
                    Swal.fire('La informacion no fue actualizada', '', 'info')
                }
            });
        }
    }

    return {
        loadData:loadData,
        saveProfile:saveProfile,
        validateSelect:validateSelect
    };
})();