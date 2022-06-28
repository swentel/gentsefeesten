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
if (file_exists('uuid_events.json')) {
  $uuid_events = unserialize(file_get_contents('uuid_events.json'));
}
else {
  $uuid_events = array();
}

$locations_sum = array();
$locations_decoded = json_decode(file_get_contents('gentsefeestenlocaties.json'));
//print_r($locations_decoded);
//return;
$locations = array();
$i = 1;
foreach ($locations_decoded as $location) {
  $location = (array) $location;
  //print_r($location);
  //return;
  $id = $location['fields']->id;
  $locations[$id] = $location;
  $locations[$id]['locatie_id'] = $i;
  $name = $location['fields']->name_nl;

  $l_set = FALSE;
  $locations_sum[] = array(
    'id' => $i,
    'name' => $name,
  );

  if (strpos($name, 'Baafs') !== FALSE || strpos($name, 'Brewery') !== FALSE || strpos($name, 'Stage') !== FALSE) {
    //print $name . ' - ' . $i . "\n";  
  }

  // Salsabar, tref.club -> baudelo
  //if ($i == 8 || $i == 6 || $i == 5 || $i == 88 || $i == 351 || $i == 383) {
  if ($i == 776 || $i == 1233 || $i == 415 || $i == 1232 || $i == 1234 || $i == 903) {
    $locations[$id]['locatie_id'] = 7;
    $l_set = TRUE;
  }

  // Sint jacobs
  if (strpos($name, 'Sint-Jacobs') !== FALSE || strpos($name, 'Walter De Buckplein') !== FALSE) {
    $locations[$id]['locatie_id'] = 4;
    $l_set = TRUE;
  }

  // Luisterplein
  if ($i == 609 || $i == 392) {
    $locations[$id]['locatie_id'] = 14;
    $l_set = TRUE;
  }

  // Kinky
  if (strpos($name, 'Kinky') !== FALSE || strpos($name, 'Charlatan') !== FALSE || $i == 938) {
    $locations[$id]['locatie_id'] = 15;
    $l_set = TRUE;
  }

  // Special
  if ($i == 397 || $i == 416 || $i == 417 || $i == 895 || $i == 1087 || $i == 1132) {
    $locations[$id]['locatie_id'] = 22;
    $l_set = TRUE;
  }

  // Kouter
  if ($i == 889 || $i == 891 || $i == 1174 || $i == 1204 || $i == 47 || $i == 13 || $i == 939 || $i == 1214) {
    $locations[$id]['locatie_id'] = 16;
    $l_set = TRUE;
  }

  // Korenmarkt
  if ($i == 608) {
    $locations[$id]['locatie_id'] = 3;
    $l_set = TRUE;
  }

  // Korenlei-Gralei
  if ($i == 423) {
    $locations[$id]['locatie_id'] = 10;
    $l_set = TRUE;
  }


  // Groentenmarkt
  if ($i == 42 || $i == 115) {
    $locations[$id]['locatie_id'] = 12;
    $l_set = TRUE;
  }

  // Emile
  if ($i == 610 || $i == 837) {
    $locations[$id]['locatie_id'] = 18;
    $l_set = TRUE;
  }


  // Sint-baafs
  if ($i == 607) {
    $locations[$id]['locatie_id'] = 2;
    $l_set = TRUE;
  }

  // Beverhout
  if ($i == 43) {
    $locations[$id]['locatie_id'] = 17;
    $l_set = TRUE;
  }

  // Willem
  if ($i == 611) {
    $locations[$id]['locatie_id'] = 19;
    $l_set = TRUE;
  }

  // Veerle
  if ($i == 937 || $i == 726) {
    $locations[$id]['locatie_id'] = 13;
    $l_set = TRUE;
  }

  if (!$l_set) {
    $locations[$id]['locatie_id'] = 10000;
  }

  $i++;
}


$cats = array();
$cats_small = array();
$cats_decoded = json_decode(file_get_contents('gentsefeestencategorien.json'));
$ii = 1;
foreach ($cats_decoded as $caty) {
  $caty = $caty->fields;
  //print_r($caty);
  $cats[$caty->id] = $caty; 
  $cats[$caty->id]->categorie_id = $ii;
  $cats_small[$ii] = $caty->name;
  $ii++;

}

//print_r($cats_small);
file_put_contents('catssum', print_r($cats, 1));
//die();

//print_r($locations);
//print_r($locations_sum);
file_put_contents('locationssum', print_r($locations_sum, 1));
//die();

// Debugging
$debug = isset($argv[1]) ? TRUE : FALSE;

// Decode.
$decode = json_decode($json);
// Create sql queries.
$statements = "";

//print_r($decode);
//die();

