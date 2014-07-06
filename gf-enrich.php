<?php

/**
 * @file
 *
 * This converts the events from the open data website of Gent to generate a rich data page.
 * Download the events file locally in a file called 'events.json':
 * http://datatank.gent.be/Toerisme/GentseFeestenEvents.json
 *
 * @author: swentel - swentel@realize.be
 */

// Get content.
if (!file_exists('events.json')) {
  print "'events.json' file not found.\n";
  exit;
}

// Decode.
$json = file_get_contents('events.json');
$decode = json_decode($json);

$number = 0;
$unique = array();
touch("events.enrich");
$lines = file("events.enrich");
$lines = array_map('trim', $lines);
foreach ($decode->GentseFeestenEvents as $key => $event) {

  // The data contains too much info
  if ($event->datum < 1404943200 || $event->datum > 1406584800) {
    continue;
  }

  // Check category, ignore non relevant categories.
  // Relevant categories are: 12, 13, 14, 15
  if (!in_array($event->categorie_id, array(12, 13, 14, 15))) {
    continue;
  }

  // TODO - remove certain parts of the title, e.g. 'Jonge wolven'.
  // TODO - explode on certain titles, e.g. for 10 days off

  $title = $event->titel;

  // Search on spotify.
  if (!isset($unique['spotify'][$title])) {
    $unique['spotify'][$title] = $title;

    // Check if we did a lookup for this already.
    if (in_array($title ."::spotify", $lines)) {
      continue;
    }

    print "Searching spotify for $title\n";

    $lines[] = $title . "::spotify";
    $xml = simplexml_load_file("http://ws.spotify.com/search/1/artist?q=artist:" . urlencode($title));
    if (!empty($xml)) {
      // TODO match title with results
      $lines[] = $title . "::spotify-link::" . $xml->artist[0]->attributes()->href;
    }

    sleep(1);
  
    $number++;
  }

}


// Write to file.
file_put_contents('events.enrich', implode("\n", $lines));

// Generate page.
if (!file_exists('enrich')) {
  mkdir('enrich');
}

$links = "";
foreach ($lines as $line) {
  $ex = explode("::", $line);
  if ($ex[1] == 'spotify-link') {
    $links .= "<a href=\"" . $ex[2] . "\">" . $ex[0] . "</a><br />";
  }
}

file_put_contents('enrich/enrich.html', $links);

