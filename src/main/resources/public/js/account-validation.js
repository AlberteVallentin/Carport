// Få adgang til alle input-elementer
const firstName = document.getElementById('first-name');
const lastName = document.getElementById('last-name');
const email = document.getElementById('email');
const phoneNumber = document.getElementById('phone');
const password1 = document.getElementById('password1');
const password2 = document.getElementById('password2');
const streetName = document.getElementById('ship-address');
const houseNumber = document.getElementById('house-number');
const postalCode = document.getElementById('postcode');
const city = document.getElementById('locality');

// Funktion til at vise fejlbesked
const setError = (inputElement, errorMessage) => {
    const errorElement = inputElement.parentElement.querySelector('.error-message');
    errorElement.innerText = errorMessage;
}

// Funktion til at rydde fejlbesked
const clearError = (inputElement) => {
    const errorElement = inputElement.parentElement.querySelector('.error-message');
    errorElement.innerText = '';
}

// Funktion til at validere et input-element
const validateInput = (inputElement, errorMessage) => {
    if (!inputElement.value) {
        setError(inputElement, errorMessage);
        return false;  // Return 'false' hvis validering fejler
    } else {
        clearError(inputElement);  // Ryd fejlbesked, hvis input er gyldigt
        return true;  // Return 'true' hvis validering er succesfuld
    }
}

// Funktion til at validere alle input-elementer
const validateInputs = () => {
    const isValidFirstName = validateInput(firstName, 'Skriv dit fornavn');
    const isValidLastName = validateInput(lastName, 'Skriv dit efternavn');
    const isValidEmail = validateInput(email, 'Skriv din e-mail');
    const isValidPhoneNumber = validateInput(phoneNumber, 'Skriv dit telefonnummer');
    const isValidPassword1 = validateInput(password1, 'Skriv en adgangskode');
    const isValidPassword2 = validateInput(password2, 'Gentag din adgangskode');
    const isValidStreetName = validateInput(streetName, 'Skriv dit vejnavn');
    const isValidHouseNumber = validateInput(houseNumber, 'Skriv dit husnummer');
    const isValidPostalCode = validateInput(postalCode, 'Skriv dit postnummer');
    const isValidCity = validateInput(city, 'Skriv din by');

    // Returner 'true' hvis alle inputs er gyldige, ellers 'false'
    return isValidFirstName && isValidLastName && isValidEmail && isValidPhoneNumber && isValidPassword1 && isValidPassword2 && isValidStreetName && isValidHouseNumber && isValidPostalCode && isValidCity;
}

// Tilføj event listener til formularindsendelse
document.querySelector('#accountForm').addEventListener('submit', e => {
    // Prøv at validere indtastninger først
    if (!validateInputs()) {
        e.preventDefault();  // Kun forhindre standardadfærd hvis valideringen fejler
    }
});