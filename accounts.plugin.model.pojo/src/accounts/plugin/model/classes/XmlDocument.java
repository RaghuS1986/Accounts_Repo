package accounts.plugin.model.classes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XmlDocument {

	/** Reference of document builder */
	private DocumentBuilder mBuilder;

	/** Reference of W3C document */
	private Document mDocument;

	/**
	 * Constructor creating an instance of an empty document. By loading an XML
	 * file the empty document will be replaced by the content of the XML file.
	 * 
	 * @param schema
	 *            reference of schema file. The XML parser will use this schema
	 *            file for content validation of XML files. If null, no schema
	 *            validation will be performed.
	 * @throws SAXException
	 *             if schema validation fails.
	 * @throws ParserConfigurationException
	 *             if document cannot be parsed.
	 */
	public XmlDocument(Object schema) throws SAXException, ParserConfigurationException {

		if ((schema != null) && !(schema instanceof File))
			schema = new File(schema.toString());

		// get document builder factory
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		// set schema if applicable
		if (schema != null) {
			SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
			factory.setSchema(schemaFactory.newSchema((File) schema));
		}

		// create the document
		mBuilder = factory.newDocumentBuilder();

		// create empty document
		mDocument = mBuilder.newDocument();
	}

	/**
	 * Load an XML file and perform schema validation if a schema was specified
	 * in the constructor. The previous content of the document will be
	 * discarded.
	 * 
	 * @param input
	 *            reference of XML input file.
	 * @throws IOException
	 *             if file cannot be opened for reading.
	 */
	public void load(Object input) throws IOException {

		InputStream is = null;

		if (input instanceof String)
			is = new FileInputStream((String) input);
		else if (input instanceof File)
			is = new FileInputStream((File) input);
		else if (input instanceof InputStream)
			is = (InputStream) is;
		else if (input instanceof URL)
			is = ((URL) input).openConnection().getInputStream();
		else
			throw new IOException("Cannot convert object into input stream");

		try {
			// create the document
			mDocument = mBuilder.parse(is);
		} catch (Exception e) {

			// transform to IO exception
			throw (IOException) new IOException(e.toString()).initCause(e);
		} finally {

			try {
				// cleanup
				if (is != null) {
					is.close();
				}
			} catch (Exception ie) {
				// ignore
			}
		}
	}

	/**
	 * Store the document content in an XML file. Due to the nature of the DOM,
	 * the order of attributes is not necessarily preserved.
	 * 
	 * @param output
	 *            reference of output file.
	 * @throws IOException
	 *             if file cannot be opened for writing.
	 */
	public void save(Object output) throws IOException {

		OutputStream os = null;

		if (output instanceof String)
			os = new FileOutputStream((String) output);
		else if (output instanceof File)
			os = new FileOutputStream((File) output);
		else if (output instanceof OutputStream)
			os = (OutputStream) os;
		else if (output instanceof URL)
			os = ((URL) output).openConnection().getOutputStream();
		else
			throw new IOException("Cannot convert object into output stream");

		try {
			Transformer t = TransformerFactory.newInstance().newTransformer();
			t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			t.setOutputProperty(OutputKeys.INDENT, "yes");
			t.transform(new DOMSource(mDocument), new StreamResult(os));
		} catch (Exception e) {

			// transform to IO exception
			throw (IOException) new IOException(e.toString()).initCause(e);
		} finally {
			try {
				// cleanup
				if (os != null) {
					os.close();
				}
			} catch (Exception ie) {
				// ignore
			}
		}
	}

	/**
	 * Retrieve the underlying W3C DOM document.
	 * 
	 * @return reference of underlying document.
	 */
	public Document getDocument() {

		return mDocument;
	}

	/**
	 * Lookup a node in the document tree. If a start node is specified, the
	 * search is done in the subtree defined by the node. If <code>node</code>
	 * is null, the complete document is searched. If <code>elementType</code>
	 * is not null, only element nodes with a name equal to
	 * <code>elementType</code> match the search criteria. If null, any element
	 * node is considered. If <code>attribute</code> is not null, the element
	 * node must have an attribute with the name equal to <code>attribute</code>
	 * . If <code>value</code> is not null, the attribute (or element if no
	 * attribute specified) must have the given value to match the search
	 * criteria.
	 * 
	 * @param node
	 *            reference of start node or null if complete document is to be
	 *            searched.
	 * @param elementType
	 *            name of element or null, if all element nodes in the subtree
	 *            are searched.
	 * @param attribute
	 *            attribute of element or null if no additional attribute
	 *            condition shall be checked.
	 * @param value
	 *            value of attribute (or element) or null if no condition shall
	 *            be checked.
	 * @return reference of first node matching the search criteria or null if
	 *         no matching node found.
	 */
	private Node getNode(Node node, String elementType, String attribute, String value) {

		if (node == null)
			node = mDocument;

		// get child nodes of current node
		NodeList nl = node.getChildNodes();

		// loop through child nodes
		for (int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);

			// only consider element nodes
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				if (matchElement(n, elementType, attribute, value))
					return n;

				if (n.hasChildNodes()) {
					// recurse into children
					n = getNode(n, elementType, attribute, value);

					// return node if found
					if (n != null)
						return n;
				}
			}
		}

		return null;
	}

	/**
	 * Helper method that checks if a given node matches to the search criteria.
	 * 
	 * @param node
	 *            reference of node.
	 * @param elementType
	 *            requested name of element node or null, if all element nodes
	 *            in the subtree are searched.
	 * @param attribute
	 *            attribute of element or null if no additional attribute
	 *            condition shall be checked.
	 * @param value
	 *            requested value of attribute (or element) or null if no
	 *            condition shall be checked.
	 * @return true if element node matches search criteria, false otherwise.
	 */
	private static boolean matchElement(Node node, String elementType, String attribute, String value) {

		// check if no name provided or name match
		if ((elementType == null) || elementType.equals(node.getNodeName())) {
			// check if attribute name specified
			if (attribute != null) {
				if (node.hasAttributes()) {
					// get attribute
					Node attrib = node.getAttributes().getNamedItem(attribute);

					if (attrib != null) {
						// check if no value specified or value matches
						if ((value == null) || (value.equals(attrib.getNodeValue())))
							return true;
					}
				}
			} else {
				// check if no value specified or value matches
				if ((value == null) || (value.equals(getElementValue(node))))
					return true;
			}
		}

		return false;
	}

	/**
	 * Lookup a node in the document tree. If a start node is specified, the
	 * search is done in the subtree defined by the node. If <code>node</code>
	 * is null, the complete document is searched. If <code>elementType</code>
	 * is not null, only element nodes with a name equal to
	 * <code>elementType</code> match the search criteria. If null, any element
	 * node is considered. The parameter <code>condition</code> allows to
	 * specify a side condition that has to be fulfilled for a match. The
	 * condition can have three forms:
	 * <ul>
	 * <li>element node has an attribute with the given name (form:
	 * '&lt;attribute&gt;')</li>
	 * <li>element node has an attribute with the given value (form:
	 * '&lt;attribute&gt;=&lt;value&gt;')</li>
	 * <li>element node has a value equal to <code>condition</code> (form:
	 * '=&lt;value&gt;')</li>
	 * </ul>
	 * 
	 * @param node
	 *            reference of start node or null if complete document is to be
	 *            searched.
	 * @param elementType
	 *            name of element or null, if all element nodes in the subtree
	 *            are searched.
	 * @param condition
	 *            side condition in the form &lt;attribute&gt; OR
	 *            &lt;attribute&gt;=&lt;value&gt; OR =&lt;value&gt;
	 * @return reference of first node matching the search criteria or null if
	 *         no matching node found
	 */
	public Node getNode(Node node, String elementType, String condition) {

		String condAttrib = null;
		String condValue = null;

		if (condition != null) {
			int i = condition.indexOf('=');
			if (i >= 0) {
				condAttrib = condition.substring(0, i);
				condValue = condition.substring(i + 1, condition.length());
			}
		}

		return getNode(node, elementType, condAttrib, condValue);
	}

	/**
	 * Get the value of attribute with name <code>attribute</code> from the
	 * first node matching the search criteria <code>elementType</code> and
	 * <code>condition</code>. For more details on search criterias see
	 * <code>getNode()</code>.
	 * 
	 * @param elementType
	 *            name of element or null, if all element nodes in the subtree
	 *            are searched.
	 * @param condition
	 *            side condition in the form &lt;attribute&gt; OR
	 *            &lt;attribute&gt;=&lt;value&gt; OR =&lt;value&gt;
	 * @param attribute
	 *            name of attribute for which value is requested
	 * @return value of attribute or null if attribute cannot be found (either
	 *         because no matching node was found or node does not have an
	 *         attribute with the given name)
	 */
	public String getAttribute(String elementType, String condition, String attribute) {

		return getAttributeValue(getNode(null, elementType, condition), attribute);
	}

	/**
	 * Get the text value of the first node matching the search criteria
	 * <code>elementType
	 * </code> and <code>condition</code>. For more details on search criterias
	 * see <code>getNode()</code>.
	 * 
	 * @param elementType
	 *            name of element or null, if all element nodes in the subtree
	 *            are searched.
	 * @param condition
	 *            side condition in the form &lt;attribute&gt; OR
	 *            &lt;attribute&gt;=&lt;value&gt; OR =&lt;value&gt;
	 * @return text value of first node matching the search criteria or null if
	 *         no matching node found.
	 */
	public String getValue(String elementType, String condition) {

		return getElementValue(getNode(null, elementType, condition));
	}

	/**
	 * Helper function to retrieve the text value of an element node.
	 * 
	 * @param node
	 *            reference of element node.
	 * @return text value of element node.
	 */
	public static String getElementValue(Node node) {

		// check if node has attributes at all
		if ((node != null) && node.hasChildNodes()) {
			NodeList nl = node.getChildNodes();

			// loop through child nodes
			for (int i = 0; i < nl.getLength(); i++) {
				Node n = nl.item(i);

				// only consider text nodes
				if (n.getNodeType() == Node.TEXT_NODE) {
					return n.getNodeValue();
				}
			}

			// return empty string
			return "";
		}

		return null;
	}

	/**
	 * Helper function to get the value of an attribute of an element node.
	 * 
	 * @param node
	 *            reference of element node.
	 * @param attribute
	 *            attribute name.
	 * @return value of attribute.
	 */
	public static String getAttributeValue(Node node, String attribute) {

		// check if node has attributes at all
		if ((node != null) && node.hasAttributes()) {
			// get attribute
			Node attrib = node.getAttributes().getNamedItem(attribute);

			// check if attribute exists
			if (attrib != null) {
				return attrib.getNodeValue();
			}
		}

		return null;
	}

	/**
	 * Get a list of attributes of a node. If the node has no elements or the
	 * node reference is null, an emtpy array is returned.
	 * 
	 * @param node
	 *            reference of element node.
	 * @return array of attribute nodes.
	 */
	public static Node[] getAttributes(Node node) {

		if ((node != null) && (node.hasAttributes())) {
			NamedNodeMap nl = node.getAttributes();

			// convert map to array
			Node[] na = new Node[nl.getLength()];
			for (int i = 0; i < na.length; i++)
				na[i] = nl.item(i);

			return na;
		}

		return new Node[0];
	}

	/**
	 * Get child element nodes matching the given search criteria. If both
	 * <code>elementType</code> and <code>attribute</code> parameters are null,
	 * the complete list of all child element nodes is returned.
	 * 
	 * @param node
	 *            reference of element node.
	 * @param elementType
	 *            name of child element node or null if any element node.
	 * @param condition
	 *            optional side condition in the form &lt;attribute&gt; OR
	 *            &lt;attribute&gt;=&lt;value&gt; OR =&lt;value&gt;
	 * @return list of element nodes matching the search criteria.
	 */
	public static Node[] getElements(Node node, String elementType, String condition) {

		if (node != null) {
			Vector<Node> el = new Vector<Node>();
			String condAttrib = null;
			String condValue = null;

			if (condition != null) {
				int i = condition.indexOf('=');
				if (i >= 0) {
					condAttrib = condition.substring(0, i);
					condValue = condition.substring(i + 1, condition.length());
				}
			}

			// get child nodes of current node
			NodeList nl = node.getChildNodes();

			// loop through child nodes
			for (int i = 0; i < nl.getLength(); i++) {
				Node n = nl.item(i);

				// only consider element nodes
				if (n.getNodeType() == Node.ELEMENT_NODE) {
					if (matchElement(n, elementType, condAttrib, condValue))
						el.add(n);
				}
			}

			// convert vector to array
			Node[] ea = new Node[el.size()];
			for (int i = 0; i < ea.length; i++)
				ea[i] = el.get(i);

			return ea;
		}

		return new Node[0];
	}
}
