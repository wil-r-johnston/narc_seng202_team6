//Variable to hold Google Maps object
let map;
//Array holds the current markers displayed on the map except address marker
let mapMarkers = [];
//Holds the coordinates of the address searched in the map
let returnLocation = { lat: 41.85, lng: -87.65 };
//Holds the string address of the searched address in the map
let currentAddress;
//Holds the marker which represents the address searched in the map
let locationMarker;

//Initialises the dynamic map
function initMap() {
  //Creates a new map object
  map = new google.maps.Map(document.getElementById("map"), {
    center: { lat: 41.85, lng: -87.65 },
    zoom: 10,
    mapId: '6a20fe312aa3d2af',
    streetViewControl: false,
    fullscreenControl: false,
    mapTypeControl: false,
  });
  //Creating the input box for searching address
  const card = document.getElementById("ac-card");
  const input = document.getElementById("autocomplete");
  //Sets the autocomplete to return full addresses
  const options =     {
    types: ["address"],
    fields: ["formatted_address", "geometry", "name"],
  };
  map.controls[google.maps.ControlPosition.TOP_LEFT].push(card);
  //Sets the input box to autocomplete
  autocomplete = new google.maps.places.Autocomplete(input, options);
  //When an address is entered in the input box call onPlaceChanged()
  autocomplete.addListener('place_changed', onPlaceChanged);
}

//Moves the map when address is entered
function onPlaceChanged() {
  const place = autocomplete.getPlace();
  //Sets the centre and zoom of map to be the location returned by the places api
  map.setCenter(place.geometry.location);
  map.setZoom(17);
  //Sets returnLocation to be the coordinates of the address searched for
  returnLocation = {lat: map.getCenter().lat(), lng: map.getCenter().lng()};
  //Sets currentAddress to be the address searched for
  currentAddress = document.getElementById("autocomplete").value;
  //Calls the addMarkersToMap() method in JavascriptMethods object
  javascriptMethods.addMarkersToMap();
}

//Returns returnLocation
function getLocation() {
  return returnLocation;
}

//Adds a marker to the map object
function addMarker(crim) {
  //Creates a new map marker, sets its position to the coordinates of the crime provided by crim.
  //Sets its map to map. Sets its icon to red pin
  const marker = new google.maps.Marker({
    position: crim[0],
    map: map,
    icon: 'http://maps.google.com/mapfiles/ms/icons/red-dot.png',
  });
  //Content of infowindow which shows the id, type and date of crime.
  //Also creates a button which calls viewMoreInfo() function with crimes id as parameter
  const contentString =
    '<div id="content">' +
    '<div id="bodyContent">' +
    "<p>Case Number: "+crim[3].id+"</p>" +
    "<p>Type: "+crim[1].crime+"</p>" +
    "<p>Date: "+crim[2].date+"</p>" +
    "<button onclick=\"viewMoreInfo('"+crim[3].id+"')\">View More Information</button>" +
    "</div>" +
    "</div>";
  //creates a new marker infowindow and sets its content to contentString
  const infowindow = new google.maps.InfoWindow({
    content: contentString,
  });
  //Adds a listener to marker onclick which shows the infowindow
  marker.addListener("click", () => {
    infowindow.open({
      anchor: marker,
      map,
      shouldFocus: false,
    });
  });
  //Appends marker to mapMarkers
  mapMarkers.push(marker);
}

//Removes all crime markers by setting their map to null and reinstantiating mapMakers
function removeMarkers() {
  for (let i = 0; i < mapMarkers.length; i++) {
    mapMarkers[i].setMap(null);
  }
  mapMarkers = [];
}

//Adds a green marker at location of returnLocation
function addLocationMarker() {
    const contentString =
    '<div id="content">' +
    '<div id="bodyContent">' +
    "<p>Address: "+currentAddress+"</p>" +
    "</div>" +
    "</div>";
  const infowindow = new google.maps.InfoWindow({
    content: contentString,
  });
  const marker = new google.maps.Marker({
    position: returnLocation,
    map: map,
    title: "Centre",
    icon: 'http://maps.google.com/mapfiles/ms/icons/green-dot.png',
  });
  marker.addListener("click", () => {
    infowindow.open({
      anchor: marker,
      map,
      shouldFocus: false,
    });
  });
  locationMarker = marker;
}

//Removes marker stored in locationMarker
function removeLocationMarker() {
    if (locationMarker != null) {
    locationMarker.setMap(null);
    locationMarker = null;
  }
}

//Calls the viewInfo() method in JavascriptMethods object
function viewMoreInfo(crimid) {
  javascriptMethods.viewInfo(crimid);
}