<?php


namespace heinthanth\Uit\Interpreter\DataTypes;


class NullType implements DataTypeInterface
{
    /**
     * NullType constructor.
     * Value of null type as string
     * @param string $value
     */
    public function __construct(public string $value = "\0")
    {
    }
}