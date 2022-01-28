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

import network.balanced.score.core.Asset;
import network.balanced.score.core.Market;

public class MarketDB {
    private ArrayDB<Market> markets = Context.newArrayDB("markets", Market.class);

    public MarketDB() {
    }

    public int numberOfMarkets() {
        return markets.size();
    }

    public ArrayList<String> getDeadMarkets() {
        ArrayList<String> deadMarkets = new ArrayList<String>();
        for (int i = 0; i < numberOfMarkets(); i++) {
            Market market = markets.get(i);
            if (market.isDead()) {
                deadMarkets.add(market.getMintable().symbol());
            }
        }
        return deadMarkets;
    }

    public Map<String, Address> getAssets() {
        Map <String, Address> assets = new HashMap<String, Address>();
        for (int i = 0; i < numberOfMarkets(); i++) {
            Market market = markets.get(i);
            assets.put(market.mintable.symbol(), market.mintable.getAddress());
            assets.put(market.collateral.symbol(), market.collateral.getAddress());
        }
    
        return assets;
    }

    public Map<String, Address> getCollateralTypes() {
        HashMap<String, Address> assets = new HashMap<String, Address>();
        for (int i = 0; i < numberOfMarkets(); i++) {
            Market market = markets.get(i);
            assets.put(market.collateral.symbol(), market.collateral.getAddress());
        }
    
        return assets;
    }

    public BigInteger getTotalActiveCollateral() {
        BigInteger totalCollateral = BigInteger.ZERO;
        for (int i = 0; i < numberOfMarkets(); i++) {
            Market market = markets.get(i);
            totalCollateral = totalCollateral.add(market.getCollateralValue());
        }

        return totalCollateral;
    }

}