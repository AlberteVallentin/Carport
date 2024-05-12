const carportWidth = document.getElementById('carport-width');
const carportLength = document.getElementById('carport-length');
const carportRoof = document.getElementById('carport-roof');
const shedWidth = document.getElementById('shed-width');
const shedLength = document.getElementById('shed-length');
const validateLink = document.getElementById('validateLink');

// Funktion til at sætte en fejlbesked på et specifikt element
const setError = (element, message) => {
    const inputControl = element.parentElement;
    const errorDisplay = inputControl.querySelector('.error-message');
    errorDisplay.innerText = message;
};

// Funktion til at rydde fejlbesked fra et specifikt element
const clearError = (element) => {
    const inputControl = element.parentElement;
    const errorDisplay = inputControl.querySelector('.error-message');
    if (errorDisplay) {
        errorDisplay.innerText = '';
    }
};

// Valideringsfunktion for dropdowns, som sætter eller rydder fejl
const validateSelect = (selectElement, errorMessage) => {
    if (selectElement.value === selectElement.options[0].value) {
        setError(selectElement, errorMessage);
        return false;  // Return 'false' hvis validering fejler
    } else {
        clearError(selectElement);  // Ryd fejlbesked, når valget er gyldigt
        return true;  // Return 'true' hvis validering er succesfuld
    }
};

const validateInputs = () => {
    const isValidWidth = validateSelect(carportWidth, 'Vælg venligst en bredde');
    const isValidLength = validateSelect(carportLength, 'Vælg venligst en længde');
    const isValidRoof = validateSelect(carportRoof, 'Vælg venligst en tagtype');
    const isValidShedWidth = validateSelect(shedWidth, 'Vælg venligst en bredde eller "Uden redskabsrum"');
    const isValidShedLength = validateSelect(shedLength, 'Vælg venligst en længde eller "Uden redskabsrum"');

    // Tjek om brugeren har valgt "Uden redskabsrum" i længde, men har valgt en bredde (tjekker først om der er valgt en bredde og længde)
    if (isValidShedWidth && isValidShedLength && shedLength.value === "0" && shedWidth.value !== "0") {
        setError(shedLength, 'Vælg "Uden redskabsrum" eller både en længde og bredde for redskabsrummet');
        setError(shedWidth, 'Vælg "Uden redskabsrum" eller både en længde og bredde for redskabsrummet');
        return false;
    }
    // Tjek om brugeren har valgt "Uden redskabsrum" i bredde, men har valgt en længde (tjekker først om der er valgt en bredde og længde)
    if (isValidShedWidth && isValidShedLength && shedWidth.value === "0" && shedLength.value !== "0") {
        setError(shedWidth, 'Vælg "Uden redskabsrum" eller både en længde og bredde for redskabsrummet');
        setError(shedLength, 'Vælg "Uden redskabsrum" eller både en længde og bredde for redskabsrummet');
        return false;
    }

    return isValidWidth && isValidLength && isValidRoof && isValidShedWidth && isValidShedLength;  // Returner 'true' kun hvis alle er gyldige
};

validateLink.addEventListener('click', e => {
    // Prøv at validere indtastninger først
    if (!validateInputs()) {
        e.preventDefault();  // Kun forhindre standardadfærd hvis valideringen fejler
    } else {
        // Hvis alt er gyldigt, tillad normal navigation
        window.location.href = validateLink.getAttribute('href');  // Brug den rigtige URL fra href attribut
    }
});