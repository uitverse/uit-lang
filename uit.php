<?php

use heinthanth\Uit\Core\Uit;

require_once __DIR__ . '/vendor/autoload.php';

// remove first Element from arguments
$args = $argv;
array_shift($args);

// and run with argument
$uit = new Uit();
$uit->run($args);
