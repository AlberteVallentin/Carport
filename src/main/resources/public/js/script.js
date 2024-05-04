document.addEventListener("DOMContentLoaded", function() {
    initializeProgress();
    setupButtons();
});

function initializeProgress() {
    // Get the current step from sessionStorage or default to the first step
    const currentStep = parseInt(sessionStorage.getItem('currentStep') || '0');
    updateProgressbar(currentStep);
}

function updateProgressbar(step) {
    const progress = document.getElementById("progress");
    const progressSteps = document.querySelectorAll(".progress-step");
    const numberOfSteps = progressSteps.length - 1;

    progress.style.width = `${(step / numberOfSteps) * 100}%`;

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
    const currentPage = window.location.pathname.split("/").pop(); // Get the current file name

    if (nextBtn) {
        nextBtn.addEventListener('click', (e) => {
            e.preventDefault();
            // Only perform validation on 'carportform.html'
            if (currentPage === 'carportform.html') {
                if (validateInputs()) {
                    navigate(1); // Navigate forward if validation is successful
                }
            } else {
                navigate(1); // Navigate forward without validation
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
    const pages = ['carportform.html', 'kontakt.html', 'kvittering.html'];

    if (newStep >= 0 && newStep < pages.length) {
        sessionStorage.setItem('currentStep', newStep);
        updateProgressbar(newStep);
        window.location.href = pages[newStep];
    }
}

function validateInputs() {
    const carportWidth = document.getElementById('carport-width');
    const carportLength = document.getElementById('carport-length');
    const carportRoof = document.getElementById('carport-roof');

    const isValidWidth = validateSelect(carportWidth, 'Vælg venligst en bredde');
    const isValidLength = validateSelect(carportLength, 'Vælg venligst en længde');
    const isValidRoof = validateSelect(carportRoof, 'Vælg venligst en tagtype');

    return isValidWidth && isValidLength && isValidRoof;
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
