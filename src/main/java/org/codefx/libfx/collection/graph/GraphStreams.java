package org.codefx.libfx.collection.graph;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class GraphStreams {

	// TODO how to include spliterator characteristics dependent on the graph navigator?

	public static <N> Stream<N> dfs(N root, GraphNavigator<N> navigator) {
		Iterator<N> graphIterator = new GraphDfsIterator<>(root, navigator);
		return StreamSupport.stream(wrapInSpliterator(graphIterator), false);
	}

	public static <N> Stream<N> dfs(N root, GraphNavigator<N> navigator, N start) {
		Iterator<N> graphIterator = new GraphDfsIterator<>(root, navigator, start);
		return StreamSupport.stream(wrapInSpliterator(graphIterator), false);
	}

	private static <N> Spliterator<N> wrapInSpliterator(Iterator<N> graphIterator) {
		Spliterator<N> graphSpliterator = Spliterators.spliteratorUnknownSize(
				graphIterator,
				Spliterator.NONNULL | Spliterator.ORDERED);
		return graphSpliterator;
	}

}
