/*
 * Este script se ejecuta cuando el contenido del documento ha sido completamente cargado.
 *
 * 1. **Comprobación de errores y apertura de modal:**
 *    - Selecciona el elemento con la clase `.text-danger`, que se utiliza para mostrar mensajes de error.
 *    - Verifica si el elemento existe y si su contenido no está vacío (es decir, hay un mensaje de error).
 *    - Si hay un mensaje de error, crea una nueva instancia del modal de Bootstrap asociado al ID `#modalReserva`.
 *    - Muestra el modal automáticamente para que el usuario pueda ver y corregir el error.
 *
 * 2. **Actualización del precio total:**
 *    - Obtiene referencias a los elementos del DOM que corresponden al número de asistentes (`#asistentes`) y al precio total (`#precioTotal`).
 *    - Obtiene el precio por asistente desde el valor actual del campo `#precioTotal`. Si no es un número válido, se usa `0` como valor predeterminado.
 *
 * 3. **Función `updatePrecioTotal`:**
 *    - Esta función calcula el precio total en función del número de asistentes ingresados por el usuario.
 *    - Obtiene el número de asistentes del campo `#asistentes` y lo convierte a un número entero. Si el valor no es válido, se usa `1` como valor predeterminado.
 *    - Calcula el precio total multiplicando el número de asistentes por el precio por asistente.
 *    - Actualiza el campo `#precioTotal` con el precio total calculado, formateado a dos decimales.
 *
 * 4. **Evento de entrada del campo de asistentes:**
 *    - Añade un oyente de eventos al campo de número de asistentes (`#asistentes`) para que cada vez que el usuario cambie el valor (evento `input`), se ejecute la función `updatePrecioTotal` y se actualice el precio total.
 */
document.addEventListener("DOMContentLoaded", () => {
    // Comprobación de errores y apertura de modal
    const errorElement = document.querySelector(".text-danger");
    if (errorElement && errorElement.textContent.trim() !== "") {
        const modal = new bootstrap.Modal(
            document.getElementById("modalReserva")
        );
        modal.show();
    }

    // Obtén los elementos del DOM
    const asistentesInput = document.getElementById("asistentes");
    const precioTotalInput = document.getElementById("precioTotal");
    if(precioTotalInput && asistentesInput) {
        const precioActividadPorAsistente =
            parseFloat(precioTotalInput.value) || 0;

        // Función para actualizar el precio total
        function updatePrecioTotal() {
            const asistentes = parseInt(asistentesInput.value) || 1;
            const precioTotal = asistentes * precioActividadPorAsistente;
            precioTotalInput.value = precioTotal.toFixed(2);
        }
    }
    // Escucha el evento input en el campo de asistentes
    asistentesInput.addEventListener("input", updatePrecioTotal);
});
