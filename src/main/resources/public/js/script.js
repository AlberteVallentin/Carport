document.addEventListener("DOMContentLoaded", function() {
    // Setup event listeners once the document is fully loaded
    setupNavigation();
    attachEventListeners();
});

function setupNavigation() {
    const validateLink = document.getElementById('validateLink');

    if (validateLink) {
        validateLink.addEventListener('click', function(e) {
            e.preventDefault(); // Prevent the default link action

            // Perform validation
            if (validateInputs()) {
                // If validation is successful, navigate
                navigate();
            }
        });
    }
}

function validateInputs() {
    // Fetch all select elements
    const carportWidth = document.getElementById('carport-width');
    const carportLength = document.getElementById('carport-length');
    const carportRoof = document.getElementById('carport-roof');

    // Validate each select element
    const isValidWidth = validateSelect(carportWidth, 'Vælg venligst en bredde');
    const isValidLength = validateSelect(carportLength, 'Vælg venligst en længde');
    const isValidRoof = validateSelect(carportRoof, 'Vælg venligst en tagtype');

    // Return true only if all validations are passed
    return isValidWidth && isValidLength && isValidRoof;
}

function validateSelect(selectElement, errorMessage) {
    // Simple validation to check if the first option (usually default "choose" option) is selected
    if (selectElement.value === "" || selectElement.value === selectElement.options[0].value) {
        setError(selectElement, errorMessage);
        return false;
    } else {
        clearError(selectElement);
        return true;
    }
}

function setError(element, message) {
    const inputControl = element.parentElement;
    const errorDisplay = inputControl.querySelector('.error-message');
    errorDisplay.innerText = message;
    element.classList.add('error');
}

function clearError(element) {
    const inputControl = element.parentElement;
    const errorDisplay = inputControl.querySelector('.error-message');
    errorDisplay.innerText = '';
    element.classList.remove('error');
}

function navigate() {
    // Increase the step in sessionStorage
    const currentStep = parseInt(sessionStorage.getItem('currentStep') || '0');
    const nextStep = currentStep + 1;
    sessionStorage.setItem('currentStep', nextStep);

    // Update the progress bar
    updateProgressbar(nextStep);

    // Determine the next page based on the current step
    const pages = ['carportform.html', 'kontakt.html', 'kvittering.html'];
    if (nextStep < pages.length) {
        window.location.href = pages[nextStep];
    }
}

function updateProgressbar(step) {
    const progress = document.getElementById("progress");
    if (progress) {
        progress.style.width = ((step / 2) * 100) + '%'; // Assuming there are 3 steps (0, 1, 2)
    }
}
