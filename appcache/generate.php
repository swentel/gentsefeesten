<?php

// This converts the events from the open data website of Gent for use in the
// as an offline application use Application Cache.

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

$days_of_week = [
  1 => 'Maandag',
  2 => 'Dinsdag',
  3 => 'Woensdag',
  4 => 'Donderdag',
  5 => 'Vrijdag',
  6 => 'Zaterdag',
  0 => 'Zondag',
];

// Default content to work with.
$header = file_get_contents('header.html');
$footer = file_get_contents('footer.html');
$index_text = file_get_contents('index.txt');
$manifest_text = file_get_contents('manifest.appcache.txt');

// Define event content.
$event_content = '';

// Pages for manifest
$pages = [];

// All events
$events = [];

// Get events json.
$json = file_get_contents('events.json');

// Event uuids - ids.
$uuid_events = unserialize(file_get_contents('uuid_events.json'));

// Parse locations.
$locations_decoded = json_decode(file_get_contents('gentsefeestenlocaties.json'));
$locations = [];
$i = 1;
foreach ($locations_decoded as $location) {
  $location = (array) $location;
  $locations[$location['@id']] = $location;
  $locations[$location['@id']]['location_id'] = $i;
  $locations[$location['@id']]['location_name'] = $location['name']->nl;

  // Salsabar, tref.club -> baudelo
  //if ($i == 8 || $i == 6 || $i == 5 || $i == 88 || $i == 351 || $i == 383) {
  if ($i == 8 || $i == 6 || $i == 5 || $i == 88 || $i == 351) {
    $locations[$location['@id']]['location_id'] = 7;
    if (strpos('baudeloo', $locations[$location['@id']]['location_name']) === FALSE) {
      $locations[$location['@id']]['location_name'] .= ' (baudelohof)';
    }
  }

  // Sint-baafs
  if ($i == 248 || $i == 249 || $i == 250) {
    $locations[$location['@id']]['location_name'] = 'Sint-baafs';
    $locations[$location['@id']]['location_id'] = 2;
  }

  // Willem
  if ($i == 20 || $i == 21) {
    $locations[$location['@id']]['location_id'] = 19;
  }

  $i++;
}

// Parse categories.
$ii = 1;
$categories = [];
$cats_decoded = json_decode(file_get_contents('gentsefeestencategorien.json'));
foreach ($cats_decoded as $cat) {
  $cat_array = (array) $cat;
  $categories[$cat_array['@id']] = $cat_array; 
  $categories[$cat_array['@id']]['category_id'] = $ii;
  $ii++;
}

// Debugging
$debug = isset($argv[2]) ? TRUE : FALSE;

// Directory to publish generated content into.
$public_dir = isset($argv[1]) ? $argv[1] . '/' : 'gf17/';

// Decode events.
$decode = json_decode($json);

// Create sql queries.
$statements = "";

