var files = (function () {

    var urlAPI = "http://localhost:8080/"

    let _apiclient = urlAPI+"js/apiclient.js"

    function exportReportToExcel() {
        var table = document.getElementById('html-data-table'); // id of table
        var elt = table.cloneNode(true);
        var row = elt.rows;
        for (var j = 0; j < row.length; j++) {
            if(j==0){
                row[j].innerHTML = '<td>carnet</td>';
                row[j].innerHTML = row[j].innerHTML + '<td>nombre</td>';
                row[j].innerHTML = row[j].innerHTML + '<td>apellido</td>';
                row[j].innerHTML = row[j].innerHTML + '<td>dia</td>';
                row[j].innerHTML = row[j].innerHTML + '<td>mes</td>';
                row[j].innerHTML = row[j].innerHTML + '<td>correo</td>';
                row[j].innerHTML = row[j].innerHTML + '<td>perfil</td>';
                row[j].innerHTML = row[j].innerHTML + '<td>observacion</td>';
                row[j].innerHTML = row[j].innerHTML + '<td>linkedin</td>';
            }
            if(j>0){
                row[j].deleteCell(10);
                row[j].deleteCell(9);
            }
        }
        var ws = XLSX.utils.table_to_sheet(elt);
        var wb = XLSX.utils.book_new();
        XLSX.utils.book_append_sheet(wb, ws, "Sheet 1");
        XLSX.writeFile(wb, ('datos.' + 'xlsx'));
    }

    function uploadExcelFile(){
        Swal.fire({
            title: 'Estas seguro que quieres importar esta informacion?',
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
                    title: "Cargando informacion ....",
                    html: "Por favor espere un momento"
                })
                Swal.showLoading()
                let excel = document.getElementById("input-excel").files[0];
                let formData = new FormData();
                formData.append("excelFile", excel);
                console.log(formData.getAll("excelFile"))
                $.getScript(_apiclient,function(){
                    apiclient.uploadExcel(formData);
                });
            } else if (result.isDenied) {
                location.reload()
            }
        })

    }

    function deleteExcelFile(){
        Swal.fire({
            title: 'Estas seguro que quieres eliminar esta informacion?',
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
                let excel = document.getElementById("input-excel").files[0];
                let formData = new FormData();
                formData.append("excelFile", excel);
                console.log(formData.getAll("excelFile"))
                $.getScript(_apiclient,function(){
                    apiclient.deleteExcel(formData);
                });
            } else if (result.isDenied) {
                location.reload()
            }
        })

    }

    function cleanLogs(){
        Swal.fire({
            title: 'Estas seguro que deseas eliminar los logs?',
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
                    apiclient.cleanLogs();
                });
            } else if (result.isDenied) {
                Swal.fire('Los logs no fueron eliminados', '', 'info')
            }
        })
    }

    function shutdownApp(){
        $.getScript(_apiclient,function(){
            apiclient.shutdown();
        });

    }

    function prepareElements(){
        document.getElementById("shutdown").addEventListener("click", function(e) {
            e.preventDefault();
            shutdownApp();
        });
        document.getElementById("upload-excel").addEventListener("click", function(e) {
            e.preventDefault(); // stop the submit
            uploadExcelFile();
        });
        document.getElementById("delete-excel").addEventListener("click", function(e) {
            e.preventDefault(); // stop the submit
            deleteExcelFile();
        });
    }


    return {
        prepareElements:prepareElements,
        uploadExcelFile:uploadExcelFile,
        cleanLogs:cleanLogs,
        deleteExcelFile:deleteExcelFile,
        exportReportToExcel:exportReportToExcel
    };
})();