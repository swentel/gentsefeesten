<?php

// This converts the events from the open data website of Gent for use in the
// Android application.

// https://datatank.stad.gent/4/toerisme/gentsefeestenevents.json
// https://datatank.stad.gent/4/cultuursportvrijetijd/gentsefeestenlocaties.json
// https://datatank.stad.gent/4/toerisme/gentsefeestencategorien.json

// Download the events file locally in a file called 'events.json'.
// You can ignore the other files, their data is small and defined in the
// arrays in the app.

// Get content.
if (!file_exists('events.json')) {
  print "'events.json' file not found.\n";
  exit;
}

$json = file_get_contents('events.json');

// Event uuids - ids.
$uuid_events = unserialize(file_get_contents('uuid_events.json'));

$locations_sum = array();
$locations_decoded = json_decode(file_get_contents('gentsefeestenlocaties.json'));
$locations = array();
$i = 1;
foreach ($locations_decoded as $location) {
  $location = (array) $location;
  $locations[$location['@id']] = $location;
  $locations[$location['@id']]['locatie_id'] = $i;

  $locations_sum[] = array(
    'id' => $i,
    'name' => $location['name']->nl,
  );

  // Salsabar, tref.club -> baudelo
  //if ($i == 8 || $i == 6 || $i == 5 || $i == 88 || $i == 351 || $i == 383) {
  if ($i == 8 || $i == 6 || $i == 5 || $i == 88 || $i == 351) {
    $locations[$location['@id']]['locatie_id'] = 7;
  }

  // Sint-baafs
  if ($i == 248 || $i == 249 || $i == 250) {
    $locations[$location['@id']]['locatie_id'] = 2;
  }

  // Willem
  if ($i == 20 || $i == 21) {
    $locations[$location['@id']]['locatie_id'] = 19;
  }

  $i++;
}

$cats = array();
$cats_decoded = json_decode(file_get_contents('gentsefeestencategorien.json'));
$ii = 1;
foreach ($cats_decoded as $caty) {
  $caty = (array) $caty;
  $cats[$caty['@id']] = $caty; 
  $cats[$caty['@id']]['categorie_id'] = $ii;
  $ii++;

}

//print_r($cats);
//file_put_contents('catssum', print_r($cats, 1));
//die();

//print_r($locations);
//print_r($locations_sum);
//file_put_contents('locationssum', print_r($locations_sum, 1));
//die();

// Debugging
$debug = isset($argv[1]) ? TRUE : FALSE;

// Decode.
$decode = json_decode($json);
// Create sql queries.
$statements = "";

//print_r($decode);
//die();

