document.addEventListener("DOMContentLoaded", function() {
    const origenInput = document.getElementById("origenNombre");
    const destinoInput = document.getElementById("destinoNombre");
    const origenSuggestionsBox = document.getElementById("origenSuggestions");
    const destinoSuggestionsBox = document.getElementById("destinoSuggestions");

    function handleInput(inputElement, suggestionsBox) {
        inputElement.addEventListener("input", function() {
            const query = inputElement.value;

            if (query.length >= 2) { // Mínimo de 2 caracteres antes de buscar
                fetch(`/autocomplete-localizacion?query=${query}`)
                    .then(response => response.json())
                    .then(data => {
                        suggestionsBox.innerHTML = "";
                        suggestionsBox.style.display = "block";

                        if (Array.isArray(data) && data.length > 0) {
                            data.forEach(item => {
                                const suggestionItem = document.createElement("div");
                                suggestionItem.textContent = item;
                                suggestionItem.classList.add("suggestion-item");
                                suggestionItem.setAttribute("tabindex", "0");
                                suggestionItem.setAttribute("role", "option");

                                suggestionItem.addEventListener("click", function() {
                                    inputElement.value = item;
                                    suggestionsBox.style.display = "none";
                                });

                                suggestionItem.addEventListener("keydown", function(e) {
                                    if (e.key === "Enter") {
                                        inputElement.value = item;
                                        suggestionsBox.style.display = "none";
                                    }
                                });

                                suggestionsBox.appendChild(suggestionItem);
                            });
                        } else {
                            const noResults = document.createElement("div");
                            noResults.textContent = "No se encontraron resultados";
                            noResults.classList.add("no-results");
                            suggestionsBox.appendChild(noResults);
                        }
                    })
                    .catch(error => {
                        console.error('Error fetching suggestions:', error);
                    });
            } else {
                suggestionsBox.style.display = "none";
            }
        });
    }

    handleInput(origenInput, origenSuggestionsBox);
    handleInput(destinoInput, destinoSuggestionsBox);

    document.addEventListener("click", function(e) {
        if (!origenSuggestionsBox.contains(e.target) && e.target !== origenInput) {
            origenSuggestionsBox.style.display = "none";
        }
        if (!destinoSuggestionsBox.contains(e.target) && e.target !== destinoInput) {
            destinoSuggestionsBox.style.display = "none";
        }
    });
});
