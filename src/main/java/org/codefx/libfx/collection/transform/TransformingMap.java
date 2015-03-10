package org.codefx.libfx.collection.transform;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

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
