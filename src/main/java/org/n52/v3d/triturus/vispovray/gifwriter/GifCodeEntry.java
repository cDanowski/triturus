/**
 * Copyright (C) 2007-2015 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of
 * the following licenses, the combination of the program with the linked
 * library is not considered a "derivative work" of the program:
 *
 *  - Apache License, version 2.0
 *  - Apache Software License, version 1.0
 *  - GNU Lesser General Public License, version 3
 *  - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *  - Common Development and Distribution License (CDDL), version 1.0.
 *
 * Therefore the distribution of the program linked with libraries licensed
 * under the aforementioned licenses, is permitted by the copyright holders
 * if the distribution is compliant with both the GNU General Public
 * icense version 2 and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * Contact: Benno Schmidt & Martin May, 52 North Initiative for Geospatial Open Source
 * Software GmbH, Martin-Luther-King-Weg 24, 48155 Muenster, Germany, info@52north.org
 */
package org.n52.v3d.triturus.vispovray.gifwriter;

/**
 * Helper class to implement the <tt>GifEncodedRaster</tt>.
 * @author Benno Schmidt
 * @see GifEncodedRaster
 */
public class GifCodeEntry
{
    private short mPriorCode = -1;
    private short mCode = -1;
    private byte mChar = 0;

    public GifCodeEntry()
    {
        this.reset();
    }

    public boolean isFree()
    {
        return (mCode == -1);
    }

    public void set(short pPriorCode, short pCode, byte pChar)
    {
        mPriorCode = pPriorCode;
        mCode = pCode;
        mChar = pChar;
    }

    public void reset()
    {
        mPriorCode = -1;
        mCode = -1;
        mChar = 0;
    }

    public boolean isMatch(short pCode, byte pChar)
    {
        return (mCode == -1) || ((mPriorCode == pCode) && (mChar == pChar));
    }

    public short getCode()
    {
        return mCode;
    }
}
