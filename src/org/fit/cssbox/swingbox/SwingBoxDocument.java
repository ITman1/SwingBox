/**
 *
 */
package org.fit.cssbox.swingbox;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Element;

import org.fit.cssbox.swingbox.util.Anchor;
import org.fit.cssbox.swingbox.util.Constants;

/**
 * @author Peter Bielik
 * @version 1.0
 * @since 1.0 - 28.9.2010
 */
public class SwingBoxDocument extends DefaultStyledDocument{
    private static final long serialVersionUID = 5342259762698268312L;

    /**
     *Custom implementation of Document, used in SwingBox
     */
    public SwingBoxDocument() {
	super();
	//we do not suuport any inserting, removing or replacing of string & no filters
	setDocumentFilter(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void insert(int offset, ElementSpec[] data)
    throws BadLocationException {
	//we need this method, so we are "re-visibling" it
	super.insert(offset, data);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void create(ElementSpec[] data) {
	//we need this method, so we are "re-visibling" it
	super.create(data);
    }



    /**
     * {@inheritDoc}
     */
    @Override
    protected AbstractElement createDefaultRoot() {
	try {
	    writeLock();
	    BranchElement delegate = new DelegateElement(Constants.VIEWPORT);
	    delegate.addAttribute(Constants.ATTRIBUTE_ANCHOR_REFERENCE, new Anchor());

	    return delegate;
	} finally {
	    writeUnlock();
	}
    }


    /**
     * The Class DelegateElement.
     */
    public class DelegateElement extends BranchElement {
	private static final long serialVersionUID = 5636867648057150930L;
	private LeafElement DEFAULT_CONTENT;
	private String delegateName;
	/**
	 * Creates a new SectionElement.
	 * @param delegateName the name of element, we are interested in.
	 */
	public DelegateElement(String delegateName) {
	    super(null, null);

	    this.delegateName = delegateName;
	    DEFAULT_CONTENT = new LeafElement(this, null, 0, 1);
	    replace(0, 0, new Element[] {DEFAULT_CONTENT});
	}

	/**
     * Gets the delegate name.
     *
     * @return the delegate name
     */
	public String getDelegateName() {
	    //this is not a class-name, but name of a Element
	    //this name is used in DelegateView to filter & get desired view
	    return delegateName;
	}

	/**
     * Gets the default content.
     *
     * @return the default content, instance of LabelView.
     */
	public Element getDefaultContent() {
	    return DEFAULT_CONTENT;
	}

	/* (non-Javadoc)
	 * @see javax.swing.text.AbstractDocument.BranchElement#replace(int, int, javax.swing.text.Element[])
	 */
	@Override
	public void replace(int offset, int length, Element[] elems) {

	    javax.swing.text.Element els[];
	    if (elems.length > 0) {
	    javax.swing.text.Element data = elems[0];

		for (int i=0; i<elems.length; i++) {
		    if (delegateName.equals(elems[i].getName())) {
			data = elems[i];
			break;
		    }
		}

		els = new Element[] {data};
	    } else {
		//there are no elems
		els = new Element[] {DEFAULT_CONTENT};
	    }

	    super.replace(0, getElementCount(), els);
	}
	/**
	 * Gets the name of the element.
	 *
	 * @return the name
	 */
	public String getName() {
	    return Constants.DELEGATE;
	    //            return AbstractDocument.SectionElementName;
	}
    }

}
