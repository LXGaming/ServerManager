/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package io.github.lxgaming.servermanager.common.network.util;

import io.netty.buffer.ByteBuf;
import io.netty.util.AsciiString;
import io.netty.util.internal.MathUtil;
import io.netty.util.internal.StringUtil;

public class ByteBufUtil {
    
    private static CharSequence checkCharSequenceBounds(CharSequence seq, int start, int end) {
        if (MathUtil.isOutOfBounds(start, end - start, seq.length())) {
            throw new IndexOutOfBoundsException("expected: 0 <= start(" + start + ") <= end (" + end
                    + ") <= seq.length(" + seq.length() + ')');
        }
        return seq;
    }
    
    /**
     * Returns the exact bytes length of UTF8 character sequence.
     * <p>
     * This method is producing the exact length according to {@link io.netty.buffer.ByteBufUtil#writeUtf8(ByteBuf, CharSequence)}.
     */
    public static int utf8Bytes(final CharSequence seq) {
        return utf8ByteCount(seq, 0, seq.length());
    }
    
    /**
     * Equivalent to <code>{@link #utf8Bytes(CharSequence) utf8Bytes(seq.subSequence(start, end))}</code>
     * but avoids subsequence object allocation.
     * <p>
     * This method is producing the exact length according to {@link io.netty.buffer.ByteBufUtil#writeUtf8(ByteBuf, CharSequence, int, int)}.
     */
    public static int utf8Bytes(final CharSequence seq, int start, int end) {
        return utf8ByteCount(checkCharSequenceBounds(seq, start, end), start, end);
    }
    
    private static int utf8ByteCount(final CharSequence seq, int start, int end) {
        if (seq instanceof AsciiString) {
            return end - start;
        }
        int i = start;
        // ASCII fast path
        while (i < end && seq.charAt(i) < 0x80) {
            ++i;
        }
        // !ASCII is packed in a separate method to let the ASCII case be smaller
        return i < end ? (i - start) + utf8BytesNonAscii(seq, i, end) : i - start;
    }
    
    private static int utf8BytesNonAscii(final CharSequence seq, final int start, final int end) {
        int encodedLength = 0;
        for (int i = start; i < end; i++) {
            final char c = seq.charAt(i);
            // making it 100% branchless isn't rewarding due to the many bit operations necessary!
            if (c < 0x800) {
                // branchless version of: (c <= 127 ? 0:1) + 1
                encodedLength += ((0x7f - c) >>> 31) + 1;
            } else if (StringUtil.isSurrogate(c)) {
                if (!Character.isHighSurrogate(c)) {
                    encodedLength++;
                    // WRITE_UTF_UNKNOWN
                    continue;
                }
                // Surrogate Pair consumes 2 characters.
                if (++i == end) {
                    encodedLength++;
                    // WRITE_UTF_UNKNOWN
                    break;
                }
                if (!Character.isLowSurrogate(seq.charAt(i))) {
                    // WRITE_UTF_UNKNOWN + (Character.isHighSurrogate(c2) ? WRITE_UTF_UNKNOWN : c2)
                    encodedLength += 2;
                    continue;
                }
                // See https://www.unicode.org/versions/Unicode7.0.0/ch03.pdf#G2630.
                encodedLength += 4;
            } else {
                encodedLength += 3;
            }
        }
        return encodedLength;
    }
}