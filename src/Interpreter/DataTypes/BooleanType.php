<?php


namespace heinthanth\Uit\Interpreter\DataTypes;


use JetBrains\PhpStorm\Pure;

class BooleanType implements DataTypeInterface
{
    /**
     * BooleanType constructor.
     * Value of null type as string
     * @param string $value
     */
    public function __construct(public string $value = 'false')
    {
    }

    /**
     * Revert boolean
     */
    #[Pure] public function not(): BooleanType
    {
        $result = $this->value === 'true' ? 'false' : 'true';
        return new BooleanType($result ? 'true' : 'false');
    }

    /**
     * Equal comparison
     * @param DataTypeInterface $next
     * @return BooleanType
     */
    #[Pure] public function equal(DataTypeInterface $next): BooleanType
    {
        $result = boolval($this->value) === boolval($next->value);
        return new BooleanType($result ? 'true' : 'false');
    }

    /**
     * Not equal comparison
     * @param DataTypeInterface $next
     * @return BooleanType
     */
    #[Pure] public function notEqual(DataTypeInterface $next): BooleanType
    {
        $result = boolval($this->value) !== boolval($next->value);
        return new BooleanType($result ? 'true' : 'false');
    }

    /**
     * less than comparison
     * @param DataTypeInterface $next
     * @return BooleanType
     */
    #[Pure] public function lessThan(DataTypeInterface $next): BooleanType
    {
        $result = boolval($this->value) < boolval($next->value);
        return new BooleanType($result ? 'true' : 'false');
    }

    /**
     * less than or equal comparison
     * @param DataTypeInterface $next
     * @return BooleanType
     */
    #[Pure] public function lessThanEqual(DataTypeInterface $next): BooleanType
    {
        $result = boolval($this->value) <= boolval($next->value);
        return new BooleanType($result ? 'true' : 'false');
    }

    /**
     * Greater than comparison
     * @param DataTypeInterface $next
     * @return BooleanType
     */
    #[Pure] public function greaterThan(DataTypeInterface $next): BooleanType
    {
        $result = boolval($this->value) > boolval($next->value);
        return new BooleanType($result ? 'true' : 'false');
    }

    /**
     * Greater than or equal comparison
     * @param DataTypeInterface $next
     * @return BooleanType
     */
    #[Pure] public function greaterThanEqual(DataTypeInterface $next): BooleanType
    {
        $result = boolval($this->value) >= boolval($next->value);
        return new BooleanType($result ? 'true' : 'false');
    }

    /**
     * and operator
     * @param DataTypeInterface $next
     * @return BooleanType
     */
    #[Pure] public function and(DataTypeInterface $next): BooleanType
    {
        $result = boolval($this->value) && boolval($next->value);
        return new BooleanType($result ? 'true' : 'false');
    }

    /**
     * or operator
     * @param DataTypeInterface $next
     * @return BooleanType
     */
    #[Pure] public function or(DataTypeInterface $next): BooleanType
    {
        $result = boolval($this->value) || boolval($next->value);
        return new BooleanType($result ? 'true' : 'false');
    }
}
