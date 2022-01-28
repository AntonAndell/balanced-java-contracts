package network.balanced.score.core;

import score.Context;
import score.VarDB;
import score.Address;

import java.math.BigInteger;

import network.balanced.score.core.Asset;

public class Collateral extends Asset {
    public Collateral(Address address) {
        super(address);
    }
}