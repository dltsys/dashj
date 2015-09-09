/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.bitcoinj.params;

import org.bitcoinj.core.*;

import java.math.BigInteger;

/**
 * Network parameters used by the bitcoinj unit tests (and potentially your own). This lets you solve a block using
 * {@link org.bitcoinj.core.Block#solve()} by setting difficulty to the easiest possible.
 */
public class UnitTestParams extends AbstractBitcoinNetParams {
    // A simple static key/address for re-use in unit tests, to speed things up.
    public static ECKey TEST_KEY = new ECKey();
    public static Address TEST_ADDRESS;

    public UnitTestParams() {
        super();
        id = ID_UNITTESTNET;
        packetMagic = 0x0b110907;
        addressHeader = CoinDefinition.testnetAddressHeader;
        p2shHeader = CoinDefinition.testnetp2shHeader;
        acceptableAddressCodes = new int[] { addressHeader, p2shHeader };
        maxTarget = new BigInteger("00ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff", 16);
        genesisBlock.setTime(System.currentTimeMillis() / 1000);
        genesisBlock.setDifficultyTarget(Block.EASIEST_DIFFICULTY_TARGET);
        genesisBlock.solve();
        port = CoinDefinition.TestPort;
        interval = 10;
        dumpedPrivateKeyHeader = 128 + CoinDefinition.testnetAddressHeader;
        targetTimespan = 200000000;  // 6 years. Just a very big number.
        spendableCoinbaseDepth = 5;
        subsidyDecreaseBlockCount = 100;
        dnsSeeds = null;
        addrSeeds = null;
        bip32HeaderPub = 0x043587CF;
        bip32HeaderPriv = 0x04358394;
    }

    private static UnitTestParams instance;
    public static synchronized UnitTestParams get() {
        if (instance == null) {
            instance = new UnitTestParams();
            TEST_ADDRESS = TEST_KEY.toAddress(instance);
        }
        return instance;
    }

    @Override
    public String getPaymentProtocolId() {
        return "unittest";
    }
}
