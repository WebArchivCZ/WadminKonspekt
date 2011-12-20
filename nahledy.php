#!/usr/bin/php
<?php

/* begin of configuration */
$wkhtmltoimage = "/home/xrosecky/wkhtmltoimage-i386";
$imagemagick = "/usr/bin/convert";
$dir = "/home/app/nahledy/";
$urls = "/home/app/WadminKonspekt/urls.txt";
$temp = "/tmp/";
/* end of configuration */

function make_preview($id, $page) {
   global $wkhtmltoimage;
   global $imagemagick;
   global $dir;
   global $temp;
   $date = date('Ymd');
   $original_image = $dir . "original_" . "$id" . "_" . $date . ".png";
   $big_image = $dir . "big_" . $id . "_" . "$date" . ".png";
   $small_image = $dir . "small_" . $id . "_" . "$date" . ".png";
   $output = "";
   $return_var = 1;
   $cmd = "$wkhtmltoimage --load-error-handling ignore '$page' '$original_image'";
   print "$cmd\n";
   exec($cmd, $output, $return_var);
   if ($return_var == 0) {
      exec("$imagemagick -resize 120x -crop x80 '$original_image' '$temp/foo0.png'");
      exec("$imagemagick -resize 800x -crop x600 '$original_image' '$temp/foo1.png'");
      exec("mv '$temp/foo0-0.png' '$small_image'");
      exec("mv '$temp/foo1-0.png' '$big_image'");
      return true;
   } else {
      return false;
   }
}

$lines = file($urls);

foreach($lines as $line) {
   $line = trim($line);
   list($id, $sysno, $url) = split(" ", $line);
   print "$id $sysno $url\n";
   make_preview($id, $url);
}

?>

