package org.codefx.libfx.collection.transform;

import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Abstract superclass to {@link Map}s which transform another map.
 * <p>
 * This class allows null keys and values. Subclasses might override that by implementing aggressive null checks.
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
abstract class AbstractTransformingMap<IK, OK, IV, OV> implements Map<OK, OV> {

	// #begin FIELDS

	private final Set<OK> outerKeys;

	private final Collection<OV> outerValues;

	private final Set<Entry<OK, OV>> outerEntries;

	// #end FIELDS

	// #begin CONSTRUCTION

	/**
	 * Creates a new abstract transforming map.
	 */
	protected AbstractTransformingMap() {
		outerKeys = new KeySetView();
		outerValues = new ValueCollectionView();
		outerEntries = new EntrySetView();
	}

	// #end CONSTRUCTION

	// #begin IMPLEMENTATION OF 'Map<OK, OV>'

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

	@Override
	public OV getOrDefault(Object key, OV defaultValue) {
		if (isOuterKey(key)) {
			@SuppressWarnings("unchecked")
			/*
			 * This cast can not fail due to erasure but the following call to 'transformToInnerKey' might. In that case
			 * a 'ClassCastException' will be thrown which is in accordance with the contract of 'get'. If 'isOuterKey'
			 * does its job well (which can be hard due to erasure) this will not happen.
			 */
			OK outerKey = (OK) key;
			return transformToOuterValue(getInnerMap().getOrDefault(
					transformToInnerKey(outerKey),
					transformToInnerValue(defaultValue)
					));
		} else
			return defaultValue;
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
				transformToInnerKeyValueToValueFunction(remappingFunction)
				));
	}

	@Override
	public OV computeIfAbsent(OK key, Function<? super OK, ? extends OV> mappingFunction) {
		Objects.requireNonNull(mappingFunction, "The argument 'mappingFunction' must not be null.");
		return transformToOuterValue(getInnerMap().computeIfAbsent(
				transformToInnerKey(key),
				transformToInnerToKeyValueFunction(mappingFunction)
				));
	}

	@Override
	public OV computeIfPresent(OK key, BiFunction<? super OK, ? super OV, ? extends OV> remappingFunction) {
		Objects.requireNonNull(remappingFunction, "The argument 'remappingFunction' must not be null.");
		return transformToOuterValue(getInnerMap().computeIfPresent(
				transformToInnerKey(key),
				transformToInnerKeyValueToValueFunction(remappingFunction)
				));
	}

	@Override
	public OV merge(OK key, OV value, BiFunction<? super OV, ? super OV, ? extends OV> remappingFunction) {
		Objects.requireNonNull(remappingFunction, "The argument 'remappingFunction' must not be null.");
		return transformToOuterValue(getInnerMap().merge(
				transformToInnerKey(key),
				transformToInnerValue(value),
				transformToInnerValueValueToValueFunction(remappingFunction)
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
		getInnerMap().replaceAll(transformToInnerKeyValueToValueFunction(function));
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

	// process

	@Override
	public void forEach(BiConsumer<? super OK, ? super OV> action) {
		Objects.requireNonNull(action, "The argument 'action' must not be null.");
		getInnerMap().forEach(transformToInnerKeyValueConsumer(action));
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

	private Function<? super IK, ? extends IV> transformToInnerToKeyValueFunction(
			Function<? super OK, ? extends OV> function) {

		return innerKey -> transformToInnerValue(function.apply(transformToOuterKey(innerKey)));
	}

	private BiFunction<? super IK, ? super IV, ? extends IV> transformToInnerKeyValueToValueFunction(
			BiFunction<? super OK, ? super OV, ? extends OV> function) {

		return (innerKey, innerValue) -> transformToInnerValue(function.apply(
				transformToOuterKey(innerKey),
				transformToOuterValue(innerValue)));
	}

	private BiFunction<? super IV, ? super IV, ? extends IV> transformToInnerValueValueToValueFunction(
			BiFunction<? super OV, ? super OV, ? extends OV> function) {

		return (innerValue1, innerValue2) -> transformToInnerValue(function.apply(
				transformToOuterValue(innerValue1),
				transformToOuterValue(innerValue2)));
	}

	private BiConsumer<? super IK, ? super IV> transformToInnerKeyValueConsumer(
			BiConsumer<? super OK, ? super OV> consumer) {

		return (innerKey, innerValue) -> consumer.accept(
				transformToOuterKey(innerKey),
				transformToOuterValue(innerValue));
	}

	// #end IMPLEMENTATION OF 'Map<OK, OV>'

	// #begin OBJECT

	@Override
	public final boolean equals(Object object) {
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
	public final int hashCode() {
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

	// #begin ABSTRACT METHODS

	/**
	 * @return the inner map wrapped by this transforming map
	 */
	protected abstract Map<IK, IV> getInnerMap();

	/**
	 * Checks whether the specified object might be an inner key.
	 * <p>
	 * This method does not have to be exact (which might be impossible due to involved generic types) and might produce
	 * false positives (but no false negatives).
	 *
	 * @param object
	 *            the object to check; may be null
	 * @return true if the object might be an inner key
	 */
	protected abstract boolean isInnerKey(Object object);

	/**
	 * Transforms the specified key to an instance of the outer key type.
	 * <p>
	 * It can not be guaranteed that the specified key is really of the inner key type. If not, an exception can be
	 * thrown.
	 *
	 * @param innerKey
	 *            the key to transform; may be null
	 * @return the transformed key
	 * @throws ClassCastException
	 *             if the specified key is not of the correct type
	 */
	protected abstract OK transformToOuterKey(IK innerKey) throws ClassCastException;

	/**
	 * Checks whether the specified object might be an outer key.
	 * <p>
	 * This method does not have to be exact (which might be impossible due to involved generic types) and might produce
	 * false positives (but no false negatives).
	 *
	 * @param object
	 *            the object to check; may be null
	 * @return true if the object might be an outer key
	 */
	protected abstract boolean isOuterKey(Object object);

	/**
	 * Transforms the specified key to an instance of the inner key type.
	 * <p>
	 * It can not be guaranteed that the specified key is really of the outer key type. If not, an exception can be
	 * thrown.
	 *
	 * @param outerKey
	 *            the key to transform; may be null
	 * @return the transformed key
	 * @throws ClassCastException
	 *             if the specified key is not of the correct type
	 */
	protected abstract IK transformToInnerKey(OK outerKey) throws ClassCastException;

	/**
	 * Checks whether the specified object might be an inner value.
	 * <p>
	 * This method does not have to be exact (which might be impossible due to involved generic types) and might produce
	 * false positives (but no false negatives).
	 *
	 * @param object
	 *            the object to check; may be null
	 * @return true if the object might be an inner value
	 */
	protected abstract boolean isInnerValue(Object object);

	/**
	 * Transforms the specified value to an instance of the outer value type.
	 * <p>
	 * It can not be guaranteed that the specified value is really of the inner value type. If not, an exception can be
	 * thrown.
	 *
	 * @param innerValue
	 *            the value to transform; may be null
	 * @return the transformed value
	 * @throws ClassCastException
	 *             if the specified value is not of the correct type
	 */
	protected abstract OV transformToOuterValue(IV innerValue) throws ClassCastException;

	/**
	 * Checks whether the specified object might be an outer value.
	 * <p>
	 * This method does not have to be exact (which might be impossible due to involved generic types) and might produce
	 * false positives (but no false negatives).
	 *
	 * @param object
	 *            the object to check; may be null
	 * @return true if the object might be an outer value
	 */
	protected abstract boolean isOuterValue(Object object);

	/**
	 * Transforms the specified value to an instance of the inner value type.
	 * <p>
	 * It can not be guaranteed that the specified value is really of the outer value type. If not, an exception can be
	 * thrown.
	 *
	 * @param outerValue
	 *            the value to transform; may be null
	 * @return the transformed value
	 * @throws ClassCastException
	 *             if the specified value is not of the correct type
	 */
	protected abstract IV transformToInnerValue(OV outerValue) throws ClassCastException;

	// #end ABSTRACT METHODS

	// #begin INNER CLASSES

	/**
	 * The view on this map's key set.
	 * <p>
	 * This view is a {@link TransformingSet} on the inner map's key set.
	 */
	private class KeySetView extends AbstractTransformingSet<IK, OK> {

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

		// prevent adding elements according to the contract of 'Map.keySet()'

		@Override
		public boolean add(OK element) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(Collection<? extends OK> otherCollection) {
			throw new UnsupportedOperationException();
		}

	}

	/**
	 * The view on this map's values.
	 * <p>
	 * This view is a {@link TransformingCollection} on the inner map's values.
	 */
	private class ValueCollectionView extends AbstractTransformingCollection<IV, OV> {

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

		// prevent adding elements according to the contract of 'Map.values()'

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

	/**
	 * The view on this map's entry set.
	 * <p>
	 * This view is a {@link TransformingSet} on the inner map's entry set.
	 */
	private class EntrySetView extends AbstractTransformingSet<Entry<IK, IV>, Entry<OK, OV>> {

		@Override
		protected Set<Entry<IK, IV>> getInnerSet() {
			return getInnerMap().entrySet();
		}

		@Override
		protected boolean isInnerElement(Object object) {
			if (!(object instanceof Entry<?, ?>))
				// this also returns 'false' if object is 'null';
				// that is correct because an entrySet can not contain null values
				return false;

			Entry<?, ?> entry = (Entry<?, ?>) object;
			return isInnerKey(entry.getKey()) && isInnerValue(entry.getValue());
		}

		@Override
		protected Entry<OK, OV> transformToOuter(Entry<IK, IV> innerElement) {
			// the entry view is based on an inner view, which should never contain null
			Objects.requireNonNull(innerElement, "The argument 'innerElement' must not be null.");

			OK outerKey = transformToOuterKey(innerElement.getKey());
			OV outerValue = transformToOuterValue(innerElement.getValue());
			return new SimpleEntry<>(outerKey, outerValue);
		}

		@Override
		protected boolean isOuterElement(Object object) {
			if (!(object instanceof Entry<?, ?>))
				// this also returns 'false' if object is 'null';
				// that is correct because an entrySet can not contain null values
				return false;

			Entry<?, ?> entry = (Entry<?, ?>) object;
			return isOuterKey(entry.getKey()) && isOuterValue(entry.getValue());
		}

		@Override
		protected Entry<IK, IV> transformToInner(Map.Entry<OK, OV> outerElement) {
			// someone might hand null to a method of this view (e.g. 'contains');
			// since there can never be null values in an entry view of a map, this mapping can be fixed to null -> null;
			// the inner map's entry view will handle this case correctly
			if (outerElement == null)
				return null;

			IK innerKey = transformToInnerKey(outerElement.getKey());
			IV innerValue = transformToInnerValue(outerElement.getValue());
			return new SimpleEntry<>(innerKey, innerValue);
		}

		// prevent adding elements according to the contract of 'Map.entrySet()'

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
