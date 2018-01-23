package org.lilliput.chronograph.persistent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.BsonValue;
import org.lilliput.chronograph.common.ExceptionFactory;
import org.lilliput.chronograph.common.Tokens;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class ElementHelper {

	/**
	 * Determines whether the property key/value for the specified element can be
	 * legally set. This is typically used as a pre-condition check prior to setting
	 * a property.
	 *
	 * @param element
	 *            the element for the property to be set
	 * @param key
	 *            the key of the property
	 * @param value
	 *            the value of the property
	 * @throws IllegalArgumentException
	 *             whether the triple is legal and if not, a clear reason message is
	 *             provided
	 */
	public static final void validateProperty(final ChronoElement element, final String key, final Object value)
			throws IllegalArgumentException {
		if (value == null)
			throw ExceptionFactory.propertyValueCanNotBeNull();
		if (key == null)
			throw ExceptionFactory.propertyKeyCanNotBeNull();
		if (key.equals(Tokens.ID))
			throw ExceptionFactory.propertyKeyIdIsReserved();
		if (element != null && (element instanceof ChronoEdge && key.equals(Tokens.LABEL)))
			throw ExceptionFactory.propertyKeyLabelIsReservedForEdges();
		if (key.isEmpty())
			throw ExceptionFactory.propertyKeyCanNotBeEmpty();
		if (!(value instanceof BsonValue))
			throw ExceptionFactory.propertyValueShouldBeInstanceOfBsonValue();
	}

	/**
	 * Extension
	 * 
	 * @param element
	 * @param key
	 * @param value
	 * @throws IllegalArgumentException
	 */
	public static final void validateTimestampProperty(final ChronoElement element, final Long timestamp,
			final String key, final Object value) throws IllegalArgumentException {
		if (null == timestamp)
			throw ExceptionFactory.propertyKeyCanNotBeNull();
	}

	/**
	 * Copy the properties (key and value) from one element to another. The
	 * properties are preserved on the from element. ElementPropertiesRule that
	 * share the same key on the to element are overwritten.
	 *
	 * @param from
	 *            the element to copy properties from
	 * @param to
	 *            the element to copy properties to
	 */
	public static void copyProperties(final ChronoElement from, final ChronoElement to) {
		for (final String key : from.getPropertyKeys()) {
			to.setProperty(key, from.getProperty(key));
		}
	}

	/**
	 * Clear all the properties from an iterable of elements.
	 *
	 * @param elements
	 *            the elements to remove properties from
	 */
	public static void removeProperties(final Iterable<ChronoElement> elements) {
		for (final ChronoElement element : elements) {
			final List<String> keys = new ArrayList<String>();
			keys.addAll(element.getPropertyKeys());
			for (final String key : keys) {
				element.removeProperty(key);
			}
		}
	}

	/**
	 * Remove a property from all elements in the provided iterable.
	 *
	 * @param key
	 *            the property to remove by key
	 * @param elements
	 *            the elements to remove the property from
	 */
	public static void removeProperty(final String key, final Iterable<ChronoElement> elements) {
		for (final ChronoElement element : elements) {
			element.removeProperty(key);
		}
	}

	/**
	 * Renames a property by removing the old key and adding the stored value to the
	 * new key. If property does not exist, nothing occurs.
	 *
	 * @param oldKey
	 *            the key to rename
	 * @param newKey
	 *            the key to rename to
	 * @param elements
	 *            the elements to rename
	 */
	public static void renameProperty(final String oldKey, final String newKey,
			final Iterable<ChronoElement> elements) {
		for (final ChronoElement element : elements) {
			Object value = element.removeProperty(oldKey);
			if (null != value)
				element.setProperty(newKey, value);
		}
	}

	/**
	 * Typecasts a property value. This only works for casting to a class that has a
	 * constructor of the for new X(String). If no such constructor exists, a
	 * RuntimeException is thrown and the original element property is left
	 * unchanged.
	 *
	 * @param key
	 *            the key for the property value to typecast
	 * @param classCast
	 *            the class to typecast to
	 * @param elements
	 *            the elements to have their property typecasted
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void typecastProperty(final String key, final Class classCast,
			final Iterable<ChronoElement> elements) {
		for (final ChronoElement element : elements) {
			final Object value = element.removeProperty(key);
			if (null != value) {
				try {
					element.setProperty(key, classCast.getConstructor(String.class).newInstance(value.toString()));
				} catch (Exception e) {
					element.setProperty(key, value);
					throw new RuntimeException(e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * Determines whether two elements have the same properties. To be true, both
	 * must have the same property keys and respective values must be equals().
	 *
	 * @param a
	 *            an element
	 * @param b
	 *            an element
	 * @return whether the two elements have equal properties
	 */
	public static boolean haveEqualProperties(final ChronoElement a, final ChronoElement b) {
		final Set<String> aKeys = a.getPropertyKeys();
		final Set<String> bKeys = b.getPropertyKeys();

		if (aKeys.containsAll(bKeys) && bKeys.containsAll(aKeys)) {
			for (String key : aKeys) {
				if (!a.getProperty(key).equals(b.getProperty(key)))
					return false;
			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Get a clone of the properties of the provided element. In other words, a
	 * HashMap is created and filled with the key/values of the element's
	 * properties.
	 *
	 * @param element
	 *            the element to get the properties of
	 * @return a clone of the properties of the element
	 */
	public static Map<String, Object> getProperties(final ChronoElement element) {
		final Map<String, Object> properties = new HashMap<String, Object>();
		for (final String key : element.getPropertyKeys()) {
			properties.put(key, element.getProperty(key));
		}
		return properties;
	}

	/**
	 * Set the properties of the provided element using the provided map.
	 *
	 * @param element
	 *            the element to set the properties of
	 * @param properties
	 *            the properties to set as a Map
	 */
	public static void setProperties(final ChronoElement element, final Map<String, Object> properties) {
		for (Map.Entry<String, Object> property : properties.entrySet()) {
			element.setProperty(property.getKey(), property.getValue());
		}
	}

	/**
	 * Set the properties of the provided element using the provided key value
	 * pairs. The var args of Objects must be divisible by 2. All odd elements in
	 * the array must be a String key.
	 *
	 * @param element
	 *            the element to set the properties of
	 * @param keysValues
	 *            the key value pairs of the properties
	 */
	public static void setProperties(final ChronoElement element, final Object... keysValues) {
		if (keysValues.length % 2 != 0)
			throw new IllegalArgumentException("The object var args must be divisible by 2");
		for (int i = 0; i < keysValues.length; i = i + 2) {
			element.setProperty((String) keysValues[i], keysValues[i + 1]);
		}
	}

	/**
	 * A standard method for determining if two elements are equal. This method
	 * should be used by any Element.equals() implementation to ensure consistent
	 * behavior.
	 *
	 * @param a
	 *            The first element
	 * @param b
	 *            The second element (as an object)
	 * @return Whether the two elements are equal
	 */
	public static boolean areEqual(final ChronoElement a, final Object b) {
		if (null == a && b != null)
			return false;
		if (a == b)
			return true;
		if (null == b)
			return false;
		if (!a.getClass().equals(b.getClass()))
			return false;
		return a.toString().equals(((ChronoElement) b).toString());
	}

	/**
	 * Simply tests if the element ids are equal().
	 *
	 * @param a
	 *            the first element
	 * @param b
	 *            the second element
	 * @return Whether the two elements have equal ids
	 */
	public static boolean haveEqualIds(final ChronoElement a, final ChronoElement b) {
		return a.toString().equals(b.toString());
	}

}
