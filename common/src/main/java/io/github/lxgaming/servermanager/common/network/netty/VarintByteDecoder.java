/*
 * Copyright 2021 Alex Thomson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.lxgaming.servermanager.common.network.netty;

import io.netty.util.ByteProcessor;

class VarintByteDecoder implements ByteProcessor {
    
    private int readVarint;
    private int bytesRead;
    private DecodeResult result = DecodeResult.TOO_SHORT;
    
    @Override
    public boolean process(byte value) throws Exception {
        readVarint |= (value & 0x7F) << bytesRead++ * 7;
        if (bytesRead > 3) {
            result = DecodeResult.TOO_BIG;
            return false;
        }
        
        if ((value & 0x80) != 128) {
            result = DecodeResult.SUCCESS;
            return false;
        }
        
        return true;
    }
    
    public int getReadVarint() {
        return readVarint;
    }
    
    public int getBytesRead() {
        return bytesRead;
    }
    
    public DecodeResult getResult() {
        return result;
    }
    
    public enum DecodeResult {
        SUCCESS,
        TOO_SHORT,
        TOO_BIG
    }
}