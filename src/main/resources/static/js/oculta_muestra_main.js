const btnTodos = document.querySelector(".muestra-todos");
const btnVuelos = document.querySelector(".muestra-vuelos");
const btnHoteles = document.querySelector(".muestra-hoteles");
const btnActividades = document.querySelector(".muestra-actividades");

const seccionVuelos = document.querySelector(".vuelos");
const seccionHoteles = document.querySelector(".hoteles");
const seccionActividades = document.querySelector(".actividades");

btnTodos.addEventListener("click", mostrarOcultarTodos);
btnVuelos.addEventListener("click", mostrarOcultarVuelos);
btnHoteles.addEventListener("click", mostrarOcultarHoteles);
btnActividades.addEventListener("click", mostrarOcultarActividades);

function mostrarOcultarTodos() {
  seccionVuelos.style.display = "block";
  seccionHoteles.style.display = "block";
  seccionActividades.style.display = "block";
}

function mostrarOcultarVuelos() {
  seccionVuelos.style.display = "block";
  seccionHoteles.style.display = "none";
  seccionActividades.style.display = "none";
}

function mostrarOcultarHoteles() {
  seccionVuelos.style.display = "none";
  seccionHoteles.style.display = "block";
  seccionActividades.style.display = "none";
}

function mostrarOcultarActividades() {
  seccionVuelos.style.display = "none";
  seccionHoteles.style.display = "none";
  seccionActividades.style.display = "block";
}
