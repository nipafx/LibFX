package org.codefx.libfx.listener;

// TODO what should the contract be for created handles? Always attached?
// Answer: Up to the returning method to specify!

public interface ListenerHandle {

	void attach();

	void detach();

}
