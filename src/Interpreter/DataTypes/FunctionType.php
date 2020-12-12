<?php


namespace heinthanth\Uit\Interpreter\DataTypes;


use heinthanth\Uit\Interpreter\Interpreter;
use heinthanth\Uit\Interpreter\Memory\Memory;
use heinthanth\Uit\Interpreter\Memory\SymbolTable;
use heinthanth\Uit\Parser\OperationNode\OperationNodeInterface;

class FunctionType implements DataTypeInterface
{
    public function __construct(public string $name, public OperationNodeInterface $expression, public array $arguments)
    {
    }

    /**
     * Invoke function
     * @param array $arguments
     * @param Memory $memory Parent Memory
     * @return DataTypeInterface
     */
    public function invoke(array $arguments, Memory $memory): DataTypeInterface
    {
        $symbolTable = new SymbolTable();
        $tempMem = new Memory($symbolTable, $memory);
        if (count($arguments) > count($this->arguments)) {
            die("Error: Syntax Error. Too many arguments to call function" . PHP_EOL);
        } elseif (count($arguments) < count($this->arguments)) {
            die("Error: Syntax Error. Too few arguments to call function" . PHP_EOL);
        }
        for ($i = 0; $i < count($this->arguments); $i++) {
            $localName = $this->arguments[$i];
            // since our symbol table is accepting only DataType, we need to convert Node to DataType.
            // So, convert OperationNode to DataType with temporary Interpreter.
            $interpreter = new Interpreter($memory);
            $localValue = $interpreter->interpret($arguments[$i]);
            $tempMem->symbols->set($localName, $localValue);
        }
        $interpreter = new Interpreter($tempMem);
        return $interpreter->interpret($this->expression);
    }
}
