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

package org.d2ab.collection.chars;

import org.d2ab.collection.Arrayz;
import org.d2ab.collection.ints.ArrayIntList;
import org.d2ab.iterator.chars.CharIterator;
import org.junit.Test;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.d2ab.test.IsCharIterableContainingInOrder.containsChars;
import static org.d2ab.test.IsIntIterableContainingInOrder.containsInts;
import static org.d2ab.test.Tests.expecting;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.emptyIterable;
import static org.junit.Assert.assertThat;

public class BitCharSetTest {
	private final BitCharSet empty = new BitCharSet();
	private final BitCharSet set = new BitCharSet('a', 'b', 'c', 'd', 'e');

	@Test
	public void size() {
		assertThat(empty.size(), is(0));
		assertThat(set.size(), is(5));
	}

	@Test
	public void iterator() {
		assertThat(empty, is(emptyIterable()));
		assertThat(set, containsChars('a', 'b', 'c', 'd', 'e'));
	}

	@Test
	public void isEmpty() {
		assertThat(empty.isEmpty(), is(true));
		assertThat(set.isEmpty(), is(false));
	}

	@Test
	public void clear() {
		empty.clear();
		assertThat(empty.isEmpty(), is(true));

		set.clear();
		assertThat(set.isEmpty(), is(true));
	}

	@Test
	public void addChar() {
		empty.addChar('q');
		assertThat(empty, containsChars('q'));

		set.addChar('q');
		assertThat(set, containsChars('a', 'b', 'c', 'd', 'e', 'q'));
	}

	@Test
	public void containsChar() {
		assertThat(empty.containsChar('q'), is(false));

		assertThat(set.containsChar('q'), is(false));
		for (char x = 'a'; x <= 'e'; x++)
			assertThat(set.containsChar(x), is(true));
	}

	@Test
	public void removeChar() {
		assertThat(empty.removeChar('q'), is(false));

		assertThat(set.removeChar('q'), is(false));
		for (char x = 'a'; x <= 'e'; x++)
			assertThat(set.removeChar(x), is(true));
		assertThat(set.isEmpty(), is(true));
	}

	@Test
	public void testToString() {
		assertThat(empty.toString(), is("[]"));
		assertThat(set.toString(), is("[a, b, c, d, e]"));
	}

	@Test
	public void testEqualsHashCode() {
		BitCharSet charSet2 = new BitCharSet('a', 'b', 'c', 'd', 'e', 'q');
		assertThat(set, is(not(equalTo(charSet2))));
		assertThat(set.hashCode(), is(not(charSet2.hashCode())));

		charSet2.removeChar('q');

		assertThat(set, is(equalTo(charSet2)));
		assertThat(set.hashCode(), is(charSet2.hashCode()));
	}

	@Test
	public void addAllCharArray() {
		assertThat(empty.addAllChars('a', 'b', 'c'), is(true));
		assertThat(empty, containsChars('a', 'b', 'c'));

		assertThat(set.addAllChars('c', 'd', 'e', 'f', 'g'), is(true));
		assertThat(set, containsChars('a', 'b', 'c', 'd', 'e', 'f', 'g'));
	}

	@Test
	public void addAllCharCollection() {
		assertThat(empty.addAllChars(CharList.of('a', 'b', 'c')), is(true));
		assertThat(empty, containsChars('a', 'b', 'c'));

		assertThat(set.addAllChars(CharList.of('c', 'd', 'e', 'f', 'g')), is(true));
		assertThat(set, containsChars('a', 'b', 'c', 'd', 'e', 'f', 'g'));
	}

	@Test
	public void stream() {
		assertThat(empty.stream().collect(Collectors.toList()), is(emptyIterable()));
		assertThat(set.stream().collect(Collectors.toList()), contains('a', 'b', 'c', 'd', 'e'));
	}

	@Test
	public void parallelStream() {
		assertThat(empty.parallelStream().collect(Collectors.toList()), is(emptyIterable()));
		assertThat(set.parallelStream().collect(Collectors.toList()), contains('a', 'b', 'c', 'd', 'e'));
	}

	@Test
	public void intStream() {
		assertThat(empty.intStream().collect(ArrayIntList::new, ArrayIntList::addInt, ArrayIntList::addAllInts),
		           is(emptyIterable()));

		assertThat(set.intStream().collect(ArrayIntList::new, ArrayIntList::addInt, ArrayIntList::addAllInts),
		           containsInts('a', 'b', 'c', 'd', 'e'));
	}

