<?php

namespace heinthanth\Uit\Interpreter\DataTypes;

use JetBrains\PhpStorm\Pure;

class StringType implements DataTypeInterface
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
    #[Pure] public function add(DataTypeInterface $next): StringType
    {
        $result = strval($this->value) . strval($next->value);
        return new StringType($result);
    }

    /**
     * times operator. Multiply number with another Number
     * @param DataTypeInterface $next
     * @return StringType
     */
    #[Pure] public function times(DataTypeInterface $next): StringType
    {
        $result = str_repeat(strval($this->value), intval($next->value));
        return new StringType($result);
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
}
