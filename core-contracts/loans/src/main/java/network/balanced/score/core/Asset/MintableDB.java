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

import network.balanced.score.core.Mintable;

public class MintableDB {
    private ArrayDB<String> mintableSymbols = Context.newArrayDB("mintableAddresses", String.class);
    private DictDB<String, Mintable> collateralTypes = Context.newDictDB("collateralTypes", Mintable.class);

    public MintableDB() {
    }

    public int numberOfAssets() {
        return mintableSymbols.size();
    }

}