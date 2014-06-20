<?php

// This converts the events from the open data website of Gent.

// http://datatank.gent.be/Toerisme/GentseFeestenEvents.json
// http://datatank.gent.be/Cultuur-Sport-VrijeTijd/GentseFeestenLocaties.json
// http://datatank.gent.be/Toerisme/GentseFeestenCategorie%C3%ABn.json
// http://datatank.gent.be/Cultuur-Sport-VrijeTijd/GentseFeestenData.json

// Download the events file locally in a file called 'events.json'.
// You can ignore the other files, their data is small and defined in the
// arrays in the app.

// Get content.
if (!file_exists('events.json')) {
  print "'events.json' file not found.\n";
  exit;
}

$json = file_get_contents('events.json');

// Decode.
$decode = json_decode($json);

// Create sql queries.
$statements = "";

$number = 0;
$lines = array();
$unique_dates = array();
foreach ($decode->GentseFeestenEvents as $key => $event) {

  //print_r($event);
  //echo "\n --------------------------------------------------- \n";
  //continue;

  // The data contains too much info
  if ($event->datum < 1404943200 || $event->datum > 1406584800) {
    continue;
  }

  // Omschrijving.
  $new = str_replace("\r", "", trim($event->omschrijving));
  $new = str_replace("\n", "|NEWLINE|", $new);
  $event->omsch = $new;

  // Locatie.
  $loc = $event->locatie;
  if (!empty($event->straat)) {
    $loc .= "\n" . trim($event->straat);
    if (!empty($event->huisnummer)) {
      $loc .= " " . $event->huisnummer;
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
  $query .= "'" . ($event->datum + 7200) . "',";
  $hour_string = "";
  if (!empty($event->startuur)) {
    $hour_string = $event->startuur . " - " . $event->einduur;
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
    $minutes = substr($sort, -2);
    if (strlen($sort) == 3) {
      $hours = substr($sort, 0, 1);
      echo "$hours - $sort\n";
    }
    else {
      $hours = substr($sort, 0, 2);
      echo "$hours - $sort\n";
    }
    $total = ($hours * 3600) + $minutes;
    $timestamp = $event->datum + $total + 7200; // + two hours because datum is in GMT.
  }

  $query .= "" . $timestamp . ",";
  $query .= "'" . my_mysql_escape_string($event->categorie_naam) . "',";
  $query .= "'" . $event->categorie_id . "',";
  $query .= "'" . my_mysql_escape_string($event->url) . "',";
  $query .= "'" . $event->locatie_id . "',";
  $query .= "'" . my_mysql_escape_string($event->locatie) . "',";
  $query .= "'" . $event->latitude . "',";
  $query .= "'" . $event->longitude . "',";
  $query .= "'" . my_mysql_escape_string($event->korting) . "',";
  $query .= "'" . $event->festival . "'";
  $query .= ")";

  if ($event->festival) {
    print $event->titel . "\n";
  }

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
file_put_contents('events-2014.data', $statements);
