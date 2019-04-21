package com.offz.spigot.custommobs.Builders;

public final class IAttributeBuilder{
    public Double getMaxHealth() {
        return maxHealth;
    }

    public IAttributeBuilder setMaxHealth(Double maxHealth) {
        this.maxHealth = maxHealth;
        return this;
    }

    public Double getMovementSpeed() {
        return movementSpeed;
    }

    public IAttributeBuilder setMovementSpeed(Double movementSpeed) {
        this.movementSpeed = movementSpeed;
        return this;
    }

    public Double getFollowRange() {
        return followRange;
    }

    public IAttributeBuilder setFollowRange(Double followRange) {
        this.followRange = followRange;
        return this;
    }

    public Double getAttackDamage() {
        return attackDamage;
    }

    public IAttributeBuilder setAttackDamage(Double attackDamage) {
        this.attackDamage = attackDamage;
        return this;
    }

    private Double maxHealth;
    private Double movementSpeed;
    private Double followRange;
    private Double attackDamage;
}
