package org.codefx.libfx.collection.transform;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class AbstractReadOnlyTransformingMap<IK, OK, IV, OV>
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
