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

import network.balanced.score.core.Position;

class PositionNode {
    public Position position;
    public Address nextAddress;

    PositionNode(Position position, Address address) {
        this.position = position;
        this.nextAddress = address;
    }
}

public class PositionsDB {
    private DictDB<Address, Position> collateralTypes = Context.newDictDB("collateralTypes", Collateral.class);
    private VarDB<Address> head = Context.newVarDB("positionsDBHead", Address.class);
    private VarDB<Address> tail = Context.newVarDB("positionsDBTail", Address.class);

    public PositionsDB() {
    }

    public Position getPostion() {
        return collateralSymbols.size();
    }

    public void setPostion(Position position) {
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