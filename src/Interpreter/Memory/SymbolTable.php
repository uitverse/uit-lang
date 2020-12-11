<?php


namespace heinthanth\Uit\Interpreter\Memory;


use heinthanth\Uit\Interpreter\DataTypes\DataTypeInterface;

class SymbolTable
{
    /**
     * List of variable symbols
     * @var array
     */
    private array $symbols = [];

    // TODO need to get datatype of parent
    /**
     * Parent of symbol tables
     * @var mixed|null
     */
    private mixed $parent = null;

    /**
     * Get variable
     * @param string $name
     * @return DataTypeInterface
     */
    public function get(string $name): DataTypeInterface
    {
        if (isset($this->symbols[$name])) {
            return $this->symbols[$name];
        } else {
            if ($this->parent) {
                return $this->parent->get($name);
            }
        }
        die("Error: variable not defined" . PHP_EOL);
    }

    /**
     * Set variable to symbol table
     * @param string $name
     * @param DataTypeInterface $value
     */
    public function set(string $name, DataTypeInterface $value)
    {
        $this->symbols[$name] = $value;
    }

    /**
     * Remove variable from symbol table
     * @param string $name
     */
    public function remove(string $name)
    {
        unset($this->symbols[$name]);
    }
}
