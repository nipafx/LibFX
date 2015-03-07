package org.codefx.libfx.collection.transform;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public final class TransformingMap<IK, OK, IV, OV> extends AbstractTransformingMap<IK, OK, IV, OV> {

	// #region FIELDS

	// TODO make final and instantiate

	private Set<OK> outerKeys;

	private Collection<OV> outerValues;

	private Set<Entry<OK, OV>> outerEntries;

	// #end FIELDS

	// #region ABSTRACT METHODS FROM SUPERCLASS

	@Override
	protected Map<IK, IV> getInnerMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isInnerKey(Object object) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected OK transformToOuterKey(IK innerKey) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isOuterKey(Object object) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected IK transformToInnerKey(OK outerKey) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isInnerValue(Object object) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected OV transformToOuterValue(IV innerValue) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean isOuterValue(Object object) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected IV transformToInnerValue(OV outerValue) {
		// TODO Auto-generated method stub
		return null;
	}

	// #end ABSTRACT METHODS FROM SUPERCLASS

	// #region IMPLEMENTATION OF 'Map'

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

	// #end IMPLEMENTATION OF 'Map'
}
