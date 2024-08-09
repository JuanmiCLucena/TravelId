document.addEventListener("DOMContentLoaded", function() {
    const input = document.getElementById("localizacionNombre");
    const suggestionsBox = document.getElementById("suggestions");

    input.addEventListener("input", function() {
        const query = input.value;

        if (query.length >= 2) { // Minimo de 2 caracteres antes de buscar
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

                        // Manejar la selección de sugerencia
                        suggestionItem.addEventListener("click", function() {
                            input.value = item;
                            suggestionsBox.style.display = "none"; // Ocultar sugerencias
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