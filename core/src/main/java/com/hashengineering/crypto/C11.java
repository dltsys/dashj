package com.hashengineering.crypto;

import org.bitcoinj.core.Sha256Hash;

import fr.cryptohash.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Hash Engineering on 4/24/14 for the C11 algorithm
 */
public class C11 {

    private static final Logger log = LoggerFactory.getLogger(C11.class);
    private static boolean native_library_loaded = false;

    static {

        try {
            log.info("Loading c11 native library...");
            System.loadLibrary("c11");
            native_library_loaded = true;
            log.info("Loaded c11 successfully.");
        }
        catch(UnsatisfiedLinkError x)
        {
            log.info("Loading c11 failed: " + x.getMessage());
        }
        catch(Exception e)
        {
            native_library_loaded = false;
            log.info("Loading c11 failed: " + e.getMessage());
        }
    }

    public static byte[] c11Digest(byte[] input, int offset, int length)
    {
        byte [] buf = new byte[length];
        for(int i = 0; i < length; ++i)
        {
            buf[i] = input[offset + i];
        }
        return c11Digest(buf);
    }

    public static byte[] c11Digest(byte[] input) {
        //long start = System.currentTimeMillis();
        try {
            return native_library_loaded ? c11_native(input) : c11(input);
            /*long start = System.currentTimeMillis();
            byte [] result = c11_native(input);
            long end1 = System.currentTimeMillis();
            byte [] result2 = c11(input);
            long end2 = System.currentTimeMillis();
            log.info("c11: native {} / java {}", end1-start, end2-end1);
            return result;*/
        } catch (Exception e) {
            return null;
        }
        finally {
            //long time = System.currentTimeMillis()-start;
            //log.info("C11 Hash time: {} ms per block", time);
        }
    }

    static native byte [] c11_native(byte [] input);


    static byte [] c11(byte header[])
    {
        //Initialize
        Sha512Hash[] hash = new Sha512Hash[11];

        //Run the chain of algorithms
        BLAKE512 blake512 = new BLAKE512();
        hash[0] = new Sha512Hash(blake512.digest(header));

        BMW512 bmw = new BMW512();
        hash[1] = new Sha512Hash(bmw.digest(hash[0].getBytes()));

        Groestl512 groestl = new Groestl512();
        hash[2] = new Sha512Hash(groestl.digest(hash[1].getBytes()));
        
        JH512 jh = new JH512();
        hash[3] = new Sha512Hash(jh.digest(hash[2].getBytes()));

        Keccak512 keccak = new Keccak512();
        hash[4] = new Sha512Hash(keccak.digest(hash[3].getBytes()));

        Skein512 skein = new Skein512();
        hash[5] = new Sha512Hash(skein.digest(hash[4].getBytes()));

        Luffa512 luffa = new Luffa512();
        hash[6] = new Sha512Hash(luffa.digest(hash[5].getBytes()));

        CubeHash512 cubehash = new CubeHash512();
        hash[7] = new Sha512Hash(cubehash.digest(hash[6].getBytes()));

        SHAvite512 shavite = new SHAvite512();
        hash[8] = new Sha512Hash(shavite.digest(hash[7].getBytes()));

        SIMD512 simd = new SIMD512();
        hash[9] = new Sha512Hash(simd.digest(hash[8].getBytes()));

        ECHO512 echo = new ECHO512();
        hash[10] = new Sha512Hash(echo.digest(hash[9].getBytes()));

        return hash[10].trim256().getBytes();
    }
}
