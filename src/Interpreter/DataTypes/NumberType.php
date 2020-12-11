<?php

namespace heinthanth\Uit\Interpreter\DataTypes;

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
        if (intval($next->value) === 0)
            die("Invalid Operation. Divided By Zero" . PHP_EOL);
        $result = floatval($this->value) / floatval($next->value);
        if (floor($result) === $result) $result = floor($result);
        return new NumberType(strval($result));
    }

    /**
     * Modulo operator. Divide number with another Number
     * @param DataTypeInterface $next
     * @return NumberType
     */
    #[NoReturn]
    public function modulo(DataTypeInterface $next): NumberType
    {
        if (intval($next->value) === 0)
            die("Invalid Operation. Divided By Zero" . PHP_EOL);
        $result = floatval($this->value) % floatval($next->value);
        if (floor($result) === $result) $result = floor($result);
        return new NumberType(strval($result));
    }

    /**
     * Exponent function. Get power of a given number
     * @param DataTypeInterface $next
     * @return NumberType
     */
    #[Pure] public function power(DataTypeInterface $next): NumberType
    {
        $result = pow(floatval($this->value), floatval($next->value));
        if (floor($result) === $result) $result = floor($result);
        return new NumberType(strval($result));
    }

    /**
     * Equal comparison
     * @param DataTypeInterface $next
     * @return BooleanType
     */
    #[Pure] public function equal(DataTypeInterface $next): BooleanType
    {
        $result = floatval($this->value) === floatval($next->value);
        return new BooleanType($result ? 'true' : 'false');
    }

    /**
     * Not equal comparison
     * @param DataTypeInterface $next
     * @return BooleanType
     */
    #[Pure] public function notEqual(DataTypeInterface $next): BooleanType
    {
        $result = floatval($this->value) !== floatval($next->value);
        return new BooleanType($result ? 'true' : 'false');
    }

    /**
     * less than comparison
     * @param DataTypeInterface $next
     * @return BooleanType
     */
    #[Pure] public function lessThan(DataTypeInterface $next): BooleanType
    {
        $result = floatval($this->value) < floatval($next->value);
        return new BooleanType($result ? 'true' : 'false');
    }

    /**
     * less than or equal comparison
     * @param DataTypeInterface $next
     * @return BooleanType
     */
    #[Pure] public function lessThanEqual(DataTypeInterface $next): BooleanType
    {
        $result = floatval($this->value) <= floatval($next->value);
        return new BooleanType($result ? 'true' : 'false');
    }

    /**
     * Greater than comparison
     * @param DataTypeInterface $next
     * @return BooleanType
     */
    #[Pure] public function greaterThan(DataTypeInterface $next): BooleanType
    {
        $result = floatval($this->value) > floatval($next->value);
        return new BooleanType($result ? 'true' : 'false');
    }

    /**
     * Greater than or equal comparison
     * @param DataTypeInterface $next
     * @return BooleanType
     */
    #[Pure] public function greaterThanEqual(DataTypeInterface $next): BooleanType
    {
        $result = floatval($this->value) >= floatval($next->value);
        return new BooleanType($result ? 'true' : 'false');
    }
}
