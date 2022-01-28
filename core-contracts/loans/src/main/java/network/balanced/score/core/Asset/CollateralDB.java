package network.balanced.score.core;

import score.Context;
import score.VarDB;
import score.ArrayDB;
import score.DictDB;
import score.Address;

import java.math.BigInteger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import network.balanced.score.core.Collateral;

public class CollateralDB {
    private ArrayDB<String> collateralSymbols = Context.newArrayDB("collateralSymbols", String.class);
    private DictDB<String, Collateral> collateralTypes = Context.newDictDB("collateralTypes", Collateral.class);
    
    public CollateralDB() {
    }

    public int numberOfCollaterTypes() {
        return collateralSymbols.size();
    }

    public BigInteger getTotalActiveCollateral() {
        BigInteger totalCollateral = BigInteger.ZERO;
        for (int i = 0; i < numberOfCollaterTypes(); i++) {
            String symbol = collateralSymbols.get(i);
            Collateral collateral = collateralTypes.get(symbol);
            totalCollateral = totalCollateral.add(collateral.getTotalValue());
        }

        return totalCollateral;
    }

}