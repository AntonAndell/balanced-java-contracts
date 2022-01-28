package network.balanced.score.core;

import score.Context;
import score.VarDB;
import score.Address;

import java.util.HashMap;
import java.util.Map;

import java.math.BigInteger;

import network.balanced.score.core.Asset;

public class Position {
    private HashMap<String, BigInteger> collateral;
    private HashMap<String, BigInteger> debt;

    public Position() {
    }

    public BigInteger getCollateralValue(String symbol) {
        return collateral.get(symbol);
    }

    public void addCollateral(String symbol, BigInteger amount) {
        BigInteger newAmount = collateral.getOrDefault(symbol, BigInteger.ZERO).add(amount);
        collateral.put(symbol, newAmount);
    }

    public BigInteger getDebt(String symbol) {
        return debt.get(symbol);
    }

    public void addDebt(String symbol, BigInteger amount) {
        BigInteger newAmount = debt.getOrDefault(symbol, BigInteger.ZERO).add(amount);
        debt.put(symbol, newAmount);
    }
}