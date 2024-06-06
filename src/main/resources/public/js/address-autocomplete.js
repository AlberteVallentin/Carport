// Dette eksempel bruger Places Autocomplete-widget til at:
// 1. Hjælpe brugeren med at vælge et sted
// 2. Hente adressekomponenterne, der er knyttet til det sted
// 3. Udfylde formularfelterne med disse adressekomponenter.
// Dette eksempel kræver Places-biblioteket og Maps JavaScript API.
// Inkluder parameteren libraries=places, når du først indlæser API'en.
// For eksempel: <script
// src="https://maps.googleapis.com/maps/api/js?key=YOUR_API_KEY&libraries=places">

let autocomplete;
let address1Field;
let address2Field;
let postalField;

function initAutocomplete() {
    // Vælg formularfelter fra DOM'en
    address1Field = document.querySelector("#ship-address");
    address2Field = document.querySelector("#address2");
    postalField = document.querySelector("#postcode");

    // Opret autocomplete-objektet og begræns søgeforslagene til adresser i Danmark
    autocomplete = new google.maps.places.Autocomplete(address1Field, {
        componentRestrictions: { country: ["dk"] },
        fields: ["address_components", "geometry"],
        types: ["address"],
    });

    // Tilføj en lytter, der udfylder adressefelterne, når brugeren vælger en adresse fra dropdown-listen
    autocomplete.addListener("place_changed", fillInAddress);
}

function fillInAddress() {
    // Hent stedsdetaljerne fra autocomplete-objektet
    const place = autocomplete.getPlace();
    let address1 = "";
    let postcode = "";
    let street_number = "";

    // Hent hver komponent af adressen fra stedsdetaljerne
    // place.address_components er google.maps.GeocoderAddressComponent objekter
    // som er dokumenteret på http://goo.gle/3l5i5Mr
    for (const component of place.address_components) {
        // Fjern typinger, når de er rettet
        // @ts-ignore
        const componentType = component.types[0];

        // Tjek komponenttypen og udfyld de tilsvarende felter
        switch (componentType) {
            case "street_number": {
                street_number = component.long_name;
                break;
            }

            case "route": {
                address1 = `${component.short_name} ${street_number}`;
                break;
            }

            case "postal_code": {
                postcode = `${component.long_name}${postcode}`;
                break;
            }

            case "postal_code_suffix": {
                postcode = `${postcode}-${component.long_name}`;
                break;
            }
            case "locality":
                document.querySelector("#locality").value = component.long_name;
                break;
            case "administrative_area_level_1": {
                document.querySelector("#state").value = component.short_name;
                break;
            }
            case "country":
                //document.querySelector("#country").value = component.long_name;
                break;
        }
    }

    // Udfyld adresse- og postnummerfelterne
    address1Field.value = address1;
    postalField.value = postcode;

    // Efter at have udfyldt formularen med adressekomponenter fra Autocomplete forudsigelsen
    // sæt cursorfokus på den anden adresselinje for at tilskynde indtastning af ekstra oplysninger
    address2Field.focus();
}

// Initialiser autocomplete, når vinduet indlæses
window.initAutocomplete = initAutocomplete;
