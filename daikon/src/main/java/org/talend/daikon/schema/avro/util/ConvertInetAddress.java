package org.talend.daikon.schema.avro.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

import org.apache.avro.Schema;
import org.talend.daikon.schema.type.AvroConverter;

public class ConvertInetAddress implements AvroConverter<InetAddress, byte[]> {

    @Override
    public Schema getSchema() {
        return Schema.createUnion( //
                Arrays.asList(Schema.createFixed("Inet4", null, null, 4), //
                        Schema.createFixed("Inet6", null, null, 16)));
    }

    @Override
    public Class<InetAddress> getDatumClass() {
        return InetAddress.class;
    }

    @Override
    public InetAddress convertToDatum(byte[] value) {
        try {
            return value == null ? null : InetAddress.getByAddress(value);
        } catch (UnknownHostException e) {
            // TODO(rskraba): Error handling
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] convertToAvro(InetAddress value) {
        return value == null ? null : value.getAddress();
    }

}