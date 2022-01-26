/*
 * Copyright 2021 ICONLOOP Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package network.balanced.score.core;

import score.Address;
import score.Context;
import score.VarDB;
import score.annotation.External;

import java.math.BigInteger;
import network.balanced.score.core.token1Basic;

public abstract class token1Mintable extends token1Basic {

    private final VarDB<Address> minter = Context.newVarDB("minter", Address.class);

    public token1Mintable(String _name, String _symbol, int _decimals) {
        super(_name, _symbol, _decimals);
        // By default, set the minter role to the owner
        if (minter.get() == null) {
            minter.set(Context.getOwner());
        }
    }

    /**
     * Creates _amount number of tokens, and assigns to caller.
     * Increases the balance of that account and the total supply.
     */
    @External
    public void mint(BigInteger _amount) {
        // simple access control - only the minter can mint new token
        Context.require(Context.getCaller().equals(minter.get()));
        _mint(Context.getCaller(), _amount);
    }

    /**
     * Creates _amount number of tokens, and assigns to _account.
     * Increases the balance of that account and the total supply.
     */
    @External
    public void mintTo(Address _account, BigInteger _amount) {
        // simple access control - only the minter can mint new token
        Context.require(Context.getCaller().equals(minter.get()));
        _mint(_account, _amount);
    }

    @External
    public void setMinter(Address _minter) {
        // simple access control - only the contract owner can set new minter
        Context.require(Context.getCaller().equals(Context.getOwner()));
        minter.set(_minter);
    }
}