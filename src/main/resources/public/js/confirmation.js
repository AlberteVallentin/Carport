document.addEventListener('DOMContentLoaded', () => {
    const carportWidthText = localStorage.getItem('carport-width');
    const carportLengthText = localStorage.getItem('carport-length');
    const roofText = localStorage.getItem('carport-roof');
    const shedWidthText = localStorage.getItem('shed-width');
    const shedLengthText = localStorage.getItem('shed-length');

    document.getElementById('selectedCarportWidth').textContent = carportWidthText;
    document.getElementById('selectedCarportLength').textContent = carportLengthText;
    document.getElementById('selectedCarportRoof').textContent = roofText;
    document.getElementById('selectedShedWidth').textContent = shedWidthText;
    document.getElementById('selectedShedLength').textContent = shedLengthText;


});
