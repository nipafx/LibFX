package org.codefx.libfx.collection.transform;

import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

abstract class AbstractTransformingMap<IK, OK, IV, OV> implements Map<OK, OV> {

	// #region FIELDS

	private final Set<OK> outerKeys;

	private final Collection<OV> outerValues;

	private final Set<Entry<OK, OV>> outerEntries;

	// #end FIELDS

	// #region CONSTRUCTION

	public AbstractTransformingMap() {
		outerKeys = new KeySetView();
		outerValues = new ValueCollectionView();
		outerEntries = new EntrySetView();
	}

	// #end CONSTRUCTION

	// #region IMPLEMENTATION OF 'Map<OK, OV>'

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
			return getInnerMap().containsKey(
					transformToInnerKey(outerKey));
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
			return getInnerMap().containsValue(
					transformToInnerValue(outerValue));
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
			return transformToOuterValue(getInnerMap().get(
					transformToInnerKey(outerKey)));
		} else
			return null;
	}

	// put / compute / merge / replace

	@Override
	public OV put(OK key, OV value) {
		return transformToOuterValue(getInnerMap().put(
				transformToInnerKey(key),
				transformToInnerValue(value)));
	}

	@Override
	public OV putIfAbsent(OK key, OV value) {
		return transformToOuterValue(getInnerMap().putIfAbsent(
				transformToInnerKey(key),
				transformToInnerValue(value)));
	}

	@Override
	public void putAll(Map<? extends OK, ? extends OV> outerMap) {
		Map<IK, IV> asInner = new TransformToReadOnlyInnerMap(outerMap);
		getInnerMap().putAll(asInner);
	}

	@Override
	public OV compute(OK key, BiFunction<? super OK, ? super OV, ? extends OV> remappingFunction) {
		Objects.requireNonNull(remappingFunction, "The argument 'remappingFunction' must not be null.");
		return transformToOuterValue(getInnerMap().compute(
				transformToInnerKey(key),
				transformToInnerEntryValueBiFunction(remappingFunction)
				));
	}

	@Override
	public OV computeIfAbsent(OK key, Function<? super OK, ? extends OV> mappingFunction) {
		Objects.requireNonNull(mappingFunction, "The argument 'mappingFunction' must not be null.");
		return transformToOuterValue(getInnerMap().computeIfAbsent(
				transformToInnerKey(key),
				transformToInnerKeyValueFunction(mappingFunction)
				));
	}

	@Override
	public OV computeIfPresent(OK key, BiFunction<? super OK, ? super OV, ? extends OV> remappingFunction) {
		Objects.requireNonNull(remappingFunction, "The argument 'remappingFunction' must not be null.");
		return transformToOuterValue(getInnerMap().computeIfPresent(
				transformToInnerKey(key),
				transformToInnerEntryValueBiFunction(remappingFunction)
				));
	}

	@Override
	public OV merge(OK key, OV value, BiFunction<? super OV, ? super OV, ? extends OV> remappingFunction) {
		Objects.requireNonNull(remappingFunction, "The argument 'remappingFunction' must not be null.");
		return transformToOuterValue(getInnerMap().merge(
				transformToInnerKey(key),
				transformToInnerValue(value),
				transformToInnerValueBiFunction(remappingFunction)
				));
	}

	@Override
	public OV replace(OK key, OV value) {
		return transformToOuterValue(getInnerMap().replace(
				transformToInnerKey(key),
				transformToInnerValue(value)));
	}

	@Override
	public boolean replace(OK key, OV oldValue, OV newValue) {
		return getInnerMap().replace(
				transformToInnerKey(key),
				transformToInnerValue(oldValue),
				transformToInnerValue(newValue)
				);
	}

	@Override
	public void replaceAll(BiFunction<? super OK, ? super OV, ? extends OV> function) {
		getInnerMap().replaceAll(transformToInnerEntryValueBiFunction(function));
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
			return transformToOuterValue(getInnerMap().remove(
					transformToInnerKey(outerKey)));
		} else
			return null;
	}

	@Override
	public boolean remove(Object key, Object value) {
		if (isOuterKey(key) && isOuterValue(value)) {
			/*
			 * These casts can not fail due to erasure but the following calls to 'transformToInner...' might. In that
			 * case a 'ClassCastException' will be thrown which is in accordance with the contract of 'remove'. If
			 * 'isOuter...' does its job well (which can be hard due to erasure) this will not happen.
			 */
			@SuppressWarnings("unchecked")
			OK outerKey = (OK) key;
			@SuppressWarnings("unchecked")
			OV outerValue = (OV) value;
			return getInnerMap().remove(
					transformToInnerKey(outerKey),
					transformToInnerValue(outerValue)
					);
		} else
			return false;
	}

	@Override
	public void clear() {
		getInnerMap().clear();
	}

	// views

	@Override
	public Set<OK> keySet() {
		return outerKeys;
	}

	@Override
	public Collection<OV> values() {
		return outerValues;
	}

	@Override
	public Set<Entry<OK, OV>> entrySet() {
		return outerEntries;
	}

	// function transformation

	private Function<? super IK, ? extends IV> transformToInnerKeyValueFunction(
			Function<? super OK, ? extends OV> function) {

		return innerKey -> transformToInnerValue(function.apply(transformToOuterKey(innerKey)));
	}

	private BiFunction<? super IK, ? super IV, ? extends IV> transformToInnerEntryValueBiFunction(
			BiFunction<? super OK, ? super OV, ? extends OV> function) {

		return (innerKey, innerValue) -> transformToInnerValue(function.apply(
				transformToOuterKey(innerKey),
				transformToOuterValue(innerValue)));
	}

	private BiFunction<? super IV, ? super IV, ? extends IV> transformToInnerValueBiFunction(
			BiFunction<? super OV, ? super OV, ? extends OV> function) {

		return (innerValue1, innerValue2) -> transformToInnerValue(function.apply(
				transformToOuterValue(innerValue1),
				transformToOuterValue(innerValue2)));
	}

	// #end IMPLEMENTATION OF 'Map<OK, OV>'

	// #region OBJECT

	@Override
	public boolean equals(Object object) {
		if (object == this)
			return true;
		if (!(object instanceof Map))
			return false;

		Map<?, ?> other = (Map<?, ?>) object;
		if (isThisMap(other))
			return true;

		return outerEntries.equals(other.entrySet());
	}

	@Override
	public int hashCode() {
		return outerEntries.hashCode();
	}

	@Override
	public String toString() {
		return outerEntries
				.stream()
				.map(Objects::toString)
				.collect(Collectors.joining(", ", "{", "}"));
	}

	// #end OBJECT

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

	// #region INNER CLASSES

	protected class KeySetView extends AbstractTransformingSet<IK, OK> {

		@Override
		protected Set<IK> getInnerSet() {
			return getInnerMap().keySet();
		}

		@Override
		protected boolean isInnerElement(Object object) {
			return isInnerKey(object);
		}

		@Override
		protected OK transformToOuter(IK innerElement) {
			return transformToOuterKey(innerElement);
		}

		@Override
		protected boolean isOuterElement(Object object) {
			return isOuterKey(object);
		}

		@Override
		protected IK transformToInner(OK outerElement) {
			return transformToInnerKey(outerElement);
		}

		// prevent adding elements

		@Override
		public boolean add(OK element) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(Collection<? extends OK> otherCollection) {
			throw new UnsupportedOperationException();
		}

	}

	protected class ValueCollectionView extends AbstractTransformingCollection<IV, OV> {

		@Override
		protected Collection<IV> getInnerCollection() {
			return getInnerMap().values();
		}

		@Override
		protected boolean isInnerElement(Object object) {
			return isInnerValue(object);
		}

		@Override
		protected OV transformToOuter(IV innerElement) {
			return transformToOuterValue(innerElement);
		}

		@Override
		protected boolean isOuterElement(Object object) {
			return isOuterValue(object);
		}

		@Override
		protected IV transformToInner(OV outerElement) {
			return transformToInnerValue(outerElement);
		}

		// prevent adding elements

		@Override
		public boolean add(OV element) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(Collection<? extends OV> otherCollection) {
			throw new UnsupportedOperationException();
		}

		// object

		@Override
		public boolean equals(Object object) {
			if (object == this)
				return true;
			if (!(object instanceof Collection))
				return false;

			Collection<?> other = (Collection<?>) object;
			if (isThisCollection(other))
				return true;

			return other.containsAll(this) && this.containsAll(other);
		}

		@Override
		public int hashCode() {
			int hashCode = 1;
			for (OV outerElement : this)
				hashCode = 31 * hashCode + (outerElement == null ? 0 : outerElement.hashCode());
			return hashCode;
		}

	}

	protected class EntrySetView extends AbstractTransformingSet<Entry<IK, IV>, Entry<OK, OV>> {

		@Override
		protected Set<Entry<IK, IV>> getInnerSet() {
			return getInnerMap().entrySet();
		}

		@Override
		protected boolean isInnerElement(Object object) {
			if (!(object instanceof Entry<?, ?>))
				return false;

			Entry<?, ?> entry = (Entry<?, ?>) object;
			return isInnerKey(entry.getKey()) && isInnerValue(entry.getValue());
		}

		@Override
		protected Entry<OK, OV> transformToOuter(Entry<IK, IV> innerElement) {
			OK outerKey = transformToOuterKey(innerElement.getKey());
			OV outerValue = transformToOuterValue(innerElement.getValue());
			return new SimpleEntry<>(outerKey, outerValue);
		}

		@Override
		protected boolean isOuterElement(Object object) {
			if (!(object instanceof Entry<?, ?>))
				return false;

			Entry<?, ?> entry = (Entry<?, ?>) object;
			return isOuterKey(entry.getKey()) && isOuterValue(entry.getValue());
		}

		@Override
		protected Entry<IK, IV> transformToInner(Map.Entry<OK, OV> outerElement) {
			IK innerKey = transformToInnerKey(outerElement.getKey());
			IV innerValue = transformToInnerValue(outerElement.getValue());
			return new SimpleEntry<>(innerKey, innerValue);
		}

		// prevent adding elements

		@Override
		public boolean add(Entry<OK, OV> element) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(Collection<? extends Entry<OK, OV>> otherCollection) {
			throw new UnsupportedOperationException();
		}

	}

	private class TransformToReadOnlyInnerMap extends AbstractReadOnlyTransformingMap<OK, IK, OV, IV> {

		private final Map<? extends OK, ? extends OV> transformedMap;

		public TransformToReadOnlyInnerMap(Map<? extends OK, ? extends OV> transformedMap) {
			this.transformedMap = transformedMap;
		}

		// abstract methods

		@Override
		protected Map<OK, OV> getInnerMap() {
			@SuppressWarnings("unchecked")
			/*
			 * This cast is not safe! But since this class only allows reading operations, it can not cause trouble.
			 */
			Map<OK, OV> unsafelyTypedMap = (Map<OK, OV>) transformedMap;
			return unsafelyTypedMap;
		}

		@Override
		protected boolean isInnerKey(Object object) {
			return AbstractTransformingMap.this.isOuterKey(object);
		}

		@Override
		protected IK transformToOuterKey(OK innerKey) {
			return AbstractTransformingMap.this.transformToInnerKey(innerKey);
		}

		@Override
		protected boolean isOuterKey(Object object) {
			return AbstractTransformingMap.this.isInnerKey(object);
		}

		@Override
		protected OK transformToInnerKey(IK outerKey) {
			return AbstractTransformingMap.this.transformToOuterKey(outerKey);
		}

		@Override
		protected boolean isInnerValue(Object object) {
			return AbstractTransformingMap.this.isOuterValue(object);
		}

		@Override
		protected IV transformToOuterValue(OV innerValue) {
			return AbstractTransformingMap.this.transformToInnerValue(innerValue);
		}

		@Override
		protected boolean isOuterValue(Object object) {
			return AbstractTransformingMap.this.isInnerValue(object);
		}

		@Override
		protected OV transformToInnerValue(IV outerValue) {
			return AbstractTransformingMap.this.transformToOuterValue(outerValue);
		}

	}

	// #end INNER CLASSES

}
