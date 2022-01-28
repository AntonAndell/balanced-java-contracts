package network.balanced.score.core;

import score.Context;
import score.VarDB;
import score.Address;

import java.math.BigInteger;

public class Asset {
    public final Address assetAddress;
    public Boolean active = false;

    public Asset(Address address) {
        assetAddress = address;
    }

    public String symbol() {
        return (String) Context.call(assetAddress, "symbol");
    }

    public BigInteger totalSupply() {
        return (BigInteger) Context.call(assetAddress, "totalSupply");
    }

    public BigInteger balanceOf(Address address) {
        return (BigInteger) Context.call(assetAddress, "balanceOf", address);
    } 

    public String getPeg() {
        return (String) Context.call(assetAddress, "getPeg");
    }

    public BigInteger priceInLoop() {
        return (BigInteger) Context.call(assetAddress, "priceInLoop");
    }

    public BigInteger lastPriceInLoop() {
        return (BigInteger) Context.call(assetAddress, "lastPriceInLoop");
    }

    public Address getAddress() {
        return (Address) assetAddress;
    }

    public Boolean isActive() {
        return active;
    }
}