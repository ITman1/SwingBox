
package org.fit.cssbox.swingbox.util;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;

import org.apache.xerces.parsers.DOMParser;
import org.cyberneko.html.HTMLConfiguration;
import org.fit.cssbox.css.CSSNorm;
import org.fit.cssbox.css.DOMAnalyzer;
import org.fit.cssbox.layout.BrowserCanvas;
import org.fit.cssbox.layout.ElementBox;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * This is customizable default implementation of CSSBoxAnalyzer.
 * 
 * @author Peter Bielik
 * @version 1.0
 * @since 1.0 - 29.1.2011
 */
public class DefaultAnalyzer implements CSSBoxAnalyzer
{
    protected org.w3c.dom.Document w3cdoc;
    protected BrowserCanvas canvas;

    @Override
    public ElementBox analyze(InputSource is, URL url, Dimension dim, Charset charset)
            throws Exception
    {
        w3cdoc = parseDocument(is, charset);

        // Create the CSS analyzer
        DOMAnalyzer da = new DOMAnalyzer(w3cdoc, url);
        da.attributesToStyles();
        da.addStyleSheet(null, CSSNorm.stdStyleSheet(), DOMAnalyzer.Origin.AGENT);
        da.addStyleSheet(null, CSSNorm.userStyleSheet(), DOMAnalyzer.Origin.AGENT);
        da.getStyleSheets();
        
        BufferedImage tmpImg = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        canvas = new BrowserCanvas(da.getRoot(), da, url);
        canvas.setImage(tmpImg);
        canvas.getConfig().setLoadImages(true);
        canvas.getConfig().setLoadBackgroundImages(true);
        canvas.createLayout(dim);

        return canvas.getViewport();
    }

    @Override
    public ElementBox update(ElementBox elem, Dimension dim)
            throws Exception
    {
        canvas.createLayout(dim);
        return canvas.getViewport();
    }

    @Override
    public org.w3c.dom.Document getDocument()
    {
        return w3cdoc;
    }

    protected org.w3c.dom.Document parseDocument(org.xml.sax.InputSource is,
            Charset charset) throws SAXException, IOException
    {
        // if custom implemetation is needed, override this method

        DOMParser parser = new DOMParser(new HTMLConfiguration());
        parser.setProperty("http://cyberneko.org/html/properties/names/elems", "lower");
        if (charset != null)
            parser.setProperty("http://cyberneko.org/html/properties/default-encoding", charset.name());
        parser.parse(is);
        return parser.getDocument();
    }

}
