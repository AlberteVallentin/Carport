document.addEventListener("DOMContentLoaded", function() {
    initializeProgress();
    setupNavigation();
});

function initializeProgress() {
    // Hent det aktuelle trin fra sessionStorage, eller start fra trin 0
    const currentStep = parseInt(sessionStorage.getItem('currentStep') || '0');
    updateProgressbar(currentStep);
}

function updateProgressbar(currentStep) {
    const progress = document.getElementById("progress");
    const progressSteps = document.querySelectorAll(".progress-step");

    // Opdater alle trin før og inklusive det aktuelle trin som aktive
    progressSteps.forEach((step, index) => {
        if (index <= currentStep) {
            step.classList.add("progress-step-active");
        } else {
            step.classList.remove("progress-step-active");
        }
    });

    // Beregn progressbaren bredde
    progress.style.width = ((currentStep / (progressSteps.length - 1)) * 100) + "%";
}

function setupNavigation() {
    const nextBtn = document.querySelector(".btn-next");
    const prevBtn = document.querySelector(".btn-prev");

    nextBtn && nextBtn.addEventListener('click', () => navigate(1));
    prevBtn && prevBtn.addEventListener('click', () => navigate(-1));
}

function navigate(direction) {
    const currentStep = parseInt(sessionStorage.getItem('currentStep') || '0');
    const newStep = currentStep + direction;

    // Kontroller at det nye trin er inden for de gyldige grænser
    if (newStep < 0 || newStep >= document.querySelectorAll(".progress-step").length) {
        return; // Forhindrer navigation uden for tilladte trin
    }

    // Gem det nye trin i sessionStorage
    sessionStorage.setItem('currentStep', newStep);

    // Redirect til den relevante side baseret på trinnummeret
    const pages = ['carportform.html', 'kontakt.html', 'kvittering.html'];
    window.location.href = pages[newStep];
}
