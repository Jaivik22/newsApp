<?php

class DataBaseConfig
{
    public $servername;
    public $username;
    public $password;
    public $databasename;

    public function __construct()
    {

        $this->servername = '127.0.0.1:8111';
        $this->username = 'root';
        $this->password = '';
        $this->databasename = 'newsapp';

    }
}

?>
