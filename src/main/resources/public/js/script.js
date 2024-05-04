document.addEventListener("DOMContentLoaded", function() {
    initializeProgress();
    setupButtons();
});

function initializeProgress() {
    const currentStep = parseInt(sessionStorage.getItem('currentStep') || '0');
    updateProgressbar(currentStep);
}

function updateProgressbar(step) {
    const progress = document.getElementById("progress");
    const progressSteps = document.querySelectorAll(".progress-step");
    const numberOfSteps = progressSteps.length - 1; // Total number of steps minus one

    // Calculate the width of the progress bar based on the current step
    progress.style.width = `${(step / numberOfSteps) * 100}%`;

    // Update step active state
    progressSteps.forEach((stepElement, index) => {
        if (index <= step) {
            stepElement.classList.add("progress-step-active");
        } else {
            stepElement.classList.remove("progress-step-active");
        }
    });
}

function setupButtons() {
    const nextBtn = document.querySelector(".btn-next");
    const prevBtn = document.querySelector(".btn-prev");

    if (nextBtn) {
        nextBtn.addEventListener('click', (e) => {
            e.preventDefault();
            if (validateInputs()) { // Only navigate forward if the inputs are valid
                navigate(1); // Navigate forward
            }
        });
    }

    if (prevBtn) {
        prevBtn.addEventListener('click', (e) => {
            e.preventDefault();
            navigate(-1); // Navigate backward
        });
    }
}

function navigate(direction) {
    const currentStep = parseInt(sessionStorage.getItem('currentStep') || '0');
    const newStep = currentStep + direction;

    // Ensure the new step is within valid bounds
    if (newStep >= 0 && newStep < document.querySelectorAll(".progress-step").length) {
        sessionStorage.setItem('currentStep', newStep);
        updateProgressbar(newStep);
        const pages = ['carportform.html', 'kontakt.html', 'kvittering.html'];
        window.location.href = pages[newStep];
    }
}

function validateInputs() {
    const carportWidth = document.getElementById('carport-width');
    const carportLength = document.getElementById('carport-length');
    const carportRoof = document.getElementById('carport-roof');
    const shedWidth = document.getElementById('shed-width');
    const shedLength = document.getElementById('shed-length');

    const isValidWidth = validateSelect(carportWidth, 'Vælg venligst en bredde');
    const isValidLength = validateSelect(carportLength, 'Vælg venligst en længde');
    const isValidRoof = validateSelect(carportRoof, 'Vælg venligst en tagtype');
    const isValidShedWidth = validateSelect(shedWidth, 'Vælg venligst en bredde for redskabsrum');
    const isValidShedLength = validateSelect(shedLength, 'Vælg venligst en længde for redskabsrum');

    return isValidWidth && isValidLength && isValidRoof && isValidShedWidth && isValidShedLength;
}

function validateSelect(selectElement, errorMessage) {
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
