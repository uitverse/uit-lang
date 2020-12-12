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

    // todo need to get datatype of parent
    /**
     * Parent of symbol tables
     * @var ?SymbolTable
     */
    public SymbolTable|null $parent = null;

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
        die("Error: variable $name not defined" . PHP_EOL);
    }

    /**
     * Set variable to symbol table
     * @param string $name
     * @param DataTypeInterface $value
     */
    public function set(string $name, DataTypeInterface $value)
    {
        if (isset($this->symbols[$name])) {
            $this->symbols[$name] = $value;
        } else {
            $this->parent?->set($name, $value);
        }
    }

    /**
     * Declare variable
     * @param string $name
     * @param DataTypeInterface $value
     */
    public function declare(string $name, DataTypeInterface $value)
    {
        $this->symbols[$name] = $value;
    }

    /**
     * Remove variable from symbol table
     * @param string $name
     */
    public function remove(string $name): void
    {
        unset($this->symbols[$name]);
    }

    /**
     * Check variable exists or not
     * @param string $name
     * @return bool
     */
    public function isExist(string $name): bool
    {
        return isset($this->symbols[$name]) || $this->parent?->isExist($name);
    }
}
