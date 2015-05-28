package org.codefx.libfx.collection.transform;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * Builder for {@link TransformingMap}s.
 * <p>
 * A builder can be obtained by calling {@link #forTypes(Class, Class, Class, Class) forTypes} or
 * {@link #forTypesUnknown()}. The building method TODO can only be called after transformations from inner to outer
 * keys and values and vice versa have been set.
 *
 * @param <IK>
 *            the inner key type of the created transforming map, i.e. the type of the keys contained in the
 *            wrapped/inner map
 * @param <OK>
 *            the outer key type of the created transforming map, i.e. the type of keys appearing to be in this map
 * @param <IV>
 *            the inner value type of the created transforming map, i.e. the type of the values contained in the
 *            wrapped/inner map
 * @param <OV>
 *            the outer value type of the created transforming map, i.e. the type of values appearing to be in this map
 */
public class TransformingMapBuilder<IK, OK, IV, OV> {

	// #begin FIELDS

	private final Class<? super OK> outerKeyTypeToken;
	private final Class<? super IK> innerKeyTypeToken;
	private Function<? super IK, ? extends OK> transformToOuterKey;
	private Function<? super OK, ? extends IK> transformToInnerKey;

	private final Class<? super OV> outerValueTypeToken;
	private final Class<? super IV> innerValueTypeToken;
	private Function<? super IV, ? extends OV> transformToOuterValue;
	private Function<? super OV, ? extends IV> transformToInnerValue;

	// #end FIELDS

	// #begin CONSTRUCTION

	private TransformingMapBuilder(
			Class<? super IK> innerKeyTypeToken, Class<? super OK> outerKeyTypeToken,
			Class<? super IV> innerValueTypeToken, Class<? super OV> outerValueTypeToken) {

		Objects.requireNonNull(innerKeyTypeToken, "The argument 'innerKeyTypeToken' must not be null.");
		Objects.requireNonNull(outerKeyTypeToken, "The argument 'outerKeyTypeToken' must not be null.");
		Objects.requireNonNull(innerValueTypeToken, "The argument 'innerValueTypeToken' must not be null.");
		Objects.requireNonNull(outerValueTypeToken, "The argument 'outerValueTypeToken' must not be null.");

		this.innerKeyTypeToken = innerKeyTypeToken;
		this.outerKeyTypeToken = outerKeyTypeToken;
		this.innerValueTypeToken = innerValueTypeToken;
		this.outerValueTypeToken = outerValueTypeToken;
	}

	/**
	 * Creates a new builder for the specified inner and outer key and value types.
	 *
	 * @param <IK>
	 *            the inner key type of the created transforming map, i.e. the type of the keys contained in the
	 *            wrapped/inner map
	 * @param <OK>
	 *            the outer key type of the created transforming map, i.e. the type of keys appearing to be in this map
	 * @param <IV>
	 *            the inner value type of the created transforming map, i.e. the type of the values contained in the
	 *            wrapped/inner map
	 * @param <OV>
	 *            the outer value type of the created transforming map, i.e. the type of values appearing to be in this
	 *            map
	 * @param innerKeyTypeToken
	 *            the token for the inner key type
	 * @param outerKeyTypeToken
	 *            the token for the outer key type
	 * @param innerValueTypeToken
	 *            the token for the inner value type
	 * @param outerValueTypeToken
	 *            the token for the outer value type
	 * @return a new builder
	 */
	public static <IK, OK, IV, OV> TransformingMapBuilder<IK, OK, IV, OV> forTypes(
			Class<? super IK> innerKeyTypeToken, Class<? super OK> outerKeyTypeToken,
			Class<? super IV> innerValueTypeToken, Class<? super OV> outerValueTypeToken) {

		return new TransformingMapBuilder<>(
				innerKeyTypeToken, outerKeyTypeToken, innerValueTypeToken, outerValueTypeToken);
	}

	/**
	 * Creates a new builder for unknown inner and outer key and value types.
	 * <p>
	 * This is equivalent to calling {@link #forTypes(Class, Class, Class, Class) forTypes(Object.class, Object.class,
	 * Object.class, Object.class)}. To obtain a builder for {@code <IK, OK, IV, OV>} you will have to call
	 * {@code TransformingMapBuilder.<IK, OK, IV, OV> forTypesUnknown()}.
	 *
	 * @param <IK>
	 *            the inner key type of the created transforming map, i.e. the type of the keys contained in the
	 *            wrapped/inner map
	 * @param <OK>
	 *            the outer key type of the created transforming map, i.e. the type of keys appearing to be in this map
	 * @param <IV>
	 *            the inner value type of the created transforming map, i.e. the type of the values contained in the
	 *            wrapped/inner map
	 * @param <OV>
	 *            the outer value type of the created transforming map, i.e. the type of values appearing to be in this
	 *            map
	 * @return a new builder
	 */
	public static <IK, OK, IV, OV> TransformingMapBuilder<IK, OK, IV, OV> forTypesUnknown() {
		return forTypes(Object.class, Object.class, Object.class, Object.class);
	}

	// #end CONSTRUCTION

	// #begin SET FIELDS

	/**
	 * Sets the transformation from inner to outer keys which will be used by the created map.
	 *
	 * @param transformToOuterKey
	 *            transforms inner to outer keys
	 * @return this builder
	 */
	public TransformingMapBuilder<IK, OK, IV, OV> toOuterKey(Function<? super IK, ? extends OK> transformToOuterKey) {
		Objects.requireNonNull(transformToOuterKey, "The argument 'transformToOuterKey' must not be null.");

		this.transformToOuterKey = transformToOuterKey;
		return this;
	}

	/**
	 * Sets the transformation from outer to inner keys which will be used by the created map.
	 *
	 * @param transformToInnerKey
	 *            transforms outer to inner keys
	 * @return this builder
	 */
	public TransformingMapBuilder<IK, OK, IV, OV> toInnerKey(Function<? super OK, ? extends IK> transformToInnerKey) {
		Objects.requireNonNull(transformToInnerKey, "The argument 'transformToInnerKey' must not be null.");

		this.transformToInnerKey = transformToInnerKey;
		return this;
	}

	/**
	 * Sets the transformation from inner to outer values which will be used by the created map.
	 *
	 * @param transformToOuterValue
	 *            transforms inner to outer values
	 * @return this builder
	 */
	public TransformingMapBuilder<IK, OK, IV, OV> toOuterValue(Function<? super IV, ? extends OV> transformToOuterValue) {
		Objects.requireNonNull(transformToOuterValue, "The argument 'transformToOuterValue' must not be null.");

		this.transformToOuterValue = transformToOuterValue;
		return this;
	}

	/**
	 * Sets the transformation from outer to inner values which will be used by the created map.
	 *
	 * @param transformToInnerValue
	 *            transforms outer to inner values
	 * @return this builder
	 */
	public TransformingMapBuilder<IK, OK, IV, OV> toInnerValue(Function<? super OV, ? extends IV> transformToInnerValue) {
		Objects.requireNonNull(transformToInnerValue, "The argument 'transformToInnerValue' must not be null.");

		this.transformToInnerValue = transformToInnerValue;
		return this;
	}

	// #end SET FIELDS

	// #begin BUILD

	/**
	 * Creates a {@link TransformingMap} which transforms/decorates the specified map.
	 *
	 * @param map
	 *            the map to transform; will be the inner map of the returned transformation
	 * @return a new {@link TransformingMap}
	 */
	public TransformingMap<IK, OK, IV, OV> transformMap(Map<IK, IV> map) {
		return new TransformingMap<>(map,
				innerKeyTypeToken, outerKeyTypeToken, transformToOuterKey, transformToInnerKey,
				innerValueTypeToken, outerValueTypeToken, transformToOuterValue, transformToInnerValue);
	}

	// #end BUILD

}
