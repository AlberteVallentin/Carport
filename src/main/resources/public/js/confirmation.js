document.addEventListener('DOMContentLoaded', () => {
    const carportWidthText = localStorage.getItem('width');
    const lengthText = localStorage.getItem('length');
    const roofText = localStorage.getItem('roof');

    document.getElementById('selectedWidth').textContent = widthText;
    document.getElementById('selectedLength').textContent = lengthText;
    document.getElementById('selectedRoof').textContent = roofText;

});
