
//Comienzo el JQuery


$(function() {

    $(".bEnvio").click(function() {
        $("form").fadeToggle(1000);

    });
});

if(document.getElementById("nombre").value!="" || document.getElementById("password").value!=""){
	form[0].bEnvio.disabled=false; //REVISAR ESTA SENTENCIA
}

/*El formulario de datos del regalo debe de estar al principio invisible y solo aparecer
 una vez el usuario se haya logueado,sustituyendo el espacio del Login*/