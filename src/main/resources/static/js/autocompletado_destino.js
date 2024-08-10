document.addEventListener("DOMContentLoaded", function() {
    const input = document.getElementById("localizacionNombre");
    const suggestionsBox = document.getElementById("suggestions");

    input.addEventListener("input", function() {
        const query = input.value;

        if (query.length >= 2) { // Mínimo de 2 caracteres antes de buscar
            fetch(`/autocomplete-localizacion?query=${query}`)
                .then(response => response.json())
                .then(data => {
                    // Limpiar sugerencias anteriores
                    suggestionsBox.innerHTML = "";
                    suggestionsBox.style.display = "block";

                    // Llenar la caja de sugerencias
                    data.forEach(function(item) {
                        const suggestionItem = document.createElement("div");
                        suggestionItem.textContent = item;
                        suggestionItem.classList.add("suggestion-item");
                        suggestionItem.setAttribute("tabindex", "0"); // Hacer el div enfocables
                        suggestionItem.setAttribute("role", "option"); // Añadir rol para accesibilidad

                        // Manejar la selección de sugerencia
                        suggestionItem.addEventListener("click", function() {
                            input.value = item;
                            suggestionsBox.style.display = "none"; // Ocultar sugerencias
                        });

                        suggestionItem.addEventListener("keydown", function(e) {
                            if (e.key === "Enter") { // Permitir selección con Enter
                                input.value = item;
                                suggestionsBox.style.display = "none"; // Ocultar sugerencias
                            }
                        });

                        suggestionsBox.appendChild(suggestionItem);
                    });
                });
        } else {
            suggestionsBox.style.display = "none"; // Ocultar sugerencias si no hay texto suficiente
        }
    });

    // Ocultar las sugerencias si se hace clic fuera de la caja
    document.addEventListener("click", function(e) {
        if (!suggestionsBox.contains(e.target) && e.target !== input) {
            suggestionsBox.style.display = "none";
        }
    });
});
