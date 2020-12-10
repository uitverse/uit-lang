<?php

namespace heinthanth\Uit\Interpreter\Types;

use JetBrains\PhpStorm\NoReturn;
use JetBrains\PhpStorm\Pure;

class NumberType implements DataTypeInterface
{
    /**
     * NumberType constructor.
     * Value of number type as string
     * @param string $value
     */
    public function __construct(public string $value)
    {
    }

    /**
     * add operator. Add number to another Number
     * @param DataTypeInterface $next
     * @return $this
     */
    #[Pure] public function add(DataTypeInterface $next): static
    {
        $result = floatval($this->value) + floatval($next->value);
        if (floor($result) === $result) $result = floor($result);
        return new NumberType(strval($result));
    }

    /**
     * minus operator. Minus another number from that Number
     * @param DataTypeInterface $next
     * @return NumberType
     */
    #[Pure] public function minus(DataTypeInterface $next): NumberType
    {
        $result = floatval($this->value) - floatval($next->value);
        if (floor($result) === $result) $result = floor($result);
        return new NumberType(strval($result));
    }

    /**
     * times operator. Multiply number with another Number
     * @param DataTypeInterface $next
     * @return NumberType
     */
    #[Pure] public function times(DataTypeInterface $next): NumberType
    {
        $result = floatval($this->value) * floatval($next->value);
        if (floor($result) === $result) $result = floor($result);
        return new NumberType(strval($result));
    }

    /**
     * Divide operator. Divide number with another Number
     * @param DataTypeInterface $next
     * @return NumberType
     */
    #[NoReturn]
    public function divide(DataTypeInterface $next): NumberType
    {
        if (floatval($next->value) === 0)
            die("Invalid Operation. Divided By Zero");
        $result = floatval($this->value) / floatval($next->value);
        if (floor($result) === $result) $result = floor($result);
        return new NumberType(strval($result));
    }
}