$total_events = 0;
$number = 0;
$double = 0;
$lines = array();
$unique_dates = array();
$location_ids_unique = array();
$names = array();
$doubles = array();
foreach ($decode as $key => $jevent) {

  $new_event = $jevent->fields;

  if (empty($new_event->startdate)) {
    //print "Empty start date: " . $new_event->name_nl . "\n";
    continue;
  }

  //if ($new_event->name_nl == 'EXIT - Circumstances') {
  //  continue;
  //}

  //print_r($new_event);
  //die();

  // The scheme is different, convert it.
  $event = new stdClass();
  $full_unix = strtotime($new_event->startdate);
  $unix_day = strtotime(date('d-m-Y', $full_unix));
  //$full_unix += 7200;
  $startuur = date('G:i', $full_unix);
  
  $einduur = '';
  if (!empty($new_event->enddate)) {
    $end_full_unix = strtotime($new_event->enddate);
    //$end_full_unix += 7200;
    $einduur = date('G:i', $end_full_unix);
  }

  // 'all day' is einduur 5:59 and startuur not on 6:00
  if ($einduur == '5:59' && $startuur == '6:00') {
    $startuur = '';
    $einduur = '';
  }

  if ($einduur == '5:59' && $startuur != '6:00') {
    $einduur = date('G:i', $full_unix);
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
  $event->titel = $new_event->name_nl;


  //if (strpos($event->titel, 'Mardi Gras') !== FALSE) {
    $n = $event->titel . $event->datum . $event->startuur . $event->einduur;
    $doubles[] = $n;
    if (isset($names[$n])) {
      $double++;	    
      continue;
    }
    $names[$n] = TRUE;
  //}

  $desc = isset($new_event->description) ? json_decode($new_event->description) : '';
  $event->omschrijving = !empty($desc->nl) ? $desc->nl : '';
  $event->url = isset($new_event->url) ? $new_event->url : '';
  $event->gratis = isset($new_event->isaccessibleforfree) ? (int) $new_event->isaccessibleforfree : 0;

  $location = $new_event->location;
  if (isset($locations[$location])) {
    //print "location found\n";
    //print_r($locations[$location]);
    //die();
    $event->locatie = $locations[$location]['fields']->name_nl;
    $event->locatie_id = $locations[$location]['locatie_id'];
    $event->straat = !empty($locations[$location]['fields']->address_streetaddress) ? $locations[$location]['fields']->address_streetaddress : '';
    $event->huisnummer = '';

    if (!isset($location_ids_unique[$event->locatie_id])) {
      $location_ids_unique[$event->locatie_id] = $event->locatie;
    }

    //print $event->locatie_id . "\n";
  }
  else {
    $event->location = "Anders";
    $event->locatie_id = 1000;
    $event->straat = '';
    print "location not set\n";
  }

  // Ignore location id 383
  if (!empty($event->locatie_id) && $event->locatie_id == 383) {
    //print "location  id 383\n";
    continue;
  }
 
  // ID.
  $uuid = $new_event->id;
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
  //print $event->id . "\n";

  // Categories
  $event->categorie_naam = '';
  $event->categorie_id = 0;
  $catje = isset($new_event->theme) ? explode(';', $new_event->theme) : 'nope';
  //print_r($new_event);
  //print_r($cats);
  if (isset($cats[$catje[0]])) {
    $event->categorie_naam = $cats[$catje[0]]->name;
    $event->categorie_id = $cats[$catje[0]]->categorie_id;
    //print $event->categorie_id . " - " . $event->categorie_naam . "\n";
  }

  // Price 
  $off = !empty($new_event->offers) ? json_decode($new_event->offers) : '';
  //print_r($off)
  if (!empty($off[0]->price)) {
    $event->prijs = $off[0]->price;
  }
  else {
    $event->prijs = '';
  }
  //print "price: " . $event->prijs . "\n";
  $event->prijs_vvk = '';

  // Lat and long are not exported.
  $event->latitude = '';
  $event->longitude = '';
  $event->korting = 0;
  $event->festival = 0;

  // Image.
  $event->afbeelding = '';
  /*if (!empty($new_event->image->thumbnailUrl)) {
    $event->afbeelding = $new_event->image->thumbnailUrl;
  }*/

  if ($debug) {
    //if (strpos($event->titel, 'Soul Shakers (BE)') !== FALSE) {
    //if (strpos($event->titel, 'Ertebrekers') !== FALSE) {
    //if (strpos($event->titel, 'Helder') !== FALSE) {
    if (strpos($event->titel, 'Ponykamp') !== FALSE) {
    //if (strpos($event->titel, 'SHHT') !== FALSE) {
    //if (strpos($event->titel, '25e wandelzoektocht Gentse Feesten') !== FALSE) {
    //if (strpos($event->titel, 'De fantastische Anna') !== FALSE) {
      print_r($new_event);
      print_r($event);
      print "-------------------------------------------\n";
      //print "$full_unix - $unix_day\n";
    }
    //continue;
  }

  // The data contains too much info
  if ($event->datum < 1657836000 || $event->datum > 1658613600) {
    //print "out of range date\n";
    continue;
  }

  //print $new_event->name_nl . ": $unix_day : $startuur - $einduur\n";

  // Omschrijving.
  // They used html this since 2015, so do some fiddling on it.
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
  //  print $event->titel . "\n";
  }

  //print_r($event);

  $lines[] = $query;
  $number++;
  $total_events++;
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

//ksort($location_ids_unique);
//print_r($location_ids_unique);

krsort($unique_dates);
print_r($unique_dates);

print "Total events: $total_events - doubles: " . $double . " \n";

//print_r($names);
//print_r($doubles);

// Write to file.
//print $statements;
file_put_contents('events-2022.data', $statements);

// Write
file_put_contents('uuid_events.json', serialize($uuid_events));
