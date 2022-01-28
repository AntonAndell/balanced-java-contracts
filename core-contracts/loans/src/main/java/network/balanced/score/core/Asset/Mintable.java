package network.balanced.score.core;

import score.Context;
import score.VarDB;
import score.Address;

import java.math.BigInteger;

import network.balanced.score.core.Asset;

public class Mintable extends Asset {
    private VarDB<BigInteger> burnedTokens =  Context.newVarDB("burnedTokens", BigInteger.class);
    private VarDB<Boolean> active = Context.newVarDB("active", Boolean.class);

    public Mintable(Address address) {
        super(address);
    }

    public void mint(Address to, BigInteger amount) {
        Context.call(assetAddress.get(), "mintTo", to, amount);
    }

    public void burn(BigInteger amount) {
        Context.call(assetAddress.get(), "burn", amount);
        burnedTokens.set(burnedTokens.get().add(amount));
    }

    public void burnFrom(Address from, BigInteger amount) {
        Context.call(assetAddress.get(), "burnFrom", from, amount);
        burnedTokens.set(burnedTokens.get().add(amount));
    }

    public Boolean isActive() {
        return active.get();
    }

}