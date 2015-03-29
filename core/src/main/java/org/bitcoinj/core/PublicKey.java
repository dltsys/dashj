package org.bitcoinj.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * Created by Hash Engineering on 2/10/2015.
 */
public class PublicKey extends ChildMessage {
    private static final Logger log = LoggerFactory.getLogger(PublicKey.class);

    byte [] bytes;

    private transient int optimalEncodingMessageSize;


    PublicKey()
    {
        super();
    }

    public PublicKey(NetworkParameters params, byte[] payloadBytes)
    {
        super(params, payloadBytes, 0, null, false, false, payloadBytes.length);
    }

    public PublicKey(NetworkParameters params, byte[] payloadBytes, int cursor, Message parent, boolean parseLazy, boolean parseRetain)
    {
        super(params, payloadBytes, cursor, parent, parseLazy, parseRetain, payloadBytes.length);
    }

    @Override
    protected void parseLite() throws ProtocolException {
        if (parseLazy && length == UNKNOWN_LENGTH) {
            //If length hasn't been provided this tx is probably contained within a block.
            //In parseRetain mode the block needs to know how long the transaction is
            //unfortunately this requires a fairly deep (though not total) parse.
            //This is due to the fact that transactions in the block's list do not include a
            //size header and inputs/outputs are also variable length due the contained
            //script so each must be instantiated so the scriptlength varint can be read
            //to calculate total length of the transaction.
            //We will still persist will this semi-light parsing because getting the lengths
            //of the various components gains us the ability to cache the backing bytearrays
            //so that only those subcomponents that have changed will need to be reserialized.

            //parse();
            //parsed = true;
            length = calcLength(payload, offset);
            cursor = offset + length;
        }
    }
    protected static int calcLength(byte[] buf, int offset) {
        VarInt varint;
        // jump past version (uint32)
        int cursor = offset;// + 4;
        //vin TransactionInput
        varint = new VarInt(buf, cursor);
        long len = varint.value;
        // 4 = length of sequence field (unint32)
        cursor += len;

        return cursor - offset;
    }
    @Override
    void parse() throws ProtocolException {
        if(parsed)
            return;

        cursor = offset;

        optimalEncodingMessageSize = 0;

        bytes = readByteArray();

        optimalEncodingMessageSize += VarInt.sizeOf(bytes.length) + bytes.length;

        length = cursor - offset;
    }
    @Override
    protected void bitcoinSerializeToStream(OutputStream stream) throws IOException {

        stream.write(new VarInt(bytes.length).encode());
        stream.write(bytes);
    }

    long getOptimalEncodingMessageSize()
    {
        if (optimalEncodingMessageSize != 0)
            return optimalEncodingMessageSize;
        maybeParse();
        if (optimalEncodingMessageSize != 0)
            return optimalEncodingMessageSize;
        optimalEncodingMessageSize = getMessageSize();
        return optimalEncodingMessageSize;
    }

    public String toString()
    {
        return "public key:  " + Utils.HEX.encode(bytes);

    }

    public byte [] getBytes() { maybeParse(); return bytes; }

    public boolean equals(Object o)
    {
        maybeParse();
       PublicKey key = (PublicKey)o;
        if(key.bytes.length == this.bytes.length)
        {
            if(Arrays.equals(key.bytes, this.bytes) == true)
                return true;
        }
        return false;
    }


}
