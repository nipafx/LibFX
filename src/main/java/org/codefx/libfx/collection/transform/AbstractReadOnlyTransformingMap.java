package org.codefx.libfx.collection.transform;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Abstract superclass to read-only {@link Map}s which transform another map.
 *
 * @param <IK>
 *            the inner key type, i.e. the type of the keys contained in the wrapped/inner map
 * @param <OK>
 *            the outer key type, i.e. the type of keys appearing to be in this map
 * @param <IV>
 *            the inner value type, i.e. the type of the values contained in the wrapped/inner map
 * @param <OV>
 *            the outer value type, i.e. the type of values appearing to be in this map
 * @see AbstractTransformingMap
 */
abstract class AbstractReadOnlyTransformingMap<IK, OK, IV, OV>
		extends AbstractTransformingMap<IK, OK, IV, OV> {

	// prevent modification

	@Override
	public final OV put(OK key, OV value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final OV putIfAbsent(OK key, OV value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final void putAll(Map<? extends OK, ? extends OV> outerMap) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final OV compute(OK key, BiFunction<? super OK, ? super OV, ? extends OV> remappingFunction) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final OV computeIfAbsent(OK key, Function<? super OK, ? extends OV> mappingFunction) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final OV computeIfPresent(OK key, BiFunction<? super OK, ? super OV, ? extends OV> remappingFunction) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final OV merge(OK key, OV value, BiFunction<? super OV, ? super OV, ? extends OV> remappingFunction) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final OV replace(OK key, OV value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final boolean replace(OK key, OV oldValue, OV newValue) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final void replaceAll(BiFunction<? super OK, ? super OV, ? extends OV> function) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final OV remove(Object key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final boolean remove(Object key, Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final void clear() {
		throw new UnsupportedOperationException();
	}

}
