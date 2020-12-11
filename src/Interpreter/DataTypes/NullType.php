<?php


namespace heinthanth\Uit\Interpreter\DataTypes;


use JetBrains\PhpStorm\Pure;

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

    /**
     * Equal comparison
     * @param DataTypeInterface $next
     * @return BooleanType
     */
    #[Pure] public function equal(DataTypeInterface $next): BooleanType
    {
        $result = false;
        if ($next instanceof NullType) $result = true;
        return new BooleanType($result ? 'true' : 'false');
    }

    /**
     * Not equal comparison
     * @param DataTypeInterface $next
     * @return BooleanType
     */
    #[Pure] public function notEqual(DataTypeInterface $next): BooleanType
    {
        $result = true;
        if ($next instanceof NullType) $result = false;
        return new BooleanType($result ? 'true' : 'false');
    }

    /**
     * less than comparison
     * @param DataTypeInterface $next
     * @return BooleanType
     */
    #[Pure] public function lessThan(DataTypeInterface $next): BooleanType
    {
        return new BooleanType('true');
    }

    /**
     * less than or equal comparison
     * @param DataTypeInterface $next
     * @return BooleanType
     */
    #[Pure] public function lessThanEqual(DataTypeInterface $next): BooleanType
    {
        return new BooleanType('true');
    }

    /**
     * Greater than comparison
     * @param DataTypeInterface $next
     * @return BooleanType
     */
    #[Pure] public function greaterThan(DataTypeInterface $next): BooleanType
    {
        return new BooleanType('false');
    }

    /**
     * Greater than or equal comparison
     * @param DataTypeInterface $next
     * @return BooleanType
     */
    #[Pure] public function greaterThanEqual(DataTypeInterface $next): BooleanType
    {
        if ($next instanceof NullType) return new BooleanType('true');
        return new BooleanType('false');
    }
}
