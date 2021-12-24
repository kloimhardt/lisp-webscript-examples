<?php

#use Phel\Phel;

#$projectRootDir = __DIR__ . '/./';

#require $projectRootDir . 'vendor/autoload.php';

#Phel::run($projectRootDir, 'app\\server');

# following lines are used to call compiled version

 use Phel\Compiler\Analyzer\Environment\GlobalEnvironmentSingleton;
// include 'vendor.phar';
// require_once "autoload.php";

 require_once __DIR__ ."/vendor/autoload.php";
 GlobalEnvironmentSingleton::initialize(); // I try to ride of this line in a later release.
 require_once __DIR__ . "/out/app/server.php";
