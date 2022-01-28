package network.balanced.score.core;

import score.Context;
import score.VarDB;
import score.Address;

import java.math.BigInteger;

public class Asset {
    public final VarDB<Address> assetAddress = Context.newVarDB("assetAddress", Address.class);

    public Asset(Address address) {
        assetAddress.set(address);
    }

    public String symbol() {
        return (String) Context.call(assetAddress.get(), "symbol");
    }

    public BigInteger totalSupply() {
        return (BigInteger) Context.call(assetAddress.get(), "totalSupply");
    }

    public BigInteger balanceOf() {
        return (BigInteger) Context.call(assetAddress.get(), "balanceOf");
    }

    public String getPeg() {
        return (String) Context.call(assetAddress.get(), "getPeg");
    }

    public BigInteger priceInLoop() {
        return (BigInteger) Context.call(assetAddress.get(), "priceInLoop");
    }

    public BigInteger lastPriceInLoop() {
        return (BigInteger) Context.call(assetAddress.get(), "lastPriceInLoop");
    }

    public Address getAddress() {
        return (Address) assetAddress.get();
    }
}