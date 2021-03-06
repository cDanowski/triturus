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
package org.n52.v3d.triturus.examples.pointfeatures;

import org.n52.v3d.triturus.gisimplm.GmAttrFeature;
import org.n52.v3d.triturus.gisimplm.GmPoint;
import org.n52.v3d.triturus.t3dutil.*;
import org.n52.v3d.triturus.t3dutil.symboldefs.T3dBox;
import org.n52.v3d.triturus.vgis.VgAttrFeature;
import org.n52.v3d.triturus.vgis.VgGeomObject;
import org.n52.v3d.triturus.vgis.VgPoint;
import org.n52.v3d.triturus.viskml.KmlScene;

import java.util.ArrayList;
import java.util.List;

/**
 * Triturus example application: Shows how to export points of interest (POIs) to KML.
 *
 * @author Benno Schmidt
 */
public class KmlSceneExample 
{
    public static void main(String args[])
    {
    	KmlSceneExample app = new KmlSceneExample();
        app.run();
    }

    private void run() 
    {
    	KmlScene s = new KmlScene();

        // 1. Generate some random point geometries and add them to scene:
    	for (int i = 0; i < 10; i++) {
    		VgPoint p = new GmPoint(7.5 + Math.random(), 51.5 + Math.random(), 0.0);
        	p.setSRS("EPSG:4326");
        	s.add(p);
    	}
   	
        // 2. Generate some point features and add them:
        List<VgAttrFeature> fList = this.constructFeatures();
        for (VgAttrFeature f : fList) {
            s.add(f);
        }

        // 3. Add some symbols referring to a point location and add them:
    	T3dSymbolDef sym = new T3dBox(1., 1., 10.); // define marker symbol
        MpQuantitativeValue2Color colMapper = this.defineThematicColorMapping(); // get thematic color mapping
        // Define marker symbol instances...
        List<T3dSymbolInstance> mList = new ArrayList<T3dSymbolInstance>();
        for (VgAttrFeature f : fList) {
            T3dSymbolInstance m = new T3dSymbolInstance(sym, (VgPoint)(f.getGeometry()));
            T3dColor col = colMapper.transform(f.getAttributeValue("temperature"));
            m.setColor(col);
            m.setScale(0.01 * ((Double) f.getAttributeValue("temperature"))); // TODO nur in z-Richtung skal.
            mList.add(m);
        }
        // ... and add them to scene:
        for (T3dSymbolInstance m : mList) {
            s.add(m);
        }

    	s.generateScene("C:\\temp\\test.kml");
    }

    private List<VgAttrFeature> constructFeatures()
    {
        GmAttrFeature f1 = new GmAttrFeature();
        VgGeomObject geom1 = new GmPoint(57.37, 42.34, 1.2);
        geom1.setSRS("EPSG:4326");
        f1.setGeometry(geom1);
        f1.addAttribute("temperature", "double", 18.8);
        f1.addAttribute("station", "String", "City_Hall");

        GmAttrFeature f2 = new GmAttrFeature();
        VgGeomObject geom2 = new GmPoint(65.31, 48.12, 9.8);
        geom2.setSRS("EPSG:4326");
        f2.setGeometry(geom2);
        f2.addAttribute("temperature", "double", 15.8);
        f2.addAttribute("station", "String", "Railway_Station");

        GmAttrFeature f3 = new GmAttrFeature();
        VgGeomObject geom3 = new GmPoint(60.26, 49.05, 3.0);
        geom3.setSRS("EPSG:4326");
        f3.setGeometry(geom3);
        f3.addAttribute("temperature", "double", 16.8);
        f3.addAttribute("station", "String", "High_Street");

        List<VgAttrFeature> res = new ArrayList<VgAttrFeature>();
        res.add(f1);
        res.add(f2);
        res.add(f3);
        return res;
    }

    private MpQuantitativeValue2Color defineThematicColorMapping()
    {
        MpQuantitativeValue2Color colMapper = new MpSimpleHypsometricColor();
        double val[] = {0., 10, 20.};
        T3dColor cols[] = {
            new T3dColor(0.0f, 0.0f, 1.0f), // blue
            new T3dColor(0.0f, 1.0f, 1.0f), // cyan
            new T3dColor(1.0f, 0.0f, 0.0f)}; // red
        ((MpSimpleHypsometricColor) colMapper).setPalette(val, cols, true);
        return colMapper;
    }
}
