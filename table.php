<?php
require_once "spyc.php";

function makeTable($rows) {
    $tbody = array_reduce($rows, function($a, $b){return $a.="<tr><td>".implode("</td><td>",$b)."</td></tr>";});
    $thead = "<tr><th>" . implode("</th><th>", array_keys($rows[0])) . "</th></tr>";
    echo "<table>\n$thead\n$tbody\n</table>";
}

makeTable(spyc_load_file("table.yaml"));
?>
