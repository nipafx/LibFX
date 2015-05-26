package org.codefx.libfx.collection.transform;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * A {@link Map} which decorates another map and transforms the key and value types from the inner types {@code IK},
 * {@code IV} to outer types {@code OK}, {@code OV}.
 * <p>
 * See the {@link org.codefx.libfx.collection.transform package} documentation for general comments on transformation.
 * <p>
 * This implementation mitigates the type safety problems by using tokens of the inner and the outer types to check
 * instances against them. This solves some of the critical situations but not all of them. In those other cases
 * {@link ClassCastException}s might occur when an element can not be transformed by the transformation functions.
 * <p>
 * Null keys and values are allowed unless the inner map does not accept them. These are handled explicitly and fixed to
 * the transformation {@code null -> null}. The transforming functions specified during construction neither have to
 * handle that case nor are they allowed to produce null elements.
 * <p>
 * If the {@link java.util.stream.Stream streams} returned by this map's views are told to
 * {@link java.util.stream.Stream#sorted() sort} themself, they will do so on the base of the comparator returned by the
 * inner map view's spliterator (e.g. based on the natural order of {@code IK} or {@code IV} if it has one).
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

	// #begin FIELDS

	private final Map<IK, IV> innerMap;

	private final Class<? super OK> outerKeyTypeToken;

	private final Class<? super IK> innerKeyTypeToken;

	private final Function<IK, OK> transformToOuterKey;

	private final Function<OK, IK> transformToInnerKey;

	private final Class<? super OV> outerValueTypeToken;

	private final Class<? super IV> innerValueTypeToken;

	private final Function<IV, OV> transformToOuterValue;

	private final Function<OV, IV> transformToInnerValue;

	// #end FIELDS

	// #begin CONSTRUCTION

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

		Objects.requireNonNull(innerMap, "The argument 'innerMap' must not be null.");
		Objects.requireNonNull(innerKeyTypeToken, "The argument 'innerKeyTypeToken' must not be null.");
		Objects.requireNonNull(transformToOuterKey, "The argument 'transformToOuterKey' must not be null.");
		Objects.requireNonNull(outerKeyTypeToken, "The argument 'outerKeyTypeToken' must not be null.");
		Objects.requireNonNull(transformToInnerKey, "The argument 'transformToInnerKey' must not be null.");
		Objects.requireNonNull(innerValueTypeToken, "The argument 'innerValueTypeToken' must not be null.");
		Objects.requireNonNull(transformToOuterValue, "The argument 'transformToOuterValue' must not be null.");
		Objects.requireNonNull(outerValueTypeToken, "The argument 'outerValueTypeToken' must not be null.");
		Objects.requireNonNull(transformToInnerValue, "The argument 'transformToInnerValue' must not be null.");

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

	// #begin IMPLEMENTATION OF 'AbstractTransformingMap'

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

	// #end IMPLEMENTATION OF 'AbstractTransformingMap'

}
