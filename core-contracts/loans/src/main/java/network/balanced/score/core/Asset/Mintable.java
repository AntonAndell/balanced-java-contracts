package network.balanced.score.core;

import score.Context;
import score.VarDB;
import score.Address;

import java.math.BigInteger;

import network.balanced.score.core.Asset;

public class Mintable extends Asset {
    public BigInteger burnedTokens = BigInteger.ZERO;

    public Mintable(Address address) {
        super(address);
    }

    public void mint(Address to, BigInteger amount) {
        Context.call(assetAddress, "mintTo", to, amount);
    }

    public void burn(BigInteger amount) {
        Context.call(assetAddress, "burn", amount);
        burnedTokens = burnedTokens.add(amount);
    }

    public void burnFrom(Address from, BigInteger amount) {
        Context.call(assetAddress, "burnFrom", from, amount);
        burnedTokens = burnedTokens.add(amount);
    }
}