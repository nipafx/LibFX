package org.codefx.libfx.collection.transform;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * A {@link Map} which decorates another map and transforms the key and value types from the inner types {@code IK},
 * {@code IV} to outer types {@code OK}, {@code OV}.
 * <p>
 * See the {@link org.codefx.libfx.collection.transform package} documentation for general comments.
 * <p>
 * This implementation mitigates the type safety problems by using tokens of the inner and the outer types to check
 * instances against them. This solves some of the critical situations but not all of them. In those other cases
 * {@link ClassCastException}s might occur when an element can not be transformed by the transformation functions.
 * <p>
 * Null keys and values are allowed. These are handled explicitly and fixed to the transformation {@code null -> null}.
 * The transforming functions specified during construction neither have to handle that case nor must they produce null
 * elements.
 * <p>
 * All method calls (of abstract and default methods existing in JDK 8) are forwarded to <b>the same method</b> on the
 * wrapped map. This implies that all all guarantees made by such methods (e.g. regarding atomicity) are upheld by the
 * transformation.
 *
 * @param <IK>
 *            the inner key type, i.e. the type of the keys contained in the wrapped/inner map
 * @param <OK>
 *            the outer key type, i.e. the type of keys appearing to be in this map
 * @param <IV>
 *            the inner value type, i.e. the type of the values contained in the wrapped/inner map
 * @param <OV>
 *            the outer value type, i.e. the type of values appearing to be in this map
 */
public final class TransformingMap<IK, OK, IV, OV> extends AbstractTransformingMap<IK, OK, IV, OV> {

	// #region FIELDS

	private final Map<IK, IV> innerMap;

	private final Class<OK> outerKeyTypeToken;

	private final Class<IK> innerKeyTypeToken;

	private final Function<IK, OK> transformToOuterKey;

	private final Function<OK, IK> transformToInnerKey;

	private final Class<OV> outerValueTypeToken;

	private final Class<IV> innerValueTypeToken;

	private final Function<IV, OV> transformToOuterValue;

	private final Function<OV, IV> transformToInnerValue;

	// #end FIELDS

	// #region CONSTRUCTION

	/**
	 * Creates a new transforming map.
	 *
	 * @param innerMap
	 *            the wrapped map
	 * @param innerKeyTypeToken
	 *            the token for the inner key type
	 * @param transformToOuterKey
	 *            transforms a key from an inner to an outer key type; will never be called with null argument and must
	 *            not produce null
	 * @param outerKeyTypeToken
	 *            the token for the outer key type
	 * @param transformToInnerKey
	 *            transforms a key from an outer to an inner key type; will never be called with null argument and must
	 *            not produce null
	 * @param innerValueTypeToken
	 *            the token for the inner value type
	 * @param transformToOuterValue
	 *            transforms a value from an inner to an outer value type; will never be called with null argument and
	 *            must not produce null
	 * @param outerValueTypeToken
	 *            the token for the outer value type
	 * @param transformToInnerValue
	 *            transforms a value from an outer to an inner value type; will never be called with null argument and
	 *            must not produce null
	 */
	public TransformingMap(
			Map<IK, IV> innerMap,
			Class<IK> innerKeyTypeToken, Function<IK, OK> transformToOuterKey,
			Class<OK> outerKeyTypeToken, Function<OK, IK> transformToInnerKey,
			Class<IV> innerValueTypeToken, Function<IV, OV> transformToOuterValue,
			Class<OV> outerValueTypeToken, Function<OV, IV> transformToInnerValue) {

		this.innerMap = innerMap;
		this.outerKeyTypeToken = outerKeyTypeToken;
		this.innerKeyTypeToken = innerKeyTypeToken;
		this.transformToOuterKey = transformToOuterKey;
		this.transformToInnerKey = transformToInnerKey;
		this.outerValueTypeToken = outerValueTypeToken;
		this.innerValueTypeToken = innerValueTypeToken;
		this.transformToOuterValue = transformToOuterValue;
		this.transformToInnerValue = transformToInnerValue;
	}

	// #end CONSTRUCTION

	// #region ABSTRACT METHODS FROM SUPERCLASS

	@Override
	protected Map<IK, IV> getInnerMap() {
		return innerMap;
	}

	@Override
	protected boolean isInnerKey(Object object) {
		return object == null || innerKeyTypeToken.isInstance(object);
	}

	@Override
	protected OK transformToOuterKey(IK innerKey) {
		if (innerKey == null)
			return null;

		OK outerKey = transformToOuterKey.apply(innerKey);
		Objects.requireNonNull(outerKey, "The transformation must not create null instances.");
		return outerKey;
	}

	@Override
	protected boolean isOuterKey(Object object) {
		return object == null || outerKeyTypeToken.isInstance(object);
	}

	@Override
	protected IK transformToInnerKey(OK outerKey) {
		if (outerKey == null)
			return null;

		IK innerKey = transformToInnerKey.apply(outerKey);
		Objects.requireNonNull(innerKey, "The transformation must not create null instances.");
		return innerKey;
	}

	@Override
	protected boolean isInnerValue(Object object) {
		return object == null || innerValueTypeToken.isInstance(object);
	}

	@Override
	protected OV transformToOuterValue(IV innerValue) {
		if (innerValue == null)
			return null;

		OV outerValue = transformToOuterValue.apply(innerValue);
		Objects.requireNonNull(outerValue, "The transformation must not create null instances.");
		return outerValue;
	}

	@Override
	protected boolean isOuterValue(Object object) {
		return object == null || outerValueTypeToken.isInstance(object);
	}

	@Override
	protected IV transformToInnerValue(OV outerValue) {
		if (outerValue == null)
			return null;

		IV innerValue = transformToInnerValue.apply(outerValue);
		Objects.requireNonNull(innerValue, "The transformation must not create null instances.");
		return innerValue;
	}

	// #end ABSTRACT METHODS FROM SUPERCLASS

}
