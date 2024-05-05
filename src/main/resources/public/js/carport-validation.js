const carportWidth = document.getElementById('carport-width');
const carportLength = document.getElementById('carport-length');
const carportRoof = document.getElementById('carport-roof');
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

// Samlet valideringsfunktion, som kontrollerer alle inputs
const validateInputs = () => {
    const isValidWidth = validateSelect(carportWidth, 'Vælg venligst en bredde');
    const isValidLength = validateSelect(carportLength, 'Vælg venligst en længde');
    const isValidRoof = validateSelect(carportRoof, 'Vælg venligst en tagtype');

    return isValidWidth && isValidLength && isValidRoof;  // Returner 'true' kun hvis alle er gyldige
};

// Event listener på linket, der validerer data før navigation
validateLink.addEventListener('click', e => {
    e.preventDefault();  // Forhindrer linket i at følge sin href

    if (validateInputs()) {
        window.location.href = 'contact-details.html';  // Skift til din ønskede bekræftelsesside
    }
});