const width = document.getElementById('width');
const length = document.getElementById('length');
const roof = document.getElementById('roof');
const form = document.querySelector('form');

const setError = (element, message) => {
    const inputControl = element.parentElement;
    const errorDisplay = inputControl.querySelector('.error-message');

    errorDisplay.innerText = message;
    inputControl.classList.add('error');
    inputControl.classList.remove('success')
}

const setSuccess = (element) => {
    const inputControl = element.parentElement;
    const errorDisplay = inputControl.querySelector('.error-message');

    errorDisplay.innerText = '';
    inputControl.classList.remove('error');
    inputControl.classList.add('success');
}

const validateSelect = (selectElement, errorMessage) => {
    if(selectElement.value === selectElement.options[0].value) {
        setError(selectElement, errorMessage);
    } else {
        setSuccess(selectElement);
    }
};

const validateInputs = () => {
    validateSelect(width, 'Vælg venligst en bredde');
    validateSelect(length, 'Vælg venligst en længde');
    validateSelect(roof, 'Vælg venligst en tagtype');
}

form.addEventListener('submit', e => {
    e.preventDefault();
    validateInputs();
});