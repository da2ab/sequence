/*
 * Copyright 2016 Daniel Skogquist Åborg
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.d2ab.iterable;

import org.d2ab.iterator.ChainingIterator;
import org.d2ab.util.Arrayz;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.Stream;

public class ChainingIterable<T> implements Iterable<T> {
	private final Collection<Iterable<? extends T>> iterables = new ArrayList<>();

	public ChainingIterable() {
	}

	public ChainingIterable(Iterable<? extends T> iterable) {
		iterables.add(iterable);
	}

	@SafeVarargs
	public ChainingIterable(Iterable<? extends T>... iterables) {
		Arrayz.forEach(iterables, this.iterables::add);
	}

	public static <U> Iterable<U> flatten(Iterable<?> containers) {
		return new ChainingIterable<U>().flatAppend(containers);
	}

	public static <T, U> Iterable<U> flatten(Iterable<? extends T> iterable,
	                                         Function<? super T, ? extends Iterable<U>> mapper) {
		ChainingIterable<U> result = new ChainingIterable<>();
		iterable.forEach(each -> result.append(mapper.apply(each)));
		return result;
	}

	public Iterable<T> flatAppend(Iterable<?> containers) {
		for (Object each : containers)
			flatAppend(each);
		return this;
	}

	public Iterable<T> flatAppend(Object container) {
		return append(Iterables.from(container));
	}

	@SuppressWarnings("unchecked")
	public Iterable<T> append(T... objects) {
		return append(Iterables.of(objects));
	}

	public Iterable<T> append(Iterable<T> iterable) {
		iterables.add(iterable);
		return this;
	}

	public Iterable<T> append(Iterator<T> iterator) {
		return append(Iterables.once(iterator));
	}

	public Iterable<T> append(Stream<T> stream) {
		return append(stream.iterator());
	}

	@Override
	public Iterator<T> iterator() {
		return new ChainingIterator<>(iterables);
	}

	@Override
	public int hashCode() {
		return iterables.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if ((o == null) || (getClass() != o.getClass()))
			return false;

		ChainingIterable<?> that = (ChainingIterable<?>) o;

		return iterables.equals(that.iterables);
	}

	@Override
	public String toString() {
		return "ChainingIterable" + iterables;
	}
}