	@Test
	public void parallelIntStream() {
		assertThat(empty.parallelIntStream().collect(ArrayIntList::new, ArrayIntList::addInt,
		                                             ArrayIntList::addAllInts),
		           is(emptyIterable()));

		assertThat(set.parallelIntStream().collect(ArrayIntList::new, ArrayIntList::addInt, ArrayIntList::addAllInts),
		           containsInts('a', 'b', 'c', 'd', 'e'));
	}

	@Test
	public void sequence() {
		assertThat(empty.sequence(), is(emptyIterable()));
		assertThat(set.sequence(), containsChars('a', 'b', 'c', 'd', 'e'));
	}

	@Test
	public void firstChar() {
		expecting(NoSuchElementException.class, empty::firstChar);
		assertThat(set.firstChar(), is('a'));
	}

	@Test
	public void lastChar() {
		expecting(NoSuchElementException.class, empty::lastChar);
		assertThat(set.lastChar(), is('e'));
	}

	@Test
	public void iteratorRemoveAll() {
		CharIterator iterator = set.iterator();
		char value = 'a';
		while (iterator.hasNext()) {
			assertThat(iterator.nextChar(), is(value));
			iterator.remove();
			value++;
		}
		assertThat(value, is('f'));
		assertThat(set, is(emptyIterable()));
	}

	@Test
	public void removeAllCharArray() {
		assertThat(empty.removeAllChars('a', 'b', 'c'), is(false));
		assertThat(empty, is(emptyIterable()));

		assertThat(set.removeAllChars('a', 'b', 'c'), is(true));
		assertThat(set, containsChars('d', 'e'));
	}

	@Test
	public void removeAllCharCollection() {
		assertThat(empty.removeAll(CharList.of('a', 'b', 'c')), is(false));
		assertThat(empty, is(emptyIterable()));

		assertThat(set.removeAll(CharList.of('a', 'b', 'c')), is(true));
		assertThat(set, containsChars('d', 'e'));
	}

	@Test
	public void retainAllCharArray() {
		assertThat(empty.retainAllChars('a', 'b', 'c'), is(false));
		assertThat(empty, is(emptyIterable()));

		assertThat(set.retainAllChars('a', 'b', 'c'), is(true));
		assertThat(set, containsChars('a', 'b', 'c'));
	}

	@Test
	public void retainAllCharCollection() {
		assertThat(empty.retainAll(CharList.of('a', 'b', 'c')), is(false));
		assertThat(empty, is(emptyIterable()));

		assertThat(set.retainAll(CharList.of('a', 'b', 'c')), is(true));
		assertThat(set, containsChars('a', 'b', 'c'));
	}

	@Test
	public void removeCharsIf() {
		assertThat(empty.removeCharsIf(x -> x > 'c'), is(false));
		assertThat(empty, is(emptyIterable()));

		assertThat(set.removeCharsIf(x -> x > 'c'), is(true));
		assertThat(set, containsChars('a', 'b', 'c'));
	}

	@Test
	public void containsAllCharArray() {
		assertThat(empty.containsAllChars('a', 'b', 'c'), is(false));
		assertThat(set.containsAllChars('a', 'b', 'c'), is(true));
		assertThat(set.containsAllChars('a', 'b', 'c', 'q'), is(false));
	}

	@Test
	public void containsAllCharCollection() {
		assertThat(empty.containsAll(CharList.of('a', 'b', 'c')), is(false));
		assertThat(set.containsAll(CharList.of('a', 'b', 'c')), is(true));
		assertThat(set.containsAll(CharList.of('a', 'b', 'c', 'q')), is(false));
	}

	@Test
	public void forEachChar() {
		empty.forEachChar(x -> {
			throw new IllegalStateException("should not get called");
		});

		AtomicInteger value = new AtomicInteger('a');
		set.forEachChar(x -> assertThat(x, is((char) value.getAndIncrement())));
		assertThat(value.get(), is((int) 'f'));
	}

	@Test
	public void addBoxed() {
		empty.add('q');
		assertThat(empty, containsChars('q'));

		set.add('q');
		assertThat(set, containsChars('a', 'b', 'c', 'd', 'e', 'q'));
	}

	@Test
	public void containsBoxed() {
		assertThat(empty.contains('q'), is(false));

		assertThat(set.contains('q'), is(false));
		assertThat(set.contains(new Object()), is(false));
		for (char x = 'a'; x <= 'e'; x++)
			assertThat(set.contains(x), is(true));
	}

	@Test
	public void removeBoxed() {
		assertThat(empty.remove('q'), is(false));

		assertThat(set.remove('q'), is(false));
		assertThat(set.remove(new Object()), is(false));
		for (char x = 'a'; x <= 'e'; x++)
			assertThat(set.remove(x), is(true));
		assertThat(set.isEmpty(), is(true));
	}