$number = 0;
$lines = array();
$unique_dates = array();
foreach ($decode as $key => $new_event) {


  $array = (array) $new_event;

  // The scheme is different, convert it.
  $event = new stdClass();
  $full_unix = strtotime($new_event->startDate);
  $unix_day = strtotime(date('d-m-Y', $full_unix));
  $startuur = date('G:i', $full_unix);
  
  $einduur = '';
  if (!empty($new_event->endDate)) {
    $end_full_unix = strtotime($new_event->endDate);
    $einduur = date('G:i', $end_full_unix);
  }

  // 'all day' is einduur 3:59
  if ($einduur == '3:59') {
    $startuur = '';
    $einduur = '';
  }

  // Switch date one day back.
  $hour = date('G', $full_unix);
  $sorting = $full_unix;
  if ($hour >= 0 && $hour < 6) {
    $full_unix -= 86400;
    $unix_day = strtotime(date('d-m-Y', $full_unix));
  }

  $event->startuur = $startuur;
  $event->einduur = $einduur;
  $event->tijdstip_sortering = $sorting;
  $event->datum = $unix_day;
  $event->titel = $new_event->name->nl;
  $event->omschrijving = !empty($new_event->description->nl) ? $new_event->description->nl : '';
  $event->url = isset($new_event->url) ? $new_event->url : '';
  $event->gratis = isset($new_event->isAccessibleForFree) ? (int) $new_event->isAccessibleForFree : 0;

  $location = $new_event->location;
  if (isset($locations[$location])) {
    //print "location found";
    $event->locatie = $locations[$location]['name']->nl;
    $event->locatie_id = $locations[$location]['locatie_id'];
    $event->straat = !empty($locations[$location]['address']->streetAddress) ? $locations[$location]['address']->streetAddress : '';
    $event->huisnummer = '';
  }
  else {
    $event->location = "";
    $event->locatie_id = 0;
    $event->straat = '';
    //print "location not set\n";
  }

  // Ignore location id 383
  if (!empty($event->locatie_id) && $event->locatie_id == 383) {
    continue;
  }
 
  // ID.
  $uuid = $array['@id'];
  if (isset($uuid_events[$uuid])) {
    $id = $uuid_events[$uuid];  
  }
  else {
    $id_number = count($uuid_events);
    $id_number++;
    $id = $id_number;
    $uuid_events[$uuid] = $id;
  }
  $event->id = $id;

  // Categories
  $event->categorie_naam = '';
  $event->categorie_id = 0;
  $catje = isset($new_event->theme[0]) ? $new_event->theme[0] : 'nope';
//print $catje . "\n";
  if (isset($cats[$catje])) {
    $event->categorie_naam = $cats[$catje]['name']->nl;
    $event->categorie_id = $cats[$catje]['categorie_id'];
  }
 
  // Price 
  if (!empty($new_event->offers[0]->price)) {
    $event->prijs = $new_event->offers[0]->price;
  }
  else {
    $event->prijs = '';
  }
  $event->prijs_vvk = '';

  // Lat and long are not exported.
  $event->latitude = '';
  $event->longitude = '';
  $event->korting = 0;
  $event->festival = 0;

  // Image.
  $event->afbeelding = '';
  //if (!empty($new_event->image->thumbnailUrl)) {
  //  $event->afbeelding = $new_event->image->thumbnailUrl;
  //}

  if ($debug) {
    //if (strpos($event->titel, 'Soul Shakers (BE)') !== FALSE) {
    //if (strpos($event->titel, 'Ertebrekers') !== FALSE) {
    if (strpos($event->titel, 'De Buck') !== FALSE) {
    //if (strpos($event->titel, 'De fantastische Anna') !== FALSE) {
      print_r($new_event);
      print_r($event);
      print "-------------------------------------------\n";
      //print "$full_unix - $unix_day\n";
    }
    continue;
  }

  // The data contains too much info
  if ($event->datum < 1530748800 || $event->datum > 1532217600) {
    continue;
  }

  // Omschrijving.
  // They used html this in 2015, so do some fiddling on it.
  $description = $event->omschrijving;
  $description = html_entity_decode($description, ENT_QUOTES);
  // Replace p and br tags.
  $description = str_replace(array('<p>', '<br />', '<br>', '</p>'), array('', "\n", "\n", "\n"), $description);
  // Replace &nbsp;
  $description = str_replace('&nbsp;', ' ', $description);
  // Now convert our newlines.
  $description = str_replace("\r", "", trim($description));
  $description = str_replace("\n", "|NEWLINE|", $description);
  $event->omsch = $description;

  // Locatie.
  $loc = isset($event->locatie) ? trim($event->locatie) : '';
  if (!empty($event->straat)) {
    $street = trim($event->straat);
    if (!empty($event->huisnummer)) {
      $street .= " " . $event->huisnummer;
    }
    $street = trim($street);
    if ($street != $loc) {
      $loc .= "\n" . $street;
    }
  }
  $loc = str_replace("\r", "", $loc);
  $loc = str_replace("\n", "|NEWLINE|", $loc);
  $event->locatie = $loc;

  // Prijs
  if (!empty($event->prijs)) {
    $event->prijs = "€ " . $event->prijs;
  }

  // Keep an array of unique dates.
  $udate = $event->datum;
  if (!isset($unique_dates[$udate])) {
    $unique_dates[$udate] = date('d m Y', $udate);
  }

  $query = "('" . my_mysql_escape_string($event->titel) . "',";
  $query .= "'" . $event->id . "',";
  $query .= "'" . $event->gratis . "',";
  $query .= "'" . my_mysql_escape_string($event->prijs) . "',";
  $query .= "'" . my_mysql_escape_string($event->prijs_vvk) . "',";
  $query .= "'" . my_mysql_escape_string($event->omsch) . "',";
  //$query .= "'" . ($event->datum + 7200) . "',";
  $query .= "'" . ($event->datum) . "',";

  $hour_string = "";
  if (!empty($event->startuur)) {
    $hour_string = $event->startuur;
    if (!empty($event->einduur)) {
      $hour_string .= ' - ' . $event->einduur;
    }
  }

  $query .= "'" . $hour_string . "',";
  $query .= "'" . my_mysql_escape_string($event->startuur) . "',";

  // Date sort is broken in so many ways. We thus take the timestamp
  // and add the sort which is in the format of hhmm (wihout leading 0)
  // for times before 12 and add calculate a sort which we can
  // actually use for decent sorting.
  $sort = $event->tijdstip_sortering;
  $timestamp = 0;
  $hours = 0;
  if (!empty($sort)) {
    //$minutes = substr($sort, -2);
    //if (strlen($sort) == 3) {
    //  $hours = substr($sort, 0, 1);
      //echo "$hours - $sort\n";
    //}
    //else {
    //  $hours = substr($sort, 0, 2);
      //echo "$hours - $sort\n";
    //}
 
    // In case hours is after midnight, until 5 in the morning
    // add 24 hours more for sorting.
    //if ($hours == '00' || $hours < 5) {
    //  $hours = 24 + $hours;
    //}

    //$total = ($hours * 3600) + $minutes;
    //$timestamp = $event->datum + $total + 7200; // + two hours because datum is in GMT.
    $timestamp = $sort;
    //echo "$event->datum - $hours - $sort - $total - $timestamp\n";
  }

  /*if ($event->id == 13235) {
    print $timestamp . "\n";
    die();
  }*/

  // Korting is an array or false.
  $korting = $event->korting;
  if (is_array($korting)) {
    $korting = implode(', ', $korting);
  }
  else {
    $korting = '';
  }

  // Media.
  $media = '';
  if (!empty($event->afbeelding)) {
    $media = $event->afbeelding;
  }
  //if (!empty($event->videos) && strpos($event->videos->input, 'youtube') !== FALSE) {
  //  $media['video'] = $event->videos->input;
  //}

  $query .= "" . $timestamp . ",";
  $query .= "'" . my_mysql_escape_string($event->categorie_naam) . "',";
  $query .= "'" . $event->categorie_id . "',";
  $query .= "'" . my_mysql_escape_string($event->url) . "',";
  $query .= "'" . $event->locatie_id . "',";
  $query .= "'" . my_mysql_escape_string($event->locatie) . "',";
  $query .= "'" . $event->latitude . "',";
  $query .= "'" . $event->longitude . "',";
  $query .= "'" . my_mysql_escape_string($korting) . "',";
  $query .= "'" . $event->festival . "',";
  $query .= "'" . $media . "'";
  $query .= ")";

  if ($event->festival) {
    print $event->titel . "\n";
  }

  //print_r($event);

  $lines[] = $query;
  $number++;
  if ($number == 5) {
    $number = 0;
    $statements .= implode(":SPLIT:", $lines) . "\n";
    $lines = array();
  }
}

if (!empty($lines)) {
  $statements .= implode(",", $lines);
}

function my_mysql_escape_string($string) {
  return str_replace("'", "''", $string);
}

krsort($unique_dates);
print_r($unique_dates);

// Write to file.
file_put_contents('events-2018.data', $statements);

// Write
file_put_contents('uuid_events.json', serialize($uuid_events));
