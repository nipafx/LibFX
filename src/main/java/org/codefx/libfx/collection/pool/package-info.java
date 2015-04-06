/**
 * TODO
 * <ul>
 * <li>allow to change ResourcePoolStrategy in a StrategyBasedResourcePool
 * <li>a subclass NonBlockingStrategyBasedResourcePool may implement NonBlockingResourcePool and only accept
 * NonBlockingResourcePoolStrategy; this would be an abstract implementation of a strategy which throws runtime
 * exceptions when the blocking instructions are returned
 * <li>split the strategy into strategy and statistics (most strategies will implement both but stats can be used
 * independently)
 * </ul>
 */
package org.codefx.libfx.collection.pool;

