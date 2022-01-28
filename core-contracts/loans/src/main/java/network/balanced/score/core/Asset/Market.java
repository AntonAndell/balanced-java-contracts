package network.balanced.score.core;

import score.Context;
import score.VarDB;
import score.Address;

import java.math.BigInteger;

import network.balanced.score.core.Asset;
import network.balanced.score.core.Mintable;

public class Market {
    private VarDB<Asset> collateral = Context.newVarDB("collateral", Asset.class);
    private VarDB<Mintable> mintable = Context.newVarDB("mintAble", Mintable.class);
    private VarDB<BigInteger> badDebt = Context.newVarDB("badDebt", BigInteger.class);
    private VarDB<BigInteger> liquidationPool = Context.newVarDB("liquidationPool", BigInteger.class);
    private VarDB<Boolean> dead = Context.newVarDB("dead", Boolean.class);
    
    
    public Market(Asset collateral, Mintable mintable) {
        this.collateral.set(collateral);
        this.mintable.set(mintable);
    }

    public Mintable getMintable() {
        return mintable.get();
    }


    public Boolean isDead() {
        if (!mintable.get().isActive()) {
            return false;
        } else if (dead.get()){
            return true;
        }

        BigInteger badDebt = this.badDebt.get();
        BigInteger outStanding = mintable.get().totalSupply().subtract(badDebt);
        BigInteger collateralValue = liquidationPool.get().multiply(mintable.get().priceInLoop().divide(collateral.get().priceInLoop()));
        BigInteger netBadDebt = badDebt.subtract(collateralValue);
        Boolean marketDead = netBadDebt.compareTo(outStanding.divide(BigInteger.valueOf(2))) == 1;
        if (marketDead) {
            dead.set(true);
        }

        return marketDead;
    }

}