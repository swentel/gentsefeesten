<?php

$url = "http://gfapi.timleytens.be/gf-api/events/list.json?key=XeqAsustujew6re3&secret=trezenu3uzuDrecE4upruCruq5ba4tec";

// Get content.
$json = file_get_contents($url);

// Decode.
$decode = json_decode($json);

// Create sql queries.
$statements = "";

$number = 0;
$lines = array();
foreach ($decode as $event) {

  // Omschrijving.
  $new = str_replace("\r", "", $event->omsch);
  $new = str_replace("\n", "|NEWLINE|", $new);
  $event->omsch = $new;

  // Locatie.
  $loc = str_replace("\r", "", $event->loc);
  $loc = str_replace("\n", "|NEWLINE|", $loc);
  $event->loc = $loc;

  $query = "('" . my_mysql_escape_string($event->title) . "',";
  $query .= "'" . $event->id . "',";
  $query .= "'" . $event->gratis . "',";
  $query .= "'" . my_mysql_escape_string($event->prijs) . "',";
  $query .= "'" . my_mysql_escape_string($event->prijs_vvk) . "',";
  $query .= "'" . my_mysql_escape_string($event->omsch) . "',";
  $query .= "'" . ($event->datum + 7200) . "',";
  $query .= "'" . my_mysql_escape_string($event->periode) . "',";
  $query .= "'" . my_mysql_escape_string($event->start) . "',";

  // Date sort is broken in so many ways. We thus take the timestamp
  // and add the sort which is in the format of hhmm (wihout leading 0)
  // for times before 12 and add calculate a sort which we can
  // actually use for decent sorting.
  $sort = $event->sort;
  $timestamp = 0;
  if (!empty($sort)) {
    $minutes = substr($sort, -2);
    if (strlen($sort) == 3) {
      $hours = substr($sort, 0, 1);
      echo "$hours - $sort\n";
    }
    else {
    }
    $total = ($hours * 3600) + $minutes;
    $timestamp = $event->datum + $total + 7200; // + two hours because datum is in GMT.
  }

  $query .= "" . $timestamp . ",";
  $query .= "'" . my_mysql_escape_string($event->cat) . "',";
  $query .= "'" . $event->cat_id . "',";
  $query .= "'" . my_mysql_escape_string($event->url) . "',";
  $query .= "'" . $event->loc_id . "',";
  $query .= "'" . my_mysql_escape_string($event->loc) . "',";
  $query .= "'" . $event->lat . "',";
  $query .= "'" . $event->lon . "',";
  $query .= "'" . my_mysql_escape_string($event->korting) . "',";
  $query .= "'" . $event->festival . "'";
  $query .= ")";

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


// Write to file.
file_put_contents('events-2014.data', $statements);