$unique_dates = [];
foreach ($decode as $key => $event_object) {

  // Cast to string
  $array = (array) $event_object;

  // The scheme is different, convert it.
  $event = new stdClass();
  $full_unix = strtotime($event_object->startDate);
  $unix_day = strtotime(date('d-m-Y', $full_unix));
  $start_hour = date('G:i', $full_unix);
  
  $end_hour = '';
  if (!empty($event_object->endDate)) {
    $end_full_unix = strtotime($event_object->endDate);
    $end_hour = date('G:i', $end_full_unix);
  }

  // 'all day', ignore those.
  if ($end_hour == '5:59') {
    continue;
  }

  // Switch date one day back if necessary.
  $hour = date('G', $full_unix);
  $compare_date= $full_unix;
  if ($hour >= 0 && $hour < 6) {
    $full_unix -= 86400;
    $unix_day = strtotime(date('d-m-Y', $full_unix));
  }
  // Ignore morning programs.
  elseif ($hour < 12) {
    continue;
  }

  // The data contains too much events outside GF.
  $event->compare_date = $compare_date;
  $event->date = $unix_day;
  if ($event->date < 1499904000 || $event->date > 1500854400) {
    continue;
  }

  $event->start_hour = $start_hour;
  $event->end_hour = $end_hour;
  $event->title = $event_object->name->nl;
  $event->description = !empty($event_object->description->nl) ? $event_object->description->nl : '';
  $event->free = isset($event_object->isAccessibleForFree) ? (int) $event_object->isAccessibleForFree : 0;

  $event->location = '';
  $location = $event_object->location;
  if (isset($locations[$location])) {
    $event->location = $locations[$location]['location_name'];
    $event->location_id = $locations[$location]['location_id'];
    $event->street = !empty($locations[$location]['address']->streetAddress) ? $locations[$location]['address']->streetAddress : '';
    $event->house_number = '';    
  }

  // Ignore location id 383 and 394 (and starting title with DI0
  if (!empty($event->location_id) && ($event->location_id == 383 || ($event->location_id == 7 && substr($event->title, 0, 3) == 'DI:'))) {
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
  $event->category = '';
  $event->category_id = 0;
  $category = isset($event_object->theme[0]) ? $event_object->theme[0] : 'nope';
  if (isset($categories[$category])) {
    $event->category = $categories[$category]['name']->nl;
    $event->category_id = $categories[$category]['category_id'];
  }
 
  // Price 
  if (!empty($event_object->offers[0]->price)) {
    $event->price = "&euro; " . $event_object->offers[0]->price;
  }
  else {
    $event->price = '';
  }

  if ($debug) {
    //if (strpos($event->title, 'Soul Shakers (BE)') !== FALSE) {
    //if (strpos($event->title, 'Ertebrekers') !== FALSE) {
    if (strpos($event->title, 'Bound') !== FALSE) {
    //if (strpos($event->title, 'De fantastische Anna') !== FALSE) {
      print_r($event_object);
      print_r($event);
      print "-------------------------------------------\n";
      //print "$full_unix - $unix_day\n";
    }
    continue;
  }

  // Location.
  $loc = trim($event->location);
  $event->location_full = '';
  if (!empty($event->street)) {
    $street = trim($event->street);
    if (!empty($event->house_number)) {
      $street .= " " . $event->house_number;
    }
    $street = trim($street);
    if ($street != $loc) {
      $loc .= "\n" . $street;
    }
    $loc = str_replace("\n", "<br />", $loc);
    $loc = str_replace("\n", "<br />", $loc);
    $event->location_full = $loc;
  }

  // Keep an array of unique dates.
  $udate = $event->date;
  if (!isset($unique_dates[$udate])) {
    $unique_dates[$udate]['full'] = date('l j', $udate);
    $unique_dates[$udate]['day'] = $days_of_week[date('w', $udate)];
    $unique_dates[$udate]['date'] = date('d', $udate);
  }

  // Hour string.
  $hour_string = "";
  if (!empty($event->start_hour)) {
    $hour_string = $event->start_hour;
    if (!empty($event->end_hour)) {
      $hour_string .= ' - ' . $event->end_hour;
    }
  }
  $event->hour_human = $hour_string;

  // Store event.
  if (!isset($events[$udate])) {
    $events[$udate] = [];
  }
  $events[$udate][] = $event;

}

// Create date links.
$i = 1;
$date_links = [];
ksort($unique_dates);
foreach ($unique_dates as $date) {
  $date_links[] = '<li><a href="/' . $public_dir . $i . '.html"><span class="day">' . $date['day'] . '</span><span class="date">' . $date['date'] . '</span></a></li>';
  $i++;
}
$date_links_html = '<ul class="dates">' . implode(' ', $date_links) . '</ul>';


// Sort on (day) date.
ksort($events);

$i = 1;
foreach ($events as $date => $day_events) {

  $file_name = $i . '.html';
  $pages[] = '/' . $public_dir . $file_name;

  // Start of content, with the links.
  $event_content = '<div>' . $date_links_html . '</div>';
  $event_content .= '<div class="t-s">';

  // Sort on date and title.
  uksort($day_events, function ($a, $b) use ($day_events) {
    if ($day_events[$a]->compare_date != $day_events[$b]->compare_date) {
      return $day_events[$a]->compare_date > $day_events[$b]->compare_date ? 1 : -1;
    }
    return $day_events[$a]->title > $day_events[$b]->title ? 1 : -1;
  });


  foreach ($day_events as $event) {

    $sub_title_content = [];
    if (!empty($event->hour_human)) {
      $sub_title_content[] = $event->hour_human;
    }
    if ($event->free) {
      $sub_title_content[] = 'Gratis';
    }
    if (!empty($event->price)) {
      $sub_title_content[] = $event->price;
    }

    $event_content .= '<div class="fis loc-' . $event->location_id . ' cat-' . $event->category_id . '"><div class="s-t">
    <div class="s-l"><div class="s-ti"><span class="bold">' . $event->title . (!empty($sub_title_content) ? '</span><br />' . implode(' &bull; ', $sub_title_content) : '') . '</span></div><div class="clearfix e-desc field__item" style="display: none;">' . (!empty($event->location_full) ? '<p>' . $event->location_full . '</p>' : '') . $event->description . '</div><div class="room field__item">' . $event->location . '</div><div class="s-tys"><div class="s-ty"><span>' . $event->category . '</span></div></div></div></div></div>';
  }

  // closing div.
  $event_content .= '</div>';

  // Generate day file.
  $program = $header . $event_content . $footer;
  $program = str_replace('{title}', $days_of_week[date('w', $date)] . ' ' . date('j', $date), $program);
  $program = str_replace(array('{program-is-active}', '{index-is-active}', '{public_dir}'), array(' class="is-active"', '', $public_dir), $program);
  file_put_contents($public_dir . $file_name, $program);

  $i++; 
}

function my_mysql_escape_string($val) {
  return $val;
}

// Debug dates.
print_r($unique_dates);

// Generate index file.
$index = $header . $index_text . $footer;
$index = str_replace('{title}', 'Welkom', $index);
$index = str_replace(array('{program-is-active}', '{index-is-active}', '{public_dir}'), array('', ' class="is-active"', $public_dir), $index);
file_put_contents($public_dir . 'index.html', $index);

// Generate manifest.
$manifest_text = str_replace('{timestamp}', $_SERVER['REQUEST_TIME'], $manifest_text);
$manifest_text = str_replace('{pages}', implode("\n", $pages), $manifest_text);
$manifest_text = str_replace('{public_dir}', $public_dir, $manifest_text);
file_put_contents($public_dir . 'manifest.appcache', $manifest_text);

print "Check the manifest.json file\n";

// Write uuid events.
file_put_contents('uuid_events.json', serialize($uuid_events));
