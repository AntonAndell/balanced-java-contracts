package network.balanced.score.core;

import score.Context;
import score.VarDB;
import score.Address;

import java.math.BigInteger;

import network.balanced.score.core.Asset;
import network.balanced.score.core.Mintable;

public class Market {
    public int id;
    public Asset collateral;
    public Mintable mintable;
    private BigInteger badDebt = BigInteger.ZERO;
    private BigInteger liquidationPool = BigInteger.ZERO;
    private Boolean dead = false;

    public Market(int id, Asset collateral, Mintable mintable) {
        this.id = id;
        this.collateral = collateral;
        this.mintable = mintable;

    }

    public Mintable getMintable() {
        return mintable;
    }

    public BigInteger getCollateralValue() {
        BigInteger amount = collateral.balanceOf(Context.getAddress());
        return amount.multiply(collateral.lastPriceInLoop());
    }

    public Boolean isDead() {
        if (!mintable.isActive()) {
            return false;
        } else if (dead) {
            return true;
        };

        BigInteger outStanding = mintable.totalSupply().subtract(badDebt);
        BigInteger collateralValue = liquidationPool.multiply(mintable.priceInLoop().divide(collateral.priceInLoop()));
        BigInteger netBadDebt = badDebt.subtract(collateralValue);
        Boolean marketDead = netBadDebt.compareTo(outStanding.divide(BigInteger.valueOf(2))) == 1;

        if (marketDead) {
            dead = true;
        }

        return marketDead;
    }

}