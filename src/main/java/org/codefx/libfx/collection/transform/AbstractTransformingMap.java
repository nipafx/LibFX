package org.codefx.libfx.collection.transform;

import java.util.Collection;
import java.util.Map;

public abstract class AbstractTransformingMap<IK, OK, IV, OV> implements Map<OK, OV> {

	// #region IMPLEMENTATION OF 'Collection<O>'

	/**
	 * Indicates whether the specified collection is equivalent to this one. This is the case if it is also an
	 * {@link AbstractTransformingMap} and wraps the same {@link #getInnerMap() innerMap}.
	 *
	 * @param otherMap
	 *            the {@link Collection} which is compared with this one
	 * @return true if this and the specified collection are equivalent
	 */
	protected final boolean isThisMap(Map<?, ?> otherMap) {
		if (otherMap == this)
			return true;

		if (otherMap instanceof AbstractTransformingMap) {
			AbstractTransformingMap<?, ?, ?, ?> otherTransformingMap =
					(AbstractTransformingMap<?, ?, ?, ?>) otherMap;
			boolean sameInnerMap = otherTransformingMap.getInnerMap() == getInnerMap();
			return sameInnerMap;
		}

		return false;
	}

	// size

	@Override
	public int size() {
		return getInnerMap().size();
	}

	@Override
	public boolean isEmpty() {
		return getInnerMap().isEmpty();
	}

	// contains

	@Override
	public boolean containsKey(Object key) {
		if (isOuterKey(key)) {
			@SuppressWarnings("unchecked")
			/*
			 * This cast can not fail due to erasure but the following call to 'transformToInnerKey' might. In that case
			 * a 'ClassCastException' will be thrown which is in accordance with the contract of 'containsKey'. If
			 * 'isOuterKey' does its job well (which can be hard due to erasure) this will not happen.
			 */
			OK outerKey = (OK) key;
			IK innerKey = transformToInnerKey(outerKey);
			return getInnerMap().containsKey(innerKey);
		} else
			return false;
	}

	@Override
	public boolean containsValue(Object value) {
		if (isOuterValue(value)) {
			@SuppressWarnings("unchecked")
			/*
			 * This cast can not fail due to erasure but the following call to 'transformToInnerValue' might. In that
			 * case a 'ClassCastException' will be thrown which is in accordance with the contract of 'containsValue'.
			 * If 'isOuterValue' does its job well (which can be hard due to erasure) this will not happen.
			 */
			OV outerValue = (OV) value;
			IV innerValue = transformToInnerValue(outerValue);
			return getInnerMap().containsValue(innerValue);
		} else
			return false;
	}

	// get

	@Override
	public OV get(Object key) {
		if (isOuterKey(key)) {
			@SuppressWarnings("unchecked")
			/*
			 * This cast can not fail due to erasure but the following call to 'transformToInnerKey' might. In that case
			 * a 'ClassCastException' will be thrown which is in accordance with the contract of 'get'. If 'isOuterKey'
			 * does its job well (which can be hard due to erasure) this will not happen.
			 */
			OK outerKey = (OK) key;
			IK innerKey = transformToInnerKey(outerKey);

			IV innerValue = getInnerMap().get(innerKey);

			OV outerValue = transformToOuterValue(innerValue);
			return outerValue;
		} else
			return null;
	}

	// put

	@Override
	public OV put(OK key, OV value) {
		IK innerKey = transformToInnerKey(key);
		IV innerValue = transformToInnerValue(value);

		IV previousInnerValue = getInnerMap().put(innerKey, innerValue);

		OV previousOuterValue = transformToOuterValue(previousInnerValue);
		return previousOuterValue;
	}

	@Override
	public void putAll(Map<? extends OK, ? extends OV> map) {
		// TODO Auto-generated method stub
	}

	// remove

	@Override
	public OV remove(Object key) {
		if (isOuterKey(key)) {
			@SuppressWarnings("unchecked")
			/*
			 * This cast can not fail due to erasure but the following call to 'transformToInnerKey' might. In that case
			 * a 'ClassCastException' will be thrown which is in accordance with the contract of 'remove'. If
			 * 'isOuterKey' does its job well (which can be hard due to erasure) this will not happen.
			 */
			OK outerKey = (OK) key;
			IK innerKey = transformToInnerKey(outerKey);

			IV previousInnerValue = getInnerMap().remove(innerKey);

			OV previousOuterValue = transformToOuterValue(previousInnerValue);
			return previousOuterValue;
		} else
			return null;
	}

	@Override
	public void clear() {
		getInnerMap().clear();
	}

	// #region ABSTRACT METHODS

	protected abstract Map<IK, IV> getInnerMap();

	protected abstract boolean isInnerKey(Object object);

	protected abstract OK transformToOuterKey(IK innerKey);

	protected abstract boolean isOuterKey(Object object);

	protected abstract IK transformToInnerKey(OK outerKey);

	protected abstract boolean isInnerValue(Object object);

	protected abstract OV transformToOuterValue(IV innerValue);

	protected abstract boolean isOuterValue(Object object);

	protected abstract IV transformToInnerValue(OV outerValue);

	// #end ABSTRACT METHODS

}