	@Test
	public void addAllBoxed() {
		assertThat(empty.addAll(Arrays.asList('a', 'b', 'c')), is(true));
		assertThat(empty, containsChars('a', 'b', 'c'));

		assertThat(set.addAll(Arrays.asList('c', 'd', 'e', 'f', 'g')), is(true));
		assertThat(set, containsChars('a', 'b', 'c', 'd', 'e', 'f', 'g'));
	}

	@Test
	public void firstBoxed() {
		expecting(NoSuchElementException.class, empty::first);
		assertThat(set.first(), is('a'));
	}

	@Test
	public void lastBoxed() {
		expecting(NoSuchElementException.class, empty::last);
		assertThat(set.last(), is('e'));
	}

	@Test
	public void removeAllBoxed() {
		assertThat(empty.removeAll(Arrays.asList('a', 'b', 'c')), is(false));
		assertThat(empty, is(emptyIterable()));

		assertThat(set.removeAll(Arrays.asList('a', 'b', 'c')), is(true));
		assertThat(set, containsChars('d', 'e'));
	}

	@Test
	public void retainAllBoxed() {
		assertThat(empty.retainAll(Arrays.asList('a', 'b', 'c')), is(false));
		assertThat(empty, is(emptyIterable()));

		assertThat(set.retainAll(Arrays.asList('a', 'b', 'c')), is(true));
		assertThat(set, containsChars('a', 'b', 'c'));
	}

	@Test
	public void removeIfBoxed() {
		assertThat(empty.removeIf(x -> x > 'c'), is(false));
		assertThat(empty, is(emptyIterable()));

		assertThat(set.removeIf(x -> x > 'c'), is(true));
		assertThat(set, containsChars('a', 'b', 'c'));
	}

	@Test
	public void containsCharCollection() {
		assertThat(empty.containsAll(Arrays.asList('a', 'b', 'c')), is(false));
		assertThat(set.containsAll(Arrays.asList('a', 'b', 'c')), is(true));
		assertThat(set.containsAll(Arrays.asList('a', 'b', 'c', 'q')), is(false));
	}

	@Test
	public void forEachBoxed() {
		empty.forEach(x -> {
			throw new IllegalStateException("should not get called");
		});

		AtomicInteger value = new AtomicInteger('a');
		set.forEach(x -> assertThat(x, is((char) value.getAndIncrement())));
		assertThat(value.get(), is((int) 'f'));
	}

	@Test
	public void boundaries() {
		BitCharSet charSet = new BitCharSet();
		assertThat(charSet.addChar(Character.MIN_VALUE), is(true));
		assertThat(charSet.addChar(Character.MAX_VALUE), is(true));

		assertThat(charSet, containsChars(Character.MIN_VALUE, Character.MAX_VALUE));

		assertThat(charSet.containsChar(Character.MIN_VALUE), is(true));
		assertThat(charSet.containsChar(Character.MAX_VALUE), is(true));

		assertThat(charSet.removeChar(Character.MIN_VALUE), is(true));
		assertThat(charSet.removeChar(Character.MAX_VALUE), is(true));

		assertThat(charSet, is(emptyIterable()));
	}

	@Test
	public void fuzz() {
		char[] randomValues = new char[10000];
		Random random = new Random();
		for (int i = 0; i < randomValues.length; i++) {
			char randomValue;
			do
				randomValue = (char) random.nextInt(Character.MAX_VALUE + 1);
			while (Arrayz.contains(randomValues, randomValue));
			randomValues[i] = randomValue;
		}

		// Adding
		for (char randomValue : randomValues)
			assertThat(empty.addChar(randomValue), is(true));
		assertThat(empty.size(), is(randomValues.length));

		for (char randomValue : randomValues)
			assertThat(empty.addChar(randomValue), is(false));
		assertThat(empty.size(), is(randomValues.length));

		// Containment checks
		assertThat(empty.containsAllChars(randomValues), is(true));

		for (char randomValue : randomValues)
			assertThat(empty.containsChar(randomValue), is(true));

		// toString
		Arrays.sort(randomValues);
		StringBuilder expectedToString = new StringBuilder("[");
		for (int i = 0; i < randomValues.length; i++)
			expectedToString.append(i > 0 ? ", " : "").append(randomValues[i]);
		expectedToString.append("]");
		assertThat(empty.toString(), is(expectedToString.toString()));

		// Removing
		for (char randomValue : randomValues)
			assertThat(empty.removeChar(randomValue), is(true));
		assertThat(empty.toString(), is("[]"));
		assertThat(empty.size(), is(0));

		for (char randomValue : randomValues)
			assertThat(empty.removeChar(randomValue), is(false));
	}
}
