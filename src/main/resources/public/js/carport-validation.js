const carportWidth = document.getElementById('carport-width');
const carportLength = document.getElementById('carport-length');
const carportRoof = document.getElementById('carport-roof');
const shedWidth = document.getElementById('shed-width');
const shedLength = document.getElementById('shed-length');
const validateLink = document.getElementById('validateLink');

const setError = (element, message) => {
    const inputControl = element.parentElement;
    const errorDisplay = inputControl.querySelector('.error-message');
    errorDisplay.innerText = message;
};

const validateSelect = (selectElement, errorMessage) => {
    if (selectElement.value === selectElement.options[0].value) {
        setError(selectElement, errorMessage);
        return false;  // Return 'false' hvis validering fejler
    } else {
        return true;  // Return 'true' hvis validering er succesfuld
    }
};

const validateInputs = () => {
    const isValidWidth = validateSelect(carportWidth, 'Vælg venligst en bredde');
    const isValidLength = validateSelect(carportLength, 'Vælg venligst en længde');
    const isValidRoof = validateSelect(carportRoof, 'Vælg venligst en tagtype');

    return isValidWidth && isValidLength && isValidRoof;  // Returner 'true' kun hvis alle er gyldige
};

validateLink.addEventListener('click', e => {
    e.preventDefault();  // Forhindrer linket i at følge sin href

    if (validateInputs()) {
        window.location.href = 'contact-details.html';  // Skift til din ønskede bekræftelsesside
    }
});
